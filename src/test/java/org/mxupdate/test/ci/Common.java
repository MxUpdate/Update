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

package org.mxupdate.test.ci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;
import org.mxupdate.update.util.UpdateException_mxJPO.Error;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * The class is used to check common test cases related to configuration items.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Common
    extends AbstractTest
{
    /**
     * Defines the CI which must be ignored because updates are not ignored.
     *
     * @see #testException4CallingJpoCallerMethodWithoutArguments(CI)
     */
    private static final Set<AbstractTest.CI> IGNORES = new HashSet<AbstractTest.CI>();
    static  {
        Common.IGNORES.add(AbstractTest.CI.PRG_JPO);
        Common.IGNORES.add(AbstractTest.CI.PRG_MQL_PROGRAM);
        Common.IGNORES.add(AbstractTest.CI.PRG_PAGE);
    }

    /**
     * Data provider to get all configuration item enumerations for which the
     * JPO caller must be tested.
     *
     * @return CI enum's
     */
    @DataProvider(name = "cis4jpoCaller")
    public Object[][] getData4JPOCaller()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();
        for (final CI ci : CI.values())  {
            if (!Common.IGNORES.contains(ci) && (ci.filePath != null))  {
                ret.add(new Object[]{ci});
            }
        }
        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * Removes the MxUpdate commands used within automatic tests.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        for (final AbstractTest.CI ci : AbstractTest.CI.values())  {
            this.cleanup(ci);
        }
    }

    /**
     * Test that the correct error code is returned if the encoding of the file
     * name for the configuration item failed.
     *
     * @throws Exception    if test failed
     */
    @Test(description = "test file name with not correct encoding")
    public void testException4UpdateWrongFileName()
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put("COMMAND_Start@.tcl", "");
        final Map<?,?> bck = this.executeEncoded("Update", null, "FileContents", files);
        final Exception ex = (Exception) bck.get("exception");
        Assert.assertTrue((ex != null), "an exception must be thrown");
        Assert.assertTrue(ex.getMessage().contains("UpdateError #"
                                                    + Error.UTIL_STRINGUTIL_CONVERT_FROM_FILENAME.getCode()
                                                    + ":"),
                          "correct error code is returned");
    }

    /**
     * Checks if correct exception is thrown if JPO caller is thrown with wrong
     * method name.
     *
     * @param _ci   configuration item to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "cis4jpoCaller",
          description = "checks if correct exception is thrown if JPO caller is thrown with wrong method name")
    public void testException4CallingJpoCallerMethodWithoutArguments(final AbstractTest.CI _ci)
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put(_ci.filePrefix + AbstractTest.PREFIX + "_Test"
                        + ((_ci == AbstractTest.CI.IEF_EBOMSYNC_CONFIG) ? AbstractBusData.SEPARATOR + "-" : "") + ".tcl",
                  "mql exec prog org.mxupdate.update.util.JPOCaller;");
        final Map<?,?> bck = this.executeEncoded("Update", null, "FileContents", files);
        final Exception ex = (Exception) bck.get("exception");
        Assert.assertTrue((ex != null), "an exception must be thrown");
        Assert.assertTrue(ex.getMessage().contains("UpdateError #"
                                                    + Error.ABSTRACT_PROPERTY_JPO_CALL_METHOD_NOT_DEFINED.getCode()
                                                    + ":"),
                          "correct error code is returned");
    }

    /**
     * Checks if correct exception is thrown if JPO caller is thrown with no
     * argument.
     *
     * @param _ci   configuration item to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "cis4jpoCaller",
          description = "checks if correct exception is thrown if JPO caller is thrown with no argument")
    public void testException4CallingWrongJpoCallerMethod(final AbstractTest.CI _ci)
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put(_ci.filePrefix + AbstractTest.PREFIX + "_Test"
                        + ((_ci == AbstractTest.CI.IEF_EBOMSYNC_CONFIG) ? AbstractBusData.SEPARATOR + "-" : "") + ".tcl",
                  "mql exec prog org.mxupdate.update.util.JPOCaller unknownMethod");
        final Map<?,?> bck = this.executeEncoded("Update", null, "FileContents", files);
        final Exception ex = (Exception) bck.get("exception");
        Assert.assertTrue((ex != null), "an exception must be thrown");
        Assert.assertTrue(ex.getMessage().contains("UpdateError #"
                                                    + Error.ABSTRACT_PROPERTY_JPO_CALL_METHOD_UNKNOWN.getCode()
                                                    + ":"),
                          "correct error code is not returned " + ex.getMessage());
    }
}
