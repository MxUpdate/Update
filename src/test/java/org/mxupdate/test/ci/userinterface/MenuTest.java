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
import org.mxupdate.test.data.userinterface.CommandData;
import org.mxupdate.test.data.userinterface.MenuData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of menus.
 *
 * @author The MxUpdate Team
 */
@Test()
public class MenuTest
    extends AbstractUITest<MenuData>
{
    /**
     * Data provider for test menus.
     *
     * @return object array with all test menus
     */
    @DataProvider(name = "data")
    public Object[][] getMenus()
    {
        return this.prepareData("menu",
                new Object[]{
                        "menu with complex configuration",
                        new MenuData(this, "hallo \" test")
                                .setValue("label", "command label \" \\ ' #")
                                .setValue("description", "\"\\\\ hallo")
                                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                                .setKeyValue("setting", "Setting 1", "Setting Value ' 1")
                                .addChild(new MenuData(this, "child menu 1"))
                                .addChild(new CommandData(this, "child command 1"))
                                .addChild(new CommandData(this, "child command 2"))
                                .addChild(new CommandData(this, "child command 3"))},
                new Object[]{
                        "menu defined as type tree menu",
                        new MenuData(this, "hallo \" test")
                                .setTreeMenu(true)},
                new Object[]{
                        "menu not defined as type tree menu (with !treemenu)",
                        new MenuData(this, "hallo \" test")
                                .setTreeMenu(false)});
    }

    /**
     * Positive test to change the order of children's.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change the order of children's")
    public void positiveTestChangeOrderChilds()
        throws Exception
    {
        new MenuData(this, "test")
                .addChild(new MenuData(this, "child menu 1"))
                .addChild(new CommandData(this, "child command 1"))
                .addChild(new CommandData(this, "child command 2"))
                .addChild(new CommandData(this, "child command 3"))
                .create()
                .checkExport();

        new MenuData(this, "test")
                .addChild(new MenuData(this, "child menu 1"))
                .addChild(new CommandData(this, "child command 3"))
                .addChild(new CommandData(this, "child command 2"))
                .addChild(new CommandData(this, "child command 1"))
                .update("")
                .checkExport();
    }

    /**
     * Cleanup all test commands.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_COMMAND);
        this.cleanup(AbstractTest.CI.UI_MENU);
    }

    /**
     * Creates for given <code>_name</code> a new menu instance.
     *
     * @param _name     name of the menu instance
     * @return menu instance
     */
    @Override()
    protected MenuData createNewData(final String _name)
    {
        return new MenuData(this, _name);
    }
}
