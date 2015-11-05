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
import org.mxupdate.update.userinterface.Inquiry_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Inquiry_mxJPO inquiry CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class InquiryCI_1ParserTest
    extends AbstractParserTest<Inquiry_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" pattern \"\" format \"\" code \" \""},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" pattern \"\" format \"\" code \" \""},
            {"1b) package single",
                    "package \"abc\" description \"\" pattern \"\" format \"\" code \" \"",
                    "package abc     description \"\" pattern \"\" format \"\" code \" \""},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" pattern \"\" format \"\" code \" \""},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" pattern \"\" format \"\" code \" \""},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" pattern \"\" format \"\" code \" \"",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" pattern \"\" format \"\" code \" \""},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"form__abc\" description \"\" pattern \"\" format \"\" code \" \""},
            {"3b) two symbolic names",
                    "symbolicname \"form__abc\" symbolicname \"form__def\" description \"\"  pattern \"\" format \"\" code \" \"",
                    "symbolicname \"form__def\" symbolicname \"form__abc\" description \"\"  pattern \"\" format \"\" code \" \""},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" pattern \"\" format \"\" code \" \""},
            {"4b) description not defined",
                    "description \"\" pattern \"\" format \"\" code \" \"",
                    "                 pattern \"\" format \"\" code \" \""},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" pattern \"\" format \"\" code \" \""},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" pattern \"\" format \"\" code \" \""},
            // hidden flag
            {"5a) hidden",
                    "",
                    "description \"\" hidden  pattern \"\" format \"\" code \" \""},
            {"5b) not hidden (not defined)",
                    "description \"\" pattern \"\" format \"\" code \" \"",
                    "description \"\" !hidden pattern \"\" format \"\" code \" \""},
            // pattern
            {"6a) pattern",
                    "",
                    "description \"\" pattern \"abc\" format \"\" code \" \""},
            {"6b) pattern not defined",
                    "description \"\" pattern \"\" format \"\" code \" \"",
                    "description \"\"              format \"\" code \" \""},
            // format
            {"7a) format",
                    "",
                    "description \"\" pattern \"\" format \"abc\" code \" \""},
            {"7b) format not defined",
                    "description \"\" pattern \"\" format \"\" code \" \"",
                    "description \"\" pattern \"\"             code \" \""},
            // code
            {"8a) code",
                    "",
                    "description \"\" pattern \"\" format \"\" code \" abc \""},
            {"8b) code not defined",
                    "description \"\" pattern \"\" format \"\" code \" \"",
                    "description \"\" pattern \"\" format \"\"          "},
            // argument
            {"9a) argument",
                    "",
                    "description \"\" pattern \"\" format \"\" argument \"Key\" \"Value\" code \" \""},
            {"9b) argument with empty key",
                    "",
                    "description \"\" pattern \"\" format \"\" argument \"\" \"Value\" code \" \""},
            {"9c) argument with empty value",
                    "",
                    "description \"\" pattern \"\" format \"\" argument \"Key\" \"\" code \" \""},
            {"9d) multiple arguments",
                    "description \"\" pattern \"\" format \"\" argument \"key1\" \"value\" argument \"key2\" \"value\" code \" \"",
                    "description \"\" pattern \"\" format \"\" argument \"key2\" \"value\" argument \"key1\" \"value\" code \" \""},
            // property
            {"10a) property special characters",
                    "",
                    "description \"\" pattern \"\" format \"\" property \"{}\\\"\" code \" \""},
            {"10b) property and value special characters",
                    "",
                    "description \"\" pattern \"\" format \"\" property \"{}\\\"\" value \"{}\\\"\" code \" \""},
            {"10c) property link special characters",
                    "",
                    "description \"\" pattern \"\" format \"\" property \"{}\\\"\" to type \"{}\\\"\" code \" \""},
            {"10d) property link and value special characters",
                    "",
                    "description \"\" pattern \"\" format \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" code \" \""},
        };
    }

    @Override
    protected Inquiry_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name)
    {
        return new Inquiry_mxJPO(_name);
    }
}
