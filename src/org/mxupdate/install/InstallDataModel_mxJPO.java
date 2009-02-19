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

package org.mxupdate.install;

import matrix.db.Context;

import org.mxupdate.mapping.Mapping_mxJPO;
import org.mxupdate.mapping.ParameterDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * Installs and updates the data model needed for MxUpdate.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class InstallDataModel_mxJPO
{
    /**
     * Name of the parameter defining the program name where applications must
     * be registered.
     *
     * @see #registerMxUpdate(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_PROGAPPL = "RegisterApplicationProg";

    /**
     * Method used as entry from the MQL interface to install / update the data
     * model for MxUpdate.
     *
     * @param _context      MX context for this request
     * @param _args         first value defines the version of MxUpdate which
     *                      must be installed
     * @throws Exception if installation failed
     * @see #updateBusTypes(ParameterCache_mxJPO)
     * @see #registerMxUpdate(ParameterCache_mxJPO, String)
     */
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        final String version = _args[0];

        // initialize mapping
        Mapping_mxJPO.init(_context);
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, ParameterDef_mxJPO.values());

        this.updateBusTypes(paramCache);
        this.registerMxUpdate(paramCache, version);

    }

    /**
     * Makes the registration of MxUpdate as application.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _version      MxUpdate version
     * @throws Exception if registration of MxUpdate failed
     * @see #PARAM_PROGAPPL
     */
    protected void registerMxUpdate(final ParameterCache_mxJPO _paramCache,
                                    final String _version)
            throws Exception
    {
        final String progName = _paramCache.getValueString(PARAM_PROGAPPL);

        _paramCache.logInfo("register MxUpdate " + _version);
        execMql(_paramCache.getContext(), new StringBuilder()
                .append("mod prog \"").append(progName).append("\" ")
                .append("add property \"appVersionMxUpdate\" ")
                .append("value \"").append(_version).append("\""));
    }

    /**
     * Updates all business type for which no defined administration type
     * exists. This includes:
     * <ul>
     * <li>append all required MxUpdate administrative attributes to business
     *     types</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @throws Exception if the update of the business types failed
     */
    protected void updateBusTypes(final ParameterCache_mxJPO _paramCache)
            throws Exception
    {
        for (final TypeDef_mxJPO typeDef : TypeDef_mxJPO.values())  {
            if ((typeDef.getMxBusType() != null) && !"".equals(typeDef.getMxBusType())
                    && (typeDef.getMxAdminName() == null)
                    && (!typeDef.isBusCheckExists() || typeDef.existsBusType(_paramCache.getContext())))  {
                _paramCache.logInfo("check type "+typeDef.getMxBusType());
                for (final AdminPropertyDef propDef : AdminPropertyDef.values())  {
                    if ((propDef.getAttrName() != null) && !"".equals(propDef.getAttrName()))  {
                        final StringBuilder cmd = new StringBuilder()
                                .append("print type \"").append(typeDef.getMxBusType())
                                .append("\" select attribute[").append(propDef.getAttrName())
                                .append("] dump");
                        if ("false".equalsIgnoreCase(execMql(_paramCache.getContext(), cmd)))  {
                            _paramCache.logDebug("    - add missing attribute '" + propDef.getAttrName() + "'");
                            execMql(_paramCache.getContext(), new StringBuilder()
                                    .append("mod type \"").append(typeDef.getMxBusType())
                                    .append("\" add attribute \"").append(propDef.getAttrName())
                                    .append('\"'));
                        }
                    }
                }
            }
        }
    }
}
