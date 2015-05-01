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

package org.mxupdate.test.ci.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.userinterface.InquiryData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of inquiries.
 *
 * @author The MxUpdate Team
 */
@Test()
public class InquiryTest
    extends AbstractUITest<InquiryData>
{
    /**
     * Data provider for test inquiries.
     *
     * @return object array with all test inquiries
     */
    @DataProvider(name = "data")
    public Object[][] getInquiries()
    {
        return this.prepareData("inquiry",
                new Object[]{
                        "complex inquiry with all values and three arguments",
                        new InquiryData(this, "Test1")
                             .setValue("description", "test description")
                             .setValue("pattern", "${ID}|*")
                             .setValue("format", "${ID}")
                             .setKeyValue("argument", "TYPE", "MY_TYPE")
                             .setKeyValue("argument", "NAME", "MY_NAME")
                             .setKeyValue("argument", "REVISION", "MY_REVISION")},
                new Object[]{
                        "complex inquiry with all values and one argument",
                        new InquiryData(this, "Test3 \" test")
                             .setValue("description", "test description \" '")
                             .setValue("pattern", "patternprefix \" patternsuffix")
                             .setValue("format", "formatprefix \" formatsuffix")
                             .setKeyValue("argument", "ARGUMENT \" ARGSUFFIX", "argumentprefix \" argumentsuffix")}
        );
    }

    /**
     * Positive test for update of code.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for update of code")
    public void positiveTestCodeUpdate()
        throws Exception
    {
        final String code = "print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump";

        final InquiryData inquiry = new InquiryData(this, "Test").setValue("code", code).update("");

        Assert.assertEquals(code, this.mql("print inquiry " + inquiry.getName() + " select code dump"));

        final ExportParser exportParser = inquiry.export();

        // remove code and update via export
        this.mql("mod inquiry " + inquiry.getName() + " code ''");
        inquiry.updateWithCode(exportParser.getCode(), "");

        // and check result (so that the export of code is also checked!)
        Assert.assertEquals(code, this.mql("print inquiry " + inquiry.getName() + " select code dump"));
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_INQUIRY);
    }

    @Override()
    protected InquiryData createNewData(final String _name)
    {
        return new InquiryData(this, _name);
    }
}
