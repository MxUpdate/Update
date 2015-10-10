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

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.ci.user.AbstractUserTest;
import org.mxupdate.test.data.system.SiteData;
import org.mxupdate.test.data.user.AbstractPersonAdminData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.RoleData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of administration persons (without
 * related business object).
 *
 * @author The MxUpdate Team
 * @param <PERSON> person class
 */
public abstract class AbstractPersonAdminCITest<PERSON extends AbstractPersonAdminData<PERSON>>
    extends AbstractUserTest<PERSON>
{
    /**
     * Data provider for test persons.
     *
     * @return object array with all test persons
     */
    @DataProvider(name = "data")
    public Object[][] getPersons()
    {
        return this.prepareData("person",
                new Object[]{
                        "1) simple person",
                        this.createNewData("hello \" test")
                                .setValue("comment", "hallo")},
                new Object[]{
                        "2a) person with some access",
                        this.createNewData("hello \" test")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "2b) person with normal access but specific changeName / changevault access but no business access",
                        this.createNewData("hello \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "2c) person with normal access but non specific access but no business access",
                        this.createNewData("hello \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")},
                new Object[]{
                        "3a) person as business administration for types",
                        this.createNewData("hello \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type")},
                new Object[]{
                        "3b) person as business administration for types and vaults",
                        this.createNewData("hello \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type", "vault")},
                new Object[]{
                        "4a) person with one group",
                        this.createNewData("hello \" test")
                                .addGroup(new GroupData(this, "test \"group\""))},
                new Object[]{
                        "4b) person with two groups",
                        this.createNewData("hello \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))},
                new Object[]{
                        "5a) person with one role",
                        this.createNewData("hello \" test")
                                .addRole(new RoleData(this, "test \"role\""))},
                new Object[]{
                        "5b) person with two roles",
                        this.createNewData("hello \" test")
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},
                new Object[]{
                        "5c) person with two groups and roles",
                        this.createNewData("hello \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},

                new Object[]{
                        "6a) person with assigned site",
                        this.createNewData("hello \" test")
                                .defData("site", new SiteData(this, "Test \" Site"))},
                new Object[]{
                        "7) complex person",
                        this.createNewData("hello \" test")
                                .setValue("fullname", "test person \"full name\"")
                                .setValue("comment", "test person \"comment\"")
                                .setValue("address", "test person \"address\"")
                                .setValue("phone", "test person \"phone\"")},
                new Object[]{
                        "8a) person no password expires flag defined",
                        this.createNewData("hello \" test")
                                .setPasswordNeverExpires(null)},
                new Object[]{
                        "8b) person with password expires flag defined to true",
                        this.createNewData("hello \" test")
                                .setPasswordNeverExpires(true)},
                new Object[]{
                        "8c) person with password expires flag defined to false",
                        this.createNewData("hello \" test")
                                .setPasswordNeverExpires(false)},
                new Object[]{
                        "9a) person with no wants email flag",
                        this.createNewData("hello \" test")
                                .setWantsEmail(null)},
                new Object[]{
                        "9b) person with wants email flag to true",
                        this.createNewData("hello \" test")
                                .setWantsEmail(true)},
                new Object[]{
                        "9c) person with wants email flag to false",
                        this.createNewData("hello \" test")
                                .setWantsEmail(false)},
                new Object[]{
                        "10a) person with no wants icon mail flag",
                        this.createNewData("hello \" test")
                                .setWantsIconMail(null)},
                new Object[]{
                        "10b) person with wants icon mail flag to true",
                        this.createNewData("hello \" test")
                                .setWantsIconMail(true)},
                new Object[]{
                        "10c) person with wants icon mail flag to false",
                        this.createNewData("hello \" test")
                                .setWantsIconMail(false)},

                new Object[]{
                        "11a) person with one product",
                        this.createNewData("hello \" test")
                                .addProduct("CPF")},
                new Object[]{
                        "11b) person with two products",
                        this.createNewData("hello \" test")
                                .addProduct("CPF", "DC2")}
        );
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
                new Object[]{"UserIgnoreWSO4Persons", true},
                new Object[]{"UserIgnoreWSO4Roles", false},
                new Object[]{"UserIgnoreWSO4Groups", false}
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
        this.cleanup(AbstractTest.CI.USR_PERSON);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.SYS_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL);
    }

    /**
     * Test an update of a person where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing person with site by removing site")
    public void checkExistingSiteRemovedWithinUpdate()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hallo \" test")
                .defData("site", new SiteData(this, "Test \" Site"))
                .create()
                .update((String) null);

        Assert.assertEquals(this.mql("escape print person \""
                                    + AbstractTest.convertMql(person.getName())
                                    + "\" select site dump"),
                            "",
                            "check that no site is defined");
    }

    /**
     * Check that the wants email flag is removed within update.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the wants email flag is removed within update")
    public void checkWantsEmailFlagRemoveWithinUpdate()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setWantsEmail(true)
                .create()
                .setWantsEmail(null)
                .update((String) null);

        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select emailenabled dump"),
                            "FALSE",
                            "check that 'wants email' flag is disabled");
    }

    /**
     * Checks that 'the wants email' - flag is not reset if the
     * parameter 'UserPersonIgnoreWantsEmail' is defined.
     *
     * @throws Exception if check failed
     */
    @Test(description = "checks that the 'wants email' - flag is not reset if the parameter 'UserPersonIgnoreWantsEmail' is defined")
    public void checkWithIgnoreParameterWantsEmailFlag()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setWantsEmail(true)
                .create()
                .setWantsEmail(null)
                .update((String) null, "UserPersonIgnoreWantsEmail", "*");

        // check not updated
        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select emailenabled dump"),
                            "TRUE",
                            "check that 'wants email' flag is enabled");
        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonIgnoreWantsEmail", "*");
        final Set<String> enabled = new HashSet<String>(exportParser.getLines("/mql/enable/@value"));
        final Set<String> disabled = new HashSet<String>(exportParser.getLines("/mql/disable/@value"));
        Assert.assertFalse(enabled.contains("email"), "check email not enabled");
        Assert.assertFalse(disabled.contains("email"), "check email not disabled");
    }

    /**
     * Check that the wants email flag is removed within update.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the wants email flag is set within update")
    public void checkWantsIconMailFlagSetWithinUpdate()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setWantsIconMail(false)
                .create()
                .setWantsIconMail(null)
                .update((String) null);

        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select iconmailenabled dump"),
                            "TRUE",
                            "check that 'wants icon mail' flag is enabled");
    }

    /**
     * Checks that 'the wants icon mail' - flag is not reset if the
     * parameter 'UserPersonIgnoreWantsIconMail' is defined.
     *
     * @throws Exception if check failed
     */
    @Test(description = "checks that the 'wants icon mail' - flag is not reset if the parameter 'UserPersonIgnoreWantsIconMail' is defined")
    public void checkWithIgnoreParameterWantsIconMailFlag()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setWantsIconMail(false)
                .create()
                .setWantsIconMail(null)
                .update((String) null, "UserPersonIgnoreWantsIconMail", "*");

        // check not updated
        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select iconmailenabled dump"),
                            "FALSE",
                            "check that 'wants icon mail' flag is disabled");
        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonIgnoreWantsIconMail", "*");
        final Set<String> enabled = new HashSet<String>(exportParser.getLines("/mql/enable/@value"));
        final Set<String> disabled = new HashSet<String>(exportParser.getLines("/mql/disable/@value"));
        Assert.assertFalse(enabled.contains("iconmail"), "check icon mail not enabled");
        Assert.assertFalse(disabled.contains("iconmail"), "check icon mail not disabled");
    }

    /**
     * Checks that products which are assigned in TCL update file are correct
     * removed from the 'setProducts' method.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that products which are assigned in TCL update file are correct removed from the 'setProducts' procedure")
    public void checkUpdateProduct()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("test")
                .create();
        person  .updateWithCode("mql mod product CPF add person " + person.getName() + "\nsetProducts", (String) null)
                .checkExport();
    }

    /**
     * Checks that products which are remove in TCL update file are correct
     * set from the 'setProducts' method.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that products which are remove in TCL update file are correct set from 'setProducts' procedure")
    public void checkUpdateProductOnEmptyAssigned()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("test")
                .addProduct("CPF")
                .create();
        person  .updateWithCode("mql mod product CPF remove person " + person.getName() + "\nsetProducts \"CPF\"", (String) null)
                .checkExport();
    }

    /**
     * Checks that assigned products of persons are ignored if the parameter is
     * defined.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that assigned products of persons are ignored if the parameter is defined")
    public void _positiveTestIgnoreProductsParameter()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("test")
                .addProduct("CPF")
                .create()
                .setWantsIconMail(null);
        this.mql("escape mod product CPF remove person \"" + AbstractTest.convertMql(person.getName()) + "\"");
        person.update((String) null, "UserPersonIgnoreProducts", "*");

        // check not updated
        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select product dump"),
                            "",
                            "check that no product is assigned");
        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonIgnoreProducts", "*");
        Assert.assertTrue(
                exportParser.getLines("/setProducts/@value").isEmpty(),
                "check that no product definition is in the export file");
    }
}
