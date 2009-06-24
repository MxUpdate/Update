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

package org.mxupdate.test.update.program;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for JPO updates (imports).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test
public class JPO
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
    private static final String JPO_COMPL1 = JPO.JPO_PACKAGE1 + "." + JPO.JPO_NAME1;

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
        if (!"".equals(this.mql("list program " + JPO.JPO_COMPL1)))  {
            this.mql("delete program " + JPO.JPO_COMPL1);
        }
        if (!"".equals(this.mql("list program " + JPO.JPO_NAME2)))  {
            this.mql("delete program " + JPO.JPO_NAME2);
        }
    }

    /**
     * Tests, if the JPO within MX is created and registered with the correct
     * symbolic name.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=22
     */
    public void testJPOUpdate()
        throws Exception
    {
        final Map<String,String> params = new HashMap<String,String>();
        params.put(JPO.JPO_NAME2 + "_mxJPO.java",
                   "public class " + JPO.JPO_NAME2 + "_mxJPO {}");
        this.jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);

        Assert.assertTrue(!"".equals(this.mql("list program " + JPO.JPO_NAME2)),
                          "check JPO is created");
        Assert.assertTrue(!"".equals(this.mql("list property to program " + JPO.JPO_NAME2)),
                          "check that the JPO is registered");
        Assert.assertEquals(this.mql("list property to program " + JPO.JPO_NAME2),
                            "program_" + JPO.JPO_NAME2
                                    + " on program eServiceSchemaVariableMapping.tcl to program "
                                    + JPO.JPO_NAME2,
                            "check that the JPO is registered with correct symbolic name");
    }
}
