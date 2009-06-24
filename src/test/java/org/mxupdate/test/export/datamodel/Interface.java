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
 * Class is used to test interface exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test
public class Interface
    extends AbstractTest
{
    /**
     * Name of test interface.
     */
    private static final String INTERFACE_NAME = "MxUpdate_TestInterface";

    /**
     * Name of test type.
     */
    private static final String TYPE_NAME = "MxUpdate_TestType";

    /**
     * Name of test relationship.
     */
    private static final String REL_NAME = "MxUpdate_TestRelationship";

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
        this.cleanup(CI.INTERFACE, Interface.INTERFACE_NAME);
        this.cleanup(CI.TYPE, Interface.TYPE_NAME);
        this.cleanup(CI.RELATIONSHIP, Interface.REL_NAME);
    }

    /**
     * Check to export an interface for one single type.
     *
     * @throws Exception if test failed
     */
    public void interfaceWithType()
        throws Exception
    {
        this.mql("add type '" + Interface.TYPE_NAME + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' type '" + Interface.TYPE_NAME + "'");

        final String code = this.exportInterface();

        Assert.assertTrue(code.indexOf(" add type \"" + Interface.TYPE_NAME + "\"") >= 0,
                          "check that a type is defined in the TCL update code");
        Assert.assertTrue(code.indexOf(" add type all") < 0,
                          "check that not all types is defined in the TCL update code");
    }

    /**
     * Check to export an interface for all types.
     *
     * @throws Exception if test failed
     */
    public void interfaceWithAllTypes()
        throws Exception
    {
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' type all");

        final String code = this.exportInterface();

        Assert.assertTrue(code.indexOf(" add type \"") < 0,
                          "check that not a type is defined in the TCL update code");
        Assert.assertTrue(code.indexOf(" add type all") >= 0,
                          "check that all types is defined in the TCL update code");
    }

    /**
     * Check to export an interface for one single relationship.
     *
     * @throws Exception if test failed
     */
    public void interfaceWithRelationship()
        throws Exception
    {
        this.mql("add relationship '" + Interface.REL_NAME + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' relationship '" + Interface.REL_NAME + "'");


        final String code = this.exportInterface();

        Assert.assertTrue(code.indexOf(" add relationship \"" + Interface.REL_NAME + "\"") >= 0,
                          "check that a relation is defined in the TCL update code");
        Assert.assertTrue(code.indexOf(" add type all") < 0,
                          "check that not all relation is defined in the TCL update code");
    }

    /**
     * Check to export an interface for all relationships.
     *
     * @throws Exception if test failed
     */
    public void interfaceWithAllRelationships()
        throws Exception
    {
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' relationship all");

        final String code = this.exportInterface();

        Assert.assertTrue(code.indexOf(" add relationship \"" + Interface.REL_NAME + "\"") < 0,
                          "check that not a relation is defined in the TCL update code");
        Assert.assertTrue(code.indexOf(" add relationship all") >= 0,
                          "check that all relation is defined in the TCL update code");
    }

    /**
     * Exports test interface {@link #INTERFACE_NAME} and returns the related
     * TCL update code.
     *
     * @return TCL update code of the exported interface
     * @throws Exception if export failed
     */
    private String exportInterface()
        throws Exception
    {
        final Export export = this.export(CI.INTERFACE, Interface.INTERFACE_NAME);

        Assert.assertEquals(export.getPath(), "datamodel/interface", "path is not correct");
        Assert.assertEquals(export.getFileName(),
                            "INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                            "check that the correct file name is returned");

        return export.getCode();
    }
}
