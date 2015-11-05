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

package org.mxupdate.test.test.update.datamodel.relationshipci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.PathTypeData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Relationship_mxJPO relationship CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class RelationshipCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Relationship_mxJPO,RelationshipData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) uuid",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"2a) symbolic name",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").setValue("symbolicname", "expression_123")},
            {"2b) two symbolic name",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
            {"3) description",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").setValue("description", "abc def")},
            // derived
            {"5a) derived",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("meaning", "abc")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("meaning")},
            {"5.10) derived from meaning",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("meaning", "abc")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("meaning")},
            {"5.11) derived from cardinality one",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("cardinality", "one")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("cardinality")},
            {"5.12) derived from cardinality many",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("cardinality", "many")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("cardinality")},
            {"5.13) derived from revision none",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("revision", "none")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("revision")},
            {"5.14) derived from revision float",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("revision", "float")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("revision")},
            {"5.15) derived from revision replicate",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("revision", "replicate")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("revision")},
            {"5.16) derived from clone none",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("clone", "none")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("clone")},
            {"5.17) derived from clone float",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("clone", "float")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("clone")},
            {"5.18) derived from clone replicate",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defString("clone", "replicate")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("clone")},
            {"5.19) derived from propagatemodify true",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defFlag("propagatemodify", true)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("propagatemodify").from().defNotDefined("!propagatemodify")},
            {"5.20) derived from propagatemodify false",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defFlag("propagatemodify", false)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("propagatemodify").from().defNotDefined("!propagatemodify")},
            {"5.21) derived from propagateconnection true",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defFlag("propagateconnection", true)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("propagateconnection").from().defNotDefined("!propagateconnection")},
            {"5.22) derived from propagateconnection false",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defFlag("propagateconnection", false)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("propagateconnection").from().defNotDefined("!propagateconnection")},
            {"5.23) derived from type all",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defSingle("type", "all")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defNotDefined("type")},
            {"5.24) derived from type single type",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").from().defSingle("type", "all")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").from().defData("type", new TypeData(this, "Type"))},
            {"5.30) derived to meaning",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("meaning", "abc")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("meaning")},
            {"5.31) derived to cardinality one",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("cardinality", "one")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("cardinality")},
            {"5.32) derived to cardinality many",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("cardinality", "many")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("cardinality")},
            {"5.33) derived to revision none",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("revision", "none")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("revision")},
            {"5.34) derived to revision float",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("revision", "float")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("revision")},
            {"5.35) derived to revision replicate",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("revision", "replicate")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("revision")},
            {"5.36) derived to clone none",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("clone", "none")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("clone")},
            {"5.37) derived to clone float",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("clone", "float")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("clone")},
            {"5.38) derived to clone replicate",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defString("clone", "replicate")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("clone")},
            {"5.39) derived to propagatemodify true",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defFlag("propagatemodify", true)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("propagatemodify").to().defNotDefined("!propagatemodify")},
            {"5.40) derived to propagatemodify false",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defFlag("propagatemodify", false)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("propagatemodify").to().defNotDefined("!propagatemodify")},
            {"5.41) derived to propagateconnection true",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defFlag("propagateconnection", true)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("propagateconnection").to().defNotDefined("!propagateconnection")},
            {"5.42) derived to propagateconnection false",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defFlag("propagateconnection", false)),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("propagateconnection").to().defNotDefined("!propagateconnection")},
            {"5.43) derived to type all",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defSingle("type", "all")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defNotDefined("type")},
            {"5.44) derived to type single type",
                    new RelationshipData(this, "Test").defData("derived", new RelationshipData(this, "Root").to().defSingle("type", "all")),
                    new RelationshipData(this, "Test").setValue("derived", AbstractTest.PREFIX + "Root").to().defData("type", new TypeData(this, "Type"))},

            // from propagatemodify flag
            {"24a) relationship with from propagate modify true",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").from().defFlag("propagatemodify", true)},
            {"24b) relationship with from propagate modify false",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").from().defFlag("propagatemodify", false)},
            // from propagateconnection flag
            {"25a) relationship with from propagate connection true",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").from().defFlag("propagateconnection", true)},
            {"25b) relationship with from propagate connection false",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").from().defFlag("propagateconnection", false)},
            // to propagatemodify flag
            {"34a) relationship with to propagate modify true",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").to().defFlag("propagatemodify", true)},
            {"34b) relationship with to propagate modify false",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").to().defFlag("propagatemodify", false)},
            // to propagateconnection flag
            {"35a) relationship with to propagate connection true",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").to().defFlag("propagateconnection", true)},
            {"35b) relationship with to propagate connection false",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test").to().defFlag("propagateconnection", false)},

            {"101) binary attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "binary")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"102) boolean attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalAttribute(
                                    new AttributeData(this, "ATTR1")
                                        .setSingle("kind", "boolean")
                                        .setValue("description", "abc def")
                                        .setFlag("hidden", false, Create.ViaFlag)
                                        .setFlag("multivalue", false, Create.ViaFlag)
                                        .setFlag("resetonclone", false, Create.ViaFlag)
                                        .setFlag("resetonrevision", false, Create.ViaFlag)
                                        .setValue("default", "")) },
            {"103) date attribute",
                    new TypeData(this, "Test"),
                    new TypeData(this, "Test")
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
            {"104) integer attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
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
            {"105) real attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
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
            {"106) string attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
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
            {"107) attribute with uuid",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
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
            {"108) attribute with symbolic names",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
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

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // local path type

            {"301) simple local path type",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalPathType(new PathTypeData(this, "PathType").def4FromSingle("cardinality", "many")) },
            {"302) local path type with uuid",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalPathType(new PathTypeData(this, "PathType").setValue("uuid", "UUID").def4FromSingle("cardinality", "many")) },
            {"303) local path type with global attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalPathType(new PathTypeData(this, "PathType")
                                    .def4FromSingle("cardinality", "many")
                                    .defData("attribute", new AttributeData(this, "PathType Attribute").setSingle("kind", "string"))) },
            {"304a) local path type with embedded local attribute",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalPathType(new PathTypeData(this, "PathType")
                                    .def4FromSingle("cardinality", "many")
                                    .addLocalAttribute(new AttributeData(this, "PathType Attribute").setSingle("kind", "string"))) },
            {"304b) local path type with embedded local attribute with uuid",
                    new RelationshipData(this, "Test"),
                    new RelationshipData(this, "Test")
                            .addLocalPathType(new PathTypeData(this, "PathType")
                                    .def4FromSingle("cardinality", "many")
                                    .addLocalAttribute(new AttributeData(this, "PathType Attribute").setValue("uuid", "UUID").setSingle("kind", "string"))) },
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_PATHTYPE); // as first, so that local attributes of path types are deleted!
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    @Override
    protected Relationship_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                               final String _name)
    {
        return new Relationship_mxJPO(_name);
    }
}
