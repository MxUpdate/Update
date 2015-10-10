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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.system.SiteData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.user.Group_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Group_mxJPO group CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class GroupCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Group_mxJPO,GroupData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) symbolic name",
                    new GroupData(this, "Test"),
                    new GroupData(this, "Test").setValue("symbolicname", "group_123")},
            {"1b) two symbolic name",
                    new GroupData(this, "Test"),
                    new GroupData(this, "Test").setValue("symbolicname", "group_123").setValue("symbolicname", "group_345")},
            {"2) description",
                    new GroupData(this, "Test"),
                    new GroupData(this, "Test").setValue("description", "abc def")},
            {"4) group with assigned site",
                    new GroupData(this, "Test"),
                    new GroupData(this, "Test")
                            .defData("site", new SiteData(this, "Test \" Site"))},
       };
    }

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.SYS_SITE);
    }

    @Override
    protected Group_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Group_mxJPO(_name);
    }
}
