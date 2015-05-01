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

package org.mxupdate.test.data.user;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * The class is used to define all association objects used to create / update
 * and to export.
 *
 * @author The MxUpdate Team
 */
public class AssociationData
    extends AbstractAdminData<AssociationData>
{
    /**
     * Constructor to initialize this association.
     *
     * @param _test     related test implementation (where this association is
     *                  defined)
     * @param _name     name of the association
     */
    public AssociationData(final AbstractTest _test,
                           final String _name)
    {
        super(_test, AbstractTest.CI.USR_ASSOCIATION, _name);
    }
}
