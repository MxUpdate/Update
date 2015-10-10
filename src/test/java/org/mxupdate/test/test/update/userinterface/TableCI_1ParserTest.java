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
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Table_mxJPO table CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class TableCI_1ParserTest
    extends AbstractParserTest<Table_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\""},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\""},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"table_abc\" description \"\""},
            {"2b) two symbolic names",
                    "symbolicname \"table_abc\" symbolicname \"table_def\" description \"\"",
                    "symbolicname \"table_def\" symbolicname \"table_abc\" description \"\""},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\""},
            {"3b) description not defined",
                    "description \"\" ",
                    ""},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\""},
            {"3d) tab's in description",
                    "",
                    "description \"abc\tdef\""},
            // hidden
            {"4a) not hidden",
                    "description \"\"",
                    "description \"\" !hidden"},
            {"4b) hidden",
                    "",
                    "description \"\" hidden"},
            // column name
            {"5a) column empty values",
                    "",
                    "description \"\" column { name \"\" label \"\" }"},
            {"5b) column default value for name",
                    "description \"\" column { name \"\" label \"\" }",
                    "description \"\" column { label \"\" }"},
            {"5c) column value for name",
                    "",
                    "description \"\" column { name \"ANAME\"  label \"\" }"},
            // column label
            {"6a) column default value for label",
                    "description \"\" column { name \"\" label \"\" }",
                    "description \"\" column { name \"\" }"},
            {"6b) column value for label",
                    "",
                    "description \"\" column { name \"\"  label \"ALABEL\" }"},
            // column select
            {"7a) column empty select",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" select \"\" }"},
            {"7b) column value for select",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" select \"ASELECT\" }"},
            // column businessobject
            {"8a) column empty businessobject",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" businessobject \"\" }"},
            {"8b) column value for businessobject",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" businessobject \"$<type>\" }"},
            // column relationship
            {"9a) column empty relationship",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" relationship \"\" }"},
            {"9b) column value for relationship",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" relationship \"$<type>\" }"},
            // column range
            {"10a) column empty range",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" range \"\" }"},
            {"10b) column value for range",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" range \"${SUITE_DIR}/range.jsp\" }"},
            // column href
            {"11a) column empty href",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" href \"\" }"},
            {"11b) column value for href",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" href \"${SUITE_DIR}/execute.jsp\" }"},
            // column alt
            {"12a) column empty alt",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" alt \"\" }"},
            {"12b) column value for alt",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" alt \"emxFramework.Basic.Type\" }"},
            // column hidden
            {"13a) column empty hidden",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" !hidden }"},
            {"13b) column value for hidden",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" hidden }"},
            // column user
            {"14a) column user",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" user \"abc\" }"},
            {"14b) column multiple users to check sorting",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" user \"abc\" user \"def\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" user \"def\" user \"abc\" }"},
            // column sorttype
            {"15a) column sorttype alpha",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype alpha   }"},
            {"15b) column sorttype numeric",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype numeric }"},
            {"15c) column sorttype other",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype other   }"},
            {"15d) column sorttype none",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\"                  }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype none    }"},
            // column setting
            {"16a) empty setting",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" setting \"\" \"\" }"},
            {"16b) setting with values",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" setting \"Field Type\" \"basic\" }"}
        };
    }

    @Override
    protected Table_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Table_mxJPO(_name);
    }
}
