/*
 * Copyright 2008-2010 The MxUpdate Team
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

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.datamodel.FormatData;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.util.IssueLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for policy exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PolicyTest
    extends AbstractDataExportUpdate<PolicyData>
{
    /**
     * Name of test policy.
     */
    private static final String POLICY_NAME = "MxUpdate_Test";

    /**
     * Name of test type.
     */
    private static final String TYPE_NAME = "MxUpdate_TestType";

    /**
     * TCL update code for the test policy.
     */
    private static final String POLICY_UPDATE_CODE =
              "################################################################################\n"
            + "# POLICY:\n"
            + "# ~~~~~~~\n"
            + "# " + AbstractTest.PREFIX + PolicyTest.POLICY_NAME + "\n"
            + "#\n"
            + "# SYMBOLIC NAME:\n"
            + "# ~~~~~~~~~~~~~~\n"
            + "# policy_" + AbstractTest.PREFIX + PolicyTest.POLICY_NAME + "\n"
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
    @IssueLink({"30", "86", "99"})
    @DataProvider(name = "policies")
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
                        "policy with default format",
                        new PolicyData(this, "hello \" test")
                                .setValue("defaultformat", "generic")},

                new Object[]{
                        "policy with sequence",
                        new PolicyData(this, "hello \" test")
                                .setValue("sequence", "1,2,3,A,B,C")},

                new Object[]{
                        "policy with store",
                        new PolicyData(this, "hello \" test")
                                .setValue("store", "STORE")},

                new Object[]{
                        "policy with default hidden flag",
                        new PolicyData(this, "hello \" test")
                                .setHidden(null)},
                new Object[]{
                        "policy with hidden flag false",
                        new PolicyData(this, "hello \" test")
                                .setHidden(false)},
                new Object[]{
                        "policy with hidden flag true",
                        new PolicyData(this, "hello \" test")
                                .setHidden(true)},

                // issue 30
                new Object[]{
                        "issue #30: policy with all types",
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
                        "issue #86: policy with all formats",
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

                // issue 99
                new Object[]{
                        "issue #99: policy with default all state flag",
                        new PolicyData(this, "hello \" test")
                                .setAllState(null)},
                new Object[]{
                        "issue #99: policy with all state flag false",
                        new PolicyData(this, "hello \" test")
                                .setAllState(false)},
                new Object[]{
                        "issue #99: policy with all state flag true",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addOwnerAccess("read", "show")},
                new Object[]{
                        "issue #99: policy with all state flag true and all owner access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addOwnerAccess("all")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner access and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addOwnerAccess("read", "show")
                                .getAllStateAccess().setOwnerAccessFilter("current==\"hello\"")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addOwnerRevoke("read", "show")},
                new Object[]{
                        "issue #99: policy with all state flag true and all owner revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addOwnerRevoke("all")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show owner revoke and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addOwnerRevoke("read", "show")
                                .getAllStateAccess().setOwnerRevokeFilter("current==\"hello\"")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addPublicAccess("read", "show")},
                new Object[]{
                        "issue #99: policy with all state flag true and all public access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addPublicAccess("all")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public access and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addPublicAccess("read", "show")
                                .getAllStateAccess().setPublicAccessFilter("current==\"hello\"")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addPublicRevoke("read", "show")},
                new Object[]{
                        "issue #99: policy with all state flag true and all public revoke",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addPublicRevoke("all")},
                new Object[]{
                        "issue #99: policy with all state flag true and read / show public revoke and filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addPublicRevoke("read", "show")
                                .getAllStateAccess().setPublicRevokeFilter("current==\"hello\"")},
                new Object[]{
                        "issue #99: policy with user access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addUserAccess(
                                        new PolicyData.UserAccessFilter()
                                                .setUser("creator")
                                                .addAccess("read", "show"))},
                new Object[]{
                        "issue #99: policy with user access add filter expression",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addUserAccess(
                                        new PolicyData.UserAccessFilter()
                                                .setUser("creator")
                                                .addAccess("read", "show")
                                                .setFilter("current==\"hello\""))},
                new Object[]{
                        "issue #99: policy with all user access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addUserAccess(
                                        new PolicyData.UserAccessFilter()
                                                .setUser("creator")
                                                .addAccess("all"))},
                new Object[]{
                        "issue #99: policy with two different user access",
                        new PolicyData(this, "hello \" test")
                                .setAllState(true)
                                .getAllStateAccess().addUserAccess(
                                        new PolicyData.UserAccessFilter()
                                                .setUser("creator")
                                                .addAccess("all"))
                                .getAllStateAccess().addUserAccess(
                                        new PolicyData.UserAccessFilter()
                                                .setUser("guest")
                                                .addAccess("read", "show"))}
                );
    }

    /**
     * Removes the MxUpdate test policy {@link #POLICY_NAME}.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void removePolicy()
        throws Exception
    {
        if (!"".equals(this.mql("list policy " + PolicyTest.POLICY_NAME)))  {
            this.mql("delete policy " + PolicyTest.POLICY_NAME);
        }
    }

    /**
     * Removes the MxUpdate test type {@link #TYPE_NAME}.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void removeType()
        throws Exception
    {
        if (!"".equals(this.mql("list type " + PolicyTest.TYPE_NAME)))  {
            this.mql("delete type " + PolicyTest.TYPE_NAME);
        }
    }

    /**
     * Deletes all test data model object.
     *
     * @throws Exception if clean up failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_POLICY);
        this.cleanup(AbstractTest.CI.DM_TYPE);
        this.cleanup(AbstractTest.CI.DM_FORMAT);
   }

    /**
     * Creates a new policy with one state and with one symbolic name. The
     * symbolic name of the state must not defined as property, only with
     * &quot;registeredName&quot; in the update policy definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("33")
    @Test(description = "export of policy with symbolic name property for state")
    public void testExportNoPropertyDefinitionForStateSymbolicName()
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
    @Test(description = "export of all symbolic names for states")
    public void testExportAllSymbolicNamesForStatesDefined()
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
     * Test export with owner access for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "export with owner access for a state with filter expression")
    public void exportStateOwnerAccessWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create owner read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    owner {read show} filter \"type==Part\"\n") >= 0,
                "check the for owner filter is exported");
    }

    /**
     * Test export with owner access for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "export with owner access for a state without filter expression")
    public void exportStateOwnerAccessWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create owner read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    owner {read show}\n") >= 0,
                "check the for owner filter is exported");
    }

    /**
     * Test export with owner revoke for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with owner revoke for a state with filter expression")
    public void exportStateOwnerRevokeWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create revoke owner read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    revoke owner {read show} filter \"type==Part\"\n") >= 0,
                "check the for owner filter is exported");
    }

    /**
     * Test export with none owner revoke.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with none owner revoke")
    public void exportStateOwnerRevokeWithoutDefinition()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create revoke owner none filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("revoke owner") < 0,
                "check the for owner revoke is not exported");
    }

    /**
     * Test export with owner revoke for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with owner revoke for a state without filter expression")
    public void exportStateOwnerRevokeWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create revoke owner read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();

        Assert.assertTrue(
                code.indexOf("\n    revoke owner {read show}\n") >= 0,
                "check the for owner revoke filter is not exported");
    }

    /**
     * Test export with public access for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "export with public access for a state with filter expression")
    public void exportStatePublicAccessWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create public read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    public {read show} filter \"type==Part\"\n") >= 0,
                "check the for public access filter is exported");
    }

    /**
     * Test export with public access for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "export with public access for a state without filter expression")
    public void exportStatePublicAccessWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create public read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    public {read show}\n") >= 0,
                "check the for public access filter is not exported");
    }

    /**
     * Test export with none public revoke.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with none public revoke")
    public void exportStatePublicRevokeWithoutDefinition()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create revoke public none filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();

        Assert.assertTrue(
                code.indexOf("\n    revoke public") < 0,
                "check the for public revoke is not exported");
    }

    /**
     * Test export with public revoke for a state with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with public revoke for a state with filter expression")
    public void exportStatePublicRevokeWithFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create revoke public read,show filter type==Part");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    revoke public {read show} filter \"type==Part\"\n") >= 0,
                "check the for public revoke filter is exported");
    }

    /**
     * Test export with public revoke for a state without filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "export with public revoke for a state without filter expression")
    public void exportStatePublicRevokeWithoutFilterExpression()
        throws Exception
    {
        this.mql("add policy " + PolicyTest.POLICY_NAME
                + " state create revoke public read,show filter ''");

        final Export export = this.export(CI.DM_POLICY, PolicyTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(
                code.indexOf("\n    revoke public {read show}\n") >= 0,
                "check the for public revoke filter is exported");
    }

    /**
     * Creates a new policy with one state without state symbolic name. In the
     * export a symbolic name of the state must be written.
     *
     * @throws Exception if test failed
     */
    @IssueLink("11")
    @Test(description = "export of state symbolic name export if not defined")
    public void exportStateSymbolicNameExportedIfNotDefined()
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
    @Test(description = "update policy with all formats in braces")
    public void testUpdateFormatAllWithBraces()
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
    @Test(description = "update policy with all formats in braces")
    public void testUpdateFormatAllWithoutBraces()
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
    @Test(description = "update policy with multiple symbolic names for one state")
    public void testUpdateMultipleStateSymbolicNames()
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
    @Test(description = "update policy with no properties")
    public void testUpdateNullProperties()
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
    @Test(description = "check update of policy without state symbolic names are updated without property definition for states")
    public void testUpdateProperties()
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
     * Test create owner access with empty owner filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "create owner access with empty owner filter definition")
    public void updateStateOwnerAccessWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test create owner access with empty owner filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "update owner access with empty owner filter definition for existing filter expression")
    public void updateStateOwnerAccessWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);
        this.mql("add policy " + policy.getName()
                + " state create owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type!=Part"),
               "check that owner access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test create owner access with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "create owner access with filter expression")
    public void updateStateOwnerAccessWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type==Part"),
                "check that owner access with filter expression defined");
    }

    /**
     * Test update owner access with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "update owner access with filter expression for existing filter expression")
    public void updateStateOwnerAccessWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);
        this.mql("add policy " + policy.getName()
                + " state create owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type!=Part"),
               "check that owner access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName() + " ").contains("\n    owner read,show\n     filter type==Part"),
                "check that owner access with filter expression defined");
    }

    /**
     * Test create owner access without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "create owner access without filter expression for non existing state")
    public void updateStateOwnerAccessWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test update owner access without filter expression for existing state
     * with existing owner filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "update owner access without filter expression for existing state with existing owner filter expression")
    public void updateStateOwnerAccessWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create owner read,show filter \"type==Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n     filter type==Part"),
               "check that owner access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    owner read,show\n    public"),
                "check that owner access without filter expression defined");
    }

    /**
     * Test create owner revoke with empty owner filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "create owner revoke with empty owner filter definition")
    public void updateStateOwnerRevokeWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test create owner revoke with empty owner filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "update owner revoke with empty owner filter definition for existing filter expression")
    public void updateStateOwnerRevokeWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type!=Part"),
               "check that owner revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test create owner revoke with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "create owner revoke with filter expression")
    public void updateStateOwnerRevokeWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type==Part"),
                "check that owner revoke with filter expression defined");
    }

    /**
     * Test update owner revoke with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "update owner revoke with filter expression for existing filter expression")
    public void updateStateOwnerRevokeWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke owner read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type!=Part"),
               "check that owner revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type==Part"),
                "check that owner revoke with filter expression defined");
    }

    /**
     * Test create owner revoke without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "create owner revoke without filter expression for non existing state")
    public void updateStateOwnerRevokeWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test update owner revoke without filter expression for existing state
     * with existing owner filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "update owner revoke without filter expression for existing state with existing owner filter expression")
    public void updateStateOwnerRevokeWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke owner read,show filter \"type==Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n     filter type==Part"),
               "check that revoke owner revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke owner {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke owner read,show\n    public"),
                "check that owner revoke without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "create public access with empty public filter definition")
    public void updateStatePublicAccessWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "update public access with empty public filter definition for existing filter expression")
    public void updateStatePublicAccessWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create public read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type!=Part"),
               "check that public access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test create public access with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "create public access with filter expression")
    public void updateStatePublicAccessWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type==Part"),
                "check that public access with filter expression defined");
    }

    /**
     * Test update public access with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "update public access with filter expression for existing filter expression")
    public void updateStatePublicAccessWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create public read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type!=Part"),
               "check that public access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type==Part"),
                "check that public access with filter expression defined");
    }

    /**
     * Test create public access without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "create public access without filter expression for non existing state")
    public void updateStatePublicAccessWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test update public access without filter expression for existing state
     * with existing public filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("87")
    @Test(description = "update public access without filter expression for existing state with existing public filter expression")
    public void updateStatePublicAccessWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create public read,show filter \"type==Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    public read,show\n     filter type==Part"),
               "check that public access with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    public read,show\n  nothidden"),
                "check that public access without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "create public access with empty public filter definition")
    public void updateStatePublicRevokeWithEmptyFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke  without filter expression defined");
    }

    /**
     * Test create public access with empty public filter definition for existing
     * definition.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "update public access with empty public filter definition for existing filter expression")
    public void updateStatePublicRevokeWithEmptyFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke public read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type!=Part"),
               "check that public revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke without filter expression defined");
    }

    /**
     * Test create public access with filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "create public access with filter expression")
    public void updateStatePublicRevokeWithFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type==Part"),
                "check that public revoke with filter expression defined");
    }

    /**
     * Test update public access with filter expression for existing filter
     * expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "update public access with filter expression for existing filter expression")
    public void updateStatePublicRevokeWithFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke public read,show filter \"type!=Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type!=Part"),
               "check that public revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show} filter \"type==Part\"\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

        Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type==Part"),
                "check that public revoke with filter expression defined");
    }

    /**
     * Test create public access without filter expression for non existing
     * state.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "create public access without filter expression for non existing state")
    public void updateStatePublicRevokeWithoutFilterExpression()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke without filter expression defined");
    }

    /**
     * Test update public access without filter expression for existing state
     * with existing public filter expression.
     *
     * @throws Exception if test failed
     */
    @IssueLink("88")
    @Test(description = "update public access without filter expression for existing state with existing public filter expression")
    public void updateStatePublicRevokeWithoutFilterExpression4Existing()
        throws Exception
    {
        final PolicyData policy = new PolicyData(this, PolicyTest.POLICY_NAME);

        this.mql("add policy " + policy.getName()
                + " state create revoke public read,show filter \"type==Part\"");

        Assert.assertTrue(
               this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n     filter type==Part"),
               "check that public revoke with filter expression defined");

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "  state \"create\"  {\n"
            + "    revoke public {read show}\n"
            + "  }\n"
            + "}";

        policy.updateWithCode(updateCode);

         Assert.assertTrue(
                this.mql("print pol " + policy.getName()).contains("\n    revoke public read,show\n  nothidden"),
                "check that public revoke without filter expression defined");
    }



    /**
     * Test update of policy with all types in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with all types in braces")
    public void testUpdateTypeAllWithBraces()
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
    @Test(description = "update policy with all types in braces")
    public void testUpdateTypeAllWithoutBraces()
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
     * Tests a new created policy and the related export.
     *
     * @param _description  description of the test case
     * @param _policy       policy to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "policies",
          description = "test export of new created policies")
    public void testExport(final String _description,
                           final PolicyData _policy)
        throws Exception
    {
        _policy.create()
               .checkExport(_policy.export());
    }

    /**
     * Tests an update of non existing command. The result is tested with by
     * exporting the command and checking the result.
     *
     * @param _description  description of the test case
     * @param _policy       policy to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "policies",
          description = "test update of non existing policy")
    public void testUpdate(final String _description,
                           final PolicyData _policy)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _policy.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create assigned types
        for (final TypeData type : _policy.getTypes())  {
            type.create();
        }
        // create assigned formats
        for (final FormatData format : _policy.getFormats())  {
            format.create();
        }

        // first update with original content
        _policy.update();
        final ExportParser exportParser = _policy.export();
        _policy.checkExport(exportParser);

        // second update with delivered content
        _policy.updateWithCode(exportParser.getOrigCode())
               .checkExport(_policy.export());
    }

    /**
     * Test that existing store will be removed.
     *
     * @throws Exception if test failed
     */
    @IssueLink("100")
    @Test(description = "issue #100: test that existing store will be removed")
    public void testRemoveStore()
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
     * Test update of policy with all state and without allstate.
     *
     *
     * @throws Exception if test failed
     */
    @IssueLink("99")
    @Test(description = "issue #99: test update of policy with all state and without allstate")
    public void testUpdateAllState()
        throws Exception
    {
        new PolicyData(this, "Test")
            .setAllState(true)
            .update()
            .update()
            .checkExport()
            .setAllState(false)
            .update()
            .update()
            .checkExport()
            .setAllState(true)
            .update()
            .update()
            .checkExport();
    }
}
