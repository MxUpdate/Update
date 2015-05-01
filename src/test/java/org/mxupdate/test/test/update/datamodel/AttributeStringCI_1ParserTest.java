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

import java.io.IOException;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.AbstractAttribute_mxJPO;
import org.mxupdate.update.datamodel.AbstractAttribute_mxJPO.Kind;
import org.mxupdate.update.datamodel.AttributeString_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AbstractAttribute_mxJPO attribute CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeStringCI_1ParserTest
    extends AbstractParserTest<AbstractAttribute_mxJPO<?>>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0a) simple",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"0b) simple w/o anything to test default values",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                ""},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"1b) two symbolic names",
                    "symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                    "symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            // description
            {"2a) description",
                "",
                "description \"abc def\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"2b) description not defined",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "!hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"2c) multi-line description",
                "",
                "description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            // hidden flag
            {"3a) hidden",
                "",
                "description \"\" hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"3b) not hidden (not defined)",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "description \"\" !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            // multivalue flag
            {"4a) multivalue",
                "",
                "description \"\" !hidden multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"4b) not multivalue not defined",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "description \"\" !hidden             !resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            // multivalue flag
            {"5a) multivalue",
                "",
                "description \"\" !hidden !multivalue resetonclone !resetonrevision !multiline maxlength 0 default \"\""},
            {"5b) not multivalue not defined",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "description \"\" !hidden !multivalue                !resetonrevision !multiline maxlength 0 default \"\""},
            // resetonrevision flag
            {"6a) multivalue",
                "",
                "description \"\" !hidden !multivalue !resetonclone resetonrevision !multiline maxlength 0 default \"\""},
            {"6b) not multivalue not defined",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "description \"\" !hidden !multivalue !resetonclone                  !multiline maxlength 0 default \"\""},
            // multiline flag
            {"7a) multivalue",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision  multiline maxlength 0 default \"\""},
            {"7b) not multivalue not defined",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\""},
            // maxlength
            {"8a) maxlength",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 123 default \"\""},
            {"8b) maxlength not defined",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline             default \"\""},
           // default value
            {"9a) default value",
                "",
                "description \"\" hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\""},
            {"9b) default value with new line",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\ndef\""},
            {"9c) default value with apostrophe",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\\\"def\""},
            {"9d) default value with \\n to test replaced by newline",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\ndef\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\\ndef\""},
            {"9e) default value with \\\\ to test replaced by \\",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\\\\def\""},
            {"9f) default value with \\{ to test replaced by {",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc{}def\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"abc\\{\\}def\""},
            // action trigger
            {"10a) action trigger with input",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\""},
            {"10b) action trigger w/o input",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify action \"{}\\\"\" input \"\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify action \"{}\\\"\""},
            // check trigger
            {"10a) check trigger with input",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\""},
            {"10b) check trigger w/o input",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify check \"{}\\\"\" input \"\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify check \"{}\\\"\""},
            // override trigger
            {"11a) override trigger with input",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\""},
            {"11b) override trigger w/o input",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify override \"{}\\\"\" input \"\"",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" trigger modify override \"{}\\\"\""},
            // property
            {"12a) property special characters",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" property \"{}\\\"\""},
            {"12b) property and value special characters",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"12c) property link special characters",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"12d) property link and value special characters",
                "",
                "description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected AbstractAttribute_mxJPO<?> createNewData(final ParameterCache_mxJPO _paramCache,
                                                  final String _name)
    {
        return new AbstractAttribute_mxJPO<AttributeString_mxJPO>(_paramCache.getMapping().getTypeDef("AttributeString"), _name, Kind.String)
        {
            @Override()
            protected void write(final ParameterCache_mxJPO _paramCache,
                                 final Appendable _out)
                throws IOException
            {
                super.write(_paramCache, _out);
            }
        };
    }
}
