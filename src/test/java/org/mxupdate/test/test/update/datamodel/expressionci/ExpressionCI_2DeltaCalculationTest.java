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

package org.mxupdate.test.test.update.datamodel.expressionci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.ExpressionData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Expression_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Expression_mxJPO expression CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class ExpressionCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Expression_mxJPO,ExpressionData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) new package",
                    new ExpressionData(this, "Test"),
                    new ExpressionData(this, "Test").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new ExpressionData(this, "Test").defData("package", new PackageData(this, "TestPackage1")),
                    new ExpressionData(this, "Test").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new ExpressionData(this, "Test").defData("package", new PackageData(this, "TestPackage")),
                    new ExpressionData(this, "Test").defKeyNotDefined("package")},
            {"2) uuid",
                    new ExpressionData(this, "Test"),
                    new ExpressionData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"3a) symbolic name",
                    new ExpressionData(this, "Test"),
                    new ExpressionData(this, "Test").setValue("symbolicname", "expression_123")},
            {"3b) two symbolic name",
                    new ExpressionData(this, "Test"),
                    new ExpressionData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
            {"4) description",
                    new ExpressionData(this, "Test"),
                    new ExpressionData(this, "Test").setValue("description", "abc def")},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_EXPRESSION);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected Expression_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Expression_mxJPO(_name);
    }
}
