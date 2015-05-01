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
import org.mxupdate.test.ci.user.AbstractUserTest;
import org.mxupdate.test.data.system.SiteData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.update.user.Group_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Group_mxJPO group CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class GroupCI_3UpdateTest
    extends AbstractUserTest<GroupData>
{
    /**
     * Data provider for test groups.
     *
     * @return object array with all test groups
     */
    @DataProvider(name = "data")
    public Object[][] getGroups()
    {
        return this.prepareData("group",
                new Object[]{
                        "0a) group without anything",
                        new GroupData(this, "Test")},
                new Object[]{
                        "0b) group without anything (to test required fields)",
                        new GroupData(this, "Test")
                                .setValue("description", "")
                                .setFlag("hidden", false),
                        new GroupData(this, "Test")},
                new Object[]{
                        "0c) group with escaped name",
                        new GroupData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "1) group with two parent groups",
                        new GroupData(this, "test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParents(new GroupData(this, "hallo parent1 \" test"))
                                .assignParents(new GroupData(this, "hallo parent2 \" test"))},
                new Object[]{
                        "2) group with assigned site",
                        new GroupData(this, "test")
                                .defData("site", new SiteData(this, "Test \" Site"))});
    }


    /**
     * Returns the mapping if for given parameter the workspace objects must be
     * ignored to remove or not.
     *
     * @return map between the parameter and the workspace objects are ignored
     *         to removed
     */
    @DataProvider(name = "wsoParameters")
    public Object[][] getWSOParameters()
    {
        return new Object[][]{
                new Object[]{"UserIgnoreWSO4Users", true},
                new Object[]{"UserIgnoreWSO4Persons", false},
                new Object[]{"UserIgnoreWSO4Roles", false},
                new Object[]{"UserIgnoreWSO4Groups", true}
        };
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.SYS_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
    }

    /**
     * Creates for given <code>_name</code> a new group instance.
     *
     * @param _name     name of the group instance
     * @return group instance
     */
    @Override()
    protected GroupData createNewData(final String _name)
    {
        return new GroupData(this, _name);
    }
}
