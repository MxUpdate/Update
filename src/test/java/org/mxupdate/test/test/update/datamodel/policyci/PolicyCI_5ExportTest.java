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
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.Signature;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the export of {@link Policy_mxJPO policy CI}.
 *
 * @author The MxUpdate Team
 */
public class PolicyCI_5ExportTest
    extends AbstractTest
{
    /**
     * Positive test correct order of signature.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test correct order of signature")
    public void positiveTestSortStateSignature()
        throws Exception
    {
        final String code = new PolicyData(this, "Test")
                .addState(new State().setName("State 1")
                        .addSignature(new Signature().setName("Signature 2").setBranch("State 1"))
                        .addSignature(new Signature().setName("Signature 1").setBranch("State 2")))
                .addState(new State().setName("State 2"))
                .create()
                .update("")
                .export()
                .getCode();
        Assert.assertTrue(
                code.indexOf("Signature 1") < code.indexOf("Signature 2"),
                "check correct order");
    }

    /**
     * Removes the created test data.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_POLICY);
    }
}
