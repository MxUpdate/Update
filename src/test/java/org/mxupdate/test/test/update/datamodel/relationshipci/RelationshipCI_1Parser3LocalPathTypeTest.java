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

package org.mxupdate.test.test.update.datamodel.relationshipci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Relationship_mxJPO relationship CI} delta calculation for
 * the local path type.
 *
 * @author The MxUpdate Team
 */
@Test()
public class RelationshipCI_1Parser3LocalPathTypeTest
    extends AbstractParserTest<Relationship_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0) local path type: simple",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // registered name
            {"1a) local path type: symbolic name",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"1b) local path type: two symbolic names",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // description
            {"2a) local path type: description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc def\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"2b) local path type: description not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "!hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"2c) local path type: multi-line description ",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\ndef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"2d) local path type: tab's in description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\tdef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // global attribute
            {"11a) local path type: global attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + " attribute \"111\" "
                            + "}"},
            {"11b) local path type: global attribute name w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + " attribute \"111\" "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute 111 "
                            + "}"},
            {"11c) local path type: two global attributes (to check sort)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute \"111\" attribute \"222\" "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "attribute \"222\" attribute \"111\" "
                            + "}"},
            // property
            {"12a) local path type: property special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" "
                            + "}"},
            {"12b) local path type: property and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" value \"{}\\\"\" "
                            + "}"},
            {"12c) local path type: property link special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" to type \"{}\\\"\" "
                            + "}"},
            {"12d) local path type: property link and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" "
                            + "}"},

            // from cardinality
            {"1a) local path type from: one cardinality",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality one  } "
                                    + "to   { } "
                            + "}"},
            {"1b) local path type from: default cardinality",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from {                   } "
                                    + "to   { } "
                            + "}"},
            // from type
            {"2a) local path type from: all type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type all } "
                                    + "to   { } "
                            + "}"},
            {"2b) local path type from: one type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"2c) local path type from: one type w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type ABC } "
                                    + "to   { } "
                            + "}"},
            {"2d) local path type from: two types unsorted",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" type \"DEF\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"DEF\" type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            // from relationship
            {"3a) local path type from: all relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship all } "
                                    + "to   { } "
                            + "}"},
            {"3b) local path type from: 1 relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"3c) local path type from: 1 relationship w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship ABC } "
                                    + "to   { } "
                            + "}"},
            {"3d) local path type from: 2 relationships unsorted",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" relationship \"DEF\" } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"DEF\" relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},

            // to type
            {"12a) local path type to: all type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type all } "
                            + "}"},
            {"12b) local path type to: one type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" } "
                            + "}"},
            {"12c) local path type to: one type w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type \"ABC\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type ABC } "
                            + "}"},
            {"12d) local path type to: two types unsorted",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" type \"DEF\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { type \"DEF\" type \"ABC\" } "
                            + "}"},
            // to relationship
            {"13a) local path type to: all relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship all } "
                            + "}"},
            {"13b) local path type to: 1 relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" } "
                            + "}"},
            {"13c) local path type to: 1 relationship w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship \"ABC\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship ABC } "
                            + "}"},
            {"13d) local path type to: 2 relationships unsorted",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" relationship \"DEF\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship \"DEF\" relationship \"ABC\" } "
                            + "}"},

            // general attribute definition
            {"20a) local path type: local binary attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" } "
                            + "}"},
            {"20b) local path type: local boolean attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" } "
                            + "}"},
            {"20c) local path type: local date attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"20d) local path type: local integer attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"20e) local path type: local real attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" } "
                            + "}"},
            {"20f) local path type: local string attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" } "
                            + "}"},

             // attribute registered name
            {"21a) local path type: local attribute symbolic name",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"21b) local path type: attribute two symbolic names",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute description
            {"22a) local path type: locale attribute description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"22b) local path type: local attribute description not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"22c) local path type: multi-line local attribute description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute hidden flag
            {"23a) local path type: local attribute hidden",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"23b) local path type: local attribute not hidden (not defined)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute multivalue flag
            {"24a) local path type: local attribute multivalue flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" } "
                            + "}"},
            {"24b) local path type: local attribute multivalue flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" } "
                            + "}"},

            // attribute resetonclone flag
            {"25a) local path type: local attribute resetonclone flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" } "
                            + "}"},
            {"25b) local path type: local attribute resetonclone flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" } "
                            + "}"},

            // attribute resetonrevision flag
            {"26a) local path type: local attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" } "
                            + "}"},
            {"26b) local path type: local attribute resetonrevision flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" } "
                            + "}"},

            // attribute default value
            {"27a) local path type: local attribute default value",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  } "
                            + "}"},
            {"27b) local path type: local attribute default value not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      } "
                            + "}"},
            {"27c) local path type: multi-line local attribute default value",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" } "
                            + "}"},

            // real attribute rangevalue flag
            {"28a) local path type: local real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" } "
                            + "}"},
            {"28b) local path type: local real attribute rangevalue flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" } "
                            + "}"},

            // string attribute multiline flag
            {"29a) local path type: local attribute multiline flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" } "
                            + "}"},
            {"29b) local path type: local attribute multiline flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" } "
                            + "}"},

             // string attribute maxlength
            {"30a) local path type: local attribute maxlength",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" } "
                            + "}"},
            {"30b) local path type: local attribute maxlength not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" } "
                            + "}"},

            // attribute rule
            {"31a) local path type: local attribute rule",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  } "
                            + "}"},
            {"31a) local path type: local attribute rule list (if more than one none)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  } "
                            + "}"},

            // attribute dimension
            {"32a) local path type: local real attribute dimension",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  } "
                            + "}"},

            // attribute action trigger
            {"30a) local path type: local action trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"30b) local path type: local action trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" } "
                            + "}"},
            //  attribute check trigger
            {"31a) local path type: local attribute check trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"31b) local path type: local attributecheck trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" } "
                            + "}"},
            //  attribute override trigger
            {"32a) local path type: local attribute override trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" } "
                            + "}"},
            {"32b) local path type: local attribute override trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" } "
                            + "}"},

            // attribute ranges
            {"40a) local path type: local attribute range",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" } "
                            + "}"},
            {"40b) local path type: local attribute range",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" } "
                            + "}"},
            {"40c) local path type: local attribute range >",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" } "
                            + "}"},
            {"40d) local path type: local attribute range >=",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" } "
                            + "}"},
            {"40e) local path type: local attribute range <",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" } "
                            + "}"},
            {"40f) local path type: local attribute range <=",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" } "
                            + "}"},
            {"40g) local path type: local attribute range !=",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" } "
                            + "}"},
            {"40h) local path type: local attribute range match",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" } "
                            + "}"},
            {"40i) local path type: local attribute range !match",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" } "
                            + "}"},
            {"40j) local path type: local attribute range smatch",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" } "
                            + "}"},
            {"40k) local path type: local attribute range !smatch",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" } "
                            + "}"},
            {"40l) local path type: local attribute range program",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" } "
                            + "}"},
            {"40m) local path type: local attribute range program input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" } "
                            + "}"},
            {"40n) local path type: local attribute range between inclusive",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive } "
                            + "}"},
            {"40o) local path type: local attribute range between exclusive",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive } "
                            + "}"},

            // attribute property
            {"50a) local path type: local attribute property special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" } "
                            + "}"},
            {"50b) local path type: local attribute property and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" } "
                            + "}"},
            {"50c) local path type: local attribute property link special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" } "
                            + "}"},
            {"50d) local path type: local attribute property link and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" } "
                            + "}"},
         };
    }

    @Override
    protected Relationship_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                               final String _name)
    {
        return new Relationship_mxJPO(_name);
    }
}
