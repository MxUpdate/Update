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
                                .setSetting("Setting 1", "Setting Value ' 1")
                                .addChild(new MenuData(this, "child menu 1"))
                                .addChild(new CommandData(this, "child command 1"))});
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
