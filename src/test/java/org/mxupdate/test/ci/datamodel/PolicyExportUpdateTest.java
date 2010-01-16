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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.util.IssueLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for policy exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PolicyExportUpdateTest
    extends AbstractTest
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
            + "# " + PolicyExportUpdateTest.POLICY_NAME + "\n"
            + "#\n"
            + "# SYMBOLIC NAME:\n"
            + "# ~~~~~~~~~~~~~~\n"
            + "# policy_" + PolicyExportUpdateTest.POLICY_NAME + "\n"
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
     * Removes the MxUpdate test policy {@link #POLICY_NAME}.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void removePolicy()
        throws Exception
    {
        if (!"".equals(this.mql("list policy " + PolicyExportUpdateTest.POLICY_NAME)))  {
            this.mql("delete policy " + PolicyExportUpdateTest.POLICY_NAME);
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
        if (!"".equals(this.mql("list type " + PolicyExportUpdateTest.TYPE_NAME)))  {
            this.mql("delete type " + PolicyExportUpdateTest.TYPE_NAME);
        }
    }

    /**
     * Creates a new policy for all types and tries to export it.
     *
     * @throws Exception if test failed
     */
    @IssueLink("30")
    @Test(description = "export policy for all types")
    public void exportPolicyForAllTypes()
        throws Exception
    {
        this.mql("add policy " + PolicyExportUpdateTest.POLICY_NAME + " type all");

        final Export export = this.export(CI.DM_POLICY, PolicyExportUpdateTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        final String testCode = code.substring(code.lastIndexOf('#')).trim();
        Assert.assertTrue(testCode.indexOf(" type all") > 0, "checks that all types are defined");
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
        this.mql("add policy " + PolicyExportUpdateTest.POLICY_NAME
                + " state create");

        final Export export = this.export(CI.DM_POLICY, PolicyExportUpdateTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(code.indexOf("registeredName \"state_create\"") >= 0,
                          "check that symbolic name 'state_create' exists");
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
    public void exportNoPropertyDefinitionForStateSymbolicName()
        throws Exception
    {
        this.mql("add policy " + PolicyExportUpdateTest.POLICY_NAME
                + " state create property state_create value create");

        final Export export = this.export(CI.DM_POLICY, PolicyExportUpdateTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
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
    public void exportAllSymbolicNamesForStatesDefined()
        throws Exception
    {
        this.mql("add policy " + PolicyExportUpdateTest.POLICY_NAME
                + " state create property state_create value create property state_exists value create");

        final Export export = this.export(CI.DM_POLICY, PolicyExportUpdateTest.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(code.indexOf("registeredName \"state_create\"") >= 0,
                          "check that symbolic name 'state_create' exists");
        Assert.assertTrue(code.indexOf("registeredName \"state_exists\"") >= 0,
                          "check that symbolic name 'state_exists' exists");
    }

    /**
     * Test update of policy with all formats in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with all formats in braces")
    public void updateFormatAllWithBraces()
        throws Exception
    {
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  format {all}\n"
            + "}";

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl", updateCode);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertTrue(
                this.mql("print pol " + PolicyExportUpdateTest.POLICY_NAME + " ").contains("\n  format all\n"),
                "check that all formats are defined");
    }

    /**
     * Test update of policy with all formats in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with all formats in braces")
    public void updateFormatAllWithoutBraces()
        throws Exception
    {
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  format all\n"
            + "}";

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl", updateCode);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertTrue(
                this.mql("print pol " + PolicyExportUpdateTest.POLICY_NAME + " ").contains("\n  format all\n"),
                "check that all formats are defined");
    }

    /**
     * Test update of policy with one format.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with one format")
    public void updateFormatOne()
        throws Exception
    {
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  format {generic}\n"
            + "}";

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl", updateCode);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertEquals(
                this.mql("print pol " + PolicyExportUpdateTest.POLICY_NAME + " select format dump"),
                "generic",
                "check for format generic");
    }

    /**
     * Tests that multiple symbolic names for a single state could be defined.
     *
     * @throws Exception if test failed
     */
    @IssueLink("11")
    @Test(description = "update policy with multiple symbolic names for one state")
    public void updateMultipleStateSymbolicNames()
        throws Exception
    {
        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
                   "updatePolicy \"${NAME}\" {"
                    + "  state \"Pending\"  {"
                    + "    registeredName \"state_Exists\""
                    + "    registeredName \"state_Pending\""
                    + "  }"
                    + "}");
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertEquals(this.mql("print policy " + PolicyExportUpdateTest.POLICY_NAME
                                + " select property[state_Exists].value dump"),
                            "Pending",
                            "test that symbolic name 'state_Exists' is correct registered");
        Assert.assertEquals(this.mql("print policy " + PolicyExportUpdateTest.POLICY_NAME
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
    public void updateNullProperties()
        throws Exception
    {
        this.mql("add policy MxUpdate_Test state Pending property \"\" value Test");

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
                   PolicyExportUpdateTest.POLICY_UPDATE_CODE);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        // check that only 11 properties are defined....
        // 4 state properties + installer + installed date + original name +
        // file date + author + version + application
        final Set<String> propNames = new HashSet<String>();
        final String propNamesStr = this.mql("print policy MxUpdate_Test select property.name dump '\n'");
        for (final String propName : propNamesStr.split("\n"))  {
            propNames.add(propName);
        }
        Assert.assertTrue(!propNames.contains(""), "Update did not remove empty property!");
        Assert.assertTrue(this.mql("print policy MxUpdate_Test").indexOf("property  value Test") < 0,
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
    @Test(description = "")
    public void updateProperties()
        throws Exception
    {
        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl",
                   PolicyExportUpdateTest.POLICY_UPDATE_CODE);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertTrue(!"".equals(this.mql("list policy MxUpdate_Test")),
                          "policy was not created!");

        // check that only 11 properties are defined....
        // 4 state properties + installer + installed date + original name +
        // file date + author + version + application
        final Set<String> propNames = new HashSet<String>();
        final String propNamesStr = this.mql("print policy MxUpdate_Test select property.name dump '\n'");
        for (final String propName : propNamesStr.split("\n"))  {
            propNames.add(propName);
        }
        Assert.assertTrue(propNames.size() == 11, "check that all properties are defined");
        Assert.assertTrue(this.mql("print policy MxUpdate_Test").indexOf("property  value ") < 0,
                          "Update did set an empty property!");
    }


    /**
     * Test update of policy with all types in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with all types in braces")
    public void updateTypeAllWithBraces()
        throws Exception
    {
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {all}\n"
            + "}";

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl", updateCode);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertTrue(
                this.mql("print pol " + PolicyExportUpdateTest.POLICY_NAME + " ").contains("\n  type all\n"),
                "check that all types are defined");
    }

    /**
     * Test update of policy with all types in braces.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with all types in braces")
    public void updateTypeAllWithoutBraces()
        throws Exception
    {
        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type all\n"
            + "}";

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl", updateCode);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertTrue(
                this.mql("print pol " + PolicyExportUpdateTest.POLICY_NAME + " ").contains("\n  type all\n"),
                "check that all types are defined");
    }

    /**
     * Test update of policy with one type.
     *
     * @throws Exception if test failed
     */
    @IssueLink("86")
    @Test(description = "update policy with one type")
    public void updateTypeOne()
        throws Exception
    {
        this.mql("add type " + PolicyExportUpdateTest.TYPE_NAME);

        final String updateCode =
            "updatePolicy \"${NAME}\" {\n"
            + "  description \"\"\n"
            + "  type {" + PolicyExportUpdateTest.TYPE_NAME + "}\n"
            + "}";

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + PolicyExportUpdateTest.POLICY_NAME + ".tcl", updateCode);
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertEquals(
                this.mql("print pol " + PolicyExportUpdateTest.POLICY_NAME + " select type dump"),
                PolicyExportUpdateTest.TYPE_NAME,
                "check for correct type");
    }
}
