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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.datamodel.FormatData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for format exports and updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class FormatTest
    extends AbstractDataExportUpdate<FormatData>
{
    /**
     * Creates for given <code>_name</code> a new format instance.
     *
     * @param _name     name of the format instance
     * @return format instance
     */
    @Override()
    protected FormatData createNewData(final String _name)
    {
        return new FormatData(this, _name);
    }

    /**
     * Data provider for test formats.
     *
     * @return object array with all test formats
     */
    @DataProvider(name = "formats")
    public Object[][] getFormats()
    {
        return this.prepareData("format",
                new Object[]{
                        "format without anything (to test required fields)",
                        new FormatData(this, "hello \" test")},
                new Object[]{
                        "format with other symbolic name",
                        new FormatData(this, "hello \" test")
                                .setSymbolicName("format_Test")},
                new Object[]{
                        "format with description",
                        new FormatData(this, "hello \" test")
                                .setValue("description", "complex description \"test\"")},
                new Object[]{
                        "format with version",
                        new FormatData(this, "hello \" test")
                                .setValue("version", "version \"test\"")},
                new Object[]{
                        "format with suffix",
                        new FormatData(this, "hello \" test")
                                .setValue("suffix", "suffix \"test\"")},
                new Object[]{
                        "format with mime",
                        new FormatData(this, "hello \" test")
                                .setValue("mime", "mime \"test\"")},
                new Object[]{
                        "format with type",
                        new FormatData(this, "hello \" test")
                                .setValue("type", "type \"test\"")},
                new Object[]{
                        "format with view program",
                        new FormatData(this, "hello \" test")
                                .setViewProgram(new MQLProgramData(this, "ViewProgram"))},
                new Object[]{
                        "format with edit program",
                        new FormatData(this, "hello \" test")
                                .setEditProgram(new MQLProgramData(this, "EditProgram"))},
                new Object[]{
                        "format with print program",
                        new FormatData(this, "hello \" test")
                                .setPrintProgram(new MQLProgramData(this, "PrintProgram"))}
        );
    }

    /**
     * Removes the MxUpdate formats and programs.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_FORMAT);
        this.cleanup(CI.PRG_MQL_PROGRAM);
    }

    /**
     * Tests a new created format and the related export.
     *
     * @param _description  description of the test case
     * @param _format       format to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "formats",
          description = "test export of new created formats")
    public void testExport(final String _description,
                           final FormatData _format)
        throws Exception
    {
        _format.create()
               .checkExport();
    }


    /**
     * Tests an update of non existing format. The result is tested with by
     * exporting the format and checking the result.
     *
     * @param _description  description of the test case
     * @param _format       format to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "formats",
          description = "test update of non existing format")
    public void testUpdate(final String _description,
                           final FormatData _format)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _format.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create programs
        if (_format.getViewProgram() != null)  {
            _format.getViewProgram().create();
        }
        if (_format.getEditProgram() != null)  {
            _format.getEditProgram().create();
        }
        if (_format.getPrintProgram() != null)  {
            _format.getPrintProgram().create();
        }

        // first update with original content
        _format.update();
        final ExportParser exportParser = _format.export();
        _format.checkExport(exportParser);

        // second update with delivered content
        _format.updateWithCode(exportParser.getOrigCode())
               .checkExport();
    }

    /**
     * Test update of existing format that all parameters are cleaned.
     *
     * @param _description  description of the test case
     * @param _format       format to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "formats",
          description = "test update of existing format for cleaning")
    public void testUpdate4Existing(final String _description,
                                    final FormatData _format)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _format.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create programs
        if (_format.getViewProgram() != null)  {
            _format.getViewProgram().create();
        }
        if (_format.getEditProgram() != null)  {
            _format.getEditProgram().create();
        }
        if (_format.getPrintProgram() != null)  {
            _format.getPrintProgram().create();
        }

        // first update with original content
        _format.update()
               .checkExport();

        // second update with delivered content
        new FormatData(this, _format.getName().substring(AbstractTest.PREFIX.length()))
                .update()
                .setValue("description", "")
                .setValue("version", "")
                .setValue("suffix", "")
                .setValue("mime", "")
                .setValue("type", "")
                .checkExport();
    }
}
