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
@Test()
public class GroupCI_1ParserTest
    extends AbstractParserTest<Group_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0) simple",
                    "",
                    "description \"\" !hidden"},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"group_abc\" description \"\" !hidden"},
            {"1b) two symbolic names",
                    "symbolicname \"group_abc\" symbolicname \"group_def\" description \"\" !hidden",
                    "symbolicname \"group_def\" symbolicname \"group_abc\" description \"\" !hidden"},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"2b) description not defined",
                    "description \"\" !hidden",
                    "!hidden"},
            {"2c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            // hidden flag
            {"3a) hidden",
                    "",
                    "description \"\" hidden"},
            {"3b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\""},
            // description
            {"4a) site",
                    "",
                    "description \"\" !hidden site \"side\""},
            {"4b) empty site",
                    "description \"\" !hidden",
                    "description \"\" !hidden site \"\""},
            // parent groups
            {"5a) parent",
                    "",
                    "description \"\" !hidden parent \"111\""},
            {"5b) parent name w/o apostrophe",
                    "description \"\" !hidden parent \"111\"",
                    "description \"\" !hidden parent 111"},
            {"5c) two parents (to check sort)",
                    "description \"\" !hidden parent \"111\" parent \"222\"",
                    "description \"\" !hidden parent \"222\" parent \"111\""},
            // property
            {"6a) property special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\""},
            {"6b) property and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"6c) property link special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"6d) property link and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Group_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                        final String _name)
    {
        return new Group_mxJPO(_paramCache.getMapping().getTypeDef("Group"), _name);
    }
}
