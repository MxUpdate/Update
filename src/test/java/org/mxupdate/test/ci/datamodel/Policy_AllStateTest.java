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

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.AccessFilter;
import org.mxupdate.test.data.datamodel.PolicyData.AllState;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.IssueLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for policy exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Policy_AllStateTest
    extends AbstractDataExportUpdate<PolicyData>
{
    /**
     * Creates for given <code>_name</code> a new policy instance.
     *
     * @param _name     name of the policy instance
     * @return policy instance
     */
    @Override()
    protected PolicyData createNewData(final String _name)
    {
        return new PolicyData(this, _name);
    }

    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"99"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return this.prepareData((String) null,
                // issue 99
                new Object[]{
                        "issue #99: policy with default all state flag",
                        new PolicyData(this, "hello \" test")
                                .setAllState(null)},
                new Object[]{
                        "issue #99: policy with all state flag false",
                        new PolicyData(this, "hello \" test")},
                new Object[]{
                        "issue #99: policy with all state flag true",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState())},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                                .addAccessFilter(new AccessFilter()
                                                        .setKind("owner")
                                                        .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state flag true and all owner access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner access and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state flag true and all owner revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner revoke and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("public")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state flag true and all public access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public access and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with all state flag true and all public revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public revoke and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #99: policy with user access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")))},
                new Object[]{
                        "issue #99: policy with user access add filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\"")))},
                new Object[]{
                        "issue #99: policy with all user access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(new AccessFilter()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))},
                new Object[]{
                        "issue #99: policy with two different user access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(new AllState()
                                        .addAccessFilter(
                                                new AccessFilter()
                                                        .setKind("user")
                                                        .setUser(new PersonAdminData(this, "creator"))
                                                        .addAccess("all"),
                                                new AccessFilter()
                                                        .setKind("user")
                                                        .setUser(new PersonAdminData(this, "guest"))
                                                        .addAccess("read", "show")))}
                );
    }

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
        this.cleanup(AbstractTest.CI.DM_TYPE);
        this.cleanup(AbstractTest.CI.DM_FORMAT);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_ROLE);
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
