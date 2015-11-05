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
public class PathTypeCI_1Parser1Test
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
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"1b) package single",
                    "package \"abc\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "package abc     description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"3b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"4b) description not defined",
                    "description \"\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }",
                    "!hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"4c) multi-line description ",
                    "",
                    "description \"abc\ndef\" !hidden "
                            + "from { cardinality many } "
                            + "to   { }"},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" !hidden "
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
        };
    }

    @Override
    protected PathType_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
            final String _name)
    {
        return new PathType_mxJPO(_name);
    }
}
