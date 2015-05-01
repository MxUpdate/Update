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

package org.mxupdate.action;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;

/**
 * Implements the export action used within MxUpdate.
 *
 * @author The MxUpdate Team
 */
public class ExportAction_mxJPO
{
    /** Parameter cache. */
    private final ParameterCache_mxJPO paramCache;
    /** Selected files / CI objects. */
    private final SelectTypeDefUtil_mxJPO selects;

    /**
     * Initializes the action.
     *
     * @param _paramCache   parameter cache
     * @param _selects      selected matched files
     */
    public ExportAction_mxJPO(final ParameterCache_mxJPO _paramCache,
                              final SelectTypeDefUtil_mxJPO _selects)
    {
        this.paramCache = _paramCache;
        this.selects = _selects;
    }

    /**
     * Executes the action.
     *
     * @throws Exception if execute failed
     */
    public void execute()
        throws Exception
    {
        final Collection<String> paths = this.paramCache.getValueList(ValueKeys.Path);

        // check for definition of min. / max. one path
        if (paths.isEmpty())  {
            throw new Exception("no path is defined, but required for the export!");
        }
        if (paths.size() > 1)  {
            throw new Exception("more than one path is defined, but maximum is allowed for the export!");
        }
        final String pathStr = paths.iterator().next();

        // evaluate all matching administration objects
        final Map<TypeDef_mxJPO,Set<String>> clazz2names = this.selects.getMatching(this.paramCache);

        // export
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2names.entrySet())  {
            for (final String name : entry.getValue())  {
                final AbstractObject_mxJPO instance = entry.getKey().newTypeInstance(name);
                this.paramCache.logInfo("export "+instance.getTypeDef().getLogging() + " '" + name + "'");
                instance.export(this.paramCache, new File(pathStr + File.separator + instance.getPath()));
            }
        }
    }
}
