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

package org.mxupdate.test.export.program;

import org.mxupdate.test.AbstractTest;
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
        this.cleanup(CI.JPO, JPO.JPO_COMPL1);
        this.cleanup(CI.JPO, JPO.JPO_NAME2);
    }

    /**
     * Checks that the new created JPO {@link #JPO_NAME2} is exported
     * correctly.
     *
     * @throws Exception if check failed
     */
    public void testExportJPOWithoutPackage()
        throws Exception
    {
        this.mql("add program " + JPO.JPO_NAME2 + " java");

        final Export export = this.export(CI.JPO, JPO.JPO_NAME2);

        Assert.assertEquals(export.getPath(), "jpo", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            JPO.JPO_NAME2 + "_mxJPO.java",
                            "check that the correct file name is returned");
        Assert.assertEquals(export.getCode(), "", "checks that Java code is empty");
    }

    /**
     * Checks that the new created JPO {@link #JPO_COMPL1} is exported
     * correctly. The JPO includes package {@link #JPO_PACKAGE1}.
     *
     * @throws Exception if check failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=26
     */
    public void testExportJPOWithPackage()
        throws Exception
    {
        this.mql("add program " + JPO.JPO_COMPL1 + " java");

        final Export export = this.export(CI.JPO, JPO.JPO_COMPL1);

        Assert.assertEquals(export.getPath(), "jpo/" + JPO.JPO_PACKAGE1, "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            JPO.JPO_NAME1 + "_mxJPO.java",
                            "check that the correct file name is returned");
        Assert.assertEquals(export.getCode().trim(),
                            "package MxUpdate;",
                            "checks that Java code has package definition");
    }
}
