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
                                        .setValueWOQuots("offset", "0.0"))}
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
            for (final String key : new String[]{"default", "multiplier"})  {
                if (unit.getValuesWOQuots().containsKey(key))  {
                    retUnit.setValueWOQuots(key, unit.getValuesWOQuots().get(key));
                }
            }
            ret.addUnit(retUnit);
        }

        return ret;
    }
}
