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

package org.mxupdate.test.export.datamodel;

import org.mxupdate.test.AbstractTest;
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
@Test
public class Policy
    extends AbstractTest
{
    /**
     * Name of test policy.
     */
    private static final String POLICY_NAME = "MxUpdate_TestType";

    /**
     * Removes the MxUpdate test type {@link #POLICY_NAME}.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterMethod
    public void removeType()
        throws Exception
    {
        if (!"".equals(this.mql("list policy " + Policy.POLICY_NAME)))  {
            this.mql("delete policy " + Policy.POLICY_NAME);
        }
    }

    /**
     * Creates a new policy for all types and tries to export it.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=30
     */
    public void testExportPolicyForAllTypes()
        throws Exception
    {
        this.mql("add policy " + Policy.POLICY_NAME + " type all");

        final Export export = this.export(CI.DM_POLICY, Policy.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + Policy.POLICY_NAME + ".tcl",
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
    public void testStateSymbolicNameExportedIfNotDefined()
        throws Exception
    {
        this.mql("add policy " + Policy.POLICY_NAME
                + " state create");

        final Export export = this.export(CI.DM_POLICY, Policy.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + Policy.POLICY_NAME + ".tcl",
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
     * @see http://code.google.com/p/mxupdate/issues/detail?id=33
     */
    public void testNoPropertyDefinitionForStateSymbolicName()
        throws Exception
    {
        this.mql("add policy " + Policy.POLICY_NAME
                + " state create property state_create value create");

        final Export export = this.export(CI.DM_POLICY, Policy.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + Policy.POLICY_NAME + ".tcl",
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
     * @see http://code.google.com/p/mxupdate/issues/detail?id=34
     */
    public void testAllSymbolicNamesForStatesDefined()
        throws Exception
    {
        this.mql("add policy " + Policy.POLICY_NAME
                + " state create property state_create value create property state_exists value create");

        final Export export = this.export(CI.DM_POLICY, Policy.POLICY_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/policy", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "POLICY_" + Policy.POLICY_NAME + ".tcl",
                            "check that the correct file name is returned");
        final String code = export.getCode();
        Assert.assertTrue(code.indexOf("registeredName \"state_create\"") >= 0,
                          "check that symbolic name 'state_create' exists");
        Assert.assertTrue(code.indexOf("registeredName \"state_exists\"") >= 0,
                          "check that symbolic name 'state_exists' exists");
    }
}
