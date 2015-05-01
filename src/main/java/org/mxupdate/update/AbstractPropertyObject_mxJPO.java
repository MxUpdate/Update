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

package org.mxupdate.update;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.JPOCaller_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * Abstract definition for objects with properties.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractPropertyObject_mxJPO
    extends AbstractObject_mxJPO
{
    /**
     * String of the key within the parameter cache for the export application
     * parameter.
     */
    private static final String PARAM_EXPORTAPPLICATION = "ExportApplication";

    /**
     * String of the key within the parameter cache for the export author
     * parameter.
     */
    private static final String PARAM_EXPORTAUTHOR = "ExportAuthor";

    /**
     * String of the key within the parameter cache for the export original
     * name parameter.
     */
    private static final String PARAM_EXPORTORIGINALNAME = "ExportOriginalName";

    /**
     * String of the key within the parameter cache for the export version
     * parameter.
     */
    private static final String PARAM_EXPORTVERSION = "ExportVersion";

    /**
     * Header string of the application.
     */
    private static final String HEADER_APPLICATION = "\n# APPLICATION:\n# ~~~~~~~~~~~~\n#";

    /**
     * Header string of the author.
     */
    private static final String HEADER_AUTHOR = "\n# AUTHOR:\n# ~~~~~~~\n#";

    /**
     * Header string of the installer name.
     */
    private static final String HEADER_INSTALLER = "\n# INSTALLER:\n# ~~~~~~~~~~\n#";

    /**
     * Header string of the original name.
     */
    private static final String HEADER_ORIGINALNAME = "\n# ORIGINAL NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the version.
     */
    private static final String HEADER_VERSION = "\n# VERSION:\n# ~~~~~~~~\n#";

    /**
     * Test string used as output to test that the update runs correctly
     * (because it could be that no exception was thrown while an error happens
     * and the result is a rolled back transaction).
     */
    private static final String TEST_EXECUTED = "MxUpdate Executed";

    /**
     * <p>Defines the TCL procedures for updates and for logging purposes which
     * are executed by {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     * </p>
     * <p><b>Hint:</b><br/>
     * To get the complete definition, all quot's are replaced by {@code @0@0@}
     * and all apostroph's are replaced by {@code @1@1@}.
     * </p>
     */
    private static final String TCL_PROCEDURES
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
            + "}\n"
            + "proc mxUpdate {_sKind _sName _lsArgs}  {\n"
                + "global FILEDATE FILENAME\n"
                + "regsub -all {\\\\} $_lsArgs {@0@0@} sArg\n"
                + "regsub -all {'}    $sArg    {@1@1@} sArg\n"
                + "regsub -all {\\\"} $sArg    {@2@2@} sArg\n"
                + "regsub -all {\\\\\\[} $sArg {[} sArg\n"
                + "regsub -all {\\\\\\]} $sArg {]} sArg\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller mxUpdate $_sKind $_sName \"${sArg}\" \"$FILEDATE\" \"$FILENAME\" \"END\"\n"
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
        // write header
        if (_paramCache.getValueBoolean(AbstractPropertyObject_mxJPO.PARAM_EXPORTAUTHOR))  {
            _out.append('#').append(AbstractPropertyObject_mxJPO.HEADER_AUTHOR);
            if ((this.getAuthor() != null) && !this.getAuthor().isEmpty())  {
                _out.append(" ").append(this.getAuthor()).append('\n');
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
        if (_paramCache.contains(ValueKeys.Installer))  {
            installer = _paramCache.getValueString(ValueKeys.Installer);
        } else  {
            installer = this.extractFromCode(code,
                                             AbstractPropertyObject_mxJPO.HEADER_INSTALLER,
                                             _paramCache.getValueString(ValueKeys.DefaultInstaller));
        }
        tclVariables.put(PropertyDef_mxJPO.INSTALLER.name(), installer);

        if (this.getTypeDef().getMxAdminName() != null)  {
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

        // define file name
        tclVariables.put("FILENAME", _file.toString().replaceAll("\\\\", "/"));

        try {
            this.update(_paramCache, "", "", "", tclVariables, _file);
        } catch (final Exception e) {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
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
            throw new UpdateException_mxJPO(ErrorKey.ABSTRACT_PROPERTY_JPO_CALL_METHOD_NOT_DEFINED);
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
            throw new UpdateException_mxJPO(ErrorKey.ABSTRACT_PROPERTY_JPO_CALL_METHOD_UNKNOWN, Arrays.asList(_args));
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
           .append(AbstractPropertyObject_mxJPO.TCL_PROCEDURES);


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
