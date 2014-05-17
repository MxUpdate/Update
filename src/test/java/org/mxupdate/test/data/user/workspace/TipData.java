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
 * The class is used to define all tip objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <USER> class of the related user for which this tip is defined
 */
public class TipData<USER extends AbstractUserData<?>>
    extends AbstractVisualQueryWorkspaceObjectData<TipData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        TipData.REQUIRED_EXPORT_VALUES.put("user", "");
        TipData.REQUIRED_EXPORT_VALUES.put("appliesto", "");
        TipData.REQUIRED_EXPORT_VALUES.put("type", "");
        TipData.REQUIRED_EXPORT_VALUES.put("name", "");
        TipData.REQUIRED_EXPORT_VALUES.put("revision", "");
        TipData.REQUIRED_EXPORT_VALUES.put("vault", "");
        TipData.REQUIRED_EXPORT_VALUES.put("owner", "");
    }

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this tip is defined
     * @param _name     name of the tip
     */
    public TipData(final AbstractTest _test,
                   final USER _user,
                   final String _name)
    {
        super(_test, "tip", _user, _name, TipData.REQUIRED_EXPORT_VALUES);
        this.setValue("appliesto", "relationship");
    }
}
