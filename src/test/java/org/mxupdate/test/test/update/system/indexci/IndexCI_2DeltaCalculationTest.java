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

package org.mxupdate.test.test.update.system.indexci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.system.IndexData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.system.IndexCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link IndexCI_mxJPO index CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class IndexCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<IndexCI_mxJPO,IndexData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0a) simple",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")},
            // unique flag
            {"1) unique",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setFlag("unique", true)},
            // uuid
            {"2a) uuid with type",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"2b) uuid with relationship",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic names
            {"3a) symbolic name",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("symbolicname", "expression_123")},
            {"3b) two symbolic name",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("symbolicname", "expression_123")
                            .setValue("symbolicname", "expression_345")},
            // description
            {"4) description with type",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("description", "abc def")},
            // hidden
            {"5) hidden",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setFlag("hidden", true)},
            // not hidden
            {"6) not hidden",
                    new IndexData(this, "Test")
                            .setFlag("hidden", true),
                    new IndexData(this, "Test")
                            .setFlag("hidden", false)},
            // enable
            {"10) enable",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("field", "name")
                            .setFlag("enable", true)},

            // not enable
            {"11) not enable",
                    new IndexData(this, "Test")
                            .setValue("field", "name")
                            .setFlag("enable", true),
                    new IndexData(this, "Test")
                            .setValue("field", "name")
                            .setFlag("enable", false)},
            // property
            {"20a) property with type",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))},
            {"20b) property with relationship",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))},

            // field for disabled unique key
            {"30) add field with type",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .setValue("field", "type")},
            {"31a) add attribute field",
                    new IndexData(this, "Test"),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")},
            {"32a) add second attribute field",
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120"),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")},
            {"33a) remove second attribute",
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120"),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")},
            {"34a) update attribute",
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120"),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 200")},

            // field for disabled unique key
            {"42) add second attribute field with type",
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .setFlag("enable", true),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")
                            .setFlag("enable", true)},
            {"43) remove second attribute field ",
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .defText("field", "\"attribute[MXUPDATE_Test2]\" size 120")
                            .setFlag("enable", true),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test1]\" size 120")
                            .setFlag("enable", true)},
            {"44) update attribute field with type",
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                            .setFlag("enable", true),
                    new IndexData(this, "Test")
                            .defText("field", "\"attribute[MXUPDATE_Test]\" size 200")
                            .setFlag("enable", true)},
        };
    }

    @BeforeMethod
    public void createAttributes()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
        this.mql().cmd("escape add attribute ").arg(AbstractTest.PREFIX + "Test") .cmd(" type ").arg("string").exec(this.getContext());
        this.mql().cmd("escape add attribute ").arg(AbstractTest.PREFIX + "Test1").cmd(" type ").arg("string").exec(this.getContext());
        this.mql().cmd("escape add attribute ").arg(AbstractTest.PREFIX + "Test2").cmd(" type ").arg("string").exec(this.getContext());
    }

    @AfterClass
    public void cleanupAttributes()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
    }

    @Override
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.SYS_INDEX);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
    }

    @Override
    protected IndexCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name)
    {
        return new IndexCI_mxJPO(_name);
    }
}
