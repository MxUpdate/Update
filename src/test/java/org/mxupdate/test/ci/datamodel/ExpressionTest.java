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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.datamodel.ExpressionData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for expression exports and updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class ExpressionTest
    extends AbstractDataExportUpdate<ExpressionData>
{
    /**
     * Creates for given <code>_name</code> a new expression instance.
     *
     * @param _name     name of the expression instance
     * @return expression instance
     */
    @Override()
    protected ExpressionData createNewData(final String _name)
    {
        return new ExpressionData(this, _name);
    }

    /**
     * Data provider for test expressions.
     *
     * @return object array with all test expressions
     */
    @DataProvider(name = "data")
    public Object[][] getExpressions()
    {
        return this.prepareData("expression",
                new Object[]{
                        "expression without anything (to test required fields)",
                        new ExpressionData(this, "hello \" test")},
                new Object[]{
                        "expression with other symbolic name",
                        new ExpressionData(this, "hello \" test")
                                .setSymbolicName("expression_Test")},

                new Object[]{
                        "expression with value",
                        new ExpressionData(this, "hello \" test")
                                .setValue("value", "value")},
                new Object[]{
                        "expression with escaped value",
                        new ExpressionData(this, "hello \" test")
                                .setValue("value", "value \" value")}
        );
    }

    /**
     * Removes the MxUpdate expression.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_EXPRESSION);
    }
}
