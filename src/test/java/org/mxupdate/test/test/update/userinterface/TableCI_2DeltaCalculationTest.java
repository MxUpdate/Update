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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.userinterface.TableData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Table_mxJPO table CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class TableCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Table_mxJPO,TableData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) uuid",
                    new TableData(this, "Test"),
                    new TableData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic names
            {"2a) symbolic name",
                    new TableData(this, "Test"),
                    new TableData(this, "Test").setValue("symbolicname", "expression_123")},
            {"2b) two symbolic name",
                    new TableData(this, "Test"),
                    new TableData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
            // column add / remove
            {"3a) add a column",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field").setValue("label", "an \"label\"").getFormTable() },
            {"3b) add tow column",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable() },
            {"3c) remove all columns",
                    new TableData(this, "Table1")
                            .setValue("description", "description")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                    new TableData(this, "Table1").setValue("description", "description")},
            {"3d) remove one column",
                    new TableData(this, "Table1")
                            .setValue("description", "description")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                    new TableData(this, "Table1")
                            .setValue("description", "description")
                            .newField("field2").setValue("label", "an \"label\"").getFormTable() },
            {"3e) add one column",
                    new TableData(this, "Table1")
                            .setValue("description", "description")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                    new TableData(this, "Table1")
                            .setValue("description", "description")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable()
                            .newField("field3").setValue("label", "an \"label\"").getFormTable() },
            // column set values
            {"10a) set business object expression",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("businessobject", "value.new").getFormTable()},
            {"10b) set relationship expression",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("relationship", "value.new").getFormTable()},
            {"10c) set select expression",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("select", "value.new").getFormTable()},
            {"11) set label",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("label", "value.new").getFormTable()},
            {"12) new field with alt",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("alt", "value.new").getFormTable()},
            {"13) new field with href",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("href", "value.new").getFormTable()},
            {"14) new field with range href",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setValue("range", "value.new").getFormTable()},
            {"15) set sort type",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setSingle("sorttype", "alpha").getFormTable()},
            {"16) set hidden flag",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setFlag("hidden", true).getFormTable()},
            {"17) set user",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").defData("user", new PersonAdminData(this, "Test.Old")).getFormTable()},
            {"18) set setting",
                    new TableData(this, "Table1"),
                    new TableData(this, "Table1")
                            .newField("field").setKeyValue("setting", "key", "value.new").getFormTable()},
            // column update values
            {"20a) update business object expression",
                    new TableData(this, "Table1")
                            .newField("field").setValue("businessobject", "label.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("businessobject", "label.new").getFormTable()},
            {"20b) update relationship expression",
                    new TableData(this, "Table1")
                            .newField("field").setValue("relationship", "label.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("relationship", "label.new").getFormTable()},
            {"20c) update select expression",
                    new TableData(this, "Table1")
                            .newField("field").setValue("select", "label.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("select", "label.new").getFormTable()},
            {"21) update label",
                    new TableData(this, "Table1")
                            .newField("field").setValue("label", "label.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("label", "label.new").getFormTable()},
            {"22) update alt",
                    new TableData(this, "Table1")
                            .newField("field").setValue("alt", "label.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("alt", "label.new").getFormTable()},
            {"23) update hraf",
                    new TableData(this, "Table1")
                            .newField("field").setValue("href", "label.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("href", "label.new").getFormTable()},
            {"24) update range href",
                    new TableData(this, "Table1")
                            .newField("field").setValue("range", "value.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setValue("range", "value.new").getFormTable()},
            {"25) update sort type",
                    new TableData(this, "Table1")
                            .newField("field").setSingle("sorttype", "alpha").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setSingle("sorttype", "numeric").getFormTable()},
            {"26) update hidden flag",
                    new TableData(this, "Table1")
                            .newField("field").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setFlag("hidden", true).getFormTable()},
            {"27) update user",
                    new TableData(this, "Table1")
                            .newField("field").defData("user", new PersonAdminData(this, "Test.Old")).getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").defData("user", new PersonAdminData(this, "Test.New")).getFormTable()},
            {"28) update setting",
                    new TableData(this, "Table1")
                            .newField("field").setKeyValue("setting", "key", "value.old").getFormTable(),
                    new TableData(this, "Table1")
                            .newField("field").setKeyValue("setting", "key", "value.new").getFormTable()},
        };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.UI_TABLE);
    }

    @Override
    protected Table_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                        final String _name)
    {
        return new Table_mxJPO(_name);
    }
}
