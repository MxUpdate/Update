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
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" mime \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" mime \"\""},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" mime \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" mime \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" mime \"\""},
            {"2b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" mime \"\"",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" mime \"\""},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" mime \"\" "},
            {"3b) description not defined",
                    "description \"\" mime \"\"",
                    "                 mime \"\" "},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" mime \"\""},
            {"3d) tab's in description",
                    "",
                    "description \"abc\tdef\" mime \"\""},
            // hidden flag
            {"4a) hidden",
                    "",
                    "description \"\" hidden mime \"\" "},
            {"4b) not hidden (not defined)",
                    "description \"\"         mime \"\" ",
                    "description \"\" !hidden mime \"\" "},
            // mime
            {"5a) mime",
                    "",
                    "description \"\" mime \"side\""},
            {"5b) empty definition",
                    "description \"\" mime \"\" ",
                    "description \"\"           "},
            // content
            {"6a) content",
                    "",
                    "description \"\" mime \"\" content \" side \""},
            {"6b) empty definition",
                    "description \"\" mime \"\"              ",
                    "description \"\" mime \"\" content \"\" "},
            // file
            {"7) file (as content)",
                    "description \"\" mime \"\" content \"This is a page test.\"",
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
