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
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden"},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden"},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden"},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" !hidden "},
            {"2b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden"},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"3b) description not defined",
                    "description \"\" !hidden",
                    "                 !hidden"},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            {"3d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden"},
            // hidden
            {"4a) simple not hidden dimension",
                    "",
                    "description \"\" !hidden"},
            {"4b) simple not hidden dimension definition defined as value w/o apostrophe",
                    "description \"\" !hidden",
                    "description \"\" hidden FALSE"},
            {"4c) simple not hidden dimension definition defined as value with apostrophe",
                    "description \"\" !hidden",
                    "description \"\" hidden \"false\""},
            {"4d) simple hidden dimension definition",
                    "",
                    "description \"\" hidden"},
            {"4e) simple hidden dimension definition defined as value w/o apostrophe",
                    "description \"\" hidden",
                    "description \"\" hidden TRUE"},
            {"4f) simple hidden dimension definition defined as value with apostrophe",
                    "description \"\" hidden",
                    "description \"\" hidden \"True\""},
            // unit
            {"5a) dimension with simple default unit",
                      "",
                      "description \"\"\n"
                    + "!hidden\n"
                    + "unit \"name1\" {\n"
                    + "  default\n"
                    + "  description \"\"\n"
                    + "  label \"\"\n"
                    + "  multiplier 1.0\n"
                    + "  offset 0.0\n"
                    + "}\n"
                    + "unit \"name2\" {\n"
                    + "  description \"\"\n"
                    + "  label \"\"\n"
                    + "  multiplier -10.0\n"
                    + "  offset 20.0\n"
                    + "}",
            },
            {"5b) dimension with simple default unit defined as value w/o apostrophe",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default true\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
            },
            {"5c) dimension with simple default unit defined as value with apostrophe",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default \"TRUE\"\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
            },
            {"5d) dimension with simple default unit and not default",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  !default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
            },
            {"5e) dimension with simple default unit and not default as value w/o apostrophe",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  default FALSE\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
            },
            {"5f) dimension with simple default unit and not default as value with apostrophe",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
                    "description \"\"\n"
                  + "!hidden\n"
                  + "unit \"name1\" {\n"
                  + "  default\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier 1.0\n"
                  + "  offset 0.0\n"
                  + "}\n"
                  + "unit \"name2\" {\n"
                  + "  default \"false\"\n"
                  + "  description \"\"\n"
                  + "  label \"\"\n"
                  + "  multiplier -10.0\n"
                  + "  offset 20.0\n"
                  + "}",
            },
            {"6) complex dimension definition",
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
            {"7) dimension with unit with negative offset",
                    "",
                    "description \"ein test\"\n"
                    + "!hidden\n"
                    + "unit \"name1\" {\n"
                    + "  default\n"
                    + "  description \"description1\"\n"
                    + "  label \"label1\"\n"
                    + "  multiplier 1.0\n"
                    + "  offset 0.0\n"
                    + "}\n"
                    + "unit \"name2\" {\n"
                    + "  description \"description 2\"\n"
                    + "  label \"label2\"\n"
                    + "  multiplier 10.0\n"
                    + "  offset -20.0\n"
                    + "}",
            },
            {"8) dimension with unit with negative multiplier",
                    "",
                    "description \"ein test\"\n"
                    + "!hidden\n"
                    + "unit \"name1\" {\n"
                    + "  default\n"
                    + "  description \"description1\"\n"
                    + "  label \"label1\"\n"
                    + "  multiplier 1.0\n"
                    + "  offset 0.0\n"
                    + "}\n"
                    + "unit \"name2\" {\n"
                    + "  description \"description 2\"\n"
                    + "  label \"label2\"\n"
                    + "  multiplier -10.0\n"
                    + "  offset 20.0\n"
                    + "}",
            },
            {"9) dimension with unit with integer number",
                    "description \"ein test\"\n"
                    + "!hidden\n"
                    + "unit \"name1\" {\n"
                    + "  default\n"
                    + "  description \"\"\n"
                    + "  label \"\"\n"
                    + "  multiplier 1.0\n"
                    + "  offset 0.0\n"
                    + "}",
                    "description \"ein test\"\n"
                    + "!hidden\n"
                    + "unit \"name1\" {\n"
                    + "  default\n"
                    + "  multiplier 1\n"
                    + "  offset 0\n"
                    + "}"
            },
            // property
            {"10a) property special characters",
                "",
                "description \"\" !hidden property \"{}\\\"\""},
            {"10b) property and value special characters",
                "",
                "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"10c) property link special characters",
                "",
                "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"10d) property link and value special characters",
                "",
                "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Dimension_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new Dimension_mxJPO(_name);
    }
}
