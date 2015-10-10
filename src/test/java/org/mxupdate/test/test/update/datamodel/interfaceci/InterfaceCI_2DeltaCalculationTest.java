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

package org.mxupdate.test.test.update.datamodel.interfaceci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeBinaryData;
import org.mxupdate.test.data.datamodel.AttributeBooleanData;
import org.mxupdate.test.data.datamodel.AttributeDateData;
import org.mxupdate.test.data.datamodel.AttributeIntegerData;
import org.mxupdate.test.data.datamodel.AttributeRealData;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Interface_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Interface_mxJPO interface CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class InterfaceCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Interface_mxJPO,InterfaceData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) symbolic name",
                    new InterfaceData(this, "Test"),
                    new InterfaceData(this, "Test").setValue("symbolicname", "interface_123")},
            {"1b) two symbolic name",
                    new InterfaceData(this, "Test"),
                    new InterfaceData(this, "Test").setValue("symbolicname", "interface_123").setValue("symbolicname", "interface_345")},
            {"2a) with property",
                    new InterfaceData(this, "Test"),
                    new InterfaceData(this, "Test").addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))},

            {"101) binary attribute",
                new InterfaceData(this, "Test"),
                new InterfaceData(this, "Test")
                        .addLocalAttribute(
                                new AttributeBinaryData(this, "ATTR1")
                                    .setSingle("kind", "binary")
                                    .setValue("description", "abc def")
                                    .setFlag("hidden", false, Create.ViaFlag)
                                    .setFlag("resetonclone", false, Create.ViaFlag)
                                    .setFlag("resetonrevision", false, Create.ViaFlag)
                                    .setValue("default", "")) },
            {"102) boolean attribute",
                new InterfaceData(this, "Test"),
                new InterfaceData(this, "Test")
                        .addLocalAttribute(
                                new AttributeBooleanData(this, "ATTR1")
                                    .setSingle("kind", "boolean")
                                    .setValue("description", "abc def")
                                    .setFlag("hidden", false, Create.ViaFlag)
                                    .setFlag("multivalue", false, Create.ViaFlag)
                                    .setFlag("resetonclone", false, Create.ViaFlag)
                                    .setFlag("resetonrevision", false, Create.ViaFlag)
                                    .setValue("default", "")) },
            {"103) date attribute",
                new InterfaceData(this, "Test"),
                new InterfaceData(this, "Test")
                        .addLocalAttribute(
                                new AttributeDateData(this, "ATTR1")
                                    .setSingle("kind", "date")
                                    .setValue("description", "abc def")
                                    .setFlag("hidden", false, Create.ViaFlag)
                                    .setFlag("multivalue", false, Create.ViaFlag)
                                    .setFlag("resetonclone", false, Create.ViaFlag)
                                    .setFlag("resetonrevision", false, Create.ViaFlag)
                                    .setFlag("rangevalue", false, Create.ViaFlag)
                                    .setValue("default", "")) },
            {"104) integer attribute",
                new InterfaceData(this, "Test"),
                new InterfaceData(this, "Test")
                        .addLocalAttribute(
                                new AttributeIntegerData(this, "ATTR1")
                                    .setSingle("kind", "integer")
                                    .setValue("description", "abc def")
                                    .setFlag("hidden", false, Create.ViaFlag)
                                    .setFlag("multivalue", false, Create.ViaFlag)
                                    .setFlag("resetonclone", false, Create.ViaFlag)
                                    .setFlag("resetonrevision", false, Create.ViaFlag)
                                    .setFlag("rangevalue", false, Create.ViaFlag)
                                    .setValue("default", "")) },
            {"105) real attribute",
                new InterfaceData(this, "Test"),
                new InterfaceData(this, "Test")
                        .addLocalAttribute(
                                new AttributeRealData(this, "ATTR1")
                                    .setSingle("kind", "real")
                                    .setValue("description", "abc def")
                                    .setFlag("hidden", false, Create.ViaFlag)
                                    .setFlag("multivalue", false, Create.ViaFlag)
                                    .setFlag("resetonclone", false, Create.ViaFlag)
                                    .setFlag("resetonrevision", false, Create.ViaFlag)
                                    .setFlag("rangevalue", false, Create.ViaFlag)
                                    .setValue("default", "")) },
            {"106) string attribute",
                new InterfaceData(this, "Test"),
                new InterfaceData(this, "Test")
                        .addLocalAttribute(
                                new AttributeStringData(this, "ATTR1")
                                    .setSingle("kind", "string")
                                    .setValue("description", "abc def")
                                    .setFlag("hidden", false, Create.ViaFlag)
                                    .setFlag("multivalue", false, Create.ViaFlag)
                                    .setFlag("resetonclone", false, Create.ViaFlag)
                                    .setFlag("resetonrevision", false, Create.ViaFlag)
                                    .setFlag("multiline", false, Create.ViaFlag)
                                    .setSingle("maxlength", "0")
                                    .setValue("default", "")) },
       };
    }

    @Override
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_INTERFACE);
    }

    @Override()
    protected Interface_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new Interface_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_INTERFACE.updateType), _name);
    }
}
