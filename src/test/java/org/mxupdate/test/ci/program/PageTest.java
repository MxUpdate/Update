/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.test.ci.program;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
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
 */
public class PageTest
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
        this.cleanup(CI.PRG_PAGE);
    }

    /**
     * Data provider for test MQL programs (with and without extensions).
     *
     * @return object array with all test MQL programs
     */
    @DataProvider(name = "pages")
    public Object[][] getPagePrograms()
    {
        return new Object[][]  {
                new Object[]{
                        "without extension with code",
                        new PageData(this, "Test1").setContent("test")},
                new Object[]{
                        "without extension without code",
                        new PageData(this, "Test2").setContent("")},
                new Object[]{
                        "with extension with code",
                        new PageData(this, "Test3.tcl").setContent("test")},
                new Object[]{
                        "with extension without code",
                        new PageData(this, "Test4.tcl").setContent("")},
                new Object[]{
                        "with extension without code",
                        new PageData(this, "Test \" 5.tcl").setContent("")},
                new Object[]{
                        "with extension and CDATA tag end",
                        new PageData(this, "Test.tcl").setContent("<]]>")},
                new Object[]{
                        "with description",
                        new PageData(this, "Test")
                                .setValue("description", "My \"Test\" description")
                                .setContent("Test Code")},
                new Object[]{
                        "with mime type",
                        new PageData(this, "Test")
                                .setValue("mime", "text/plain")
                                .setContent("Test Code")},
                new Object[]{
                        "without code",
                        new PageData(this, "Test")},
        };
    }

    /**
     * Checks that the new created pages is exported correctly.
     *
     * @param _description  description of the test data
     * @param _page         page to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "pages",
          description = "test export of pages")
    public void simpleExport(final String _description,
                             final PageData _page)
        throws Exception
    {
        _page.create();
        _page.checkExport(_page.export());
    }

    /**
     * Tests, if the MQL program within MX is created and registered with the
     * correct symbolic name.
     *
     * @param _description  description of the test data
     * @param _page         page to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "pages",
          description = "test update of non existing pages")
    public void simpleUpdate(final String _description,
                             final PageData _page)
        throws Exception
    {
        // first update with original content
        _page.update((String) null);
        final ExportParser exportParser = _page.export();
        _page.checkExport(exportParser);

        // second update with delivered content
        _page.updateWithCode(exportParser.getOrigCode(), (String) null)
             .checkExport();

        // and check that both export code is equal
        Assert.assertEquals(exportParser.getOrigCode(),
                            _page.export().getOrigCode(),
                            "check that first and second export is equal");
    }


    /**
     * Check that the end tag of CDATA is correct translated to
     * 'Inserted_by_ENOVIA'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the end tag of CDATA is correct translated to 'Inserted_by_ENOVIA'")
    public void checkCDataTranslation()
        throws Exception
    {
        final String name = AbstractTest.PREFIX + "_Test";
        this.mql("add page " + name + " content '<]]>'");
        final String xml = this.mql("export page " + name + " xml");
        Assert.assertTrue(xml.indexOf("<pageContent><![CDATA[<]Inserted_by_ENOVIA]Inserted_by_ENOVIA>]]></pageContent>") >= 0,
                          "check translation of the CDATA conversion");
    }
}
