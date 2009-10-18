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

package org.mxupdate.test.ci.integration;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.integration.IEFGlobalConfig;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of integration global configuration
 * objects.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class IEFGlobalConfigExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test integration global configuration objects.
     *
     * @return object array with all test objects
     */
    @DataProvider(name = "busDatas")
    public Object[][] getData()
    {
        return new Object[][]  {
                new Object[]{
                        "simple object",
                        new IEFGlobalConfig(this, "HelloTest", "1")},
                new Object[]{
                        "simple object with description",
                        new IEFGlobalConfig(this, "Hello \"Test\"", "1")
                                .setDescription("a \"description\"")},
                new Object[]{
                        "simple object with description and single apostrophe",
                        new IEFGlobalConfig(this, "Hello \"Test\" 'with single apostrophe'", "1")
                                .setDescription("a \"description\" with single 'apostrophe'")},
                new Object[]{
                        "complex object",
                        new IEFGlobalConfig(this, "HelloTest", "1")
                                .setValue("MCADInteg-TypeFormatMapping", "complex \"data\" 'and single 'apostrophe'")},
        };
    }

    /**
     * Cleanup all test integration global configuration objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.IEF_GLOBAL_CONFIG);
    }

    /**
     * Tests a new created integration global configuration objects and the
     * related export.
     *
     * @param _description      description of the test case
     * @param _globalConfig     global configuration to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas", description = "test export of new created integration global configuration objects")
    public void simpleExport(final String _description,
                             final IEFGlobalConfig _globalConfig)
        throws Exception
    {
        _globalConfig.create();
        _globalConfig.checkExport(_globalConfig.export());
    }

    /**
     * Tests an update of non existing table. The result is tested with by
     * exporting the table and checking the result.
     *
     * @param _description      description of the test case
     * @param _globalConfig   global configuration to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas", description = "test update of non existing table")
    public void simpleUpdate(final String _description,
                             final IEFGlobalConfig _globalConfig)
        throws Exception
    {
        // first update with original content
        this.update(_globalConfig);
        final ExportParser exportParser = _globalConfig.export();
        _globalConfig.checkExport(exportParser);

        // second update with delivered content
        this.update(_globalConfig.getCIFileName(), exportParser.getOrigCode());
        _globalConfig.checkExport(_globalConfig.export());
    }
}
