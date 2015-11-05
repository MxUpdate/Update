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

package org.mxupdate.mapping;

import java.util.Collection;

import org.mxupdate.action.DeleteAction_mxJPO;
import org.mxupdate.action.ExportAction_mxJPO;
import org.mxupdate.action.HelpAction_mxJPO;
import org.mxupdate.action.LicenseAction_mxJPO;
import org.mxupdate.action.SelectTypeDefUtil_mxJPO;
import org.mxupdate.action.UpdateAction_mxJPO;
import org.mxupdate.action.VersionAction_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Enumeration of all modes which are supported by the MxUpdate.
 *
 * @author The MxUpdate Team
 */
public enum Action_mxJPO
{
    /** Deletes in MX objects which are not defined in the repository (file system). */
    DELETE()  {
        @Override
        public void execute(final ParameterCache_mxJPO _paramCache,
                            final SelectTypeDefUtil_mxJPO _selects)
            throws Exception
        {
            new DeleteAction_mxJPO(_paramCache, _selects).execute();
        }
    },
    /** Exports defined administration objects from MX into a file system. */
    EXPORT()  {
        @Override
        public void execute(final ParameterCache_mxJPO _paramCache,
                            final SelectTypeDefUtil_mxJPO _selects)
            throws Exception
        {
            new ExportAction_mxJPO(_paramCache, _selects).execute();
        }
    },
    /** Prints out the help description. */
    HELP()  {
        @Override public void execute(final ParameterCache_mxJPO _paramCache,
                                      final SelectTypeDefUtil_mxJPO _selects)
            throws Exception
        {
            new HelpAction_mxJPO(_paramCache).execute();
        }
    },
    /** Prints out the license information. */
    LICENSE()  {
        @Override public void execute(final ParameterCache_mxJPO _paramCache,
                                      final SelectTypeDefUtil_mxJPO _selects)
            throws Exception
        {
            new LicenseAction_mxJPO(_paramCache).execute();
        }
    },
    /** Updates defined administration objects from file system into MX. */
    UPDATE()  {
        @Override
        public void execute(final ParameterCache_mxJPO _paramCache,
                            final SelectTypeDefUtil_mxJPO _selects)
            throws Exception
        {
            new UpdateAction_mxJPO(_paramCache, _selects).execute();
        }
    },
    /** Show informations about current installed MxUdpate version. */
    VERSION()  {
        @Override
        public void execute(final ParameterCache_mxJPO _paramCache,
                            final SelectTypeDefUtil_mxJPO _selects)
            throws Exception
        {
            new VersionAction_mxJPO(_paramCache).execute();
        }
    };

    /**
     * Defines the values of the mode enumerations.
     *
     * @param _mapping  cache for all mapping
     * @param _key      key with name of enumeration and (separated by a point)
     *                  the key
     * @param _value    value which must be set
     * @throws Exception if the key is not known
     * @see AbstractValue_mxJPO
     */
    protected static void defineValue(final Mapping_mxJPO _mapping,
                                      final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "").toUpperCase();
        final String key = _key.substring(enumName.length() + 1);

        final Action_mxJPO modeEnum = Action_mxJPO.valueOf(enumName);
        AbstractValue_mxJPO mode = _mapping.getModeMap().get(modeEnum);
        if (mode == null)  {
            mode = new AbstractValue_mxJPO(enumName);
            _mapping.getModeMap().put(modeEnum, mode);
        }

        mode.defineValues(key, _value);
    }

    /**
     * Returns the description of parameters which defines mode.
     *
     * @param _paramCache   for which parameter cache must the parameter
     *                      description returned
     * @return description of parameter
     * @see AbstractValue_mxJPO#paramDesc
     */
    public String getParameterDesc(final ParameterCache_mxJPO _paramCache)
    {
        return _paramCache.getMapping().getModeMap().get(this).getParameterDesc();
    }

    /**
     * Returns the list of parameters which defines this mode.
     *
     * @param _paramCache   for which parameter cache must the parameter
     *                      list returned
     * @return list of parameter strings
     * @see AbstractValue_mxJPO#paramList
     */
    public Collection<String> getParameterList(final ParameterCache_mxJPO _paramCache)
    {
        return _paramCache.getMapping().getModeMap().get(this).getParameterList();
    }

    /**
     * Executes this action.
     *
     * @param _paramCache   parameter cache
     * @param _selects      selected matched files
     * @throws Exception if execute failed
     */
    public abstract void execute(final ParameterCache_mxJPO _paramCache,
                                 final SelectTypeDefUtil_mxJPO _selects)
        throws Exception;
}
