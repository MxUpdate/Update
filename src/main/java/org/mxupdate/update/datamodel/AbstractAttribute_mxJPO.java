/*
 * Copyright 2008-2014 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.attribute.AttributeDefParser_mxJPO;
import org.mxupdate.update.datamodel.helper.TriggerList_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to evaluate information from attributes within MX used to
 * export, delete and update an attribute.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractAttribute_mxJPO<CLASS extends AbstractAttribute_mxJPO<CLASS>>
    extends AbstractAdminObject_mxJPO
{
    /**
     * MQL list statement with select for the attribute type and name used
     * to get the list of all attribute MX names depending on the attribute
     * type.
     */
    private static final String SELECT_ATTRS = "list attribute * select type name dump";

    /**
     * Key used to identify the update of a dimension of an attribute within
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     */
    private static final String JPO_CALLER_KEY_DIM = "defineAttrDimension";

    /**
     * Called TCL procedure within the TCL update to update the dimension of an
     * attribute. The TCL procedure calls method
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)} with the new
     * attribute definition.
     */
    private static final String TCL_PROCEDURE_DIM
            = "proc defineAttrDimension {_sName _sDimension}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller " + AbstractAttribute_mxJPO.JPO_CALLER_KEY_DIM + " $_sName $_sDimension\n"
            + "}\n";

    /**
     * Key used to identify the update of an attribute within
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     */
    private static final String JPO_CALLER_KEY = "updateAttribute";

    /**
     * Called TCL procedure within the TCL update to parse the new policy
     * definition. The TCL procedure calls method
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)} with the new
     * policy definition. All quot's are replaced by <code>@0@0@</code> and all
     * apostroph's are replaced by <code>@1@1@</code>.
     */
    private static final String TCL_PROCEDURE
            = "proc updateAttribute {_sName _lsArgs}  {\n"
                + "regsub -all {'} $_lsArgs {@0@0@} sArg\n"
                + "regsub -all {\\\"} $sArg {@1@1@} sArg\n"
                + "regsub -all {\\\\\\[} $sArg {[} sArg\n"
                + "regsub -all {\\\\\\]} $sArg {]} sArg\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller " + AbstractAttribute_mxJPO.JPO_CALLER_KEY + " ${_sName} \"${sArg}\"\n"
            + "}\n";

    /**
     * Set of all ignored URLs from the XML definition for attributes.
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        // max length only valid for string attributes!
        AbstractAttribute_mxJPO.IGNORED_URLS.add("/maxlength");
        AbstractAttribute_mxJPO.IGNORED_URLS.add("/primitiveType");
        AbstractAttribute_mxJPO.IGNORED_URLS.add("/rangeList");
        AbstractAttribute_mxJPO.IGNORED_URLS.add("/rangeProgram");
        AbstractAttribute_mxJPO.IGNORED_URLS.add("/triggerList");
    }

    /**
     * Mapping between the comparators defined within XML and the comparators
     * used within MX.
     */
    private static final Map<String,String> RANGE_COMP = new HashMap<String,String>();
    static  {
        AbstractAttribute_mxJPO.RANGE_COMP.put("equal",             "=");
        AbstractAttribute_mxJPO.RANGE_COMP.put("greaterthan",       ">");
        AbstractAttribute_mxJPO.RANGE_COMP.put("greaterthanequal",  ">=");
        AbstractAttribute_mxJPO.RANGE_COMP.put("lessthan",          "<");
        AbstractAttribute_mxJPO.RANGE_COMP.put("lessthanequal",     "<=");
        AbstractAttribute_mxJPO.RANGE_COMP.put("notequal",          "!=");
        AbstractAttribute_mxJPO.RANGE_COMP.put("match",             "match");
        AbstractAttribute_mxJPO.RANGE_COMP.put("notmatch",          "!match");
        AbstractAttribute_mxJPO.RANGE_COMP.put("smatch",            "smatch");
        AbstractAttribute_mxJPO.RANGE_COMP.put("notsmatch",         "!smatch");
        AbstractAttribute_mxJPO.RANGE_COMP.put("programRange",      "program");
        AbstractAttribute_mxJPO.RANGE_COMP.put("between",           "between");
    }

    /** Set holding all rules referencing this attribute. */
    private final  Set<String> rules = new TreeSet<String>();


    /**
     * If the range is a program the value references a program. Because only
     * one range program could be defined at maximum, specific parsing exists!
     * So the range program is defined as variable directly on the attribute
     * (and correct in {@link #prepare(ParameterCache_mxJPO)}).
     */
    private String rangeProgramRef;
    /** If the range is a program the value are the input arguments. */
    private String rangeProgramInputArguments;
    /** Stores the ranges of the attribute (used while parsing the XML attribute). */
    private final Stack<Range> rangesStack = new Stack<Range>();
    /** All ranges but sorted after they are prepared. */
    private final Ranges rangesSorted = new Ranges();

    /** Map with all triggers. The key is the name of the trigger. */
    private final TriggerList_mxJPO triggers = new TriggerList_mxJPO();

    /** Default value of the attribute. */
    private String defaultValue = null;

    /** Holds the attribute type used to create a new attribute. */
    private final String attrTypeCreate;

    /**
     * Holds the attribute including the &quot;,&quot; returned from the
     * <code>list attribute</code> statement {@link #SELECT_ATTRS}.
     */
    private final String attrTypeList;

    /** Stores the reference to the dimension of an attribute. */
    private String dimension;

    /**
     * The value is needed to hold the information if the update of the
     * dimension is run (because the TCL procedure {@link #TCL_PROCEDURE} must
     * not be called everytime...).
     */
    private boolean dimensionUpdated = false;

    /** Flag that the attribute value is reset on clone. */
    private boolean resetOnClone = false;
    /** Flag that the attribute value is reset on revision. */
    private boolean resetOnRevision = false;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the attribute object
     * @param _attrTypeCreate   attribute type used to create new attribute
     * @param _attrTypeList     attribute type incl. &quot;,&quot; returned
     *                          from the select list statement
     */
    public AbstractAttribute_mxJPO(final TypeDef_mxJPO _typeDef,
                                   final String _mxName,
                                   final String _attrTypeCreate,
                                   final String _attrTypeList)
    {
        super(_typeDef, _mxName);
        this.attrTypeCreate = _attrTypeCreate;
        this.attrTypeList = _attrTypeList;
    }

    /**
     * Searches for all attribute objects depending on the attribute type.
     *
     * @param _paramCache   parameter cache
     * @return set of MX names of all attributes of attribute type
     *         {@link #attrTypeList}
     * @throws MatrixException if the query for attribute objects failed
     * @see #SELECT_ATTRS
     * @see #attrTypeList
     */
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final Set<String> ret = new TreeSet<String>();
        final int length = this.attrTypeList.length();
        for (final String name : MqlUtil_mxJPO.execMql(_paramCache, AbstractAttribute_mxJPO.SELECT_ATTRS).split("\n"))  {
            if (!"".equals(name) && name.startsWith(this.attrTypeList))  {
                ret.add(name.substring(length));
            }
        }
        return ret;
    }

    /**
     * The method parses the attribute specific XML URLs. This includes
     * information about:
     * <ul>
     * <li>assigned {@link #rules}</li>
     * <li>{@link #defaultValue default value}</li>
     * <li>{@link #resetOnClone reset on clone} flag</li>
     * <li>{@link #resetOnRevision reset on revision} flag</li>
     * <li>defined {@link #ranges}, {@link #rangeProgramRef program ranges} and
     *     their {@link #rangeProgramInputArguments input arguments}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (AbstractAttribute_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/accessRuleRef".equals(_url))  {
            this.rules.add(_content);
            parsed = true;
        } else if ("/defaultValue".equals(_url))  {
            this.defaultValue = _content;
            parsed = true;
        } else if ("/dimensionRef".equals(_url))  {
            this.dimension = _content;
            parsed = true;
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
            this.rangesStack.peek().type = AbstractAttribute_mxJPO.RANGE_COMP.get(_content);
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
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * The ranges in {@link #ranges} are sorted into {@link #rangesSorted}.
     * The program range definition is correct (by defining
     * {@link Range#value1} as {@link #rangeProgramRef} and
     * {@link Range#value2} as {@link #rangeProgramInputArguments}).
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the prepare from the derived class failed
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
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

        super.prepare(_paramCache);
    }

    /**
     * Writes the TCL update file for this attribute. The original method is
     * overwritten because am attribute could not be only updated. A compare
     * must be done in front or otherwise some data is lost. Following
     * information is written
     * <ul>
     * <li>flag &quot;{@link #resetonclone}&quot; (if parameter
     *     {@link #PARAM_SUPPORT_FLAG_RESET_ON_CLONE} is defined)</li>
     * <li>flag &quot;{@link #resetOnRevision}&quot; (if parameter
     *     {@link #PARAM_SUPPORT_FLAG_RESET_ON_REVISION} is defined)</li>
     * <li>{@link #writeAttributeSpecificValues(ParameterCache_mxJPO, Appendable)
     *     attribute specific properties and flags}</li>
     * <li>all assigned {@link #rules}</li>
     * <li>{@link #defaultValue default value}</li>
     * <li>{@link #writeTriggers(Appendable) triggers}</li>
     * <li>{@link #rangesSorted ranges}</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written to the
     *                     writer instance
     * @see #PARAM_SUPPORT_FLAG_RESET_ON_CLONE
     * @see #PARAM_SUPPORT_FLAG_RESET_ON_REVISION
     * @see #writeAttributeSpecificValues(ParameterCache_mxJPO, Appendable)
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        // write header
        this.writeHeader(_paramCache, _out);

        // write attribute
        _out.append("updateAttribute \"${NAME}\"  {\n")
            .append("  description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"\n")
            .append("  ").append(this.isHidden() ? "" : "!").append("hidden\n");
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnClone))  {
            _out.append("  ").append(this.resetOnClone ? "" : "!").append("resetonclone\n");
        }
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnRevision))  {
            _out.append("  ").append(this.resetOnRevision ? "" : "!").append("resetonrevision\n");
        }
        this.writeAttributeSpecificValues(_paramCache, _out);

        if (!this.rules.isEmpty())  {
            assert (this.rules.size() == 1);
            _out.append("  rule \"").append(StringUtil_mxJPO.convertTcl(this.rules.iterator().next())).append("\"\n");
        }

        _out.append("  default \"").append((this.defaultValue != null) ? StringUtil_mxJPO.convertTcl(this.defaultValue) : "").append("\"\n");

        // append triggers
        this.triggers.write(_out, "  ", "\n");

        // append ranges
        this.rangesSorted.write(_out);

        _out.append("}");
    }

    /**
     * Only implemented as stub because
     * {@link #write(ParameterCache_mxJPO, Appendable)} is new implemented.
     *
     * @param _paramCache   parameter cache (not used)
     * @param _out          appendable instance to the TCL update file (not
     *                      used)
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
    {
    }

    /**
     * Defines attribute specific values which are written to the CI file.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written to the
     *                     writer instance
     */
    protected void writeAttributeSpecificValues(final ParameterCache_mxJPO _paramCache,
                                                final Appendable _out)
        throws IOException
    {
    }

    /**
     * Writes the TCL update code for the dimension of an attribute (if
     * exists).
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written to the
     *                     writer instance
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        super.writeEnd(_paramCache, _out);
        if (this.dimension != null)  {
            _out.append("\ndefineAttrDimension \"${NAME}\" \"")
                .append(StringUtil_mxJPO.convertTcl(this.dimension))
                .append('\"');
        }
    }

    /**
     * Creates given attribute from given type with given name. Because the
     * type of the attribute must defined, the original create method must be
     * overwritten.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the new attribute for given type could not be
     *                   created
     * @see #attrTypeCreate
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape add ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                .append(" type ").append(this.attrTypeCreate);
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this attribute. This includes:
     * <ul>
     * <li>set to not hidden</li>
     * <li>flag &quot;resetonclone&quot; is disabled (if parameter
     *     {@link #PARAM_SUPPORT_FLAG_RESET_ON_CLONE} is defined)</li>
     * <li>flag &quot;resetonrevision&quot; is disabled (if parameter
     *     {@link #PARAM_SUPPORT_FLAG_RESET_ON_REVISION} is defined)</li>
     * <li>flag &quot;multiline&quot; is disabled for string attributes</li>
     * <li>reset description and default value</li>
     * <li>remove all ranges</li>
     * <li>define the TCL procedure {@link #TCL_PROCEDURE} to set the
     *     dimension</li>
     * <li>if the dimension is not updated within the TCL update file (tested
     *     with {@link #dimensionUpdated}) method
     *     {@link #updateDimension(ParameterCache_mxJPO, String)} is called
     *     </li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     * @see Range#remove4Update(Appendable)
     * @see #updateDimension(ParameterCache_mxJPO, String)
     * @see #dimensionUpdated
     * @see #TCL_PROCEDURE
     * @see #PARAM_SUPPORT_FLAG_RESET_ON_CLONE
     * @see #PARAM_SUPPORT_FLAG_RESET_ON_REVISION
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // add TCL code for the procedure
        final StringBuilder preTCLCode = new StringBuilder()
                .append(AbstractAttribute_mxJPO.TCL_PROCEDURE_DIM)
                .append(AbstractAttribute_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, _preMQLCode, _postMQLCode, preTCLCode, _tclVariables, _sourceFile);

        // is the dimension already updated?
        if (!this.dimensionUpdated)  {
            this.updateDimension(_paramCache, "");
        }
    }

    /**
     * The method is called from the TCL update code to define the dimension
     * for this attribute. If the correct use case is defined method
     * {@link #updateDimension(ParameterCache_mxJPO, String)} is called.
     *
     * @param _paramCache   parameter cache
     * @param _args         first index defines the use case (must be
     *                      &quot;defineAttrDimension&quot; that the dimension
     *                      of the attribute is updated); second index the name
     *                      of the attribute to update; third index the new
     *                      dimension to set
     * @throws Exception if the update of the dimension failed or for all other
     *                   use cases from super JPO call
     * @see #updateDimension(ParameterCache_mxJPO, String)
     * @see #dimensionUpdated
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        // check if dimension is defined
        if ((_args.length == 3) && AbstractAttribute_mxJPO.JPO_CALLER_KEY.equals(_args[0])) {
// TODO: Exception Handling
            // check that attribute names are equal
            if (!this.getName().equals(_args[1]))  {
                throw new Exception("wrong attribute '"
                        + _args[1] + "' is set to update (currently attribute '" + this.getName()
                        + "' is updated!)");
            }
            this.updateAttribute(_paramCache, _args[2]);
        } else if  ((_args.length == 3) && AbstractAttribute_mxJPO.JPO_CALLER_KEY_DIM.equals(_args[0])) {
            // check that attribute names are equal
            if (!this.getName().equals(_args[1]))  {
                throw new Exception("dimension for wrong attribute '"
                        + _args[1] + "' is set (attribute '" + this.getName()
                        + "' is updated!)");
            }

            this.updateDimension(_paramCache, _args[2]);
            this.dimensionUpdated = true;
        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Updates the dimension of this attribute. If a dimension is already
     * defined for this attribute and it is not the same attribute, an
     * exception is thrown (because information could be lost).
     *
     * @param _paramCache   parameter cache
     * @param _dimension    new dimension to set
     * @throws Exception if update failed or the dimension of an update should
     *                   be changed (and some information could be lost)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    protected void updateDimension(final ParameterCache_mxJPO _paramCache,
                                   final String _dimension)
        throws Exception
    {
        if ((this.dimension == null) || "".equals(this.dimension))  {
            if (!"".equals(_dimension))  {
                _paramCache.logDebug("    - set dimension '" + _dimension + "'");
                MqlUtil_mxJPO.execMql(_paramCache,
                        new StringBuilder().append("escape mod attribute \"")
                        .append(StringUtil_mxJPO.convertMql(this.getName()))
                        .append("\" add dimension \"")
                        .append(StringUtil_mxJPO.convertMql(_dimension))
                        .append('\"'));
            }
        } else if (!this.dimension.equals(_dimension))  {
            throw new Exception("If dimension '" + this.dimension
                    + "' is changed to new dimension '" + _dimension
                    + "' some information could be lost!");
        }
    }

    private void updateAttribute(final ParameterCache_mxJPO _paramCache,
                                 final String _code)
        throws Exception
    {
        final String code = _code.replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

        final AttributeDefParser_mxJPO parser = new AttributeDefParser_mxJPO(new StringReader(code));
        @SuppressWarnings("unchecked")
        final CLASS attribute = (CLASS) parser.attribute(_paramCache, this.getTypeDef(), this.getName());


        final MqlBuilder_mxJPO mql = new MqlBuilder_mxJPO(new StringBuilder("escape mod attribute \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\""));

        this.calcDelta(_paramCache, mql, attribute);

        mql.exec(_paramCache);
    }

    /**
     * Calculates the delta between this current attribute definition and the
     * {@code _target} attribute definition and appends the MQL append commands
     * to {@code _cmd}.
     *
     * @param _paramCache   parameter cache
     * @param _cmd          string builder to append the MQL commands
     * @param _target       target attribute definition
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MqlBuilder_mxJPO _mql,
                             final CLASS _target)
    {
        final AbstractAttribute_mxJPO<CLASS> target = _target;

        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",     target.getDescription(), this.getDescription());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "default",         target.defaultValue,     this.defaultValue);
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",          target.isHidden(),       this.isHidden());

        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnClone))  {
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "resetonclone",    target.resetOnClone,     this.resetOnClone);
        }
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnRevision))  {
            DeltaUtil_mxJPO.calcFlagDelta(_mql,  "resetonrevision", target.resetOnRevision,  this.resetOnRevision);
        }
        DeltaUtil_mxJPO.calcListDelta(_mql, "rule", target.rules, this.rules);

        target.triggers.calcDelta(this.triggers, _mql);
        target.rangesSorted.calcDelta(this.rangesSorted, _mql);
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
        String type = null;

        /** Hold the first range value. */
        String value1 = null;
        /** Holds the second range value. */
        String value2 = null;
        /** Include first value (used for range type &quot;between&quot;). */
        Boolean include1 = false;
        /** Include second value (used for range type &quot;between&quot;). */
        Boolean include2 = false;

        /**
         * Write this range value to the writer instance.
         *
         * @param _out                          writer instance
         * @param _rangeProgramRef              name of the program
         * @param _rangeProgramInputArguments   input arguments from the
         *                                      program input
         * @throws IOException if write to the writer instance is not possible
         */
        private void write(final Appendable _out)
            throws IOException
        {
            _out.append("  range ").append(this.type);
            // if the range is a program it is a 'global' attribute info
            if ("program".equals(this.type))  {
                _out.append(" \"").append(StringUtil_mxJPO.convertTcl(this.value1)).append('\"');
                if (this.value2 != null)  {
                    _out.append(" input \"").append(StringUtil_mxJPO.convertTcl(this.value2)).append('\"');
                }
            } else  {
                _out.append(" \"").append(StringUtil_mxJPO.convertTcl(this.value1)).append("\"");
                if ("between".equals(this.type))  {
                    _out.append(' ').append(this.include1 ? "inclusive" : "exclusive")
                        .append(" \"").append(StringUtil_mxJPO.convertTcl(this.value2)).append("\"")
                        .append(' ').append(this.include2 ? "inclusive" : "exclusive");
                }
            }
            _out.append('\n');
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

        @Override()
        public String toString()
        {
            final StringBuilder ret = new StringBuilder("[range type=").append(this.type);
            // if the range is a program it is a 'global' attribute info
            if ("program".equals(this.type))  {
                ret.append(", program=").append(StringUtil_mxJPO.convertTcl(this.value1));
                if (this.value2 != null)  {
                    ret.append(", input=").append(StringUtil_mxJPO.convertTcl(this.value2));
                }
            } else  {
                if ("between".equals(this.type))  {
                    ret.append(", value1=").append(StringUtil_mxJPO.convertTcl(this.value1))
                        .append(' ').append(this.include1 ? "inclusive" : "exclusive")
                        .append(", value2=").append(StringUtil_mxJPO.convertTcl(this.value2))
                        .append(' ').append(this.include2 ? "inclusive" : "exclusive");
                } else  {
                    ret.append(", value=").append(StringUtil_mxJPO.convertTcl(this.value1));
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
    {
        /** Serial Version UID. */
        private static final long serialVersionUID = -3281559363746986173L;

        /**
         * Append all ranges.
         *
         * @param _out  writer instance
         * @throws IOException if write to the writer instance is not possible
         */
        void write(final Appendable _out)
            throws IOException
        {
            for (final Range range : this)  {
                range.write(_out);
            }
        }

        /**
         * Calculates the delta between {@code _currents} range definition and
         * this target range definitions.
         *
         * @param _currents current ranges
         * @param _mql      MQL builder to append the delta
         */
        protected void calcDelta(final Ranges _currents,
                                 final MqlBuilder_mxJPO _mql)
        {
            // remove obsolete ranges
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
                        .append("remove range ");
                    if ("programRange".equals(current.type))  {
                        _mql.lastLine()
                            .append("program \"").append(StringUtil_mxJPO.convertMql(current.value1)).append('\"');
                    } else  {
                        _mql.lastLine()
                            .append(current.type)
                            .append(" \"").append(StringUtil_mxJPO.convertMql(current.value1)).append('\"');
                        if ("between".equals(current.type))  {
                            _mql.lastLine()
                                .append(' ').append(current.include1 ? "inclusive" : "exclusive")
                                .append(" \"").append(StringUtil_mxJPO.convertMql(current.value2)).append("\"")
                                .append(' ').append(current.include2 ? "inclusive" : "exclusive");
                        }
                    }
                }
            }
            // append new ranges
            for (final Range target : this)  {
                boolean found = false;
                for (final Range current : _currents)  {
                    if (current.compareTo(target) == 0)  {
                        found = true;
                        break;
                    }
                }
                if (!found)  {
                    _mql.newLine()
                        .append("add range ").append(target.type);
                    // if the range is a program it is a 'global' attribute info
                    if ("program".equals(target.type))  {
                        _mql.lastLine()
                            .append(" \"").append(StringUtil_mxJPO.convertMql(target.value1)).append('\"');
                        if (target.value2 != null)  {
                            _mql.lastLine()
                                .append(" input \"").append(StringUtil_mxJPO.convertMql(target.value2)).append('\"');
                        }
                    } else  {
                        _mql.lastLine()
                            .append(" \"").append(StringUtil_mxJPO.convertMql(target.value1)).append("\"");
                        if ("between".equals(target.type))  {
                            _mql.lastLine()
                                .append(' ').append(target.include1 ? "inclusive" : "exclusive")
                                .append(" \"").append(StringUtil_mxJPO.convertMql(target.value2)).append("\"")
                                .append(' ').append(target.include2 ? "inclusive" : "exclusive");
                        }
                    }
                }
            }
        }
    }
}
