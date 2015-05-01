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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.userinterface.CommandData;
import org.mxupdate.test.util.IssueLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export of commands.
 *
 * @author The MxUpdate Team
 */
@Test()
public class CommandTest
    extends AbstractUITest<CommandData>
{
    /**
     * Data provider for test commands.
     *
     * @return object array with all test commands
     */
    @DataProvider(name = "data")
    public Object[][] getCommands()
    {
        return this.prepareData("command",
                new Object[]{
                        "complex command with settings",
                        new CommandData(this, "hello \" test")
                                .setValue("label", "command label \" \\ ' #")
                                .setValue("description", "\"\\\\ hallo")
                                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                                .setKeyValue("setting", "Setting 1", "Setting Value ' 1")
                                .setKeyValue("setting", "Setting 2", "Value2")
                                .setKeyValue("setting", "Setting \"3\"", "Value 3")},
                new Object[]{
                        "complex command with users",
                        new CommandData(this, "command")
                                .setValue("label", "aaa.bbb.ccc")
                                .setValue("description", "\"\\\\ hallo")
                                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                                .setKeyValue("setting", "Setting 1", "Setting Value ' 1")
                                .defData("user", new RoleData(this, "assigned \"role\""))
                                .defData("user", new PersonAdminData(this, "assigned \"person\""))},
                new Object[]{
                        "complex command with value with escaped special characters but w/o any space",
                        new CommandData(this, "command")
                                .setValue("label", "aa\"bb")
                                .setValue("description", "desc\"\\\\ription")
                                .setValue("href", "javascript(\\\"test\\\")")
                                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?\"mode=insert")
                                .setKeyValue("setting", "Setting 1", "SettingValue\"1")
                                .setKeyValue("setting", "Setting 2", "desc\"\\\\ription")});
    }

    /**
     * Positive test for Command with 'add setting' syntax.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for command with 'add setting' syntax")
    public void positiveTestUpdateWithAddSettingSyntax()
        throws Exception
    {
        final CommandData orgData = new CommandData(this, "command").addCILine("add setting \"Key\" \"Value\"");
        final CommandData expData = new CommandData(this, "command").setKeyValue("setting", "Key", "Value");

        orgData.update("");

        expData.checkExport(expData.export());
    }

    /**
     * Positive test for command with 'add user' syntax.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for command with 'add user' syntax")
    public void positiveTestUpdateWithAddUserSyntax()
        throws Exception
    {
        final CommandData orgData = new CommandData(this, "command").addCILine("add user \"" + AbstractTest.PREFIX + "adminuser" + "\"");
        final CommandData expData = new CommandData(this, "command").defData("user", new PersonAdminData(this, "adminuser"));

        expData.createDependings();

        orgData.update("");

        expData.checkExport(expData.export());
    }

    /**
     * Positive test for update of code.
     *
     * @throws Exception if test failed
     */
    @IssueLink("194")
    @Test(description = "issue #194: positive test for update of code")
    public void positiveTestCodeUpdate()
        throws Exception
    {
        final String code = "function beta()\n{\n    var idx = 1;\n    $[test]\n}";

        final CommandData command = new CommandData(this, "Test").setValue("code", code).update("");

        Assert.assertEquals(code, this.mql("print command " + command.getName() + " select code dump"));

        final ExportParser exportParser = command.export();

        // remove code and update via export
        this.mql("mod command " + command.getName() + " code ''");
        command.updateWithCode(exportParser.getCode(), "");

        // and check result (so that the export of code is also checked!)
        Assert.assertEquals(code, this.mql("print command " + command.getName() + " select code dump"));
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_COMMAND);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
    }

    @Override()
    protected CommandData createNewData(final String _name)
    {
        return new CommandData(this, _name);
    }
}
