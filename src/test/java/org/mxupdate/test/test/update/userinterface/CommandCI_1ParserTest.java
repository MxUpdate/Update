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

package org.mxupdate.test.test.update.userinterface;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.userinterface.Command_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Command_mxJPO command CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class CommandCI_1ParserTest
    extends AbstractParserTest<Command_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\""},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" label \"\" href \"\" alt \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" label \"\" href \"\" alt \"\""},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" label \"\" href \"\" alt \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" label \"\" href \"\" alt \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"command_abc\" description \"\" label \"\" href \"\" alt \"\""},
            {"2b) two symbolic names",
                    "symbolicname \"command_abc\" symbolicname \"command_def\" description \"\" label \"\" href \"\" alt \"\"",
                    "symbolicname \"command_def\" symbolicname \"command_abc\" description \"\" label \"\" href \"\" alt \"\""},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" label \"\" href \"\" alt \"\""},
            {"3b) description not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "                 label \"\" href \"\" alt \"\""},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" label \"\" href \"\" alt \"\""},
            {"3d) tab's in description",
                    "",
                    "description \"abc\\tdef\" label \"\" href \"\" alt \"\""},
            // hidden flag
            {"4a) hidden",
                    "",
                    "description \"\" hidden  label \"\" href \"\" alt \"\""},
            {"4b) not hidden (not defined)",
                    "description \"\"         label \"\" href \"\" alt \"\"",
                    "description \"\" !hidden label \"\" href \"\" alt \"\""},
            // label
            {"5a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\""},
            {"5b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\"            href \"\" alt \"\""},
            // href
            {"6a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\""},
            {"6b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\"           alt \"\""},
            // alt
            {"7a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\""},
            {"7b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\" href \"\"         "},
            // user
            {"8a) user",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" user \"Key\""},
            {"8b) multiple users",
                    "description \"\" label \"\" href \"\" alt \"\" user \"key1\" user \"key2\" ",
                    "description \"\" label \"\" href \"\" alt \"\" user \"key2\" user \"key1\" "},
            // code
            {"9a) code",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" code \" abc \""},
            {"9b) code empty",
                    "description \"\" label \"\" href \"\" alt \"\" ",
                    "description \"\" label \"\" href \"\" alt \"\" code \"\""},
            // setting
            {"10a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"Value\""},
            {"10b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"\" \"Value\""},
            {"10c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"\""},
            {"10d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key2\" \"value\" setting \"key1\" \"value\""},
            // property
            {"11a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\""},
            {"11b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"11c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"11d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Command_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name)
    {
        return new Command_mxJPO(_name);
    }
}
