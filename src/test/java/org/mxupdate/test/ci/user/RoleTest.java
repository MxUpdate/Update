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
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.other.SiteData;
import org.mxupdate.test.data.user.RoleData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of roles.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test()
public class RoleTest
    extends AbstractUserTest<RoleData>
{
    /**
     * Creates for given <code>_name</code> a new role instance.
     *
     * @param _name     name of the role instance
     * @return role instance
     */
    @Override()
    protected RoleData createNewData(final String _name)
    {
        return new RoleData(this, _name);
    }

    /**
     * Data provider for test roles.
     *
     * @return object array with all test roles
     */
    @DataProvider(name = "data")
    public Object[][] getRoles()
    {
        return this.prepareData("role",
                new Object[]{
                        "simple role",
                        new RoleData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "simple organizational role",
                        new RoleData(this, "hallo \" test")
                            .setRoleType(RoleData.RoleType.ORGANIZATION)
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "simple project role",
                        new RoleData(this, "hallo \" test")
                            .setRoleType(RoleData.RoleType.PROJECT)
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "role with two parent roles",
                        new RoleData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParents(new RoleData(this, "hallo parent1 \" test"))
                                .assignParents(new RoleData(this, "hallo parent2 \" test"))},
                new Object[]{
                        "role with assigned site",
                        new RoleData(this, "hallo \" test")
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
                new Object[]{"UserIgnoreWSO4Roles", true},
                new Object[]{"UserIgnoreWSO4Groups", false}
        };
    }

    /**
     * Cleanup all test roles.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.OTHER_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
    }

    /**
     * Test an update of a role where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing role with site by removing site")
    public void checkExistingSiteRemovedWithinUpdate()
        throws Exception
    {
        final RoleData role = new RoleData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        role.create();
        role.setSite(null);
        role.update();

        Assert.assertEquals(this.mql("escape print role \"" + AbstractTest.convertMql(role.getName()) + "\" select site dump"),
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
        final RoleData role = new RoleData(this, "hallo \" test").setFlag("hidden", true);
        role.create()
            .setFlag("hidden", null)
            .update();
        Assert.assertEquals(this.mql("escape print role \"" + AbstractTest.convertMql(role.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that role is not hidden");
    }

    /**
     * Check that an update of an organizational role with child organizational
     * role works.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that an update of an organizational role with child organizational role works")
    public void checkOrgRoleWithChildOrgRole()
        throws Exception
    {
        final RoleData parent = new RoleData(this, "parent")
                .setRoleType(RoleData.RoleType.ORGANIZATION);
        final RoleData child = new RoleData(this, "child")
                .setRoleType(RoleData.RoleType.ORGANIZATION)
                .assignParents(parent);
        child.create();

        final ExportParser exportParser = parent.export();
        parent.checkExport(exportParser);

        // second update with delivered content
        parent.updateWithCode(exportParser.getOrigCode())
              .checkExport();
    }
}
