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

import org.mxupdate.script.ScriptHandler_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.FileUtils_mxJPO;

/**
 * Updates given CI object.
 *
 * @author The MxUpdate Team
 */
public class UpdateObject_mxJPO
    implements IUpdate_mxJPO
{
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
        final ScriptHandler_mxJPO script = new ScriptHandler_mxJPO();

        script.defVar("FILEDATE", StringUtil_mxJPO.formatFileDate(_paramCache, new Date(_file.lastModified())));
        script.defVar("FILENAME", _file.toString().replaceAll("\\\\", "/"));
        script.defVar("FILESUBPATH", FileUtils_mxJPO.extraceSubPath(_file.getAbsolutePath(), _typeDef.getFilePath()));
        script.defVar("CREATE", String.valueOf(_create));

        if (_typeDef.getMxAdminName() != null)  {
            script.defVar("NAME", _mxName);
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
            script.defVar("NAME",     nameRev[0]);
            script.defVar("REVISION", (nameRev.length > 1) ? nameRev[1] : "");
        }

        try {
            script.parse(FileUtils_mxJPO.readFileToString(_file))
                  .execute(_paramCache);
        } catch (final Exception e) {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        }
    }
}
