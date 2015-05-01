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
@Test()
public class TableCI_1ParserTest
    extends AbstractParserTest<Table_mxJPO>
{

    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0) simple",
                    "",
                    "description \"\""},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"table_abc\" description \"\""},
            {"1b) two symbolic names",
                    "symbolicname \"table_abc\" symbolicname \"table_def\" description \"\"",
                    "symbolicname \"table_def\" symbolicname \"table_abc\" description \"\""},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\""},
            {"2b) description not defined",
                    "description \"\" ",
                    ""},
            // hidden
            {"3a) not hidden",
                    "description \"\"",
                    "description \"\" !hidden"},
            {"3b) hidden",
                    "",
                    "description \"\" hidden"},
            // column name
            {"4a) column empty values",
                    "",
                    "description \"\" column { name \"\" label \"\" }"},
            {"4b) column default value for name",
                    "description \"\" column { name \"\" label \"\" }",
                    "description \"\" column { label \"\" }"},
            {"4c) column value for name",
                    "",
                    "description \"\" column { name \"ANAME\"  label \"\" }"},
            // column label
            {"5a) column default value for label",
                    "description \"\" column { name \"\" label \"\" }",
                    "description \"\" column { name \"\" }"},
            {"5b) column value for label",
                    "",
                    "description \"\" column { name \"\"  label \"ALABEL\" }"},
            // column select
            {"6a) column empty select",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" select \"\" }"},
            {"6b) column value for select",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" select \"ASELECT\" }"},
            // column businessobject
            {"7a) column empty businessobject",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" businessobject \"\" }"},
            {"7b) column value for businessobject",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" businessobject \"$<type>\" }"},
            // column relationship
            {"8a) column empty relationship",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" relationship \"\" }"},
            {"8b) column value for relationship",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" relationship \"$<type>\" }"},
            // column range
            {"9a) column empty range",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" range \"\" }"},
            {"9b) column value for range",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" range \"${SUITE_DIR}/range.jsp\" }"},
            // column href
            {"10a) column empty href",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" href \"\" }"},
            {"10b) column value for href",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" href \"${SUITE_DIR}/execute.jsp\" }"},
            // column alt
            {"11a) column empty alt",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" alt \"\" }"},
            {"11b) column value for alt",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" alt \"emxFramework.Basic.Type\" }"},
            // column hidden
            {"12a) column empty hidden",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" !hidden }"},
            {"12b) column value for hidden",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" hidden }"},
            // column user
            {"13a) column user",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" user \"abc\" }"},
            {"13b) column multiple users to check sorting",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" user \"abc\" user \"def\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" user \"def\" user \"abc\" }"},
            // column sorttype
            {"14a) column sorttype alpha",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype alpha   }"},
            {"14b) column sorttype numeric",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype numeric }"},
            {"14c) column sorttype other",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype other   }"},
            {"14d) column sorttype none",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\"                  }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" sorttype none    }"},
            // column setting
            {"15a) empty setting",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" setting \"\" \"\" }"},
            {"15b) setting with values",
                    "",
                    "description \"\" column { name \"ANAME\" label \"ALABEL\" setting \"Field Type\" \"basic\" }"}
        };
    }

    @Override()
    protected Table_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Table_mxJPO(_paramCache.getMapping().getTypeDef("Table"), _name);
    }

}
