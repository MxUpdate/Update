/*
 * Copyright 2008-2009 The MxUpdate Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * Data model interface class.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Interface_mxJPO
        extends AbstractDMWithAttributes_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 7025932171586411284L;

    /**
     * Called TCL procedure within the TCL update to assign parent interface to
     * this interface object. The first argument of the JPO caller is
     * &quot;parents&quot; to differ between an update for parent interface or
     * for attributes.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String TCL_PROCEDURE
            = "proc testParents {args}  {\n"
                + "set iIdx 0\n"
                + "set lsCmd [list mql exec prog org.mxupdate.update.util.JPOCaller parents]\n"
                + "while {$iIdx < [llength $args]}  {\n"
                +   "lappend lsCmd [lindex $args $iIdx]\n"
                +   "incr iIdx\n"
                + "}\n"
                + "eval $lsCmd\n"
            + "}\n";

    /**
     * Set of all ignored URLs from the XML definition for interfaces.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Interface_mxJPO.IGNORED_URLS.add("/derivedFromInterface");
        Interface_mxJPO.IGNORED_URLS.add("/derivedFromInterface/interfaceTypeRefList");
        Interface_mxJPO.IGNORED_URLS.add("/relationshipDefRefList");
        Interface_mxJPO.IGNORED_URLS.add("/typeRefList");
    }

    /**
     * From which interfaces is this interface derived?
     *
     * @see #parse(String, String)
     * @see #writeEnd(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> derived = new TreeSet<String>();

    /**
     * Is the interface abstract?
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private boolean abstractFlag;

    /**
     * Are all types allowed for this interface?
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private boolean allTypes;

    /**
     * Information about all allowed types for this interface.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Set<String> types = new TreeSet<String>();

    /**
     * Are all relationships allowed for this interface?
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private boolean allRelationships;

    /**
     * Information about all allowed relationships for this interface.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final Set<String> relationships = new TreeSet<String>();

    /**
     * Constructor used to initialize the interface class instance.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the interface object
     */
    public Interface_mxJPO(final TypeDef_mxJPO _typeDef,
                           final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses the interface specific XML export URL.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #abstractFlag
     * @see #allTypes
     * @see #types
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if (!Interface_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/abstract".equals(_url))  {
                this.abstractFlag = true;
            } else if ("/allowAllRelationships".equals(_url))  {
                this.allRelationships = true;
            } else if ("/allowAllTypes".equals(_url))  {
                this.allTypes = true;
            } else if ("/derivedFromInterface/interfaceTypeRefList/interfaceTypeRef".equals(_url))  {
                this.derived.add(_content);
            } else if ("/relationshipDefRefList/relationshipDefRef".equals(_url))  {
                this.relationships.add(_content);
            } else if ("/typeRefList/typeRef".equals(_url))  {
                this.types.add(_content);
            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * Writes the interface specific information into the TCL update file. The
     * interface specific information are:
     * <ul>
     * <li>abstract information {@link #abstractFlag}</li>
     * <li>relationship information (all relationships defined with
     *     {@link #allRelationships} or some relationships defined with
     *     {@link #relationships})</li>
     * <li>type information (all types defined with {@link #allTypes} or some
     *     types defined with {@link #types})</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the interface specific information could not be
     *                     written
     * @see #abstractFlag
     * @see #allRelationships
     * @see #relationships
     * @see #allTypes
     * @see #types
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        // write abstract information
        _out.append(" \\\n    abstract ").append(Boolean.toString(this.abstractFlag));

        // relationship information
        if (this.allRelationships)  {
            _out.append(" \\\n    add relationship all");
        } else  {
            for (final String relationship : this.relationships)  {
                _out.append(" \\\n    add relationship \"")
                    .append(StringUtil_mxJPO.convertTcl(relationship)).append('\"');
            }
        }

        // type information
        if (this.allTypes)  {
            _out.append(" \\\n    add type all");
        } else  {
            for (final String type : this.types)  {
                _out.append(" \\\n    add type \"").append(StringUtil_mxJPO.convertTcl(type)).append('\"');
            }
        }
    }

    /**
     * Appends at the end of the TCL update file the call to the
     * {@link #TCL_PROCEDURE} to define the parent interfaces for this
     * interface.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #derived
     */
    @Override
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
            throws IOException
    {
        _out.append("\n\ntestParents -").append(this.getTypeDef().getMxAdminName())
            .append(" \"${NAME}\" -parents [list \\\n");
        for (final String parent : this.derived)  {
            _out.append("    \"").append(StringUtil_mxJPO.convertTcl(parent)).append("\" \\\n");
        }
        _out.append("]");

        super.writeEnd(_paramCache, _out);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this interface and to append
     * the tcl procedure {@link #TCL_PROCEDURE}. This information is reset:
     * <ul>
     * <li>set not hidden</li>
     * <li>reset description</li>
     * <li>remove all types</li>
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
     * @see #TCL_PROCEDURE
     */
    @Override
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" !hidden description \"\"");

        // relationship information
        if (this.allRelationships)  {
            preMQLCode.append(" remove relationship all");
        } else  {
            for (final String relationship : this.relationships)  {
                preMQLCode.append(" remove relationship \"")
                          .append(StringUtil_mxJPO.convertMql(relationship)).append('\"');
            }
        }

        // type information
        if (this.allTypes)  {
            preMQLCode.append(" remove type all");
        } else  {
            for (final String type : this.types)  {
                preMQLCode.append(" remove type \"").append(StringUtil_mxJPO.convertMql(type)).append('\"');
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // add TCL code for the procedure
        final StringBuilder preTclCode = new StringBuilder()
                .append(Interface_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, preTclCode, _tclVariables, _sourceFile);
    }

    /**
     * The method is called within the update of an interface object. The
     * method is called directly within the update and checks which parent
     * interfaces are missed in the new definition and adds missing parent
     * interfaces to the interface object. If an interface is not defined
     * anymore but assigned in MX, an exception is thrown.
     * If the first argument is not &quot;parents&quot; method
     * {@link AbstractDMWithAttributes_mxJPO#jpoCallExecute(ParameterCache_mxJPO, String...)}
     * is called.
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments from the TCL procedure
     * @throws Exception if an unknown parameter is defined, the given name of
     *                   the administration object is not the same or an
     *                   interface is assigned to the interface object
     *                   within MX but not defined anymore
     */
    @Override
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
            throws Exception
    {
        if (!"parents".equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else  {
            // evaluate parameters
            final String nameParam = new StringBuilder()
                    .append('-').append(this.getTypeDef().getMxAdminName()).toString();
            int idx = 1;
            String name = null;
            String parentsStr = null;
            while (idx < _args.length)  {
                final String arg = _args[idx];
                if (nameParam.equals(arg))  {
                    name = _args[++idx];
                } else if ("-parents".equals(arg))  {
                    parentsStr = _args[++idx];
                } else  {
                    throw new Exception("unknown parameter \"" + arg + "\"");
                }
                idx++;
            }

            // check for equal administration name
            if (!this.getName().equals(name))  {
                throw new Exception(this.getTypeDef().getLogging()
                        + " '" + this.getName() + "' was called to"
                        + " update via update script, but "
                        + this.getTypeDef().getLogging() + " '" + name + "' was"
                        + " called in the procedure...");
            }

            // get all parent interfaces
            final Pattern pattern = Pattern.compile("(\\{[^\\{\\}]*\\} )|([^ \\{\\}]* )");
            final Matcher matcher = pattern.matcher(parentsStr + " ");
            final Set<String> newParents = new TreeSet<String>();
            while (matcher.find())  {
                final String parentName = matcher.group().trim().replaceAll("(^\\{)|(\\}$)", "");
                if (!"".equals(parentName))  {
                    newParents.add(parentName);
                }
            }

            // check if all current parents are within new parents
            for (final String curParent : this.derived)  {
                if (!newParents.contains(curParent))  {
                    throw new Exception("Current parent " + this.getTypeDef().getLogging()
                            + " '" + curParent + "' must be removed from " + this.getTypeDef().getLogging()
                            + " '" + this.getName() + "'. This is not allowed!");
                }
            }

            // and append all not current derived parents
            if (!newParents.isEmpty())  {
                final StringBuilder cmd = new StringBuilder()
                        .append("mod ").append(this.getTypeDef().getMxAdminName())
                        .append(" '").append(this.getName()).append("\' derived \"");
                boolean first = true;
                for (final String newParent : newParents)  {
                    if (!this.derived.contains(newParent))  {
                        _paramCache.logDebug("    - add to parent '" + newParent + "'");
                    }
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append(newParent);
                }
                cmd.append('\"');
                MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd);
            }
        }
    }
}
