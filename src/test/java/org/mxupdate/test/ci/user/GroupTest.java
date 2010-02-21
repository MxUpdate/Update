/*
 * Copyright 2008-2010 The MxUpdate Team
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
import org.mxupdate.test.data.user.GroupData;
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
 * Test cases for the export and update of groups.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class GroupTest
    extends AbstractUserTest<GroupData>
{
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

    /**
     * Data provider for test groups.
     *
     * @return object array with all test groups
     */
    @DataProvider(name = "groups")
    public Object[][] getGroups()
    {
        return this.prepareData("group",
                new Object[]{
                        "simple group",
                        new GroupData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "hidden group",
                        new GroupData(this, "hallo \" test")
                                .setHidden(true)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "not hidden group",
                        new GroupData(this, "hallo \" test")
                                .setHidden(false)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "default hidden group",
                        new GroupData(this, "hallo \" test")
                                .setHidden(null)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "group with two parent groups",
                        new GroupData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParent(new GroupData(this, "hallo parent1 \" test"))
                                .assignParent(new GroupData(this, "hallo parent2 \" test"))},
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
        this.cleanup(AbstractTest.CI.OTHER_SITE);
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
    }

    /**
     * Tests a new created group and the related export.
     *
     * @param _description  description of the test case
     * @param _group        group to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "groups", description = "test export of new created group")
    public void simpleExport(final String _description,
                             final GroupData _group)
        throws Exception
    {
        _group.create();
        _group.checkExport(_group.export());
    }

    /**
     * Tests an update of non existing group. The result is tested with by
     * exporting the group and checking the result.
     *
     * @param _description  description of the test case
     * @param _group        group to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "groups",
          description = "test update of non existing group")
    public void simpleUpdate(final String _description,
                             final GroupData _group)
        throws Exception
    {
        // create all parent groups
        for (final GroupData parent : _group.getParent())  {
            parent.create();
        }
        // create referenced property value
        for (final PropertyDef prop : _group.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create site
        if (_group.getSite() != null)  {
            _group.getSite().create();
        }
        // create cue properties
        for (final CueData<GroupData> cue : _group.getCues())  {
            for (final PropertyDef prop : cue.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create filter properties
        for (final FilterData<GroupData> filter : _group.getFilters())  {
            for (final PropertyDef prop : filter.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create query properties
        for (final QueryData<GroupData> query : _group.getQueries())  {
            for (final PropertyDef prop : query.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create table properties
        for (final TableData<GroupData> table : _group.getTables())  {
            for (final PropertyDef prop : table.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create tip properties
        for (final TipData<GroupData> tip : _group.getTips())  {
            for (final PropertyDef prop : tip.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }
        // create tool set properties and programs
        for (final ToolSetData<GroupData> toolSet : _group.getToolSets())  {
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
        for (final ViewData<GroupData> view : _group.getViews())  {
            for (final PropertyDef prop : view.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // first update with original content
        _group.update();
        final ExportParser exportParser = _group.export();
        _group.checkExport(exportParser);

        // second update with delivered content
        _group.updateWithCode(exportParser.getOrigCode())
              .checkExport(_group.export());
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
        final GroupData group = new GroupData(this, "hallo \" test").setHidden(true);
        group.create();
        group.setHidden(null);
        group.update();
        Assert.assertEquals(this.mql("escape print group \"" + AbstractTest.convertMql(group.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that group is not hidden");
    }
}
