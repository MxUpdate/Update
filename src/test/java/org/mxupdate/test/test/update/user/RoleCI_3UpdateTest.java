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
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.update.user.Role_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Role_mxJPO role CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class RoleCI_3UpdateTest
    extends AbstractUserTest<RoleData>
{
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
                        "0a) role without anything",
                        new RoleData(this, "Test")},
                new Object[]{
                        "0b) role without anything (to test required fields)",
                        new RoleData(this, "Test")
                                .setValue("description", "")
                                .setFlag("hidden", false),
                        new RoleData(this, "Test")},
                new Object[]{
                        "0c) role with escaped name",
                        new RoleData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "1) role with two parent groups",
                        new RoleData(this, "test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParents(new RoleData(this, "hallo parent1 \" test"))
                                .assignParents(new RoleData(this, "hallo parent2 \" test"))},
                new Object[]{
                        "2) group with assigned site",
                        new RoleData(this, "test")
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
                new Object[]{"UserIgnoreWSO4Roles", true},
                new Object[]{"UserIgnoreWSO4Groups", false}
        };
    }

    /**
     * Positive test for kind organization.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for kind organization")
    public void t3a_positiveTestKindOrganization()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "organization")
                .update("")
                .checkExport();
    }

    /**
     * Positive test for kind organization.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for kind project")
    public void t3b_positiveTestKindProject()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "project")
                .update("")
                .checkExport();
    }

    /**
     * Negative test if the kind is changed from organization to project.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if the kind is changed from organization to project")
    public void t3c_negativeTestChangeKindOrganizationToProject()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "organization")
                .update("")
                .checkExport();
        this.createNewData("Test")
                .setSingle("kind", "project")
                .failureUpdate(ErrorKey.USER_ROLE_NOT_ROLE_KIND);
        this.createNewData("Test")
                .setSingle("kind", "organization")
                .checkExport();
    }

    /**
     * Negative test if the kind is changed from organization to role.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if the kind is changed from organization to role")
    public void t3d_negativeTestChangeKindOrganizationToRole()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "organization")
                .update("")
                .checkExport();
        this.createNewData("Test")
                .setSingle("kind", "role")
                .failureUpdate(ErrorKey.USER_ROLE_NOT_ROLE_KIND);
        this.createNewData("Test")
                .setSingle("kind", "organization")
                .checkExport();
    }

    /**
     * Negative test if the kind is changed back from project to organization.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if the kind is changed from project to organization")
    public void t3e_negativeTestChangeKindProjectToOrganization()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "project")
                .update("")
                .checkExport();
        this.createNewData("Test")
                .setSingle("kind", "organization")
                .failureUpdate(ErrorKey.USER_ROLE_NOT_ROLE_KIND);
        this.createNewData("Test")
                .setSingle("kind", "project")
                .checkExport();
    }

    /**
     * Negative test if the kind is changed from project to role.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if the kind is changed from project to role")
    public void t3f_negativeTestChangeKindProjectToRole()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "project")
                .update("")
                .checkExport();
        this.createNewData("Test")
                .setSingle("kind", "role")
                .failureUpdate(ErrorKey.USER_ROLE_NOT_ROLE_KIND);
        this.createNewData("Test")
                .setSingle("kind", "project")
                .checkExport();
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.SYS_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL);
    }

    @Override()
    protected RoleData createNewData(final String _name)
    {
        return new RoleData(this, _name);
    }
}
