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
 * Test class for JPO exports (extracts).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class TestJPOExport
    extends AbstractTest
{
    /**
     * Name of test JPO with package (but name is without package).
     *
     * @see #testExportJPOWithPackage()
     * @see #JPO_PACKAGE1
     * @see #JPO_COMPL1
     */
    private static final String JPO_NAME1 = "Test";

    /**
     * Package of the test JPO.
     *
     * @see #testExportJPOWithPackage()
     * @see #JPO_NAME1
     * @see #JPO_COMPL1
     */
    private static final String JPO_PACKAGE1 = "MxUpdate";

    /**
     * Complete name of the test JPO (concatenation of {@link #JPO_PACKAGE1}
     * and {@link #JPO_NAME1}).
     *
     * @see #testExportJPOWithPackage()
     * @see #JPO_NAME1
     * @see #JPO_PACKAGE1
     */
    private static final String JPO_COMPL1 = TestJPOExport.JPO_PACKAGE1 + "." + TestJPOExport.JPO_NAME1;

    /**
     * Name of test JPO without package name.
     */
    private static final String JPO_NAME2 = "MxUpdate_Test";

    /**
     * Cleanups the MX system by deleting the test JPOs with names
     * {@link #JPO_COMPL1} and {@link #JPO_NAME2}.
     *
     * @throws Exception if cleanup failed
     * @see #JPO_NAME
     */
    @BeforeMethod
    @AfterMethod
    public void cleanup()
        throws Exception
    {
        if (!"".equals(this.mql("list program " + TestJPOExport.JPO_COMPL1)))  {
            this.mql("delete program " + TestJPOExport.JPO_COMPL1);
        }
        if (!"".equals(this.mql("list program " + TestJPOExport.JPO_NAME2)))  {
            this.mql("delete program " + TestJPOExport.JPO_NAME2);
        }
    }

    /**
     * Checks that the new created JPO {@link #JPO_NAME2} is exported
     * correctly.
     *
     * @throws Exception if check failed
     */
    @Test
    public void testExportJPOWithoutPackage()
        throws Exception
    {
        this.mql("add program " + TestJPOExport.JPO_NAME2 + " java");

        final Map<String,Collection<String>> params = new HashMap<String,Collection<String>>();
        params.put("JPO", Arrays.asList(new String[]{TestJPOExport.JPO_NAME2}));
        final Map<String,Collection<Map<String,String>>> bck =
                this.<Map<String,Collection<Map<String,String>>>>jpoInvoke("org.mxupdate.plugin.Export",
                                                                           "exportByName",
                                                                           params)
                    .getValues();

        Assert.assertNotNull(bck);
        Assert.assertTrue(bck.containsKey("JPO"));
        Assert.assertEquals(bck.get("JPO").size(), 1, "one element is returned");
        final Map<String,String> desc = bck.get("JPO").iterator().next();
        Assert.assertEquals(desc.get("name"), TestJPOExport.JPO_NAME2, "returned name is equal to given name");
        Assert.assertEquals(desc.get("path"), "jpo", "path is not correct");
        Assert.assertEquals(desc.get("filename"),
                            TestJPOExport.JPO_NAME2 + "_mxJPO.java",
                            "check that the correct file name is returned");
        Assert.assertEquals(desc.get("code"), "", "checks that Java code is empty");
    }

    /**
     * Checks that the new created JPO {@link #JPO_COMPL1} is exported
     * correctly. The JPO includes package {@link #JPO_PACKAGE1}.
     *
     * @throws Exception if check failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=26
     */
    @Test
    public void testExportJPOWithPackage()
        throws Exception
    {
        this.mql("add program " + TestJPOExport.JPO_COMPL1 + " java");

        final Map<String,Collection<String>> params = new HashMap<String,Collection<String>>();
        params.put("JPO", Arrays.asList(new String[]{TestJPOExport.JPO_COMPL1}));
        final Map<String,Collection<Map<String,String>>> bck =
                this.<Map<String,Collection<Map<String,String>>>>jpoInvoke("org.mxupdate.plugin.Export",
                                                                           "exportByName",
                                                                           params)
                    .getValues();

        Assert.assertNotNull(bck);
        Assert.assertTrue(bck.containsKey("JPO"));
        Assert.assertEquals(bck.get("JPO").size(), 1, "one element is returned");
        final Map<String,String> desc = bck.get("JPO").iterator().next();
        Assert.assertEquals(desc.get("name"), TestJPOExport.JPO_COMPL1, "returned name is equal to given name");
        Assert.assertEquals(desc.get("path"), "jpo/" + TestJPOExport.JPO_PACKAGE1, "path is not correct");
        Assert.assertEquals(desc.get("filename"),
                            TestJPOExport.JPO_NAME1 + "_mxJPO.java",
                            "check that the correct file name is returned");
        Assert.assertEquals(desc.get("code").trim(),
                            "package MxUpdate;",
                            "checks that Java code has package definition");
    }
}
