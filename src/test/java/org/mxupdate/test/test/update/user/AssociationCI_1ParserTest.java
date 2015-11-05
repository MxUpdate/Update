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
import org.mxupdate.update.user.Association_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Association_mxJPO association CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class AssociationCI_1ParserTest
    extends AbstractParserTest<Association_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" !hidden definition \"\""},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" !hidden definition \"\""},
            {"1b) package single",
                    "package \"abc\" description \"\" !hidden definition \"\"",
                    "package abc     description \"\" !hidden definition \"\""},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden definition \"\""},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden definition \"\""},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden definition \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden definition \"\""},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"association_abc\" description \"\" !hidden definition \"\""},
            {"3b) two symbolic names",
                    "symbolicname \"association_abc\" symbolicname \"association_def\" description \"\" !hidden definition \"\"",
                    "symbolicname \"association_def\" symbolicname \"association_abc\" description \"\" !hidden definition \"\""},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" !hidden definition \"\""},
            {"4b) description not defined",
                    "description \"\" !hidden definition \"\"",
                    "                 !hidden definition \"\""},
            // hidden flag
            {"5a) hidden",
                    "",
                    "description \"\" hidden definition \"\""},
            {"5b) not hidden (not defined)",
                    "description \"\" !hidden definition \"\"",
                    "description \"\"         definition \"\""},
            // definition
            {"6a) site",
                    "",
                    "description \"\" !hidden definition \"side\""},
            {"6b) empty definition",
                    "description \"\" !hidden definition \"\"",
                    "description \"\" !hidden "},
            // property
            {"7a) property special characters",
                    "",
                    "description \"\" !hidden definition \"\" property \"{}\\\"\""},
            {"7b) property and value special characters",
                    "",
                    "description \"\" !hidden definition \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"7c) property link special characters",
                    "",
                    "description \"\" !hidden definition \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"7d) property link and value special characters",
                    "",
                    "description \"\" !hidden definition \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Association_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new Association_mxJPO(_name);
    }
}
