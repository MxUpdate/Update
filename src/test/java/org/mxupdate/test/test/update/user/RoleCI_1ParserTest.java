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
import org.mxupdate.update.user.Role_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Role_mxJPO role CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class RoleCI_1ParserTest
    extends AbstractParserTest<Role_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
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
                    "symbolicname \"role_abc\" description \"\" !hidden"},
            {"2b) two symbolic names",
                    "symbolicname \"role_abc\" symbolicname \"role_def\" description \"\" !hidden",
                    "symbolicname \"role_def\" symbolicname \"role_abc\" description \"\" !hidden"},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"3b) description not defined",
                    "description \"\" !hidden",
                    "                 !hidden"},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            // kind
            {"4a) kind organization",
                    "",
                    "kind organization description \"\" !hidden"},
            {"4b) kind project",
                    "",
                    "kind project description \"\" !hidden"},
            {"4c) kind role",
                    "          description \"\" !hidden",
                    "kind role description \"\" !hidden"},
            // hidden flag
            {"5a) hidden",
                    "",
                    "description \"\" hidden"},
            {"5b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\"        "},
            // site
            {"6a) site",
                    "",
                    "description \"\" !hidden site \"side\""},
            {"6b) empty site",
                    "description \"\" !hidden",
                    "description \"\" !hidden site \"\""},
            // parent roles
            {"7a) parent",
                    "",
                    "description \"\" !hidden parent \"111\""},
            {"7b) parent name w/o apostrophe",
                    "description \"\" !hidden parent \"111\"",
                    "description \"\" !hidden parent 111"},
            {"7c) two parents (to check sort)",
                    "description \"\" !hidden parent \"111\" parent \"222\"",
                    "description \"\" !hidden parent \"222\" parent \"111\""},
            // property
            {"8a) property special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\""},
            {"8b) property and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"8c) property link special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"8d) property link and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Role_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Role_mxJPO(_name);
    }
}
