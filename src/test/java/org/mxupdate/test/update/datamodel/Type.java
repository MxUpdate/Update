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

import org.mxupdate.test.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class is used to test type updates.
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
     * Test update for existing type with no method and as target no method.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    public void noMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        this.mql("add type '" + Type.TYPE_NAME + "'");

        this.update("TYPE_" + Type.TYPE_NAME + ".tcl",
                    "mql mod type \"${NAME}\"");

        Assert.assertEquals(this.mql("print type '" + Type.TYPE_NAME + "' select method dump"),
                            "",
                            "check that no method is defined");
    }

    /**
     * Test for an update for an existing type with one method, but after the
     * update without method.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    public void noMethod4ExistingTypeWithOneMethod()
        throws Exception
    {
        this.mql("add program '" + Type.PROG_NAME1 + "' mql");
        this.mql("add type " + Type.TYPE_NAME
                + " method '" + Type.PROG_NAME1 + "'");

        this.update("TYPE_" + Type.TYPE_NAME + ".tcl",
                    "mql mod type \"${NAME}\"");

        Assert.assertEquals(this.mql("print type '" + Type.TYPE_NAME + "' select method dump"),
                            "",
                            "check that no method is defined");
    }

    /**
     * Test an update for an existing type with no method, but after the update
     * with one method.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    public void oneMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        this.mql("add program '" + Type.PROG_NAME1 + "' mql");
        this.mql("add type '" + Type.TYPE_NAME + "'");

        this.update("TYPE_" + Type.TYPE_NAME + ".tcl",
                    "mql mod type \"${NAME}\" add method '" + Type.PROG_NAME1 + "'");

        Assert.assertEquals(this.mql("print type '" + Type.TYPE_NAME + "' select method dump"),
                            Type.PROG_NAME1,
                            "check that one method is defined");
    }
}
