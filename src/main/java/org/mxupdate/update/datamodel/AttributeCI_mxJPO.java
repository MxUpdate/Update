/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.update.datamodel;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.helper.TriggerList_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateUtils_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * The class is used to evaluate information from attributes within MX used to
 * export, delete and update an attribute. Following properties are handled:
 * <ul>
 * <li>package</li>
 * <li>{@link #kind}</li>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>flag &quot;{@link #multiValue multiple value}&quot; (if parameter
 *     {@link ValueKeys#DMAttrSupportsFlagMultiValue} is defined)</li>
 * <li>flag &quot;{@link #resetonclone}&quot; (if parameter
 *     {@link ValueKeys#DMAttrSupportsFlagResetOnClone} is defined)</li>
 * <li>flag &quot;{@link #resetOnRevision}&quot; (if parameter
 *     {@link ValueKeys#DMAttrSupportsFlagResetOnRevision} is defined)</li>
 * <li>{@link #maxLength max length} for {@link Kind#String string}
 *     attributes</li>
 * <li>{@link #multiline multi line} flag for {@link Kind#String string}
 *     attributes</li>
 * <li>{@link #rangeValue range value} flag for {@link Kind#Real real},
 *     {@link Kind#Integer integer} and {@link Kind#Date date} attributes (if
 *     {@link ValueKeys#DMAttrSupportsFlagRangeValue} is defined)</li>
 * <li>{@link #dimension} for {@link Kind#Real real} and
 *     {@link Kind#Integer integer} attributes (if
 *     {@link ValueKeys#DMAttrSupportsDimension} is defined)</li>
 * <li>all assigned {@link #rules}</li>
 * <li>{@link #defaultValue default value}</li>
 * <li>{@link #writeTriggers(Appendable) triggers}</li>
 * <li>{@link #rangesSorted ranges}</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class AttributeCI_mxJPO
    extends AbstractAdminObject_mxJPO<AttributeCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for attributes. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        AttributeCI_mxJPO.IGNORED_URLS.add("/rangeList");
        AttributeCI_mxJPO.IGNORED_URLS.add("/rangeProgram");
        AttributeCI_mxJPO.IGNORED_URLS.add("/triggerList");
    }

    /**
     * Mapping between the comparators defined within XML and the comparators
     * used within MX.
     */
    private static final Map<String,String> RANGE_COMP = new HashMap<>();
    static  {
        AttributeCI_mxJPO.RANGE_COMP.put("equal",             "=");
        AttributeCI_mxJPO.RANGE_COMP.put("greaterthan",       ">");
        AttributeCI_mxJPO.RANGE_COMP.put("greaterthanequal",  ">=");
        AttributeCI_mxJPO.RANGE_COMP.put("lessthan",          "<");
        AttributeCI_mxJPO.RANGE_COMP.put("lessthanequal",     "<=");
        AttributeCI_mxJPO.RANGE_COMP.put("notequal",          "!=");
        AttributeCI_mxJPO.RANGE_COMP.put("match",             "match");
        AttributeCI_mxJPO.RANGE_COMP.put("notmatch",          "!match");
        AttributeCI_mxJPO.RANGE_COMP.put("smatch",            "smatch");
        AttributeCI_mxJPO.RANGE_COMP.put("notsmatch",         "!smatch");
        AttributeCI_mxJPO.RANGE_COMP.put("programRange",      "program");
        AttributeCI_mxJPO.RANGE_COMP.put("between",           "between");
    }

    /** Kind of the attribute. */
    private Kind kind;

    /** Set holding all rules referencing this attribute. */
    private final  SortedSet<String> rules = new TreeSet<>();

    /**
     * If the range is a program the value references a program. Because only
     * one range program could be defined at maximum, specific parsing exists!
     * So the range program is defined as variable directly on the attribute
     * (and correct in {@link #prepare()}).
     */
    private String rangeProgramRef;
    /** If the range is a program the value are the input arguments. */
    private String rangeProgramInputArguments;
    /** Stores the ranges of the attribute (used while parsing the XML attribute). */
    private final Stack<Range> rangesStack = new Stack<>();
    /** All ranges but sorted after they are prepared. */
    private final Ranges rangesSorted = new Ranges();

    /** Map with all triggers. The key is the name of the trigger. */
    private final TriggerList_mxJPO triggers = new TriggerList_mxJPO();

    /** Default value of the attribute. */
    private String defaultValue = "";

    /** Flag that the attribute has multiple values. */
    private boolean multiValue = false;
    /** Flag that the attribute value is reset on clone. */
    private boolean resetOnClone = false;
    /** Flag that the attribute value is reset on revision. */
    private boolean resetOnRevision = false;

    // string attribute
    /** The attribute is a {@link Kind#String string} multi line attribute. */
    private boolean multiline = false;
    /** Maximum length of the value for {@link Kind#String string}  attributes. */
    private String maxLength = "0";

    // real / integer attribute
    /** Range value flag. */
    private boolean rangeValue = false;
    /** Stores the reference to the dimension of an attribute. */
    private String dimension = null;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the attribute object
     */
    public AttributeCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Attribute, _mxName);
    }

    /**
     * Returns the {@link #kind} of the attribute.
     *
     * @return kind of attribute
     */
    public Kind getKind()
    {
        return this.kind;
    }

    @Override
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new AttributeParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (AttributeCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/accessRuleRef".equals(_url))  {
            this.rules.add(_content);
            parsed = true;
        } else if ("/attrValueType".equals(_url))  {
            if ("2".equals(_content))  {
                this.rangeValue = true;
                parsed = true;
            } else if ("1".equals(_content))  {
                this.multiValue = true;
                parsed = true;
            } else if ("0".equals(_content))  {
                parsed = true;
            } else  {
                parsed = false;
            }

        } else if ("/defaultValue".equals(_url))  {
            this.defaultValue = _content;
            parsed = true;

        } else if ("/dimensionRef".equals(_url))  {
            this.dimension = _content;
            parsed = true;

        } else if ("/maxlength".equals(_url))  {
            this.maxLength = _content;
            parsed = true;
        } else if ("/multiline".equals(_url))  {
            this.multiline = true;
            parsed = true;

        } else if ("/primitiveType".equals(_url))  {
            for (final Kind checkKind : Kind.values())  {
                if (checkKind.attrTypeParse.equals(_content))  {
                    this.kind = checkKind;
                    break;
                }
            }
            parsed = (this.kind != null);

        } else if ("/resetonclone".equals(_url))  {
            this.resetOnClone = true;
            parsed = true;
        } else if ("/resetonrevision".equals(_url))  {
            this.resetOnRevision = true;
            parsed = true;

        } else if ("/rangeList/range".equals(_url))  {
            this.rangesStack.add(new Range());
            parsed = true;
        } else if ("/rangeList/range/rangeType".equals(_url))  {
            this.rangesStack.peek().type = AttributeCI_mxJPO.RANGE_COMP.get(_content);
            if (this.rangesStack.peek().type == null)  {
                throw new Error("unknown range comparator " + _content);
            }
            parsed = true;
        } else if ("/rangeList/range/rangeValue".equals(_url))  {
            this.rangesStack.peek().value1 = _content;
            parsed = true;
        } else if ("/rangeList/range/includingValue".equals(_url))  {
            this.rangesStack.peek().include1 = true;
            parsed = true;
        } else if ("/rangeList/range/rangeSecondValue".equals(_url))  {
            this.rangesStack.peek().value2 = _content;
            parsed = true;
        } else if ("/rangeList/range/includingSecondValue".equals(_url))  {
            this.rangesStack.peek().include2 = true;
            parsed = true;

        } else if ("/rangeProgram/programRef".equals(_url))  {
            this.rangeProgramRef = _content;
            parsed = true;
        } else if ("/rangeProgram/inputArguments".equals(_url))  {
            this.rangeProgramInputArguments = _content;
            parsed = true;

        } else if (_url.startsWith("/triggerList"))  {
            parsed = this.triggers.parse(_paramCache, _url.substring(12), _content);

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * The ranges in {@link #ranges} are sorted into {@link #rangesSorted}.
     * The program range definition is correct (by defining
     * {@link Range#value1} as {@link #rangeProgramRef} and
     * {@link Range#value2} as {@link #rangeProgramInputArguments}).
     */
    @Override
    protected void prepare()
    {
        // sort all triggers
        this.triggers.prepare();

        // sort all ranges
        Range progRange = null;
        for (final Range range : this.rangesStack)  {
            this.rangesSorted.add(range);
            if ("program".equals(range.type))  {
                progRange = range;
            }
        }

        // fix program range
        if (this.rangeProgramRef != null)  {
            if (progRange == null)  {
                progRange = new Range();
                progRange.type = "program";
                this.rangesSorted.add(progRange);
            }
            progRange.value1 = this.rangeProgramRef;
            progRange.value2 = this.rangeProgramInputArguments;
        }

        super.prepare();
    }

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .stringNotNull( "package",                  this.getPackageRef())
                .single(        "kind",                     this.kind.name().toLowerCase())
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",                   false, this.isHidden())
                .flagIfTrue(    "multivalue",        false, this.multiValue,                    (this.kind != Kind.Binary) && _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMAttrSupportsFlagMultiValue))
                .flagIfTrue(    "resetonclone",      false, this.resetOnClone,                  _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnClone))
                .flagIfTrue(    "resetonrevision",   false, this.resetOnRevision,               _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnRevision))
                .flagIfTrue(    "multiline",         false, this.multiline,                     (this.kind == Kind.String))
                .singleIfTrue(  "maxlength",                this.maxLength,                     ((this.kind == Kind.String) && _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMAttrSupportsPropMaxLength)))
                .flagIfTrue(    "rangevalue",        false, this.rangeValue,                    (((this.kind == Kind.Date) || (this.kind == Kind.Integer) || (this.kind == Kind.Real)) && _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMAttrSupportsFlagRangeValue)))
                .stringIfTrue(  "dimension",                this.dimension,                     ((this.dimension != null) && !this.dimension.isEmpty() && ((this.kind == Kind.Integer) || (this.kind == Kind.Real)) && _updateBuilder.getParamCache().getValueBoolean(ValueKeys.DMAttrSupportsDimension)))
                .listIfTrue(    "rule",                     this.rules,                         (this.rules.size() == 1))
                .string(        "default",                  this.defaultValue)
                .write(this.triggers)
                .write(this.rangesSorted)
                .properties(this.getProperties());
    }

    @Override
    public void createOld(final ParameterCache_mxJPO _paramCache)
    {
    }

    /**
     * Creates given attribute from given type with given name. Because the
     * type of the attribute must defined, the original create method must be
     * overwritten.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the new attribute for given type could not be
     *                   created
     */
    @Override
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        MqlBuilderUtil_mxJPO.mql()
                .cmd("escape add attribute ").arg(this.getName()).cmd(" type ").cmd(this.kind.attrTypeCreate)
                .exec(_paramCache.getContext());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final AttributeCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        final AttributeCI_mxJPO current = _current;

        DeltaUtil_mxJPO.calcPackage(_paramCache, _mql, this, current);
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(), (current != null) ? current.getDescription() : null);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "default",             this.defaultValue,     (current != null) ? current.defaultValue : null);
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),       (current != null) ? current.isHidden() : null);

        switch (this.kind)  {
            case String:
                DeltaUtil_mxJPO.calcFlagDelta(_mql, "multiline", false, this.multiline, (current != null) ? current.multiline : null);
                if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsPropMaxLength))  {
                    DeltaUtil_mxJPO.calcValueDelta(_mql, "maxlength", this.maxLength, (current != null) ? current.maxLength : null);
                }
                break;
            case Integer:
            case Real:
                if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsDimension))  {
                    if ((current == null) || (current.dimension == null) || current.dimension.isEmpty()) {
                        if ((this.dimension != null) && !this.dimension.isEmpty())  {
                            _paramCache.logDebug("    - set dimension '" + this.dimension + "'");
                            _mql.newLine().cmd("dimension ").arg(this.dimension);
                        }
                    } else if (!current.dimension.equals(this.dimension))  {
                        throw new UpdateException_mxJPO(
                                UpdateException_mxJPO.ErrorKey.ABSTRACTATTRIBUTE_UPDATE_DIMENSION_UPDATED,
                                this.getName(),
                                current.dimension,
                                (this.dimension != null) ? this.dimension : "");
                    }
                }
                // no break, because range value is also for integer / real!
            case Date:
                if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagRangeValue))  {
                    if ((current == null) || !current.rangeValue)  {
                        DeltaUtil_mxJPO.calcFlagDelta(_mql, "rangevalue", false, this.rangeValue, (current != null) ? current.rangeValue : null);
                    } else if (!this.rangeValue)  {
                        throw new UpdateException_mxJPO(
                                UpdateException_mxJPO.ErrorKey.ABSTRACTATTRIBUTE_UPDATE_RANGEVALUEFLAG_UPDATED,
                                this.getName());
                    }
                }
                break;
            default:
                break;
        }

        if ((this.kind != Kind.Binary) && _paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagMultiValue))  {
            if ((current == null) || !current.multiValue)  {
                DeltaUtil_mxJPO.calcFlagDelta(_mql, "multivalue", false, this.multiValue, (current != null) ? current.multiValue : null);
            } else if (!this.multiValue)  {
                throw new UpdateException_mxJPO(
                        UpdateException_mxJPO.ErrorKey.ABSTRACTATTRIBUTE_UPDATE_MULTIVALUEFLAG_UPDATED,
                        _current.getName());
            }
        }
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnClone))  {
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "resetonclone", false, this.resetOnClone, (current != null) ? current.resetOnClone : null);
        }
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnRevision))  {
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "resetonrevision", false, this.resetOnRevision, (current != null) ? current.resetOnRevision : null);
        }
        DeltaUtil_mxJPO.calcListDelta(_mql, "rule", this.rules, (current != null) ? current.rules : null);

        this.triggers       .calcDelta(_mql, (current != null) ? current.triggers : null);
        this.rangesSorted   .calcDelta(_mql, (current != null) ? current.rangesSorted : null);
        this.getProperties().calcDelta(_mql, "", (current != null) ? _current.getProperties() : null);
    }

    /**
     * Kind of the attribute.
     */
    public enum Kind
    {
        /** Binary attribute. */
        Binary("binary", "binary"),
        /** Boolean attribute. */
        Boolean("boolean", "boolean"),
        /** Date attribute. */
        Date("date", "datetime"),
        /** Integer attribute. */
        Integer("integer", "integer"),
        /** Real attribute. */
        Real("real", "real"),
        /** String attribute. */
        String("string", "string");

        /** Holds the attribute type used to create and parse for a new attribute. */
        private final String attrTypeCreate, attrTypeParse;

        Kind(final String _attrTypeCreate,
             final String _attrTypeParse)
        {
            this.attrTypeCreate = _attrTypeCreate;
            this.attrTypeParse = _attrTypeParse;
        }

        /**
         * Returns the name of the attribute type used within create.
         *
         * @return name of attribute used within create
         */
        public String getAttrTypeCreate()
        {
            return this.attrTypeCreate;
        }
    }

    /**
     * Class holding range values of this attribute.
     */
    public static class Range
        implements Comparable<Range>
    {
        /**
         * Holds the range type. Typically following range types are known:
         * <ul>
         * <li>between<li>
         * <li>equal<li>
         * <li>greaterthan<li>
         * <li>greaterthanequal<li>
         * <li>lessthan<li>
         * <li>lessthanequal<li>
         * <li>match<li>
         * <li>notequal<li>
         * <li>notmatch<li>
         * <li>notsmatch<li>
         * <li>smatch<li>
         * </ul>
         */
        private String type = null;

        /** Hold the first range value. */
        private String value1 = null;
        /** Holds the second range value. */
        private String value2 = null;
        /** Include first value (used for range type &quot;between&quot;). */
        private Boolean include1 = false;
        /** Include second value (used for range type &quot;between&quot;). */
        private Boolean include2 = false;

        /**
         * Write this range value to the writer instance.
         *
         * @param _updateBuilder                update builder
         */
        private void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            _updateBuilder
                    .stepStartNewLine()
                    .stepSingle("range").stepSingle(this.type).stepString(this.value1);

            if ("program".equals(this.type) && (this.value2 != null))  {
                _updateBuilder.stepSingle("input").stepString(this.value2);
            } else if ("between".equals(this.type))  {
                _updateBuilder
                        .stepSingle(this.include1 ? "inclusive" : "exclusive")
                        .stepString(this.value2)
                        .stepSingle(this.include2 ? "inclusive" : "exclusive");
            }
            _updateBuilder.stepEndLine();
        }

        /**
         * Compare this range instance with given range instance. The compare
         * is done for each instance variable in this order:
         * <ul>
         * <li>{@link #type}</li>
         * <li>{@link #include1}</li>
         * <li>{@link #include2}</li>
         * <li>{@link #value1}</li>
         * <li>{@link #value2}</li>
         * </ul>
         * If one of the compared instance variables are not equal, the
         * compared value is returned. So only if all instance variable has the
         * same values the ranges itself are identically.
         *
         * @param _range        range instance to which this instance must be
         *                      compared to
         * @return &quot;0&quot; if both ranges are equal; &quot;1&quot; if
         *         greater; otherwise &quot;-1&quot;
         */
        @Override()
        public int compareTo(final Range _range)
        {
            int ret = StringUtil_mxJPO.compare(this.type, _range.type);
            if (ret == 0)  {
                ret = this.include1.compareTo(_range.include1);
            }
            if (ret == 0)  {
                ret = this.include2.compareTo(_range.include2);
            }
            if (ret == 0)  {
                ret = StringUtil_mxJPO.compare(this.value1, _range.value1);
            }
            if (ret == 0)  {
                ret = StringUtil_mxJPO.compare(this.value2, _range.value2);
            }
            return ret;
        }

        @Override
        public String toString()
        {
            final StringBuilder ret = new StringBuilder("[range type=").append(this.type);
            // if the range is a program it is a 'global' attribute info
            if ("program".equals(this.type))  {
                ret.append(", program=").append(UpdateUtils_mxJPO.encodeText(this.value1));
                if (this.value2 != null)  {
                    ret.append(", input=").append(UpdateUtils_mxJPO.encodeText(this.value2));
                }
            } else  {
                if ("between".equals(this.type))  {
                    ret.append(", value1=").append(UpdateUtils_mxJPO.encodeText(this.value1))
                        .append(' ').append(this.include1 ? "inclusive" : "exclusive")
                        .append(", value2=").append(UpdateUtils_mxJPO.encodeText(this.value2))
                        .append(' ').append(this.include2 ? "inclusive" : "exclusive");
                } else  {
                    ret.append(", value=").append(UpdateUtils_mxJPO.encodeText(this.value1));
                }
            }
            return ret.append(']').toString();
        }
    }

    /**
     * Handles all ranges.
     */
    class Ranges
        extends TreeSet<Range>
        implements UpdateList
    {
        /** Dummy serial version UID. */
        private static final long serialVersionUID = 1L;

        /**
         * Append all ranges.
         *
         * @param _updateBuilder    update builder
         */
        @Override()
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            for (final Range range : this)  {
                range.write(_updateBuilder);
            }
        }

        /**
         * Calculates the delta between {@code _currents} range definition and
         * this target range definitions.
         *
         * @param _mql      MQL builder to append the delta
         * @param _currents current ranges
         */
        protected void calcDelta(final MultiLineMqlBuilder _mql,
                                 final Ranges _currents)
        {
            // remove obsolete ranges
            if (_currents != null)  {
                for (final Range current : _currents)  {
                    boolean found = false;
                    for (final Range target : this)  {
                        if (current.compareTo(target) == 0)  {
                            found = true;
                            break;
                        }
                    }
                    if (!found)  {
                        _mql.newLine()
                            .cmd("remove range ").cmd(current.type).cmd(" ").arg(current.value1);
                        if ("between".equals(current.type))  {
                            _mql.cmd(" ").cmd(current.include1 ? "inclusive" : "exclusive")
                                .cmd(" ").arg(current.value2).cmd(" ").cmd(current.include2 ? "inclusive" : "exclusive");
                        }
                    }
                }
            }
            // append new ranges
            for (final Range target : this)  {
                boolean found = false;
                if (_currents != null)  {
                    for (final Range current : _currents)  {
                        if (current.compareTo(target) == 0)  {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found)  {
                    _mql.newLine()
                        .cmd("add range ").cmd(target.type).cmd(" ").arg(target.value1);
                    if ("between".equals(target.type))  {
                        _mql.cmd(" ").cmd(target.include1 ? "inclusive" : "exclusive")
                            .cmd(" ").arg(target.value2).cmd(" ").cmd(target.include2 ? "inclusive" : "exclusive");
                    } else if ("program".equals(target.type))  {
                        _mql.cmd(" input ").arg(target.value2);
                    }
                }
            }
        }
    }
}
