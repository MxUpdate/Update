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

package org.mxupdate.test.ci.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.user.workspace.CueData;
import org.mxupdate.test.data.user.workspace.FilterData;
import org.mxupdate.test.data.user.workspace.QueryData;
import org.mxupdate.test.data.user.workspace.TableData;
import org.mxupdate.test.data.user.workspace.TipData;
import org.mxupdate.test.data.user.workspace.ToolSetData;
import org.mxupdate.test.data.user.workspace.ViewData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Prepares test data for users (group, role and person).
 *
 * @author The MxUpdate Team
 * @param <USER> user class which is tested
 */
public abstract class AbstractUserTest<USER extends AbstractUserData<USER>>
    extends AbstractDataExportUpdate<USER>
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
        ret.addAll(Arrays.asList(_datas));

        if (_logText != null)  {
            this.appendCue(_logText, ret);
            this.appendFilter(_logText, ret);
            this.appendQuery(_logText, ret);
            this.appendTable(_logText, ret);
            this.appendTip(_logText, ret);
            this.appendToolSet(_logText, ret);
            this.appendView(_logText, ret);
        }

        return super.prepareData(_logText, ret.toArray(new Object[ret.size()][]));
    }

    /**
     * Appends the test data for the cues related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendCue(final String _logText,
                           final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two cues",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"").getUser()
                        .newCue("cue b").getUser()});
        _ret.add(new Object[]{
                _logText + " with complex cue",
                this.createNewData("hallo \" test")
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
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue which applies to relationship",
                this.createNewData("hallo \" test")
                    .newCue("my cue \"test\"")
                        .setValue("appliesto", "relationship")
                        .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue which applies to both business object and relationship",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setValue("appliesto", "all")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with order -1",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setValue("order", "-1")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with order 0",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setValue("order", "0")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with order 1",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setValue("order", "1")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with visible user definition",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not active cue",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setActive(false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with active cue",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setActive(true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not hidden cue",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setFlag("hidden", false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with hidden cue",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .setFlag("hidden", true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with property name",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with property name and value",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with property name, value and referenced admin object",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with cue with multiple properties",
                this.createNewData("hallo \" test")
                        .newCue("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" " + _logText)))
                                .getUser()});
    }

    /**
     * Appends the test data for the filters related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendFilter(final String _logText,
                              final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two filters",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"").getUser()
                        .newFilter("filter b").getUser()});
        _ret.add(new Object[]{
                _logText + " with complex filter",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setValue("appliesto", "businessobject")
                                .setValue("type", "TYPE \"")
                                .setValue("name", "NAME \"")
                                .setValue("revision", "revision \"")
                                .setValue("vault", "vault \"")
                                .setValue("owner", "owner \"")
                                .setValue("where", "LL = \"attribute[hallo]\"")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter which applies to relationship",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setValue("appliesto", "relationship")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter with visible user definition",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not active filter",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setActive(false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with active filter",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setActive(true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not hidden filter",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setFlag("hidden", false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with hidden filter",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setFlag("hidden", true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter in the to direction",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setDirection(FilterData.Direction.TO)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter in the from direction",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setDirection(FilterData.Direction.FROM)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter in the both direction",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .setDirection(FilterData.Direction.BOTH)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter with property name",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter with property name and value",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter with property name, value and referenced admin object",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with filter with multiple properties",
                this.createNewData("hallo \" test")
                        .newFilter("my filter \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" " + _logText)))
                                .getUser()});
    }

    /**
     * Appends the test data for the queries related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendQuery(final String _logText,
                             final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two queries",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"").getUser()
                        .newQuery("query b").getUser()});
        _ret.add(new Object[]{
                _logText + " with complex query",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .setValue("type", "TYPE \"")
                                .setValue("name", "NAME \"")
                                .setValue("revision", "revision \"")
                                .setValue("vault", "vault \"")
                                .setValue("owner", "owner \"")
                                .setValue("where", "LL = \"attribute[hallo]\"")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with visible user definition",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not hidden query",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .setFlag("hidden", false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with hidden query",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .setFlag("hidden", true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with expand type true",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .setExpandType(true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with expand type false",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .setExpandType(false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with property name",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with property name and value",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with property name, value and referenced admin object",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with query with multiple properties",
                this.createNewData("hallo \" test")
                        .newQuery("my query \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" " + _logText)))
                                .getUser()});
    }

    /**
     * Appends the test data for the tables related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendTable(final String _logText,
                             final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two tables",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"").getUser()
                        .newTable("table b").setActive(false).getUser()});
        _ret.add(new Object[]{
                _logText + " with complex table",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .newField(null)
                                        .setValue("businessobject", "name")
                                        .setValue("href", "http://www.mxupdate.org")
                                        .setValue("alt", "alt \" value")
                                        .setValue("range", "a \"range href\"")
                                        .setValue("update", "an \"update href\"")
                                        .setValue("program", "MxUpdate")
                                        .getFormTable()
                                .newField(null)
                                        .setValue("relationship", "type")
                                        .getFormTable()
                                .newField("scale 50")               .setScale(50.0).getFormTable()
                                .newField("scale 50.5")             .setScale(50.5).getFormTable()
                                .newField("no size")                .setSize(null, null).getFormTable()
                                .newField("size 1 1")               .setSize(1.0, 1.0).getFormTable()
                                .newField("size 0.5 0.5")           .setSize(0.5, 0.5).getFormTable()
                                .newField("size 100 59")            .setSize(100.0, 59.0).getFormTable()
                                .newField("no minimum size")        .setMinSize(null, null).getFormTable()
                                .newField("minimum size 0 0")       .setMinSize(0.0, 0.0).getFormTable()
                                .newField("minimum size 0.5 0.5")   .setMinSize(0.5, 0.5).getFormTable()
                                .newField("minimum size 100 59")      .setMinSize(100.0, 59.0).getFormTable()
                                .newField("default auto height")    .setAutoHeight(null).getFormTable()
                                .newField("auto height true")       .setAutoHeight(true).getFormTable()
                                .newField("auto height false")      .setAutoHeight(false).getFormTable()
                                .newField("default auto width")     .setAutoWidth(null).getFormTable()
                                .newField("auto width true")        .setAutoWidth(true).getFormTable()
                                .newField("auto width false")       .setAutoWidth(false).getFormTable()
                                .newField("default (none) sorttype").setSortType(null).getFormTable()
                                .newField("sorttype alpha")         .setSortType("alpha").getFormTable()
                                .newField("sorttype numeric")       .setSortType("numeric").getFormTable()
                                .newField("sorttype other")         .setSortType("other").getFormTable()
                                .newField("sorttype none")          .setSortType("none").getFormTable()
                                .newField("default not editable")   .setEditable(null).getFormTable()
                                .newField("editable ")              .setEditable(true).getFormTable()
                                .newField("not editable")           .setEditable(false).getFormTable()
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with table with visible user definition",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not active table",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .setActive(false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with active table",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .setActive(true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with table and hidden field",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .newField("field").setFlag("hidden", true).getFormTable()
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with table with property name",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with table with property name and value",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with table with property name, value and referenced admin object",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with table with multiple properties",
                this.createNewData("hallo \" test")
                        .newTable("my table \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" " + _logText)))
                                .getUser()});
    }

    /**
     * Appends the test data for the tips related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendTip(final String _logText,
                           final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two tips",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"").getUser()
                        .newTip("tip b").getUser()});
        _ret.add(new Object[]{
                _logText + " with complex tip",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setValue("appliesto", "businessobject")
                                .setValue("type", "TYPE \"")
                                .setValue("name", "NAME \"")
                                .setValue("revision", "revision \"")
                                .setValue("vault", "vault \"")
                                .setValue("owner", "owner \"")
                                .setValue("where", "LL = \"attribute[hallo]\"")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip which applies to relationship",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setValue("appliesto", "relationship")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip with visible user definition",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not active tip",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setActive(false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with active tip",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setActive(true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not hidden tip",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setFlag("hidden", false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with hidden tip",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setFlag("hidden", true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip with expression",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .setValue("expression", "this is a \"expression\" with {braces}")
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip with property name",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip with property name and value",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip with property name, value and referenced admin object",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tip with multiple properties",
                this.createNewData("hallo \" test")
                        .newTip("my tip \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" " + _logText)))
                                .getUser()});
    }

    /**
     * Appends the test data for the tool sets related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendToolSet(final String _logText,
                               final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two tool sets",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"").getUser()
                        .newToolSet("tool set b").getUser()});
        _ret.add(new Object[]{
                _logText + " with tool set with two programs",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .addProgram(new MQLProgramData(this, "\"tool set\" program 1"))
                                .addProgram(new MQLProgramData(this, "\"tool set\" program 2"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tool set with visible user definition",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not active tool set",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .setActive(false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with active tool set",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .setActive(true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not hidden tool set",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .setFlag("hidden", false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with hidden tool set",
                this.createNewData("hallo \" test")
                        .newTip("my tool set \"test\"")
                                .setFlag("hidden", true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tool set with property name",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tool set with property name and value",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tool set with property name, value and referenced admin object",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with tool set with multiple properties",
                this.createNewData("hallo \" test")
                        .newToolSet("my tool set \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" " + _logText)))
                                .getUser()});
    }

    /**
     * Appends the test data for the view related to an user.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _ret      list of objects to return
     */
    private void appendView(final String _logText,
                            final List<Object[]> _ret)
    {
        _ret.add(new Object[]{
                _logText + " with two views",
                this.createNewData("hello \" test")
                        .newView("my view \"test\"").getUser()
                        .newView("view b").getUser()});
        _ret.add(new Object[]{
                _logText + " with one view and all assigned objects",
                this.prepareViewTestData("hello \" test",
                                         "\"view\" name",
                                         new String[]{"active cue '1'",             "active cue \"2\""},
                                         new String[]{"not active cue '1'",         "not active cue \"2\""},
                                         new String[]{"active filter '1'",          "active filter \"2\""},
                                         new String[]{"not active filter '1'",      "not active filter \"2\""},
                                         new String[]{"active tip '1'",             "active tip \"2\""},
                                         new String[]{"not active tip '1'",         "not active tip \"2\""},
                                         new String[]{"active tool set '1'",        "active tool set \"2\""},
                                         new String[]{"not active tool set '1'",    "not active tool set \"2\""},
                                         // only onw table could be active to the same time...
                                         new String[]{"active table '1'"},
                                         new String[]{"not active table '1'",       "not active table \"2\""})});
        _ret.add(new Object[]{
                _logText + " with view with visible user definition",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .setVisible(new PersonAdminData(this, "creator"))
                                .setVisible(new PersonAdminData(this, "guest"))
                                .setVisible(new PersonAdminData(this, "Test Everything"))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with not hidden view",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .setFlag("hidden", false)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with hidden view",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .setFlag("hidden", true)
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with view with property name",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with view with property name and value",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\""))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with view with property name, value and referenced admin object",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData(" property \" " + _logText)))
                                .getUser()});
        _ret.add(new Object[]{
                _logText + " with view with multiple properties",
                this.createNewData("hello \" test")
                        .newView("my cue \"test\"")
                                .addProperty(new PropertyDef("my test \"property\" 1"))
                                .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\""))
                                .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData( "property \" " + _logText)))
                                .getUser()});

    }

    /**
     * Prepares the test data for views.
     *
     * @param _name                     name of the user data
     * @param _viewName                 name of the view
     * @param _activeCueNames           names of active cues
     * @param _notActiveCueNames        name of inactive cues
     * @param _activeFilterNames        name of active filters
     * @param _notActiveFilterNames     name of inactive filters
     * @param _activeTipNames           name of active tips
     * @param _notActiveTipNames        name of inactive tips
     * @param _activeToolSetNames       name of active tool sets
     * @param _notActiveToolSetNames    name of inactive tool sets
     * @param _activeTableNames         name of active tables
     * @param _notActiveTableNames      name of inactive tables
     * @return user test data used for views
     */
    private USER prepareViewTestData(final String _name,
                                     final String _viewName,
                                     final String[] _activeCueNames,
                                     final String[] _notActiveCueNames,
                                     final String[] _activeFilterNames,
                                     final String[] _notActiveFilterNames,
                                     final String[] _activeTipNames,
                                     final String[] _notActiveTipNames,
                                     final String[] _activeToolSetNames,
                                     final String[] _notActiveToolSetNames,
                                     final String[] _activeTableNames,
                                     final String[] _notActiveTableNames)
    {
        final USER userData = this.createNewData(_name);
        final ViewData<USER> view = userData.newView(_viewName);
        if (_activeCueNames != null)  {
            for (final String name : _activeCueNames)  {
                view.addActive(userData.newCue(name));
            }
        }
        if (_notActiveCueNames != null)  {
            for (final String name : _notActiveCueNames)  {
                view.addNotActive(userData.newCue(name));
            }
        }
        if (_activeFilterNames != null)  {
            for (final String name : _activeFilterNames)  {
                view.addActive(userData.newFilter(name));
            }
        }
        if (_notActiveFilterNames != null)  {
            for (final String name : _notActiveFilterNames)  {
                view.addNotActive(userData.newFilter(name));
            }
        }
        if (_activeTipNames != null)  {
            for (final String name : _activeTipNames)  {
                view.addActive(userData.newTip(name));
            }
        }
        if (_notActiveTipNames != null)  {
            for (final String name : _notActiveTipNames)  {
                view.addNotActive(userData.newTip(name));
            }
        }
        if (_activeToolSetNames != null)  {
            for (final String name : _activeToolSetNames)  {
                view.addActive(userData.newToolSet(name));
            }
        }
        if (_notActiveToolSetNames != null)  {
            for (final String name : _notActiveToolSetNames)  {
                view.addNotActive(userData.newToolSet(name));
            }
        }
        if (_activeTableNames != null)  {
            for (final String name : _activeTableNames)  {
                view.addActive(userData.newTable(name));
            }
        }
        if (_notActiveTableNames != null)  {
            for (final String name : _notActiveTableNames)  {
                view.addNotActive(userData.newTable(name));
            }
        }
        return userData;
    }

    /**
     * Checks if for given parameter the cue workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that cue the workspace objects are (not) removed")
    public void checkWSOCueNotRemoved(final String _paramName,
                                      final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final CueData<USER> cue = user.newCue("cue not removed test");
        user.create();
        user.getCues().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list cue user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? cue.getName() : "",
                            "check that cue exists");
    }

    /**
     * Checks if for given parameter the filter workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that the filter workspace objects are (not) removed")
    public void checkWSOFilterNotRemoved(final String _paramName,
                                         final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final FilterData<USER> filter = user.newFilter("filter not removed test");
        user.create();
        user.getFilters().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list filter user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? filter.getName() : "",
                            "check that filter exists");
    }

    /**
     * Checks if for given parameter the query workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that the query workspace objects are (not) removed")
    public void checkWSOQueryNotRemoved(final String _paramName,
                                        final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final QueryData<USER> query = user.newQuery("query not removed test");
        user.create();
        user.getQueries().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list query user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? query.getName() : "",
                            "check that query exists");
    }

    /**
     * Checks if for given parameter the table workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that the table workspace objects are (not) removed")
    public void checkWSOTableNotRemoved(final String _paramName,
                                        final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final TableData<USER> table = user.newTable("table not removed test");
        user.create();
        user.getTables().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list table user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? table.getName() : "",
                            "check that table exists");
    }

    /**
     * Checks if for given parameter the tip workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that the tip workspace objects are (not) removed")
    public void checkWSOTipNotRemoved(final String _paramName,
                                      final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final TipData<USER> tip = user.newTip("tip not removed test");
        user.create();
        user.getTips().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list tip user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? tip.getName() : "",
                            "check that tip exists");
    }

    /**
     * Checks if for given parameter the tool set workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that the tool set workspace objects are (not) removed")
    public void checkWSOToolSetNotRemoved(final String _paramName,
                                          final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final ToolSetData<USER> toolSet = user.newToolSet("tool set not removed test");
        user.create();
        user.getToolSets().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list toolset user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? toolSet.getName() : "",
                            "check that tool set exists");
    }

    /**
     * Checks if for given parameter the view workspace objects are ignored
     * (and not removed).
     *
     * @param _paramName    name of the used parameter to check
     * @param _ignored      is the user ignored?
     * @throws Exception if check failed
     */
    @Test(dataProvider = "wsoParameters", description = "check that the view workspace objects are (not) removed")
    public void checkWSOViewNotRemoved(final String _paramName,
                                       final boolean _ignored)
        throws Exception
    {
        final USER user = this.createNewData("hello \" test");
        final ViewData<USER> view = user.newView("view not removed test");
        user.create();
        user.getViews().clear();
        user.update((String) null, _paramName, "*");
        Assert.assertEquals(this.mql("escape list view user \"" + AbstractTest.convertMql(user.getName()) + "\""),
                            _ignored ? view.getName() : "",
                            "check that view exists");
    }
}
