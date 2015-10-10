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
 * Tests the {@link AttributeCI_mxJPO date attribute CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeDateCI_1ParserTest
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
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"0b) simple w/o anything to test default values",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date"},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "kind date uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "kind date uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"1c) uuid convert from single to string",
                    "kind date uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date uuid FDA7-5674979211-E6AE2256B6-B6499611      description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "kind date symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"2b) two symbolic names",
                    "kind date symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // description
            {"3a) description",
                    "",
                    "kind date description \"abc def\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"3b) description not defined",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date                  !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"3c) multi-line description",
                    "",
                    "kind date description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"3d) tab's in description",
                    "",
                    "kind date description \"abc\\tdef\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // hidden flag
            {"4a) hidden",
                    "",
                    "kind date description \"\" hidden  !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"4b) not hidden (not defined)",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date description \"\"         !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // multivalue flag
            {"5a) multivalue",
                    "",
                    "kind date description \"\" !hidden multivalue  !resetonclone !resetonrevision !rangevalue default \"\""},
            {"5b) not multivalue not defined",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date description \"\" !hidden             !resetonclone !resetonrevision !rangevalue default \"\""},
            // resetonclone flag
            {"6a) resetonclone",
                    "",
                    "kind date description \"\" !hidden !multivalue resetonclone  !resetonrevision !rangevalue default \"\""},
            {"6b) not resetonclone not defined",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date description \"\" !hidden !multivalue               !resetonrevision !rangevalue default \"\""},
            // resetonrevision flag
            {"7a) resetonrevision",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone resetonrevision  !rangevalue default \"\""},
            {"7b) not resetonrevision not defined",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone                  !rangevalue default \"\""},
            // rangevalue flag
            {"8a) rangevalue",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"8b) not rangevalue not defined",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision  default \"\""},
            // default value
            {"10a) default value",
                    "",
                    "kind date description \"\" hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\""},
            {"10b) default value with new line",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\ndef\""},
            {"10c) default value with apostrophe",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\\"def\""},
            {"10d) default value with \\n to test replaced by newline",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\ndef\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\ndef\""},
            {"10e) default value with \\\\ to test replaced by \\",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\\\def\""},
            {"10f) default value with \\{ to test replaced by {",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc{}def\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\{\\}def\""},
            // action trigger
            {"20a) action trigger with input",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\""},
            {"20b) action trigger w/o input",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify action \"{}\\\"\" input \"\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify action \"{}\\\"\""},
            // check trigger
            {"21a) check trigger with input",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\""},
            {"21b) check trigger w/o input",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify check \"{}\\\"\" input \"\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify check \"{}\\\"\""},
            // override trigger
            {"22a) override trigger with input",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\""},
            {"22b) override trigger w/o input",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify override \"{}\\\"\" input \"\"",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify override \"{}\\\"\""},
            // property
            {"23a) property special characters",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\""},
            {"23b) property and value special characters",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"23c) property link special characters",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"23d) property link and value special characters",
                    "",
                    "kind date description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected AttributeCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new AttributeCI_mxJPO(_name);
    }
}
