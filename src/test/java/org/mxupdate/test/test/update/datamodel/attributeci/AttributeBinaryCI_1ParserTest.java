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
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "kind binary uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "kind binary uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            {"1c) uuid convert from single to string",
                    "kind binary uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "kind binary symbolicname \"attribute_abc\" description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            {"2b) two symbolic names",
                    "kind binary symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !resetonclone !resetonrevision default \"\""},
            // description
            {"3a) description",
                    "",
                    "kind binary description \"abc def\" !hidden !resetonclone !resetonrevision default \"\""},
            {"3b) description not defined",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary                  !hidden !resetonclone !resetonrevision default \"\""},
            {"3c) multi-line description",
                    "",
                    "kind binary description \"abc\ndef\" !hidden !resetonclone !resetonrevision default \"\""},
            {"3d) tab's in description",
                    "",
                    "kind binary description \"abc\\tdef\" !hidden !resetonclone !resetonrevision default \"\""},
            // hidden flag
            {"4a) hidden",
                    "",
                    "kind binary description \"\" hidden !resetonclone !resetonrevision default \"\""},
            {"4b) not hidden (not defined)",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\"         !resetonclone !resetonrevision default \"\""},
            // multivalue flag must be ignored
            {"5a) multivalue defined, but must be ignored",
                    "kind binary description \"\" !hidden             !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\""},
            {"5b) multivalue defined, but must be ignored",
                    "kind binary description \"\" !hidden             !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden  multivalue !resetonclone !resetonrevision default \"\""},
            // resetonclone flag
            {"6a) resetonclone",
                    "",
                    "kind binary description \"\" !hidden resetonclone  !resetonrevision default \"\""},
            {"6b) not resetonclone not defined",
                    "kind binary description \"\" !hidden !resetonclone !resetonrevision default \"\"",
                    "kind binary description \"\" !hidden               !resetonrevision default \"\""},
            // resetonrevision flag
            {"7a) resetonrevision",
                    "",
                    "kind binary description \"\" !hidden !resetonclone resetonrevision default \"\""},
            {"7b) not resetonrevision not defined",
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
