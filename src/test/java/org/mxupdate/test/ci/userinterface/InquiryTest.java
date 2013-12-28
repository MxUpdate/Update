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
import org.mxupdate.test.data.userinterface.InquiryData;
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
                             .setArgument("TYPE", "MY_TYPE")
                             .setArgument("NAME", "MY_NAME")
                             .setArgument("REVISION", "MY_REVISION")
                             .setCode("print bus '${TYPE}' '${NAME}' '${REVISION}' select id dump")},
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
}
