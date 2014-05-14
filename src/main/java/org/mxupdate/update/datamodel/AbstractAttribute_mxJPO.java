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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
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
abstract class AbstractAttribute_mxJPO
    extends AbstractDMWithTriggers_mxJPO
{
    /**
     * MQL list statement with select for the attribute type and name used
     * to get the list of all attribute MX names depending on the attribute
     * type.
     */
    private static final String SELECT_ATTRS = "list attribute * select type name dump";

    /**
     * Key used to identify the update of an attribute within
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     */
    private static final String JPO_CALLER_KEY = "defineAttrDimension";

    /**
     * Called TCL procedure within the TCL update to parse the new policy
     * definition. The TCL procedure calls method
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)} with the new
     * policy definition. All quot's are replaced by <code>@0@0@</code> and all
     * apostroph's are replaced by <code>@1@1@</code>.
     */
    private static final String TCL_PROCEDURE
            = "proc defineAttrDimension {_sName _sDimension}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller " + AbstractAttribute_mxJPO.JPO_CALLER_KEY + " $_sName $_sDimension\n"
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

    /**
     * Set holding all rules referencing this attribute.
     */
    private final  Set<String> rules = new TreeSet<String>();

    /**
     * Stores the ranges of the attribute (used while parsing the XML
     * attribute).
     */
    private final Stack<Range> ranges = new Stack<Range>();

    /**
     * If the range is a program the value references a program. Only one range
     * program could be defined at maximum! So the range program is defined as
     * variable directly on the attribute and not as range.
     */
    private String rangeProgramRef;

    /**
     * If the range is a program the value are the input arguments.
     */
    private String rangeProgramInputArguments;

    /**
     * All ranges but sorted after they are prepared.
     */
    private final Set<Range> rangesSorted = new TreeSet<Range>();

    /**
     * Default value of the attribute.
     */
    private String defaultValue = null;

    /**
     * Holds the attribute type used to create a new attribute.
     */
    private final String attrTypeCreate;

    /**
     * Holds the attribute including the &quot;,&quot; returned from the
     * <code>list attribute</code> statement {@link #SELECT_ATTRS}.
     */
    private final String attrTypeList;

    /**
     * Stores the reference to the dimension of an attribute.
     */
    private String dimension;

    /**
     * The value is needed to hold the information if the update of the
     * dimension is run (because the TCL procedure {@link #TCL_PROCEDURE} must
     * not be called everytime...).
     */
    private boolean dimensionUpdated = false;

    /**
     * Flag that the attribute value is reset on clone.
     */
    private boolean resetOnClone = false;

    /**
     * Flag that the attribute value is reset on revision.
     */
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
            this.ranges.add(new Range());
            parsed = true;
        } else if ("/rangeList/range/rangeType".equals(_url))  {
            this.ranges.peek().type = AbstractAttribute_mxJPO.RANGE_COMP.get(_content);
            if (this.ranges.peek().type == null)  {
                throw new Error("unknown range comparator " + _content);
            }
            parsed = true;
        } else if ("/rangeList/range/rangeValue".equals(_url))  {
            this.ranges.peek().value1 = _content;
            parsed = true;
        } else if ("/rangeList/range/includingValue".equals(_url))  {
            this.ranges.peek().include1 = true;
            parsed = true;
        } else if ("/rangeList/range/rangeSecondValue".equals(_url))  {
            this.ranges.peek().value2 = _content;
            parsed = true;
        } else if ("/rangeList/range/includingSecondValue".equals(_url))  {
            this.ranges.peek().include2 = true;
            parsed = true;

        } else if ("/rangeProgram/programRef".equals(_url))  {
            this.rangeProgramRef = _content;
            parsed = true;
        } else if ("/rangeProgram/inputArguments".equals(_url))  {
            this.rangeProgramInputArguments = _content;
            parsed = true;

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * The ranges in {@link #ranges} are sorted into {@link #rangesSorted}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the prepare from the derived class failed
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        for (final Range range : this.ranges)  {
            this.rangesSorted.add(range);
        }
        super.prepare(_paramCache);
    }

    /**
     * Writes specific information about the cached attribute to the given
     * writer instance. Following information is written
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
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    ").append(this.isHidden() ?        "hidden"          : "!hidden");
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnClone))  {
            _out.append(" \\\n    ").append(this.resetOnClone ?      "resetonclone"    : "!resetonclone");
        }
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnRevision))  {
            _out.append(" \\\n    ").append(this.resetOnRevision ?   "resetonrevision" : "!resetonrevision");
        }
        this.writeAttributeSpecificValues(_paramCache, _out);
        for (final String rule : this.rules)  {
            _out.append(" \\\n    add rule \"").append(StringUtil_mxJPO.convertTcl(rule)).append("\"");
        }
        if (this.defaultValue != null)  {
            _out.append(" \\\n    default \"").append(StringUtil_mxJPO.convertTcl(this.defaultValue)).append("\"");
        } else  {
            _out.append(" \\\n    default \"\"");
        }
        // append triggers
        this.writeTriggers(_out);
        // append ranges
        for (final Range range : this.rangesSorted)  {
            range.write(_out);
        }
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
        // remove all properties
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !hidden description \"\" default \"\"");
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnClone))  {
            preMQLCode.append(" !resetonclone");
        }
        if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsFlagResetOnRevision))  {
            preMQLCode.append(" !resetonrevision");
        }
        // remove rules
        for (final String rule : this.rules)  {
            preMQLCode.append(" remove rule \"").append(StringUtil_mxJPO.convertMql(rule)).append('\"');
        }
        // remove ranges
        for (final Range range : this.rangesSorted)  {
            range.remove4Update(preMQLCode);
        }
        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);
        // add TCL code for the procedure
        final StringBuilder preTCLCode = new StringBuilder()
                .append(AbstractAttribute_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, preTCLCode, _tclVariables, _sourceFile);

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
        if ((_args.length == 0) || !AbstractAttribute_mxJPO.JPO_CALLER_KEY.equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else  {
            // check that attribute names are equal
            if (!this.getName().equals(_args[1]))  {
                throw new Exception("dimension for wrong attribute '"
                        + _args[1] + "' is set (attribute '" + this.getName()
                        + "' is updated!)");
            }

            this.updateDimension(_paramCache, _args[2]);
            this.dimensionUpdated = true;
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

    /**
     * Class holding range values of this attribute.
     */
    private class Range
        implements Comparable<AbstractAttribute_mxJPO.Range>
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

        /**
         * Hold the first range value.
         */
        String value1 = null;

        /**
         * Holds the second range value.
         */
        String value2 = null;

        /**
         * Include first value (used for range type &quot;between&quot;).
         */
        Boolean include1 = false;

        /**
         * Include second value (used for range type &quot;between&quot;).
         */
        Boolean include2 = false;

        /**
         * Write this range value to the writer instance.
         * If the range type is a program, the name of the program and the
         * input arguments are defined directly on the attribute in
         * {@link AbstractAttribute_mxJPO#rangeProgramRef} and
         * {@link AbstractAttribute_mxJPO#rangeProgramInputArguments}.
         *
         * @param _out  writer instance
         * @throws IOException if write to the writer instance is not possible
         */
        private void write(final Appendable _out)
                throws IOException
        {
            _out.append(" \\\n    add range ").append(this.type);
            // if the range is a program it is a 'global' attribute info
            if ("program".equals(this.type))  {
                _out.append(" \"")
                    .append(StringUtil_mxJPO.convertTcl(AbstractAttribute_mxJPO.this.rangeProgramRef))
                    .append('\"');
                if (AbstractAttribute_mxJPO.this.rangeProgramInputArguments != null)  {
                    _out.append(" input \"")
                        .append(StringUtil_mxJPO.convertTcl(AbstractAttribute_mxJPO.this.rangeProgramInputArguments))
                        .append('\"');
                }
            } else  {
                _out.append(" \"").append(StringUtil_mxJPO.convertTcl(this.value1)).append("\"");
                if ("between".equals(this.type))  {
                    if (this.include1)  {
                        _out.append(" inclusive");
                    } else  {
                        _out.append(" exclusive");
                    }
                    _out.append(" \"").append(StringUtil_mxJPO.convertTcl(this.value2)).append("\"");
                    if (this.include2)  {
                        _out.append(" inclusive");
                    } else  {
                        _out.append(" exclusive");
                    }
                }
            }
        }

        /**
         * Appends the MQL code to remove this range depending on the type of
         * range in {@link #type}. If it is a program the related name of
         * program is defined as attribute variable in
         * {@link AbstractAttribute_mxJPO#rangeProgramRef}.
         *
         * @param _out      appendable instance where the MQL code is appended
         *                  to remove this range
         * @throws IOException if the MQL code could not appended
         */
        private void remove4Update(final Appendable _out)
                throws IOException
        {
            _out.append(" remove range ");

            if ("programRange".equals(this.type))  {
                _out.append("program \"")
                    .append(StringUtil_mxJPO.convertMql(AbstractAttribute_mxJPO.this.rangeProgramRef))
                    .append('\"');
            } else  {
                _out.append(this.type)
                    .append(" \"").append(StringUtil_mxJPO.convertMql(this.value1)).append('\"');
                if ("between".equals(this.type))  {
                    if (this.include1)  {
                        _out.append(" inclusive");
                    } else  {
                        _out.append(" exclusive");
                    }
                    _out.append(" \"").append(StringUtil_mxJPO.convertMql(this.value2)).append("\"");
                    if (this.include2)  {
                        _out.append(" inclusive");
                    } else  {
                        _out.append(" exclusive");
                    }
                }
            }
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
            int ret = this.type.compareTo(_range.type);
            if (ret == 0)  {
                ret = this.include1.compareTo(_range.include1);
            }
            if (ret == 0)  {
                ret = this.include2.compareTo(_range.include2);
            }
            if (ret == 0)  {
                ret = (this.value1 != null)
                      ? ((_range.value1 != null) ? this.value1.compareTo(_range.value1) : 0)
                      : -1;
            }
            if (ret == 0)  {
                ret = (this.value2 != null)
                      ? ((_range.value2 != null) ? this.value2.compareTo(_range.value2) : 0)
                      : 1;
            }
            return ret;
        }
    }
}
