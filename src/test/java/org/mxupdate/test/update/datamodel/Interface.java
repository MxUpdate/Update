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
 * Class is used to test interface updates.
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
     * Name of first test relationship.
     */
    private static final String REL_NAME1 = "MxUpdate_TestRelationship1";

    /**
     * Name of second test relationship.
     */
    private static final String REL_NAME2 = "MxUpdate_TestRelationship2";

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
        this.cleanup(CI.RELATIONSHIP, Interface.REL_NAME1);
        this.cleanup(CI.RELATIONSHIP, Interface.REL_NAME2);
    }

    /**
     * Check for an interface update with all types of an interface with one
     * assigned type.
     *
     * @throws Exception if test failed
     */
    public void allTypes4ExistingType()
        throws Exception
    {
        this.mql("add type '" + Interface.TYPE_NAME + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' type '" + Interface.TYPE_NAME + "'");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                    "mql mod interface \"${NAME}\" add type all");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select type dump"),
                            "all",
                            "check that only all type is defined");
    }

    /**
     * Check for an interface update with one types of an interface with all
     * assigned types.
     *
     * @throws Exception if test failed
     */
    public void oneType4ExistingAllTypes()
        throws Exception
    {
        this.mql("add type '" + Interface.TYPE_NAME + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' type all");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                "mql mod interface \"${NAME}\" add type \"" + Interface.TYPE_NAME + "\"");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select type dump"),
                            Interface.TYPE_NAME,
                            "check that only one type is defined");
    }

    /**
     * Check for an interface update with one types of non existing interface.
     *
     * @throws Exception if test failed
     */
    public void oneType4NonExisting()
        throws Exception
    {
        this.mql("add type '" + Interface.TYPE_NAME + "'");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                    "mql mod interface \"${NAME}\" add type \"" + Interface.TYPE_NAME + "\"");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select type dump"),
                            Interface.TYPE_NAME,
                            "check that only one type is defined");
    }

    /**
     * Check for an interface update with all relationships of an interface
     * with one assigned relationship.
     *
     * @throws Exception if test failed
     */
    public void allRelationships4ExistingRelationship()
        throws Exception
    {
        this.mql("add relationship '" + Interface.REL_NAME1 + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' relationship '" + Interface.REL_NAME1 + "'");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                    "mql mod interface \"${NAME}\" add relationship all");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select relationship dump"),
                            "all",
                            "check that only all relationship is defined");
    }

    /**
     * Check for an interface update with one relationships of an interface
     * with all assigned relationships.
     *
     * @throws Exception if test failed
     */
    public void oneRelationship4ExistingOneRelationships()
        throws Exception
    {
        this.mql("add relationship '" + Interface.REL_NAME1 + "'");
        this.mql("add relationship '" + Interface.REL_NAME2 + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' relationship '" + Interface.REL_NAME1 + "'");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                "mql mod interface \"${NAME}\" add relationship \"" + Interface.REL_NAME2 + "\"");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select relationship dump"),
                            Interface.REL_NAME2,
                            "check that only second relationship is defined");
    }

    /**
     * Check for an interface update with one relationships of an interface
     * with all assigned relationships.
     *
     * @throws Exception if test failed
     */
    public void oneRelationship4ExistingAllRelationships()
        throws Exception
    {
        this.mql("add relationship '" + Interface.REL_NAME1 + "'");
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' relationship all");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                "mql mod interface \"${NAME}\" add relationship \"" + Interface.REL_NAME1 + "\"");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select relationship dump"),
                            Interface.REL_NAME1,
                            "check that only one relationship is defined");
    }

    /**
     * Check for an interface update with one relationship of an non existing
     * interface.
     *
     * @throws Exception if test failed
     */
    public void oneRelationship4NonExisting()
        throws Exception
    {
        this.mql("add relationship '" + Interface.REL_NAME1 + "'");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                    "mql mod interface \"${NAME}\" add relationship \"" + Interface.REL_NAME1 + "\"");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select relationship dump"),
                            Interface.REL_NAME1,
                            "check that only one relationship is defined");
    }


    /**
     * Check for an interface update with no types and no relationships of an
     * existing interface with all assigned types and all relationships.
     *
     * @throws Exception if test failed
     */
    public void non4ExistingAllTypesRelationship()
        throws Exception
    {
        this.mql("add interface '" + Interface.INTERFACE_NAME + "' type all relationship all");

        this.update("INTERFACE_" + Interface.INTERFACE_NAME + ".tcl",
                    "mql mod interface \"${NAME}\"");

        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select type dump"),
                            "",
                            "check that no type is defined");
        Assert.assertEquals(this.mql("print interface '" + Interface.INTERFACE_NAME + "' select relationship dump"),
                            "",
                            "check that no relationship is defined");
    }
}
