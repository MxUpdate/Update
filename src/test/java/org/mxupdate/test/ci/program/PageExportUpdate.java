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

package org.mxupdate.test.ci.program;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.program.PageData;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of page programs.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PageExportUpdate
    extends AbstractTest
{
    /**
     * Cleanups the MX system by deleting the test pages.
     *
     * @throws Exception if cleanup failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.PAGE);
    }

    /**
     * Data provider for test MQL programs (with and without extensions).
     *
     * @return object array with all test MQL programs
     */
    @DataProvider(name = "pages")
    public Object[][] getPagePrograms()
    {
        // without extension with code
        final DataCollection data1 = new DataCollection(this);
        data1.getPage("Test1")
             .setCode("test");

        // without extension without code
        final DataCollection data2 = new DataCollection(this);
        data2.getPage("Test2")
             .setCode("");

        // with extension with code
        final DataCollection data3 = new DataCollection(this);
        data3.getPage("Test3.tcl")
             .setCode("test");

        // with extension without code
        final DataCollection data4 = new DataCollection(this);
        data4.getPage("Test4.tcl")
             .setCode("");

        // with extension without code
        final DataCollection data5 = new DataCollection(this);
        data5.getPage("Test \" 5.tcl")
             .setCode("");

        return new Object[][]  {
                new Object[]{data1, "Test1"},
                new Object[]{data2, "Test2"},
                new Object[]{data3, "Test3.tcl"},
                new Object[]{data4, "Test4.tcl"},
                new Object[]{data5, "Test \" 5.tcl"},
        };
    }

    /**
     * Checks that the new created MQL progam is exported correctly.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "pages", description = "test export of pages")
    public void testExport(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final PageData page = _data.getPage(_name);
        page.create();
        final Export export = this.export(CI.PAGE, page.getName());

        // check oath
        Assert.assertEquals(export.getPath(),
                            page.getCiPath(),
                            "check path is correct");

        // check file name
        Assert.assertEquals(export.getFileName(),
                            page.getCIFileName(),
                            "check that the correct file name is returned");

        // check JPO code
        Assert.assertEquals(export.getCode(),
                            page.getCode(),
                            "checks MQL program code");
    }

    /**
     * Tests, if the MQL program within MX is created and registered with the
     * correct symbolic name.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "pages", description = "test update of non existing pages")
    public void testUpdate(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final PageData page = _data.getPage(_name);

        // first update with original content
        this.update(page.getCIFileName(), page.ciFile());

        Assert.assertTrue(!"".equals(this.mql("list page " + page.getName())),
                          "check page is created");
        Assert.assertEquals(this.mql("escape print page \""
                                    + AbstractTest.convertMql(page.getName()) + "\" select content dump"),
                            page.getCode(),
                            "check correct code");
    }
}
