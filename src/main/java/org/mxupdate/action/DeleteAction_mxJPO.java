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
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;

/**
 * Implements the delete action used within MxUpdate.
 *
 * @author The MxUpdate Team
 */
public class DeleteAction_mxJPO
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
    public DeleteAction_mxJPO(final ParameterCache_mxJPO _paramCache,
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
        // check for definition of min. / max. one path
        if (this.paramCache.getValueList(ValueKeys.Path).isEmpty())  {
            throw new Exception("no path is defined, but required for the delete!");
        }

        // evaluate all matching administration objects
        final Map<TypeDef_mxJPO,Set<String>> clazz2MxNames = this.selects.getMatching(this.paramCache);

        // get all matching files depending on the update classes
        final Map<TypeDef_mxJPO,Map<String,File>> clazz2FileNames = this.selects.evalMatches(this.paramCache);

        // and now loop throw the list of file names and compare to existing
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2MxNames.entrySet())  {
            final Set<String> mxNames = clazz2FileNames.containsKey(entry.getKey()) ? clazz2FileNames.get(entry.getKey()).keySet() : null;
            for (final String name : entry.getValue())  {
                if ((mxNames == null) || !mxNames.contains(name))  {
                    this.paramCache.logInfo("delete " + entry.getKey().getLogging() + " '" + name + "'");
                    boolean commit = false;
                    final boolean transActive = this.paramCache.getContext().isTransactionActive();
                    try  {
                        if (!transActive)  {
                            this.paramCache.getContext().start(true);
                        }
                        entry.getKey().newTypeInstance(name).delete(this.paramCache);
                        if (!transActive)  {
                            this.paramCache.getContext().commit();
                        }
                        commit = true;
                    } finally  {
                        if (!commit && !transActive && this.paramCache.getContext().isTransactionActive())  {
                            this.paramCache.getContext().abort();
                        }
                    }
                }
            }
        }
    }
}
