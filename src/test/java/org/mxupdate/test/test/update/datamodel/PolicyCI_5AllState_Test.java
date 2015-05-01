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

package org.mxupdate.test.test.update.datamodel;

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.AllState;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Policy_mxJPO policy CI} allstate export / update.
 *
 * @author The MxUpdate Team
 */
public class PolicyCI_5AllState_Test
    extends AbstractPolicyTest
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"99", "177"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return this.prepareData((String) null,
                new Object[]{
                        "issue #99: policy with no all state access",
                        new PolicyData(this, "test")},
                new Object[]{
                        "issue #99: policy with all state access (empty)",
                        new PolicyData(this, "test")
                                .setAllState(new AllState())}
        );
    }

    /**
     * Test update of policy with all state and without allstate.
     *
     *
     * @throws Exception if test failed
     */
    @IssueLink("99")
    @Test(description = "issue #99: test update of policy with all state and without allstate")
    public void testPositiveUpdateAllState()
        throws Exception
    {
        new PolicyData(this, "Test")
            .setAllState(new AllState())
            .update((String) null)
            .update((String) null)
            .checkExport()
            .setAllState((AllState) null)
            .update((String) null)
            .update((String) null)
            .checkExport()
            .setAllState(new AllState())
            .update((String) null)
            .update((String) null)
            .checkExport();
    }
}
