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

package org.mxupdate.script.statement;

import java.io.File;
import java.util.Date;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.script.ScriptContext_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Statement to update a MxUpdate object.
 *
 * @author The MxUpdate Team
 */
public class MxUpdateStatement_mxJPO
    extends AbstractStatement_mxJPO
{
    /** MxUpdate type, name and revision of the MxUpdate object. */
    private String mxUpdateType, name, revision, code;

    /**
     * Defines the {@link #mxUpdateType MxUdpate type}.
     *
     * @param _mxUpdateType     MxUpdate type
     * @return this statement instance
     */
    public MxUpdateStatement_mxJPO setMxUpdateType(final String _mxUpdateType)
    {
        this.mxUpdateType = _mxUpdateType;
        return this;
    }

    /**
     * The method is called within the update of an administration object.
     *
     * @param _context  script context
     */
    @Override
    public void execute(final ScriptContext_mxJPO _context)
            throws Exception
    {
        final EMxAdmin_mxJPO mxClass = EMxAdmin_mxJPO.valueOfByClass(this.mxUpdateType);
        if (mxClass == null)  {
            this.execBus(
                    _context.getParamCache(),
                    _context.getVarValue("NAME"),
                    _context.getVarValue("REVISION"),
                    _context.getVarValue("FILEDATE"),
                    new File(_context.getVarValue("FILENAME")),
                    _context.getVarValue("FILESUBPATH"));
        } else  {
            this.execAdmin(
                    _context.getParamCache(),
                    Boolean.valueOf(_context.getVarValue("CREATE")),
                    mxClass,
                    _context.getVarValue("NAME"),
                    _context.getVarValue("FILEDATE"),
                    new File(_context.getVarValue("FILENAME")),
                    _context.getVarValue("FILESUBPATH"));
        }
    }

    /**
     * Executes the update for given administration object.
     *
     * @param _paramCache   parameter cache
     * @param _create       must the admin object be created?
     * @param _mxClass      MX admin class
     * @param _mxName       MX name
     * @param _fileDate     last modified date of the file
     * @param _file         file
     * @param _fileSubPath  sub path of the file
     * @throws Exception if execute failed
     */
    private void execAdmin(final ParameterCache_mxJPO _paramCache,
                           final boolean _create,
                           final EMxAdmin_mxJPO _mxClass,
                           final String _mxName,
                           final String _fileDate,
                           final File _file,
                           final String _fileSubPath)
        throws Exception
    {
        final AbstractAdminObject_mxJPO<?> target  = _mxClass.newTypeInstance(_mxName);
        target.parseUpdate(_file, this.code);

        final AbstractAdminObject_mxJPO<?> current  = _mxClass.newTypeInstance(_mxName);
        current.parse(_paramCache);

        // MxUpdate File Date => must be always overwritten if newer!
        target.getProperties().setValue4KeyValue(_paramCache, PropertyDef_mxJPO.FILEDATE, _fileDate);

        // installed date => reuse if already defined, new is not
        final String curInstalledDate = current.getProperties().getValue4KeyValue(_paramCache, PropertyDef_mxJPO.INSTALLEDDATE);
        target.getProperties().setValue4KeyValue(
                _paramCache,
                PropertyDef_mxJPO.INSTALLEDDATE,
                ((curInstalledDate != null) && !curInstalledDate.isEmpty()) ? curInstalledDate : StringUtil_mxJPO.formatInstalledDate(_paramCache, new Date()));

        // installer
        // => check if already defined
        // => check if installed via parameter
        // => use default installer
        final String curInstaller = current.getProperties().getValue4KeyValue(_paramCache, PropertyDef_mxJPO.INSTALLER);
        target.getProperties().setValue4KeyValue(
                _paramCache,
                PropertyDef_mxJPO.INSTALLER,
                _paramCache.contains(ValueKeys.Installer)
                        ?_paramCache.getValueString(ValueKeys.Installer)
                        : ((curInstaller != null) && !curInstaller.isEmpty())
                                ? curInstaller
                                : _paramCache.getValueString(ValueKeys.DefaultInstaller));

        // set sub path always
        target.getProperties().setValue4KeyValue(_paramCache, PropertyDef_mxJPO.SUBPATH, _fileSubPath);

        // initialize MQL builder (with or w/o suffix!)
        final MultiLineMqlBuilder mql;
        if (target.mxClassDef().hasMxClassSuffix())  {
            mql = MqlBuilderUtil_mxJPO.multiLine(_file, "escape mod " + target.mxClassDef().mxClass() + " $1 " + target.mxClassDef().mxClassSuffix(), target.getName());
        } else  {
            mql = MqlBuilderUtil_mxJPO.multiLine(_file, "escape mod " + target.mxClassDef().mxClass() + " $1", target.getName());
        }

        if (_mxClass == EMxAdmin_mxJPO.Policy)  {
            ((Policy_mxJPO) target).setUpdateWithCreate(_create);
        }

        ((AbstractAdminObject_mxJPO) target).calcDelta(_paramCache, mql, current);

        mql.exec(_paramCache.getContext());
    }

    /**
     * Executes the update for given business  object {@code _mxName} 
     * {@code _mxRevision}.
     *
     * @param _paramCache   parameter cache
     * @param _mxName       MX name
     * @param _mxRevision   MX revision
     * @param _fileDate     last modified date of the file
     * @param _file         file
     * @param _fileSubPath  sub path of the file
     * @throws Exception if execute failed
     */
    private void execBus(final ParameterCache_mxJPO _paramCache,
                         final String _mxName,
                         final String _mxRevision,
                         final String _fileDate,
                         final File _file,
                         final String _fileSubPath)
        throws Exception
    {
        // evaluate type definition
        TypeDef_mxJPO typeDef = null;
        for (final TypeDef_mxJPO tmpTypeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
            if (tmpTypeDef.getMxUpdateType().equals(this.mxUpdateType))  {
                typeDef = tmpTypeDef;
                break;
            }
        }

        final BusObject_mxJPO target = (BusObject_mxJPO) typeDef.newTypeInstance(_mxName + BusObject_mxJPO.SPLIT_NAME + _mxRevision);
        target.parseUpdate(_file, this.code);

        final BusObject_mxJPO current = new BusObject_mxJPO(typeDef, target.getBusType(), target.getBusName(), target.getBusRevision());
        current.parse(_paramCache);

        // MxUpdate File Date => must be always overwritten if newer!
        final String attrFileDate = PropertyDef_mxJPO.FILEDATE.getAttrName(_paramCache);
        if ((attrFileDate != null) && !attrFileDate.isEmpty())  {
            target.getAttrValues().put(attrFileDate, _fileDate);
        }

        // installed date => reuse if already defined, new is not
        final String attrInstDate = PropertyDef_mxJPO.INSTALLEDDATE.getAttrName(_paramCache);
        if ((attrInstDate != null) && !attrInstDate.isEmpty())  {
            final String curInstalledDate = current.getAttrValues().get(attrInstDate);
            target.getAttrValues().put(
                    attrInstDate,
                    ((curInstalledDate != null) && !curInstalledDate.trim().isEmpty()) ? curInstalledDate : StringUtil_mxJPO.formatInstalledDate(_paramCache, new Date()));
        }

        // installer
        // => check if already defined
        // => check if installed via parameter
        // => use default installer
        final String attrInstaller = PropertyDef_mxJPO.INSTALLER.getAttrName(_paramCache);
        if ((attrInstaller != null) && !attrInstaller.isEmpty())  {
            final String curInstaller = current.getAttrValues().get(attrInstaller);
            target.getAttrValues().put(
                    attrInstaller,
                    _paramCache.contains(ValueKeys.Installer)
                            ? _paramCache.getValueString(ValueKeys.Installer)
                            : ((curInstaller != null) && !curInstaller.isEmpty())
                                    ? curInstaller
                                    : _paramCache.getValueString(ValueKeys.DefaultInstaller));
        }

        // calc sub path always
        final String attrSubPath = PropertyDef_mxJPO.SUBPATH.getAttrName(_paramCache);
        if ((attrSubPath != null) && !attrSubPath.isEmpty())  {
            target.getAttrValues().put(attrSubPath, _fileSubPath);
        }

        // attributes to ignore
        for (final String attrName : current.getTypeDef().getMxBusIgnoredAttributes())  {
            if ((current.getAttrValues().get(attrName) != null) && !current.getAttrValues().get(attrName).isEmpty())  {
                target.getAttrValues().put(attrName, current.getAttrValues().get(attrName));
            }
        }

        // initialize MQL builder
        final MultiLineMqlBuilder mql = MqlBuilderUtil_mxJPO.multiLine(_file, "escape mod bus $1 $2 $3", target.getBusType(), target.getBusName(), target.getBusRevision());

        target.calcDelta(_paramCache, mql, current);

        mql.exec(_paramCache.getContext());
    }


    /**
     * Defines the {@link #name} of the MxUpdate object.
     *
     * @param _name         name of te MxUpdate object to update
     * @return this statement instance
     */
    public MxUpdateStatement_mxJPO setName(final String _name)
    {
        this.name = _name;
        return this;
    }

    /**
     * Defines the {@link #revision} of the MxUpdate object.
     *
     * @param _revision     revision of the MxUpdate object to update
     * @return this statement instance
     */
    public MxUpdateStatement_mxJPO setRevision(final String _revision)
    {
        this.revision = _revision;
        return this;
    }

    /**
     * Defines the {@link #code} of the MxUpdate object.
     *
     * @param _code     code of the MxUpdate object to update
     * @return this statement instance
     */
    public MxUpdateStatement_mxJPO setCode(final String _code)
    {
        this.code = _code;
        return this;
    }

    @Override
    public boolean equals(final Object _compareTo)
    {
        boolean ret = _compareTo instanceof MxUpdateStatement_mxJPO;
        if (ret)  {
            final MxUpdateStatement_mxJPO compareTo = (MxUpdateStatement_mxJPO)  _compareTo;
            ret &= (this.mxUpdateType != null) ? this.mxUpdateType.equals(compareTo.mxUpdateType) : (compareTo.mxUpdateType == null);
            ret &= (this.name         != null) ? this.name.equals(compareTo.name)                 : (compareTo.name == null);
            ret &= (this.revision     != null) ? this.revision.equals(compareTo.revision)         : (compareTo.revision == null);
            ret &= (this.code         != null) ? this.code.equals(compareTo.code)                 : (compareTo.code == null);
        }
        return ret;
    }

    @Override
    public String toString()
    {
        return    "mxUpdateType = \"" + this.mxUpdateType + "\", "
                + "name = \"" + this.name + "\", "
                + "revision = \"" + this.revision + "\", "
                + "code = \"" + this.code + "\", ";
    }
}
