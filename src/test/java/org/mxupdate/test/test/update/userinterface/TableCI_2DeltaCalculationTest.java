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
import org.mxupdate.test.data.userinterface.TableData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

/**
 * Tests the {@link Table_mxJPO table CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
public class TableCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Table_mxJPO,TableData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
           {"",   new TableData(this, "Table1"),
                new TableData(this, "Table1")
                        .setValue("description", "\"\\\\ hello")
                        .newField("field").setValue("label", "an \"label\"").getFormTable() },

           {"",   new TableData(this, "Table1"),
                new TableData(this, "Table1")
                    .setValue("description", "\"\\\\ hello")
                        .newField("field1").setValue("label", "an \"label\"").getFormTable()
                        .newField("field2").setValue("label", "an \"label\"").getFormTable() },

           {"",   new TableData(this, "Table1")
                    .setValue("description", "description")
                        .newField("field1").setValue("label", "an \"label\"").getFormTable()
                        .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                new TableData(this, "Table1").setValue("description", "description")},

            {"",   new TableData(this, "Table1")
                    .setValue("description", "description")
                    .newField("field1").setValue("label", "an \"label\"").getFormTable()
                    .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                new TableData(this, "Table1")
                    .setValue("description", "description")
                    .newField("field2").setValue("label", "an \"label\"").getFormTable() },

            {"",   new TableData(this, "Table1")
                        .setValue("description", "description")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                new TableData(this, "Table1")
                        .setValue("description", "description")
                        .newField("field1").setValue("label", "an \"label\"").getFormTable()
                        .newField("field2").setValue("label", "an \"label\"").getFormTable()
                        .newField("field3").setValue("label", "an \"label\"").getFormTable() },
            // sort type
            {"",   new TableData(this, "Table1"),
                new TableData(this, "Table1")
                        .setValue("description", "")
                        .newField("field").setValue("label", "").setSingle("sorttype", "alpha").getFormTable() },
        };
    }

    @Override
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_TABLE);
    }

    @Override()
    protected Table_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                        final String _name)
    {
        return new Table_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_TABLE.updateType), _name);
    }
}
