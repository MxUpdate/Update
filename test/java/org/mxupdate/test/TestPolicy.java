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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import matrix.db.Context;

import org.mxupdate.util.MqlUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class is used to test policy updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class TestPolicy
{
    /**
     * MX context.
     */
    private Context context;

    /**
     * Policy update file.
     */
    private File file;

    /**
     * Connects to MX.
     *
     * @throws Exception if connect failed
     */
    @BeforeClass
    public void connect()
        throws Exception
    {
        this.context = new Context("http://172.16.62.120:8080/ematrix");
        this.context.resetContext("creator", "", null);
        this.context.connect();

        this.file = new File("test/java/org/mxupdate/test/testpolicy/POLICY_MxUpdate_Test.tcl");
    }

    /**
     * Disconnects from MX.
     *
     * @throws Exception if disconnect failed
     */
    @AfterClass
    public void close()
        throws Exception
    {
        this.context.disconnect();
        this.context.closeContext();
        this.context = null;
    }

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
        if (!"".equals(MqlUtil_mxJPO.execMql(this.context, "list policy MxUpdate_Test")))  {
            MqlUtil_mxJPO.execMql(this.context, "delete policy MxUpdate_Test");
        }
    }

    /**
     * Test for issue #29.
     *
     * @throws Exception if MQL execution failed
     */
    @Test(description = "Check that the new created policy has exact 11 properties defined")
    public void testProperties()
        throws Exception
    {
        MqlUtil_mxJPO.execMql(this.context,
                              "exec prog MxUpdate --update --policy \"" + this.file.getAbsolutePath() + "\"");

        Assert.assertTrue(!"".equals(MqlUtil_mxJPO.execMql(this.context, "list policy MxUpdate_Test")),
                          "policy was not created!");

        // check that only 11 properties are defined....
        // 4 state properties + installer + installed date + original name +
        // file date + author + version + application
        final Set<String> propNames = new HashSet<String>();
        final String propNamesStr = MqlUtil_mxJPO.execMql(this.context,
                "print policy MxUpdate_Test select property.name dump '\n'");
        for (final String propName : propNamesStr.split("\n"))  {
            propNames.add(propName);
        }
        Assert.assertTrue(propNames.size() == 11, "check that all properties are defined");
    }

    /**
     * Test for issue #28 and #29.
     *
     * @throws Exception if MQL execution failed
     */
    @Test(description = "Check that property with no name is removed")
    public void testNullProperties()
        throws Exception
    {
        MqlUtil_mxJPO.execMql(this.context,
                              "add policy MxUpdate_Test state Pending property \"\" value Test");
        MqlUtil_mxJPO.execMql(this.context,
                              "exec prog MxUpdate --update --policy \"" + this.file.getAbsolutePath() + "\"");
        // check that only 11 properties are defined....
        // 4 state properties + installer + installed date + original name +
        // file date + author + version + application
        final Set<String> propNames = new HashSet<String>();
        final String propNamesStr = MqlUtil_mxJPO.execMql(this.context,
                "print policy MxUpdate_Test select property.name dump '\n'");
        for (final String propName : propNamesStr.split("\n"))  {
            propNames.add(propName);
        }
        Assert.assertTrue(!propNames.contains(""), "Update did not remove empty property!");
        Assert.assertTrue(MqlUtil_mxJPO.execMql(this.context, "print policy MxUpdate_Test")
                                       .indexOf("property  value Test") < 0,
                          "Update did not remove empty property!");
        Assert.assertTrue(propNames.size() == 11, "check that all properties are defined");
    }
}
