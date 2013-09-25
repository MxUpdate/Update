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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.Version;

/**
 * Abstract definition for policy access tests (for allstate and a single
 * state).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public final class AccessTestUtil
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    public static Object[][] getTestData(final IAccessTest _test)
    {
        final AbstractTest test = (AbstractTest) _test;

        final List<Object[]> ret = new ArrayList<Object[]>();

        for (final String kind : new String[]{"owner", "public", "user"})  {
            for (final String prefix : new String[]{null, "login", "revoke"})  {
                if ((test.getVersion() != Version.V6R2011x)
                        || ("owner".equals(kind)  && !"login".equals(prefix))
                        || ("public".equals(kind) && !"login".equals(prefix))
                        || ("user".equals(kind)   && (prefix == null)))  {

                    final String txt = ((prefix != null) ? prefix + " ": "") + kind;
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " read/show",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show"))});
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " state access for " + txt + " all",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("all"))});
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " read all and second different user",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("all"),
                                                new Access()
                                                        .setKind("user")
                                                        .setUser(new PersonAdminData(test, "guest"))
                                                        .addAccess("read", "show"))});
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " read/show with filter expression",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setFilter("current==\"hello\""))});
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " all for key",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("all")
                                                        .setKey("abcd 123"))
                                        .notSupported(Version.V6R2011x)});

                    final PersonAdminData tmpUser = "user".equals(kind) ? new PersonAdminData(test, "creator") : null;
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for three " + txt + " definitions (one w/o key, two with keys)",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser(tmpUser)
                                                        .addAccess("modify", "show"),
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser(tmpUser)
                                                        .addAccess("read", "show")
                                                        .setKey("abcd 1"),
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser(tmpUser)
                                                        .addAccess("toconnect", "todisconnect")
                                                        .setKey("abcd 2"))
                                        .notSupported(Version.V6R2011x)});
                    // user item 'organization' / 'project'
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for any organization",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setOrganization("any"))
                                    .notSupported(Version.V6R2011x),
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show"))});
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for any project",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setProject("any"))
                                    .notSupported(Version.V6R2011x),
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show"))});
                    for (final String orgOrProj : new String[]{"single", "ancestor", "descendant", "related"})  {
                        ret.add(new Object[]{
                                _test.getDescriptionPrefix() + " access for " + txt + " for " + orgOrProj + " organization",
                                _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setOrganization(orgOrProj))
                                        .notSupported(Version.V6R2011x)});
                        ret.add(new Object[]{
                                _test.getDescriptionPrefix() + " access for " + txt + " for " + orgOrProj + " project",
                                _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setProject(orgOrProj))
                                        .notSupported(Version.V6R2011x)});
                        ret.add(new Object[]{
                                _test.getDescriptionPrefix() + " access for " + txt + " for any organization and " + orgOrProj + " project",
                                _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setOrganization("any")
                                                        .setProject(orgOrProj))
                                        .notSupported(Version.V6R2011x),
                                _test.createTestData4Access(new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setProject(orgOrProj))});
                    }
                    // user item 'owner'
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for any owner",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setOwner("any"))
                                    .notSupported(Version.V6R2011x),
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show"))});
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for context owner",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setOwner("context"))
                                    .notSupported(Version.V6R2011x)});
                    // user item 'reserve'
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for any reserve",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setReserve("any"))
                                    .notSupported(Version.V6R2011x),
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show"))});
                    for (final String reserve : new String[]{"no", "context", "inclusive"})  {
                        ret.add(new Object[]{
                                _test.getDescriptionPrefix() + " access for " + txt + " for " + reserve + " reserve",
                                _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setReserve(reserve))
                                        .notSupported(Version.V6R2011x)});
                    }
                    // user item 'maturity'
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for any maturity",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setMaturity("any"))
                                    .notSupported(Version.V6R2011x),
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show"))});
                    for (final String maturity : new String[]{"no", "public", "protected", "private", "notprivate", "ppp"})  {
                        ret.add(new Object[]{
                                _test.getDescriptionPrefix() + " access for " + txt + " for " + maturity + " maturity",
                                _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setMaturity(maturity))
                                        .notSupported(Version.V6R2011x)});
                    }
                    // user items with combination of all
                    for (final String org : new String[]{"single", "ancestor", "descendant", "related"})  {
                        for (final String project : new String[]{"single", "ancestor", "descendant", "related"})  {
                            for (final String reserve : new String[]{"no", "context", "inclusive"})  {
                                for (final String maturity : new String[]{"no", "public", "protected", "private", "notprivate", "ppp"})  {
                                    ret.add(new Object[]{
                                            _test.getDescriptionPrefix() + " access for " + txt + " for " + org + " organization, " + project + " project, context owner, " + reserve + " reserve and " + maturity + " maturity",
                                            _test.createTestData4Access(
                                                            new Access()
                                                                    .setPrefix(prefix)
                                                                    .setKind(kind)
                                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                                    .addAccess("read", "show")
                                                                    .setOrganization(org)
                                                                    .setProject(project)
                                                                    .setOwner("context")
                                                                    .setReserve(reserve)
                                                                    .setMaturity(maturity))
                                                    .notSupported(Version.V6R2011x)});
                                }
                            }
                        }
                    }
                }
            }
        }

        return ret.toArray(new Object[ret.size()][]);
     }

    public interface IAccessTest
    {
        /**
         * Returns the used test string for the test case description.
         *
         * @return text string
         */
        String getDescriptionPrefix();

        /**
         * Defines new test data for given access list {@code _accesss}.
         *
         * @param _accesss      access list
         * @return new test data definition
         */
        AbstractAdminData<?> createTestData4Access(final Access... _accesss);
    }
}
