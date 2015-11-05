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
 * Tests the {@link Portal_mxJPO portal CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class PortalCI_1ParserTest
    extends AbstractParserTest<Portal_mxJPO>
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
                    "symbolicname \"portal_abc\" description \"\" label \"\" href \"\" alt \"\""},
            {"3b) two symbolic names",
                    "symbolicname \"portal_abc\" symbolicname \"portal_def\" description \"\" label \"\" href \"\" alt \"\"",
                    "symbolicname \"portal_def\" symbolicname \"portal_abc\" description \"\" label \"\" href \"\" alt \"\""},
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
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"Key\" \"Value\""},
            {"9b) setting with empty key",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"\" \"Value\""},
            {"9c) setting with empty value",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"Key\" \"\""},
            {"9d) multiple settings",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key2\" \"value\" setting \"key1\" \"value\""},
            {"9e) multiple setting with mixed old add-syntaxs",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key1\" \"value\" setting \"key2\" \"value\"",
                    "description \"\" label \"\" href \"\" alt \"\"  setting \"key2\" \"value\" add setting \"key1\" \"value\""},
            // children
            {"10a) child channel",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  channel \"123\" "},
            {"10c) multiple children",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  channel \"123\" channel \"123\" channel \"234\" channel \"123\""},
            {"10d) multiple children with new rows",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  channel \"123\" newrow channel \"123\" channel \"234\" newrow channel \"123\""},
            // property
            {"11a) property special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\""},
            {"11b) property and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\" value \"{}\\\"\""},
            {"11c) property link special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\" to type \"{}\\\"\""},
            {"11d) property link and value special characters",
                    "",
                    "description \"\" label \"\" href \"\" alt \"\"  property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Portal_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Portal_mxJPO(_name);
    }
}
