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

package org.mxupdate.test.test.update.program;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.program.PageData;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.program.Page_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Test cases for the export and update of page programs.
 *
 * @author The MxUpdate Team
 */
public class PageCI_3UpdateTest
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
        this.mql().cmd("escape add page ").arg(this.createNewData("Test").getName()).exec(this.getContext());

        final PageData page = this.createNewData("Test").setValue("content", _code).update("");

        Assert.assertEquals(this.mql().cmd("escape print page ").arg(page.getName()).cmd(" select ").arg("content").cmd(" dump").exec(this.getContext()), _code);

        final ExportParser exportParser = page.export();

        // remove code and update via export
        this.mql().cmd("escape mod page ").arg(page.getName()).cmd(" content ").arg("").exec(this.getContext());
        page.updateWithCode(exportParser.getCode(), "");

        // and check result (so that the export of code is also checked!)
        Assert.assertEquals(this.mql().cmd("escape print page ").arg(page.getName()).cmd(" select ").arg("content").cmd(" dump").exec(this.getContext()), _code);
    }

    /**
     * Positive test for update with referenced file.
     *
     * @throws Exception if update failed
     */
    @Test(description = "positive test for update with referenced file")
    public void positiveTestUpdateWithFile()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final PageData page = new PageData(this, "Test").create().setValue("file", "program/mql/test.tcl");

        final WrapperCIInstance<Page_mxJPO> wrapper = new WrapperCIInstance<>(new Page_mxJPO(page.getName()));
        wrapper.parseUpdate(page);
        wrapper.store(new File(this.getResourcesDir(), "test.mxu"), paramCache);

        Assert.assertEquals(
                this.mql().cmd("escape print page ").arg(page.getName()).cmd(" select ").arg("content").cmd(" dump").exec(this.getContext()),
                FileUtils.readFileToString(new File(this.getResourcesDir(), "program/mql/test.tcl")).trim());
    }

    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.PRG_PAGE);
    }

    @Override
    protected PageData createNewData(final String _name)
    {
        return new PageData(this, _name);
    }
}
