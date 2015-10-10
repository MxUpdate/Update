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
public class TypeCI_1Parser3LocalPathTypesTest
    extends AbstractParserTest<Type_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) local type path",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"1b) uuid w/o minus separator",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"1c) uuid convert from single to string",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // registered name
            {"2a) local type path: symbolic name",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"2b) local type path: two symbolic names",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // description
            {"3a) local type path: description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc def\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"3b) local type path: description not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "!hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"3c) local type path: multi-line description ",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\ndef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"3d) local type path: tab's in description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\tdef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // global attribute
            {"11a) local type path: global attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + " attribute \"111\" "
                            + "}"},
            {"11b) local type path: global attribute name w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + " attribute \"111\" "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute 111 "
                            + "}"},
            {"11c) local type path: two global attributes (to check sort)",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute \"111\" attribute \"222\" "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute \"222\" attribute \"111\" "
                            + "}"},
            // property
            {"12a) local type path: property special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" "
                            + "}"},
            {"12b) local type path: property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" value \"{}\\\"\" "
                            + "}"},
            {"12c) local type path: property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" to type \"{}\\\"\" "
                            + "}"},
            {"12d) local type path: property link and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" "
                            + "}"},

            // from cardinality
            {"21a) local type path from: one cardinality",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality one  } "
                                    + "to   { } "
                            + "}"},
            {"21b) local type path from: default cardinality",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from {                   } "
                                    + "to   { } "
                            + "}"},
            // from type
            {"22a) local type path from: all type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type all } "
                                    + "to   { } "
                            + "}"},
            {"22b) local type path from: one type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"22c) local type path from: one type w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type ABC } "
                                    + "to   { } "
                            + "}"},
            {"22d) local type path from: two types unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" type \"DEF\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"DEF\" type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            // from relationship
            {"23a) local type path from: all relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship all } "
                                    + "to   { } "
                            + "}"},
            {"23b) local type path from: 1 relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"23c) local type path from: 1 relationship w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship ABC } "
                                    + "to   { } "
                            + "}"},
            {"23d) local type path from: 2 relationships unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" relationship \"DEF\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"DEF\" relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},

            // to type
            {"31a) local type path to: all type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type all } "
                            + "}"},
            {"31b) local type path to: one type",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" } "
                            + "}"},
            {"31c) local type path to: one type w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type \"ABC\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type ABC } "
                            + "}"},
            {"31d) local type path to: two types unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" type \"DEF\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type \"DEF\" type \"ABC\" } "
                            + "}"},
            // to relationship
            {"32a) local type path to: all relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship all } "
                            + "}"},
            {"32b) local type path to: 1 relationship",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" } "
                            + "}"},
            {"32c) local type path to: 1 relationship w/o apostrophe",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship \"ABC\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship ABC } "
                            + "}"},
            {"32d) local type path to: 2 relationships unsorted",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" relationship \"DEF\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship \"DEF\" relationship \"ABC\" } "
                            + "}"},

            // general attribute definition
            {"40a) local type path: local binary attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" } "
                            + "}"},
            {"40b) local type path: local boolean attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" } "
                            + "}"},
            {"40c) local type path: local date attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"40d) local type path: local integer attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"40e) local type path: local real attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"40f) local type path: local string attribute",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" } "
                            + "}"},

            // uuid
            {"41a) local attribute with uuid with minus separator",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}"},
            {"41b) local attribute with uuid w/o minus separator",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid \"FDA75674979211E6AE2256B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}"},
            {"41c) local attribute with uuid convert from single to string",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid \"FDA7-5674979211-E6AE2256B6-B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid FDA7-5674979211-E6AE2256B6-B6499611 description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}"},

             // attribute registered name
            {"42a) local type path: local attribute symbolic name",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"42b) local type path: attribute two symbolic names",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute description
            {"43a) local type path: locale attribute description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"43b) local type path: local attribute description not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"43c) local type path: multi-line local attribute description",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute hidden flag
            {"44a) local type path: local attribute hidden",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"44b) local type path: local attribute not hidden (not defined)",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute multivalue flag
            {"45a) local type path: local attribute multivalue flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"45b) local type path: local attribute multivalue flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute resetonclone flag
            {"46a) local type path: local attribute resetonclone flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" } "
                            + "}"},
            {"46b) local type path: local attribute resetonclone flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" } "
                            + "}"},

            // attribute resetonrevision flag
            {"47a) local type path: local attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" } "
                            + "}"},
            {"47b) local type path: local attribute resetonrevision flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" } "
                            + "}"},

            // attribute default value
            {"48a) local type path: local attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  } "
                            + "}"},
            {"48b) local type path: local attribute default value not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      } "
                            + "}"},
            {"48c) local type path: multi-line local attribute default value",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" } "
                            + "}"},

            // real attribute rangevalue flag
            {"49a) local type path: local real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" } "
                            + "}"},
            {"49b) local type path: local real attribute rangevalue flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" } "
                            + "}"},

            // string attribute multiline flag
            {"50a) local type path: local attribute multiline flag",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" } "
                            + "}"},
            {"50b) local type path: local attribute multiline flag not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" } "
                            + "}"},

             // string attribute maxlength
            {"51a) local type path: local attribute maxlength",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" } "
                            + "}"},
            {"51b) local type path: local attribute maxlength not defined",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" } "
                            + "}"},

            // attribute rule
            {"52a) local type path: local attribute rule",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  } "
                            + "}"},
            {"52a) local type path: local attribute rule list (if more than one none)",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  } "
                            + "}"},

            // attribute dimension
            {"53) local type path: local real attribute dimension",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  } "
                            + "}"},

            // attribute action trigger
            {"54a) local type path: local action trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"54b) local type path: local action trigger w/o input",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" } "
                            + "}"},
            //  attribute check trigger
            {"55a) local type path: local attribute check trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"55b) local type path: local attributecheck trigger w/o input",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" } "
                            + "}"},
            //  attribute override trigger
            {"56a) local type path: local attribute override trigger with input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"56b) local type path: local attribute override trigger w/o input",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" } "
                            + "}"},

            // attribute ranges
            {"57a) local type path: local attribute range",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" } "
                            + "}"},
            {"57b) local type path: local attribute range",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" } "
                            + "}"},
            {"57c) local type path: local attribute range >",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" } "
                            + "}"},
            {"57d) local type path: local attribute range >=",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" } "
                            + "}"},
            {"57e) local type path: local attribute range <",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" } "
                            + "}"},
            {"57f) local type path: local attribute range <=",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" } "
                            + "}"},
            {"57g) local type path: local attribute range !=",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" } "
                            + "}"},
            {"57h) local type path: local attribute range match",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" } "
                            + "}"},
            {"57i) local type path: local attribute range !match",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" } "
                            + "}"},
            {"57j) local type path: local attribute range smatch",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" } "
                            + "}"},
            {"57k) local type path: local attribute range !smatch",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" } "
                            + "}"},
            {"57l) local type path: local attribute range program",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" } "
                            + "}"},
            {"57m) local type path: local attribute range program input",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" } "
                            + "}"},
            {"57n) local type path: local attribute range between inclusive",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive } "
                            + "}"},
            {"57o) local type path: local attribute range between exclusive",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive } "
                            + "}"},

            // attribute property
            {"58a) local type path: local attribute property special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" } "
                            + "}"},
            {"58b) local type path: local attribute property and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" } "
                            + "}"},
            {"58c) local type path: local attribute property link special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" } "
                            + "}"},
            {"58d) local type path: local attribute property link and value special characters",
                    "",
                    "description \"\" !hidden "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" } "
                            + "}"},
        };
    }

    @Override
    protected Type_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Type_mxJPO(_name);
    }
}
