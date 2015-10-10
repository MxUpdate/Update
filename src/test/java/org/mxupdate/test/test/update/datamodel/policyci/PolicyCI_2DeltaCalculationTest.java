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

package org.mxupdate.test.test.update.datamodel.policyci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.FormatData;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.AllState;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Policy_mxJPO policy} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PolicyCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Policy_mxJPO,PolicyData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) symbolic name",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").setValue("symbolicname", "policy_123")},
            {"1b) two symbolic name",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").setValue("symbolicname", "policy_123").setValue("symbolicname", "policy_345")},

            {"4a) with all types",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").defDataAll("type")},
            {"4b) with specific type from all types",
                    new PolicyData(this, "Test").defDataAll("type"),
                    new PolicyData(this, "Test").defData("type", new TypeData(this, "Type"))},
            {"4c) with specific type from another type",
                    new PolicyData(this, "Test").defData("type", new TypeData(this, "TypeOld")),
                    new PolicyData(this, "Test").defData("type", new TypeData(this, "TypeNew"))},

            {"5a) with all formats",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").defDataAll("format")},
            {"5b) with specific format from all format",
                    new PolicyData(this, "Test").defDataAll("format"),
                    new PolicyData(this, "Test").defData("format", new FormatData(this, "Format"))},
            {"5c) with specific format from another specific format",
                    new PolicyData(this, "Test").defData("format", new FormatData(this, "FormatOld")),
                    new PolicyData(this, "Test").defData("format", new FormatData(this, "FormatNew"))},

            {"10a) with two registered state names",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").addState(new State().setName("A").setValue("registeredname", "state_A").setValue("registeredname", "state_A1"))},
            {"10b) registered state name added",
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A")),
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_A"))},
            {"10c) registered state name removed",
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_A").setValue("registeredname", "state_A1")),
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_A"))},
            {"10d) registered state name moved from first state to second state",
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_A"))
                            .addState(new State().setName("B").setValue("registeredname", "state_B")),
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_ABC"))
                            .addState(new State().setName("B").setValue("registeredname", "state_A"))},
            {"10e) registered state name moved from second state to first state",
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_A"))
                            .addState(new State().setName("B").setValue("registeredname", "state_B")),
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A").setValue("registeredname", "state_B"))
                            .addState(new State().setName("B").setValue("registeredname", "state_BCD"))},
            // state
            {"20a) new state",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A"))},
            {"20b) append state",
                    new PolicyData(this, "Test").addState(new State().setName("A")),
                    new PolicyData(this, "Test").addState(new State().setName("A")).addState(new State().setName("B"))},
            {"20c) insert state",
                    new PolicyData(this, "Test").addState(new State().setName("A"))                                   .addState(new State().setName("C")),
                    new PolicyData(this, "Test").addState(new State().setName("A")).addState(new State().setName("B")).addState(new State().setName("C"))},

            // allstate
            {"21a) all state access: new public all",
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A")),
                    new PolicyData(this, "Test")
                            .setAllState(new AllState().addAccess(new Access().setKind("public").addAccess("all")))
                            .addState(new State().setName("A"))},
            {"21b) all state access: remove public all",
                    new PolicyData(this, "Test")
                            .setAllState(new AllState().addAccess(new Access().setKind("public").addAccess("all")))
                            .addState(new State().setName("A")),
                    new PolicyData(this, "Test")
                            .addState(new State().setName("A"))},
       };
    }

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_POLICY);
        this.cleanup(AbstractTest.CI.DM_FORMAT);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    @Override
    protected Policy_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Policy_mxJPO(_name);
    }
}
