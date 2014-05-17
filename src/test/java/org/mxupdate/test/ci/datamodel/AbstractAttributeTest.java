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

package org.mxupdate.test.ci.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

/**
 * Test cases for the update and export of attributes.
 *
 * @author The MxUpdate Team
 * @param <ATTRIBUTEDATA>   attribute data class
 */
public abstract class AbstractAttributeTest<ATTRIBUTEDATA extends AbstractAttributeData<?>>
    extends AbstractDataExportUpdate<ATTRIBUTEDATA>
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
    protected Object[][] prepareData(final String _logText,
                                     final String _value1,
                                     final String _value2,
                                     final Object[]... _datas)
    {
        final List<Object[]> ret = new ArrayList<Object[]>();
        ret.add(new Object[]{
                _logText + " with name",
                this.createNewData("helloBlank")});
        ret.add(new Object[]{
                _logText + " with escaped name",
                this.createNewData("hello \" test")});

        // rule
        ret.add(new Object[]{
                _logText + " with rule",
                this.createNewData("hello")
                        .setRule(new RuleData(this, "Rule"))});

         // triggers
        ret.add(new Object[]{
                _logText + " with modify action trigger",
                this.createNewData("hello \" test")
                        .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test \" Program")))});
        ret.add(new Object[]{
                _logText + " with modify check trigger",
                this.createNewData("hello \" test")
                        .addTrigger(new AbstractDataWithTrigger.TriggerCheck("modify", new MQLProgramData(this, "Test \" Program")))});
        ret.add(new Object[]{
                _logText + " with modify override trigger",
                this.createNewData("hello \" test")
                        .addTrigger(new AbstractDataWithTrigger.TriggerOverride("modify", new MQLProgramData(this, "Test \" Program")))});
        ret.add(new Object[]{
                _logText + " with modify action, check and override trigger",
                this.createNewData("hello \" test")
                        .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test \" Program 1")))
                        .addTrigger(new AbstractDataWithTrigger.TriggerCheck("modify", new MQLProgramData(this, "Test \" Program 2")))
                        .addTrigger(new AbstractDataWithTrigger.TriggerOverride("modify", new MQLProgramData(this, "Test \" Program 3")))});

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

        // default value
        ret.add(new Object[]{
                _logText + " with default value",
                this.createNewData("hello")
                        .setValue("default", _value1)});

        // reset on ? flags
        ret.add(new Object[]{
                _logText + " with reset on flag true",
                this.createNewData("hello")
                        .setValue("default", _value1)
                        .setFlag("resetonclone", true)});
        ret.add(new Object[]{
                _logText + " with reset on flag false",
                this.createNewData("hello")
                        .setValue("default", _value1)
                        .setFlag("resetonclone", false)});
        ret.add(new Object[]{
                _logText + " with reset on revision true",
                this.createNewData("hello")
                        .setValue("default", _value1)
                        .setFlag("resetonrevision", true)});
        ret.add(new Object[]{
                _logText + " with reset on revision false",
                this.createNewData("hello")
                        .setValue("default", _value1)
                        .setFlag("resetonrevision", false)});

        ret.addAll(Arrays.asList(_datas));

        return super.prepareData(_logText, ret.toArray(new Object[ret.size()][]));
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
        this.cleanup(CI.DM_ATTRIBUTE_BOOLEAN);
        this.cleanup(CI.DM_ATTRIBUTE_DATE);
        this.cleanup(CI.DM_ATTRIBUTE_INTEGER);
        this.cleanup(CI.DM_ATTRIBUTE_REAL);
        this.cleanup(CI.DM_ATTRIBUTE_STRING);
        this.cleanup(CI.DM_RULE);
        this.cleanup(CI.PRG_MQL_PROGRAM);
    }
}
