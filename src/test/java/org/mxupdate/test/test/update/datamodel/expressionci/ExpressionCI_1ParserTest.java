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
@Test
public class ExpressionCI_1ParserTest
    extends AbstractParserTest<Expression_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
            {"0) simple",
                    "",
                    "description \"\" !hidden value \"\""},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" !hidden value \"\""},
            {"1b) package single",
                    "package \"abc\" description \"\" !hidden value \"\"",
                    "package abc     description \"\" !hidden value \"\""},
            {"1c) package string with property (to check expression value is correct set)",
                    "                  package \"abc\"               description \"\" !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" package \"abc\" value \"abc\" description \"\" !hidden"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden value \"\""},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden value \"\""},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden value \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden value \"\""},
            {"2d) uuid with property (to check expression value is correct set)",
                    "                  uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"               description \"\" !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" uuid \"FDA7-5674979211-E6AE2256B6-B6499611\" value \"abc\" description \"\" !hidden"},
            {"2e) uuid single with property (to check expression value is correct set)",
                    "                  uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"               description \"\" !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" uuid   FDA7-5674979211-E6AE2256B6-B6499611   value \"abc\" description \"\" !hidden"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"expression_abc\" description \"\" !hidden value \"\""},
            {"3b) two symbolic names",
                    "symbolicname \"expression_abc\" symbolicname \"expression_def\" description \"\" !hidden value \"\"",
                    "symbolicname \"expression_def\" symbolicname \"expression_abc\" description \"\" !hidden value \"\""},
            {"3c) symbolic name with property (to check expression value is correct set)",
                    "                  symbolicname \"expression_abc\"               description \"\" !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" symbolicname \"expression_abc\" value \"abc\" description \"\" !hidden"},
            {"3d) symbolic name single with property (to check expression value is correct set)",
                    "                  symbolicname \"expression_abc\"               description \"\" !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" symbolicname expression_abc     value \"abc\" description \"\" !hidden"},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" !hidden value \"\""},
            {"4b) description not defined",
                    "description \"\" !hidden value \"\"",
                    "                 !hidden value \"\""},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden value \"\""},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" !hidden value \"\""},
            {"4e) description with property (to check expression value is correct set)",
                    "                  description \"abcdef\"               !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" description \"abcdef\" value \"abc\" !hidden"},
            {"4f) description single with property (to check expression value is correct set)",
                    "                  description \"abcdef\"               !hidden value \"abc\" property \"prop\"",
                    "property \"prop\" description abcdef     value \"abc\" !hidden"},
            // hidden
            {"5a) not hidden",
                    "description \"\" !hidden value \"\"",
                    "description \"\"         value \"\""},
            {"5b) hidden",
                    "",
                    "description \"\" hidden value \"\""},
            {"5c) not hidden with property (to check expression value is correct set)",
                    "description \"\"                   !hidden value \"abc\" property \"prop\"",
                    "description \"\" property \"prop\" !hidden value \"abc\""},
            {"5d) hidden with property (to check expression value is correct set)",
                    "description \"\"                   hidden value \"abc\" property \"prop\"",
                    "description \"\" property \"prop\" hidden value \"abc\""},
            // value
            {"6a) value",
                    "",
                    "description \"\" !hidden value \"abc def\""},
            {"6b) value not defined",
                    "description \"\" !hidden value \"\"",
                    "description \"\" !hidden           "},
            {"6c) multi-line value",
                    "",
                    "description \"\" !hidden value \"abc\ndef\""},
            {"6d) value with property (to check expression value is correct set)",
                    "description \"\" !hidden                                 value \"\" property \"prop\" value \"abc\"",
                    "description \"\" !hidden property \"prop\" value \"abc\" value \"\""},
            {"6e) value single with property (to check expression value is correct set)",
                    "description \"\" !hidden                             value \"\" property \"prop\" value \"abc\"",
                    "description \"\" !hidden property \"prop\" value abc value \"\""},
            // property
            {"7a) property special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\""},
            {"7b) property and value special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"7c) property link special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"7d) property link and value special characters",
                "",
                "description \"\" !hidden value \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
            {"7e) two properties",
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
