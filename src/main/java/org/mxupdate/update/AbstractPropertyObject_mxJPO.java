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

package org.mxupdate.update;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.JPOCaller_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * @author The MxUpdate Team
 */
public abstract class AbstractPropertyObject_mxJPO
    extends AbstractObject_mxJPO
{
    /**
     * String of the key within the parmater cache to define that symbolic
     * names must be always calculated.
     *
     * @see #extractSymbolicNameFromCode(ParameterCache_mxJPO, StringBuilder)
     */
    private static final String PARAM_CALCSYMBOLICNAMES = "CalcSymbolicNames";

    /**
     * String of the key within the parameter cache for the export application
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_EXPORTAPPLICATION = "ExportApplication";

    /**
     * String of the key within the parameter cache for the export author
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_EXPORTAUTHOR = "ExportAuthor";

    /**
     * String of the key within the parameter cache for the export installer
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_EXPORTINSTALLER = "ExportInstaller";

    /**
     * String of the key within the parameter cache for the export original
     * name parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_EXPORTORIGINALNAME = "ExportOriginalName";

    /**
     * String of the key within the parameter cache for the export version
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     */
    private static final String PARAM_EXPORTVERSION = "ExportVersion";

    /**
     * Name of the key within the parameter cache that the update continues
     * if a error was thrown.
     */
    private static final String PARAM_CONTINUEONERROR = "ContinueOnError";

    /**
     * Header string of the application.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String HEADER_APPLICATION = "\n# APPLICATION:\n# ~~~~~~~~~~~~\n#";

    /**
     * Header string of the author.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String HEADER_AUTHOR = "\n# AUTHOR:\n# ~~~~~~~\n#";

    /**
     * Header string of the installer name.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String HEADER_INSTALLER = "\n# INSTALLER:\n# ~~~~~~~~~~\n#";

    /**
     * Header string of the original name.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String HEADER_ORIGINALNAME = "\n# ORIGINAL NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the symbolic name.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     * @see #extractSymbolicNameFromCode(ParameterCache_mxJPO, StringBuilder)
     */
    private static final String HEADER_SYMBOLIC_NAME = "\n# SYMBOLIC NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the version.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Appendable)
     * @see #update(ParameterCache_mxJPO, File, String)
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
     * Defines the TCL procedures for logging purposes which are executed by
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String TCL_LOG_PROCS
            = "proc puts {_sText}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller logDebug ${_sText}\n"
            + "}\n"
            + "proc logError {_sText}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller logError ${_sText}\n"
            + "}\n"
            + "proc logWarning {_sText}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller logWarning ${_sText}\n"
            + "}\n"
            + "proc logInfo {_sText}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller logInfo ${_sText}\n"
            + "}\n"
            + "proc logDebug {_sText}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller logDebug ${_sText}\n"
            + "}\n"
            + "proc logTrace {_sText}  {\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller logTrace ${_sText}\n"
            + "}\n";

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
                               final Appendable _out)
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
            .append("# ").append(this.getName()).append("\n");
        // symbolic name only if defined
        if (this.getSymbolicNames() != null)  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_SYMBOLIC_NAME).append(' ');
            if (this.getSymbolicNames().isEmpty())  {
                final String symbName = this.calcDefaultSymbolicName(_paramCache);
                _paramCache.logWarning("    - No symbolic name defined for '" + this.getName()
                        + "'! Symbolic name '" + symbName + "' will be used!");
                _out.append(symbName);
            } else if (this.getSymbolicNames().size() > 1)  {
                final String symbName = this.getSymbolicNames().iterator().next();
                _paramCache.logError("    - Found " + this.getSymbolicNames().size()
                        + " symbolic names! Only one is allowed! '"
                        + symbName + "' will be used!");
                _paramCache.logTrace("    - Following symbolic names found:");
                for (final String origSymb : this.getSymbolicNames())  {
                    _paramCache.logTrace("      * '" + origSymb + "'");
                }
                _out.append(symbName);
            } else  {
                _out.append(this.getSymbolicNames().iterator().next());
            }
            _out.append('\n');
        }
        // original name
        // (only if an administration type and related parameter is defined)
        if ((this.getTypeDef().getMxAdminName() != null)
                && _paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_EXPORTORIGINALNAME))  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_ORIGINALNAME);
            if ((this.getOriginalName() != null) && !"".equals(this.getOriginalName()))  {
                _out.append(" ").append(this.getOriginalName()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // description
        _out.append("#\n# DESCRIPTION:\n")
            .append("# ~~~~~~~~~~~~\n");
        if ((this.getDescription() != null) && !this.getDescription().isEmpty())  {
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
        if (_paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_EXPORTAUTHOR))  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_AUTHOR);
            if ((this.getAuthor() != null) && !this.getAuthor().isEmpty())  {
                _out.append(" ").append(this.getAuthor()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write installer
        if (_paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_EXPORTINSTALLER))  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_INSTALLER);
            if ((this.getInstaller() != null) && !this.getInstaller().isEmpty())  {
                _out.append(" ").append(this.getInstaller()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write application
        if (_paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_EXPORTAPPLICATION))  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_APPLICATION);
            if ((this.getApplication() != null) && !this.getApplication().isEmpty())  {
                _out.append(" ").append(this.getApplication()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write version
        if (_paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_EXPORTVERSION))  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_VERSION);
            if ((this.getVersion() != null) && !this.getVersion().isEmpty())  {
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
     * {@link #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)}.
     *
     * @param _paramCache   parameter cache
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _file         file with TCL update code
     * @param _newVersion   new version which must be set within the update
     *                      (or {@code null} if the version must not be set).
     * @throws Exception if the update from the derived class failed
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #extractFromCode(StringBuilder, String, String)
     * @see #extractSymbolicNameFromCode(ParameterCache_mxJPO,StringBuilder)
     * @see #HEADER_APPLICATION
     * @see #HEADER_AUTHOR
     * @see #HEADER_INSTALLER
     * @see #HEADER_ORIGINALNAME
     * @see #HEADER_VERSION
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
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
            tclVariables.put(PropertyDef_mxJPO.VERSION.name(),
                              this.extractFromCode(code, AbstractPropertyObject_mxJPO.HEADER_VERSION, ""));
        } else  {
            tclVariables.put(PropertyDef_mxJPO.VERSION.name(), _newVersion);
        }

        // define author
        final String author;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_AUTHOR))  {
            author = _paramCache.getValueString(ParameterCache_mxJPO.KEY_AUTHOR);
        } else  {
            author = this.extractFromCode(code,
                                          AbstractPropertyObject_mxJPO.HEADER_AUTHOR,
                                          _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAUTHOR));
        }
        tclVariables.put(PropertyDef_mxJPO.AUTHOR.name(), author);

        // define application
        String appl = null;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_APPLICATION))  {
            appl = _paramCache.getValueString(ParameterCache_mxJPO.KEY_APPLICATION);
        }
        if ((appl == null) || "".equals(appl))  {
            appl = this.extractFromCode(code,
                                        AbstractPropertyObject_mxJPO.HEADER_APPLICATION,
                                        _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAPPLICATION));
        }
        if (appl == null)  {
            appl = "";
        }
        tclVariables.put(PropertyDef_mxJPO.APPLICATION.name(), appl);

        // define installer
        final String installer;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_INSTALLER))  {
            installer = _paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLER);
        } else  {
            installer = this.extractFromCode(code,
                                             AbstractPropertyObject_mxJPO.HEADER_INSTALLER,
                                             _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTINSTALLER));
        }
        tclVariables.put(PropertyDef_mxJPO.INSTALLER.name(), installer);

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
            tclVariables.put(PropertyDef_mxJPO.ORIGINALNAME.name(), origName);
        }

        // define file date
        tclVariables.put(PropertyDef_mxJPO.FILEDATE.name(),
                         StringUtil_mxJPO.formatFileDate(_paramCache, new Date(_file.lastModified())));

        try
        {
            this.update(_paramCache, "", "", "", tclVariables, _file);
        }
        catch (final Exception e)
        {
            final boolean continueOnError = _paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_CONTINUEONERROR);
            if (continueOnError)
            {
                _paramCache.logError(e.toString());
            }
            else
            {
                throw e;
            }
        }
    }

    /**
     * The method is called from the JPO caller interface. In this abstract
     * class the logging TCL procedures are executed and mapped to the logging
     * methods defined in <code>_paramCache</code>
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments, not used
     * @throws Exception never, only dummy
     * @see #TCL_LOG_PROCS
     */
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        if (_args.length == 0)  {
            throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.ABSTRACT_PROPERTY_JPO_CALL_METHOD_NOT_DEFINED);
        } else if ("logDebug".equals(_args[0]))  {
            _paramCache.logDebug(_args[1]);
        } else if ("logError".equals(_args[0]))  {
            _paramCache.logError(_args[1]);
        } else if ("logInfo".equals(_args[0]))  {
            _paramCache.logInfo(_args[1]);
        } else if ("logTrace".equals(_args[0]))  {
            _paramCache.logTrace(_args[1]);
        } else if ("logWarning".equals(_args[0]))  {
            _paramCache.logWarning(_args[1]);
        } else  {
            throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.ABSTRACT_PROPERTY_JPO_CALL_METHOD_UNKNOWN, _args[0]);
        }
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
     * <p>Returns the symbolic name for current property object. If parameter
     * {@link #PARAM_CALCSYMBOLICNAMES} is defined, the symbolic name is only
     * calculated. Otherwise the header is checked if there is a defined
     * symbolic name (and by used if exists). A check is done if the symbolic
     * name starts correctly with the administration type name (in lower
     * case!). If not, the calculated symbolic name is used. A warning is shown
     * if the symbolic name defined in the header is not equal to the
     * calculated symbolic name.</p>
     * <p>The symbolic name is calculated by removing all spaces and slashes
     * within the name of the object and add as prefix the administration type
     * name.</p>
     *
     * @param _paramCache   parameter cache
     * @param _code         TCL update source code
     * @return extracted symbolic name from the source code header
     * @see #PARAM_CALCSYMBOLICNAMES
     */
    private String extractSymbolicNameFromCode(final ParameterCache_mxJPO _paramCache,
                                               final StringBuilder _code)
    {
        String codeSymbName = null;

        if (this.getTypeDef().getMxAdminName() != null)  {
            final String symbName = this.calcDefaultSymbolicName(_paramCache);
            if (_paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_CALCSYMBOLICNAMES))  {
                codeSymbName = symbName;
                _paramCache.logDebug("    - using calculated symbolic name '" + symbName + "'");
            } else  {
                codeSymbName = this.extractFromCode(_code, AbstractPropertyObject_mxJPO.HEADER_SYMBOLIC_NAME, null);
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
        }

        return codeSymbName;
    }

    /**
     * The method updates this administration (business) object. First all MQL
     * commands are concatenated:
     * <ul>
     * <li>pre MQL commands (from parameter <code>_preMQLCode</code>)</li>
     * <li>change to TCL mode</li>
     * <li>define the {@link #TCL_LOG_PROCS TCL logging procedures}</li>
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
     * @param _sourceFile       souce file with the TCL code to update; if
     *                          <code>null</code> file is not called (sourced)
     * @throws Exception if update failed
     * @see #TCL_LOG_PROCS
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
           .append("eval  {\n")
           .append(AbstractPropertyObject_mxJPO.TCL_LOG_PROCS);


        // define all TCL variables
        for (final Map.Entry<String, String> entry : _tclVariables.entrySet())  {
            cmd.append("set ").append(entry.getKey())
               .append(" \"").append(StringUtil_mxJPO.convertTcl(entry.getValue())).append("\"\n");
        }
        // append TCL code, end of TCL mode and post MQL statements
        // (source with the file must be replace for windows ...)
        cmd.append(_preTCLCode);
        if (_sourceFile != null)  {
            cmd.append("\nsource \"").append(_sourceFile.toString().replaceAll("\\\\", "/")).append("\"");
        }
        cmd.append("\n}\nexit;\n")
           .append(_postMQLCode)
           .append("output '';output '").append(AbstractPropertyObject_mxJPO.TEST_EXECUTED).append("';");

        // execute update
        JPOCaller_mxJPO.defineInstance(_paramCache, this);
        try  {
            final String[] ret = MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n");
            if (!AbstractPropertyObject_mxJPO.TEST_EXECUTED.equals(ret[ret.length - 1]))  {
                throw new Exception("Execution of the update was not complete! Update Failed!");
            }
        } finally  {
            JPOCaller_mxJPO.undefineInstance(_paramCache);
        }
    }
}
