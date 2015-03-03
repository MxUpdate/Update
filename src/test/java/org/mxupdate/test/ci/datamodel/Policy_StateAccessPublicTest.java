/*
 * Copyright 2008-2014 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.util.IssueLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class to check state policy for the public keys.
 *
 * @author The MxUpdate Team
 */
public class Policy_StateAccessPublicTest
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
                .update()
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
                .update()
                .checkExport();
    }
}
