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

package org.mxupdate.test.test.update.datamodel;

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
@Test()
public class FormatCI_1ParserTest
    extends AbstractParserTest<Format_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
            {"0) simple",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"format_abc\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"1b) two symbolic names",
                    "symbolicname \"format_abc\" symbolicname \"format_def\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "symbolicname \"format_def\" symbolicname \"format_abc\" description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},

            // description
            {"2a) description",
                    "",
                    "description \"abc def\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"2b) description not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "                 !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            {"2c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // hidden
            {"3a) not hidden",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\"         mime \"\" suffix \"\" type \"\" version \"\""},
            {"3b) hidden",
                    "",
                    "description \"\" hidden mime \"\" suffix \"\" type \"\" version \"\""},
            // mime
            {"4a) mime",
                    "",
                    "description \"\" !hidden mime \"abc\" suffix \"\" type \"\" version \"\""},
            {"4b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden           suffix \"\" type \"\" version \"\""},
            // suffix
            {"5a) mime",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"abc\" type \"\" version \"\""},
            {"5b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden mime \"\"             type \"\" version \"\""},
            // type
            {"6a) mime",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"abc\" version \"\""},
            {"6b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden mime \"\" suffix \"\"           version \"\""},
            // version
            {"7a) mime",
                    "",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"abc\""},
            {"7b) mime not defined",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\"",
                    "description \"\" !hidden mime \"\" suffix \"\" type \"\"             "},
            // property
            {"8a) property special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\""},
            {"8b) property and value special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"8c) property link special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"8d) property link and value special characters",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
            {"8e) two properties",
                "",
                "description \"\" !hidden mime \"\" suffix \"\" type \"\" version \"\" property \"1\" property \"2\" value \"value\" "},
        };
    }
  /*
    .append("    mime \"").append(StringUtil_mxJPO.convertUpdate(this.mimeType)).append("\"\n")
    .append("    suffix \"").append(StringUtil_mxJPO.convertUpdate(this.fileSuffix)).append("\"\n")
    .append("    type \"").append(StringUtil_mxJPO.convertUpdate(this.type)).append("\"\n")
    .append("    version \"").append(StringUtil_mxJPO.convertUpdate(this.version)).append("\"\n");
*/

    @Override()
    protected Format_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Format_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_FORMAT.updateType), _name);
    }
}
