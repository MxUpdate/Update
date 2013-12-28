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

package org.mxupdate.test.ci.user;

import org.mxupdate.test.data.user.PersonAdminData;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of administration persons (without
 * related business object).
 *
 * @author The MxUpdate Team
 */
@Test()
public class PersonAdminTest
    extends AbstractPersonAdminTest<PersonAdminData>
{
    /**
     * Creates for given <code>_name</code> a new person instance.
     *
     * @param _name     name of the person instance
     * @return person instance
     */
    @Override()
    protected PersonAdminData createNewData(final String _name)
    {
        return new PersonAdminData(this, _name);
    }
}
