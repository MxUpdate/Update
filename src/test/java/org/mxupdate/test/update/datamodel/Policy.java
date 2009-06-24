/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.test.update.datamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class is used to test policy updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test
public class Policy
    extends AbstractTest
{

    /**
     * Name of the test policy.
     */
    private static final String POLICY_NAME = "MxUpdate_Test";

    /**
     * TCL update code for the test policy.
     */
    private static final String POLICY_UPDATE_CODE =
              "################################################################################\n"
            + "# POLICY:\n"
            + "# ~~~~~~~\n"
            + "# MxUpdate_Test\n"
            + "#\n"
            + "# SYMBOLIC NAME:\n"
            + "# ~~~~~~~~~~~~~~\n"
            + "# policy_MxUpdate_Test\n"
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
     * Removes the MxUpdate test policy.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterMethod
    public void removePolicy()
        throws Exception
    {
        if (!"".equals(this.mql("list policy " + Policy.POLICY_NAME)))  {
            this.mql("delete policy " + Policy.POLICY_NAME);
        }
    }

    /**
     * Checks that states where no symbolic name is defined in the TCL update
     * does not define a symbolic name (with empty name).
     *
     * @throws Exception if MQL execution failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=29
     */
    public void testProperties()
        throws Exception
    {
        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + Policy.POLICY_NAME + ".tcl",
                   Policy.POLICY_UPDATE_CODE);
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
     * Test for for an update for existing policy with empty property and state
     * names without symbolic names. The empty property must be removed after
     * the update and for the state without symbolic name no registration must
     * be done.
     *
     * @throws Exception if MQL execution failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=28
     * @see http://code.google.com/p/mxupdate/issues/detail?id=29
     */
    public void testNullProperties()
        throws Exception
    {
        this.mql("add policy MxUpdate_Test state Pending property \"\" value Test");

        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + Policy.POLICY_NAME + ".tcl",
                   Policy.POLICY_UPDATE_CODE);
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
     * Tests that multiple symbolic names for a single state could be defined.
     *
     * @throws Exception if test failed
     */
    public void testMultipleStateSymbolicNames()
        throws Exception
    {
        final Map<String,String> params = new HashMap<String,String>();
        params.put("POLICY_" + Policy.POLICY_NAME + ".tcl",
                   "updatePolicy \"${NAME}\" {"
                    + "  state \"Pending\"  {"
                    + "    registeredName \"state_Exists\""
                    + "    registeredName \"state_Pending\""
                    + "  }"
                    + "}");
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertEquals(this.mql("print policy " + Policy.POLICY_NAME
                                + " select property[state_Exists].value dump"),
                            "Pending",
                            "test that symbolic name 'state_Exists' is correct registered");
        Assert.assertEquals(this.mql("print policy " + Policy.POLICY_NAME
                                + " select property[state_Pending].value dump"),
                            "Pending",
                            "test that symbolic name 'state_Pending' is correct registered");
    }
}
