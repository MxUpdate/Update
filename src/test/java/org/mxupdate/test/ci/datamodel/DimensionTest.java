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

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.datamodel.DimensionData;
import org.mxupdate.test.data.datamodel.DimensionData.UnitData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for dimension exports and updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class DimensionTest
    extends AbstractDataExportUpdate<DimensionData>
{
    /**
     * Creates for given {@code _name} a new dimension instance.
     *
     * @param _name     name of the dimension instance
     * @return dimension instance
     */
    @Override()
    protected DimensionData createNewData(final String _name)
    {
        return new DimensionData(this, _name);
    }

    /**
     * Data provider for test dimensions.
     *
     * @return object array with all test dimensions
     */
    @DataProvider(name = "data")
    public Object[][] getDimensions()
    {
        return this.prepareData("dimension",
                new Object[]{
                        "dimension without anything (to test required fields)",
                        new DimensionData(this, "hello \" test")},
                new Object[]{
                        "dimension with other symbolic name",
                        new DimensionData(this, "hello \" test")
                                .setSymbolicName("dimension_Test")},
                new Object[]{
                        "dimension with complex description",
                        new DimensionData(this, "hello \" test")
                                .setValue("description", "\"\\\\ hello")},
                new Object[]{
                        "dimension with unit",
                        new DimensionData(this, "test")
                                .addUnit(new UnitData("unit")
                                        .setValueWOQuots("default", "true")
                                        .setValueWithQuots("description", "\"\\\\ hello")
                                        .setValueWithQuots("label", "\"\\\\ label")
                                        .setValueWOQuots("multiplier", "1.0")
                                        .setValueWOQuots("offset", "0.0"))},
                new Object[]{
                        "dimension with unit (and multiplier 1.0E9)",
                        new DimensionData(this, "test")
                                .addUnit(new UnitData("unit1")
                                        .setValueWOQuots("default", "true")
                                        .setValueWithQuots("description", "hello 1")
                                        .setValueWithQuots("label", "label 1")
                                        .setValueWOQuots("multiplier", "1.0")
                                        .setValueWOQuots("offset", "0.0"))
                                .addUnit(new UnitData("unit2")
                                        .setValueWithQuots("description", "hello 2")
                                        .setValueWithQuots("label", "label 2")
                                        .setValueWOQuots("multiplier", "1.0E9")
                                        .setValueWOQuots("offset", "0.0"))},
                new Object[]{
                        "dimension with unit (and offset 1.0E10)",
                        new DimensionData(this, "test")
                                .addUnit(new UnitData("unit1")
                                        .setValueWOQuots("default", "true")
                                        .setValueWithQuots("description", "hello 1")
                                        .setValueWithQuots("label", "label 1")
                                        .setValueWOQuots("multiplier", "1.0")
                                        .setValueWOQuots("offset", "0.0"))
                                .addUnit(new UnitData("unit2")
                                        .setValueWithQuots("description", "hello 2")
                                        .setValueWithQuots("label", "label 2")
                                        .setValueWOQuots("multiplier", "1.0")
                                        .setValueWOQuots("offset", "1.0E10"))}
        );
    }

    /**
     * Removes the MxUpdate dimension.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_DIMENSION);
    }

    @Override()
    protected DimensionData createCleanNewData(final DimensionData _original)
    {
        final DimensionData ret = super.createCleanNewData(_original);

        // units can not be removed....
        for (final UnitData unit : _original.getUnits())  {
            final UnitData retUnit = new UnitData(unit.getName());
            for (final String key : new String[]{"default", "multiplier", "offset"})  {
                if (unit.getValuesWOQuots().containsKey(key))  {
                    retUnit.setValueWOQuots(key, unit.getValuesWOQuots().get(key));
                }
            }
            ret.addUnit(retUnit);
        }

        return ret;
    }
}
