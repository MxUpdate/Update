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
import org.mxupdate.update.userinterface.Channel_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Channel_mxJPO channel CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class ChannelCI_1ParserTest
    extends AbstractParserTest<Channel_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" height 0"},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" label \"\" href \"\" alt \"\" height 0"},
            {"1b) package single",
                    "package \"abc\" description \"\" label \"\" href \"\" alt \"\" height 0",
                    "package abc     description \"\" label \"\" href \"\" alt \"\" height 0"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" label \"\" href \"\" alt \"\" height 0"},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" label \"\" href \"\" alt \"\" height 0"},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" label \"\" href \"\" alt \"\" height 0",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" label \"\" href \"\" alt \"\" height 0"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" label \"\" href \"\" alt \"\" height 0"},
            {"3b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" label \"\" href \"\" alt \"\" height 0",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" label \"\" href \"\" alt \"\" height 0"},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" label \"\" href \"\" alt \"\" height 0"},
            {"4b) description not defined",
                    "description \"\" label \"\" href \"\" alt \"\" height 0",
                    "                 label \"\" href \"\" alt \"\" height 0"},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" label \"\" href \"\" alt \"\" height 0"},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" label \"\" href \"\" alt \"\" height 0"},
            // hidden
            {"5a) not hidden",
                    "description \"\"         label \"\" href \"\" alt \"\" height 0",
                    "description \"\" !hidden label \"\" href \"\" alt \"\" height 0"},
            {"5b) hidden",
                    "",
                    "description \"\" hidden label \"\" href \"\" alt \"\" height 0"},
            // label
            {"6a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\" height 0"},
            {"6b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\" height 0",
                    "description \"\"            href \"\" alt \"\" height 0"},
            // href
            {"7a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\" height 0"},
            {"7b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\" height 0",
                    "description \"\" label \"\"           alt \"\" height 0"},
            // alt
            {"8a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\" height 0"},
            {"8b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\" height 0",
                    "description \"\" label \"\" href \"\"          height 0"},
            // height
            {"9a) height",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" height 123"},
            {"9b) height not defined",
                    "description \"\" label \"\" href \"\" alt \"\" height 0",
                    "description \"\" label \"\" href \"\" alt \"\"        "},
            // setting
            {"10a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"Key\" \"Value\""},
            {"10b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"\" \"Value\""},
            {"10c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"Key\" \"\""},
            {"10d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"key2\" \"value\" setting \"key1\" \"value\""},
            {"10e) multiple setting with mixed old add-syntaxs",
                "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"key1\" \"value\" setting \"key2\" \"value\"",
                "description \"\" label \"\" href \"\" alt \"\"  height 0 setting \"key2\" \"value\" add setting \"key1\" \"value\""},
            // children
            {"11a) child command",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 command \"123\" "},
            {"11c) multiple children",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 command \"123\" command \"123\" command \"234\" command \"123\""},
            {"11d) multiple children with mixed old add-syntax",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 command \"123\" command \"123\" command \"234\" command \"123\"",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 add command \"123\" add command \"123\" command \"234\" command \"123\""},
            // property
            {"12a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 property \"{}\\\"\""},
            {"12b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 property \"{}\\\"\" value \"{}\\\"\""},
            {"12c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 property \"{}\\\"\" to type \"{}\\\"\""},
            {"12d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  height 0 property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Channel_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name)
    {
        return new Channel_mxJPO(_name);
    }
}
