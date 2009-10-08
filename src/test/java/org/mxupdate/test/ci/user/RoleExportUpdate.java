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
public class RoleExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test roles.
     *
     * @return object array with all test roles
     */
    @DataProvider(name = "roles")
    public Object[][] getRoles()
    {
        final RoleData role1 = new RoleData(this, "hallo \" test")
                .setValue("description", "\"\\\\ hallo");

        final RoleData role2 = new RoleData(this, "hallo \" test")
                .setHidden(true)
                .setValue("description", "\"\\\\ hallo");

        final RoleData role3 = new RoleData(this, "hallo \" test")
                .setValue("description", "\"\\\\ hallo")
                .assignParent(new RoleData(this, "hallo parent1 \" test"))
                .assignParent(new RoleData(this, "hallo parent2 \" test"));

        final RoleData role4 = new RoleData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));

        return new Object[][]  {
                new Object[]{role1},
                new Object[]{role2},
                new Object[]{role3},
                new Object[]{role4},
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
        this.cleanup(AbstractTest.CI.ROLE);
        this.cleanup(AbstractTest.CI.SITE);
    }

    /**
     * Tests a new created role and the related export.
     *
     * @param _role     role to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "roles", description = "test export of new created role")
    public void simpleExport(final RoleData _role)
        throws Exception
    {
        _role.create();
        _role.checkExport(_role.export());
    }

    /**
     * Tests an update of non existing role. The result is tested with by
     * exporting the role and checking the result.
     *
     * @param _role     role to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "roles", description = "test update of non existing role")
    public void simpleUpdate(final RoleData _role)
        throws Exception
    {
        // create all parent roles
        for (final RoleData parentRole : _role.getParent())  {
            parentRole.create();
        }

        // create site
        if (_role.getSite() != null)  {
            _role.getSite().create();
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
    public void removeExistingSite()
        throws Exception
    {
        final RoleData role = new RoleData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        role.create();
        role.setSite(null);
        this.update(role.getCIFileName(), role.ciFile());

        Assert.assertEquals(this.mql("escape print role \""
                                    + AbstractTest.convertMql(role.getName())
                                    + "\" select site dump"),
                            "",
                            "check that no site is defined");
    }
}
