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
import org.mxupdate.test.data.datamodel.PolicyData.AccessFilter;
import org.mxupdate.test.data.datamodel.PolicyData.AllState;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
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
                                .setAllState(new AllState())},
                // owner
                new Object[]{
                        "issue #99: policy with all state access for owner read/show",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                                .addAccessFilter(new AccessFilter()
                                                        .setKind("owner")
                                                        .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with aall state access for owner all",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state access for owner read/show owner with filter expression",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                // login owner
                new Object[]{
                        "issue #177: policy with all state access for login owner read/show",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #177: policy with all state access for login owner all",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #177: policy with all state access for login owner read/show with filter expression",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                // revoke owner
                new Object[]{
                        "issue #99: policy with all state access for revoke owner read/show",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state access for revoke owner all",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state access for revoke owner read/show with filter expression",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                // public
                new Object[]{
                        "issue #99: policy with all state access for public read/show",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("public")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state access for public all",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state access for public read/show with filter expression",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                // login public
                new Object[]{
                        "issue #177: policy with all state access for login public read/show",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #177: policy with all state access for login public all",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #177: policy with all state access for login public read/show and filter expression",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                // revoke public
                new Object[]{
                        "issue #99: policy with all state access for revoke public read/show",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state access for revoke public all",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state access for revoke public read/show and filter expression",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                // user
                new Object[]{
                        "issue #99: policy with all state access for user read/show",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state access for user read/show and filter expression",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #99: policy with all state access for user all",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state access for two different user",
                        new PolicyData(this, "test")
                                .setAllState(new AllState()
                                        .addAccessFilter(
                                                new AccessFilter()
                                                        .setKind("user")
                                                        .setUser(new PersonAdminData(this, "creator"))
                                                        .addAccess("all"),
                                                new AccessFilter()
                                                        .setKind("user")
                                                        .setUser(new PersonAdminData(this, "guest"))
                                                        .addAccess("read", "show")))},
                // login user
                new Object[]{
                        "issue #177: policy with all state access for login user read/show",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #177: policy with all state access for login user read/show with filter expression",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #177: policy with all state access for login user all",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))},
                // revoke user
                new Object[]{
                        "issue #177: policy with all state access for revoke user read/show",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #177: policy with all state access for revoke user read/show with filter expression",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #177: policy with all state access for revoke user all",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))}
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
