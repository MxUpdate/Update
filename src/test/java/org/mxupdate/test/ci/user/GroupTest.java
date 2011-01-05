/*
 * Copyright 2008-2011 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test.ci.user;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.other.SiteData;
import org.mxupdate.test.data.user.GroupData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of groups.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test()
public class GroupTest
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
                        "simple group",
                        new GroupData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "group with two parent groups",
                        new GroupData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParents(new GroupData(this, "hallo parent1 \" test"))
                                .assignParents(new GroupData(this, "hallo parent2 \" test"))},
                new Object[]{
                        "group with assigned site",
                        new GroupData(this, "hallo \" test")
                                .setSite(new SiteData(this, "Test \" Site"))});
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

    /**
     * Cleanup all test persons.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.OTHER_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
    }

    /**
     * Test an update of a group where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing group with site by removing site")
    public void checkExistingSiteRemovedWithinUpdate()
        throws Exception
    {
        final GroupData group = new GroupData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        group.create();
        group.setSite(null);
        group.update();

        Assert.assertEquals(this.mql("escape print group \"" + AbstractTest.convertMql(group.getName()) + "\" select site dump"),
                            "",
                            "check that no site is defined");
    }

    /**
     * Check that the hidden flag is removed within update.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the hidden flag is removed within update")
    public void checkHiddenFlagRemoveWithinUpdate()
        throws Exception
    {
        final GroupData group = new GroupData(this, "hallo \" test").setFlag("hidden", true);
        group.create();
        group.setFlag("hidden", null);
        group.update();
        Assert.assertEquals(this.mql("escape print group \"" + AbstractTest.convertMql(group.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that group is not hidden");
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
