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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to handle the export / import of administration objects
 * depending on attributes.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractDMWithAttributes_mxJPO
    extends AbstractDMWithTriggers_mxJPO
{
    /**
     * Name of parameter to define ignored attributes within the test
     * attributes procedure for interfaces.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_IGNORE_INTERFACE = "DMWithAttrIgnoreIntAttr";

    /**
     * Name of parameter to define ignored attributes within the test
     * attributes procedure for relationship.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_IGNORE_RELATIONSHIP = "DMWithAttrIgnoreRelAttr";

    /**
     * Name of parameter to define ignored attributes within the test
     * attributes procedure for types.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_IGNORE_TYPE = "DMWithAttrIgnoreTypeAttr";

    /**
     * Name of parameter to define removed attributes within the test
     * attributes procedure for interfaces.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_REMOVE_INTERFACE = "DMWithAttrRemoveIntAttr";

    /**
     * Name of parameter to define removed attributes within the test
     * attributes procedure for relationship.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_REMOVE_RELATIONSHIP = "DMWithAttrRemoveRelAttr";

    /**
     * Name of parameter to define removed attributes within the test
     * attributes procedure for types.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_REMOVE_TYPE = "DMWithAttrRemoveTypeAttr";

    /**
     * Key used to identify the update of a data model object with attributes
     * within {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     * @see #TCL_PROCEDURE
     */
    private static final String JPO_CALLER_KEY = "attributes";

    /**
     * Called TCL procedure within the TCL update to assign attributes to this
     * administration object. The given values of the arguments are converted
     * so that the values are handled correctly from TCL and MQL.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #jpoCallExecuteConvert(String)
     */
    private static final String TCL_PROCEDURE
            = "proc testAttributes {args}  {\n"
                + "set iIdx 0\n"
                + "set lsCmd [list mql exec prog org.mxupdate.update.util.JPOCaller " + AbstractDMWithAttributes_mxJPO.JPO_CALLER_KEY + "]\n"
                + "while {$iIdx < [llength $args]}  {\n"
                +     "set sValue [lindex $args $iIdx]\n"
                +     "if {($iIdx > 0) && ([lindex $args [expr $iIdx - 1]] == \"-attributes\")}  {\n"
                +         "set sCmd \"\"\n"
                +         "foreach sOneValue $sValue  {\n"
                +             "regsub -all {@} \"${sOneValue}\" \"@@\" sOneValue\n"
                +             "regsub -all { } \"${sOneValue}\" \"@20\" sOneValue\n"
                +             "regsub -all {\\\"} \"${sOneValue}\" \"@22\" sOneValue\n"
                +             "regsub -all {'} \"${sOneValue}\" \"@27\" sOneValue\n"
                +             "regsub -all \"\\{\" \"${sOneValue}\" \"@7B\" sOneValue\n"
                +             "regsub -all \"\\}\" \"${sOneValue}\" \"@7D\" sOneValue\n"
                +             "set sCmd \"${sCmd} ${sOneValue}\"\n"
                +         "}\n"
                +         "lappend lsCmd ${sCmd}\n"
                +     "} else  {\n"
                +         "regsub -all {@} \"${sValue}\" \"@@\" sValue\n"
                +         "regsub -all { } \"${sValue}\" \"@20\" sValue\n"
                +         "regsub -all {\\\"} \"${sValue}\" \"@22\" sValue\n"
                +         "regsub -all {'} \"${sValue}\" \"@27\" sValue\n"
                +         "regsub -all \"\\{\" \"${sValue}\" \"@7B\" sValue\n"
                +         "regsub -all \"\\}\" \"${sValue}\" \"@7D\" sValue\n"
                +         "lappend lsCmd ${sValue}\n"
                +     "}\n"
                +     "incr iIdx\n"
                + "}\n"
                + "eval $lsCmd\n"
            + "}\n";

    /**
     * Set of all ignored URLs from the XML definition for data model objects
     * with attributes.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractDMWithAttributes_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
    }

    /**
     * List of all attributes for this data model administration object.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private final Set<String> attributes = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public AbstractDMWithAttributes_mxJPO(final TypeDef_mxJPO _typeDef,
                                          final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Checks if the URL to parse defined an attribute and appends this
     * name of attribute to the list of attributes {@link #attributes}.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #attributes
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (AbstractDMWithAttributes_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.attributes.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Appends at the end of the TCL update file the call to the
     * {@link #TCL_PROCEDURE} to append missing attributes to the
     * administration object.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #attributes
     */
    @Override()
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
        _out.append("\n\ntestAttributes -").append(this.getTypeDef().getMxAdminName())
            .append(" \"${NAME}\" -attributes [list \\\n");
        for (final String attr : this.attributes)  {
            _out.append("    \"").append(StringUtil_mxJPO.convertTcl(attr)).append("\" \\\n");
        }
        _out.append("]");
    }

    /**
     * Adds the TCL procedure {@link #TCL_PROCEDURE} so that attributes could
     * be assigned to this administration object. The instance itself
     * is stored as encoded string in the TCL variable
     * <code>JPO_CALLER_INSTANCE</code>.
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map with TCL variables
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     * @see #TCL_PROCEDURE
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
        final StringBuilder tclCode = new StringBuilder()
                .append(AbstractDMWithAttributes_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, _preMQLCode, _postMQLCode, tclCode, _tclVariables, _sourceFile);
    }

    /**
     * The method is called within the update of an administration object. The
     * method is called directly within the update and checks which attributes
     * are missed in the new definition and adds missing attributes to the
     * administration object. If an attribute is not defined anymore but
     * assigned in MX, an exception is thrown.
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments from the TCL procedure
     * @throws Exception if an unknown parameter is defined, the given name of
     *                   the administration object is not the same or an
     *                   attribute is assigned to an administration object
     *                   within MX but not defined anymore
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        if ((_args.length == 0) || !AbstractDMWithAttributes_mxJPO.JPO_CALLER_KEY.equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else  {
            int idx = 1;
            String name = null;
            String attrStr = null;

            final Set<String> ignoreAllAttrs = new HashSet<String>();
            final Set<String> removeAllAttrs = new HashSet<String>();
            final Set<String> ignoreAttrs = new HashSet<String>();
            final Set<String> removeAttrs = new HashSet<String>();
            while (idx < _args.length)  {
                final String arg = _args[idx];
                if ("-attributes".equals(arg))  {
                    attrStr = _args[++idx];
                } else if ("-ignoreattr".equals(arg))  {
                    ignoreAttrs.add(this.jpoCallExecuteConvert(_args[++idx]));
                } else if ("-interface".equals(arg) && this.getTypeDef().getMxAdminName().equals("interface"))  {
                    name = this.jpoCallExecuteConvert(_args[++idx]);
                    final Collection<String> tmp1 = _paramCache.getValueList(AbstractDMWithAttributes_mxJPO.PARAM_IGNORE_INTERFACE);
                    if (tmp1 != null)  {
                        ignoreAllAttrs.addAll(tmp1);
                    }
                    final Collection<String> tmp2 = _paramCache.getValueList(AbstractDMWithAttributes_mxJPO.PARAM_REMOVE_INTERFACE);
                    if (tmp2 != null)  {
                        removeAllAttrs.addAll(tmp2);
                    }
                } else if ("-relationship".equals(arg) && this.getTypeDef().getMxAdminName().equals("relationship"))  {
                    name = this.jpoCallExecuteConvert(_args[++idx]);
                    final Collection<String> tmp1 = _paramCache.getValueList(AbstractDMWithAttributes_mxJPO.PARAM_IGNORE_RELATIONSHIP);
                    if (tmp1 != null)  {
                        ignoreAllAttrs.addAll(tmp1);
                    }
                    final Collection<String> tmp2 = _paramCache.getValueList(AbstractDMWithAttributes_mxJPO.PARAM_REMOVE_RELATIONSHIP);
                    if (tmp2 != null)  {
                        removeAllAttrs.addAll(tmp2);
                    }
                } else if ("-removeattr".equals(arg))  {
                    removeAttrs.add(this.jpoCallExecuteConvert(_args[++idx]));
                } else if ("-type".equals(arg) && this.getTypeDef().getMxAdminName().equals("type"))  {
                    name = this.jpoCallExecuteConvert(_args[++idx]);
                    final Collection<String> tmp1 = _paramCache.getValueList(AbstractDMWithAttributes_mxJPO.PARAM_IGNORE_TYPE);
                    if (tmp1 != null)  {
                        ignoreAllAttrs.addAll(tmp1);
                    }
                    final Collection<String> tmp2 = _paramCache.getValueList(AbstractDMWithAttributes_mxJPO.PARAM_REMOVE_TYPE);
                    if (tmp2 != null)  {
                        removeAllAttrs.addAll(tmp2);
                    }
                } else  {
                    throw new UpdateException_mxJPO(
                            UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_UKNOWN_PARAMETER,
                            arg);
                }
                idx++;
            }

            // check for equal administration name
            if (!this.getName().equals(name))  {
                throw new UpdateException_mxJPO(
                        UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_WRONG_OBJECT,
                        this.getTypeDef().getLogging(),
                        this.getName(),
                        name);
            }

            // extract all attributes
            final Set<String> newAttrs = new TreeSet<String>();
            if (attrStr != null)  {
                for (final String attrName : attrStr.split(" "))  {
                    if (!"".equals(attrName))  {
                        newAttrs.add(this.jpoCallExecuteConvert(attrName));
                    }
                }
            }

            // and update updates
            ignoreAllAttrs.addAll(ignoreAttrs);
            removeAllAttrs.addAll(removeAttrs);
            this.updateAttributes(_paramCache, newAttrs, ignoreAllAttrs, removeAllAttrs);
        }
    }

    /**
     * Updates this administration object with given attributes.
     *
     * @param _paramCache   parameter cache
     * @param _newAttrs     set of attributes which must be assigned
     * @param _ignoreAttrs  set of attributes which are ignored (if assigned)
     * @param _removeAttrs  set of attributes which could be removed (if
     *                      assigned)
     * @throws Exception if update failed or attribute could not be removed
     */
    protected void updateAttributes(final ParameterCache_mxJPO _paramCache,
                                    final Set<String> _newAttrs,
                                    final Set<String> _ignoreAttrs,
                                    final Set<String> _removeAttrs)
        throws Exception
    {
        // now check for not defined but existing attributes
        for (final String attr : this.attributes)  {
            if (!_newAttrs.contains(attr))  {
                boolean ignore = false;
                for (final String ignoreAttr : _ignoreAttrs)  {
                    if (StringUtil_mxJPO.match(attr, ignoreAttr))  {
                        ignore = true;
                        _paramCache.logDebug("    - attribute '" + attr + "' is not defined but will be ignored");
                        break;
                    }
                }
                if (!ignore)  {
                    boolean remove = false;
                    for (final String removeAttr : _removeAttrs)  {
                        if (StringUtil_mxJPO.match(attr, removeAttr))  {
                            remove = true;
                            _paramCache.logDebug("    - attribute '" + attr + "' is not defined and will be removed");
                            MqlUtil_mxJPO.execMql(_paramCache,
                                    new StringBuilder()
                                        .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                                        .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\"")
                                        .append(" remove attribute \"")
                                                .append(StringUtil_mxJPO.convertMql(attr)).append("\""));
                            break;
                        }
                    }
                    if (!remove)  {
                        throw new UpdateException_mxJPO(
                                UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_ATTRIBUTE_REMOVED,
                                attr,
                                this.getTypeDef().getLogging(),
                                this.getName());
                    }
                }
            }
        }

        // and check for not existing attributes but needed
        for (final String attr : _newAttrs)  {
            if (!this.attributes.contains(attr))  {
                _paramCache.logDebug("    - add attribute '" + attr + "'");
                MqlUtil_mxJPO.execMql(_paramCache,
                        new StringBuilder()
                            .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                            .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\"")
                            .append(" add attribute \"").append(StringUtil_mxJPO.convertMql(attr)).append("\""));
            }
        }
    }

    /**
     * Converts given string by replacing escaped characters.
     *
     * @param _value    value to convert
     * @return replaced value from the converted value used for the JPO call
     * @see #TCL_PROCEDURE
     */
    protected String jpoCallExecuteConvert(final String _value)
    {
        return _value.replaceAll("@20", " ")
                     .replaceAll("@22", "\\\"")
                     .replaceAll("@27", "'")
                     .replaceAll("@7B", "{")
                     .replaceAll("@7D", "}")
                     .replaceAll("@@", "@");
    }
}
