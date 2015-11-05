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

package org.mxupdate.test.test.update.datamodel.pathtype;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.PathTypeData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.PathType_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link PathType_mxJPO path type CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class PathTypeCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<PathType_mxJPO,PathTypeData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0) simple",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")},

            {"1) uuid",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},

            {"2a) symbolic name",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test").setValue("symbolicname", "expression_123")},
            {"2b) two symbolic name",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},

            {"3) description",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test").setValue("description", "abc def")},

            {"4) global attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test").defData("attribute", new AttributeData(this, "Test Attribute").setSingle("kind", "string"))},

            {"5) with property",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test").addProperty(new PropertyDef("property"))},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // from / to direction

            {"101a) from cardinality one",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "one")},
            {"101b) from cardinality many",
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "one"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")},

            {"102a) from type all",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromSingle("type", "all")},
            {"102b) from specific type",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromData("type", new TypeData(this, "Type"))},
            {"102c) from changing from type all to specific type",
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromSingle("type", "all"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromData("type", new TypeData(this, "Type"))},

            {"103a) from relation all",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromSingle("relationship", "all")},
            {"103b) from specific relation",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromData("relationship", new RelationshipData(this, "Relationship"))},
            {"103c) from changing from relation all to specific relation",
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromSingle("type", "all"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4FromData("relationship", new RelationshipData(this, "Relationship"))},

            {"112a) to type all",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToSingle("type", "all")},
            {"112b) to specific type",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToData("type", new TypeData(this, "Type"))},
            {"112c) to changing from type all to specific type",
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToSingle("type", "all"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToData("type", new TypeData(this, "Type"))},

            {"113a) to relation all",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToSingle("relationship", "all")},
            {"113b) to specific relation",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToData("relationship", new RelationshipData(this, "Relationship"))},
            {"113c) to changing from relation all to specific relation",
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToSingle("type", "all"),
                    new PathTypeData(this, "Test")
                            .def4FromSingle("cardinality", "many")
                            .def4ToData("relationship", new RelationshipData(this, "Relationship"))},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // local attribute

            {"201) local binary attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "binary")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"202) local boolean attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "boolean")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"203) local date attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "date")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setFlag("rangevalue", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"204) local integer attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "integer")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setFlag("rangevalue", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"205) local real attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "real")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setFlag("rangevalue", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"206) local string attribute",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "string")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setFlag("multiline", false, Create.ViaFlag)
                                        .setSingle("maxlength", "0")
                                        .setValue("default", "")) },
            {"207) attribute with uuid",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "boolean")
                                        .setValue("uuid", "UUID")
                                        .setValue("description", "")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"208) attribute with symbolic name",
                    new PathTypeData(this, "Test"),
                    new PathTypeData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "boolean")
                                        .setValue("symbolicname", "attribute_" + AbstractTest.PREFIX + "Test." + AbstractTest.PREFIX + "ATTR1")
                                        .setValue("description", "")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setValue("default", "")) },
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_PATHTYPE);  // as first, so that local attributes of path types are deleted!
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
        this.cleanup(AbstractTest.CI.DM_TYPE);
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
    }

    @Override
    protected PathType_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                           final String _name)
    {
        return new PathType_mxJPO(_name);
    }
}
