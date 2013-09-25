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
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class to check state policy for the user keys.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test()
public class Policy_StateAccessKeyTest
    extends AbstractPolicyTest
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"180"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return this.prepareData((String) null,
                // public
                new Object[]{
                        "issue #180: policy state with access for public all w/o key",
                        new PolicyData(this, "test")
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for public all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("public")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three public definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(
                                                new Access()
                                                        .setKind("public")
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setKind("public")
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setKind("public")
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2")))},
                // login public
                new Object[]{
                        "issue #180: policy state with access for login public all w/o key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for login public all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three login public definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(
                                                new Access()
                                                        .setPrefix("login")
                                                        .setKind("public")
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setPrefix("login")
                                                        .setKind("public")
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setPrefix("login")
                                                        .setKind("public")
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2")))},
                // revoke public
                new Object[]{
                        "issue #180: policy state with access for revoke public all w/o key",
                        new PolicyData(this, "test")
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for revoke public all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three revoke public definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(
                                                new Access()
                                                        .setPrefix("revoke")
                                                        .setKind("public")
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setPrefix("revoke")
                                                        .setKind("public")
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setPrefix("revoke")
                                                        .setKind("public")
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2")))},
                // owner
                new Object[]{
                        "issue #180: policy state with access for owner all w/o key",
                        new PolicyData(this, "test")
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for owner all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("owner")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three owner definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(
                                                new Access()
                                                        .setKind("owner")
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setKind("owner")
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setKind("owner")
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2")))},
                // login owner
                new Object[]{
                        "issue #180: policy state with access for login owner all w/o key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for login owner all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three login owner definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(
                                                new Access()
                                                        .setPrefix("login")
                                                        .setKind("owner")
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setPrefix("login")
                                                        .setKind("owner")
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setPrefix("login")
                                                        .setKind("owner")
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2")))},
                // revoke owner
                new Object[]{
                        "issue #180: policy state with access for revoke owner all w/o key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for revoke owner all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three revoke owner definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(
                                                new Access()
                                                        .setPrefix("revoke")
                                                        .setKind("owner")
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setPrefix("revoke")
                                                        .setKind("owner")
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setPrefix("revoke")
                                                        .setKind("owner")
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2")))},
                // user
                new Object[]{
                        "issue #180: policy state with access for user all w/o key",
                        new PolicyData(this, "test")
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for user all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three user definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State() {
                                    {
                                        final PersonAdminData user = new PersonAdminData(Policy_StateAccessKeyTest.this, "creator");
                                        this.setName("create")
                                            .addAccess(
                                                    new Access()
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("modify", "show"),
                                                    new Access()
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("read", "show")
                                                            .setKey("abcd 1"),
                                                    new Access()
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("toconnect", "todisconnect")
                                                            .setKey("abcd 2"));
                                    }
                                })},
                // login user
                new Object[]{
                        "issue #180: policy state with access for login user all w/o key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for login user all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three login user definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State() {
                                    {
                                        final PersonAdminData user = new PersonAdminData(Policy_StateAccessKeyTest.this, "creator");
                                        this.setName("create")
                                            .addAccess(
                                                    new Access()
                                                            .setPrefix("login")
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("modify", "show"),
                                                    new Access()
                                                            .setPrefix("login")
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("read", "show")
                                                            .setKey("abcd 1"),
                                                    new Access()
                                                            .setPrefix("login")
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("toconnect", "todisconnect")
                                                            .setKey("abcd 2"));
                                    }
                                })},
                // revoke user
                new Object[]{
                        "issue #180: policy state with access for revoke user all w/o key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for revoke user all for key",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three revoke user definitions",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()  {
                                    {
                                        final PersonAdminData user = new PersonAdminData(Policy_StateAccessKeyTest.this, "creator");
                                        this.setName("create")
                                            .addAccess(
                                                    new Access()
                                                            .setPrefix("revoke")
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("modify", "show"),
                                                    new Access()
                                                            .setPrefix("revoke")
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("read", "show")
                                                            .setKey("abcd 1"),
                                                    new Access()
                                                            .setPrefix("revoke")
                                                            .setKind("user")
                                                            .setUser(user)
                                                            .addAccess("toconnect", "todisconnect")
                                                            .setKey("abcd 2"));
                                    }
                                })}
        );
    }
}
