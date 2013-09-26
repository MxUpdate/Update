/*
 * Copyright 2008-2011 The MxUpdate Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.AllState;
import org.mxupdate.test.util.IssueLink;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for policy exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Policy_AllState_Test
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
            .update()
            .update()
            .checkExport()
            .setAllState((AllState) null)
            .update()
            .update()
            .checkExport()
            .setAllState(new AllState())
            .update()
            .update()
            .checkExport();
    }
}
