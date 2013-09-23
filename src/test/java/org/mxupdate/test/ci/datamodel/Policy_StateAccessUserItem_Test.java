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

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.AccessFilter;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for policy exports for user items of states.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test()
public class Policy_StateAccessUserItem_Test
    extends AbstractPolicyTest
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"181"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        ret.add(new Object[]{
                "issue #181: policy with state access for any organization",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setOrganization("any"))),
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")))});
        ret.add(new Object[]{
                "issue #181: policy with state access for any project",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setProject("any"))),
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")))});

        for (final String orgOrProj : new String[]{"single", "ancestor", "descendant", "related"})  {
            ret.add(new Object[]{
                "issue #181: policy with state access for " + orgOrProj + " organization",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setOrganization(orgOrProj)))});
            ret.add(new Object[]{
                    "issue #181: policy with state access for " + orgOrProj + " project",
                    new PolicyData(this, "test")
                            .notSupported(Version.V6R2011x)
                            .addState(new State()
                                    .setName("create")
                                    .addAccessFilter(new AccessFilter()
                                            .setKind("public")
                                            .addAccess("read", "show")
                                            .setProject(orgOrProj)))});
            ret.add(new Object[]{
                    "issue #181: policy with state access for any organization and " + orgOrProj + " project",
                    new PolicyData(this, "test")
                            .notSupported(Version.V6R2011x)
                            .addState(new State()
                                    .setName("create")
                                    .addAccessFilter(new AccessFilter()
                                            .setKind("public")
                                            .addAccess("read", "show")
                                            .setOrganization("any")
                                            .setProject(orgOrProj))),
                    new PolicyData(this, "test")
                            .notSupported(Version.V6R2011x)
                            .addState(new State()
                                    .setName("create")
                                    .addAccessFilter(new AccessFilter()
                                            .setKind("public")
                                            .addAccess("read", "show")
                                            .setProject(orgOrProj)))});
        }

        // owner
        ret.add(new Object[]{
                "issue #181: policy with state access for any owner",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setOwner("any"))),
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")))});
        ret.add(new Object[]{
                "issue #181: policy with state access for context owner",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setOwner("context")))});

        // reserve
        ret.add(new Object[]{
                "issue #181: policy with state access for any reserve",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setReserve("any"))),
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")))});
        for (final String reserve : new String[]{"no", "context", "inclusive"})  {
            ret.add(new Object[]{
                "issue #181: policy with state access for " + reserve + " reserve",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setReserve(reserve)))});
        }

        // maturity
        ret.add(new Object[]{
                "issue #181: policy with state access for any maturity",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setMaturity("any"))),
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")))});
        for (final String maturity : new String[]{"no", "public", "protected", "private", "notprivate", "ppp"})  {
            ret.add(new Object[]{
                "issue #181: policy with state access for " + maturity + " maturity",
                new PolicyData(this, "test")
                        .notSupported(Version.V6R2011x)
                        .addState(new State()
                                .setName("create")
                                .addAccessFilter(new AccessFilter()
                                        .setKind("public")
                                        .addAccess("read", "show")
                                        .setMaturity(maturity)))});
        }

        // combination of all
        for (final String org : new String[]{"single", "ancestor", "descendant", "related"})  {
            for (final String project : new String[]{"single", "ancestor", "descendant", "related"})  {
                for (final String reserve : new String[]{"no", "context", "inclusive"})  {
                    for (final String maturity : new String[]{"no", "public", "protected", "private", "notprivate", "ppp"})  {
                        ret.add(new Object[]{
                                "issue #181: policy with state access for " + org + " organization, " + project + " project, context owner, " + reserve + " reserve and " + maturity + " maturity",
                                new PolicyData(this, "test")
                                        .notSupported(Version.V6R2011x)
                                        .addState(new State()
                                                .setName("create")
                                                .addAccessFilter(new AccessFilter()
                                                        .setKind("public")
                                                        .addAccess("read", "show")
                                                        .setOrganization(org)
                                                        .setProject(project)
                                                        .setOwner("context")
                                                        .setReserve(reserve)
                                                        .setMaturity(maturity)))});
                    }
                }
            }
        }

        return this.prepareData((String) null, ret.toArray(new Object[ret.size()][]));
    }

}
