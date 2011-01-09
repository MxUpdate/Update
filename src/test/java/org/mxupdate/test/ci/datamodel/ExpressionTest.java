/*
 * Copyright 2008-2011 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
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
 * @version $Id$
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
