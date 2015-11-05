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

package org.mxupdate.test.test.update.userinterface;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.userinterface.Menu_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Menu_mxJPO menu CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class MenuCI_1ParserTest
    extends AbstractParserTest<Menu_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\""},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" label \"\" href \"\" alt \"\""},
            {"1b) package single",
                    "package \"abc\" description \"\" label \"\" href \"\" alt \"\"",
                    "package abc     description \"\" label \"\" href \"\" alt \"\""},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" label \"\" href \"\" alt \"\""},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" label \"\" href \"\" alt \"\""},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" label \"\" href \"\" alt \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" label \"\" href \"\" alt \"\""},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"menu__abc\" description \"\" label \"\" href \"\" alt \"\""},
            {"3b) two symbolic names",
                    "symbolicname \"menu__abc\" symbolicname \"menu__def\" description \"\" label \"\" href \"\" alt \"\"",
                    "symbolicname \"menu__def\" symbolicname \"menu__abc\" description \"\" label \"\" href \"\" alt \"\""},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" label \"\" href \"\" alt \"\""},
            {"4b) description not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "                 label \"\" href \"\" alt \"\""},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" label \"\" href \"\" alt \"\""},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" label \"\" href \"\" alt \"\""},
            // hidden
            {"5a) not hidden",
                    "description \"\"         label \"\" href \"\" alt \"\"",
                    "description \"\" !hidden label \"\" href \"\" alt \"\""},
            {"5b) hidden",
                    "",
                    "description \"\" hidden label \"\" href \"\" alt \"\""},
            // treemenu
            {"6a) not treemenu",
                    "description \"\"           label \"\" href \"\" alt \"\"",
                    "description \"\" !treemenu label \"\" href \"\" alt \"\""},
            {"6b) treemenu",
                    "",
                    "description \"\" treemenu label \"\" href \"\" alt \"\""},
            // label
            {"7a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\""},
            {"7b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\"            href \"\" alt \"\""},
            // href
            {"8a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\""},
            {"8b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\"           alt \"\""},
            // alt
            {"9a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\""},
            {"9b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\" href \"\"         "},
            // setting
            {"10a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"Value\""},
            {"10b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"\" \"Value\""},
            {"10c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"\""},
            {"10d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key2\" \"value\" setting \"key1\" \"value\""},
            // children
            {"11a) child command",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" "},
            {"11b) child menu",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\""},
            {"11c) multiple children",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" menu \"123\" command \"234\" command \"123\""},
            {"11d) multiple children with mixed old add-syntax",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" menu \"123\" command \"234\" command \"123\"",
                    "description \"\" label \"\" href \"\" alt \"\" add command \"123\" add menu \"123\" command \"234\" command \"123\""},
            // property
            {"12a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\""},
            {"12b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"12c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"12d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Menu_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Menu_mxJPO(_name);
    }
}
