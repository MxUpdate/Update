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
                    "symbolicname \"form__abc\" description \"\""},
            {"2b) two symbolic names",
                    "symbolicname \"form__abc\" symbolicname \"form__def\" description \"\"",
                    "symbolicname \"form__def\" symbolicname \"form__abc\" description \"\""},
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
                    "description \"abc\\tdef\""},
            // hidden
            {"4a) not hidden",
                    "description \"\"",
                    "description \"\" !hidden"},
            {"4b) hidden",
                    "",
                    "description \"\" hidden"},
            // field
            {"5a) field empty values",
                    "",
                    "description \"\" field { name \"\" label \"\" }"},
            {"5b) field default value for name",
                    "description \"\" field { name \"\" label \"\" }",
                    "description \"\" field { label \"\" }"},
            {"5c) field value for name",
                    "",
                    "description \"\" field { name \"ANAME\"  label \"\" }"},
            {"5d) field default value for label",
                    "description \"\" field { name \"\" label \"\" }",
                    "description \"\" field { name \"\" }"},
            {"5e) field value for label",
                    "",
                    "description \"\" field { name \"\"  label \"ALABEL\" }"},
            // select
            {"6a) field empty select",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" select \"\" }"},
            {"6b) field value for select",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" select \"ASELECT\" }"},
            // business object sleect
            {"7a) field empty businessobject",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"\" }"},
            {"7b) field value for businessobject",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" businessobject \"$<type>\" }"},
            // relationship select
            {"8a) field empty relationship",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" relationship \"\" }"},
            {"8b) field value for relationship",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" relationship \"$<type>\" }"},
            // field range
            {"9a) field empty range",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" range \"\" }"},
            {"9b) field value for range",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" range \"${SUITE_DIR}/range.jsp\" }"},
            // field href
            {"10a) field empty href",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" href \"\" }"},
            {"10b) field value for href",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" href \"${SUITE_DIR}/execute.jsp\" }"},
            // field alt
            {"11a) field empty alt",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" alt \"\" }"},
            {"11b) field value for alt",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" alt \"emxFramework.Basic.Type\" }"},
            // field user
            {"12a) field user",
                    "",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"abc\" }"},
            {"12b) field multiple users to check sorting",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"abc\" user \"def\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" user \"def\" user \"abc\" }"},
             // field setting
            {"13a) empty setting",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" }",
                    "description \"\" field { name \"ANAME\" label \"ALABEL\" setting \"\" \"\" }"},
            {"13b) setting with values",
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
