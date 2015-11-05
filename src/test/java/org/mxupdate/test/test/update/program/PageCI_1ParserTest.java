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
import org.mxupdate.update.program.Page_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Page_mxJPO page CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class PageCI_1ParserTest
    extends AbstractParserTest<Page_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" mime \"\""},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" mime \"\""},
            {"1b) package single",
                    "package \"abc\" description \"\" mime \"\"",
                    "package abc     description \"\" mime \"\""},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" mime \"\""},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" mime \"\""},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" mime \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" mime \"\""},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" mime \"\""},
            {"3b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" mime \"\"",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" mime \"\""},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" mime \"\" "},
            {"4b) description not defined",
                    "description \"\" mime \"\"",
                    "                 mime \"\" "},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" mime \"\""},
            {"4d) tab's in description",
                    "description \"abc\\tdef\" mime \"\"",
                    "description \"abc\tdef\"  mime \"\""},
            // hidden flag
            {"5a) hidden",
                    "",
                    "description \"\" hidden mime \"\" "},
            {"5b) not hidden (not defined)",
                    "description \"\"         mime \"\" ",
                    "description \"\" !hidden mime \"\" "},
            // mime
            {"6a) mime",
                    "",
                    "description \"\" mime \"side\""},
            {"6b) empty definition",
                    "description \"\" mime \"\" ",
                    "description \"\"           "},
            // content
            {"7a) content",
                    "",
                    "description \"\" mime \"\" content \" side \""},
            {"7b) empty definition",
                    "description \"\" mime \"\"              ",
                    "description \"\" mime \"\" content \"\" "},
            // file
            {"8) file (must be removed)",
                    "description \"\" mime \"\"",
                    "file \"src/test/resources/program/page/test.page\""},
            // property
            {"20a) property special characters",
                    "",
                    "description \"\" mime \"\" property \"{}\\\"\""},
            {"20b) property and value special characters",
                    "",
                    "description \"\" mime \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"20c) property link special characters",
                    "",
                    "description \"\" mime \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"20d) property link and value special characters",
                    "",
                    "description \"\" mime \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Page_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Page_mxJPO(_name);
    }
}
