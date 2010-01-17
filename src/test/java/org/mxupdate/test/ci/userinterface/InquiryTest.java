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

package org.mxupdate.test.ci.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.userinterface.InquiryData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of inquiries.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class InquiryTest
    extends AbstractDataExportUpdate<InquiryData>
{
    /**
     * Creates for given <code>_name</code> a new inquiry instance.
     *
     * @param _name     name of the inquiry instance
     * @return inquiry instance
     */
    @Override()
    protected InquiryData createNewData(final String _name)
    {
        return new InquiryData(this, _name);
    }

    /**
     * Data provider for test inquiries.
     *
     * @return object array with all test inquiries
     */
    @DataProvider(name = "inquires")
    public Object[][] getInquiries()
    {
        return this.prepareData("inquiry",
                new Object[]{
                        "complex inquiry with all values and three arguments",
                        new InquiryData(this, "Test1")
                             .setValue("description", "test description")
                             .setValue("pattern", "${ID}|*")
                             .setValue("format", "${ID}")
                             .setArgument("TYPE", "MY_TYPE")
                             .setArgument("NAME", "MY_NAME")
                             .setArgument("REVISION", "MY_REVISION")
                             .setCode("print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump")},
                new Object[]{
                        "simple inquiry only with description",
                        new InquiryData(this, "Test2")
                             .setValue("description", "test description")},
                new Object[]{
                        "complex inquiry with all values and one argument",
                        new InquiryData(this, "Test3 \" test")
                             .setValue("description", "test description \" '")
                             .setValue("pattern", "patternprefix \" patternsuffix")
                             .setValue("format", "formatprefix \" formatsuffix")
                             .setArgument("ARGUMENT \" ARGSUFFIX", "argumentprefix \" argumentsuffix")
                             .setCode("print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump")});
    }

    /**
     * Cleanup all test inquiries.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_INQUIRY);
    }

    /**
     * Tests a new created inquiry and the related export.
     *
     * @param _description  description of the test case
     * @param _inquiry      inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "inquires",
          description = "test export of new created inquires")
    public void simpleExport(final String _description,
                             final InquiryData _inquiry)
        throws Exception
    {
        _inquiry.create();
        _inquiry.checkExport(_inquiry.export());
    }

    /**
     * Tests an update of non existing inquiry. The result is tested by
     * exporting the inquiry and checking the result.
     *
     * @param _description  description of the test case
     * @param _inquiry      inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "inquires",
          description = "test update of non existing inquiry")
    public void simpleUpdate(final String _description,
                             final InquiryData _inquiry)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _inquiry.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }

        // first update with original content
        this.update(_inquiry);
        final ExportParser exportParser = _inquiry.export();
        _inquiry.checkExport(exportParser);

        // second update with delivered content
        this.update(_inquiry.getCIFileName(), exportParser.getOrigCode());
        _inquiry.checkExport(_inquiry.export());
    }
}
