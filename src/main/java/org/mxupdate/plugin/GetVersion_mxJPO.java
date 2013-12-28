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

package org.mxupdate.plugin;

import java.util.Map;

import matrix.db.Context;
import matrix.db.MatrixWriter;

import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The JPO class returns the current installed version of the MxUpdate Update
 * deployment tool stored in MX. The JPO is used from the
 * {@link Dispatcher_mxJPO dispatcher class}.
 *
 * @author The MxUpdate Team
 * @see Dispatcher_mxJPO
 */
public class GetVersion_mxJPO
    extends AbstractPlugin_mxJPO
{
    /**
     * Name of the parameter defining the program name where applications must
     * be registered.
     *
     * @see #mxMain(Context, String...)
     */
    private static final String PARAM_PROGAPPL = "RegisterApplicationProg";

    /**
     * Name of the parameter defining the application name.
     *
     * @see #mxMain(Context, String...)
     */
    private static final String PARAM_APPLNAME = "RegisterApplicationName";

    /**
     * Returns the current installed MxUpdate Update Tool version. The version
     * itself is registered on the {@link #PARAM_APPLNAME MX OOTB program} with
     * the {@link #PARAM_APPLNAME MxUpdate application name}.
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    arguments from the Eclipse Plug-In (not used)
     * @return prepared return only with the version as string
     * @throws Exception if version could not be evaluated
     */
    String execute(final ParameterCache_mxJPO _paramCache,
                   final Map<String,Object> _arguments)
        throws Exception
    {
        final String progName = _paramCache.getValueString(GetVersion_mxJPO.PARAM_PROGAPPL);
        final String applName = _paramCache.getValueString(GetVersion_mxJPO.PARAM_APPLNAME);

        final String version = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                .append("escape print prog \"").append(StringUtil_mxJPO.convertMql(progName)).append("\" ")
                .append("select property[appVersion").append(StringUtil_mxJPO.convertMql(applName)).append("].value ")
                .append("dump"));

        return version;
    }

    /**
     * This is the main method called from MQL. The version is written to the
     * MatrixWriter logger so that an execute of this JPO returns the related
     * version information about the MxUpdate Update deployment tool.
     *
     * @param _context  MX context for this request
     * @param _args     arguments from MQL console (not used)
     * @throws Exception if the evaluate of the properties failed
     */
    @Deprecated()
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true);

        final String progName = paramCache.getValueString(GetVersion_mxJPO.PARAM_PROGAPPL);
        final String applName = paramCache.getValueString(GetVersion_mxJPO.PARAM_APPLNAME);

        final String version = MqlUtil_mxJPO.execMql(paramCache, new StringBuilder()
                .append("escape print prog \"").append(StringUtil_mxJPO.convertMql(progName)).append("\" ")
                .append("select property[appVersion").append(StringUtil_mxJPO.convertMql(applName)).append("].value ")
                .append("dump"));

        final MatrixWriter writer = new MatrixWriter(_context);
        writer.write(version);
        writer.flush();
        writer.close();
    }
}
