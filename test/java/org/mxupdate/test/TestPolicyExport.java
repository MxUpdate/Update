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

package org.mxupdate.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class TestPolicyExport
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
        if (!"".equals(this.mql("list policy " + TestPolicyExport.POLICY_NAME)))  {
            this.mql("delete policy " + TestPolicyExport.POLICY_NAME);
        }
    }

    /**
     * Creates a new policy for all types and tries to export it. The check
     * is done because of issue #30.
     *
     * @throws Exception if test failed
     */
    @Test
    public void testExportPolicyForAllTypes()
        throws Exception
    {
        this.mql("add policy " + TestPolicyExport.POLICY_NAME + " type all");

        final Map<String,Collection<String>> params = new HashMap<String,Collection<String>>();
        params.put("Policy", Arrays.asList(new String[]{TestPolicyExport.POLICY_NAME}));
        final Map<String,Collection<Map<String,String>>> bck =
                this.<Map<String,Collection<Map<String,String>>>>jpoInvoke("org.mxupdate.plugin.Export",
                                                                           "export",
                                                                           params);

        Assert.assertNotNull(bck);
        Assert.assertTrue(bck.containsKey("Policy"));
        Assert.assertEquals(bck.get("Policy").size(), 1, "one element is returned");
        final Map<String,String> desc = bck.get("Policy").iterator().next();
        Assert.assertEquals(desc.get("name"), TestPolicyExport.POLICY_NAME, "returned name is equal to given name");
        Assert.assertEquals(desc.get("path"), "datamodel/policy", "path is not correct");
        final String code = desc.get("code");
        final String testCode = code.substring(code.lastIndexOf('#')).trim();
        Assert.assertTrue(testCode.indexOf(" type all") > 0, "checks that all types are defined");
    }
}
