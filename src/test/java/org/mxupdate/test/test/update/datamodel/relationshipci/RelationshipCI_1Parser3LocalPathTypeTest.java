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
            // uuid
            {"1a) local path type uuid with minus separator",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"1b) local path type uuid w/o minus separator",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"1c) local path type uuid convert from single to string",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            // registered name
            {"2a) local path type: symbolic name",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "symbolicname \"channel_abc\" description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"2b) local path type: two symbolic names",
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
            {"3a) local path type: description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc def\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"3b) local path type: description not defined",
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
            {"3c) local path type: multi-line description ",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"abc\ndef\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                            + "}"},
            {"3d) local path type: tab's in description",
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
            {"101a) local path type from: one cardinality",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality one  } "
                                    + "to   { } "
                            + "}"},
            {"101b) local path type from: default cardinality",
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
            {"102a) local path type from: all type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type all } "
                                    + "to   { } "
                            + "}"},
            {"102b) local path type from: one type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many type \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"102c) local path type from: one type w/o apostrophe",
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
            {"102d) local path type from: two types unsorted",
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
            {"103b) local path type from: 1 relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many relationship \"ABC\" } "
                                    + "to   { } "
                            + "}"},
            {"103c) local path type from: 1 relationship w/o apostrophe",
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
            {"103d) local path type from: 2 relationships unsorted",
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
            {"112a) local path type to: all type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type all } "
                            + "}"},
            {"112b) local path type to: one type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { type \"ABC\" } "
                            + "}"},
            {"112c) local path type to: one type w/o apostrophe",
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
            {"112d) local path type to: two types unsorted",
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
            {"113a) local path type to: all relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { relationship all } "
                            + "}"},
            {"113b) local path type to: 1 relationship",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to { relationship \"ABC\" } "
                            + "}"},
            {"113c) local path type to: 1 relationship w/o apostrophe",
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
            {"113d) local path type to: 2 relationships unsorted",
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
            {"200a) local path type: local binary attribute",
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
            {"200b) local path type: local boolean attribute",
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
            {"200c) local path type: local date attribute",
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
            {"200d) local path type: local integer attribute",
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
            {"200e) local path type: local real attribute",
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
            {"200f) local path type: local string attribute",
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

            // uuid
            {"201a) local attribute with uuid with minus separator",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}"},
            {"201b) local attribute with uuid w/o minus separator",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid \"FDA75674979211E6AE2256B6B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}"},
            {"201c) local attribute with uuid convert from single to string",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid \"FDA7-5674979211-E6AE2256B6-B6499611\" description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local pathtype \"PathType\" { "
                                    + "description \"\" !hidden "
                                    + "from { cardinality many } "
                                    + "to   { } "
                                    + "local attribute \"ATTRNAME\" { kind string uuid FDA7-5674979211-E6AE2256B6-B6499611 description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" } "
                            + "}"},

             // attribute registered name
            {"202a) local path type: local attribute symbolic name",
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
            {"202b) local path type: attribute two symbolic names",
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
            {"203a) local path type: locale attribute description",
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
            {"203b) local path type: local attribute description not defined",
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
            {"203c) local path type: multi-line local attribute description",
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
            {"204a) local path type: local attribute hidden",
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
            {"204b) local path type: local attribute not hidden (not defined)",
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
            {"205a) local path type: local attribute multivalue flag",
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
            {"205b) local path type: local attribute multivalue flag not defined",
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
            {"206a) local path type: local attribute resetonclone flag",
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
            {"206b) local path type: local attribute resetonclone flag not defined",
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
            {"207a) local path type: local attribute resetonrevision flag",
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
            {"207b) local path type: local attribute resetonrevision flag not defined",
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
            {"208a) local path type: local attribute default value",
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
            {"208b) local path type: local attribute default value not defined",
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
            {"208c) local path type: multi-line local attribute default value",
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
            {"209a) local path type: local real attribute rangevalue flag",
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
            {"209b) local path type: local real attribute rangevalue flag not defined",
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
            {"210a) local path type: local attribute multiline flag",
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
            {"210b) local path type: local attribute multiline flag not defined",
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
            {"211a) local path type: local attribute maxlength",
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
            {"211b) local path type: local attribute maxlength not defined",
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
            {"231a) local path type: local attribute rule",
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
            {"231a) local path type: local attribute rule list (if more than one none)",
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
            {"232a) local path type: local real attribute dimension",
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
            {"230a) local path type: local action trigger with input",
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
            {"230b) local path type: local action trigger w/o input",
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
            {"231a) local path type: local attribute check trigger with input",
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
            {"231b) local path type: local attributecheck trigger w/o input",
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
            {"232a) local path type: local attribute override trigger with input",
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
            {"232b) local path type: local attribute override trigger w/o input",
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
            {"240a) local path type: local attribute range",
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
            {"240b) local path type: local attribute range",
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
            {"240c) local path type: local attribute range >",
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
            {"240d) local path type: local attribute range >=",
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
            {"240e) local path type: local attribute range <",
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
            {"240f) local path type: local attribute range <=",
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
            {"240g) local path type: local attribute range !=",
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
            {"240h) local path type: local attribute range match",
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
            {"240i) local path type: local attribute range !match",
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
            {"240j) local path type: local attribute range smatch",
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
            {"240k) local path type: local attribute range !smatch",
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
            {"240l) local path type: local attribute range program",
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
            {"240m) local path type: local attribute range program input",
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
            {"240n) local path type: local attribute range between inclusive",
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
            {"240o) local path type: local attribute range between exclusive",
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
            {"250a) local path type: local attribute property special characters",
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
            {"250b) local path type: local attribute property and value special characters",
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
            {"250c) local path type: local attribute property link special characters",
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
            {"250d) local path type: local attribute property link and value special characters",
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
