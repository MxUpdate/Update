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

package org.mxupdate.test.ci.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.userinterface.InquiryData;
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
public class InquiryExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test inquiries.
     *
     * @return object array with all test inquiries
     */
    @DataProvider(name = "inquires")
    public Object[][] getInquiries()
    {
        final DataCollection data1 = new DataCollection(this);
        data1.getInquiry("Test1")
             .setValue("description", "test description")
             .setValue("pattern", "${ID}|*")
             .setValue("format", "${ID}")
             .setArgument("TYPE", "MY_TYPE")
             .setArgument("NAME", "MY_NAME")
             .setArgument("REVISION", "MY_REVISION")
             .setCode("print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump");

        final DataCollection data2 = new DataCollection(this);
        data2.getInquiry("Test2")
             .setValue("description", "test description");

        final DataCollection data3 = new DataCollection(this);
        data3.getInquiry("Test3 \" test")
             .setValue("description", "test description \" '")
             .setValue("pattern", "patternprefix \" patternsuffix")
             .setValue("format", "formatprefix \" formatsuffix")
             .setArgument("ARGUMENT \" ARGSUFFIX", "argumentprefix \" argumentsuffix")
             .setCode("print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump");

        return new Object[][]  {
                new Object[]{data1, "Test1"},
                new Object[]{data2, "Test2"},
                new Object[]{data3, "Test3 \" test"},
        };
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
        this.cleanup(CI.INQUIRY);
    }

    /**
     * Tests a new created inquiry and the related export.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "inquires", description = "test export of new created inquires")
    public void testExport(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        _data.create();
        final InquiryData inquiry = _data.getInquiry(_name);
        final ExportParser exportParser = inquiry.export();
        inquiry.checkExport(exportParser);
    }

    /**
     * Tests an update of non existing inquiry. The result is tested by
     * exporting the inquiry and checking the result.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "inquires", description = "test update of non existing inquiry")
    public void testUpdate(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final InquiryData inquiry = _data.getInquiry(_name);
        // first update with original content
        this.update(inquiry.getCIFileName(), inquiry.ciFile());
        final ExportParser exportParser = inquiry.export();
        inquiry.checkExport(exportParser);
        // second update with delivered content
        this.update(inquiry.getCIFileName(), exportParser.getOrigCode());
        inquiry.checkExport(inquiry.export());
    }
}
