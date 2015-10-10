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

package org.mxupdate.test.test.update.user;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.user.Group_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Group_mxJPO group CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class GroupCI_1ParserTest
    extends AbstractParserTest<Group_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0) simple",
                    "",
                    "description \"\" !hidden"},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden"},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden"},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden"},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"group_abc\" description \"\" !hidden"},
            {"2b) two symbolic names",
                    "symbolicname \"group_abc\" symbolicname \"group_def\" description \"\" !hidden",
                    "symbolicname \"group_def\" symbolicname \"group_abc\" description \"\" !hidden"},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"3b) description not defined",
                    "description \"\" !hidden",
                    "!hidden"},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            // hidden flag
            {"4a) hidden",
                    "",
                    "description \"\" hidden"},
            {"4b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\""},
            // description
            {"5a) site",
                    "",
                    "description \"\" !hidden site \"side\""},
            {"5b) empty site",
                    "description \"\" !hidden",
                    "description \"\" !hidden site \"\""},
            // parent groups
            {"6a) parent",
                    "",
                    "description \"\" !hidden parent \"111\""},
            {"6b) parent name w/o apostrophe",
                    "description \"\" !hidden parent \"111\"",
                    "description \"\" !hidden parent 111"},
            {"6c) two parents (to check sort)",
                    "description \"\" !hidden parent \"111\" parent \"222\"",
                    "description \"\" !hidden parent \"222\" parent \"111\""},
            // property
            {"7a) property special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\""},
            {"7b) property and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"7c) property link special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"7d) property link and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Group_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                        final String _name)
    {
        return new Group_mxJPO(_name);
    }
}
