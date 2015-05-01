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

package org.mxupdate.test.test.update.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.util.Version;

/**
 *
 * @author The MxUpdate Team
 * @param <ATTRIBUTEDATA>   attribute data class
 */
public abstract class AbstractAttributeWithRangesAndMultiValuesTest<ATTRIBUTEDATA extends AbstractAttributeData<?>>
    extends AbstractAttributeTest<ATTRIBUTEDATA>
{
    /**
     * Prepares the test data.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _value1   first possible range value
     * @param _value2   second possible range value
     * @param _datas    specific test data to append
     * @return prepared test data
     */
    @Override()
    protected Object[][] prepareData(final String _logText,
                                     final String _value1,
                                     final String _value2,
                                     final Object[]... _datas)
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        if (_logText != null)  {
            // ranges
            ret.add(new Object[]{
                    _logText + " with = range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeEqual(_value1))});
            ret.add(new Object[]{
                    _logText + " with != range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeNotEqual(_value1))});
            ret.add(new Object[]{
                    _logText + " with < range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeLessThan(_value1))});
            ret.add(new Object[]{
                    _logText + " with > range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeGreaterThan(_value1))});
            ret.add(new Object[]{
                    _logText + " with <= range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeLessEqualThan(_value1))});
            ret.add(new Object[]{
                    _logText + " with >= range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeGreaterEqualThan(_value1))});
            ret.add(new Object[]{
                    _logText + " with smatch range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeSMatch(_value1))});
            ret.add(new Object[]{
                    _logText + " with !smatch range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeNotSMatch(_value1))});
            ret.add(new Object[]{
                    _logText + " with match range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeMatch(_value1))});
            ret.add(new Object[]{
                    _logText + " with !match range",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeNotMatch(_value1))});
            ret.add(new Object[]{
                    _logText + " with program range w/o input",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeProgram(new MQLProgramData(this, "program \" test"), null))});
            ret.add(new Object[]{
                    _logText + " with program range with empty input (mut be removed)",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeProgram(new MQLProgramData(this, "program \" test"), "")),
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeProgram(new MQLProgramData(this, "program \" test"), null))});
            ret.add(new Object[]{
                    _logText + " with program range with input",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeProgram(new MQLProgramData(this, "program \" test"), "test \" input"))});
            ret.add(new Object[]{
                    _logText + " with between range inclusive",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeBetween(_value1, true, _value2, true))});
            ret.add(new Object[]{
                    _logText + " with between range exclusive",
                    this.createNewData("hello")
                            .addRange(new AbstractAttributeData.RangeBetween(_value1, false, _value2, false))});

            // multi value flags
            ret.add(new Object[]{
                    _logText + " with multi value flag true",
                    this.createNewData("hello")
                            .setFlag("multivalue", true)
                            .notSupported(Version.V6R2011x)});
            ret.add(new Object[]{
                    _logText + " with multi value flag false",
                    this.createNewData("hello")
                            .setFlag("multivalue", false)
                            .notSupported(Version.V6R2011x)});
            ret.add(new Object[]{
                    _logText + " without multi value flag (to test default value false in export)",
                    this.createNewData("hello")
                            .notSupported(Version.V6R2011x),
                    this.createNewData("hello")
                            .setFlag("multivalue", false)});
        }

        ret.addAll(Arrays.asList(_datas));

        return super.prepareData(_logText,_value1, _value2, ret.toArray(new Object[ret.size()][]));
    }
}
