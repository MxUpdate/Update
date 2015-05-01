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

import org.mxupdate.test.update.AbstractParserTest;
import org.mxupdate.update.program.Page_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Page_mxJPO page CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PageCI_ParserTest
    extends AbstractParserTest<Page_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                "",
                "description \"\" mime \"\" "},
            // description
            {"2a) description",
                "",
                "description \"abc def\" mime \"\" "},
            {"2b) description not defined",
                "description \"\" mime \"\"",
                "                 mime \"\" "},
            // hidden flag
            {"3a) hidden",
                "",
                "description \"\" hidden mime \"\" "},
            {"3b) not hidden (not defined)",
                "description \"\"         mime \"\" ",
                "description \"\" !hidden mime \"\" "},
            // mime
            {"4a) mime",
                "",
                "description \"\" mime \"side\""},
            {"4b) empty definition",
                "description \"\" mime \"\" ",
                "description \"\"           "},
            // content
            {"5a) content",
                "",
                "description \"\" mime \"\" content \" side \""},
            {"5b) empty definition",
                "description \"\" mime \"\"              ",
                "description \"\" mime \"\" content \"\" "},
            // property
            {"6a) property special characters",
                "",
                "description \"\" mime \"\" property \"{}\\\"\""},
            {"6b) property and value special characters",
                "",
                "description \"\" mime \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"6c) property link special characters",
                "",
                "description \"\" mime \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"6d) property link and value special characters",
                "",
                "description \"\" mime \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Page_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Page_mxJPO(_paramCache.getMapping().getTypeDef(CI.PRG_PAGE.updateType), _name);
    }
}
