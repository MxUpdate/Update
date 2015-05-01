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
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.userinterface.FormData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export / update of web forms.
 *
 * @author The MxUpdate Team
 */
@Test()
public class FormTest
    extends AbstractUITest<FormData>
{
    /**
     * Data provider for test forms.
     *
     * @return object array with all test forms
     */
    @DataProvider(name = "data")
    public Object[][] getForms()
    {
        return this.prepareData("form",
                new Object[]{
                        "simple form with two fields",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field \"1\"")
                                    .getFormTable()
                            .newField("field \"2\"")
                                    .getFormTable()},
                new Object[]{
                        "simple form with complex field",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field")
                                    .setValue("label", "an \"label\"")
                                    .setValue("range", "an \"range\"")
                                    .setValue("href", "an \"url\"")
                                    .setValue("alt", "an \"alt\"")
                                    .setKeyValue("setting", "first \"key\"", "first \"value\"")
                                    .setKeyValue("setting", "second \"key\"", "second \"value\"")
                                    .getFormTable()},
                new Object[]{
                        "form with business object select",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field")
                                    .setValue("businessobject", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "form with relationship select",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field")
                                    .setValue("relationship", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "form with one role",
                        new FormData(this, "hello \" test")
                            .newField("field")
                                    .defData("user", new RoleData(this, "user \"role\""))
                                    .getFormTable()}
        );
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_FORM);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.USR_GROUP);
    }

    @Override()
    protected FormData createNewData(final String _name)
    {
        return new FormData(this, _name);
    }
}
