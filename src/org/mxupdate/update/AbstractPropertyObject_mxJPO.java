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

package org.mxupdate.update;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.JPOCaller_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * @author Tim Moxter
 * @version $Id$
 */
public abstract class AbstractPropertyObject_mxJPO
        extends AbstractObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2794355865894159489L;

    /**
     * String of the key within the parameter cache for the export application
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTAPPLICATION = "ExportApplication";

    /**
     * String of the key within the parameter cache for the export author
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTAUTHOR = "ExportAuthor";

    /**
     * String of the key within the parameter cache for the export installer
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTINSTALLER = "ExportInstaller";

    /**
     * String of the key within the parameter cache for the export original
     * name parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTORIGINALNAME = "ExportOriginalName";

    /**
     * String of the key within the parameter cache for the export version
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTVERSION = "ExportVersion";

    /**
     * Header string of the application.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_APPLICATION = "\n# APPLICATION:\n# ~~~~~~~~~~~~\n#";

    /**
     * Header string of the author.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_AUTHOR = "\n# AUTHOR:\n# ~~~~~~~\n#";

    /**
     * Header string of the installer name.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_INSTALLER = "\n# INSTALLER:\n# ~~~~~~~~~~\n#";

    /**
     * Header string of the original name.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_ORIGINALNAME = "\n# ORIGINAL NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the symbolic name.
     *
     * @see #writeHeader(Writer)
     * @see #defineSymbolicName(Map, StringBuilder)
     */
    private static final String HEADER_SYMBOLIC_NAME = "\n# SYMBOLIC NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the version.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_VERSION = "\n# VERSION:\n# ~~~~~~~~\n#";

    /**
     * Test string used as output to test that the update runs correctly
     * (because it could be that no exception was thrown while an error happens
     * and the result is a rolled back transaction).
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String TEST_EXECUTED = "MxUpdate Executed";

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractPropertyObject_mxJPO(final TypeDef_mxJPO _typeDef,
                                           final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException if the header could not be written to the TCL update
     *                     file
     * @see #HEADER_APPLICATION
     * @see #HEADER_AUTHOR
     * @see #HEADER_INSTALLER
     * @see #HEADER_ORIGINALNAME
     * @see #HEADER_SYMBOLIC_NAME
     * @see #HEADER_VERSION
     * @see #PARAM_EXPORTAPPLICATION
     * @see #PARAM_EXPORTAUTHOR
     * @see #PARAM_EXPORTINSTALLER
     * @see #PARAM_EXPORTORIGINALNAME
     * @see #PARAM_EXPORTVERSION
     */
    protected void writeHeader(final ParameterCache_mxJPO _paramCache,
                               final Writer _out)
            throws IOException
    {
        final String headerText = this.getTypeDef().getTitle();
        _out.append("################################################################################\n")
            .append("# ").append(headerText).append(":\n")
            .append("# ~");
        for (int i = 0; i < headerText.length(); i++)  {
            _out.append("~");
        }
        _out.append("\n")
            .append("# ").append(getName()).append("\n");
        // symbolic name only if an administration type is defined
        if (this.getTypeDef().getMxAdminName() != null)  {
            _out.append('#').append(HEADER_SYMBOLIC_NAME).append(' ');
            if (this.getSymblicNames().isEmpty())  {
                final StringBuilder symbName = new StringBuilder()
                        .append(this.getTypeDef().getMxAdminName())
                        .append("_").append(this.getName().replaceAll(" ", "").replaceAll("/", ""));
                _paramCache.logWarning("    - No symbolic name defined for '" + this.getName()
                        + "'! Symbolic name '" + symbName + "' will be used!");
                _out.append(symbName);
            } else if (this.getSymblicNames().size() > 1)  {
                final String symbName = this.getSymblicNames().iterator().next();
                _paramCache.logError("    - Found " + this.getSymblicNames().size()
                        + " symbolic names! Only one is allowed! '"
                        + symbName + "' will be used!");
                _paramCache.logTrace("    - Following symbolic names found:");
                for (final String origSymb : this.getSymblicNames())  {
                    _paramCache.logTrace("      * '" + origSymb + "'");
                }
                _out.append(symbName);
            } else  {
                _out.append(this.getSymblicNames().iterator().next());
            }
            _out.append('\n');
        }
        // original name
        // (only if an administration type and related parameter is defined)
        if ((this.getTypeDef().getMxAdminName() != null) && _paramCache.getValueBoolean(PARAM_EXPORTORIGINALNAME))  {
            _out.append('#').append(HEADER_ORIGINALNAME);
            if ((this.getOriginalName() != null) && !"".equals(this.getOriginalName()))  {
                _out.append(" ").append(this.getOriginalName()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // description
        _out.append("#\n# DESCRIPTION:\n")
            .append("# ~~~~~~~~~~~~\n");
        if ((this.getDescription() != null) && !"".equals(this.getDescription()))  {
            for (final String partDesc : this.getDescription().split("\n"))  {
                _out.append('#');
                int length = 0;
                for (final String desc : partDesc.split(" "))  {
                    if (!"".equals(desc))  {
                        length += desc.length() + 1;
                        if (length > 79)  {
                            _out.append("\n#");
                            length = desc.length() + 1;
                        }
                        _out.append(' ').append(desc);
                    }
                }
                _out.append("\n");
            }
        } else  {
            _out.append("#\n");
        }
        // write header
        if (_paramCache.getValueBoolean(PARAM_EXPORTAUTHOR))  {
            _out.append('#').append(HEADER_AUTHOR);
            if ((this.getAuthor() != null) && !"".equals(this.getAuthor()))  {
                _out.append(" ").append(this.getAuthor()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write installer
        if (_paramCache.getValueBoolean(PARAM_EXPORTINSTALLER))  {
            _out.append('#').append(HEADER_INSTALLER);
            if ((this.getInstaller() != null) && !"".equals(this.getInstaller()))  {
                _out.append(" ").append(this.getInstaller()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write application
        if (_paramCache.getValueBoolean(PARAM_EXPORTAPPLICATION))  {
            _out.append('#').append(HEADER_APPLICATION);
            if ((this.getApplication() != null) && !"".equals(this.getApplication()))  {
                _out.append(" ").append(this.getApplication()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write version
        if (_paramCache.getValueBoolean(PARAM_EXPORTVERSION))  {
            _out.append('#').append(HEADER_VERSION);
            if ((this.getVersion() != null) && !"".equals(this.getVersion()))  {
                _out.append(" ").append(this.getVersion()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        _out.append("################################################################################\n\n");
    }


    /**
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     * {@link #update(Context, CharSequence, CharSequence, Map)}.
     *
     * @param _paramCache       parameter cache
     * @param _file             file with TCL update code
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if the update from the derived class failed
     * @see #update(Context, CharSequence, CharSequence, Map)
     * @see #extractFromCode(StringBuilder, String, String)
     * @see #extractSymbolicNameFromCode(ParameterCache_mxJPO,StringBuilder)
     * @see #HEADER_APPLICATION
     * @see #HEADER_AUTHOR
     * @see #HEADER_INSTALLER
     * @see #HEADER_ORIGINALNAME
     * @see #HEADER_VERSION
     */
    @Override
    public void update(final ParameterCache_mxJPO _paramCache,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        // parse objects
        this.parse(_paramCache);

        // read code
        final StringBuilder code = this.getCode(_file);

        // defines the version in the TCL variables
        final Map<String,String> tclVariables = new HashMap<String,String>();
        if (_newVersion == null)  {
            tclVariables.put(AdminPropertyDef.VERSION.name(),
                              this.extractFromCode(code, HEADER_VERSION, ""));
        } else  {
            tclVariables.put(AdminPropertyDef.VERSION.name(), _newVersion);
        }

        // define author
        final String author;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_AUTHOR))  {
            author = _paramCache.getValueString(ParameterCache_mxJPO.KEY_AUTHOR);
        } else  {
            author = this.extractFromCode(code,
                                          HEADER_AUTHOR,
                                          _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAUTHOR));
        }
        tclVariables.put(AdminPropertyDef.AUTHOR.name(), author);

        // define application
        final String appl;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_APPLICATION))  {
            appl = _paramCache.getValueString(ParameterCache_mxJPO.KEY_APPLICATION);
        } else  {
            appl = this.extractFromCode(code,
                                        HEADER_APPLICATION,
                                        _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAPPLICATION));
        }
        tclVariables.put(AdminPropertyDef.APPLICATION.name(), appl);

        // define installer
        final String installer;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_INSTALLER))  {
            installer = _paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLER);
        } else  {
            installer = this.extractFromCode(code,
                                             HEADER_INSTALLER,
                                             _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTINSTALLER));
        }
        tclVariables.put(AdminPropertyDef.INSTALLER.name(), installer);

        if (this.getTypeDef().getMxAdminName() != null)  {
            // define symbolic name
            tclVariables.put("SYMBOLICNAME", this.extractSymbolicNameFromCode(_paramCache, code));

            // define original name
            final String origName;
            if ((this.getOriginalName() != null) && !"".equals(this.getOriginalName()))  {
                origName = this.getOriginalName();
            } else  {
                origName = this.getName();
            }
            tclVariables.put(AdminPropertyDef.ORIGINALNAME.name(), origName);
        }

        // define file date
        final DateFormat format = new SimpleDateFormat(_paramCache.getValueString(ParameterCache_mxJPO.KEY_FILEDATEFORMAT));
        tclVariables.put(AdminPropertyDef.FILEDATE.name(),
                         format.format(new Date(_file.lastModified())));

        this.update(_paramCache, "", "", "", tclVariables, _file);
    }

    /**
     * The method is called from the JPO caller interface. In this abstract
     * class, the definition is only a stub and not used, because otherwise
     * the method must be defined everywhere.
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments, not used
     * @throws Exception never, only dummy
     */
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
            throws Exception
    {
    }

    /**
     * Extracts for given header text the related value from the source code.
     * If no value in the update file is defined, the default value from is
     * used.
     *
     * @param _code             TCL update source code
     * @param _headerText       text used to identify the value
     * @param _default          default value if not defined within header
     * @return extracted string from source code
     */
    protected String extractFromCode(final StringBuilder _code,
                                     final String _headerText,
                                     final String _default)
    {
        final int length = _headerText.length();
        final int start = _code.indexOf(_headerText) + length;
        final String value;
        if ((start > length) && (_code.charAt(start) == ' '))  {
            final int end = _code.indexOf("\n", start);
            if (end > 0)  {
                final String tmp = _code.substring(start, end).trim();
                if ("".equals(tmp))  {
                    value = _default;
                } else  {
                    value = tmp;
                }
            } else  {
                value = _default;
            }
        } else  {
            value = _default;
        }
        return value;
    }

    /**
     *
     * @param _paramCache   parameter cache
     * @param _code         TCL update source code
     * @return extracted symbolic name from the source code header
     */
    protected String extractSymbolicNameFromCode(final ParameterCache_mxJPO _paramCache,
                                                 final StringBuilder _code)
    {
        String codeSymbName = null;

        if (this.getTypeDef().getMxAdminName() != null)  {
            final String symbName = new StringBuilder().append(this.getTypeDef().getMxAdminName())
                                    .append("_")
                                    .append(this.getName().replaceAll(" ", "").replaceAll("/", ""))
                                    .toString();
            codeSymbName = this.extractFromCode(_code, HEADER_SYMBOLIC_NAME, null);
            if (codeSymbName == null)  {
                _paramCache.logError("No symbolic name defined! So '" + symbName + "' will be used.");
                codeSymbName = symbName;
            } else if (!codeSymbName.startsWith(this.getTypeDef().getMxAdminName()))  {
                _paramCache.logError("Symbolic name does not start correctly! So '"
                        + symbName + "' will be used (instead of '" + codeSymbName + "').");
                codeSymbName = symbName;
            } else if (!codeSymbName.equals(symbName))  {
                _paramCache.logWarning("Symbolic name '" + symbName
                        + "' should be used! But defined is '" + codeSymbName + "'.");
            }
        }

        return codeSymbName;
    }

    /**
     * The method updates this administration (business) object. First all MQL
     * commands are concatenated:
     * <ul>
     * <li>pre MQL commands (from parameter <code>_preMQLCode</code>)</li>
     * <li>change to TCL mode</li>
     * <li>set all required TCL variables (from parameter
     *     <code>_tclVariables</code>)</li>
     * <li>append TCL update code from file (from parameter
     *     <code>_tclCode</code>)</li>
     * <li>change back to MQL mode</li>
     * <li>append post MQL statements (from parameter
     *     <code>_postMQLCode</code>)</li>
     * </ul>
     * At the end of the MQL statements, {@link #TEST_EXECUTED} is printed via
     * MQL command &quot;<code>output</code> so that this output is returned
     * from the MQL execution. The return of the MQL execution is tested if
     * this output is returned. If not an exception is thrown because the
     * update failed.
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
     * @throws Exception if update failed
     */
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder().append(_preMQLCode);

        // append TCL mode
        cmd.append("tcl;\n")
           .append("eval  {\n");

        // define all TCL variables
        for (final Map.Entry<String, String> entry : _tclVariables.entrySet())  {
            cmd.append("set ").append(entry.getKey())
               .append(" \"").append(convertTcl(entry.getValue())).append("\"\n");
        }
        // append TCL code, end of TCL mode and post MQL statements
        // (source with the file must be replace for windows ...)
        cmd.append(_preTCLCode)
           .append("\nsource \"").append(_sourceFile.toString().replaceAll("\\\\", "/")).append("\"")
           .append("\n}\nexit;\n")
           .append(_postMQLCode)
           .append("output '';output '").append(TEST_EXECUTED).append("';");

        // execute update
        JPOCaller_mxJPO.defineInstance(_paramCache, this);
        try  {
            final String[] ret = execMql(_paramCache.getContext(), cmd).split("\n");
            if (!TEST_EXECUTED.equals(ret[ret.length - 1]))  {
                throw new Exception("Execution of the update was not complete! Update Failed!");
            }
        } finally  {
            JPOCaller_mxJPO.undefineInstance(_paramCache);
        }
    }
}