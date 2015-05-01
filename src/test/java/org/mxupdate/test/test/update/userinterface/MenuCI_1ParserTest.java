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
 * Tests the {@link Menu_mxJPO menu} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class MenuCI_1ParserTest
    extends AbstractParserTest<Menu_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\""},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" label \"\" href \"\" alt \"\""},
            {"2b) description not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "                 label \"\" href \"\" alt \"\""},
            // hidden
            {"3a) not hidden",
                    "description \"\"         label \"\" href \"\" alt \"\"",
                    "description \"\" !hidden label \"\" href \"\" alt \"\""},
            {"3b) hidden",
                    "",
                    "description \"\" hidden label \"\" href \"\" alt \"\""},
            // treemenu
            {"4a) not treemenu",
                    "description \"\"           label \"\" href \"\" alt \"\"",
                    "description \"\" !treemenu label \"\" href \"\" alt \"\""},
            {"4b) treemenu",
                    "",
                    "description \"\" treemenu label \"\" href \"\" alt \"\""},
            // label
            {"5a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\""},
            {"5b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\"            href \"\" alt \"\""},
            // href
            {"6a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\""},
            {"6b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\"           alt \"\""},
            // alt
            {"7a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\""},
            {"7b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\" href \"\"         "},
            // setting
            {"8a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"Value\""},
            {"8b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"\" \"Value\""},
            {"8c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"Key\" \"\""},
            {"8d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\" setting \"key2\" \"value\" setting \"key1\" \"value\""},
            // children
            {"9a) child command",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" "},
            {"9b) child menu",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\""},
            {"9c) multiple children",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" menu \"123\" command \"234\" command \"123\""},
            {"9d) multiple children with mixed old add-syntax",
                    "description \"\" label \"\" href \"\" alt \"\" command \"123\" menu \"123\" command \"234\" command \"123\"",
                    "description \"\" label \"\" href \"\" alt \"\" add command \"123\" add menu \"123\" command \"234\" command \"123\""},
            // property
            {"10a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\""},
            {"10b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"10c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"10d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Menu_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Menu_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_MENU.updateType), _name);
    }
}
