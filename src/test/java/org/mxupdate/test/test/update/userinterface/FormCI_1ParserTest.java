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
@Test
public class FormCI_1ParserTest
    extends AbstractParserTest<Form_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\""},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\""},
            {"1b) package single",
                    "package \"abc\" description \"\"",
                    "package abc     description \"\""},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\""},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\""},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\""},
             // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"form__abc\" description \"\""},
            {"3b) two symbolic names",
                    "symbolicname \"form__abc\" symbolicname \"form__def\" description \"\"",
                    "symbolicname \"form__def\" symbolicname \"form__abc\" description \"\""},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\""},
            {"4b) description not defined",
                    "description \"\" ",
                    ""},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\""},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\""},
            // hidden
            {"5a) not hidden",
                    "description \"\"",
                    "description \"\" !hidden"},
            {"5b) hidden",
                    "",
                    "description \"\" hidden"},
            // field
            {"6a) field empty values",
                    "",
                    "description \"\" field { name \"\" label \"\" }"},
            {"6b) field default value for name",
                    "description \"\" field { name \"\" label \"\" }",
                    "description \"\" field { label \"\" }"},
            {"6c) field value for name",
                    "",
                    "description \"\" field { name \"ANAME\"  label \"\" }"},
            {"6d) field default value for label",
                    "description \"\" field { name \"\" label \"\" }",
                    "description \"\" field { name \"\" }"},
            {"6e) field value for label",
                    "",
                    "description \"\" field { name \"\"  label \"ALABEL\" }"},
            // select
            {"7a) field empty select",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" select \"\" }"},
            {"7b) field value for select",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" select \"ASELECT\" }"},
            {"7c) field value for select after businessobject",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\"                               select \"ASELECT\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"PREVSELECT\" select \"ASELECT\" }"},
            // business object sleect
            {"8a) field empty businessobject",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"\" }"},
            {"8b) field value for businessobject",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"$<type>\" }"},
            // relationship select
            {"9a) field empty relationship",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" relationship \"\" }"},
            {"9b) field value for relationship",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" relationship \"$<type>\" }"},
            // field range
            {"10a) field empty range",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" range \"\" }"},
            {"10b) field value for range",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" range \"${SUITE_DIR}/range.jsp\" }"},
            // field href
            {"11a) field empty href",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" href \"\" }"},
            {"11b) field value for href",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" href \"${SUITE_DIR}/execute.jsp\" }"},
            // field alt
            {"12a) field empty alt",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" alt \"\" }"},
            {"12b) field value for alt",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" alt \"emxFramework.Basic.Type\" }"},
            // field user
            {"13a) field user",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"abc\" }"},
            {"13b) field multiple users to check sorting",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"abc\" user \"def\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"def\" user \"abc\" }"},
             // field setting
            {"14a) empty setting",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" setting \"\" \"\" }"},
            {"14b) setting with values",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" setting \"Field Type\" \"basic\" }"}
        };
    }

    @Override
    protected Form_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Form_mxJPO(_name);
    }
}
