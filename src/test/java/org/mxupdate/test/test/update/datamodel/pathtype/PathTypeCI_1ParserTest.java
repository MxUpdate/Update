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
public class PathTypeCI_1ParserTest
    extends AbstractParserTest<PathType_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"2b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"3b) description not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "!hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"3c) multi-line description ",
                    "",
                    "description \"abc\ndef\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"3d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // global attribute
            {"11a) global attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + " attribute \"111\""},
            {"11b) global attribute name w/o apostrophe",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + " attribute \"111\"",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "attribute 111"},
            {"11c) two global attributes (to check sort)",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "attribute \"111\" attribute \"222\"",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "attribute \"222\" attribute \"111\""},
            // property
            {"12a) property special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "property \"{}\\\"\""},
            {"12b) property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "property \"{}\\\"\" value \"{}\\\"\""},
            {"12c) property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "property \"{}\\\"\" to type \"{}\\\"\""},
            {"12d) property link and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { } "
                            + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // from / to direction

            // from cardinality
            {"101a) from: one cardinality",
                    "",
                    "description \"\" hidden "
                            + "from { cardinality one  } "
                              + "to { }"},
            {"101b) from: default cardinality",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { }",
                    "description \"\" !hidden "
                            + "from {                   } "
                              + "to { }"},
            // from type
            {"102a) from: all type",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many type all } "
                              + "to { }"},
            {"102b) from: one type",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many type \"ABC\" } "
                              + "to { }"},
            {"102c) from: one type w/o apostrophe",
                    "description \"\" !hidden "
                            + "from { cardinality many type \"ABC\" } "
                              + "to { }",
                    "description \"\" !hidden "
                            + "from { cardinality many type ABC } "
                              + "to { }"},
            {"102d) from: two types unsorted",
                    "description \"\" !hidden "
                            + "from { cardinality many type \"ABC\" type \"DEF\" } "
                              + "to { }",
                    "description \"\" !hidden "
                            + "from { cardinality many type \"DEF\" type \"ABC\" } "
                              + "to { }"},
            // from relationship
            {"103a) from: all relationship",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many relationship all } "
                              + "to { }"},
            {"103b) from: 1 relationship",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many relationship \"ABC\" } "
                              + "to { }"},
            {"103c) from: 1 relationship w/o apostrophe",
                    "description \"\" !hidden "
                            + "from { cardinality many relationship \"ABC\" } "
                              + "to { }",
                    "description \"\" !hidden "
                            + "from { cardinality many relationship ABC } "
                              + "to { }"},
            {"103d) from: 2 relationships unsorted",
                    "description \"\" !hidden "
                            + "from { cardinality many relationship \"ABC\" relationship \"DEF\" } "
                              + "to { }",
                    "description \"\" !hidden "
                            + "from { cardinality many relationship \"DEF\" relationship \"ABC\" } "
                              + "to { }"},

            // to type
            {"112a) to: all type",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { type all }"},
            {"112b) to: one type",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { type \"ABC\" }"},
            {"112c) to: one type w/o apostrophe",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { type \"ABC\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { type ABC }"},
            {"112d) to: two types unsorted",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { type \"ABC\" type \"DEF\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { type \"DEF\" type \"ABC\" }"},
            // to relationship
            {"113a) to: all relationship",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { relationship all }"},
            {"113b) to: 1 relationship",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { relationship \"ABC\" }"},
            {"113c) to: 1 relationship w/o apostrophe",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { relationship \"ABC\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { relationship ABC }"},
            {"113d) to: 2 relationships unsorted",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { relationship \"ABC\" relationship \"DEF\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                              + "to { relationship \"DEF\" relationship \"ABC\" }"},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // local attribute

            // general attribute definition
            {"200a) local binary attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" }"},
            {"200b) local boolean attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" }"},
            {"200c) local date attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"200d) local integer attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"200e) local real attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"200f) local string attribute",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" }"},

             // attribute registered name
            {"201a) attribute symbolic name",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"201b) attribute two symbolic names",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute description
            {"202a) attribute description",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"202b) attribute description not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"202c) multi-line attribute description",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute hidden flag
            {"203a) attribute hidden",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"203b) attribute not hidden (not defined)",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute multivalue flag
            {"204a) attribute multivalue flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" }"},
            {"204b) attribute multivalue flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" }"},

            // attribute resetonclone flag
            {"205a) attribute resetonclone flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" }"},
            {"205b) attribute resetonclone flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" }"},

            // attribute resetonrevision flag
            {"206a) attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" }"},
            {"206b) attribute resetonrevision flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" }"},

            // attribute default value
            {"207a) attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  }"},
            {"207b) attribute default value not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      }"},
            {"207c) multi-line attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" }"},

            // real attribute rangevalue flag
            {"208a) real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" }"},
            {"208b) real attribute rangevalue flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" }"},

            // string attribute multiline flag
            {"209a) attribute multiline flag",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" }"},
            {"209b) attribute multiline flag not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" }"},

             // string attribute maxlength
            {"210a) attribute maxlength",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" }"},
            {"210b) attribute maxlength not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" }"},

            // attribute rule
            {"211a) attribute rule",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  }"},
            {"211a) attribute rule list (if more than one none)",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  }"},

            // attribute dimension
            {"212a) real attribute dimension",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  }"},

            // attribute action trigger
            {"220a) action trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" }"},
            {"220b) action trigger w/o input",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" }"},
            //  attribute check trigger
            {"221a) check trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" }"},
            {"221b) check trigger w/o input",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" }"},
            //  attribute override trigger
            {"222a) override trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" }"},
            {"222b) override trigger w/o input",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" }"},

            // attribute ranges
            {"230a) attribute range",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" }"},
            {"230b) attribute range",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" }"},
            {"230c) attribute range >",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" }"},
            {"230d) attribute range >=",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" }"},
            {"230e) attribute range <",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" }"},
            {"230f) attribute range <=",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" }"},
            {"230g) attribute range !=",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" }"},
            {"230h) attribute range match",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" }"},
            {"230i) attribute range !match",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" }"},
            {"230j) attribute range smatch",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" }"},
            {"230k) attribute range !smatch",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" }"},
            {"230l) attribute range program",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" }"},
            {"230m) attribute range program input",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" }"},
            {"230n) attribute range between inclusive",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive }"},
            {"230o) attribute range between exclusive",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive }"},

            // attribute property
            {"240a) attribute property special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" }"},
            {"240b) attribute property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
            {"240c) attribute property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to { } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
            {"240d) attribute property link and value special characters",
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
