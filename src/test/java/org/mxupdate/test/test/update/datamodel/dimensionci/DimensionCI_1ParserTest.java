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

package org.mxupdate.test.test.update.datamodel.dimensionci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Dimension_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Dimension_mxJPO dimension CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class DimensionCI_1ParserTest
    extends AbstractParserTest<Dimension_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
            {"0) simple hidden dimension definition without units",
                    "",
                    "description \"\" hidden"},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" !hidden"},
            {"1b) package single",
                    "package \"abc\" description \"\" !hidden",
                    "package abc     description \"\" !hidden"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden"},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden"},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" !hidden "},
            {"3b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden"},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"4b) description not defined",
                    "description \"\" !hidden",
                    "                 !hidden"},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" !hidden"},
            // hidden
            {"5a) simple not hidden dimension",
                    "",
                    "description \"\" !hidden"},
            {"5b) simple hidden dimension definition",
                    "",
                    "description \"\" hidden"},
            // property
            {"6a) property special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\""},
            {"6b) property and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"6c) property link special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"6d) property link and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},

            // unit
            {"100) dimension with simple default unit",
                    "",
                    "description \"\" !hidden "
                            + "unit \"name1\" { default description \"\" label \"\" multiplier 1.0   offset 0.0  } "
                            + "unit \"name2\" {         description \"\" label \"\" multiplier -10.0 offset 20.0 }",
            },
            // unit uuid
            {"101a) unit uuid with minus separator",
                    "",
                    "description \"\" !hidden "
                              + "unit \"name1\" { uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" default description \"\" label \"\" multiplier 1.0 offset 0.0 }"
            },
            {"101b) unit uuid w/o minus separator",
                    "",
                    "description \"\" !hidden "
                              + "unit \"name1\" { uuid \"FDA75674979211E6AE2256B6B6499611\"     default description \"\" label \"\" multiplier 1.0 offset 0.0 }"},
            {"101c) unit uuid convert from single to string",
                    "description \"\" !hidden "
                              + "unit \"name1\" { uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  default description \"\" label \"\" multiplier 1.0 offset 0.0 }",
                    "description \"\" !hidden "
                              + "unit \"name1\" { uuid   FDA7-5674979211-E6AE2256B6-B6499611    default description \"\" label \"\" multiplier 1.0 offset 0.0 }"},
            // default unit
            {"102) dimension with simple default unit and not default",
                    "description \"\" !hidden "
                            + "unit \"name1\" { default description \"\" label \"\" multiplier 1.0   offset 0.0 } "
                            + "unit \"name2\" {         description \"\" label \"\" multiplier -10.0 offset 20.0 }",
                    "description \"\" !hidden\n"
                            + "unit \"name1\" { default  description \"\" label \"\" multiplier 1.0   offset 0.0 } "
                            + "unit \"name2\" { !default description \"\" label \"\" multiplier -10.0 offset 20.0 }",
            },
            // unit offset
            {"103) dimension with unit with negative offset",
                    "",
                    "description \"ein test\" !hidden "
                            + "unit \"name1\" { default description \"description1\"  label \"label1\" multiplier 1.0  offset 0.0 } "
                            + "unit \"name2\" {         description \"description 2\" label \"label2\" multiplier 10.0 offset -20.0 }",
            },
            // unit mulitplier
            {"104) dimension with unit with negative multiplier",
                    "",
                    "description \"ein test\" !hidden "
                            + "unit \"name1\" { default description \"description1\"  label \"label1\" multiplier 1.0  offset 0.0 } "
                            + "unit \"name2\" {         description \"description 2\" label \"label2\" multiplier -10.0 offset 20.0 }",
            },
            {"105) dimension with unit with integer number",
                    "description \"ein test\" !hidden "
                            + "unit \"name1\" { default description \"\" label \"\" multiplier 1.0 offset 0.0 }",
                    "description \"ein test\" !hidden "
                            + "unit \"name1\" { default multiplier 1 offset 0 }"
            },
            // complex definition
            {"106) complex dimension definition",
                    "",
                      "description \"ein test\"\n"
                    + "!hidden\n"
                    + "unit \"name1\" {\n"
                    + "  default\n"
                    + "  description \"description1\"\n"
                    + "  label \"label1\"\n"
                    + "  multiplier 1.0\n"
                    + "  offset 0.0\n"
                    + "  setting \"SettingKey2\" \"SettingValue2\"\n"
                    + "  setting \"to\" \"SettingValue\"\n"
                    + "  property \"Zest\" value \"Zest\"\n"
                    + "  property \"_test3\" value \"Test3\"\n"
                    + "  property \"abc\" value \"abc\"\n"
                    + "  property \"abc2\" value \"abc2\"\n"
                    + "  property \"test\" to type \"Part\" value \"Test\"\n"
                    + "  property \"test2\" value \"Test2\"\n"
                    + "}\n"
                    + "unit \"name2\" {\n"
                    + "  description \"description 2\"\n"
                    + "  label \"label2\"\n"
                    + "  multiplier 10.0\n"
                    + "  offset 20.0\n"
                    + "  system \"Duration Units\" to unit \"name2\"\n"
                    + "}",
            },
        };
    }

    @Override
    protected Dimension_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new Dimension_mxJPO(_name);
    }
}
