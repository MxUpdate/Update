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
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the {@link Policy_mxJPO policy CI} state export / update.
 *
 * @author The MxUpdate Team
 */
public class PolicyCI_3Update5StateAccessPublicTest
    extends AbstractTest
{
    /**
     * Deletes all test data model object.
     *
     * @throws Exception if clean up failed
     */
    @BeforeMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_POLICY);
        this.cleanup(AbstractTest.CI.USR_ROLE);
   }

    /**
     * Tests that a public access definition is removed if only a user is
     * defined.
     *
     * @throws Exception if test failed
     */
    @Test(description = "issue #196: tests that a public access definition is removed if only a user is defined")
    @IssueLink({"196"})
    public void positiveTest()
        throws Exception
    {
        // first only with public access
        new PolicyData(this, "Test")
                .addState(
                        new State()
                                .setName("Create")
                                .addAccess(new Access()
                                        .setKind("public")
                                        .addAccess("read")))
                .create()
                .update((String) null)
                .checkExport();

        // now with specific role access
        new PolicyData(this, "Test")
                .addState(
                        new State()
                                .setName("Create")
                                .addAccess(new Access()
                                        .setKind("user")
                                        .setUser(new RoleData(this, "New Role").create())
                                        .addAccess("read")))
                .update((String) null)
                .checkExport();
    }
}
