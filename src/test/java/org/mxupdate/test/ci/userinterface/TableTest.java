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
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.userinterface.TableData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export / update of web tables.
 *
 * @author The MxUpdate Team
 */
@Test()
public class TableTest
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
                        "simple table with two fields",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field \"1\"").getFormTable()
                            .newField("field \"2\"").getFormTable()},
                new Object[]{
                        "simple table with complex field",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field")
                                    .setValue("label", "an \"label\"")
                                    .setValue("range", "an \"range\"")
                                    .setValue("href", "an \"url\"")
                                    .setValue("alt", "an \"alt\"")
                                    .setValue("update", "an \"alt\"")
                                    .setSetting("first \"key\"", "first \"value\"")
                                    .setSetting("second \"key\"", "second \"value\"")
                                    .getFormTable()},
                new Object[]{
                        "table with business object select",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field")
                                    .setValue("businessobject", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "table with relationship select",
                        new TableData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")
                            .newField("field")
                                    .setValue("relationship", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "table with one role and one group",
                        new TableData(this, "hallo \" test")
                            .newField("field")
                                    .addUser(new RoleData(this, "user \"role\""))
                                    .addUser(new GroupData(this, "user \"group\""))
                                    .getFormTable()},
                new Object[]{
                        "table with one hidden column",
                        new TableData(this, "hallo \" test")
                            .newField("field")
                                    .setFlag("hidden", true)
                                    .getFormTable()}
        );
    }

    /**
     * Cleanup all test web tables.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_TABLE);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.USR_GROUP);
    }

    /**
     * Creates for given <code>_name</code> a new table instance.
     *
     * @param _name     name of the table instance
     * @return table instance
     */
    @Override()
    protected TableData createNewData(final String _name)
    {
        return new TableData(this, _name);
    }
}
