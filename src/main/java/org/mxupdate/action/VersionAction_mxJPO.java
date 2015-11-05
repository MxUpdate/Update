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

import java.text.MessageFormat;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

/**
 * Prints out the MxUpdate version information.
 *
 * @author The MxUpdate Team
 */
public class VersionAction_mxJPO
{
    /** Parameter cache. */
    private final ParameterCache_mxJPO paramCache;

    /**
     * Initializes the action.
     *
     * @param _paramCache   parameter cache
     */
    public VersionAction_mxJPO(final ParameterCache_mxJPO _paramCache)
    {
        this.paramCache = _paramCache;
    }

    /**
     * Executes the action.
     *
     * @throws Exception if execute failed
     */
    public void execute()
        throws Exception
    {
        final String applName = this.paramCache.getValueString(ValueKeys.RegisterApplicationName);
        final String progName = this.paramCache.getValueString(ValueKeys.RegisterApplicationProg);
        final String infoText = this.paramCache.getValueString(ValueKeys.VersionActionInfoText);

        final String curVers = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape print program ").arg(progName).cmd(" ")
                        .cmd("select ").arg("property[appVersion" + applName + "].value").cmd(" dump")
                        .exec(this.paramCache.getContext());

        System.out.println(MessageFormat.format(infoText, curVers));
    }
}
