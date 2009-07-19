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

package org.mxupdate.test.export.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.Command;
import org.mxupdate.test.data.Menu;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test cases for the export of menus.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MenuExport
    extends AbstractTest
{
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
     * @throws Exception if test failed
     */
    @Test()
    public void testSingleMenu()
        throws Exception
    {
        final Menu menu = new Menu(this, "hallo \" test")
                .setValue("label", "command label \" \\ ' #")
                .setValue("description", "\"\\\\ hallo")
                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                .setSetting("Setting 1", "Setting Value ' 1")
                .addChild(new Menu(this, "child menu 1"))
                .addChild(new Command(this, "child command 1"))
                .create();

        final Export export = this.export(CI.MENU, menu.getName());
        final ExportParser exportParser = new ExportParser(CI.MENU, export.getCode());

        menu.checkExport(exportParser);
    }
}
