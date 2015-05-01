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

package org.mxupdate.typedef.update;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.JPOCaller_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Updates given CI object.
 *
 * @author The MxUpdate Team
 */
public class UpdateObject_mxJPO
    implements IUpdate_mxJPO
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
                + "global FILEDATE FILENAME CREATE\n"
                + "if {$_lsArgs == 0}  {\n"
                        + "set sCode $_sReviLsArg\n"
                        + "set sRevi \"\""
                + "} else  {\n"
                        + "set sCode $_lsArgs\n"
                        + "set sRevi $_sReviLsArg"
                + "}\n"
                // file name
                + "regsub -all {\\\\} $FILENAME     {@0@0@} sFileName;"
                + "regsub -all {'}    $sFileName    {@1@1@} sFileName;"
                + "regsub -all {\\\"} $sFileName    {@2@2@} sFileName;"
                + "regsub -all {\\\\\\[} $sFileName {[} sFileName;"
                + "regsub -all {\\\\\\]} $sFileName {]} sFileName\n"
                // file date
                + "regsub -all {\\\\} $FILEDATE     {@0@0@} sFileDate;"
                + "regsub -all {'}    $sFileDate    {@1@1@} sFileDate;"
                + "regsub -all {\\\"} $sFileDate    {@2@2@} sFileDate;"
                + "regsub -all {\\\\\\[} $sFileDate {[} sFileDate;"
                + "regsub -all {\\\\\\]} $sFileDate {]} sFileDate\n"
                // name
                + "regsub -all {\\\\} $_sName       {@0@0@} sName;"
                + "regsub -all {'}    $sName        {@1@1@} sName;"
                + "regsub -all {\\\"} $sName        {@2@2@} sName;"
                + "regsub -all {\\\\\\[} $sName     {[} sName;"
                + "regsub -all {\\\\\\]} $sName     {]} sName\n"
                // revision
                + "regsub -all {\\\\} $sRevi        {@0@0@} sRevi;"
                + "regsub -all {'}    $sRevi        {@1@1@} sRevi;"
                + "regsub -all {\\\"} $sRevi        {@2@2@} sRevi;"
                + "regsub -all {\\\\\\[} $sRevi     {[} sRevi;"
                + "regsub -all {\\\\\\]} $sRevi     {]} sRevi\n"
                // code
                + "regsub -all {\\\\} $sCode        {@0@0@} sCode;"
                + "regsub -all {'}    $sCode        {@1@1@} sCode;"
                + "regsub -all {\\\"} $sCode        {@2@2@} sCode;"
                + "regsub -all {\\\\\\[} $sCode     {[} sCode;"
                + "regsub -all {\\\\\\]} $sCode     {]} sCode\n"
                // create
                + "regsub -all {\\\\} $CREATE        {@0@0@} sCreate;"
                + "regsub -all {'}    $sCreate       {@1@1@} sCreate;"
                + "regsub -all {\\\"} $sCreate       {@2@2@} sCreate;"
                + "regsub -all {\\\\\\[} $sCreate    {[} sCreate;"
                + "regsub -all {\\\\\\]} $sCreate    {]} sCreate\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller mxUpdate $_sKind $sFileName $sFileDate $sName $sRevi $sCode $sCreate END\n"
            + "}\n";

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
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update; if
     *                          <code>null</code> file is not called (sourced)
     * @throws Exception if update failed
     * @see #TCL_LOG_PROCS
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final TypeDef_mxJPO _typeDef,
                       final boolean _create,
                       final String _mxName,
                       final File _file)
        throws Exception
    {
        final Map<String,String> tclVariables = new HashMap<String,String>();

        tclVariables.put("FILEDATE", StringUtil_mxJPO.formatFileDate(_paramCache, new Date(_file.lastModified())));
        tclVariables.put("FILENAME", _file.toString().replaceAll("\\\\", "/"));
        tclVariables.put("CREATE", String.valueOf(_create));

        if (_typeDef.getMxAdminName() != null)  {
            tclVariables.put("NAME", _mxName);
        } else  {
            final String[] nameRev;
            if (_typeDef.hasMxBusTypeDerived())  {
                final String[] typeNameRev = _mxName.split(BusObject_mxJPO.SPLIT_TYPE);
                if (typeNameRev.length == 2)  {
                    nameRev = typeNameRev[1].split(BusObject_mxJPO.SPLIT_NAME);
                } else  {
                    nameRev = _mxName.split(BusObject_mxJPO.SPLIT_NAME);
                }
            } else  {
                nameRev = _mxName.split(BusObject_mxJPO.SPLIT_NAME);
            }
            tclVariables.put("NAME",     nameRev[0]);
            tclVariables.put("REVISION", (nameRev.length > 1) ? nameRev[1] : "");
        }

        try {
            this.executeUpdate(_paramCache, _typeDef, tclVariables, _file);
        } catch (final Exception e) {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        }
    }

    /**
     * Executes the update via TCL.
     *
     * @param _paramCache       parameter cache
     * @param _typeDef          type definition
     * @param _tclVariables     defined variables
     * @param _file             file
     * @throws Exception        exception
     */
    protected void executeUpdate(final ParameterCache_mxJPO _paramCache,
                                 final TypeDef_mxJPO _typeDef,
                                 final Map<String,String> _tclVariables,
                                 final File _file)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder();

        // append TCL mode
        cmd.append("tcl;\n")
           .append("eval  {\n")
           .append(UpdateObject_mxJPO.TCL_PROCEDURES);


        // define all TCL variables
        for (final Entry<String, String> entry : _tclVariables.entrySet())  {
            cmd.append("set ").append(entry.getKey()).append(" \"").append(StringUtil_mxJPO.convertTcl(entry.getValue())).append("\"\n");
        }
        // append TCL code, end of TCL mode and post MQL statements
        // (source with the file must be replace for windows ...)
        if (_file != null)  {
            cmd.append("\nsource \"").append(_file.toString().replaceAll("\\\\", "/")).append("\"");
        }
        cmd.append("\n}\nexit;\n")
           .append("output '';output '").append(UpdateObject_mxJPO.TEST_EXECUTED).append("';");

        // execute update
        JPOCaller_mxJPO.defineInstance(_paramCache, _typeDef);
        try  {
            final String[] ret = MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n");
            if (!UpdateObject_mxJPO.TEST_EXECUTED.equals(ret[ret.length - 1]))  {
                throw new Exception("Execution of the update was not complete! Update Failed!");
            }
        } finally  {
            JPOCaller_mxJPO.undefineInstance(_paramCache);
        }
    }
}
