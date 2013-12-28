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

package org.mxupdate.test.data.user.workspace;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AbstractUserData;

/**
 * The class is used to define all cue objects related to users used to create
 * / update and to export.
 *
 * @author The MxUpdate Team
 * @param <USER> class of the related user for which this cue is defined
 */
public class CueData<USER extends AbstractUserData<?>>
    extends AbstractVisualQueryWorkspaceObjectData<CueData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>(3);
    static  {
        CueData.REQUIRED_EXPORT_VALUES.put("user", "");
        CueData.REQUIRED_EXPORT_VALUES.put("appliesto", "");
        CueData.REQUIRED_EXPORT_VALUES.put("type", "");
        CueData.REQUIRED_EXPORT_VALUES.put("name", "");
        CueData.REQUIRED_EXPORT_VALUES.put("revision", "");
        CueData.REQUIRED_EXPORT_VALUES.put("vault", "");
        CueData.REQUIRED_EXPORT_VALUES.put("owner", "");
    }

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this cue is defined
     * @param _name     name of the cue
     */
    public CueData(final AbstractTest _test,
                   final USER _user,
                   final String _name)
    {
        super(_test, "cue", _user, _name, CueData.REQUIRED_EXPORT_VALUES);
        this.setValue("appliesto", "all");
    }
}
