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
public class GroupExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test groups.
     *
     * @return object array with all test groups
     */
    @DataProvider(name = "groups")
    public Object[][] getGroups()
    {
        final GroupData group1 = new GroupData(this, "hallo \" test")
                .setValue("description", "\"\\\\ hallo");

        final GroupData group2 = new GroupData(this, "hallo \" test")
                .setHidden(true)
                .setValue("description", "\"\\\\ hallo");

        final GroupData group3 = new GroupData(this, "hallo \" test")
                .setValue("description", "\"\\\\ hallo")
                .assignParent(new GroupData(this, "hallo parent1 \" test"))
                .assignParent(new GroupData(this, "hallo parent2 \" test"));

        final GroupData group4 = new GroupData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));

        return new Object[][]  {
                new Object[]{group1},
                new Object[]{group2},
                new Object[]{group3},
                new Object[]{group4},
        };
    }


    /**
     * Cleanup all test commands.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.GROUP);
        this.cleanup(AbstractTest.CI.SITE);
    }

    /**
     * Tests a new created group and the related export.
     *
     * @param _group     group to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "groups", description = "test export of new created group")
    public void simpleExport(final GroupData _group)
        throws Exception
    {
        _group.create();
        _group.checkExport(_group.export());
    }

    /**
     * Tests an update of non existing group. The result is tested with by
     * exporting the group and checking the result.
     *
     * @param _group     group to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "groups", description = "test update of non existing group")
    public void simpleUpdate(final GroupData _group)
        throws Exception
    {
        // create all parent groups
        for (final GroupData parent : _group.getParent())  {
            parent.create();
        }

        // create site
        if (_group.getSite() != null)  {
            _group.getSite().create();
        }

        // first update with original content
        this.update(_group.getCIFileName(), _group.ciFile());
        final ExportParser exportParser = _group.export();
        _group.checkExport(exportParser);

        // second update with delivered content
        this.update(_group.getCIFileName(), exportParser.getOrigCode());
        _group.checkExport(_group.export());
    }

    /**
     * Test an update of a group where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing group with site by removing site")
    public void removeExistingSite()
        throws Exception
    {
        final GroupData group = new GroupData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        group.create();
        group.setSite(null);
        this.update(group.getCIFileName(), group.ciFile());

        Assert.assertEquals(this.mql("escape print group \""
                                    + AbstractTest.convertMql(group.getName())
                                    + "\" select site dump"),
                            "",
                            "check that no site is defined");
    }
}
