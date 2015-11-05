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

package org.mxupdate.install;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * Installs and updates the data model needed for MxUpdate. The JPO class is
 * automatically called from the &quot;MxInstall.mql&quot; MQL installation
 * script.
 *
 * @author The MxUpdate Team
 */
public class InstallDataModel_mxJPO
{
    /** Name of the parameter defining the author name. */
    private static final String PARAM_AUTHOR            = "RegisterAuthorName";
    /** Name of the parameter defining the installer name. */
    private static final String PARAM_INSTALLER         = "RegisterInstallerName";

    /** Name of the parameter defining the format of the file date / format. */
    private static final String PARAM_INSTALLFILEDATEFORMAT = "InstallFileDateFormatJava";

    /** MQL statement to list of MxUpdate Update programs for which the symbolic names must be registered. */
    private static final String LIST_MXUPDATE_PROGRAMS = "escape list program MxUpdate,org.mxupdate* select name isjavaprogram dump @";

    /**
     * Method used as entry from the MQL interface to install / update the data
     * model for MxUpdate. The data model is installed / updated in this order:
     * <ul>
     * <li>update attributes in
     *     {@link #updateAttributes(ParameterCache_mxJPO, String, String, String, String, String, String)}</li>
     * <li>update used business types by appending required MxUpdate attributes
     *     in {@link #updateBusTypes(ParameterCache_mxJPO)}</li>
     * <li>register the MxUdpate Update programs with their symbolic names in
     *     {@link #registerPrograms(ParameterCache_mxJPO, String, String, String, String, String)}</li>
     * <li>register MxUpdate as application in
     *     {@link #registerMxUpdate(ParameterCache_mxJPO, String, String)}</li>
     * </ul>
     *
     * @param _context      MX context for this request
     * @param _args         first value defines the source installation path
     *                      (in development it is the resources sub directory),
     *                      second value the version of MxUpdate which must be
     *                      installed
     * @throws Exception if installation failed
     * @see #updateAttributes(ParameterCache_mxJPO, String, String, String, String, String, String)
     * @see #updateBusTypes(ParameterCache_mxJPO)
     * @see #registerPrograms(ParameterCache_mxJPO, String, String, String, String, String)
     * @see #registerMxUpdate(ParameterCache_mxJPO, String, String)
     * @see #makePluginProperty(ParameterCache_mxJPO, File)
     */
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        final String applVersion = _args[1];

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, false);

        final String authorName = paramCache.getValueString(InstallDataModel_mxJPO.PARAM_AUTHOR);
        final String installerName = paramCache.getValueString(InstallDataModel_mxJPO.PARAM_INSTALLER);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(paramCache.getValueString(InstallDataModel_mxJPO.PARAM_INSTALLFILEDATEFORMAT));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00"));

        final String fileDate = dateFormat.format(new Date());
        final String installedDate = StringUtil_mxJPO.formatInstalledDate(paramCache, new Date());

        this.updateAttributes(paramCache, applVersion, authorName, installerName, fileDate, installedDate);
        this.updateBusTypes(paramCache);
        this.registerPrograms(paramCache, applVersion, authorName, installerName, installedDate);
        this.registerMxUpdate(paramCache, applVersion);
    }

    /**
     * All programs evaluated with {@link #LIST_MXUPDATE_PROGRAMS} are checked
     * if they must be registered with a symbolic name and all other specific
     * properties are set. If they are not already registered, the registration
     * of the symbolic names of the programs are done. And also all other
     * properties are set only if they are not already set.
     *
     * @param _paramCache       parameter cache
     * @param _applVersion      Mx Update version
     * @param _authorName       used author name
     * @param _installerName    used installer name
     * @param _installedDate    used installed date
     * @throws Exception if registration of the symbolic names for all MxUpdate
     *                   Update programs failed
     * @see #LIST_MXUPDATE_PROGRAMS
     */
    protected void registerPrograms(final ParameterCache_mxJPO _paramCache,
                                    final String _applVersion,
                                    final String _authorName,
                                    final String _installerName,
                                    final String _installedDate)
        throws Exception
    {
        final String applName = _paramCache.getValueString(ValueKeys.RegisterApplicationName);
        final String progs = MqlUtil_mxJPO.execMql(_paramCache, InstallDataModel_mxJPO.LIST_MXUPDATE_PROGRAMS);
        for (final String progLine : new TreeSet<>(Arrays.asList(progs.split("\n"))))  {
            final String[] progLineArr = progLine.split("@");
            final String progName = progLineArr[0];
            // do we have a JPO?
            _paramCache.logInfo("check program '" + progName + "'");
            this.registerObject(_paramCache, "program", progName);
            final StringBuilder cmd = new StringBuilder()
                    .append("escape mod program \"").append(StringUtil_mxJPO.convertMql(progName)).append("\" ");

            // check for correct property entries
            this.checkProperty(_paramCache, "program", progName, "application",        applName,       cmd, false);
            this.checkProperty(_paramCache, "program", progName, "installer",          _installerName, cmd, false);
            this.checkProperty(_paramCache, "program", progName, "installed date",     _installedDate, cmd, true);
            this.checkProperty(_paramCache, "program", progName, "version",            _applVersion,   cmd, false);
            this.checkProperty(_paramCache, "program", progName, "original name",      progName,       cmd, false);
            this.checkProperty(_paramCache, "program", progName, "author",             _authorName,    cmd, false);

            MqlUtil_mxJPO.execMql(_paramCache, cmd);
        }
    }

    /**
     * Makes the registration of MxUpdate as application.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _version      MxUpdate version
     * @throws MatrixException if registration of MxUpdate failed
     */
    protected void registerMxUpdate(final ParameterCache_mxJPO _paramCache,
                                    final String _version)
            throws MatrixException
    {
        final String applName = _paramCache.getValueString(ValueKeys.RegisterApplicationName);
        final String progName = _paramCache.getValueString(ValueKeys.RegisterApplicationProg);

        final String curVers = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape print program ").arg(progName).cmd(" ")
                        .cmd("select ").arg("property[appVersion" + applName + "].value").cmd(" dump")
                        .exec(_paramCache.getContext());
        if (!_version.equals(curVers))  {
            MqlBuilderUtil_mxJPO.mql()
                    .cmd("escape modify program ").arg(progName).cmd(" ")
                            .cmd("add property ").arg("appVersion" + applName).cmd(" value ").arg(_version)
                            .exec(_paramCache.getContext());
        }
    }

    /**
     * Creates / updates all needed attributes used as MxUpdate properties.
     *
     * @param _paramCache       parameter cache
     * @param _applVersion      new MxUpdate version
     * @param _authorName       used author name
     * @param _installerName    used installer name
     * @param _fileDate         used file date
     * @param _installedDate    used installed date
     * @throws Exception if update of attribute failed
     */
    protected void updateAttributes(final ParameterCache_mxJPO _paramCache,
                                    final String _applVersion,
                                    final String _authorName,
                                    final String _installerName,
                                    final String _fileDate,
                                    final String _installedDate)
            throws Exception
    {
        final String applName = _paramCache.getValueString(ValueKeys.RegisterApplicationName);
        for (final PropertyDef_mxJPO propDef : PropertyDef_mxJPO.values())  {
            if ((propDef.getAttrName(_paramCache) != null) && !propDef.getAttrName(_paramCache).isEmpty())  {
                final String attrName = propDef.getAttrName(_paramCache);

                _paramCache.logInfo("check attribute '" + attrName + "'");

                final String exists = MqlUtil_mxJPO.execMql(_paramCache,
                        new StringBuilder().append("list attribute '").append(attrName).append('\''));
                if (exists.isEmpty())  {
                    _paramCache.logDebug("    - create");
                    MqlUtil_mxJPO.execMql(_paramCache,
                            new StringBuilder()
                                .append("escape add attribute \"")
                                .append(StringUtil_mxJPO.convertMql(attrName))
                                .append("\" type string;"));
                }

                final StringBuilder cmd = new StringBuilder()
                    .append("escape mod attribute \"")
                    .append(StringUtil_mxJPO.convertMql(attrName)).append("\" ");

                // check for correct property entries
                this.checkProperty(_paramCache, "attribute", attrName, "application",                                        applName,                           cmd, false);
                this.checkProperty(_paramCache, "attribute", attrName, "installer",                                          _installerName,                     cmd, false);
                this.checkProperty(_paramCache, "attribute", attrName, "installed date",                                     _installedDate,                     cmd, true);
                this.checkProperty(_paramCache, "attribute", attrName, "version",                                            _applVersion,                       cmd, false);
                this.checkProperty(_paramCache, "attribute", attrName, "original name",                                      propDef.getAttrName(_paramCache),   cmd, false);
                this.checkProperty(_paramCache, "attribute", attrName, "author",                                             _authorName,                        cmd, false);
                this.checkProperty(_paramCache, "attribute", attrName, PropertyDef_mxJPO.FILEDATE.getPropName(_paramCache),  _fileDate,                          cmd, true);

                MqlUtil_mxJPO.execMql(_paramCache, cmd);

                this.registerObject(_paramCache, "attribute", attrName);
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
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
            if ((typeDef.getMxBusType() != null) && !"".equals(typeDef.getMxBusType())
                    && (typeDef.getMxAdminName() == null)
                    && (!typeDef.isBusCheckExists() || typeDef.existsBusType(_paramCache)))  {
                _paramCache.logInfo("check type "+typeDef.getMxBusType());
                for (final PropertyDef_mxJPO propDef : PropertyDef_mxJPO.values())  {
                    if ((propDef.getAttrName(_paramCache) != null) && !"".equals(propDef.getAttrName(_paramCache)))  {
                        final StringBuilder cmd = new StringBuilder()
                                .append("print type \"").append(typeDef.getMxBusType())
                                .append("\" select attribute[").append(propDef.getAttrName(_paramCache))
                                .append("] dump");
                        if ("false".equalsIgnoreCase(MqlUtil_mxJPO.execMql(_paramCache, cmd)))  {
                            _paramCache.logDebug("    - add missing attribute '"
                                    + propDef.getAttrName(_paramCache) + "'");
                            MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
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
     * Checks a property value and updates the property if not defined (if
     * parameter <code>_onlyIfNotDefined</code> is <i>true</i>) or not equal to
     * new value (if parameter <code>_onlyIfNotDefined</code> is <i>false</i>).
     *
     * @param _paramCache       parameter cache
     * @param _mxClass          MX Class
     * @param _name             name of object
     * @param _propDef          property definition
     * @param _newValue         new property value
     * @param _cmd              string builder used to append MQL code
     * @param _onlyIfNotDefined new property value is only defined if currently
     *                          no value is defined
     * @throws MatrixException if current value from the instance object could
     *                         not be evaluated
     */
    protected void checkProperty(final ParameterCache_mxJPO _paramCache,
                                 final String _mxClass,
                                 final String _name,
                                 final String _propName,
                                 final String _newValue,
                                 final StringBuilder _cmd,
                                 final boolean _onlyIfNotDefined)
            throws MatrixException
    {
        // fetch current value
        final String tmp = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                .append("escape print ").append(_mxClass).append(" \"").append(StringUtil_mxJPO.convertMql(_name)).append("\" ")
                .append("select property[").append(_propName).append("] dump"));
        final int length = 7 + _propName.length();
        final String current = (tmp.length() >= length) ? tmp.substring(length) : "";

        if ((!_newValue.equals(current) && !_onlyIfNotDefined) || (((current == null) || current.isEmpty()) && _onlyIfNotDefined))  {
            _paramCache.logDebug("    - define " + _propName + " '" + _newValue + "'");
            _cmd.append("add property \"")
                .append(StringUtil_mxJPO.convertMql(_propName))
                .append("\" value \"").append(StringUtil_mxJPO.convertMql(_newValue)).append("\" ");
        }
    }

    /**
     * Register symbolic name for given administration object
     * <code>_instance</code>.
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _mxClass      MX class
     * @param _name         name
     * @param _instance     instance which must be registered
     * @throws Exception if the registration of the symbolic name failed
     */
    protected void registerObject(final ParameterCache_mxJPO _paramCache,
                                  final String _mxClass,
                                  final String _name)
        throws Exception
    {
        final String newSymbName = _mxClass + "_" + _name.replaceAll(" ", "");
        final Set<String> symbolicNames = new HashSet<>();

        final String symbProg = _paramCache.getValueString(ValueKeys.RegisterSymbolicNames);

        // reads symbolic names of the administration objects
        final String symbProgIdxOf = new StringBuilder().append(" on program ").append(symbProg).append(' ').toString();
        final StringBuilder cmd = new StringBuilder()
                .append("escape list property on program \"").append(StringUtil_mxJPO.convertMql(symbProg)).append("\" to ")
                    .append(_mxClass).append(" \"").append(_name).append("\"");
        for (final String symbName : MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n"))  {
            if (!"".equals(symbName))  {
                symbolicNames.add(symbName.substring(0, symbName.indexOf(symbProgIdxOf)));
            }
        }
        // check symbolic names
        final StringBuilder update = new StringBuilder();
        if (!symbolicNames.contains(newSymbName))  {
            _paramCache.logDebug("    - register symbolic name '" + newSymbName + "'");
            update.append("escape add property \"").append(StringUtil_mxJPO.convertMql(newSymbName)).append("\" ")
                    .append(" on program \"").append(StringUtil_mxJPO.convertMql(symbProg)).append("\" to ")
                    .append(_mxClass)
                    .append(" \"").append(StringUtil_mxJPO.convertMql(_name)).append("\";\n");
        }
        for (final String exSymbName : symbolicNames)  {
            if (!newSymbName.equals(exSymbName))  {
                _paramCache.logDebug("    - remove symbolic name '" + exSymbName + "'");
                update.append("escape delete property \"")
                                .append(StringUtil_mxJPO.convertMql(exSymbName)).append("\" ")
                        .append(" on program \"").append(StringUtil_mxJPO.convertMql(symbProg)).append("\" to ")
                        .append(_mxClass).append(" \"").append(StringUtil_mxJPO.convertMql(_name)).append("\";\n");
            }
        }

        MqlUtil_mxJPO.execMql(_paramCache, update);
    }
}
