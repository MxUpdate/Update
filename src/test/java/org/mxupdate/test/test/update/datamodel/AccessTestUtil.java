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
            for (final String prefix : new String[]{null, "login", "revoke", "revoke login"})  {
                if ((test.getVersion() != Version.V6R2011x)
                        || ("owner".equals(kind)  && !"login".equals(prefix) && !"revoke login".equals(prefix))
                        || ("public".equals(kind) && !"login".equals(prefix) && !"revoke login".equals(prefix))
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
                            _test.getDescriptionPrefix() + " access for " + txt + " read/show with local filter expression",
                            _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setLocalFilter("current==\"hello\""))
                                        .notSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x)});
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
                    // user item 'category'
                    ret.add(new Object[]{
                            _test.getDescriptionPrefix() + " access for " + txt + " for any category",
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setCategory("any"))
                                    .notSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x),
                            _test.createTestData4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                    .addAccess("read", "show"))});
                    for (final String category : new String[]{"oem", "goldpartner", "partner", "supplier", "customer", "contractor"})  {
                        ret.add(new Object[]{
                                _test.getDescriptionPrefix() + " access for " + txt + " for " + category + " category",
                                _test.createTestData4Access(
                                                new Access()
                                                        .setPrefix(prefix)
                                                        .setKind(kind)
                                                        .setUser("user".equals(kind) ? new PersonAdminData(test, "creator") : null)
                                                        .addAccess("read", "show")
                                                        .setCategory(category))
                                        .notSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x)});
                    }
                    /* Deactivated, because of amount of tests and not realy needed, because also done from parser tests...
                    // user items with combination of all
                    for (final String org : new String[]{null, "single", "ancestor", "descendant", "related"})  {
                        for (final String project : new String[]{null, "single", "ancestor", "descendant", "related"})  {
                            for (final String reserve : new String[]{null, "no", "context", "inclusive"})  {
                                for (final String maturity : new String[]{null, "no", "public", "protected", "private", "notprivate", "ppp"})  {
                                    for (final String category : new String[]{null, "oem", "goldpartner", "partner", "supplier", "customer", "contractor"})  {
                                        ret.add(new Object[]{
                                                _test.getDescriptionPrefix() + " access for " + txt + " for " + (org == null ? "any" : org) + " organization, "
                                                                                                              + (project == null ? "any" : project) + " project, context owner, "
                                                                                                              + (reserve == null ? "any" : reserve) + " reserve, "
                                                                                                              + (maturity == null ? "any" : maturity) + " maturity and "
                                                                                                              + (category == null ? "any" : category) + " category",
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
                                                                        .setMaturity(maturity)
                                                                        .setCategory(category))
                                                        .notSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x)});
                                    }
                                }
                            }
                        }
                    }*/
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
