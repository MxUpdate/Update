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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.other.SiteData;
import org.mxupdate.test.data.program.AbstractProgramData;
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
 * Test cases for the export and update of roles.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RoleExportUpdate
    extends AbstractUserExportUpdate<RoleData>
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
    @DataProvider(name = "roles")
    public Object[][] getRoles()
    {
        return super.prepareData("role",
                new Object[]{
                        "simple role",
                        new RoleData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "hidden role",
                        new RoleData(this, "hallo \" test")
                                .setHidden(true)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "not hidden role",
                        new RoleData(this, "hallo \" test")
                                .setHidden(false)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "default hidden role",
                        new RoleData(this, "hallo \" test")
                                .setHidden(null)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "role with two parent roles",
                        new RoleData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParent(new RoleData(this, "hallo parent1 \" test"))
                                .assignParent(new RoleData(this, "hallo parent2 \" test"))},
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
        this.cleanup(AbstractTest.CI.PERSONADMIN);
        this.cleanup(AbstractTest.CI.GROUP);
        this.cleanup(AbstractTest.CI.ROLE);
        this.cleanup(AbstractTest.CI.SITE);
        this.cleanup(AbstractTest.CI.MQL_PROGRAM);
    }

    /**
     * Tests a new created role and the related export.
     *
     * @param _description  description of the test case
     * @param _role         role to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "roles", description = "test export of new created role")
    public void simpleExport(final String _description,
                             final RoleData _role)
        throws Exception
    {
        _role.create();
        _role.checkExport(_role.export());
    }

    /**
     * Tests an update of non existing role. The result is tested with by
     * exporting the role and checking the result.
     *
     * @param _description  description of the test case
     * @param _role         role to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "roles", description = "test update of non existing role")
    public void simpleUpdate(final String _description,
                             final RoleData _role)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _role.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create all parent roles
        for (final RoleData parentRole : _role.getParent())  {
            parentRole.create();
        }
        // create site
        if (_role.getSite() != null)  {
            _role.getSite().create();
        }
        // create cue properties
        for (final CueData<RoleData> cue : _role.getCues())  {
            for (final PropertyDef prop : cue.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create filter properties
        for (final FilterData<RoleData> filter : _role.getFilters())  {
            for (final PropertyDef prop : filter.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create query properties
        for (final QueryData<RoleData> query : _role.getQueries())  {
            for (final PropertyDef prop : query.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create table properties
        for (final TableData<RoleData> table : _role.getTables())  {
            for (final PropertyDef prop : table.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create tip properties
        for (final TipData<RoleData> tip : _role.getTips())  {
            for (final PropertyDef prop : tip.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create tool set properties and programs
        for (final ToolSetData<RoleData> toolSet : _role.getToolSets())  {
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
        for (final ViewData<RoleData> view : _role.getViews())  {
            for (final PropertyDef prop : view.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // first update with original content
        this.update(_role.getCIFileName(), _role.ciFile());
        final ExportParser exportParser = _role.export();
        _role.checkExport(exportParser);

        // second update with delivered content
        this.update(_role.getCIFileName(), exportParser.getOrigCode());
        _role.checkExport(_role.export());
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
        this.update(role);

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
        final RoleData role = new RoleData(this, "hallo \" test").setHidden(true);
        role.create();
        role.setHidden(null);
        this.update(role);
        Assert.assertEquals(this.mql("escape print role \"" + AbstractTest.convertMql(role.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that role is not hidden");
    }
}
