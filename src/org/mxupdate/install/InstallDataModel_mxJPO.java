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

import java.util.Date;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.mapping.Mapping_mxJPO;
import org.mxupdate.mapping.ParameterDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertMql;
import static org.mxupdate.update.util.StringUtil_mxJPO.formatFileDate;
import static org.mxupdate.update.util.StringUtil_mxJPO.formatInstalledDate;
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
     * Name of the parameter defining the application name.
     *
     * @see #updateAttributes(ParameterCache_mxJPO)
     */
    private static final String PARAM_APPLNAME = "RegisterApplicationName";

    /**
     * Name of the parameter defining the author name.
     *
     * @see #updateAttributes(ParameterCache_mxJPO)
     */
    private static final String PARAM_AUTHOR = "RegisterAuthorName";

    /**
     * Name of the parameter defining the installer name.
     *
     * @see #updateAttributes(ParameterCache_mxJPO)
     */
    private static final String PARAM_INSTALLER = "RegisterInstallerName";

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

        this.updateAttributes(paramCache, version);
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
     * Creates / updates all needed attributes used as MxUpdate properties.
     *
     * @param _paramCache   parameter cache
     * @param _version      new MxUpdate version
     * @throws Exception if update of attribute failed
     */
    protected void updateAttributes(final ParameterCache_mxJPO _paramCache,
                                    final String _version)
            throws Exception
    {
        final String applName = _paramCache.getValueString(PARAM_APPLNAME);
        final String authorName = _paramCache.getValueString(PARAM_AUTHOR);
        final String installerName = _paramCache.getValueString(PARAM_INSTALLER);

        final String fileDate = formatFileDate(_paramCache, new Date());
        final String installedDate = formatInstalledDate(_paramCache, new Date());

        for (final AdminPropertyDef propDef : AdminPropertyDef.values())  {
            if ((propDef.getAttrName() != null) && !"".equals(propDef.getAttrName()))  {
                _paramCache.logInfo("check attribute '" + propDef.getAttrName() + "'");


                final String exists = execMql(_paramCache.getContext(),
                        new StringBuilder().append("list attribute '")
                                           .append(propDef.getAttrName())
                                           .append('\''));
                if ("".equals(exists))  {
                    _paramCache.logDebug("    - create");
                    execMql(_paramCache.getContext(),
                            new StringBuilder()
                                .append("escape add attribute \"").append(convertMql(propDef.getAttrName()))
                                .append("\" type string;"));
                }

                final StringBuilder cmd = new StringBuilder()
                    .append("escape mod attribute \"").append(convertMql(propDef.getAttrName())).append("\" ");

                final AbstractObject_mxJPO instance = TypeDef_mxJPO.valueOf("Attribute")
                                                                   .newTypeInstance(propDef.getAttrName());

                // check for correct application name
                this.checkProperty(_paramCache, instance, AdminPropertyDef.APPLICATION, applName, cmd, false);
                // check for correct author name
                this.checkProperty(_paramCache, instance, AdminPropertyDef.AUTHOR, authorName, cmd, false);
                // check for correct installer name
                this.checkProperty(_paramCache, instance, AdminPropertyDef.INSTALLER, installerName, cmd, false);
                // check for correct version
                this.checkProperty(_paramCache, instance, AdminPropertyDef.VERSION, _version, cmd, false);
                // check for original name
                this.checkProperty(_paramCache, instance, AdminPropertyDef.ORIGINALNAME,
                        propDef.getAttrName(), cmd, false);
                // check for file date
                this.checkProperty(_paramCache, instance, AdminPropertyDef.FILEDATE, fileDate, cmd, true);
                // check for installed date
                this.checkProperty(_paramCache, instance, AdminPropertyDef.INSTALLEDDATE, installedDate, cmd, true);

                execMql(_paramCache.getContext(), cmd);
            }
        }
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

    /**
     * Checks a property value and updates the property if not defined (if
     * parameter <code>_onlyIfNotDefined</code> is <i>true</i>) or not equal to
     * new value (if parameter <code>_onlyIfNotDefined</code> is <i>false</i>).
     *
     * @param _paramCache       parameter cache
     * @param _instance         instance used to extract current property value
     * @param _propDef          property definition
     * @param _newValue         new property value
     * @param _cmd              string builder used to append MQL code
     * @param _onlyIfNotDefined new property value is only defined if currently
     *                          no value is defined
     * @throws MatrixException if current value from the instance object could
     *                         not be evaluated
     */
    protected void checkProperty(final ParameterCache_mxJPO _paramCache,
                                 final AbstractObject_mxJPO _instance,
                                 final AdminPropertyDef _propDef,
                                 final String _newValue,
                                 final StringBuilder _cmd,
                                 final boolean _onlyIfNotDefined)
            throws MatrixException
    {
        final String current = _instance.getPropValue(_paramCache.getContext(), _propDef);
        if ((!_newValue.equals(current) && !_onlyIfNotDefined)
            || (((current == null) || "".equals(current)) && _onlyIfNotDefined))  {
            _paramCache.logDebug("    - define " + _propDef + " '" + _newValue + "'");
            _cmd.append("add property \"")
                .append(convertMql(_propDef.getPropName()))
                .append("\" value \"").append(convertMql(_newValue)).append("\" ");
        }
    }
}
