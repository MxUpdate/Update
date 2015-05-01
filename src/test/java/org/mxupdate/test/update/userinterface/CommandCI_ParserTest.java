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

package org.mxupdate.test.update.userinterface;

import org.mxupdate.test.update.AbstractParserTest;
import org.mxupdate.update.userinterface.Command_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Command_mxJPO command} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class CommandCI_ParserTest
    extends AbstractParserTest<Command_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\""},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" label \"\" href \"\" alt \"\""},
            {"2b) description not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "                 label \"\" href \"\" alt \"\""},
            // hidden flag
            {"3a) hidden",
                    "",
                    "description \"\" hidden  label \"\" href \"\" alt \"\""},
            {"3b) not hidden (not defined)",
                    "description \"\"         label \"\" href \"\" alt \"\"",
                    "description \"\" !hidden label \"\" href \"\" alt \"\""},
            // label
            {"4a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\""},
            {"4b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\"            href \"\" alt \"\""},
            // href
            {"5a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\""},
            {"5b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\"           alt \"\""},
            // alt
            {"6a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\""},
            {"6b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\" href \"\"         "},
            // user
            {"7a) user",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" user \"Key\""},
            {"7b) multiple users",
                    "description \"\" label \"\" href \"\" alt \"\" user \"key1\" user \"key2\" ",
                    "description \"\" label \"\" href \"\" alt \"\" user \"key2\" user \"key1\" "},
            // code
            {"8a) code",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" code \" abc \""},
            {"8b) code empty",
                    "description \"\" label \"\" href \"\" alt \"\" ",
                    "description \"\" label \"\" href \"\" alt \"\" code \"\""},
            // setting
            {"9a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"Value\""},
            {"9b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"\" \"Value\""},
            {"9c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"\""},
            {"9d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key2\" \"value\" setting \"key1\" \"value\""},
            // property
            {"10a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\""},
            {"10b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"10c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"10d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Command_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name)
    {
        return new Command_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_COMMAND.updateType), _name);
    }
}
