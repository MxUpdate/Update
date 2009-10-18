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

package org.mxupdate.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.util.PropertyDef;

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

}
