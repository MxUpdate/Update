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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.program.PageData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of page programs.
 *
 * @author The MxUpdate Team
 */
public class PageTest
    extends AbstractDataExportUpdate<PageData>
{
    /**
     * Data provider for test pages.
     *
     * @return object array with all test pages
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return this.prepareData("page",
                new Object[]{
                        "simple page",
                        this.createNewData("Test1")
                             .setValue("description", "test description")},
                new Object[]{
                        "hidden page with mime type",
                        this.createNewData("Test3 \" test")
                             .setValue("description", "test description \" '")
                             .setValue("mime", "text/html")
                             .setFlag("hidden", true)}
        );
    }

    @DataProvider(name = "testCode")
    public Object[][] getTestCode()
    {
        return new Object[][]
        {
            {"print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump"},
            {"<]]>"},
        };
    }

    /**
     * Positive test for update of code.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for update of code",
          dataProvider = "testCode")
    public void positiveTestCodeUpdate(final String _code)
        throws Exception
    {
        final PageData page = this.createNewData("Test").setValue("content", _code).update("");

        Assert.assertEquals(this.mql("print page " + page.getName() + " select content dump"), _code);

        final ExportParser exportParser = page.export();

        // remove code and update via export
        this.mql("mod page " + page.getName() + " content ''");
        page.updateWithCode(exportParser.getCode(), "");

        // and check result (so that the export of code is also checked!)
        Assert.assertEquals(this.mql("print page " + page.getName() + " select content dump"), _code);
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.PRG_PAGE);
    }

    @Override()
    protected PageData createNewData(final String _name)
    {
        return new PageData(this, _name);
    }
}
