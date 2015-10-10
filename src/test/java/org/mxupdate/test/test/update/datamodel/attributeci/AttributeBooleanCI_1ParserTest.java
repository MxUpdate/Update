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

package org.mxupdate.test.test.update.datamodel.attributeci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AttributeCI_mxJPO boolean attribute CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeBooleanCI_1ParserTest
    extends AbstractParserTest<AttributeCI_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0a) simple",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"0b) simple w/o anything to test default values",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean"},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "kind boolean uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "kind boolean uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"1c) uuid convert from single to string",
                    "kind boolean uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean uuid FDA7-5674979211-E6AE2256B6-B6499611      description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "kind boolean symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"2b) two symbolic names",
                    "kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            // description
            {"3a) description",
                    "",
                    "kind boolean description \"abc def\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"3b) description not defined",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean                  !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"3c) multi-line description",
                    "",
                    "kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"3d) tab's in description",
                    "",
                    "kind boolean description \"abc\\tdef\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            // hidden flag
            {"4a) hidden",
                    "",
                    "kind boolean description \"\" hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"4b) not hidden (not defined)",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\""},
            // multivalue flag
            {"5a) multivalue",
                    "",
                    "kind boolean description \"\" !hidden multivalue !resetonclone !resetonrevision default \"\""},
            {"5b) not multivalue not defined",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\""},
            // resetonclone flag
            {"6a) resetonclone",
                    "",
                    "kind boolean description \"\" !hidden !multivalue resetonclone !resetonrevision default \"\""},
            {"6b) not resetonclone not defined",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean description \"\" !hidden !multivalue                !resetonrevision default \"\""},
            // resetonrevision flag
            {"7a) resetonrevision",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision default \"\""},
            {"7b) not resetonrevision not defined",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\""},
            // default value
            {"10a) default value",
                    "",
                    "kind boolean description \"\" hidden !multivalue !resetonclone !resetonrevision default \"abc\""},
            {"10b) default value with new line",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\""},
            {"10c) default value with apostrophe",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\\\"def\""},
            {"10d) default value with \\n to test replaced by newline",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\"",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\\ndef\""},
            {"10e) default value with \\\\ to test replaced by \\",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\\\\def\""},
            {"10f) default value with \\{ to test replaced by {",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc{}def\"",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\\{\\}def\""},
            // action trigger
            {"20a) action trigger with input",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\""},
            {"20b) action trigger w/o input",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\"",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\""},
            // check trigger
            {"21a) check trigger with input",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\""},
            {"21b) check trigger w/o input",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\"",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\""},
            // override trigger
            {"22a) override trigger with input",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\""},
            {"22b) override trigger w/o input",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\"",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\""},
            // property
            {"23a) property special characters",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\""},
            {"23b) property and value special characters",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"23c) property link special characters",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"23d) property link and value special characters",
                    "",
                    "kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected AttributeCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new AttributeCI_mxJPO(_name);
    }
}
