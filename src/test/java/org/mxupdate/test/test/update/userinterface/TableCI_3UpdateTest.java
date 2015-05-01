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

package org.mxupdate.test.test.update.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ci.userinterface.AbstractUITest;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.userinterface.TableData;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Table_mxJPO table CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class TableCI_3UpdateTest
    extends AbstractUITest<TableData>
{
    /**
     * Data provider for test tables.
     *
     * @return object array with all test tables
     */
    @DataProvider(name = "data")
    public Object[][] getTables()
    {
        return this.prepareData("table",
                new Object[]{
                        "1) simple table with two fields",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field \"1\"").setValue("name", "1").getFormTable()
                            .newField("field \"2\"").setValue("name", "2").getFormTable()},
                new Object[]{
                        "2) simple table with complex field",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field")
                                    .setValue("label", "an \"label\"")
                                    .setValue("range", "an \"range\"")
                                    .setValue("href", "an \"url\"")
                                    .setValue("alt", "an \"alt\"")
                                    .setKeyValue("setting", "first \"key\"", "first \"value\"")
                                    .setKeyValue("setting", "second \"key\"", "second \"value\"")
                                    .getFormTable()},
                new Object[]{
                        "3a) table with business object select",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field")
                                    .setValue("businessobject", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "3b) table with relationship select",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field")
                                    .setValue("relationship", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "4) table with one role",
                        new TableData(this, "hallo \" test")
                            .newField("field")
                                    .defData("user", new RoleData(this, "user \"role\""))
                                    .getFormTable()},
                new Object[]{
                        "5) table with one hidden column",
                        new TableData(this, "hallo \" test")
                            .newField("field")
                                    .setFlag("hidden", true)
                                    .getFormTable()},
                new Object[]{
                        "6a) table column with sorttype alpha",
                        new TableData(this, "hallo \" test").newField("field").setValue("label", "").setSingle("sorttype", "alpha").getFormTable()},
                new Object[]{
                        "6b) table column with sorttype numeric",
                        new TableData(this, "hallo \" test").newField("field").setSingle("sorttype", "numeric").getFormTable()},
                new Object[]{
                        "6c) table column with sorttype other",
                        new TableData(this, "hallo \" test").newField("field").setSingle("sorttype", "other").getFormTable()},
                new Object[]{
                        "6d) table column with sorttype none",
                        new TableData(this, "hallo \" test").newField("field").setSingle("sorttype", "none").getFormTable(),
                        new TableData(this, "hallo \" test").newField("field").getFormTable()}
        );
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_TABLE);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.USR_GROUP);
    }

    @Override()
    protected TableData createNewData(final String _name)
    {
        return new TableData(this, _name);
    }
}
