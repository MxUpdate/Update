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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.Mime64;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.plugin.GetProperties_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * Installs and updates the data model needed for MxUpdate. The JPO class is
 * automatically called from the &quot;MxInstall.mql&quot; MQL installation
 * script.
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
     * @see #updateAttributes(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_APPLNAME = "RegisterApplicationName";

    /**
     * Name of the parameter defining the author name.
     *
     * @see #updateAttributes(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_AUTHOR = "RegisterAuthorName";

    /**
     * Name of the parameter defining the installer name.
     *
     * @see #updateAttributes(ParameterCache_mxJPO, String)
     */
    private static final String PARAM_INSTALLER = "RegisterInstallerName";

    /**
     * Method used as entry from the MQL interface to install / update the data
     * model for MxUpdate. The data model is installed / updated in this order:
     * <ul>
     * <li>update attributes in
     *     {@link #updateAttributes(ParameterCache_mxJPO, String)}</li>
     * <li>update used business types by appending requried MxUpdate attributes
     *     in {@link #updateBusTypes(ParameterCache_mxJPO)}</li>
     * <li>define plugin properties in
     *     {@link #makePluginProperty(ParameterCache_mxJPO, File)}</li>
     * <li>register MxUpdate as application in
     *     {@link #registerMxUpdate(ParameterCache_mxJPO, String)}</li>
     * </ul>
     *
     * @param _context      MX context for this request
     * @param _args         first value defines the source installation path,
     *                      second value the version of MxUpdate which must be
     *                      installed
     * @throws Exception if installation failed
     * @see #updateAttributes(ParameterCache_mxJPO, String)
     * @see #updateBusTypes(ParameterCache_mxJPO)
     * @see #registerMxUpdate(ParameterCache_mxJPO, String)
     * @see #makePluginProperty(ParameterCache_mxJPO, File)
     */
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        final File path = new File(_args[0]);
        final String version = _args[1];

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, false);

        this.updateAttributes(paramCache, version);
        this.updateBusTypes(paramCache);
        this.makePluginProperty(paramCache, path);
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
        final String progName = _paramCache.getValueString(InstallDataModel_mxJPO.PARAM_PROGAPPL);

        _paramCache.logInfo("register MxUpdate " + _version);
        MqlUtil_mxJPO.execMql(_paramCache.getContext(), new StringBuilder()
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
        final String applName = _paramCache.getValueString(InstallDataModel_mxJPO.PARAM_APPLNAME);
        final String authorName = _paramCache.getValueString(InstallDataModel_mxJPO.PARAM_AUTHOR);
        final String installerName = _paramCache.getValueString(InstallDataModel_mxJPO.PARAM_INSTALLER);

        final String fileDate = StringUtil_mxJPO.formatFileDate(_paramCache, new Date());
        final String installedDate = StringUtil_mxJPO.formatInstalledDate(_paramCache, new Date());

        for (final AdminPropertyDef propDef : AdminPropertyDef.values())  {
            if ((propDef.getAttrName(_paramCache) != null) && !"".equals(propDef.getAttrName(_paramCache)))  {
                _paramCache.logInfo("check attribute '" + propDef.getAttrName(_paramCache) + "'");


                final String exists = MqlUtil_mxJPO.execMql(_paramCache.getContext(),
                        new StringBuilder().append("list attribute '")
                                           .append(propDef.getAttrName(_paramCache))
                                           .append('\''));
                if ("".equals(exists))  {
                    _paramCache.logDebug("    - create");
                    MqlUtil_mxJPO.execMql(_paramCache.getContext(),
                            new StringBuilder()
                                .append("escape add attribute \"")
                                .append(StringUtil_mxJPO.convertMql(propDef.getAttrName(_paramCache)))
                                .append("\" type string;"));
                }

                final StringBuilder cmd = new StringBuilder()
                    .append("escape mod attribute \"")
                    .append(StringUtil_mxJPO.convertMql(propDef.getAttrName(_paramCache))).append("\" ");

                final AbstractObject_mxJPO instance = _paramCache.getMapping()
                                                                 .getTypeDef("AttributeString")
                                                                 .newTypeInstance(propDef.getAttrName(_paramCache));

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
                                   propDef.getAttrName(_paramCache), cmd, false);
                // check for file date
                this.checkProperty(_paramCache, instance, AdminPropertyDef.FILEDATE, fileDate, cmd, true);
                // check for installed date
                this.checkProperty(_paramCache, instance, AdminPropertyDef.INSTALLEDDATE, installedDate, cmd, true);

                MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd);
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
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefs())  {
            if ((typeDef.getMxBusType() != null) && !"".equals(typeDef.getMxBusType())
                    && (typeDef.getMxAdminName() == null)
                    && (!typeDef.isBusCheckExists() || typeDef.existsBusType(_paramCache.getContext())))  {
                _paramCache.logInfo("check type "+typeDef.getMxBusType());
                for (final AdminPropertyDef propDef : AdminPropertyDef.values())  {
                    if ((propDef.getAttrName(_paramCache) != null) && !"".equals(propDef.getAttrName(_paramCache)))  {
                        final StringBuilder cmd = new StringBuilder()
                                .append("print type \"").append(typeDef.getMxBusType())
                                .append("\" select attribute[").append(propDef.getAttrName(_paramCache))
                                .append("] dump");
                        if ("false".equalsIgnoreCase(MqlUtil_mxJPO.execMql(_paramCache.getContext(), cmd)))  {
                            _paramCache.logDebug("    - add missing attribute '"
                                    + propDef.getAttrName(_paramCache) + "'");
                            MqlUtil_mxJPO.execMql(_paramCache.getContext(), new StringBuilder()
                                    .append("mod type \"").append(typeDef.getMxBusType())
                                    .append("\" add attribute \"").append(propDef.getAttrName(_paramCache))
                                    .append('\"'));
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates and stores the properties for the plugin. A property entry is
     * created for each type definition of {@link TypeDef_mxJPO} for which an
     * icon (path) is defined. The format of the properties is defined in
     * {@link GetProperties_mxJPO}.
     *
     * @param _paramCache       parameter cache
     * @param _sourcePath       reference to the source path
     * @throws IOException      if the file of icon not exists or could not
     *                          opened
     * @throws MatrixException  if the update of the plugin properties program
     *                          failed
     */
    protected void makePluginProperty(final ParameterCache_mxJPO _paramCache,
                                      final File _sourcePath)
            throws IOException, MatrixException
    {
        // prepare properties as set
        final Set<String> props = new TreeSet<String>();
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefs())  {
            if (typeDef.getIconPath() != null)  {

                final File file = new File(_sourcePath, typeDef.getIconPath());
                final String icon;
                final InputStream in = new FileInputStream(file);
                try  {
                    final byte[] bin = new byte[in.available()];
                    in.read(bin);
                    icon = Mime64.encode(bin);
                } finally  {
                    in.close();
                }

                props.add(new StringBuilder()
                            .append(typeDef.getName())
                            .append(".FilePrefix = ")
                            .append(typeDef.getFilePrefix() == null ? "" : typeDef.getFilePrefix())
                            .append('\n')
                            .append(typeDef.getName())
                            .append(".FileSuffix = ")
                            .append(typeDef.getFileSuffix() == null ? "" : typeDef.getFileSuffix())
                            .append('\n')
                            .append(typeDef.getName())
                            .append(".Icon = ")
                            .append(icon).toString());
            }
        }

        // make string of properties set
        final StringBuilder propString = new StringBuilder();
        for (final String prop : props)  {
            propString.append(prop).append('\n');
        }

        // write properties
        if ("".equals(MqlUtil_mxJPO.execMql(_paramCache.getContext(), "list prog 'org.mxupdate.plugin.plugin.properties'")))  {
            MqlUtil_mxJPO.execMql(_paramCache.getContext(), "escape add prog 'org.mxupdate.plugin.plugin.properties'");
        }
        MqlUtil_mxJPO.execMql(_paramCache.getContext(), new StringBuilder()
                .append("escape mod prog 'org.mxupdate.plugin.plugin.properties' code \"")
                .append(StringUtil_mxJPO.convertMql(propString)).append("\""));
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
        final String current = _instance.getPropValue(_paramCache, _propDef);
        if ((!_newValue.equals(current) && !_onlyIfNotDefined)
            || (((current == null) || "".equals(current)) && _onlyIfNotDefined))  {
            _paramCache.logDebug("    - define " + _propDef + " '" + _newValue + "'");
            _cmd.append("add property \"")
                .append(StringUtil_mxJPO.convertMql(_propDef.getPropName(_paramCache)))
                .append("\" value \"").append(StringUtil_mxJPO.convertMql(_newValue)).append("\" ");
        }
    }
}
