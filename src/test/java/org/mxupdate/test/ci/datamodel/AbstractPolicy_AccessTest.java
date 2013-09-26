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
    @IssueLink({"99", "177"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return this.prepareData((String) null,
                // owner
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for owner read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("owner")
                                                .addAccess("read", "show"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " state access for owner all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("owner")
                                                .addAccess("all"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for owner read/show owner with filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))},
                // login owner
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login owner read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("read", "show"))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login owner all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("all"))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login owner read/show with filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))
                                .notSupported(Version.V6R2011x)},
                // revoke owner
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for revoke owner read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("read", "show"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for revoke owner all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("all"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for revoke owner read/show with filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("owner")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))},
                // public
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for public read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("public")
                                                .addAccess("read", "show"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for public all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("public")
                                                .addAccess("all"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for public read/show with filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))},
                // login public
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login public read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("read", "show"))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login public all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("all"))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login public read/show and filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))
                                .notSupported(Version.V6R2011x)},
                // revoke public
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for revoke public read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("read", "show"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for revoke public all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("all"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for revoke public read/show and filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("public")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))},
                // user
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for user read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for user read/show and filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for user all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all"))},
                new Object[]{
                        "issue (#99): " + this.getDescriptionPrefix() + " access for two different user",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all"),
                                        new Access()
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "guest"))
                                                .addAccess("read", "show"))},
                // login user
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login user read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show"))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login user read/show with filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for login user all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("login")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all"))
                                .notSupported(Version.V6R2011x)},
                // revoke user
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for revoke user read/show",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show"))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for revoke user read/show with filter expression",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #177: " + this.getDescriptionPrefix() + " access for revoke user all",
                        this.createNewPolicy4Access(
                                        new Access()
                                                .setPrefix("revoke")
                                                .setKind("user")
                                                .setUser(new PersonAdminData(this, "creator"))
                                                .addAccess("all"))
                                .notSupported(Version.V6R2011x)}
        );
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
