/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.util.IssueLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class is used to test type updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class TypeTest
    extends AbstractDataWithAttributesExportUpdateTest<TypeData>
{
    /**
     * Creates for given <code>_name</code> a new type data instance.
     *
     * @param _name     name of the type data instance
     * @return type data instance
     */
    @Override()
    protected TypeData createNewData(final String _name)
    {
        return new TypeData(this, _name);
    }

    /**
     * Data provider for test types.
     *
     * @return object array with all test types
     */
    @IssueLink("36")
    @DataProvider(name = "data")
    public Object[][] dataTypes()
    {
        return this.prepareData("types",
                new Object[]{
                        "type without anything",
                        new TypeData(this, "TestType")},
                new Object[]{
                        "type with escaped name",
                        new TypeData(this, "TestType \" 1")},
                new Object[]{
                        "type with two escaped attributes and trigger program",
                        new TypeData(this, "TestType \" 1")
                                .addAttribute(new AttributeStringData(this, "String Attribute \" ' Hallo"))
                                .addAttribute(new AttributeStringData(this, "String Attribute \" { Hallo"))
                                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test Program")))},
                new Object[]{
                        "type with one MQL program as method",
                        new TypeData(this, "TestType \" 1")
                                .addMethod(new MQLProgramData(this, "Test Program"))},
                new Object[]{
                        "type with two MQL programs as method",
                        new TypeData(this, "TestType \" 1")
                                .addMethod(new MQLProgramData(this, "Test Program 1"))
                                .addMethod(new MQLProgramData(this, "Test Program 2"))}
        );
    }

    /**
     * Removes the MxUpdate programs, attributes and types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_BOOLEAN);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_DATE);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_INTEGER);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_REAL);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_STRING);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    /**
     * Creates all depending administration objects for given
     * <code>_type</code>.
     *
     * @param _type         type with depending objects
     * @throws Exception if create failed
     */
    @Override
    protected void createDependings(final TypeData _type)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _type.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create attributes
        for (final AbstractAttributeData<?> attr : _type.getAttributes())  {
            attr.create();
        }
        // create programs
        for (final AbstractDataWithTrigger.AbstractTrigger<?> trig : _type.getTriggers())  {
            trig.getProgram().create();
        }
        for (final AbstractProgramData<?> prog : _type.getMethods())  {
            prog.create();
        }
    }

    /**
     * Test update for existing type with no method and as target no method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with no method for existing type with no method")
    public void updateNoMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        data1.create();

        type.update();

        Assert.assertEquals(this.mql("print type '" + type.getName() + "' select method dump"),
                            "",
                            "check that no method is defined");
    }

    /**
     * Test for an update for an existing type with one method, but after the
     * update without method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with no method for existing type with one method")
    public void updateNoMethod4ExistingTypeWithOneMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        final AbstractProgramData<?> method = data1.getMQLProgram("TestProg1");
        data1.create();
        this.mql("mod type \"" + AbstractTest.convertMql(type.getName())
                + "\" add method \"" + AbstractTest.convertMql(method.getName()) + "\"");

        type.update();

        Assert.assertEquals(this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
                            "",
                            "check that no method is defined");
    }

    /**
     * Test an update for an existing type with no method, but after the update
     * with one method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with method for existing type with no method")
    public void updateOneMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        final AbstractProgramData<?> method = data1.getMQLProgram("TestProg1");
        data1.create();
        // method must be defined after create (to test the update..)
        type.addMethod(method)
            .update();

        Assert.assertEquals(this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
                            AbstractTest.convertMql(method.getName()),
                            "check that one method is defined");
    }
}
