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

package org.mxupdate.test.data.user.organization;

import org.mxupdate.test.AbstractTest;

/**
 * Used to handle department data for testing persons with business object.
 *
 * @author The MxUpdate Team
 */
public class DepartmentData
    extends AbstractOrganizationalData<DepartmentData>
{
    /**
     * Constructor to initialize this department.
     *
     * @param _test     related test implementation
     * @param _name     name of the department
     */
    public DepartmentData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.OTHER_DEPARTMENT, _name);
    }
}
