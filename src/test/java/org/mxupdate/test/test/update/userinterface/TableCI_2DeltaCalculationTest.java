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

import java.io.IOException;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.userinterface.TableData;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 * Tests the {@link Table_mxJPO table} delta calculation.
 *
 * @author The MxUpdate Team
 */
public class TableCI_2DeltaCalculationTest
    extends AbstractTest
{
    @DataProvider(name = "data")
    public Object[][] getTables()
    {

        return new Object[][] {
           {   new TableData(this, "Table1"),
                new TableData(this, "Table1")
                        .setValue("description", "\"\\\\ hello")
                        .newField("field").setValue("label", "an \"label\"").getFormTable() },

           {   new TableData(this, "Table1"),
                new TableData(this, "Table1")
                    .setValue("description", "\"\\\\ hello")
                        .newField("field1").setValue("label", "an \"label\"").getFormTable()
                        .newField("field2").setValue("label", "an \"label\"").getFormTable() },

           {   new TableData(this, "Table1")
                    .setValue("description", "description")
                        .newField("field1").setValue("label", "an \"label\"").getFormTable()
                        .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                new TableData(this, "Table1").setValue("description", "description")},

            {   new TableData(this, "Table1")
                    .setValue("description", "description")
                    .newField("field1").setValue("label", "an \"label\"").getFormTable()
                    .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                new TableData(this, "Table1")
                    .setValue("description", "description")
                    .newField("field2").setValue("label", "an \"label\"").getFormTable() },

            {   new TableData(this, "Table1")
                        .setValue("description", "description")
                            .newField("field1").setValue("label", "an \"label\"").getFormTable()
                            .newField("field2").setValue("label", "an \"label\"").getFormTable(),
                new TableData(this, "Table1")
                        .setValue("description", "description")
                        .newField("field1").setValue("label", "an \"label\"").getFormTable()
                        .newField("field2").setValue("label", "an \"label\"").getFormTable()
                        .newField("field3").setValue("label", "an \"label\"").getFormTable() },
            // sort type
            {   new TableData(this, "Table1"),
                new TableData(this, "Table1")
                        .setValue("description", "")
                        .newField("field").setValue("label", "").setSingle("sorttype", "alpha").getFormTable() },
        };
    }

    @Test(dataProvider = "data")
    public void positivTestSimple(final TableData _currentData,
                                  final TableData _targetData)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef(CI.UI_TABLE.updateType);

        // prepare the current form
        final TestTable currentTable = new TestTable(typeDef, _currentData.getName());
        currentTable.create(paramCache);
        currentTable.parseUpdate(this.strip(typeDef, _currentData.ciFile()));
        final MultiLineMqlBuilder mql1;
        if ((currentTable.getTypeDef().getMxAdminSuffix()) != null && !currentTable.getTypeDef().getMxAdminSuffix().isEmpty())  {
            mql1 = MqlBuilder_mxJPO.multiLine("escape mod " + currentTable.getTypeDef().getMxAdminName() + " $1 " + currentTable.getTypeDef().getMxAdminSuffix(), _currentData.getName());
        } else  {
            mql1 = MqlBuilder_mxJPO.multiLine("escape mod " + currentTable.getTypeDef().getMxAdminName() + " $1", _currentData.getName());
        }
        currentTable.calcDelta(paramCache, mql1, new Table_mxJPO(typeDef, _currentData.getName()));
        mql1.exec(paramCache);

        // prepare the target form
        final TestTable targetTable = new TestTable(typeDef, _targetData.getName());
        final String target = this.strip(typeDef, _targetData.ciFile());
        targetTable.parseUpdate(target);

        // delta between current and target
        final MultiLineMqlBuilder mql2;
        if ((currentTable.getTypeDef().getMxAdminSuffix()) != null && !currentTable.getTypeDef().getMxAdminSuffix().isEmpty())  {
            mql2 = MqlBuilder_mxJPO.multiLine("escape mod " + targetTable.getTypeDef().getMxAdminName() + " $1 " + targetTable.getTypeDef().getMxAdminSuffix(), _currentData.getName());
        } else  {
            mql2 = MqlBuilder_mxJPO.multiLine("escape mod " + targetTable.getTypeDef().getMxAdminName() + " $1", targetTable.getName());
        }
        targetTable.calcDelta(paramCache, mql2, currentTable);
        mql2.exec(paramCache);

        // check result
        final TestTable resultTable = new TestTable(typeDef, _currentData.getName());
        resultTable.parse(paramCache);
        final StringBuilder strBldr = new StringBuilder();
        resultTable.write(paramCache, strBldr);
        Assert.assertEquals(this.strip(typeDef, strBldr.toString()), target);
    }

    public String strip(final TypeDef_mxJPO _typeDef,
                        final String _generated)
    {
        final StringBuilder newDef = new StringBuilder();
        final String startIndex = "mxUpdate " + _typeDef.getMxAdminName() + " \"${NAME}\" {";
        final int start = _generated.indexOf(startIndex) + startIndex.length() + 1;
        final int end = _generated.length() - 2;
        if (start < end)
        {
            final String temp = _generated.substring(start, end).toString();
            for (final String line : temp.split("\n"))
            {
                newDef.append(line.trim()).append(' ');
            }
        }
        return newDef.toString();
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_TABLE);
    }

    public class TestTable
        extends Table_mxJPO
    {

        public TestTable(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
        {
            super(_typeDef, _mxName);
        }
        @Override
        protected void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException, SAXException, IOException
        {
            super.parse(_paramCache);
        }

        @Override
        protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                                 final MultiLineMqlBuilder _mql,
                                 final Table_mxJPO _current)
            throws UpdateException_mxJPO
        {
            super.calcDelta(_paramCache, _mql, _current);
        }
        @Override
        protected void write(final ParameterCache_mxJPO _paramCache,
                             final Appendable _out)
            throws IOException
        {
            super.write(_paramCache, _out);
        }
    }
}