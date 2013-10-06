/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.ci.integration;

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractBusData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Abstract class used for all IEF integration tests for export and update.
 *
 * @author The MxUpdate Team
 * @param <IEFCLASS>    IEF data class to test
 */
public abstract class AbstractIEFTest<IEFCLASS extends AbstractBusData<IEFCLASS>>
    extends AbstractTest
{
    /**
     * Makes a new instance of the IEF class.
     *
     * @param _subType      <i>true</i> if sub type should be used;
     *                      otherwise <i>false</i>
     * @param _name         name of the instance
     * @return new instance
     */
    protected abstract IEFCLASS createNewData(final boolean _subType,
                                              final String _name);

    /**
     * Data provider for test integration EBOM sync configuration objects.
     *
     * @return object array with all test objects
     * @throws MatrixException if information about the IEF type and his
     *                         attributes could not be fetched
     */
    @DataProvider(name = "busDatas")
    public Object[][] getData()
        throws MatrixException
    {
        final List<Object[]> ret = new ArrayList<Object[]>();
        final IEFCLASS tmp = this.createNewData(true, "HelloTest");

        ret.add(new Object[]{
                        "simple object",
                        this.createNewData(true, "HelloTest")});
        ret.add(new Object[]{
                        "simple object with description",
                        this.createNewData(true, "Hello \"Test\"")
                                .setDescription("a \"description\"")});
        ret.add(new Object[]{
                        "simple object with description and single apostrophe",
                        this.createNewData(true, "Hello \"Test\" 'with single apostrophe'")
                                .setDescription("a \"description\" with single 'apostrophe'")});
        // default type must be only done if sub types are allowed
        if (tmp.getType() != null)  {
            ret.add(new Object[]{
                            "simple object with default type",
                            this.createNewData(false, "HelloTest")});
        }

        // sample test data for all string attributes without ranges
        for (final String oneAttr : this.mql("print type \"" + tmp.getCI().getBusType() + "\" select attribute.name dump '\n'").split("\n"))  {
            if (!oneAttr.startsWith("MxUpdate")
                    && "string".equals(this.mql("print attr \"" + oneAttr + "\" select type dump"))
                    && this.mql("print attr \"" + oneAttr + "\" select range dump").isEmpty())  {

                ret.add(new Object[]{
                        "object with defined attribute '" + oneAttr + "'",
                        this.createNewData(false, "HelloTest")
                                .setValue(oneAttr, "complex \"data\" 'and single 'apostrophe'")});
            }
        }
        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * Tests a new created integration global configuration objects and the
     * related export.
     *
     * @param _description      description of the test case
     * @param _ief              IEF instance to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas",
          description = "test export of new created IEF EBOM sync configuration objects")
    public void testExport(final String _description,
                           final IEFCLASS _ief)
        throws Exception
    {
        if (_ief.getType() != null)  {
            _ief.getType().create();
        }
        _ief.create();
        _ief.checkExport(_ief.export());
    }

    /**
     * Tests an update of non existing table. The result is tested with by
     * exporting the table and checking the result.
     *
     * @param _description      description of the test case
     * @param _ief              IEF instance to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas",
          description = "test update of non existing IEF EBOM sync configurations")
    public void testUpdate(final String _description,
                           final IEFCLASS _ief)
        throws Exception
    {
        if (_ief.getType() != null)  {
            _ief.getType().create();
        }

        // first update with original content
        _ief.update();
        final ExportParser exportParser = _ief.export();
        _ief.checkExport(exportParser);

        // second update with delivered content
        _ief.updateWithCode(exportParser.getOrigCode())
            .checkExport();
    }

    /**
     * Test update of the integration global configuration object where the
     * file date is checked.
     *
     * @param _description      description of the test case
     * @param _ief              IEF instance to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas",
          description = "check if an update of a global object works while checking the file date")
    public void testUpdateWithCheckFileDate(final String _description,
                                            final IEFCLASS _ief)
        throws Exception
    {
        if (_ief.getType() != null)  {
            _ief.getType().create();
        }

        // first update with original content
        _ief.update("UpdateCheckFileDate", "true");
        final ExportParser exportParser = _ief.export();
        _ief.checkExport(exportParser);

        // second update with delivered content
        _ief.updateWithCode(exportParser.getOrigCode())
            .checkExport();
    }
}
