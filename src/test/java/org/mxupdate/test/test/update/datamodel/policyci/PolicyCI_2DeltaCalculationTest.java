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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
            {"4) with all types",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").defDataAll("type")},
            {"5) with all formats",
                    new PolicyData(this, "Test"),
                    new PolicyData(this, "Test").defDataAll("format")},
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
       };
    }

    @Override()
    @BeforeMethod()
//    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_POLICY);
    }

    @Override()
    protected Policy_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Policy_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_POLICY.updateType), _name);
    }
}
