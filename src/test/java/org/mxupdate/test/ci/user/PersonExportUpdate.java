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
import org.mxupdate.test.data.user.PersonAdminData;
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
 * Test cases for the export and update of administration persons (without
 * related business object).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PersonExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test persons.
     *
     * @return object array with all test persons
     */
    @DataProvider(name = "persons")
    public Object[][] getPersons()
    {
        return new Object[][]  {
                new Object[]{
                        "simple person",
                        new PersonAdminData(this, "hallo \" test")
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "person with some access",
                        new PersonAdminData(this, "hallo \" test")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "person with normal access but specific changeName / changevault access but no business access",
                        new PersonAdminData(this, "hallo \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")
                                .addAccess("changeName", "changeVault")},
                new Object[]{
                        "person with normal access but non specific access but no business access",
                        new PersonAdminData(this, "hallo \" test")
                                .addType("application", "full", "notbusiness", "notsystem", "notinactive", "nottrusted")
                                },
                new Object[]{
                        "person as business administration for types",
                        new PersonAdminData(this, "hallo \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type")},
                new Object[]{
                        "person as business administration for types and vaults",
                        new PersonAdminData(this, "hallo \" test")
                                .addType("application", "notfull", "business", "notsystem", "notinactive", "nottrusted")
                                .addAdminAccess("type", "vault")},
                new Object[]{
                        "person with one group",
                        new PersonAdminData(this, "hallo \" test")
                                .addGroup(new GroupData(this, "test \"group\""))},
                new Object[]{
                        "person with two groups",
                        new PersonAdminData(this, "hallo \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))},
                new Object[]{
                        "person with one role",
                        new PersonAdminData(this, "hallo \" test")
                                .addRole(new RoleData(this, "test \"role\""))},
                new Object[]{
                        "person with two roles",
                        new PersonAdminData(this, "hallo \" test")
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},
                new Object[]{
                        "person with two groups and roles",
                        new PersonAdminData(this, "hallo \" test")
                                .addGroup(new GroupData(this, "test \"group\" 1"))
                                .addGroup(new GroupData(this, "test \"group\" 2"))
                                .addRole(new RoleData(this, "test \"role\" 1"))
                                .addRole(new RoleData(this, "test \"role\" 2"))},

                new Object[]{
                        "hidden person",
                        new PersonAdminData(this, "hallo \" test")
                                .setHidden(true)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "not hidden person",
                        new PersonAdminData(this, "hallo \" test")
                                .setHidden(false)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "default hidden person",
                        new PersonAdminData(this, "hallo \" test")
                                .setHidden(null)
                                .setValue("description", "\"\\\\ hallo")},
                new Object[]{
                        "person with assigned site",
                        new PersonAdminData(this, "hallo \" test")
                                .setSite(new SiteData(this, "Test \" Site"))},
                new Object[]{
                        "complex person",
                        new PersonAdminData(this, "hallo \" test")
                                .setValue("fullname", "test person \"full name\"")
                                .setValue("description", "test person \"comment\"")
                                .setValue("address", "test person \"address\"")
                                .setValue("email", "test person \"email\" address")
                                .setValue("phone", "test person \"phone\"")},
                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "person with two cues",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"").getUser()
                                .newCue("cue b").getUser()},
                new Object[]{
                        "person with complex cue",
                        new PersonAdminData(this, "hallo \" test")
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
                        "person with cue which applies to relationship",
                        new PersonAdminData(this, "hallo \" test")
                            .newCue("my cue \"test\"")
                                .setValue("appliesto", "relationship")
                                .getUser()},
                new Object[]{
                        "person with cue which applies to both business object and relationship",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("appliesto", "all")
                                        .getUser()},
                new Object[]{
                        "person with cue with order -1",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "-1")
                                        .getUser()},
                new Object[]{
                        "person with cue with order 0",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "0")
                                        .getUser()},
                new Object[]{
                        "person with cue with order 1",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setValue("order", "1")
                                        .getUser()},
                new Object[]{
                        "person with cue with visible user definition",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "person with not active cue",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "person with active cue",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "person with not hidden cue",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "person with hidden cue",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "person with cue with property name",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "person with cue with property name and value",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "person with cue with property name, value and referenced admin object",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new PersonAdminData(this, "")))
                                        .getUser()},
                new Object[]{
                        "person with cue with multiple properties",
                        new PersonAdminData(this, "hallo \" test")
                                .newCue("my cue \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new PersonAdminData(this, "property \" person")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "person with two filters",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"").getUser()
                                .newFilter("filter b").getUser()},
                new Object[]{
                        "person with complex filter",
                        new PersonAdminData(this, "hallo \" test")
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
                        "person with filter which applies to relationship",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setValue("appliesto", "relationship")
                                        .getUser()},
                new Object[]{
                        "person with filter with visible user definition",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "person with not active filter",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "person with active filter",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "person with not hidden filter",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "person with hidden filter",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "person with filter in the to direction",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.TO)
                                        .getUser()},
                new Object[]{
                        "person with filter in the from direction",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.FROM)
                                        .getUser()},
                new Object[]{
                        "person with filter in the both direction",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .setDirection(FilterData.Direction.BOTH)
                                        .getUser()},
                new Object[]{
                        "person with filter with property name",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "person with filter with property name and value",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "person with filter with property name, value and referenced admin object",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new PersonAdminData(this, "")))
                                        .getUser()},
                new Object[]{
                        "person with filter with multiple properties",
                        new PersonAdminData(this, "hallo \" test")
                                .newFilter("my filter \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new PersonAdminData(this, "property \" person")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "person with two queries",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"").getUser()
                                .newQuery("query b").getUser()},
                new Object[]{
                        "person with complex query",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setValue("type", "TYPE \"")
                                        .setValue("name", "NAME \"")
                                        .setValue("revision", "revision \"")
                                        .setValue("vault", "vault \"")
                                        .setValue("owner", "owner \"")
                                        .setValue("where", "LL = \"attribute[hallo]\"")
                                        .getUser()},
                new Object[]{
                        "person with query with visible user definition",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "person with not hidden query",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "person with hidden query",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "person with query with expand type true",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setExpandType(true)
                                        .getUser()},
                new Object[]{
                        "person with query with expand type false",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .setExpandType(false)
                                        .getUser()},
                new Object[]{
                        "person with query with property name",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "person with query with property name and value",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "person with query with property name, value and referenced admin object",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new PersonAdminData(this, "")))
                                        .getUser()},
                new Object[]{
                        "person with query with multiple properties",
                        new PersonAdminData(this, "hallo \" test")
                                .newQuery("my query \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new PersonAdminData(this, "property \" person")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "person with two tables",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"").getUser()
                                .newTable("table b").setActive(false).getUser()},
                new Object[]{
                        "person with complex table",
                        new PersonAdminData(this, "hallo \" test")
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
                        "person with table with visible user definition",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "person with not active table",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "person with active table",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "person with table with property name",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "person with table with property name and value",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "person with table with property name, value and referenced admin object",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new PersonAdminData(this, "")))
                                        .getUser()},
                new Object[]{
                        "person with table with multiple properties",
                        new PersonAdminData(this, "hallo \" test")
                                .newTable("my table \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new PersonAdminData(this, "property \" person")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "person with two tips",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"").getUser()
                                .newTip("tip b").getUser()},
                new Object[]{
                        "person with complex tip",
                        new PersonAdminData(this, "hallo \" test")
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
                        "person with tip which applies to relationship",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("appliesto", "relationship")
                                        .getUser()},
                new Object[]{
                        "person with tip with visible user definition",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "person with not active tip",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "person with active tip",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "person with not hidden tip",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "person with hidden tip",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "person with tip with expression",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .setValue("expression", "this is a \"expression\" with {braces}")
                                        .getUser()},
                new Object[]{
                        "person with tip with property name",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "person with tip with property name and value",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "person with tip with property name, value and referenced admin object",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new PersonAdminData(this, "")))
                                        .getUser()},
                new Object[]{
                        "person with tip with multiple properties",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tip \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new PersonAdminData(this, "property \" person")))
                                        .getUser()},

                ////////////////////////////////////////////////////////////////
                new Object[]{
                        "person with two tool sets",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"").getUser()
                                .newToolSet("tool set b").getUser()},
                new Object[]{
                        "person with tool set with two programs",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProgram(new MQLProgramData(this, "\"tool set\" program 1"))
                                        .addProgram(new MQLProgramData(this, "\"tool set\" program 2"))
                                        .getUser()},
                new Object[]{
                        "person with tool set with visible user definition",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setVisible("creator","guest","Test Everything")
                                        .getUser()},
                new Object[]{
                        "person with not active tool set",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setActive(false)
                                        .getUser()},
                new Object[]{
                        "person with active tool set",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setActive(true)
                                        .getUser()},
                new Object[]{
                        "person with not hidden tool set",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .setHidden(false)
                                        .getUser()},
                new Object[]{
                        "person with hidden tool set",
                        new PersonAdminData(this, "hallo \" test")
                                .newTip("my tool set \"test\"")
                                        .setHidden(true)
                                        .getUser()},
                new Object[]{
                        "person with tool set with property name",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\""))
                                        .getUser()},
                new Object[]{
                        "person with tool set with property name and value",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                        .getUser()},
                new Object[]{
                        "person with tool set with property name, value and referenced admin object",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\"", new PersonAdminData(this, "")))
                                        .getUser()},
                new Object[]{
                        "person with tool set with multiple properties",
                        new PersonAdminData(this, "hallo \" test")
                                .newToolSet("my tool set \"test\"")
                                        .addProperty(new PropertyDef("my test \"property\" 1"))
                                        .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                        .addProperty(new PropertyDef("my test \"property\" 3", new PersonAdminData(this, "property \" person")))
                                        .getUser()},
        };
    }

    /**
     * Cleanup all test persons.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.PERSONADMIN);
        this.cleanup(AbstractTest.CI.GROUP);
        this.cleanup(AbstractTest.CI.ROLE);
        this.cleanup(AbstractTest.CI.SITE);
        this.cleanup(AbstractTest.CI.MQL_PROGRAM);
    }

    /**
     * Tests a new created person and the related export.
     *
     * @param _description  description of the test case
     * @param _person       person to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "persons", description = "test export of new created person")
    public void simpleExport(final String _description,
                             final PersonAdminData _person)
        throws Exception
    {
        _person.create();
        _person.checkExport(_person.export());
    }

    /**
     * Tests an update of non existing person. The result is tested with by
     * exporting the person and checking the result.
     *
     * @param _description  description of the test case
     * @param _person       person to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "persons", description = "test update of non existing person")
    public void simpleUpdate(final String _description,
                             final PersonAdminData _person)
        throws Exception
    {
        // create site
        if (_person.getSite() != null)  {
            _person.getSite().create();
        }

        // create assigned roles
        for (final RoleData role : _person.getRoles())  {
            role.create();
        }

        // create assigned groups
        for (final GroupData group : _person.getGroups())  {
            group.create();
        }

        // create cue properties
        for (final CueData<PersonAdminData> cue : _person.getCues())  {
            for (final PropertyDef prop : cue.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create filter properties
        for (final FilterData<PersonAdminData> filter : _person.getFilters())  {
            for (final PropertyDef prop : filter.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create query properties
        for (final QueryData<PersonAdminData> query : _person.getQueries())  {
            for (final PropertyDef prop : query.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create table properties
        for (final TableData<PersonAdminData> table : _person.getTables())  {
            for (final PropertyDef prop : table.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create tip properties
        for (final TipData<PersonAdminData> tip : _person.getTips())  {
            for (final PropertyDef prop : tip.getProperties())  {
                if (prop.getTo() != null)  {
                    prop.getTo().create();
                }
            }
        }

        // create tool set properties and programs
        for (final ToolSetData<PersonAdminData> toolSet : _person.getToolSets())  {
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
        this.update(_person.getCIFileName(), _person.ciFile());
        final ExportParser exportParser = _person.export();
        _person.checkExport(exportParser);

        // second update with delivered content
        this.update(_person.getCIFileName(), exportParser.getOrigCode());
        _person.checkExport(_person.export());
    }

    /**
     * Test an update of a person where the site is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update existing existing person with site by removing site")
    public void checkExistingSiteRemovedWithinUpdate()
        throws Exception
    {
        final PersonAdminData person = new PersonAdminData(this, "hallo \" test")
                .setSite(new SiteData(this, "Test \" Site"));
        person.create();
        person.setSite(null);
        this.update(person);

        Assert.assertEquals(this.mql("escape print person \""
                                    + AbstractTest.convertMql(person.getName())
                                    + "\" select site dump"),
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
        final PersonAdminData person = new PersonAdminData(this, "hallo \" test").setHidden(true);
        person.create();
        person.setHidden(null);
        this.update(person);
        Assert.assertEquals(this.mql("escape print person \"" + AbstractTest.convertMql(person.getName()) + "\" select hidden dump"),
                            "FALSE",
                            "check that person is not hidden");
    }
}
