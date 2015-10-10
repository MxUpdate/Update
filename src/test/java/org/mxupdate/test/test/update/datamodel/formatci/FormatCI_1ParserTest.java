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

package org.mxupdate.test.test.update.datamodel.formatci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Format_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Format_mxJPO format CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class FormatCI_1ParserTest
    extends AbstractParserTest<Format_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
            {"0) simple",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"format_abc\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"2b) two symbolic names",
                    "symbolicname \"format_abc\" symbolicname \"format_def\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "symbolicname \"format_def\" symbolicname \"format_abc\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"3b) description not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "                 !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"3d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // hidden
            {"4a) not hidden",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\"         mime \"\" suffix \"\" type \"\" version \"\""},
            {"4b) hidden",
                    "",
                    "description \"\" hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // mime
            {"5a) mime",
                    "",
                    "description \"\" !hidden mime \"abc\" suffix \"\" type \"\" version \"\""},
            {"5b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden           suffix \"\" type \"\" version \"\""},
            // suffix
            {"6a) mime",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"abc\" type \"\" version \"\""},
            {"6b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden mime \"\"             type \"\" version \"\""},
            // type
            {"7a) mime",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"abc\" version \"\""},
            {"7b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden mime \"\" suffix \"\"           version \"\""},
            // version
            {"8a) mime",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"abc\""},
            {"8b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\"             "},
            // property
            {"9a) property special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\""},
            {"9b) property and value special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"9c) property link special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"9d) property link and value special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
            {"9e) two properties",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"1\" property \"2\" value \"value\" "},
        };
    }

    @Override
    protected Format_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Format_mxJPO(_name);
    }
}
