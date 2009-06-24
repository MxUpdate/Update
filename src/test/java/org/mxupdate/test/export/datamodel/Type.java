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
 * Class is used to test type exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test
public class Type
    extends AbstractTest
{
    /**
     * Name of test type.
     */
    private static final String TYPE_NAME = "MxUpdate_TestType";

    /**
     * Name of test program 1.
     */
    private static final String PROG_NAME1 = "MxUpdate_TestProg1";

    /**
     * Name of test program 2.
     */
    private static final String PROG_NAME2 = "org / hello";

    /**
     * Removes the MxUpdate test type {@link #TYPE_NAME} and test programs
     * {@link #PROG_NAME1} and {@PROG_NAME2}.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterMethod
    public void cleanup()
        throws Exception
    {
        if (!"".equals(this.mql("list type " + Type.TYPE_NAME)))  {
            this.mql("delete type " + Type.TYPE_NAME);
        }
        if (!"".equals(this.mql("list program '" + Type.PROG_NAME1 + "'")))  {
            this.mql("delete program '" + Type.PROG_NAME1 + "'");
        }
        if (!"".equals(this.mql("list program '" + Type.PROG_NAME2 + "'")))  {
            this.mql("delete program '" + Type.PROG_NAME2 + "'");
        }
    }

    /**
     * Tests that a type without method is exported correctly.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    public void withoutMethod()
        throws Exception
    {
        this.mql("add type " + Type.TYPE_NAME);

        final String code = this.exportType();
        Assert.assertTrue(code.indexOf(" add method ") < 0, "checks that not method is exported");
    }

    /**
     * Tests that a type with one method is exported correctly.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    public void withOneMethod()
        throws Exception
    {
        this.mql("add program '" + Type.PROG_NAME1 + "' mql");
        this.mql("add type " + Type.TYPE_NAME
                + " method '" + Type.PROG_NAME1 + "'");

        final String code = this.exportType();
        final int idx1 = code.indexOf(" add method \"" + Type.PROG_NAME1 + "\"");
        final int idx2 = code.indexOf(" add method ", idx1 + 4);

        Assert.assertTrue(idx1 >= 0, "check that a method is defined in the TCL update code");
        Assert.assertTrue(idx2 < 0, "check that no second method is defined in the TCL update code");
    }

    /**
     * Tests that a type with two method is exported correctly.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    public void withTwoMethods()
        throws Exception
    {
        this.mql("add program '" + Type.PROG_NAME1 + "' mql");
        this.mql("add program '" + Type.PROG_NAME2 + "' mql");
        this.mql("add type " + Type.TYPE_NAME
                + " method '" + Type.PROG_NAME1 + "'"
                + " method '" + Type.PROG_NAME2 + "'");

        final String code = this.exportType();
        final int idx1 = code.indexOf(" add method \"" + Type.PROG_NAME1 + "\"");
        final int idx2 = code.indexOf(" add method \"" + Type.PROG_NAME2 + "\"");
        final int idx3 = code.indexOf(" add method ", (idx1 > idx2) ? idx1 + 4 : idx2 + 4);

        Assert.assertTrue(idx1 >= 0, "check that a method is defined in the TCL update code");
        Assert.assertTrue(idx2 >= 0, "check that a second method is defined in the TCL update code");
        Assert.assertTrue(idx3 < 0, "check that no third method is defined in the TCL update code");
    }

    /**
     * Exports test type {@link #TYPE_NAME} and returns the related TCL update
     * code.
     *
     * @return TCL update code of the exported type
     * @throws Exception if export failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    private String exportType()
        throws Exception
    {
        final Export export = this.export(CI.TYPE, Type.TYPE_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/type", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "TYPE_" + Type.TYPE_NAME + ".tcl",
                            "check that the correct file name is returned");

        return export.getCode();
    }
}
