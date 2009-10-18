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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the update and export of attributes.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class AttributeExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test attributes.
     *
     * @return object array with all test commands
     */
    @DataProvider(name = "attributes")
    public Object[][] dataAttributes()
    {
        final DataCollection data1 = new DataCollection(this);
        data1.getAttributeBoolean("Boolean Attribute");

        final DataCollection data2 = new DataCollection(this);
        data2.getAttributeDate("Date Attribute");

        final DataCollection data3 = new DataCollection(this);
        data3.getAttributeInteger("Integer Attribute");

        final DataCollection data4 = new DataCollection(this);
        data4.getAttributeReal("Real Attribute");

        final DataCollection data5 = new DataCollection(this);
        data5.getAttributeReal("Simple String Attribute");

        final DataCollection data6 = new DataCollection(this);
        data5.getAttributeString("Test")
                .addRange(new AttributeStringData.Range("=", ""))
                .addRange(new AttributeStringData.Range("=", "\""))
                .addRange(new AttributeStringData.Range("=", "Test"))
                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", data5.getMQLProgram("Test Program")));

        final DataCollection data7 = new DataCollection(this);
        data7.getAttributeReal("Attribute with Quote \"")
                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", data7.getMQLProgram("Test Program\"")));

        return new Object[][]  {
                new Object[]{data1},
                new Object[]{data2},
                new Object[]{data3},
                new Object[]{data4},
                new Object[]{data5},
                new Object[]{data6},
                new Object[]{data7}
        };
    }

    /**
     * Cleanup all test attributes.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.DM_ATTRIBUTE_STRING);
        this.cleanup(CI.PRG_MQL_PROGRAM);
    }

    /**
     * Tests a new created attributes and the related export.
     *
     * @param _data     collection with data to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "attributes", description = "test export of new created attributes")
    public void testExport(final DataCollection _data)
        throws Exception
    {
        _data.create();

        for (final AbstractAttributeData<?> attribute : _data.getAttributes())  {
            // create and export
            final ExportParser exportParser = attribute.export();
            attribute.checkExport(exportParser);

            // make update with exported CI file and check result again
            this.update(attribute.getCIFileName(), exportParser.getOrigCode());
            final ExportParser exportParser2 = attribute.export();
            attribute.checkExport(exportParser2);
        }
    }


    /**
     * Tests an update of non existing attributes. The result is tested with by
     * exporting the command and checking the result.
     *
     * @param _data     collection with data to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "attributes", description = "test update of non existing attribute")
    public void testUpdate(final DataCollection _data)
        throws Exception
    {
        for (final AbstractProgramData<?> program : _data.getPrograms())  {
            program.create();
        }

        for (final AbstractAttributeData<?> attribute : _data.getAttributes())  {
            // update with non existing attribute
            this.update(attribute);

            // export and check
            final ExportParser exportParser = attribute.export();
            attribute.checkExport(exportParser);

            // update with exported file and check again
            this.update(attribute.getCIFileName(), exportParser.getOrigCode());
            final ExportParser exportParser2 = attribute.export();
            attribute.checkExport(exportParser2);
        }
    }
}
