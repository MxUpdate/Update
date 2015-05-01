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

package org.mxupdate.test.test.update.user;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.test.update.AbstractDeltaNoChangeTest;
import org.mxupdate.update.user.PersonAdmin_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PersonAdmin_mxJPO person admin CI} delta calculation for
 * default values.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PersonAdminCI_2DeltaDefaultTest
    extends AbstractDeltaNoChangeTest<PersonAdmin_mxJPO,PersonAdminData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0) simple w/o values",
                    new PersonAdminData(this, "Test")},
            {"1a) with all access",
                    new PersonAdminData(this, "Test").setSingle("access", "all")},
            {"1b) with none access",
                    new PersonAdminData(this, "Test").setSingle("access", "{none}")},
            {"2a) with business type (to check admin access)",
                    new PersonAdminData(this, "Test").setSingle("type", "{business}")},
            {"2b) with system type (to check admin access)",
                    new PersonAdminData(this, "Test").setSingle("type", "{system}")},
            {"2c) with none admin access",
                    new PersonAdminData(this, "Test").setSingle("admin", "{none}")},
       };
    }

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
    }

    @Override()
    protected PersonAdmin_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new PersonAdmin_mxJPO(_paramCache.getMapping().getTypeDef(CI.USR_PERSONADMIN.updateType), _name);
    }
}
