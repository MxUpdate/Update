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
import org.mxupdate.test.data.datamodel.PolicyData.Access;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.testng.annotations.DataProvider;

/**
 * Abstract definition for policy access tests (for allstate and a single
 * state).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractPolicy_AccessTest
    extends AbstractPolicyTest
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"99", "177", "180"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        for (final String kind : new String[]{"owner", "public", "user"})  {
            for (final String prefix : new String[]{null, "login", "revoke"})  {
                final String txt = ((prefix != null) ? prefix + " ": "") + kind;
                ret.add(new Object[]{
                            this.getDescriptionPrefix() + " access for " + txt + " read/show",
                            this.createNewPolicy4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(this, "creator") : null)
                                                    .addAccess("read", "show"))});
                ret.add(new Object[]{
                            this.getDescriptionPrefix() + " state access for " + txt + " all",
                            this.createNewPolicy4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(this, "creator") : null)
                                                    .addAccess("all"))});
                ret.add(new Object[]{
                            this.getDescriptionPrefix() + " access for " + txt + " read all and second different user",
                            this.createNewPolicy4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(this, "creator") : null)
                                                    .addAccess("all"),
                                            new Access()
                                                    .setKind("user")
                                                    .setUser(new PersonAdminData(this, "guest"))
                                                    .addAccess("read", "show"))});
                ret.add(new Object[]{
                            this.getDescriptionPrefix() + " access for " + txt + " read/show with filter expression",
                            this.createNewPolicy4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(this, "creator") : null)
                                                    .addAccess("read", "show")
                                                    .setFilter("current==\"hello\""))});
                ret.add(new Object[]{
                            this.getDescriptionPrefix() + " access for " + txt + " all for key",
                            this.createNewPolicy4Access(
                                            new Access()
                                                    .setPrefix(prefix)
                                                    .setKind(kind)
                                                    .setUser("user".equals(kind) ? new PersonAdminData(this, "creator") : null)
                                                    .addAccess("all")
                                                    .setKey("abcd 123"))
                                    .notSupported(Version.V6R2011x)});

                final PersonAdminData tmpUser = "user".equals(kind) ? new PersonAdminData(this, "creator") : null;
                ret.add(new Object[]{
                            this.getDescriptionPrefix() + " access for three " + txt + " definitions (one w/o key, two with keys)",
                            this.createNewPolicy4Access(
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
            }
        }

        return super.prepareData((String) null, ret.toArray(new Object[ret.size()][]));
     }

    /**
     * Returns the used test string for the test case description.
     *
     * @return text string
     */
    protected abstract String getDescriptionPrefix();

    /**
     * Defines new test data for given access list {@code _accesss}.
     *
     * @param _accesss      access list
     * @return new test data definition
     */
    protected abstract PolicyData createNewPolicy4Access(final Access... _accesss);
}
