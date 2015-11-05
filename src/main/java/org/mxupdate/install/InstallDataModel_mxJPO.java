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
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.StringUtils_mxJPO;

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
    /** Wildcard for list of MxUpdate Update programs for which the symbolic names must be registered. */
    private static final String MATCH_MXUPDATE_PROGRAMS = "MxUpdate,org.mxupdate*";

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
     * @param _context  MX context for this request
     * @param _args     defines the version of MxUpdate which is installed
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
        final String applVersion = _args[0];

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, false);

        final SimpleDateFormat dateFormat = new SimpleDateFormat(paramCache.getValueString(ValueKeys.InstallFileDateFormatJava));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00"));

        final String fileDate = dateFormat.format(new Date());
        final String installedDate = StringUtil_mxJPO.formatInstalledDate(paramCache, new Date());

        final SortedSet<String> attrs = this.evalAttributes(paramCache);
        final SortedSet<String> types = this.evalTypes(paramCache);

        this.updateAttributes(paramCache, attrs, applVersion, fileDate, installedDate);
        this.updateBusTypes(paramCache, types, attrs);
        this.registerPrograms(paramCache, applVersion, installedDate);
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
     * @param _installedDate    used installed date
     * @throws Exception if registration of the symbolic names for all MxUpdate
     *                   Update programs failed
     * @see #LIST_MXUPDATE_PROGRAMS
     */
    protected void registerPrograms(final ParameterCache_mxJPO _paramCache,
                                    final String _applVersion,
                                    final String _installedDate)
        throws Exception
    {
        final String applName      = _paramCache.getValueString(ValueKeys.RegisterApplicationName);
        final String authorName    = _paramCache.getValueString(ValueKeys.RegisterAuthorName);
        final String installerName = _paramCache.getValueString(ValueKeys.RegisterInstallerName);
        final String progs         = MqlBuilderUtil_mxJPO.mql().cmd("escape list program ").arg(InstallDataModel_mxJPO.MATCH_MXUPDATE_PROGRAMS).exec(_paramCache.getContext());
        for (final String progName : new TreeSet<>(Arrays.asList(progs.split("\n"))))  {
            _paramCache.logInfo("check program '" + progName + "'");
            this.registerObject(_paramCache, "program", progName);

            // check for correct property entries
            this.checkProperty(_paramCache, "program", progName, "application",                                            applName,       false);
            this.checkProperty(_paramCache, "program", progName, PropertyDef_mxJPO.INSTALLER.getPropName(_paramCache),     installerName,  false);
            this.checkProperty(_paramCache, "program", progName, PropertyDef_mxJPO.INSTALLEDDATE.getPropName(_paramCache), _installedDate, true);
            this.checkProperty(_paramCache, "program", progName, "version",                                                _applVersion,   false);
            this.checkProperty(_paramCache, "program", progName, "original name",                                          progName,       false);
            this.checkProperty(_paramCache, "program", progName, "author",                                                 authorName,     false);
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
     * Evaluates all MxUpdate specific defined attributes.
     *
     * @param _paramCache       parameter cache
     * @return list of all custom MxUpdate attributes
     */
    protected SortedSet<String> evalAttributes(final ParameterCache_mxJPO _paramCache)
    {
        final SortedSet<String> ret = new TreeSet<>();
        for (final PropertyDef_mxJPO propDef : PropertyDef_mxJPO.values())  {
            final String attrName = propDef.getAttrName(_paramCache);
            if (!StringUtils_mxJPO.isEmpty(attrName))  {
                ret.add(attrName);
            }
        }
        return ret;
    }

    /**
     * Creates / updates all needed attributes used as MxUpdate properties.
     *
     * @param _paramCache       parameter cache
     * @param _attributes       list of needed attributes
     * @param _applVersion      new MxUpdate version
     * @param _fileDate         used file date
     * @param _installedDate    used installed date
     * @throws Exception if update of attribute failed
     */
    protected void updateAttributes(final ParameterCache_mxJPO _paramCache,
                                    final SortedSet<String> _attributes,
                                    final String _applVersion,
                                    final String _fileDate,
                                    final String _installedDate)
            throws Exception
    {
        final String applName       = _paramCache.getValueString(ValueKeys.RegisterApplicationName);
        final String authorName     = _paramCache.getValueString(ValueKeys.RegisterAuthorName);
        final String installerName  = _paramCache.getValueString(ValueKeys.RegisterInstallerName);

        for (final String attrName : _attributes)  {
            _paramCache.logInfo("check attribute '" + attrName + "'");

            final String exists = MqlBuilderUtil_mxJPO.mql().cmd("escape list attribute ").arg(attrName).exec(_paramCache.getContext());
            if (exists.isEmpty())  {
                _paramCache.logDebug("    - create");
                MqlBuilderUtil_mxJPO.mql().cmd("escape add attribute ").arg(attrName).cmd(" type string").exec(_paramCache.getContext());
            }

            // check for correct property entries
            this.checkProperty(_paramCache, "attribute", attrName, "application",                                            applName,       false);
            this.checkProperty(_paramCache, "attribute", attrName, PropertyDef_mxJPO.INSTALLER.getPropName(_paramCache),     installerName,  true);
            this.checkProperty(_paramCache, "attribute", attrName, PropertyDef_mxJPO.INSTALLEDDATE.getPropName(_paramCache), _installedDate, true);
            this.checkProperty(_paramCache, "attribute", attrName, "version",                                                _applVersion,   false);
            this.checkProperty(_paramCache, "attribute", attrName, "original name",                                          attrName,       false);
            this.checkProperty(_paramCache, "attribute", attrName, "author",                                                 authorName,     false);
            this.checkProperty(_paramCache, "attribute", attrName, PropertyDef_mxJPO.FILEDATE.getPropName(_paramCache),      _fileDate,      true);

            this.registerObject(_paramCache, "attribute", attrName);
        }
    }

    /**
     * Evaluates all MX types handled from MxUpdate.
     *
     * @param _paramCache       parameter cache
     * @return list of all custom MxUpdate attributes
     * @throws MatrixException if evaluate failed
     */
    protected SortedSet<String> evalTypes(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final SortedSet<String> ret = new TreeSet<>();
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
            final String typeName = typeDef.getMxBusType();
            if (!StringUtils_mxJPO.isEmpty(typeName)
                    && StringUtils_mxJPO.isEmpty(typeDef.getMxAdminName())
                    && (!typeDef.isBusCheckExists() || typeDef.existsBusType(_paramCache)))  {
                ret.add(typeName);
            }
        }
        return ret;
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
     * @param _types        types where MxUpdate specific attributes must be
     *                      appended
     * @param _attributes   attributes to append
     * @throws Exception if the update of the business types failed
     */
    protected void updateBusTypes(final ParameterCache_mxJPO _paramCache,
                                  final SortedSet<String> _types,
                                  final SortedSet<String> _attributes)
            throws Exception
    {
        for (final String typeName : _types)  {
            _paramCache.logInfo("check type " + typeName);
            for (final String attrName : _attributes)  {
                final String assigned = MqlBuilderUtil_mxJPO.mql().cmd("escape print type ").arg(typeName).cmd(" select ").arg("attribute[" + attrName + "]").cmd(" dump").exec(_paramCache.getContext());
                if ("false".equalsIgnoreCase(assigned))  {
                    _paramCache.logDebug("    - add missing attribute '" + attrName + "'");
                    MqlBuilderUtil_mxJPO.mql().cmd("escape modify type ").arg(typeName).cmd(" add attribute ").arg(attrName).exec(_paramCache.getContext());
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
     * @param _onlyIfNotDefined new property value is only defined if currently
     *                          no value is defined
     * @throws MatrixException if current value from the instance object could
     *                          not be evaluated
     */
    protected void checkProperty(final ParameterCache_mxJPO _paramCache,
                                 final String _mxClass,
                                 final String _name,
                                 final String _propName,
                                 final String _newValue,
                                 final boolean _onlyIfNotDefined)
            throws MatrixException
    {
        // fetch current value
        final String tmp = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape print ").cmd(_mxClass).cmd(" ").arg(_name).cmd(" ").cmd("select ").arg("property[" + _propName + "]").cmd(" dump")
                .exec(_paramCache.getContext());
        final int length = 7 + _propName.length();
        final String current = (tmp.length() >= length) ? tmp.substring(length) : "";

        if ((!_newValue.equals(current) && !_onlyIfNotDefined) || (StringUtils_mxJPO.isEmpty(current) && _onlyIfNotDefined))  {
            _paramCache.logDebug("    - define " + _propName + " '" + _newValue + "'");
            MqlBuilderUtil_mxJPO.mql()
                        .cmd("escape modify ").cmd(_mxClass).cmd(" ").arg(_name).cmd(" ")
                                .cmd("add property ").arg(_propName).cmd(" ")
                                .cmd("value ").arg(_newValue)
                        .exec(_paramCache.getContext());
        }
    }

    /**
     * Register symbolic name for given administration object {@code _instance}.
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
        final String symbNameStr = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape list property ")
                        .cmd("on program ").arg(symbProg).cmd(" ")
                        .cmd("to ").cmd(_mxClass).cmd(" ").arg(_name)
                .exec(_paramCache.getContext());
        if (!StringUtils_mxJPO.isEmpty(symbNameStr))  {
            for (final String symbName : symbNameStr.split("\n"))  {
                symbolicNames.add(symbName.substring(0, symbName.indexOf(symbProgIdxOf)));
            }
        }
        // append missing names
        if (!symbolicNames.contains(newSymbName))  {
            _paramCache.logDebug("    - register symbolic name '" + newSymbName + "'");
            MqlBuilderUtil_mxJPO.mql()
                    .cmd("escape add property ").arg(newSymbName).cmd(" ")
                            .cmd("on program ").arg(symbProg).cmd(" ")
                            .cmd("to ").cmd(_mxClass).cmd(" ").arg(_name)
                    .exec(_paramCache.getContext());
        }
        // remove obsolete names
        for (final String exSymbName : symbolicNames)  {
            if (!newSymbName.equals(exSymbName))  {
                _paramCache.logDebug("    - remove symbolic name '" + exSymbName + "'");
                MqlBuilderUtil_mxJPO.mql()
                        .cmd("escape delete property ").arg(exSymbName).cmd(" ")
                                .cmd("on program ").arg(symbProg).cmd(" ")
                                .cmd("to ").cmd(_mxClass).cmd(" ").arg(_name)
                        .exec(_paramCache.getContext());
            }
        }
    }
}
