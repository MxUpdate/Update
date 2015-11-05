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

package org.mxupdate.test.test.update.system.uniquekeyci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.data.system.UniqueKeyData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.system.UniqueKeyCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link UniqueKeyCI_mxJPO unique key CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class UniqueKeyCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<UniqueKeyCI_mxJPO,UniqueKeyData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0a) simple with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())},
            {"0b) simple with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())},
            // interface
            {"1a) type with interface",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test"))
                            .defData("with interface", new InterfaceData(this, "Interface Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setValue("with interface", new InterfaceData(this, "Interface Test").getName())},
            {"1b) relationship with interface",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test"))
                            .defData("with interface", new InterfaceData(this, "Interface Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setValue("with interface", new InterfaceData(this, "Interface Test").getName())},
            // global flag
            {"2a) type and global",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test"))
                            .setFlag("global", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setFlag("global", true)},
            {"2b) relationship and global",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test"))
                            .setFlag("global", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setFlag("global", true)},
            // package
            {"3a) new package with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defData("package", new PackageData(this, "TestPackage"))},
            {"3b) update package with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test"))
                            .defData("package", new PackageData(this, "TestPackage1")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defData("package", new PackageData(this, "TestPackage2"))},
            {"3c) remove package with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test"))
                            .defData("package", new PackageData(this, "TestPackage")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defKeyNotDefined("package")},
            {"3d) new package with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defData("package", new PackageData(this, "TestPackage"))},
            {"3e) update package with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test"))
                            .defData("package", new PackageData(this, "TestPackage1")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defData("package", new PackageData(this, "TestPackage2"))},
            {"3f) remove package with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test"))
                            .defData("package", new PackageData(this, "TestPackage")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defKeyNotDefined("package")},
            // uuid
            {"4a) uuid with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"4b) uuid with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic names
            {"5a) symbolic name",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setValue("symbolicname", "expression_123")},
            {"5b) two symbolic name",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setValue("symbolicname", "expression_123")
                            .setValue("symbolicname", "expression_345")},
            // description
            {"6a) description with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setValue("description", "abc def")},
            {"6b) description with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setValue("description", "abc def")},
            // hidden
            {"7a) hidden with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setFlag("hidden", true)},
            {"7b) hidden with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setFlag("hidden", true)},
            {"7c) hidden with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test"))
                            .setFlag("hidden", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setFlag("hidden", false)},
            {"7d) hidden with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test"))
                            .setFlag("hidden", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .setFlag("hidden", false)},
            // enable
            {"10a) enable with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string"))),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true)},
            {"10b) enable with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string"))),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true)},
            // not enable
            {"11a) enable with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", false)},
            {"11b) enable with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", false)},
            // property
            {"20a) property with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))},
            {"20b) property with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))},

            // field for disabled unique key
            {"30) add field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .setValue("field", "type")},
            {"31a) add attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string"))),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")},
            {"31b) add attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string"))),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")},
            {"32a) add second attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120"),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")},
            {"32b) add second attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120"),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")},
            {"33a) remove second attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120"),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")},
            {"33b) remove second attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120"),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")},
            {"34a) update attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120"),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 200")},
            {"34b) update attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120"),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 200")},

            // field for disabled unique key
            {"42a) add second attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")
                            .setFlag("enable", true)},
            {"42b) add second attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")
                            .setFlag("enable", true)},
            {"43a) remove second attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .setFlag("enable", true)},
            {"43b) remove second attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test")
                                    .defData("attribute", new AttributeData(this, "Test1").setSingle("kind", "string"))
                                    .defData("attribute", new AttributeData(this, "Test2").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .setFlag("enable", true)},
            {"44a) update attribute field with type",
                    new UniqueKeyData(this, "Test")
                            .defData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for type", new TypeData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 200")
                            .setFlag("enable", true)},
            {"44b) update attribute field with relationship",
                    new UniqueKeyData(this, "Test")
                            .defData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")))
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true),
                    new UniqueKeyData(this, "Test")
                            .setValue("for relationship", new RelationshipData(this, "Test").getName())
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 200")
                            .setFlag("enable", true)},
        };
    }

    @Override
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.SYS_UNIQUEKEY);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
        this.cleanup(AbstractTest.CI.DM_INTERFACE);
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
        this.cleanup(AbstractTest.CI.DM_TYPE);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected UniqueKeyCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new UniqueKeyCI_mxJPO(_name);
    }
}
