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

package org.mxupdate.test.test.update.user;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ci.user.AbstractUserTest;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of administration persons (without
 * related business object).
 *
 * @author The MxUpdate Team
 */
@Test()
public class PersonAdminCI_3UpdateTest
    extends AbstractUserTest<PersonAdminData>
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return this.prepareData("person",
                new Object[]{
                        "1) simple person",
                        this.createNewData("hello \" test")
                                .setValue("comment", "hallo")},
                new Object[]{
                        "2) complex person",
                        this.createNewData("hello \" test")
                                .setValue("fullname", "test person \"full name\"")
                                .setValue("comment", "test person \"comment\"")
                                .setValue("address", "test person \"address\"")
                                .setValue("phone", "test person \"phone\"")}
        );
    }

    /**
     * Positive test if an ignored attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if products update is ignored")
    public void positiveTestIgnoreProducts()
        throws Exception
    {
        final String from, to;
        if (this.getVersion().max(Version.V6R2013x))  {
            from = "{ZCC ZCS}";
            to = "{ZCS}";
        } else   {
            from = "{CNV CSV}";
            to = "{CNV}";
        }

        this.createNewData("Test")
                .create();
        this.createNewData("Test")
                .setSingle("product", from)
                .update("");
        this.createNewData("Test")
                .setSingle("product", to)
                .update("", ValueKeys.UserPersonIgnoreProducts.name(), "*");
        this.createNewData("Test")
                .setSingle("product", from)
                .checkExport();
    }

    /**
     * Positive test if an ignored attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if mail update is ignored")
    public void positiveTestIgnoreWantsEmail()
        throws Exception
    {
        this.createNewData("Test")
                .create();
        this.createNewData("Test")
                .setFlag("email", false)
                .update("");
        this.createNewData("Test")
                .setFlag("email", true)
                .update("", ValueKeys.UserPersonIgnoreWantsEmail.name(), "*");
        this.createNewData("Test")
                .setFlag("email", false)
                .checkExport();
    }

    /**
     * Positive test if an ignored attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test iconmail update is ignored")
    public void positiveTestIgnoreWantsIconMail()
        throws Exception
    {
        this.createNewData("Test")
                .create();
        this.createNewData("Test")
                .setFlag("iconmail", false)
                .update("");
        this.createNewData("Test")
                .setFlag("iconmail", true)
                .update("", ValueKeys.UserPersonIgnoreWantsIconMail.name(), "*");
        this.createNewData("Test")
                .setFlag("iconmail", false)
                .checkExport();
    }

    @Override()
    protected PersonAdminData createNewData(final String _name)
    {
        return new PersonAdminData(this, _name);
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.SYS_SITE);
    }
}
