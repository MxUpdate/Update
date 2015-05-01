/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
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
 * @param <DATA> class of the data
 */
public abstract class AbstractUITest<DATA extends AbstractAdminData<?>>
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
        if (_logText != null)  {
            ret.add(new Object[]{
                    _logText + " without anything (to test required fields)",
                    this.createNewData("hello \" test")});
            ret.add(new Object[]{
                    _logText + " with other symbolic name",
                    this.createNewData("hello \" test").setSymbolicName(_logText + "_Test")});
            ret.add(new Object[]{
                    _logText + " with description",
                    this.createNewData("hello \" test").setValue("description", "complex description \"test\"")});
        }

        ret.addAll(Arrays.asList(_datas));
        return super.prepareData(_logText, ret.toArray(new Object[ret.size()][]));
    }
}
