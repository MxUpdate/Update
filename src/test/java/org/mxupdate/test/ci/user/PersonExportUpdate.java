/*
 * Copyright 2008-2009 The MxUpdate Team
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

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.other.SiteData;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.user.workspace.CueData;
import org.mxupdate.test.data.user.workspace.FilterData;
import org.mxupdate.test.data.user.workspace.QueryData;
import org.mxupdate.test.data.user.workspace.TableData;
import org.mxupdate.test.data.user.workspace.TipData;
import org.mxupdate.test.data.user.workspace.ToolSetData;
import org.mxupdate.test.data.user.workspace.ViewData;
import org.mxupdate.test.data.util.PropertyDef;
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
 * @version $Id$
 */
public class PersonExportUpdate
    extends AbstractUserExportUpdate<PersonAdminData>
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

    /**
     * Data provider for test persons.
     *
     * @return object array with all test persons
     */
    @DataProvider(name = "persons")
    public Object[][] getPersons()
    {
        return this.prepareData("person",
                new Object[]{
                        "simple person",
                        new PersonAdminData(this, "hello \" test")
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "person with some access",
                        new PersonAdminData(this, "hello \" test")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "person with normal access but specific changeName / changevault access but no business access",
                        new PersonAdminData(this, "hello \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "person with normal access but non specific access but no business access",
                        new PersonAdminData(this, "hello \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")
                                },
                new Object[]{
                        "person as business administration for types",
                        new PersonAdminData(this, "hello \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type")},
                new Object[]{
                        "person as business administration for types and vaults",
                        new PersonAdminData(this, "hello \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type", "vault")},
                new Object[]{
                        "person with one group",
                        new PersonAdminData(this, "hello \" test")
                                .addGroup(new GroupData(this, "test \"group\""))},
                new Object[]{
                        "person with two groups",
                        new PersonAdminData(this, "hello \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))},
                new Object[]{
                        "person with one role",
                        new PersonAdminData(this, "hello \" test")
                                .addRole(new RoleData(this, "test \"role\""))},
                new Object[]{
                        "person with two roles",
                        new PersonAdminData(this, "hello \" test")
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},
                new Object[]{
                        "person with two groups and roles",
                        new PersonAdminData(this, "hello \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},

                new Object[]{
                        "hidden person",
                        new PersonAdminData(this, "hello \" test")
                                .setHidden(true)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "not hidden person",
                        new PersonAdminData(this, "hello \" test")
                                .setHidden(false)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "default hidden person",
                        new PersonAdminData(this, "hello \" test")
                                .setHidden(null)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "person with assigned site",
                        new PersonAdminData(this, "hello \" test")
                                .setSite(new SiteData(this, "Test \" Site"))},
                new Object[]{
                        "complex person",
                        new PersonAdminData(this, "hello \" test")
                                .setValue("fullname", "test person \"full name\"")
                                .setValue("description", "test person \"comment\"")
                                .setValue("address", "test person \"address\"")
                                .setValue("email", "test person \"email\" address")
                                .setValue("phone", "test person \"phone\"")},
                new Object[]{
                        "person no password expires flag defined",
                        new PersonAdminData(this, "hello \" test")
                                .setPasswordNeverExpires(null)},
                new Object[]{
                        "person with password expires flag defined to true",
                        new PersonAdminData(this, "hello \" test")
                                .setPasswordNeverExpires(true)},
                new Object[]{
                        "person with password expires flag defined to false",
                        new PersonAdminData(this, "hello \" test")
                                .setPasswordNeverExpires(false)},
                new Object[]{
                        "person with no wants email flag",
                        new PersonAdminData(this, "hello \" test")
                                .setWantsEmail(null)},
                new Object[]{
                        "person with wants email flag to true",
                        new PersonAdminData(this, "hello \" test")
                                .setWantsEmail(true)},
                new Object[]{
                        "person with wants email flag to false",
                         new PersonAdminData(this, "hello \" test")
                                .setWantsEmail(false)},
                new Object[]{
                        "person with no wants icon mail flag",
                        new PersonAdminData(this, "hello \" test")
                                .setWantsIconMail(null)},
                new Object[]{
                        "person with wants icon mail flag to true",
                        new PersonAdminData(this, "hello \" test")
                                .setWantsIconMail(true)},
                new Object[]{
                        "person with wants icon mail flag to false",
                         new PersonAdminData(this, "hello \" test")
                                .setWantsIconMail(false)}

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
        this.cleanup(AbstractTest.CI.PERSONADMIN);
        this.cleanup(AbstractTest.CI.GROUP);
        this.cleanup(AbstractTest.CI.ROLE);
        this.cleanup(AbstractTest.CI.SITE);
        this.cleanup(AbstractTest.CI.MQL_PROGRAM);
    }

    /**
     * Tests a new created person and the related export.
     *
     * @param _description  description of the test case
     * @param _person       person to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "persons", description = "test export of new created person")
    public void simpleExport(final String _description,
                             final PersonAdminData _person)
        throws Exception
    {
        _person.create();
        _person.checkExport(_person.export());
    }

    /**
     * Tests an update of non existing person. The result is tested with by
     * exporting the person and checking the result.
     *
     * @param _description  description of the test case
     * @param _person       person to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "persons", description = "test update of non existing person")
    public void simpleUpdate(final String _description,
                             final PersonAdminData _person)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _person.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create site
        if (_person.getSite() != null)  {
            _person.getSite().create();
        }
        // create assigned roles
        for (final RoleData role : _person.getRoles())  {
            role.create();
        }
        // create assigned groups
        for (final GroupData group : _person.getGroups())  {
            group.create();
        }
        // create cue properties
        for (final CueData<PersonAdminData> cue : _person.getCues())  {
            for (final PropertyDef prop : cue.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create filter properties
        for (final FilterData<PersonAdminData> filter : _person.getFilters())  {
            for (final PropertyDef prop : filter.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create query properties
        for (final QueryData<PersonAdminData> query : _person.getQueries())  {
            for (final PropertyDef prop : query.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create table properties
        for (final TableData<PersonAdminData> table : _person.getTables())  {
            for (final PropertyDef prop : table.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create tip properties
        for (final TipData<PersonAdminData> tip : _person.getTips())  {
            for (final PropertyDef prop : tip.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create tool set properties and programs
        for (final ToolSetData<PersonAdminData> toolSet : _person.getToolSets())  {
            for (final PropertyDef prop : toolSet.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
            for (final AbstractProgramData<?> prog : toolSet.getPrograms())  {
                prog.create();
            }
        }
        // create view properties
        for (final ViewData<PersonAdminData> view : _person.getViews())  {
            for (final PropertyDef prop : view.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // first update with original content
        this.update(_person.getCIFileName(), _person.ciFile());
        final ExportParser exportParser = _person.export();
        _person.checkExport(exportParser);

        // second update with delivered content
        this.update(_person.getCIFileName(), exportParser.getOrigCode());
        _person.checkExport(_person.export());
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
        final PersonAdminData person = new PersonAdminData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        person.create();
        person.setSite(null);
        this.update(person);

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setHidden(true);
        person.create();
        person.setHidden(null);
        this.update(person);

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setPasswordNeverExpires(true);
        person.create();
        person.setPasswordNeverExpires(null);
        this.update(person);

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setPasswordNeverExpires(true);
        person.create();
        person.setPasswordNeverExpires(null);
        this.update(person, "UserPersonIgnorePswdNeverExpires", "*");

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setWantsEmail(true);
        person.create();
        person.setWantsEmail(null);
        this.update(person);

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setWantsEmail(true);
        person.create();
        person.setWantsEmail(null);
        this.update(person, "UserPersonIgnoreWantsEmail", "*");

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setWantsIconMail(false);
        person.create();
        person.setWantsIconMail(null);
        this.update(person);

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
        final PersonAdminData person = new PersonAdminData(this, "hello \" test").setWantsIconMail(false);
        person.create();
        person.setWantsIconMail(null);
        this.update(person, "UserPersonIgnoreWantsIconMail", "*");

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
}
