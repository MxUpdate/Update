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

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.other.SiteData;
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
public abstract class AbstractPersonAdminTest<PERSON extends AbstractPersonAdminData<PERSON>>
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
                        "simple person",
                        this.createNewData("hello \" test")
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "person with some access",
                        this.createNewData("hello \" test")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "person with normal access but specific changeName / changevault access but no business access",
                        this.createNewData("hello \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "person with normal access but non specific access but no business access",
                        this.createNewData("hello \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")},
                new Object[]{
                        "person as business administration for types",
                        this.createNewData("hello \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type")},
                new Object[]{
                        "person as business administration for types and vaults",
                        this.createNewData("hello \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type", "vault")},
                new Object[]{
                        "person with one group",
                        this.createNewData("hello \" test")
                                .addGroup(new GroupData(this, "test \"group\""))},
                new Object[]{
                        "person with two groups",
                        this.createNewData("hello \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))},
                new Object[]{
                        "person with one role",
                        this.createNewData("hello \" test")
                                .addRole(new RoleData(this, "test \"role\""))},
                new Object[]{
                        "person with two roles",
                        this.createNewData("hello \" test")
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},
                new Object[]{
                        "person with two groups and roles",
                        this.createNewData("hello \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},

                new Object[]{
                        "person with assigned site",
                        this.createNewData("hello \" test")
                                .setSite(new SiteData(this, "Test \" Site"))},
                new Object[]{
                        "complex person",
                        this.createNewData("hello \" test")
                                .setValue("fullname", "test person \"full name\"")
                                .setValue("description", "test person \"comment\"")
                                .setValue("address", "test person \"address\"")
                                .setValue("email", "test person \"email\" address")
                                .setValue("phone", "test person \"phone\"")},
                new Object[]{
                        "person no password expires flag defined",
                        this.createNewData("hello \" test")
                                .setPasswordNeverExpires(null)},
                new Object[]{
                        "person with password expires flag defined to true",
                        this.createNewData("hello \" test")
                                .setPasswordNeverExpires(true)},
                new Object[]{
                        "person with password expires flag defined to false",
                        this.createNewData("hello \" test")
                                .setPasswordNeverExpires(false)},
                new Object[]{
                        "person with no wants email flag",
                        this.createNewData("hello \" test")
                                .setWantsEmail(null)},
                new Object[]{
                        "person with wants email flag to true",
                        this.createNewData("hello \" test")
                                .setWantsEmail(true)},
                new Object[]{
                        "person with wants email flag to false",
                        this.createNewData("hello \" test")
                                .setWantsEmail(false)},
                new Object[]{
                        "person with no wants icon mail flag",
                        this.createNewData("hello \" test")
                                .setWantsIconMail(null)},
                new Object[]{
                        "person with wants icon mail flag to true",
                        this.createNewData("hello \" test")
                                .setWantsIconMail(true)},
                new Object[]{
                        "person with wants icon mail flag to false",
                        this.createNewData("hello \" test")
                                .setWantsIconMail(false)},

                new Object[]{
                        "person with one product",
                        this.createNewData("hello \" test")
                                .addProduct("CPF")},
                new Object[]{
                        "person with two products",
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
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.OTHER_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
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
                .setSite(new SiteData(this, "Test \" Site"))
                .create()
                .setSite(null)
                .update();

        Assert.assertEquals(this.mql("escape print person \""
                                    + AbstractTest.convertMql(person.getName())
                                    + "\" select site dump"),
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
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setFlag("hidden", true)
                .create()
                .setFlag("hidden", null)
                .update();

        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that person is not hidden");
    }

    /**
     * Check that the password never expires flag is removed within update.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the hidden flag is removed within update")
    public void checkPasswordNeverExpiresFlagRemoveWithinUpdate()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setPasswordNeverExpires(true)
                .create()
                .setPasswordNeverExpires(null)
                .update();

        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select neverexpires dump"),
                            "FALSE",
                            "check that password of person expires");
    }

    /**
     * Checks that the password never expires flag is not reset if the
     * parameter 'UserPersonIgnorePswdNeverExpires' is defined.
     *
     * @throws Exception if check failed
     */
    @Test(description = "checks that the password never expires flag is not reset if the parameter 'UserPersonIgnorePswdNeverExpires' is defined")
    public void checkWithIgnoreParameterPasswordNeverExpiresFlagIsNotRemoved()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello \" test")
                .setPasswordNeverExpires(true)
                .create()
                .setPasswordNeverExpires(null)
                .update("UserPersonIgnorePswdNeverExpires", "*");

        // check not updated
        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select neverexpires dump"),
                            "TRUE",
                            "check that password of person never expires");
        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonIgnorePswdNeverExpires", "*");
        person.checkValueExists(exportParser, "person", "neverexpires", false);
        person.checkValueExists(exportParser, "person", "!neverexpires", false);
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
                .update();

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
                .update("UserPersonIgnoreWantsEmail", "*");

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
                .update();

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
                .update("UserPersonIgnoreWantsIconMail", "*");

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
        person  .updateWithCode("mql mod product CPF add person " + person.getName() + "\nsetProducts")
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
        person  .updateWithCode("mql mod product CPF remove person " + person.getName() + "\nsetProducts \"CPF\"")
                .checkExport();
    }

    /**
     * Checks that assigned products of persons are ignored if the parameter is
     * defined.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that assigned products of persons are ignored if the parameter is defined")
    public void checkWithIgnoreProductsParameter()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("test")
                .addProduct("CPF")
                .create()
                .setWantsIconMail(null);
        this.mql("escape mod product CPF remove person \"" + AbstractTest.convertMql(person.getName()) + "\"");
        person.update("UserPersonIgnoreProducts", "*");

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

    /**
     * Check that the password expires flag is ignored.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the password expires flag is ignored")
    public void checkPasswordExpiresFlagIgnored()
        throws Exception
    {
        final AbstractPersonAdminData<?> person = this.createNewData("hello")
                .create()
                .addType("notinactive");

        this.mql("escape mod person \"" + AbstractTest.convertMql(person.getName()) + "\" passwordexpired");

        person.export();
    }
}
