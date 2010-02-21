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

package org.mxupdate.test.ci.integration;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.integration.IEFEBOMSyncConfigData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of IEF EBOM sync configuration objects.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class IEFEBOMSyncConfigTest
    extends AbstractTest
{
    /**
     * Data provider for test integration EBOM sync configuration objects.
     *
     * @return object array with all test objects
     */
    @DataProvider(name = "busDatas")
    public Object[][] getData()
    {
        return new Object[][]  {
                new Object[]{
                        "simple object",
                        new IEFEBOMSyncConfigData(this, new TypeData(this, "EBOMSync").setValue("derived", "IEF-EBOMSyncConfig"), "HelloTest", "-")},
                new Object[]{
                        "simple object with description",
                        new IEFEBOMSyncConfigData(this, new TypeData(this, "EBOMSync").setValue("derived", "IEF-EBOMSyncConfig"), "Hello \"Test\"", "-")
                                .setDescription("a \"description\"")},
                new Object[]{
                        "simple object with description and single apostrophe",
                        new IEFEBOMSyncConfigData(this, new TypeData(this, "EBOMSync").setValue("derived", "IEF-EBOMSyncConfig"),
                                                  "Hello \"Test\" 'with single apostrophe'", "-")
                                .setDescription("a \"description\" with single 'apostrophe'")},
        };
    }

    /**
     * Cleanup all test integration EBOM sync configuration objects and data
     * model types (because created as derived types).
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.IEF_EBOMSYNC_CONFIG);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }


    /**
     * Tests a new created integration global configuration objects and the
     * related export.
     *
     * @param _description      description of the test case
     * @param _syncConfig       EBOM Sync configuration to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas", description = "test export of new created IEF EBOM sync configuration objects")
    public void simpleExport(final String _description,
                             final IEFEBOMSyncConfigData _syncConfig)
        throws Exception
    {
        _syncConfig.getType().create();
        _syncConfig.create();
        _syncConfig.checkExport(_syncConfig.export());
    }

    /**
     * Tests an update of non existing table. The result is tested with by
     * exporting the table and checking the result.
     *
     * @param _description      description of the test case
     * @param _syncConfig       EBOM Sync configuration to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas",
          description = "test update of non existing IEF EBOM sync configurations")
    public void simpleUpdate(final String _description,
                             final IEFEBOMSyncConfigData _syncConfig)
        throws Exception
    {
        _syncConfig.getType().create();

        // first update with original content
        _syncConfig.update();
        final ExportParser exportParser = _syncConfig.export();
        _syncConfig.checkExport(exportParser);

        // second update with delivered content
        _syncConfig.updateWithCode(exportParser.getOrigCode())
                   .checkExport();
    }
}
