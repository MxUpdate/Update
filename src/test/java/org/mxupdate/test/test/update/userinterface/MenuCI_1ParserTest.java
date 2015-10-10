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
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" label \"\" href \"\" alt \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" label \"\" href \"\" alt \"\""},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" label \"\" href \"\" alt \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" label \"\" href \"\" alt \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"menu__abc\" description \"\" label \"\" href \"\" alt \"\""},
            {"2b) two symbolic names",
                    "symbolicname \"menu__abc\" symbolicname \"menu__def\" description \"\" label \"\" href \"\" alt \"\"",
                    "symbolicname \"menu__def\" symbolicname \"menu__abc\" description \"\" label \"\" href \"\" alt \"\""},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" label \"\" href \"\" alt \"\""},
            {"3b) description not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "                 label \"\" href \"\" alt \"\""},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" label \"\" href \"\" alt \"\""},
            {"3d) tab's in description",
                    "",
                    "description \"abc\\tdef\" label \"\" href \"\" alt \"\""},
            // hidden
            {"4a) not hidden",
                    "description \"\"         label \"\" href \"\" alt \"\"",
                    "description \"\" !hidden label \"\" href \"\" alt \"\""},
            {"4b) hidden",
                    "",
                    "description \"\" hidden label \"\" href \"\" alt \"\""},
            // treemenu
            {"5a) not treemenu",
                    "description \"\"           label \"\" href \"\" alt \"\"",
                    "description \"\" !treemenu label \"\" href \"\" alt \"\""},
            {"5b) treemenu",
                    "",
                    "description \"\" treemenu label \"\" href \"\" alt \"\""},
            // label
            {"6a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\""},
            {"6b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\"            href \"\" alt \"\""},
            // href
            {"7a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\""},
            {"7b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\"           alt \"\""},
            // alt
            {"8a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\""},
            {"8b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\" href \"\"         "},
            // setting
            {"9a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"Value\""},
            {"9b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"\" \"Value\""},
            {"9c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"\""},
            {"9d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key2\" \"value\" setting \"key1\" \"value\""},
            // children
            {"10a) child command",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" "},
            {"10b) child menu",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\""},
            {"10c) multiple children",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" menu \"123\" command \"234\" command \"123\""},
            {"10d) multiple children with mixed old add-syntax",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" menu \"123\" command \"234\" command \"123\"",
                    "description \"\" label \"\" href \"\" alt \"\" add command \"123\" add menu \"123\" command \"234\" command \"123\""},
            // property
            {"11a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\""},
            {"11b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"11c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"11d) property link and value special characters",
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
