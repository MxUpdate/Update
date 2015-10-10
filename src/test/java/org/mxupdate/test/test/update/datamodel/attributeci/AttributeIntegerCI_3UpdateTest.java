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

package org.mxupdate.test.test.update.datamodel.attributeci;

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.test.data.datamodel.AttributeIntegerData;
import org.mxupdate.test.data.datamodel.DimensionData;
import org.mxupdate.test.data.datamodel.DimensionData.UnitData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AttributeCI_mxJPO integer attribute CI}
 * export / update.
 *
 * @author The MxUpdate Team
 */
@Test
public class AttributeIntegerCI_3UpdateTest
    extends Abstract_3UpdateWithRangesAndMultiValuesTest<AttributeIntegerData>
{
    /**
     * Data provider for test integer attributes.
     *
     * @return object array with all test attributes
     */
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        final List<Object[]> ret = new ArrayList<>();

        // range value flag
        ret.add(new Object[]{
                "integer attribute with defined rangevalue flag 'true'",
                this.createNewData("hello")
                        .setFlag("rangevalue", true)
                        .defNotSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "integer attribute with defined rangevalue flag 'false'",
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .defNotSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "integer attribute with no defined rangevalue flag 'false' (to check default value)",
                this.createNewData("hello"),
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .defNotSupported(Version.V6R2011x)});

        // dimension
        ret.add(new Object[]{
                "integer attribute with dimension",
                this.createNewData("hello")
                        .setDimension(new DimensionData(this, "Test Dimension")
                                .addUnit(new UnitData("unit")
                                        .setFlag("default", true)
                                        .setValueWithQuots("description", "\"\\\\ hello")
                                        .setValueWithQuots("label", "\"\\\\ label")
                                        .setValueWOQuots("multiplier", "1.0")
                                        .setValueWOQuots("offset", "0.0")))});

        return this.prepareData("integer attribute", "0", "1", ret.toArray(new Object[ret.size()][]));
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
        this.createNewData("test")
                .setFlag("rangevalue", true)
                .create()
                .update((String) null)
                .checkExport()
                .setFlag("rangevalue", false)
                .failureUpdate(ErrorKey.ABSTRACTATTRIBUTE_UPDATE_RANGEVALUEFLAG_UPDATED);
    }

    /**
     * Negative test that update failed for modified dimension.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that update failed for modified dimension")
    public void negativeTestUpdateDimension()
        throws Exception
    {
        this.createNewData("test")
                .setDimension(new DimensionData(this, "Test Dimension")
                        .addUnit(new UnitData("unit")
                                .setFlag("default", true)
                                .setValueWithQuots("description", "\"\\\\ hello")
                                .setValueWithQuots("label", "\"\\\\ label")
                                .setValueWOQuots("multiplier", "1.0")
                                .setValueWOQuots("offset", "0.0")))
                .create()
                .update((String) null)
                .checkExport()
                .setDimension((DimensionData) null)
                .failureUpdate(ErrorKey.ABSTRACTATTRIBUTE_UPDATE_DIMENSION_UPDATED);
    }

    @Override
    protected AttributeIntegerData createNewData(final String _name)
    {
        return new AttributeIntegerData(this, _name).setSingle("kind", this.getKind());
    }

    @Override
    protected String getKind()
    {
        return "integer";
    }
}
