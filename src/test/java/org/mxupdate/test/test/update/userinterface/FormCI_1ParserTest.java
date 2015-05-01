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
import org.mxupdate.update.userinterface.Form_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Form_mxJPO form CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class FormCI_1ParserTest
    extends AbstractParserTest<Form_mxJPO>
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
                    "symbolicname \"form__abc\" description \"\""},
            {"1b) two symbolic names",
                    "symbolicname \"form__abc\" symbolicname \"form__def\" description \"\"",
                    "symbolicname \"form__def\" symbolicname \"form__abc\" description \"\""},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\""},
            {"2b) description not defined",
                    "description \"\" ",
                    ""},
            {"3a) not hidden",
                    "description \"\"",
                    "description \"\" !hidden"},
            {"3b) hidden",
                    "",
                    "description \"\" hidden"},
            {"4a) field empty values",
                    "",
                    "description \"\" field { name \"\" label \"\" }"},
            {"4b) field default value for name",
                    "description \"\" field { name \"\" label \"\" }",
                    "description \"\" field { label \"\" }"},
            {"4c) field value for name",
                    "",
                    "description \"\" field { name \"ANAME\"  label \"\" }"},
            {"4d) field default value for label",
                    "description \"\" field { name \"\" label \"\" }",
                    "description \"\" field { name \"\" }"},
            {"4e) field value for label",
                    "",
                    "description \"\" field { name \"\"  label \"ALABEL\" }"},
            {"5a) field empty select",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" select \"\" }"},
            {"5b) field value for select",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" select \"ASELECT\" }"},
            {"6a) field empty businessobject",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"\" }"},
            {"6b) field value for businessobject",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"$<type>\" }"},
            {"7a) field empty relationship",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" relationship \"\" }"},
            {"7b) field value for relationship",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" relationship \"$<type>\" }"},
            {"8a) field empty range",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" range \"\" }"},
            {"8b) field value for range",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" range \"${SUITE_DIR}/range.jsp\" }"},
            {"9a) field empty href",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" href \"\" }"},
            {"9b) field value for href",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" href \"${SUITE_DIR}/execute.jsp\" }"},
            // field alt
            {"10a) field empty alt",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" alt \"\" }"},
            {"10b) field value for alt",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" alt \"emxFramework.Basic.Type\" }"},
            // field user
            {"11a) field user",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"abc\" }"},
            {"11b) field multiple users to check sorting",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"abc\" user \"def\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"def\" user \"abc\" }"},
             // field setting
            {"12a) empty setting",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" setting \"\" \"\" }"},
            {"12b) setting with values",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" setting \"Field Type\" \"basic\" }"}
        };
    }

    @Override()
    protected Form_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Form_mxJPO(_paramCache.getMapping().getTypeDef("Form"), _name);
    }

}
