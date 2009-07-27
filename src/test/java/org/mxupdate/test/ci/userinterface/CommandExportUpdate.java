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
import org.mxupdate.test.data.userinterface.Command;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export of commands.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class CommandExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test commands.
     *
     * @return object array with all test commands
     */
    @DataProvider(name = "commands")
    public Object[][] getCommands()
    {
        final Command command1 = new Command(this, "hallo \" test")
                .setValue("label", "command label \" \\ ' #")
                .setValue("description", "\"\\\\ hallo")
                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                .setSetting("Setting 1", "Setting Value ' 1")
                .setSetting("Setting 2", "Value2");
        final Command command2 = new Command(this, "command")
                .setValue("label", "aaa.bbb.ccc")
                .setValue("description", "\"\\\\ hallo")
                .setValue("href", "${COMMON_DIR}/emxTree.jsp?mode=insert")
                .setValue("alt", "${COMMON_DIR}/emxTreeAlt.jsp?mode=insert")
                .setSetting("Setting 1", "Setting Value ' 1")
                .addUser("guest")
                .addUser("creator");
        final Command command3 = new Command(this, "hallo\ttest");

        return new Object[][]  {
                new Object[]{command1},
                new Object[]{command2},
                new Object[]{command3}
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
        this.cleanup(CI.COMMAND);
    }

    /**
     * Tests a new created command and the related export.
     *
     * @param _command      command to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "commands", description = "test export of new created commands")
    public void testExport(final Command _command)
        throws Exception
    {
        _command.create();
        final Export export = this.export(CI.COMMAND, _command.getName());
        final ExportParser exportParser = new ExportParser(CI.COMMAND, export);
        _command.checkExport(exportParser);
    }

    /**
     * Tests an update of non existing command. The result is tested with by
     * exporting the command and checking the result.
     *
     * @param _command      command to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "commands", description = "test update of non existing command")
    public void testUpdate(final Command _command)
        throws Exception
    {
        this.update(_command.getCIFileName(), _command.ciFile());
        final Export export = this.export(CI.COMMAND, _command.getName());
        final ExportParser exportParser = new ExportParser(CI.COMMAND, export);
        _command.checkExport(exportParser);
    }
}
