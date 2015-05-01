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
@Test()
public class RoleCI_1ParserTest
    extends AbstractParserTest<Role_mxJPO>
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
                    "symbolicname \"role_abc\" description \"\" !hidden"},
            {"1b) two symbolic names",
                    "symbolicname \"role_abc\" symbolicname \"role_def\" description \"\" !hidden",
                    "symbolicname \"role_def\" symbolicname \"role_abc\" description \"\" !hidden"},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"2b) description not defined",
                    "description \"\" !hidden",
                    "!hidden"},
            // kind
            {"3a) kind organization",
                    "",
                    "description \"\" kind organization !hidden"},
            {"3b) kind project",
                    "",
                    "description \"\" kind project !hidden"},
            {"3c) kind role",
                    "description \"\" !hidden",
                    "description \"\" kind role !hidden"},
            // hidden flag
            {"4a) hidden",
                    "",
                    "description \"\" hidden"},
            {"4b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\""},
            // site
            {"5a) site",
                    "",
                    "description \"\" !hidden site \"side\""},
            {"5b) empty site",
                    "description \"\" !hidden",
                    "description \"\" !hidden site \"\""},
            // parent roles
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

    @Override()
    protected Role_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Role_mxJPO(_paramCache.getMapping().getTypeDef(CI.USR_ROLE.updateType), _name);
    }
}
