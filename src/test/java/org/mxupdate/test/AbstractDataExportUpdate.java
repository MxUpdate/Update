/*
 * Copyright 2008-2011 The MxUpdate Team
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

package org.mxupdate.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.annotations.Test;

/**
 * Test cases for the export / update of abstract data.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <DATA> class of the data
 */
public abstract class AbstractDataExportUpdate<DATA extends AbstractAdminData<?>>
    extends AbstractTest
{
    /**
     * Prepares the test data.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _datas    specific test data to append
     * @return prepared test data
     */
    protected Object[][] prepareData(final String _logText,
                                     final Object[]... _datas)
    {
        final List<Object[]> ret = new ArrayList<Object[]>();
        ret.add(new Object[]{
                _logText + " with property name",
                this.createNewData("hello \" test")
                                .addProperty(new PropertyDef("my test \"property\""))});
        ret.add(new Object[]{
                _logText + " property name and value",
                this.createNewData("hello \" test")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))});
        ret.add(new Object[]{
                _logText + " property name, value and referenced admin object",
                this.createNewData("hello \" test")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" admin " + _logText)))});
        ret.add(new Object[]{
                _logText + " with multiple properties",
                this.createNewData("hello \" test")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" admin " + _logText)))});
        // hidden flag
        ret.add(new Object[]{
                _logText + " with hidden flag true",
                this.createNewData("hello \" test")
                        .setFlag("hidden", true)});
        ret.add(new Object[]{
                _logText + " with hidden flag false",
                this.createNewData("hello \" test")
                        .setFlag("hidden", false)});
        ret.add(new Object[]{
                _logText + " without hidden flag",
                this.createNewData("hello \" test")
                        .setFlag("hidden", null)});

        ret.addAll(Arrays.asList(_datas));
        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * Creates for given <code>_name</code> related data instance.
     *
     * @param _name     name of the user
     * @return new create data instance
     */
    protected abstract DATA createNewData(final String _name);

    /**
     * Tests a new created data and the related export.
     *
     * @param _description  description of the test case
     * @param _data    data to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data",
          description = "test export of new created test data object")
    public void testExport(final String _description,
                           final DATA _data)
        throws Exception
    {
        _data.create()
             .checkExport();
    }

    /**
     * Tests an update of non existing data. The result is tested with by
     * exporting the data and checking the result.
     *
     * @param _description  description of the test case
     * @param _data         data to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data",
          description = "test update of non existing data")
    public void testUpdateWithExport(final String _description,
                                     final DATA _data)
        throws Exception
    {
        _data.createDependings();

        // first update with original content
        _data.update();
        final ExportParser exportParser = _data.export();
        _data.checkExport(exportParser);

        // second update with delivered content
        _data.updateWithCode(exportParser.getOrigCode())
             .checkExport();
    }

    /**
     * Test update of existing data that all parameters are cleaned.
     *
     * @param _description  description of the test case
     * @param _data         data to test
     * @throws Exception if test failed
     */
    @SuppressWarnings("unchecked")
    @Test(dataProvider = "data",
          description = "test update of existing data instance for cleaning")
    public void testUpdateWithClean(final String _description,
                                    final DATA _data)
        throws Exception
    {
        _data.createDependings();

        // first update with original content
        _data.update()
             .checkExport();

        // second update with delivered content
        final DATA newData = (DATA) this.createCleanNewData(_data).update();

        // define all required values to empty values so that they are checked
        for (final Map.Entry<String,String> value : _data.getRequiredExportValues().entrySet())  {
            newData.setValue(value.getKey(), value.getValue());
        }

        // check export
        newData.checkExport();
    }

    /**
     * Creates a clean data instance used to update an existing data instance.
     *
     * @param _original     original data instance
     * @return new data instance (where all original data is cleaned)
     */
    protected DATA createCleanNewData(final DATA _original)
    {
        return this.createNewData(_original.getName().substring(AbstractTest.PREFIX.length()));
    }
}
