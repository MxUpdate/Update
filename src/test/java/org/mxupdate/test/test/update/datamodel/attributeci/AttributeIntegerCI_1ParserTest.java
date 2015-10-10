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
 * Tests the {@link AttributeCI_mxJPO integer attribute CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeIntegerCI_1ParserTest
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
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"0b) simple w/o anything to test default values",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    ""},
            // registered name
            {"1a) symbolic name",
                    "",
                    "kind integer symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"1b) two symbolic names",
                    "kind integer symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // description
            {"2a) description",
                    "",
                    "kind integer description \"abc def\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"2b) description not defined",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer                  !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"2c) multi-line description",
                    "",
                    "kind integer description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"2d) tab's in description",
                    "",
                    "kind integer description \"abc\tdef\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // hidden flag
            {"3a) hidden",
                    "",
                    "kind integer description \"\" hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"3b) not hidden (not defined)",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer description \"\"         !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            // multivalue flag
            {"4a) multivalue",
                    "",
                    "kind integer description \"\" !hidden multivalue  !resetonclone !resetonrevision !rangevalue default \"\""},
            {"4b) not multivalue not defined",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer description \"\" !hidden             !resetonclone !resetonrevision !rangevalue default \"\""},
            // multivalue flag
            {"5a) resetonclone",
                    "",
                    "kind integer description \"\" !hidden !multivalue resetonclone  !resetonrevision !rangevalue default \"\""},
            {"5b) not resetonclone not defined",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer description \"\" !hidden !multivalue               !resetonrevision !rangevalue default \"\""},
            // resetonrevision flag
            {"6a) resetonrevision",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone resetonrevision  !rangevalue default \"\""},
            {"6b) not resetonrevision not defined",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone                  !rangevalue default \"\""},
            // rangevalue flag
            {"7a) rangevalue",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\""},
            {"7b) not rangevalue not defined",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision  default \"\""},
            // dimension
            {"8a) dimension",
                    "",
                    "kind integer description \"abc def\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"123\" default \"\""},
            {"8b) empty dimension",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"\" default \"\""},
            // default value
            {"9a) default value",
                    "",
                    "kind integer description \"\" hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\""},
            {"9b) default value with new line",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\ndef\""},
            {"9c) default value with apostrophe",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\\"def\""},
            {"9d) default value with \\n to test replaced by newline",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\ndef\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\ndef\""},
            {"9e) default value with \\\\ to test replaced by \\",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\\\def\""},
            {"9f) default value with \\{ to test replaced by {",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc{}def\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"abc\\{\\}def\""},
            // action trigger
            {"20a) action trigger with input",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\""},
            {"20b) action trigger w/o input",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify action \"{}\\\"\" input \"\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify action \"{}\\\"\""},
            // check trigger
            {"21a) check trigger with input",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\""},
            {"21b) check trigger w/o input",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify check \"{}\\\"\" input \"\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify check \"{}\\\"\""},
            // override trigger
            {"22a) override trigger with input",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\""},
            {"22b) override trigger w/o input",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify override \"{}\\\"\" input \"\"",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" trigger modify override \"{}\\\"\""},
            // property
            {"23a) property special characters",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\""},
            {"23b) property and value special characters",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"23c) property link special characters",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"23d) property link and value special characters",
                    "",
                    "kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected AttributeCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new AttributeCI_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_ATTRIBUTE_INTEGER.updateType), _name);
    }
}
