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

package org.mxupdate.plugin;

import matrix.db.Context;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * The JPO class returns the current installed version of the MxUpdate Update
 * deployment tool stored in MX. To get the version information, following MQL
 * statement must be executed:
 * <pre>
 * exec prog org.mxupdate.plugin.GetVersion
 * </pre>
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class GetVersion_mxJPO
{
    /**
     * Name of the parameter defining the program name where applications must
     * be registered.
     *
     * @see #registerMxUpdate(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_PROGAPPL = "RegisterApplicationProg";

    /**
     * Name of the parameter defining the application name.
     *
     * @see #updateAttributes(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_APPLNAME = "RegisterApplicationName";

    /**
     * This is the main method called from MQL. The version is written to the
     * MatrixWriter logger so that an execute of this JPO returns the related
     * version information about the MxUpdate Update deployment tool.
     *
     * @param _context  MX context for this request
     * @param _args     arguments from MQL console (not used)
     * @throws Exception if the evaluate of the properties failed
     */
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true);

        final String progName = paramCache.getValueString(GetVersion_mxJPO.PARAM_PROGAPPL);
        final String applName = paramCache.getValueString(GetVersion_mxJPO.PARAM_APPLNAME);

        final String version = MqlUtil_mxJPO.execMql(paramCache.getContext(), new StringBuilder()
                .append("escape print prog \"").append(StringUtil_mxJPO.convertMql(progName)).append("\" ")
                .append("select property[appVersion").append(StringUtil_mxJPO.convertMql(applName)).append("].value ")
                .append("dump"));

        paramCache.logInfo(version);
    }
}
