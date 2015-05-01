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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AbstractUserData;

/**
 * The class is used to define all common things for query workspace objects
 * related to users used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <DATA> workspace object class
 * @param <USER> for which user class the workspace object class is defined
 */
abstract class AbstractQueryWorkspaceObjectData<DATA extends AbstractQueryWorkspaceObjectData<?,USER>, USER extends AbstractUserData<?>>
    extends AbstractWorkspaceObjectData<DATA,USER>
{
    /**
     * Default constructor.
     *
     * @param _test                 related test case
     * @param _mxAdminType          MX administration type of the visual query
     *                              workspace object
     * @param _user                 user for which this visual query workspace
     *                              object is defined
     * @param _name                 name of the visual query workspace object
     */
    AbstractQueryWorkspaceObjectData(final AbstractTest _test,
                                     final String _mxAdminType,
                                     final USER _user,
                                     final String _name)
    {
        super(_test, _mxAdminType, _user, _name);
        this.setValue("type", "*");
        this.setValue("name", "*");
        this.setValue("revision", "*");
        this.setValue("vault", "*");
        this.setValue("owner", "*");
    }
}
