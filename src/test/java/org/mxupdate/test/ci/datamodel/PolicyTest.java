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

import java.util.HashSet;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.FormatData;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for policy exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PolicyTest
    extends AbstractPolicyTest
{
    /** Name of test policy. */
    private static final String POLICY_NAME = AbstractTest.PREFIX + "Test";

    /** TCL update code for the test policy. */
    private static final String POLICY_UPDATE_CODE =
              "################################################################################\n"
            + "# POLICY:\n"
            + "# ~~~~~~~\n"
            + "# " + PolicyTest.POLICY_NAME + "\n"
            + "#\n"
            + "# SYMBOLIC NAME:\n"
            + "# ~~~~~~~~~~~~~~\n"
            + "# policy_" + PolicyTest.POLICY_NAME + "\n"
            + "#\n"
            + "# DESCRIPTION:\n"
            + "# ~~~~~~~~~~~~\n"
            + "#\n"
            + "#\n"
            + "# AUTHOR:\n"
            + "# ~~~~~~~\n"
            + "#\n"
            + "#\n"
            + "################################################################################\n"
            + "\n"
            + "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  format {generic}\n"
            + "  defaultformat \"generic\"\n"
            + "  sequence \"1,2,3,...\"\n"
            + "  store \"\"\n"
            + "  hidden \"false\"\n"
            + "  state \"Pending\"  {\n"
            + "    registeredName \"state_Pending\"\n"
            + "    revision \"true\"\n"
            + "    version \"true\"\n"
            + "    promote \"true\"\n"
            + "    checkouthistory \"true\"\n"
            + "    owner {read modify delete}\n"
            + "    public {read show}\n"
            + "    action \"\" input \"\"\n"
            + "    check \"\" input \"\"\n"
            + "  }\n"
            + "  state \"Submitted\"  {\n"
            + "    registeredName \"state_Submitted\"\n"
            + "    revision \"true\"\n"
            + "    version \"true\"\n"
            + "    promote \"true\"\n"
            + "    checkouthistory \"true\"\n"
            + "    owner {read modify checkout checkin}\n"
            + "    public {read show}\n"
            + "    action \"\" input \"\"\n"
            + "    check \"\" input \"\"\n"
            + "    signature \"Reject\" {\n"
            + "      branch \"Rejected\"\n"
            + "      approve {Employee}\n"
            + "      ignore {Employee}\n"
            + "      reject {Employee}\n"
            + "      filter \"\"\n"
            + "    }\n"
            + "    signature \"Review\" {\n"
            + "      branch \"Review\"\n"
            + "      approve {Employee}\n"
            + "      ignore {Employee}\n"
            + "      reject {Employee}\n"
            + "      filter \"\"\n"
            + "    }\n"
            + "  }\n"
            + "  state \"Review\"  {\n"
            + "    registeredName \"state_Review\"\n"
            + "    revision \"true\"\n"
            + "    version \"true\"\n"
            + "    promote \"true\"\n"
            + "    checkouthistory \"true\"\n"
            + "    owner {read modify checkout}\n"
            + "    public {read show}\n"
            + "    action \"\" input \"\"\n"
            + "    check \"\" input \"\"\n"
            + "  }\n"
            + "  state \"Approved\"  {\n"
            + "    registeredName \"state_Approved\"\n"
            + "    revision \"true\"\n"
            + "    version \"true\"\n"
            + "    promote \"true\"\n"
            + "    checkouthistory \"true\"\n"
            + "    owner {read modify checkout checkin}\n"
            + "    public {read show}\n"
            + "    action \"\" input \"\"\n"
            + "    check \"\" input \"\"\n"
            + "    signature \"creator\" {\n"
            + "      branch \"\"\n"
            + "      approve {creator}\n"
            + "      ignore {}\n"
            + "      reject {}\n"
            + "      filter \"\"\n"
            + "    }\n"
            + "  }\n"
            + "  state \"Rejected\"  {\n"
            + "    revision \"true\"\n"
            + "    version \"true\"\n"
            + "    promote \"true\"\n"
            + "    checkouthistory \"true\"\n"
            + "    owner {read modify show}\n"
            + "    public {read show}\n"
            + "    action \"\" input \"\"\n"
            + "    check \"\" input \"\"\n"
            + "  }\n"
            + "}";

    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"30", "86", "119", "120", "121", "179", "182"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return this.prepareData("policy",
                new Object[]{
                        "policy with other symbolic name",
                        new PolicyData(this, "hello \" test")
                                .setSymbolicName("policy_Test")},

                new Object[]{
                        "policy with complex description",
                        new PolicyData(this, "hello \" test")
                                .setValue("description", "\"\\\\ hello")},

                new Object[]{
                        "issue #119: policy with default format",
                        new PolicyData(this, "hello \" test")
                                .setValue("defaultformat", "generic")},

                new Object[]{
                        "policy with store",
                        new PolicyData(this, "hello \" test")
                                .setValue("store", "STORE")},

                new Object[]{
                        "policy with default hidden flag",
                        new PolicyData(this, "hello \" test")
                                .setFlag("hidden", null)},
                new Object[]{
                        "policy with hidden flag false",
                        new PolicyData(this, "hello \" test")
                                .setFlag("hidden", false)},
                new Object[]{
                        "policy with hidden flag true",
                        new PolicyData(this, "hello \" test")
                                .setFlag("hidden", true)},

                // issue 30
                new Object[]{
                        "issue #30, #120: policy with all types",
                        new PolicyData(this, "hello \" test")
                                .setAllTypes(true)},

                // issue 86
                new Object[]{
                        "issue #86: policy with one type",
                        new PolicyData(this, "hello \" test")
                                .appendTypes(new TypeData(this, "Type \"Test\""))},
                new Object[]{
                        "issue #86: policy with two type",
                        new PolicyData(this, "hello \" test")
                                .appendTypes(new TypeData(this, "Type \"Test\" 1"))
                                .appendTypes(new TypeData(this, "Type \"Test\" 2"))},

                new Object[]{
                        "issue #86, #121: policy with all formats",
                        new PolicyData(this, "hello \" test")
                                .setAllFormats(true)},
                new Object[]{
                        "issue #86: policy with one format",
                        new PolicyData(this, "hello \" test")
                                .appendFormats(new FormatData(this, "Type \"Test\""))},
                new Object[]{
                        "issue #86: policy with two formats",
                        new PolicyData(this, "hello \" test")
                                .appendFormats(new FormatData(this, "Type \"Test\" 1"))
                                .appendFormats(new FormatData(this, "Type \"Test\" 2"))},
                // published flag
                new Object[]{
                        "issue #179: policy state w/o defined published (and will be false)",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")),
                        new PolicyData(this, "test")
                                .addState(new State()
                                        .setName("create")
                                        .setValue("published", "false"))},
                new Object[]{
                        "issue #179: policy state with published true",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .setValue("published", "true"))},
                new Object[]{
                        "issue #179: policy state with published false",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .addState(new State()
                                        .setName("create")
                                        .setValue("published", "false"))},
                // locking enforced flag
                new Object[]{
                        "issue #182: policy state with enforce true",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setFlag("enforce", true)},
                new Object[]{
                        "issue #182: policy state with enforce false (means that not defined)",
                        new PolicyData(this, "test")
                                .notSupported(Version.V6R2011x)
                                .setFlag("enforce", false),
                        new PolicyData(this, "test")}
        );
    }

    /**
     * Creates a new policy with one state and with one symbolic name. The
     * symbolic name of the state must not defined as property, only with
     * &quot;registeredName&quot; in the update policy definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("33")
    @Test(description = "issue #33: export of policy with symbolic name property for state")
    public void testPositiveExportNoPropertyDefinitionForStateSymbolicName()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create property state_create value create");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(code.indexOf("mql add property \"state_create\"") < 0,
                          "check that no further property definition for the symbolic name of state exists");
    }

    /**
     * Checks that two symbolic names for a state are correct exported as
     * registered name in the update policy definiton.
     *
     * @throws Exception if test failed
     */
    @IssueLink("34")
    @Test(description = "issue #34: export of all symbolic names for states")
    public void testPositiveExportAllSymbolicNamesForStatesDefined()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create property state_create value create property state_exists value create");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(code.indexOf("registeredName \"state_create\"") >= 0,
                          "check that symbolic name 'state_create' exists");
        Assert.assertTrue(code.indexOf("registeredName \"state_exists\"") >= 0,
                          "check that symbolic name 'state_exists' exists");
    }

    /**
     * Creates a new policy with one state without state symbolic name. In the
     * export a symbolic name of the state must be written.
     *
     * @throws Exception if test failed
     */
    @IssueLink("11")
    @Test(description = "issue #11: export of state symbolic name export if not defined")
    public void testPositiveExportStateSymbolicNameExportedIfNotDefined()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(code.indexOf("registeredName \"state_create\"") >= 0,
                          "check that symbolic name 'state_create' exists");
    }

    /**
     * Test update of policy with all formats in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "issue #86: update policy with all formats in braces")
    public void testPositiveUpdateFormatAllWithBraces()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  format {all}\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n  format all\n"),
                "check that all formats are defined");
    }

    /**
     * Test update of policy with all formats in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "issue #86: update policy with all formats in braces")
    public void testPositiveUpdateFormatAllWithoutBraces()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  format all\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n  format all\n"),
                "check that all formats are defined");
    }

    /**
     * Tests that multiple symbolic names for a single state could be defined.
     *
     * @throws Exception if test failed
     */
    @IssueLink("11")
    @Test(description = "issue #11: update policy with multiple symbolic names for one state")
    public void testPositiveUpdateMultipleStateSymbolicNames()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        policy.updateWithCode(
                    "updatePolicy \"${NAME}\" {"
                    + "  state \"Pending\"  {"
                    + "    registeredName \"state_Exists\""
                    + "    registeredName \"state_Pending\""
                    + "  }"
                    + "}");

        Assert.assertEquals(this.mql("print policy " + policy.getName()
                                + " select property[state_Exists].value dump"),
                            "Pending",
                            "test that symbolic name 'state_Exists' is correct registered");
        Assert.assertEquals(this.mql("print policy " + policy.getName()
                                + " select property[state_Pending].value dump"),
                            "Pending",
                            "test that symbolic name 'state_Pending' is correct registered");
    }


    /**
     * Test for for an update for existing policy with empty property and state
     * names without symbolic names. The empty property must be removed after
     * the update and for the state without symbolic name no registration must
     * be done.
     *
     * @throws Exception if MQL execution failed
     */
    @IssueLink("11")
    @Test(description = "issue #11: update policy with no properties")
    public void testPositiveUpdateNullProperties()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName() + " state Pending property \"\" value Test");

        policy.updateWithCode(PolicyTest.POLICY_UPDATE_CODE);

        // check that only 11 properties are defined....
        // 4 state properties + installer + installed date + original name +
        // file date + author + version + application
        final Set<String> propNames = new HashSet<String>();
        final String propNamesStr = this.mql("print policy " + policy.getName() + " select property.name dump '\n'");
        for (final String propName : propNamesStr.split("\n"))  {
            propNames.add(propName);
        }
        Assert.assertTrue(!propNames.contains(""), "Update did not remove empty property!");
        Assert.assertTrue(this.mql("print policy " + policy.getName()).indexOf("property  value Test") < 0,
                          "Update did not remove empty property!");
        Assert.assertTrue(propNames.size() == 11, "check that all properties are defined");
    }

    /**
     * Checks that states where no symbolic name is defined in the TCL update
     * does not define a symbolic name (with empty name).
     *
     * @throws Exception if MQL execution failed
     */
    @IssueLink("11")
    @Test(description = "issue #11: check update of policy without state symbolic names are updated without property definition for states")
    public void testPositiveUpdateProperties()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        policy.updateWithCode(PolicyTest.POLICY_UPDATE_CODE);

        Assert.assertTrue(!"".equals(this.mql("list policy " + policy.getName())),
                          "policy was not created!");

        // check that only 11 properties are defined....
        // 4 state properties + installer + installed date + original name +
        // file date + author + version + application
        final Set<String> propNames = new HashSet<String>();
        final String propNamesStr = this.mql("print policy " + policy.getName() +" select property.name dump '\n'");
        for (final String propName : propNamesStr.split("\n"))  {
            propNames.add(propName);
        }
        Assert.assertTrue(propNames.size() == 11, "check that all properties are defined");
        Assert.assertTrue(this.mql("print policy " + policy.getName()).indexOf("property  value ") < 0,
                          "Update did set an empty property!");
    }

    /**
     * Test update of policy with all types in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "issue #86: update policy with all types in braces")
    public void testPositiveUpdateTypeAllWithBraces()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n  type all\n"),
                "check that all types are defined");
    }

    /**
     * Test update of policy with all types in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "issue #86: update policy with all types in braces")
    public void testPositiveUpdateTypeAllWithoutBraces()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type all\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n  type all\n"),
                "check that all types are defined");
    }

    /**
     * Test that existing store will be removed.
     *
     * @throws Exception if test failed
     */
    @IssueLink("100")
    @Test(description = "issue #100: test that existing store will be removed")
    public void testPositiveRemoveStore()
        throws Exception
    {
        new PolicyData(this, "Test")
            .setValue("store", "STORE")
            .create()
            .setValue("store", "")
            .update()
            .checkExport();
    }

    /**
     * Test update state definition for user with no filter (if a filter is
     * defined).
     *
     * @throws Exception if test faild
     */
    @IssueLink("151")
    @Test(description = "issue #151: test update state definition for user with no filter (if a filter is defined)")
    public void testPositiveUpdateRemoveFilter()
        throws Exception
    {
        final PersonAdminData creator = new PersonAdminData(this, "creator").create();
        final State state = new State().setName("Test");
        if (this.getVersion() == Version.V6R2011x)  {
            // for 2011x another order of the access definition...
            state.addAccess(
                    new Access()
                            .setKind("owner")
                            .addAccess("all"),
                    new Access()
                            .setPrefix("revoke")
                            .setKind("owner")
                            .addAccess("read"),
                    new Access()
                            .setKind("public")
                            .addAccess("all"),
                    new Access()
                            .setPrefix("revoke")
                            .setKind("public")
                            .addAccess("read"),
                    new Access()
                            .setKind("user")
                            .setUser(creator)
                            .addAccess("read", "show"));
        } else  {
            state.addAccess(
                    new Access()
                            .setPrefix("revoke")
                            .setKind("public")
                            .addAccess("read"),
                    new Access()
                            .setPrefix("revoke")
                            .setKind("owner")
                            .addAccess("read"),
                    new Access()
                            .setKind("public")
                            .addAccess("all"),
                    new Access()
                            .setKind("owner")
                            .addAccess("all"),
                    new Access()
                            .setKind("user")
                            .setUser(creator)
                            .addAccess("read", "show"));
        }
        final PolicyData policy = new PolicyData(this, "Test")
                .addState(state)
                .update()
                .checkExport();
        this.mql("mod policy '" + policy.getName() + "' state Test user " + creator.getName() + " read,show filter Test");
        this.mql("mod policy '" + policy.getName() + "' state Test owner all filter Test");
        this.mql("mod policy '" + policy.getName() + "' state Test revoke owner read filter Test");
        this.mql("mod policy '" + policy.getName() + "' state Test public all filter Test");
        this.mql("mod policy '" + policy.getName() + "' state Test revoke public read filter Test");
        policy.update()
              .checkExport();
    }

    /**
     * Check the the update of a branch for a signature works.
     *
     * @throws Exception if test failed
     */
    @IssueLink("155")
    @Test(description = "check the the update of a branch for a signature works")
    public void testPositiveUpdateSignatureBranch()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, "Test")
                .addState(new PolicyData.State()
                        .setName("Test 1")
                        .addSignature(new PolicyData.Signature()
                                .setName("No")
                                .setBranch("Test 2")))
                .addState(new PolicyData.State()
                        .setName("Test 2"))
                .addState(new PolicyData.State()
                        .setName("Test 3"))
                .update();
        // no change branch to signature 'Test 3'
        this.mql("mod policy '" + policy.getName() + "' state 'Test 1' signature 'No' add branch 'Test 1'");
        // update via policy
        policy.update();
        // and check expport
        policy.checkExport();
    }
}
