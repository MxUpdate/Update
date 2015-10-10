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

package org.mxupdate.test.test.update.datamodel.expressionci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Expression_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Expression_mxJPO expression CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class ExpressionCI_1ParserTest
    extends AbstractParserTest<Expression_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
            {"0) simple",
                "",
                "description \"\" !hidden value \"\""},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"expression_abc\" description \"\" !hidden value \"\""},
            {"1b) two symbolic names",
                    "symbolicname \"expression_abc\" symbolicname \"expression_def\" description \"\" !hidden value \"\"",
                    "symbolicname \"expression_def\" symbolicname \"expression_abc\" description \"\" !hidden value \"\""},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" !hidden value \"\""},
            {"2b) description not defined",
                    "description \"\" !hidden value \"\"",
                    "                 !hidden value \"\""},
            {"2c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden value \"\""},
            {"2d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden value \"\""},
            // hidden
            {"3a) not hidden",
                    "description \"\" !hidden value \"\"",
                    "description \"\"         value \"\""},
            {"3b) hidden",
                    "",
                    "description \"\" hidden value \"\""},
            // value
            {"4a) value",
                    "",
                    "description \"\" !hidden value \"abc def\""},
            {"4b) value not defined",
                    "description \"\" !hidden value \"\"",
                    "description \"\" !hidden           "},
            {"4c) multi-line value",
                "",
                "description \"\" !hidden value \"abc\ndef\""},
            // property
            {"8a) property special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\""},
            {"8b) property and value special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"8c) property link special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"8d) property link and value special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
            {"8e) two properties",
                "",
                "description \"\" !hidden value \"\" property \"1\" property \"2\" value \"value\" "},
        };
    }

    @Override
    protected Expression_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new Expression_mxJPO(_name);
    }
}
