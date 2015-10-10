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
 * Tests the {@link AttributeCI_mxJPO binary attribute CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeBinaryCI_1ParserTest
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
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            {"0b) simple w/o anything to test default values",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary"},
            // registered name
            {"1a) symbolic name",
                    "",
                    "kind binary symbolicname \"attribute_abc\" description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            {"1b) two symbolic names",
                    "kind binary symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            // description
            {"2a) description",
                    "",
                    "kind binary description \"abc def\" !hidden !resetonclone !resetonrevision default \"\""},
            {"2b) description not defined",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary                  !hidden !resetonclone !resetonrevision default \"\""},
            {"2c) multi-line description",
                    "",
                    "kind binary description \"abc\ndef\" !hidden !resetonclone !resetonrevision default \"\""},
            {"2d) tab's in description",
                    "",
                    "kind binary description \"abc\tdef\" !hidden !resetonclone !resetonrevision default \"\""},
            // hidden flag
            {"3a) hidden",
                    "",
                    "kind binary description \"\" hidden !resetonclone !resetonrevision default \"\""},
            {"3b) not hidden (not defined)",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\"         !resetonclone !resetonrevision default \"\""},
            // multivalue flag must be ignored
            {"4a) multivalue defined, but must be ignored",
                    "kind binary description \"\" !hidden             !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"4b) multivalue defined, but must be ignored",
                    "kind binary description \"\" !hidden             !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden  multivalue !resetonclone !resetonrevision default \"\""},
            // resetonclone flag
            {"5a) resetonclone",
                    "",
                    "kind binary description \"\" !hidden resetonclone  !resetonrevision default \"\""},
            {"5b) not resetonclone not defined",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden               !resetonrevision default \"\""},
            // resetonrevision flag
            {"6a) resetonrevision",
                    "",
                    "kind binary description \"\" !hidden !resetonclone resetonrevision default \"\""},
            {"6b) not resetonrevision not defined",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden !resetonclone                  default \"\""},
            // action trigger
            {"20a) action trigger with input",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\""},
            {"20b) action trigger w/o input",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\"",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\""},
            // check trigger
            {"21a) check trigger with input",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\""},
            {"22b) check trigger w/o input",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\"",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\""},
            // override trigger
            {"23a) override trigger with input",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\""},
            {"23b) override trigger w/o input",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\"",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\""},
            // property
            {"24a) property special characters",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" property \"{}\\\"\""},
            {"24b) property and value special characters",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"25c) property link special characters",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"25d) property link and value special characters",
                    "",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected AttributeCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new AttributeCI_mxJPO(_name);
    }
}
