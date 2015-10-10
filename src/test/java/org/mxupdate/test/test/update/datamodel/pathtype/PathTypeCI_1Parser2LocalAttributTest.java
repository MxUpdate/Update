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

package org.mxupdate.test.test.update.datamodel.pathtype;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.PathType_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PathType_mxJPO type CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class PathTypeCI_1Parser2LocalAttributTest
    extends AbstractParserTest<PathType_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            // general attribute definition
            {"0a) local binary attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" }"},
            {"0b) local boolean attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" }"},
            {"0c) local date attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"0d) local integer attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"0e) local real attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"0f) local string attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" }"},

            // sort of multiple local attributes
            {"1) local string attribute: sorting",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME 1\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "local attribute \"ATTRNAME 2\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME 2\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "local attribute \"ATTRNAME 1\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }"},

            // uuid
            {"2a) local attribute uuid with minus separator",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"2b) local attribute uuid w/o minus separator",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"2c) local attribute uuid convert from single to string",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

             // attribute registered name
            {"3a) local attribute symbolic name",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"3b) local attribute two symbolic names",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute description
            {"4a) local attribute description",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"4b) local attribute description not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"4c) local multi-line attribute description",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute hidden flag
            {"5a) local attribute hidden",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"5b) local attribute not hidden (not defined)",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute multivalue flag
            {"6a) local attribute multivalue flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" }"},
            {"6b) local attribute multivalue flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" }"},

            // attribute resetonclone flag
            {"7a) local attribute resetonclone flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" }"},
            {"7b) local attribute resetonclone flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" }"},

            // attribute resetonrevision flag
            {"8a) local attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" }"},
            {"8b) local attribute resetonrevision flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" }"},

            // attribute default value
            {"9a) local attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  }"},
            {"9b) local attribute default value not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      }"},
            {"9c) local multi-line attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" }"},

            // real attribute rangevalue flag
            {"10a) local real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" }"},
            {"10b) local real attribute rangevalue flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" }"},

            // string attribute multiline flag
            {"11a) local attribute multiline flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" }"},
            {"11b) local attribute multiline flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" }"},

             // string attribute maxlength
            {"12a) local attribute maxlength",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" }"},
            {"12b) local attribute maxlength not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" }"},

            // attribute rule
            {"13a) local attribute rule",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  }"},
            {"13a) local attribute rule list (if more than one none)",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  }"},

            // attribute dimension
            {"14) local real attribute dimension",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  }"},

            // attribute action trigger
            {"20a) local attribute action trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" }"},
            {"20b) local attribute action trigger w/o input",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" }"},
            //  attribute check trigger
            {"21a) local attribute check trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" }"},
            {"21b) local attribute check trigger w/o input",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" }"},
            //  attribute override trigger
            {"22a) local attribute override trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" }"},
            {"22b) local attribute override trigger w/o input",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" }"},

            // attribute ranges
            {"30a) local attribute range",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" }"},
            {"30b) local attribute range",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" }"},
            {"30c) local attribute range >",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" }"},
            {"30d) local attribute range >=",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" }"},
            {"30e) local attribute range <",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" }"},
            {"30f) local attribute range <=",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" }"},
            {"30g) local attribute range !=",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" }"},
            {"30h) local attribute range match",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" }"},
            {"30i) local attribute range !match",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" }"},
            {"30j) local attribute range smatch",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" }"},
            {"30k) local attribute range !smatch",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" }"},
            {"30l) local attribute range program",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" }"},
            {"30m) local attribute range program input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" }"},
            {"30n) local attribute range between inclusive",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive }"},
            {"30o) local attribute range between exclusive",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive }"},

            // attribute property
            {"40a) local attribute property special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" }"},
            {"40b) local attribute property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
            {"40c) local attribute property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
            {"40d) local attribute property link and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
        };
    }

    @Override
    protected PathType_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
            final String _name)
    {
        return new PathType_mxJPO(_name);
    }
}
