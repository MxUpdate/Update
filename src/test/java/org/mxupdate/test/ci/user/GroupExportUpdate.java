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

package org.mxupdate.test.ci.user;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.other.SiteData;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.workspace.CueData;
import org.mxupdate.test.data.user.workspace.FilterData;
import org.mxupdate.test.data.user.workspace.QueryData;
import org.mxupdate.test.data.user.workspace.TableData;
import org.mxupdate.test.data.user.workspace.TipData;
import org.mxupdate.test.data.user.workspace.ToolSetData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of groups.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class GroupExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test groups.
     *
     * @return object array with all test groups
     */
    @DataProvider(name = "groups")
    public Object[][] getGroups()
    {
        return new Object[][]  {
                new Object[]{
                        "simple group",
                        new GroupData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "hidden group",
                        new GroupData(this, "hallo \" test")
                                .setHidden(true)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "group with two parent groups",
                        new GroupData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParent(new GroupData(this, "hallo parent1 \" test"))
                                .assignParent(new GroupData(this, "hallo parent2 \" test"))},
                new Object[]{
                        "group with assigned site",
                        new GroupData(this, "hallo \" test")
                                .setSite(new SiteData(this, "Test \" Site"))},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "group with two cues",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"").getUser()
                                .newCue("cue b").getUser()},
                new Object[]{
                        "group with complex cue",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("appliesto", "businessobject")
                                        .setValue("type", "TYPE \"")
                                        .setValue("name", "NAME \"")
                                        .setValue("revision", "revision \"")
                                        .setValue("vault", "vault \"")
                                        .setValue("owner", "owner \"")
                                        .setValue("color", "black")
                                        .setValue("highlight", "red")
                                        .setValue("font", "arial")
                                        .setValue("linestyle", "dashed")
                                        .setValue("where", "LL = \"attribute[hallo]\"")
                                        .getUser()},
                new Object[]{
                        "group with cue which applies to relationship",
                        new GroupData(this, "hallo \" test")
                            .newCue("my cue \"test\"")
                                .setValue("appliesto", "relationship")
                                .getUser()},
                new Object[]{
                        "group with cue which applies to both business object and relationship",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("appliesto", "all")
                                        .getUser()},
                new Object[]{
                        "group with cue with order -1",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "-1")
                                        .getUser()},
                new Object[]{
                        "group with cue with order 0",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "0")
                                        .getUser()},
                new Object[]{
                        "group with cue with order 1",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "1")
                                        .getUser()},
                new Object[]{
                        "group with cue with visible user definition",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "group with not active cue",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "group with active cue",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "group with not hidden cue",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "group with hidden cue",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "group with cue with property name",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "group with cue with property name and value",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "group with cue with property name, value and referenced admin object",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new GroupData(this, "")))
                                        .getUser()},
                new Object[]{
                        "group with cue with multiple properties",
                        new GroupData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new GroupData(this, "property \" group")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "group with two filters",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"").getUser()
                                .newFilter("filter b").getUser()},
                new Object[]{
                        "group with complex filter",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setValue("appliesto", "businessobject")
                                        .setValue("type", "TYPE \"")
                                        .setValue("name", "NAME \"")
                                        .setValue("revision", "revision \"")
                                        .setValue("vault", "vault \"")
                                        .setValue("owner", "owner \"")
                                        .setValue("where", "LL = \"attribute[hallo]\"")
                                        .getUser()},
                new Object[]{
                        "group with filter which applies to relationship",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setValue("appliesto", "relationship")
                                        .getUser()},
                new Object[]{
                        "group with filter with visible user definition",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "group with not active filter",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "group with active filter",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "group with not hidden filter",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "group with hidden filter",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "group with filter in the to direction",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.TO)
                                        .getUser()},
                new Object[]{
                        "group with filter in the from direction",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.FROM)
                                        .getUser()},
                new Object[]{
                        "group with filter in the both direction",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.BOTH)
                                        .getUser()},
                new Object[]{
                        "group with filter with property name",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "group with filter with property name and value",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "group with filter with property name, value and referenced admin object",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new GroupData(this, "")))
                                        .getUser()},
                new Object[]{
                        "group with filter with multiple properties",
                        new GroupData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new GroupData(this, "property \" group")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "group with two queries",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"").getUser()
                                .newQuery("query b").getUser()},
                new Object[]{
                        "group with complex query",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setValue("type", "TYPE \"")
                                        .setValue("name", "NAME \"")
                                        .setValue("revision", "revision \"")
                                        .setValue("vault", "vault \"")
                                        .setValue("owner", "owner \"")
                                        .setValue("where", "LL = \"attribute[hallo]\"")
                                        .getUser()},
                new Object[]{
                        "group with query with visible user definition",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "group with not hidden query",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "group with hidden query",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "group with query with expand type true",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setExpandType(true)
                                        .getUser()},
                new Object[]{
                        "group with query with expand type false",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setExpandType(false)
                                        .getUser()},
                new Object[]{
                        "group with query with property name",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "group with query with property name and value",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "group with query with property name, value and referenced admin object",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new GroupData(this, "")))
                                        .getUser()},
                new Object[]{
                        "group with query with multiple properties",
                        new GroupData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new GroupData(this, "property \" group")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "group with two tables",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"").getUser()
                                .newTable("table b").setActive(false).getUser()},
                new Object[]{
                        "group with complex table",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .newField(null)
                                                .setValue("businessobject", "name")
                                                .setValue("href", "http://www.mxupdate.org")
                                                .setValue("alt", "alt \" value")
                                                .setValue("range", "a \"range href\"")
                                                .setValue("update", "an \"update href\"")
                                                .setValue("program", "MxUpdate")
                                                .setHidden(true)
                                                .getTable()
                                        .newField(null)
                                                .setValue("relationship", "type")
                                                .getTable()
                                        .newField("scale 50")               .setScale(50.0).getTable()
                                        .newField("scale 50.5")             .setScale(50.5).getTable()
                                        .newField("no size")                .setSize(null, null).getTable()
                                        .newField("size 1 1")               .setSize(1.0, 1.0).getTable()
                                        .newField("size 0.5 0.5")           .setSize(0.5, 0.5).getTable()
                                        .newField("size 100 59")            .setSize(100.0, 59.0).getTable()
                                        .newField("no minimum size")        .setMinSize(null, null).getTable()
                                        .newField("minimum size 0 0")       .setMinSize(0.0, 0.0).getTable()
                                        .newField("minimum size 0.5 0.5")   .setMinSize(0.5, 0.5).getTable()
                                        .newField("minimum size 100 59")      .setMinSize(100.0, 59.0).getTable()
                                        .newField("default auto height")    .setAutoHeight(null).getTable()
                                        .newField("auto height true")       .setAutoHeight(true).getTable()
                                        .newField("auto height false")      .setAutoHeight(false).getTable()
                                        .newField("default auto width")     .setAutoWidth(null).getTable()
                                        .newField("auto width true")        .setAutoWidth(true).getTable()
                                        .newField("auto width false")       .setAutoWidth(false).getTable()
                                        .newField("default (none) sorttype").setSortType(null).getTable()
                                        .newField("sorttype alpha")         .setSortType("alpha").getTable()
                                        .newField("sorttype numeric")       .setSortType("numeric").getTable()
                                        .newField("sorttype other")         .setSortType("other").getTable()
                                        .newField("sorttype none")          .setSortType("none").getTable()
                                        .newField("default not editable")   .setEditable(null).getTable()
                                        .newField("editable ")              .setEditable(true).getTable()
                                        .newField("not editable")           .setEditable(false).getTable()
                                        .getUser()},
                new Object[]{
                        "group with table with visible user definition",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "group with not active table",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "group with active table",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "group with table with property name",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "group with table with property name and value",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "group with table with property name, value and referenced admin object",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new GroupData(this, "")))
                                        .getUser()},
                new Object[]{
                        "group with table with multiple properties",
                        new GroupData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new GroupData(this, "property \" group")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "group with two tips",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"").getUser()
                                .newTip("tip b").getUser()},
                new Object[]{
                        "group with complex tip",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("appliesto", "businessobject")
                                        .setValue("type", "TYPE \"")
                                        .setValue("name", "NAME \"")
                                        .setValue("revision", "revision \"")
                                        .setValue("vault", "vault \"")
                                        .setValue("owner", "owner \"")
                                        .setValue("where", "LL = \"attribute[hallo]\"")
                                        .getUser()},
                new Object[]{
                        "group with tip which applies to relationship",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("appliesto", "relationship")
                                        .getUser()},
                new Object[]{
                        "group with tip with visible user definition",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "group with not active tip",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "group with active tip",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "group with not hidden tip",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "group with hidden tip",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "group with tip with expression",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("expression", "this is a \"expression\" with {braces}")
                                        .getUser()},
                new Object[]{
                        "group with tip with property name",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "group with tip with property name and value",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "group with tip with property name, value and referenced admin object",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new GroupData(this, "")))
                                        .getUser()},
                new Object[]{
                        "group with tip with multiple properties",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new GroupData(this, "property \" group")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "group with two tool sets",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"").getUser()
                                .newToolSet("tool set b").getUser()},
                new Object[]{
                        "group with tool set with two programs",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProgram(new MQLProgramData(this, "\"tool set\" program 1"))
                                        .addProgram(new MQLProgramData(this, "\"tool set\" program 2"))
                                        .getUser()},
                new Object[]{
                        "group with tool set with visible user definition",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "group with not active tool set",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "group with active tool set",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "group with not hidden tool set",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "group with hidden tool set",
                        new GroupData(this, "hallo \" test")
                                .newTip("my tool set \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "group with tool set with property name",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "group with tool set with property name and value",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "group with tool set with property name, value and referenced admin object",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new GroupData(this, "")))
                                        .getUser()},
                new Object[]{
                        "group with tool set with multiple properties",
                        new GroupData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new GroupData(this, "property \" group")))
                                        .getUser()},
        };
    }


    /**
     * Cleanup all test commands.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.GROUP);
        this.cleanup(AbstractTest.CI.SITE);
        this.cleanup(AbstractTest.CI.MQL_PROGRAM);
    }

    /**
     * Tests a new created group and the related export.
     *
     * @param _description  description of the test case
     * @param _group        group to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "groups", description = "test export of new created group")
    public void simpleExport(final String _description,
                             final GroupData _group)
        throws Exception
    {
        _group.create();
        _group.checkExport(_group.export());
    }

    /**
     * Tests an update of non existing group. The result is tested with by
     * exporting the group and checking the result.
     *
     * @param _description  description of the test case
     * @param _group        group to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "groups", description = "test update of non existing group")
    public void simpleUpdate(final String _description,
                             final GroupData _group)
        throws Exception
    {
        // create all parent groups
        for (final GroupData parent : _group.getParent())  {
            parent.create();
        }

        // create site
        if (_group.getSite() != null)  {
            _group.getSite().create();
        }

        // create cue properties
        for (final CueData<GroupData> cue : _group.getCues())  {
            for (final PropertyDef prop : cue.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create filter properties
        for (final FilterData<GroupData> filter : _group.getFilters())  {
            for (final PropertyDef prop : filter.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create query properties
        for (final QueryData<GroupData> query : _group.getQueries())  {
            for (final PropertyDef prop : query.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create table properties
        for (final TableData<GroupData> table : _group.getTables())  {
            for (final PropertyDef prop : table.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create tip properties
        for (final TipData<GroupData> tip : _group.getTips())  {
            for (final PropertyDef prop : tip.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create tool set properties and programs
        for (final ToolSetData<GroupData> toolSet : _group.getToolSets())  {
            for (final PropertyDef prop : toolSet.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
            for (final AbstractProgramData<?> prog : toolSet.getPrograms())  {
                prog.create();
            }
        }

        // first update with original content
        this.update(_group.getCIFileName(), _group.ciFile());
        final ExportParser exportParser = _group.export();
        _group.checkExport(exportParser);

        // second update with delivered content
        this.update(_group.getCIFileName(), exportParser.getOrigCode());
        _group.checkExport(_group.export());
    }

    /**
     * Test an update of a group where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing group with site by removing site")
    public void removeExistingSite()
        throws Exception
    {
        final GroupData group = new GroupData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        group.create();
        group.setSite(null);
        this.update(group.getCIFileName(), group.ciFile());

        Assert.assertEquals(this.mql("escape print group \""
                                    + AbstractTest.convertMql(group.getName())
                                    + "\" select site dump"),
                            "",
                            "check that no site is defined");
    }
}
