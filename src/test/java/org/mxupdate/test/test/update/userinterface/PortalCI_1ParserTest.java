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
import org.mxupdate.update.userinterface.Portal_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Portal_mxJPO portal} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PortalCI_1ParserTest
    extends AbstractParserTest<Portal_mxJPO>
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
            // label
            {"4a) label",
                    "",
                    "description \"\" label \"abc def\" href \"\" alt \"\""},
            {"4b) label not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\"            href \"\" alt \"\""},
            // href
            {"5a) href",
                    "",
                    "description \"\" label \"\" href \"abc def\" alt \"\""},
            {"5b) href not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\"           alt \"\""},
            // alt
            {"6a) alt",
                    "",
                    "description \"\" label \"\" href \"\" alt \"abc def\""},
            {"6b) alt not defined",
                    "description \"\" label \"\" href \"\" alt \"\"",
                    "description \"\" label \"\" href \"\"         "},
            // setting
            {"7a) setting",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"Key\" \"Value\""},
            {"7b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"\" \"Value\""},
            {"7c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"Key\" \"\""},
            {"7d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key2\" \"value\" setting \"key1\" \"value\""},
            {"7e) multiple setting with mixed old add-syntaxs",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key2\" \"value\" add setting \"key1\" \"value\""},
            // children
            {"8a) child channel",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  channel \"123\" "},
            {"8c) multiple children",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  channel \"123\" channel \"123\" channel \"234\" channel \"123\""},
            {"8d) multiple children with new rows",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  channel \"123\" newrow channel \"123\" channel \"234\" newrow channel \"123\""},
            // property
            {"9a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\""},
            {"9b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\" value \"{}\\\"\""},
            {"9c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\" to type \"{}\\\"\""},
            {"9d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Portal_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Portal_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_PORTAL.updateType), _name);
    }
}
