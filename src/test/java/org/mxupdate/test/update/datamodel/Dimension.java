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

package org.mxupdate.test.update.datamodel;

import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test cases related to updates of dimensions.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test
public class Dimension
    extends AbstractTest
{
    /**
     * Name of the test dimension.
     */
    private static final String DIMENSION_NAME = "MxUpdate_Test";

    /**
     * Update code of the test dimension.
     */
    private static final String DIMENSION_CODE =
              "################################################################################\n"
            + "# DIMENSION:\n"
            + "# ~~~~~~~~~~\n"
            + "# " + Dimension.DIMENSION_NAME + "\n"
            + "#\n"
            + "# SYMBOLIC NAME:\n"
            + "# ~~~~~~~~~~~~~~\n"
            + "# dimension_" + Dimension.DIMENSION_NAME + "\n"
            + "#\n"
            + "# DESCRIPTION:\n"
            + "# ~~~~~~~~~~~~\n"
            + "#\n"
            + "#\n"
            + "# AUTHOR:\n"
            + "# ~~~~~~~\n"
            + "#\n"
            + "#\n"
            + "################################################################################\n"
            + "\n"
            + "updateDimension \"${NAME}\" {\n"
            + "  description \"a simple description\"\n"
            + "  hidden \"true\"\n"
            + "  unit \"name1\" {\n"
            + "    default true\n"
            + "    description \"description 1\"\n"
            + "    label \"label1\"\n"
            + "    multiplier 1.0\n"
            + "    offset 0.0\n"
            + "    setting \"SettingKey2\" \"SettingValue2\"\n"
            + "    setting \"to\" \"SettingValue\"\n"
            + "    property \"Zest\" value \"Zest\"\n"
            + "    property \"_test3\" value \"Test3\"\n"
            + "    property \"abc\" value \"abc\"\n"
            + "    property \"abc2\" value \"abc2\"\n"
            + "    property \"test2\" value \"Test2\"\n"
            + "    property \"test\" to type \"Part\" value \"Test\"\n"
            + "  }\n"
            + "  unit \"name2\" {\n"
            + "    description \"description 2\"\n"
            + "    label \"label2\"\n"
            + "    multiplier 10.0\n"
            + "    offset 20.0\n"
            + "    system \"Duration Units\" to unit \"name2\"\n"
            + "  }"
            + "  unit \"name 3\" {\n"
            + "    description \"description3\"\n"
            + "    label \"label 3\"\n"
            + "    multiplier 30.0\n"
            + "    offset 40.0\n"
            + "    system \"Duration Units\" to unit \"name2\"\n"
            + "    system \"Test\" to unit \"name 3\"\n"
            + "  }"
            + "  unit \"name 4\" {\n"
            + "    description \"description4 with negative multiplier and negative offset\"\n"
            + "    label \"label 4\"\n"
            + "    multiplier -1.0\n"
            + "    offset -50.0\n"
            + "  }"
            + "}";

    /**
     * Makes an update for given <code>_code</code>.
     *
     * @param _fileName     name of the file to update
     * @param _code         TCL update code
     * @param _params       parameters
     * @return values from the called dispatcher
     * @throws Exception  if update failed
     */
    @Deprecated()
    public Map<?,?> update(final String _fileName,
                           final String _code,
                           final String... _params)
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put(_fileName, _code);
        final Map<String,String> params = new HashMap<String,String>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }
        final Map<?,?> bck = this.executeEncoded("Update", params, "FileContents", files);

        return bck;
    }

    /**
     * Removes the MxUpdate test dimension {@link #DIMENSION_NAME}.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterMethod
    public void cleanup()
        throws Exception
    {
        if (!"".equals(this.mql("list dimension " + Dimension.DIMENSION_NAME)))  {
            this.mql("delete dimension " + Dimension.DIMENSION_NAME);
        }
    }

    /**
     * Tests that the test dimension {@link #DIMENSION_NAME} is created with
     * TCL update code {@link #DIMENSION_CODE}.
     *
     * @throws Exception if create of the new dimension failed
     */
    public void testCreateNewDimension()
        throws Exception
    {
        this.updateDim();
        this.checkAll();
    }

    /**
     * Tests the update for the test dimension {@link #DIMENSION_NAME} with TCL
     * update code {@link #DIMENSION_CODE}.
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionDefaultUnitName1()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "default "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        this.updateDim();
        this.checkAll();
    }

    /**
     * The test dimension {@link #DIMENSION_NAME} is created with unit
     * &quot;name1&quot; which is not the default unit and then updated.
     * Because the parameter that update of default dimension is allowed is
     * set, the test must not fail!
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionUnitName1()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "multiplier 1.0 "
                        + "offset 0.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        this.updateDim("DMDimAllowUpdateDefUnit", "true");
        this.checkAll();
    }

    /**
     * The test dimension {@link #DIMENSION_NAME} is created with unit
     * &quot;name1&quot; which is not the default unit and then updated which
     * must fail.
     *
     * @throws Exception if test failed
     */
    public void testUpdateFailedDimensionUnitName1()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "multiplier 1.0 "
                        + "offset 0.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        final Map<?,?> bck = this.updateDim();
        final Exception ex = (Exception) bck.get("exception");

        Assert.assertNotNull(ex, "check that changing default unit is not allowed");
        Assert.assertTrue(ex.getMessage().indexOf("UpdateError #10603:") >= 0,
                          "check that an error for modify default dimension unit was thrown");
    }

    /**
     * Test that for dimension with a unit the multiplier could be changed (by
     * defining a parameter).
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionDefaultUnitName1a()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "multiplier 2.0 "
                        + "offset 0.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        this.updateDim("DMDimAllowUpdateDefUnit", "true",
                       "DMDimAllowUpdateUnitMult", "true");
        this.checkAll();
    }

    /**
     * Test that for dimension with a unit the multiplier could not be changed.
     *
     * @throws Exception if test dimension could not be created
     */
    public void testUpdateFailedDimensionDefaultUnitName1a()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "multiplier 2.0 "
                        + "offset 0.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        final Map<?,?> bck = this.updateDim();
        final Exception ex = (Exception) bck.get("exception");

        Assert.assertNotNull(ex, "check that changing dimension unit multiplier is not allowed");
        Assert.assertTrue(ex.getMessage().indexOf("UpdateError #10601:") >= 0,
                          "check that an error for modify dimension unit multiplier was thrown");
    }

    /**
     * Test that for dimension with a unit the offset could be changed (by
     * defining a parameter).
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionDefaultUnitName1b()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "multiplier 1.0 "
                        + "offset 1.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        this.updateDim("DMDimAllowUpdateDefUnit", "true",
                       "DMDimAllowUpdateUnitOffs", "true");
        this.checkAll();
    }

    /**
     * Test that for dimension with a unit the offset could not be changed.
     *
     * @throws Exception if test dimension could not be created
     */
    public void testUpdateFailedDimensionDefaultUnitName1b()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                        + "multiplier 1.0 "
                        + "offset 1.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2;");

        final Map<?,?> bck = this.updateDim();
        final Exception ex = (Exception) bck.get("exception");

        Assert.assertNotNull(ex, "check that changing dimension unit offset is not allowed");
        Assert.assertTrue(ex.getMessage().indexOf("UpdateError #10602:") >= 0,
                          "check that an error for modify dimension unit offset was thrown");
    }

    /**
     * Update test for a dimension with existing of non default unit
     * &quot;name2&quot; having some settings and properties.
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionNotDefaultUnitName2()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name2 "
                        + "multiplier 10.0 "
                        + "offset 20.0 "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2 "
                        + "property a value b;");

        this.updateDim();
        this.checkAll();
    }

    /**
     * Test for changing the default unit from &quot;name2&quot; to
     * &quot;name1&quot; which must be failed.
     *
     * @throws Exception if the dimension to update could not be added
     *                   (created)
     */
    public void testUpdateFailedDimensionDefaultUnitName2()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit name1 "
                + " unit name2 "
                        + "default "
                        + "setting to abc "
                        + "setting remove1 removeValue "
                        + "setting \"remove 2\" removeValue2 "
                        + "property a value b;");

        final Map<?,?> bck = this.updateDim("DMDimAllowUpdateUnitMult", "true");
        final Exception ex = (Exception) bck.get("exception");

        Assert.assertNotNull(ex, "check that changing default unit is not allowed");
        Assert.assertTrue(ex.getMessage().indexOf("UpdateError #10603:") >= 0,
                          "check that a change default unit error was thrown");
    }

    /**
     * Test to update existing dimension {@link #DIMENSION_NAME} with existing
     * system name (which must be updated).
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionNotDefaultUnitName3()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME + " "
                        + "unit 'name 3' "
                                + "multiplier 30.0 "
                                + "offset 40.0 "
                                + "setting to abc "
                                + "setting remove1 removeValue "
                                + "setting \"remove 2\" removeValue2;"
                + "mod dimension " + Dimension.DIMENSION_NAME + " "
                + "modify unit 'name 3' system a to unit 'name 3' "
        + "modify unit 'name 3' system b to unit 'name 3';");

        this.updateDim();
        this.checkAll();
    }

    /**
     * Tests to update dimension {@link #DIMENSION_NAME} which has a unit which
     * is not defined in the TCL update code {@link #DIMENSION_CODE}.
     *
     * @throws Exception if test failed
     */
    public void testUpdateDimensionRemovingUnit()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit removeUnit ");

        this.updateDim("DMDimAllowRemoveUnit", "true");
        this.checkAll();
    }

    /**
     * Failed tests to update dimension {@link #DIMENSION_NAME} which has a
     * unit which is not defined in the TCL update code
     * {@link #DIMENSION_CODE}.
     *
     * @throws Exception if dimension could not be created
     */
    public void testUpdateFailedDimensionRemovingUnit()
        throws Exception
    {
        this.mql("add dimension " + Dimension.DIMENSION_NAME
                + " unit removeUnit ");

        final Map<?,?> bck = this.updateDim();
        final Exception ex = (Exception) bck.get("exception");

        Assert.assertNotNull(ex, "check that a unit to remove is not allowed");
        Assert.assertTrue(ex.getMessage().indexOf("UpdateError #10604:") >= 0,
                          "check that an error for removing an unit was thrown");
    }

    /**
     * Creates / updates the dimension {@link #DIMENSION_NAME} with the update
     * code {@link #DIMENSION_CODE}.
     *
     * @param _params   predefined parameters for the update
     * @return values from the called dispatcher
     * @throws Exception if create / update of the test dimension failed
     * @see #DIMENSION_CODE
     */
    private Map<?,?> updateDim(final String... _params)
        throws Exception
    {
        return this.update("DIMENSION_" + Dimension.DIMENSION_NAME + ".tcl",
                           Dimension.DIMENSION_CODE,
                           _params);
    }

    /**
     * Checks that the new created or updated dimension {@link #DIMENSION_NAME}
     * is equal to the defined update code {@link #DIMENSION_CODE}.
     *
     * @throws MatrixException if MQL select statements failed
     */
    private void checkAll()
        throws MatrixException
    {
        Assert.assertTrue(!"".equals(this.mql("list dimension " + Dimension.DIMENSION_NAME)),
                          "dimension was not created!");
        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                    + " select description dump"),
                            "a simple description",
                            "Check that description is set");
        Assert.assertEquals(this.mql("list property to dimension " + Dimension.DIMENSION_NAME),
                            "dimension_" + Dimension.DIMENSION_NAME
                                    + " on program eServiceSchemaVariableMapping.tcl to dimension "
                                    + Dimension.DIMENSION_NAME,
                            "Check for symbolic name registration.");

        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                            + " select unit dump @")
                                    .replaceAll("name1", "")
                                    .replaceAll("name2", "")
                                    .replaceAll("name 3", "")
                                    .replaceAll("name 4", ""),
                            "@@@",
                            "check that exact four units are defined");

        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                    + " select unit[name1] dump"),
                            "TRUE",
                            "check that unit 'name1' exists");
        this.checkUnit("name1", "default", "TRUE");
        this.checkUnit("name1", "label", "label1");
        this.checkUnit("name1", "description", "description 1");
        this.checkUnit("name1", "multiplier", "1.0");
        this.checkUnit("name1", "offset", "0.0");
        this.checkUnit("name1", "setting[SettingKey2]", "TRUE");
        this.checkUnit("name1", "setting[SettingKey2].value", "SettingValue2");
        this.checkUnit("name1", "setting[to]", "TRUE");
        this.checkUnit("name1", "setting[to].value", "SettingValue");
        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                            + " select unit[name1].setting dump @")
                                    .replaceAll("to", "")
                                    .replaceAll("SettingKey2", ""),
                            "@",
                            "Check that only two settings are defined");
        this.checkUnit("name1", "property[Zest].value", "Zest");
        this.checkUnit("name1", "property[Zest].to", "");
        this.checkUnit("name1", "property[_test3].value", "Test3");
        this.checkUnit("name1", "property[_test3].to", "");
        this.checkUnit("name1", "property[abc].value", "abc");
        this.checkUnit("name1", "property[abc].to", "");
        this.checkUnit("name1", "property[abc2].value", "abc2");
        this.checkUnit("name1", "property[abc2].to", "");
        this.checkUnit("name1", "property[test2].value", "Test2");
        this.checkUnit("name1", "property[test2].to", "");
        this.checkUnit("name1", "property[test].value", "Test");
        this.checkUnit("name1", "property[test].to", "type Part");
        this.checkUnit("name1", "systemunit", "");

        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                    + " select unit[name2] dump"),
                            "TRUE",
                            "Check that unit 'name2' exists");
        this.checkUnit("name2", "default", "FALSE");
        this.checkUnit("name2", "label", "label2");
        this.checkUnit("name2", "description", "description 2");
        this.checkUnit("name2", "multiplier", "10.0");
        this.checkUnit("name2", "offset", "20.0");
        this.checkUnit("name2", "property", "");
        this.checkUnit("name2", "systemunit", "name2");
        this.checkUnit("name2", "systemunit[Duration Units]", "name2");

        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                    + " select unit[name 3] dump"),
                            "TRUE",
                            "Check that unit 'name 3' exists");
        this.checkUnit("name 3", "default", "FALSE");
        this.checkUnit("name 3", "label", "label 3");
        this.checkUnit("name 3", "description", "description3");
        this.checkUnit("name 3", "multiplier", "30.0");
        this.checkUnit("name 3", "offset", "40.0");
        this.checkUnit("name 3", "property", "");
        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                            + " select 'unit[name 3].systemunit[]' dump @")
                                    .replaceAll("name2", "")
                                    .replaceAll("name 3", ""),
                            "@",
                            "Check that exact two system infos are defined");
        this.checkUnit("name 3", "systemunit[Duration Units]", "name2");
        this.checkUnit("name 3", "systemunit[Test]", "name 3");

        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                + " select unit[name 4] dump"),
        "TRUE",
        "Check that unit 'name 4' exists");
        this.checkUnit("name 4", "default", "FALSE");
        this.checkUnit("name 4", "label", "label 4");
        this.checkUnit("name 4", "description", "description4 with negative multiplier and negative offset");
        this.checkUnit("name 4", "multiplier", "-1.0");
        this.checkUnit("name 4", "offset", "-50.0");
        this.checkUnit("name 4", "property", "");
        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                            + " select 'unit[name 4].systemunit[]' dump @"),
                            "",
                            "Check that no system infos are defined for unit 4");
    }

    /**
     * Selects for dimension {@link #DIMENSION_NAME} and <code>_unit</code> the
     * <code>_select</code> and compares the result against
     * <code>_target</code>. The check is done via {@link Assert}.
     *
     * @param _unit     name of the unit for which information must be fetched
     * @param _select   select statement of the <code>_unit</code> for which
     *                  information must be fetched
     * @param _target   estimated value of the fetched information
     * @throws MatrixException if the values for the <code>_select</code> of
     *                         the <code>_unit</code> could not fetched
     */
    private void checkUnit(final String _unit,
                           final String _select,
                           final String _target)
        throws MatrixException
    {
        Assert.assertEquals(this.mql("print dimension " + Dimension.DIMENSION_NAME
                                    + " select 'unit[" + _unit + "]." + _select + "' dump"),
                            _target,
                            "Check for unit '" + _unit + "' the " + _select + ".");

    }
}
