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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.JPOCaller_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * Abstract definition for objects with properties.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractPropertyObject_mxJPO<CLASS extends AbstractPropertyObject_mxJPO<CLASS>>
    extends AbstractObject_mxJPO
{
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
            + "proc mxUpdate {_sKind _sName _sReviLsArg {_lsArgs 0}}  {\n"
                + "global FILEDATE FILENAME\n"
                + "if {$_lsArgs == 0}  {\n"
                        + "set sArg  $_sReviLsArg\n"
                        + "set sRevi \"\""
                + "} else  {\n"
                        + "set sArg  $_lsArgs\n"
                        + "set sRevi $_sReviLsArg"
                + "}\n"
                // sName
                + "regsub -all {\\\\} $_sName   {@0@0@} sName;"
                + "regsub -all {'}    $sName    {@1@1@} sName;"
                + "regsub -all {\\\"} $sName    {@2@2@} sName;"
                + "regsub -all {\\\\\\[} $sName {[} sName;"
                + "regsub -all {\\\\\\]} $sName {]} sName\n"
                // sRevi
                + "regsub -all {\\\\} $sRevi    {@0@0@} sRevi;"
                + "regsub -all {'}    $sRevi    {@1@1@} sRevi;"
                + "regsub -all {\\\"} $sRevi    {@2@2@} sRevi;"
                + "regsub -all {\\\\\\[} $sRevi {[} sRevi;"
                + "regsub -all {\\\\\\]} $sRevi {]} sRevi\n"
                // sArg
                + "regsub -all {\\\\} $sArg     {@0@0@} sArg;"
                + "regsub -all {'}    $sArg     {@1@1@} sArg;"
                + "regsub -all {\\\"} $sArg     {@2@2@} sArg;"
                + "regsub -all {\\\\\\[} $sArg  {[} sArg;"
                + "regsub -all {\\\\\\]} $sArg  {]} sArg\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller mxUpdate $_sKind $sName $sRevi \"${sArg}\" \"$FILEDATE\" \"$FILENAME\" \"END\"\n"
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
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     * {@link #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)}.
     *
     * @param _paramCache   parameter cache
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _file         file with TCL update code
     * @throws Exception if the update from the derived class failed
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
                       final File _file)
        throws Exception
    {
        // parse objects
        this.parse(_paramCache);

        final Map<String,String> tclVariables = new HashMap<String,String>();

        // define file date
        tclVariables.put(PropertyDef_mxJPO.FILEDATE.name(), StringUtil_mxJPO.formatFileDate(_paramCache, new Date(_file.lastModified())));

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
            cmd.append("set ").append(entry.getKey()).append(" \"").append(StringUtil_mxJPO.convertTcl(entry.getValue())).append("\"\n");
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

    /**
     * Parses the given {@code _code} and updates this data instance.
     *
     * @param _code     code to parse
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public abstract void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException;

    /**
     * Writes the complete update code.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException  if the write of the TCL update to the writer
     *                      instance failed
     * @throws MatrixException if an execution of a MQL command failed
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(this.getFileName(), _paramCache);

        updateBuilder.start(this.getTypeDef());

        this.writeUpdate(updateBuilder);

        updateBuilder.end();

        _out.append(updateBuilder.toString());
    }

    /**
     * Writes the update file content to the builder.
     *
     * @param _updateBuilder    update builder
     */
    protected abstract void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder);

    /**
     * Calculates the delta between given {@code _current} admin object
     * definition and this target admin object definition and appends the MQL
     * append commands to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _mql          builder to append the MQL commands
     * @param _current      current admin object definition
     * @throws UpdateException_mxJPO if update is not allowed (e.g. if data can
     *                      be potentially lost)
     */
    protected abstract void calcDelta(final ParameterCache_mxJPO _paramCache,
                                      final MultiLineMqlBuilder _mql,
                                      final CLASS _current)
        throws UpdateException_mxJPO;
}
