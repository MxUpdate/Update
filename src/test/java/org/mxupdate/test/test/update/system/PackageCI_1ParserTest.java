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

package org.mxupdate.test.test.update.system;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.system.PackageCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PackageCI_mxJPO package} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PackageCI_1ParserTest
    extends AbstractParserTest<PackageCI_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                "",
                "description \"\" !hidden !custom"},
            // description
            {"2a) description",
                "",
                "description \"abc def\" !hidden !custom"},
            {"2b) description not defined",
                "description \"\" !hidden !custom",
                "!hidden !custom"},
            // hidden flag
            {"3a) hidden",
                "",
                "description \"\" hidden !custom"},
            {"3b) not hidden (not defined)",
                    "description \"\" !hidden !custom",
                "description \"\" !custom"},
            // custom flag
            {"4a) custom",
                "",
                "description \"\" !hidden custom"},
            {"4b) not custom (not defined)",
                "description \"\" !hidden !custom",
                "description \"\" !hidden"},
            // usespackage
            {"5a) usespackage",
                "",
                "description \"\" !hidden !custom "
                        + "usespackage \"111\""},
            {"5b) usespackage name w/o apostrophe",
                "description \"\" !hidden !custom "
                        + "usespackage \"111\"",
                "description \"\" !hidden !custom "
                        + "usespackage 111"},
            {"5c) two usespackage (to check sort)",
                "description \"\" !hidden !custom "
                        + "usespackage \"111\" "
                        + "usespackage \"222\"",
                "description \"\" !hidden !custom "
                        + "usespackage \"222\" "
                        + "usespackage \"111\""},
            // member list
            {"6a) member",
                "",
                "description \"\" !hidden !custom "
                        + "member type \"111\""},
            {"6b) member name w/o apostrophe",
                "description \"\" !hidden !custom "
                        + "member type \"111\"",
                "description \"\" !hidden !custom "
                        + "member type 111"},
            {"6c) two members from same admin type (to check sort)",
                "description \"\" !hidden !custom "
                        + "member type \"111\" "
                        + "member type \"222\"",
                "description \"\" !hidden !custom "
                        + "member type \"222\" "
                        + "member type \"111\""},
            {"6d) two members from different admin type (to check sort)",
                "description \"\" !hidden !custom "
                        + "member relationship \"111\" "
                        + "member type \"111\"",
                "description \"\" !hidden !custom "
                        + "member type \"111\" "
                        + "member relationship \"111\""},
            {"6e) new unknown member type (to check all kind of member admin types are allowed)",
                "",
                "description \"\" !hidden !custom "
                        + "member unknown \"111\""},
            // properties
            {"7a) property",
                "",
                "description \"\" !hidden !custom property \"111\""},
            {"7b) property with value",
                "",
                "description \"\" !hidden !custom property \"111\" value \"222\""},
            {"7c) property with referenced admin object",
                "",
                "description \"\" !hidden !custom property \"111\" to type \"TestType\""},
            {"7d) property with referenced admin object and value",
                "",
                "description \"\" !hidden !custom property \"111\" to type \"TestType\" value \"222\""},
            // two properties for sorting
            {"8a) sorting property",
                 "description \"\" !hidden !custom property \"111\" property \"222\"",
                "description \"\" !hidden !custom  property \"222\" property \"111\""},
            {"8b) sorting property with value",
                "description \"\" !hidden !custom property \"111\" value \"222\" property \"111\" value \"333\"",
                "description \"\" !hidden !custom property \"111\" value \"333\" property \"111\" value \"222\""},
            {"8c) sorting  property with referenced admin object",
                "description \"\" !hidden !custom property \"111\" to type \"TestType1\" property \"111\" to type \"TestType2\"",
                "description \"\" !hidden !custom property \"111\" to type \"TestType2\" property \"111\" to type \"TestType1\""},
            {"8d) sorting  property with referenced admin object and value",
                "description \"\" !hidden !custom property \"111\" to type \"TestType\" value \"222\" property \"111\" to type \"TestType\" value \"333\"",
                "description \"\" !hidden !custom property \"111\" to type \"TestType\" value \"333\" property \"111\" to type \"TestType\" value \"222\""},
         };
    }

    @Override()
    protected PackageCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new PackageCI_mxJPO(_paramCache.getMapping().getTypeDef("SystemPackage"), _name);
    }
}
