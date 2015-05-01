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

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.UpdateUtil_mxJPO;

/**
 * Implements the update action used within MxUpdate.
 *
 * @author The MxUpdate Team
 */
public class UpdateAction_mxJPO
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
    public UpdateAction_mxJPO(final ParameterCache_mxJPO _paramCache,
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
        UpdateUtil_mxJPO.update(this.paramCache, this.selects.evalMatches(this.paramCache));
    }
}
