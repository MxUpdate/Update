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

package org.mxupdate.test.test.update.datamodel.dimensionci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.DimensionData;
import org.mxupdate.test.data.datamodel.DimensionData.UnitData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Dimension_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Dimension_mxJPO dimension CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class DimensionCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Dimension_mxJPO,DimensionData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) uuid",
                    new DimensionData(this, "Test"),
                    new DimensionData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"2a) symbolic name",
                    new DimensionData(this, "Test"),
                    new DimensionData(this, "Test").setValue("symbolicname", "dimension_123")},
            {"2b) two symbolic name",
                    new DimensionData(this, "Test"),
                    new DimensionData(this, "Test").setValue("symbolicname", "dimension_123").setValue("symbolicname", "dimension_345")},
            {"3) description",
                    new DimensionData(this, "Test"),
                    new DimensionData(this, "Test").setValue("description", "abc def")},
            {"4) unit",
                    new DimensionData(this, "Test"),
                    new DimensionData(this, "Test")
                            .addUnit(new UnitData("unit")
                                    .setFlag("default", true)
                                    .setValueWithQuots("description", "\"\\\\ hello")
                                    .setValueWithQuots("label", "\"\\\\ label")
                                    .setValueWOQuots("multiplier", "1.0")
                                    .setValueWOQuots("offset", "0.0"))},
            {"5) unit with uuid",
                    new DimensionData(this, "Test"),
                    new DimensionData(this, "Test")
                            .addUnit(new UnitData("unit")
                                    .setFlag("default", true)
                                    .setValueWithQuots("uuid", "FDA75674979211E6AE2256B6B6499611")
                                    .setValueWithQuots("description", "\"\\\\ hello")
                                    .setValueWithQuots("label", "\"\\\\ label")
                                    .setValueWOQuots("multiplier", "1.0")
                                    .setValueWOQuots("offset", "0.0"))},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_DIMENSION);
    }

    @Override
    protected Dimension_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Dimension_mxJPO(_name);
    }
}
