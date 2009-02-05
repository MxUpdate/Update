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
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.JPOCaller_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * Data model interface class.
 *
 * @author Tim Moxter
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
    private final static String TCL_PROCEDURE
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
     * From which interfaces is this interface derived?
     *
     * @see #parse(String, String)
     * @see #write(Writer)
     */
    private final Set<String> derived = new TreeSet<String>();

    /**
     * Is the interface abstract?
     *
     * @see #parse(String, String)
     * @see #write(Writer)
     */
    private boolean abstractFlag = false;

    /**
     * Are all types allowed for this interface?
     *
     * @see #parse(String, String)
     * @see #write(Writer)
     */
    private boolean typesAll = false;

    /**
     * Information about all allowed types for this interface.
     *
     * @see #parse(String, String)
     * @see #write(Writer)
     */
    private final Set<String> types = new TreeSet<String>();

    /**
     * Constructor used to initialize the interface class instance.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public Interface_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Parses the interface specific XML export URL.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #abstractFlag
     * @see #typesAll
     * @see #types
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/abstract".equals(_url))  {
            this.abstractFlag = true;

        } else if ("/allowAllTypes".equals(_url))  {
            this.typesAll = true;

        } else if ("/derivedFromInterface".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFromInterface/interfaceTypeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFromInterface/interfaceTypeRefList/interfaceTypeRef".equals(_url))  {
            this.derived.add(_content);

        } else if ("/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/typeRefList/typeRef".equals(_url))  {
            this.types.add(_content);

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes the interface specific information into the TCL update file. The
     * interface specific information are:
     * <ul>
     * <li>abstract information {@link #abstractFlag}</li>
     * <li>type information (all types define with {@link #typesAll} or all
     *     types {@link #types})</li>
     * </ul>
     *
     * @param _out      writter instance to the TCL update file
     * @throws IOException if the interface specific information could not be
     *                     written
     * @see #abstractFlag
     * @see #typesAll
     * @see #types
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        // write abstract information
        _out.append(" \\\n    abstract ").append(Boolean.toString(this.abstractFlag));

        // type information
        if (this.typesAll)  {
            _out.append(" \\\n    add type all");
        } else  {
            for (final String type : this.types)  {
                _out.append(" \\\n    add type \"").append(convertTcl(type)).append('\"');
            }
        }

    }

    /**
     * Appends at the end of the TCL update file the call to the
     * {@link #TCL_PROCEDURE} to define the parent interfaces for this
     * interface.
     *
     * @param _out      appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #derived
     */
    @Override
    protected void writeEnd(final Appendable _out)
            throws IOException
    {
        _out.append("\n\ntestParents -").append(this.getTypeDef().getMxAdminName())
            .append(" \"${NAME}\" -parents [list \\\n");
        for (final String parent : this.derived)  {
            _out.append("    \"").append(convertTcl(parent)).append("\" \\\n");
        }
        _out.append("]");

        super.writeEnd(_out);
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
        JPOCaller_mxJPO.defineInstance(_paramCache, this);

        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" !hidden description \"\"");

        // type information
        if (this.typesAll)  {
            preMQLCode.append(" remove type all");
        } else  {
            for (final String type : this.types)  {
                preMQLCode.append(" remove type \"").append(type).append('\"');
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        // add TCL code for the procedure
        final StringBuilder preTclCode = new StringBuilder()
                .append(TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, preTclCode, _tclVariables, _sourceFile);

        JPOCaller_mxJPO.undefineInstance(_paramCache, this);
    }

    /**
     * The method is called within the update of an interface object. The
     * method is called directly within the update and checks which parent
     * interfaces are missed in the new definition and adds missing parent
     * interfaces to the interface object. If an interface is not defined
     * anymore but assigned in MX, an exception is thrown.
     * If the first argument is not &quot;parents&quot; method
     * {@link AbstractDMWithAttributes_mxJPO#jpoCallExecute(ParameterCache_mxJPO, String...)
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
                        System.out.println("    - add to parent '" + newParent + "'");
                    }
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append(newParent);
                }
                cmd.append('\"');
                execMql(_paramCache.getContext(), cmd);
            }
        }
    }
}
