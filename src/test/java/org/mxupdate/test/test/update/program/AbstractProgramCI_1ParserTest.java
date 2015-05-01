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

package org.mxupdate.test.test.update.program;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.program.AbstractProgram_mxJPO;
import org.testng.annotations.DataProvider;

/**
 * Tests the {@link AbstractProgram_mxJPO program CI} parser.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractProgramCI_1ParserTest<DATA extends AbstractAdminObject_mxJPO<?>>
    extends AbstractParserTest<DATA>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                    "",
                    "kind mql description \"\" "},
            // description
            {"2a) description",
                    "",
                    "kind mql description \"abc def\" "},
            {"2b) description not defined",
                    "kind mql description \"\"",
                    "kind mql                  "},
            // hidden flag
            {"3a) hidden",
                    "",
                    "kind mql description \"\" hidden "},
            {"3b) not hidden (not defined)",
                    "kind mql description \"\"         ",
                    "kind mql description \"\" !hidden "},
            // needsbusinessobject flag
            {"4a) needsbusinessobject",
                    "",
                    "kind mql description \"\" needsbusinessobject "},
            {"4b) not needsbusinessobject (not defined)",
                    "kind mql description \"\"         ",
                    "kind mql description \"\" !needsbusinessobject "},
            // downloadable flag
            {"5a) downloadable",
                    "",
                    "kind mql description \"\" downloadable "},
            {"5b) not downloadable (not defined)",
                    "kind mql description \"\"               ",
                    "kind mql description \"\" !downloadable "},
            // pipe flag
            {"6a) pipe",
                    "",
                    "kind mql description \"\" pipe "},
            {"6b) not pipe (not defined)",
                    "kind mql description \"\"         ",
                    "kind mql description \"\" !pipe "},
            // pooled flag
            {"7a) pooled",
                    "",
                    "kind mql description \"\" pooled "},
            {"7b) not pooled (not defined)",
                    "kind mql description \"\"         ",
                    "kind mql description \"\" !pooled "},
            // rule
            {"8a) rule",
                    "",
                    "kind mql description \"\" rule \"abc\""},
            {"8b) empty rule",
                    "kind mql description \"\"              ",
                    "kind mql description \"\" rule \"\" "},
            // execute
            {"9a) execute immediate",
                    "kind mql description \"\"                  ",
                    "kind mql description \"\" execute immediate"},
            {"9b) execute deferred",
                    "",
                    "kind mql description \"\" execute deferred"},
            {"9c) execute user",
                    "",
                    "kind mql description \"\" execute user \"TestUser\""},
            // code
            {"10a) code",
                    "",
                    "kind mql description \"\" code \" side \n second \""},
            {"10b) empty definition",
                    "kind mql description \"\"              ",
                    "kind mql description \"\" code \"\" "},
            // property
            {"20a) property special characters",
                    "",
                    "kind mql description \"\" property \"{}\\\"\""},
            {"20b) property and value special characters",
                    "",
                    "kind mql description \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"20c) property link special characters",
                    "",
                    "kind mql description \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"20d) property link and value special characters",
                    "",
                    "kind mql description \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }
}
