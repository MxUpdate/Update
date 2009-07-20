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

package org.mxupdate.test.ci.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.Command;
import org.mxupdate.test.data.Menu;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of menus.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MenuExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test menus.
     *
     * @return object array with all test menus
     */
    @DataProvider(name = "menus")
    public Object[][] getMenus()
    {
        final Menu menu = new Menu(this, "hallo \" test")
                .setValue("label", "command label \" \\ ' #")
                .setValue("description", "\"\\\\ hallo")
                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                .setSetting("Setting 1", "Setting Value ' 1")
                .addChild(new Menu(this, "child menu 1"))
                .addChild(new Command(this, "child command 1"));

        return new Object[][]  {
                new Object[]{menu}
        };
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
        this.cleanup(CI.COMMAND);
        this.cleanup(CI.MENU);
    }

    /**
     * Tests a 'simple' menu but with a quote in name and with one command and
     * menu.
     *
     * @param _menu     menu to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "menus", description = "test export of a single menu")
    public void testExportSingleMenu(final Menu _menu)
        throws Exception
    {
        _menu.create();

        final Export export = this.export(CI.MENU, _menu.getName());
        final ExportParser exportParser = new ExportParser(CI.MENU, export);

        _menu.checkExport(exportParser);
    }

    /**
     * Tests an update of non existing command. The result is tested with by
     * exporting the command and checking the result.
     *
     * @param _menu     menu to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "menus", description = "test update of non existing menu")
    public void testUpdate(final Menu _menu)
        throws Exception
    {
        _menu.createChildren();
        this.update(_menu.getCIFileName(), _menu.ciFile());
        final Export export = this.export(CI.MENU, _menu.getName());
        final ExportParser exportParser = new ExportParser(CI.MENU, export);
        _menu.checkExport(exportParser);
    }
}
