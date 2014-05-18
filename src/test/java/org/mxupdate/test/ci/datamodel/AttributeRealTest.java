/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.ci.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.test.data.datamodel.AttributeRealData;
import org.mxupdate.test.data.datamodel.DimensionData;
import org.mxupdate.test.data.datamodel.DimensionData.UnitData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the update and export of real attributes.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeRealTest
    extends AbstractAttributeTest<AttributeRealData>
{
    /**
     * Creates for given <code>_name</code> a new real attribute instance.
     *
     * @param _name     name of the attribute instance
     * @return attribute instance
     */
    @Override()
    protected AttributeRealData createNewData(final String _name)
    {
        return new AttributeRealData(this, _name);
    }

    /**
     * Data provider for test real attributes.
     *
     * @return object array with all test attributes
     */
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        // range value flag
        ret.add(new Object[]{
                "real attribute with defined rangevalue flag 'true'",
                this.createNewData("hello")
                        .setFlag("rangevalue", true)
                        .notSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "real attribute with defined rangevalue flag 'false'",
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .notSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "real attribute with no defined rangevalue flag 'false' (to check default value)",
                this.createNewData("hello"),
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .notSupported(Version.V6R2011x)});

        // dimension
        ret.add(new Object[]{
                "real attribute with defined rangevalue flag 'true'",
                this.createNewData("hello")
                        .setDimension(new DimensionData(this, "Test Dimension")
                                .addUnit(new UnitData("unit")
                                        .setValueWOQuots("default", "true")
                                        .setValueWithQuots("description", "\"\\\\ hello")
                                        .setValueWithQuots("label", "\"\\\\ label")
                                        .setValueWOQuots("multiplier", "1.0")
                                        .setValueWOQuots("offset", "0.0")))});

        return this.prepareData("real attribute", "1.234567", "9.876543", ret.toArray(new Object[ret.size()][]));
    }

    /**
     * Negative test that update failed for modified range value flag.
     *
     * @throws Exception if test failed
     */
    @IssueLink("192")
    @Test(description = "issue #192: negative test that update failed for modified range value flag")
    public void negativeTestUpdateRangeValueFlag()
        throws Exception
    {
        new AttributeRealData(this, "test")
                .setFlag("rangevalue", true)
                .create()
                .update()
                .checkExport()
                .setFlag("rangevalue", false)
                .failureUpdate(UpdateException_mxJPO.Error.ABSTRACTATTRIBUTE_UPDATE_RANGEVALUEFLAG_UPDATED);
    }

    /**
     * Negative test that update failed for modified dimension.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that update failed for modified dimension")
    public void negativeTestUpdateDelimiter()
        throws Exception
    {
        new AttributeRealData(this, "test")
                .setDimension(new DimensionData(this, "Test Dimension")
                        .addUnit(new UnitData("unit")
                                .setValueWOQuots("default", "true")
                                .setValueWithQuots("description", "\"\\\\ hello")
                                .setValueWithQuots("label", "\"\\\\ label")
                                .setValueWOQuots("multiplier", "1.0")
                                .setValueWOQuots("offset", "0.0")))
                .create()
                .update()
                .checkExport()
                .setDimension((DimensionData) null)
                .failureUpdate(UpdateException_mxJPO.Error.ABSTRACTATTRIBUTE_UPDATE_DIMENSION_UPDATED);
    }
}
