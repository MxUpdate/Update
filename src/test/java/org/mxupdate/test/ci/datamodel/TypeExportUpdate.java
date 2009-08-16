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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.datamodel.AbstractAttribute;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.AbstractProgramData;
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
public class TypeExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test attributes.
     *
     * @return object array with all test commands
     */
    @DataProvider(name = "types")
    public Object[][] dataTypes()
    {
        final DataCollection data1 = new DataCollection(this);
        data1.getType("TestType");

        final DataCollection data2 = new DataCollection(this);
        data2.getType("TestType \" 1");

        final DataCollection data3 = new DataCollection(this);
        data3.getType("TestType \" 2")
             .addAttribute(data3.getAttributeString("String Attribute \" ' Hallo"))
             .addAttribute(data3.getAttributeString("String Attribute \" { Hallo"));

        final DataCollection data4 = new DataCollection(this);
        data4.getType("TestType \" 2")
             .addAttribute(data4.getAttributeString("String Attribute \" ' Hallo"))
             .addAttribute(data4.getAttributeString("String Attribute \" { Hallo"))
             .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", data4.getMQLProgram("Test Program")));

        return new Object[][]  {
            new Object[]{data1, "TestType"},
            new Object[]{data2, "TestType \" 1"},
            new Object[]{data3, "TestType \" 2"},
            new Object[]{data4, "TestType \" 2"},
        };
    }

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
        this.cleanup(AbstractTest.CI.MQL_PROGRAM);
        this.cleanup(AbstractTest.CI.ATTRIBUTE_BOOLEAN);
        this.cleanup(AbstractTest.CI.ATTRIBUTE_DATE);
        this.cleanup(AbstractTest.CI.ATTRIBUTE_INTEGER);
        this.cleanup(AbstractTest.CI.ATTRIBUTE_REAL);
        this.cleanup(AbstractTest.CI.ATTRIBUTE_STRING);
        this.cleanup(AbstractTest.CI.TYPE);
    }


    /**
     * Tests a new created command and the related export.
     *
     * @param _data         data collection
     * @param _name         name of type to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "types", description = "test export of new created types")
    public void testExport(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final TypeData type = _data.getType(_name);

        _data.create();

        final ExportParser exportParser = type.export();
        type.checkExport(exportParser);
    }

    /**
     * Tests an update of non existing command. The result is tested with by
     * exporting the command and checking the result.
     *
     * @param _data         data collection
     * @param _name         name of type to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "types", description = "test update of non existing types")
    public void testUpdate(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        // create referenced attributes
        for (final AbstractAttribute<?> attribute : _data.getAttributes())  {
            attribute.create();
        }
        // create referenced methods
        for (final AbstractProgramData<?> method : _data.getPrograms())  {
            method.create();
        }

        final TypeData type = _data.getType(_name);

        this.update(type);
        final ExportParser exportParser = type.export();
        type.checkExport(exportParser);
    }

    /**
     * Test update for existing type with no method and as target no method.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    @Test(description = "update type with no method for existing type with no method")
    public void updateNoMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        data1.create();

        this.update(type);

        Assert.assertEquals(this.mql("print type '" + type.getName() + "' select method dump"),
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

        this.update(type);

        Assert.assertEquals(this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
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
    @Test(description = "update type with method for existing type with no method")
    public void updateOneMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        final AbstractProgramData<?> method = data1.getMQLProgram("TestProg1");
        data1.create();
        // method must be defined after create (to test the update..)
        type.addMethod(method);

        this.update(type);

        Assert.assertEquals(this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
                            AbstractTest.convertMql(method.getName()),
                            "check that one method is defined");
    }


    /**
     * Tests that a type without method is exported correctly.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    @Test(description = "export type without method")
    public void exportWithoutMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        data1.create();

        this.exportType(type);
    }

    /**
     * Tests that a type with one method is exported correctly.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    @Test(enabled=false,description = "export type with one method")
    public void exportWithOneMethod()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        type.addMethod(data1.getMQLProgram("Test Program"));
        data1.create();

        this.exportType(type);
    }

    /**
     * Tests that a type with two method is exported correctly.
     *
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=36
     */
    @Test(description = "export type with two methods")
    public void exportWithTwoMethods()
        throws Exception
    {
        final DataCollection data1 = new DataCollection(this);
        final TypeData type = data1.getType("TestType");
        type.addMethod(data1.getMQLProgram("Test Program 1"));
        type.addMethod(data1.getMQLProgram("Test Program 2"));
        data1.create();

        this.exportType(type);
    }

    /**
     * Exports test type {@link #TYPE_NAME}, checks the exports and returns the
     * related parsed export.
     *
     * @param _type     type to export
     * @return parsed export of the exported type
     * @throws Exception if export failed
     */
    private ExportParser exportType(final TypeData _type)
        throws Exception
    {
        final ExportParser exportParser = _type.export();
        _type.checkExport(exportParser);
        return exportParser;
    }
}
