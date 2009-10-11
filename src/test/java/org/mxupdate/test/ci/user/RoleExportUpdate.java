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
import org.mxupdate.test.data.user.RoleData;
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
 * Test cases for the export and update of roles.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RoleExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test roles.
     *
     * @return object array with all test roles
     */
    @DataProvider(name = "roles")
    public Object[][] getRoles()
    {
        return new Object[][]  {
                new Object[]{
                        "simple role",
                        new RoleData(this, "hallo \" test")
                            .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "hidden role",
                        new RoleData(this, "hallo \" test")
                                .setHidden(true)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "not hidden role",
                        new RoleData(this, "hallo \" test")
                                .setHidden(false)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "default hidden role",
                        new RoleData(this, "hallo \" test")
                                .setHidden(null)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "role with two parent roles",
                        new RoleData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")
                                .assignParent(new RoleData(this, "hallo parent1 \" test"))
                                .assignParent(new RoleData(this, "hallo parent2 \" test"))},
                new Object[]{
                        "role with assigned site",
                        new RoleData(this, "hallo \" test")
                                .setSite(new SiteData(this, "Test \" Site"))},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "role with two cues",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"").getUser()
                                .newCue("cue b").getUser()},
                new Object[]{
                        "role with complex cue",
                        new RoleData(this, "hallo \" test")
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
                        "role with cue which applies to relationship",
                        new RoleData(this, "hallo \" test")
                            .newCue("my cue \"test\"")
                                .setValue("appliesto", "relationship")
                                .getUser()},
                new Object[]{
                        "role with cue which applies to both business object and relationship",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("appliesto", "all")
                                        .getUser()},
                new Object[]{
                        "role with cue with order -1",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "-1")
                                        .getUser()},
                new Object[]{
                        "role with cue with order 0",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "0")
                                        .getUser()},
                new Object[]{
                        "role with cue with order 1",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "1")
                                        .getUser()},
                new Object[]{
                        "role with cue with visible user definition",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "role with not active cue",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "role with active cue",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "role with not hidden cue",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "role with hidden cue",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "role with cue with property name",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "role with cue with property name and value",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "role with cue with property name, value and referenced admin object",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new RoleData(this, "")))
                                        .getUser()},
                new Object[]{
                        "role with cue with multiple properties",
                        new RoleData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new RoleData(this, "property \" role")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "role with two filters",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"").getUser()
                                .newFilter("filter b").getUser()},
                new Object[]{
                        "role with complex filter",
                        new RoleData(this, "hallo \" test")
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
                        "role with filter which applies to relationship",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setValue("appliesto", "relationship")
                                        .getUser()},
                new Object[]{
                        "role with filter with visible user definition",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "role with not active filter",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "role with active filter",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "role with not hidden filter",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "role with hidden filter",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "role with filter in the to direction",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.TO)
                                        .getUser()},
                new Object[]{
                        "role with filter in the from direction",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.FROM)
                                        .getUser()},
                new Object[]{
                        "role with filter in the both direction",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.BOTH)
                                        .getUser()},
                new Object[]{
                        "role with filter with property name",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "role with filter with property name and value",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "role with filter with property name, value and referenced admin object",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new RoleData(this, "")))
                                        .getUser()},
                new Object[]{
                        "role with filter with multiple properties",
                        new RoleData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new RoleData(this, "property \" role")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "role with two queries",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"").getUser()
                                .newQuery("query b").getUser()},
                new Object[]{
                        "role with complex query",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setValue("type", "TYPE \"")
                                        .setValue("name", "NAME \"")
                                        .setValue("revision", "revision \"")
                                        .setValue("vault", "vault \"")
                                        .setValue("owner", "owner \"")
                                        .setValue("where", "LL = \"attribute[hallo]\"")
                                        .getUser()},
                new Object[]{
                        "role with query with visible user definition",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "role with not hidden query",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "role with hidden query",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "role with query with expand type true",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setExpandType(true)
                                        .getUser()},
                new Object[]{
                        "role with query with expand type false",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setExpandType(false)
                                        .getUser()},
                new Object[]{
                        "role with query with property name",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "role with query with property name and value",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "role with query with property name, value and referenced admin object",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new RoleData(this, "")))
                                        .getUser()},
                new Object[]{
                        "role with query with multiple properties",
                        new RoleData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new RoleData(this, "property \" role")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "role with two tables",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"").getUser()
                                .newTable("table b").setActive(false).getUser()},
                new Object[]{
                        "role with complex table",
                        new RoleData(this, "hallo \" test")
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
                        "role with table with visible user definition",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "role with not active table",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "role with active table",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "role with table with property name",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "role with table with property name and value",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "role with table with property name, value and referenced admin object",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new RoleData(this, "")))
                                        .getUser()},
                new Object[]{
                        "role with table with multiple properties",
                        new RoleData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new RoleData(this, "property \" role")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "role with two tips",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"").getUser()
                                .newTip("tip b").getUser()},
                new Object[]{
                        "role with complex tip",
                        new RoleData(this, "hallo \" test")
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
                        "role with tip which applies to relationship",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("appliesto", "relationship")
                                        .getUser()},
                new Object[]{
                        "role with tip with visible user definition",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "role with not active tip",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "role with active tip",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "role with not hidden tip",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "role with hidden tip",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "role with tip with expression",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("expression", "this is a \"expression\" with {braces}")
                                        .getUser()},
                new Object[]{
                        "role with tip with property name",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "role with tip with property name and value",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "role with tip with property name, value and referenced admin object",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new RoleData(this, "")))
                                        .getUser()},
                new Object[]{
                        "role with tip with multiple properties",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new RoleData(this, "property \" role")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "role with two tool sets",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"").getUser()
                                .newToolSet("tool set b").getUser()},
                new Object[]{
                        "role with tool set with two programs",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProgram(new MQLProgramData(this, "\"tool set\" program 1"))
                                        .addProgram(new MQLProgramData(this, "\"tool set\" program 2"))
                                        .getUser()},
                new Object[]{
                        "role with tool set with visible user definition",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "role with not active tool set",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "role with active tool set",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "role with not hidden tool set",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "role with hidden tool set",
                        new RoleData(this, "hallo \" test")
                                .newTip("my tool set \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "role with tool set with property name",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "role with tool set with property name and value",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "role with tool set with property name, value and referenced admin object",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new RoleData(this, "")))
                                        .getUser()},
                new Object[]{
                        "role with tool set with multiple properties",
                        new RoleData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new RoleData(this, "property \" role")))
                                        .getUser()},
        };
    }

    /**
     * Cleanup all test roles.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.ROLE);
        this.cleanup(AbstractTest.CI.SITE);
        this.cleanup(AbstractTest.CI.MQL_PROGRAM);
    }

    /**
     * Tests a new created role and the related export.
     *
     * @param _description  description of the test case
     * @param _role         role to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "roles", description = "test export of new created role")
    public void simpleExport(final String _description,
                             final RoleData _role)
        throws Exception
    {
        _role.create();
        _role.checkExport(_role.export());
    }

    /**
     * Tests an update of non existing role. The result is tested with by
     * exporting the role and checking the result.
     *
     * @param _description  description of the test case
     * @param _role         role to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "roles", description = "test update of non existing role")
    public void simpleUpdate(final String _description,
                             final RoleData _role)
        throws Exception
    {
        // create all parent roles
        for (final RoleData parentRole : _role.getParent())  {
            parentRole.create();
        }

        // create site
        if (_role.getSite() != null)  {
            _role.getSite().create();
        }

        // create cue properties
        for (final CueData<RoleData> cue : _role.getCues())  {
            for (final PropertyDef prop : cue.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create filter properties
        for (final FilterData<RoleData> filter : _role.getFilters())  {
            for (final PropertyDef prop : filter.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create query properties
        for (final QueryData<RoleData> query : _role.getQueries())  {
            for (final PropertyDef prop : query.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create table properties
        for (final TableData<RoleData> table : _role.getTables())  {
            for (final PropertyDef prop : table.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create tip properties
        for (final TipData<RoleData> tip : _role.getTips())  {
            for (final PropertyDef prop : tip.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create tool set properties and programs
        for (final ToolSetData<RoleData> toolSet : _role.getToolSets())  {
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
        this.update(_role.getCIFileName(), _role.ciFile());
        final ExportParser exportParser = _role.export();
        _role.checkExport(exportParser);

        // second update with delivered content
        this.update(_role.getCIFileName(), exportParser.getOrigCode());
        _role.checkExport(_role.export());
    }

    /**
     * Test an update of a role where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing role with site by removing site")
    public void checkExistingSiteRemovedWithinUpdate()
        throws Exception
    {
        final RoleData role = new RoleData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        role.create();
        role.setSite(null);
        this.update(role);

        Assert.assertEquals(this.mql("escape print role \"" + AbstractTest.convertMql(role.getName()) + "\" select site dump"),
                            "",
                            "check that no site is defined");
    }

    /**
     * Check that the hidden flag is removed within update.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the hidden flag is removed within update")
    public void checkHiddenFlagRemoveWithinUpdate()
        throws Exception
    {
        final RoleData role = new RoleData(this, "hallo \" test").setHidden(true);
        role.create();
        role.setHidden(null);
        this.update(role);
        Assert.assertEquals(this.mql("escape print role \"" + AbstractTest.convertMql(role.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that role is not hidden");
    }
}
