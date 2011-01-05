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

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the update and export of string attributes.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test()
public class AttributeStringTest
    extends AbstractAttributeTest<AttributeStringData>
{
    /**
     * Creates for given <code>_name</code> a new string attribute instance.
     *
     * @param _name     name of the attribute instance
     * @return attribute instance
     */
    @Override()
    protected AttributeStringData createNewData(final String _name)
    {
        return new AttributeStringData(this, _name);
    }

    /**
     * Data provider for test string attributes.
     *
     * @return object array with all test string attributes
     */
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        // max length property
        ret.add(new Object[]{
                "string attribute with max length 0",
                this.createNewData("hello")
                        .setValue("maxlength", "0")});
        ret.add(new Object[]{
                "string attribute with max length 5",
                this.createNewData("hello")
                        .setValue("maxlength", "5")});

        // multiline flag
        ret.add(new Object[]{
                "string attribute with multiline flag true",
                this.createNewData("hello")
                        .setFlag("multiline", true)});
        ret.add(new Object[]{
                "string attribute with multiline flag false",
                this.createNewData("hello")
                        .setFlag("multiline", false)});

        return super.prepareData("string attribute", "A \" B", "BCD", ret.toArray(new Object[ret.size()][]));
    }

    /**
     * Cleanup all test attributes.
     *
     * @throws MatrixException if cleanup failed
     */
    @Override()
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.DM_ATTRIBUTE_BOOLEAN);
        this.cleanup(CI.DM_ATTRIBUTE_DATE);
        this.cleanup(CI.DM_ATTRIBUTE_INTEGER);
        this.cleanup(CI.DM_ATTRIBUTE_REAL);
        this.cleanup(CI.DM_ATTRIBUTE_STRING);
        this.cleanup(CI.PRG_MQL_PROGRAM);
    }
}
