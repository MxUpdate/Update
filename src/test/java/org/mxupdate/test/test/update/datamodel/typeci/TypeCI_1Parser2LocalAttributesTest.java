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

package org.mxupdate.test.test.update.datamodel.typeci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Type_mxJPO type CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class TypeCI_1Parser2LocalAttributesTest
    extends AbstractParserTest<Type_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {

            // general attribute definition
            {"0a) local binary attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" } "},
            {"0b) local boolean attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" } "},
            {"0c) local date attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "},
            {"0d) local integer attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "},
            {"0e) local real attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "},
            {"0f) local string attribute",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" } "},

             // attribute registered name
            {"1a) attribute symbolic name",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"1b) attribute two symbolic names",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute description
            {"2a) attribute description",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"2b) attribute description not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"2c) multi-line attribute description",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute hidden flag
            {"3a) attribute hidden",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"3b) attribute not hidden (not defined)",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute multivalue flag
            {"4a) attribute multivalue flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" }"},
            {"4b) attribute multivalue flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" }"},

            // attribute resetonclone flag
            {"5a) attribute resetonclone flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" }"},
            {"5b) attribute resetonclone flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" }"},

            // attribute resetonrevision flag
            {"6a) attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" }"},
            {"6b) attribute resetonrevision flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" }"},

            // attribute default value
            {"7a) attribute default value",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  }"},
            {"7b) attribute default value not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      }"},
            {"7c) multi-line attribute default value",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" }"},

            // real attribute rangevalue flag
            {"8a) real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" }"},
            {"8b) real attribute rangevalue flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" }"},

            // string attribute multiline flag
            {"9a) attribute multiline flag",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" }"},
            {"9b) attribute multiline flag not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" }"},

             // string attribute maxlength
            {"10a) attribute maxlength",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" }"},
            {"10b) attribute maxlength not defined",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" }"},

            // attribute rule
            {"11a) attribute rule",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  }"},
            {"11a) attribute rule list (if more than one none)",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  }"},

            // attribute dimension
            {"12a) real attribute dimension",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  }"},

            // attribute action trigger
            {"20a) action trigger with input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" }"},
            {"20b) action trigger w/o input",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" }"},
            //  attribute check trigger
            {"21a) check trigger with input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" }"},
            {"21b) check trigger w/o input",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" }"},
            //  attribute override trigger
            {"22a) override trigger with input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" }"},
            {"22b) override trigger w/o input",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" }"},

            // attribute ranges
            {"30a) attribute range",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" }"},
            {"30b) attribute range",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" }"},
            {"30c) attribute range >",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" }"},
            {"30d) attribute range >=",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" }"},
            {"30e) attribute range <",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" }"},
            {"30f) attribute range <=",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" }"},
            {"30g) attribute range !=",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" }"},
            {"30h) attribute range match",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" }"},
            {"30i) attribute range !match",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" }"},
            {"30j) attribute range smatch",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" }"},
            {"30k) attribute range !smatch",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" }"},
            {"30l) attribute range program",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" }"},
            {"30m) attribute range program input",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" }"},
            {"30n) attribute range between inclusive",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive }"},
            {"30o) attribute range between exclusive",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive }"},

            // attribute property
            {"40a) attribute property special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" }"},
            {"40b) attribute property and value special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
            {"40c) attribute property link special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
            {"40d) attribute property link and value special characters",
                    "",
                    "description \"\" !hidden local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
        };
    }

    @Override
    protected Type_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Type_mxJPO(_name);
    }
}
