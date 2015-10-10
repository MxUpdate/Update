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

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Policy_mxJPO policy CI} state export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PolicyCI_3Update5StateAccessKeyTest
    extends Abstract_3UpdateTest
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("public")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three public definitions",
                        new PolicyData(this, "test")
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for login public all for key",
                        new PolicyData(this, "test")
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setKind("owner")
                                                .addAccess("all")
                                                .setKey("abcd 123")))},
                new Object[]{
                        "issue #180: policy state with access for three owner definitions",
                        new PolicyData(this, "test")
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for login owner all for key",
                        new PolicyData(this, "test")
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .addAccess(new Access()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("all")))},
                new Object[]{
                        "issue #180: policy state with access for revoke owner all for key",
                        new PolicyData(this, "test")
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State() {
                                    {
                                        final PersonAdminData user = new PersonAdminData(PolicyCI_3Update5StateAccessKeyTest.this, "creator");
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State() {
                                    {
                                        final PersonAdminData user = new PersonAdminData(PolicyCI_3Update5StateAccessKeyTest.this, "creator");
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
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
                                .defNotSupported(Version.V6R2011x)
                                .addState(new State()  {
                                    {
                                        final PersonAdminData user = new PersonAdminData(PolicyCI_3Update5StateAccessKeyTest.this, "creator");
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
