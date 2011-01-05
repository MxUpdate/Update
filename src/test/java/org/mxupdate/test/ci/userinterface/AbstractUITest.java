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

package org.mxupdate.test.ci.userinterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Abstract user interface test class.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <DATA> class of the data
 */
abstract class AbstractUITest<DATA extends AbstractAdminData<?>>
    extends AbstractDataExportUpdate<DATA>
{
    /**
     * Prepares the test data.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _datas    specific test data to append
     * @return prepared test data
     */
    @Override()
    protected Object[][] prepareData(final String _logText,
                                     final Object[]... _datas)
    {
        final List<Object[]> ret = new ArrayList<Object[]>();
        ret.add(new Object[]{
                _logText + " without anything (to test required fields)",
                this.createNewData("hello \" test")});
        ret.add(new Object[]{
                _logText + " with other symbolic name",
                this.createNewData("hello \" test").setSymbolicName(_logText + "_Test")});
        ret.add(new Object[]{
                _logText + " with description",
                this.createNewData("hello \" test").setValue("description", "complex description \"test\"")});
        ret.add(new Object[]{
                _logText + " with default hidden flag",
                this.createNewData("hello \" test").setFlag("hidden", null)});
        ret.add(new Object[]{
                _logText + " with hidden flag false",
                this.createNewData("hello \" test").setFlag("hidden", false)});
        ret.add(new Object[]{
                _logText + " with hidden flag true",
                this.createNewData("hello \" test").setFlag("hidden", true)});

        ret.addAll(Arrays.asList(_datas));
        return super.prepareData(_logText, ret.toArray(new Object[ret.size()][]));
    }
}
