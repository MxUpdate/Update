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

package org.mxupdate.test.test.update.system.indexci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.system.IndexCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link IndexCI_mxJPO unique key} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class IndexCI_1ParserTest
    extends AbstractParserTest<IndexCI_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" !hidden !enable"},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" !hidden !enable"},
            {"1b) package single",
                    "package \"abc\" description \"\" !hidden !enable",
                    "package abc     description \"\" !hidden !enable"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !enable"},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !enable"},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !enable",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden !enable"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" description \"\" !hidden !enable"},
            {"3b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden !enable",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden !enable"},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" !hidden !enable"},
            {"4b) description not defined",
                    "description \"\" !hidden !enable",
                    "!hidden"},
            // hidden flag
            {"5a) hidden",
                    "",
                    "description \"\" hidden !enable"},
            {"5b) not hidden (not defined)",
                    "description \"\" !hidden !enable",
                    "description \"\"         !enable"},
            // enable flag
            {"6a) enable",
                    "",
                    "description \"\" !hidden enable"},
            {"6b) not enable (not defined)",
                    "description \"\" !hidden !enable",
                    "description \"\" !hidden"},
            // unique flag
            {"7a) unique",
                    "",
                    "description \"\" !hidden !enable unique"},
            {"7b) not unique (defined)",
                    "description \"\" !hidden !enable",
                    "description \"\" !hidden !enable !unique"},
            // properties
            {"10a) property",
                    "",
                    "description \"\" !hidden !enable property \"111\""},
            {"10b) property with value",
                    "",
                    "description \"\" !hidden !enable property \"111\" value \"222\""},
            {"10c) property with referenced admin object",
                    "",
                    "description \"\" !hidden !enable property \"111\" to type \"TestType\""},
            {"10d) property with referenced admin object and value",
                    "",
                    "description \"\" !hidden !enable property \"111\" to type \"TestType\" value \"222\""},
            // two properties for sorting
            {"11a) sorting property",
                    "description \"\" !hidden !enable property \"111\" property \"222\"",
                    "description \"\" !hidden !enable property \"222\" property \"111\""},
            {"11b) sorting property with value",
                    "description \"\" !hidden !enable property \"111\" value \"222\" property \"111\" value \"333\"",
                    "description \"\" !hidden !enable property \"111\" value \"333\" property \"111\" value \"222\""},
            {"11c) sorting  property with referenced admin object",
                    "description \"\" !hidden !enable property \"111\" to type \"TestType1\" property \"111\" to type \"TestType2\"",
                    "description \"\" !hidden !enable property \"111\" to type \"TestType2\" property \"111\" to type \"TestType1\""},
            {"11d) sorting  property with referenced admin object and value",
                    "description \"\" !hidden !enable property \"111\" to type \"TestType\" value \"222\" property \"111\" to type \"TestType\" value \"333\"",
                    "description \"\" !hidden !enable property \"111\" to type \"TestType\" value \"333\" property \"111\" to type \"TestType\" value \"222\""},

            // field
            {"20a) basic field (string)",
                    "",
                    "description \"\" !hidden !enable field \"name\""},
            {"20a) basic field (single)",
                    "description \"\" !hidden !enable field \"name\"",
                    "description \"\" !hidden !enable field   name"},
            {"21a) basic fields sorted",
                    "",
                    "description \"\" !hidden !enable field \"name1\" field \"name2\""},
            {"21b) basic fields unsorted",
                    "description \"\" !hidden !enable field \"name1\" field \"name2\"",
                    "description \"\" !hidden !enable field \"name2\" field \"name1\""},
            {"22a) attribute field with size",
                    "",
                    "description \"\" !hidden !enable field \"attribute[Test]\" size 100"},
            {"22b) attribute field with not-defined size",
                   "description \"\" !hidden !enable field \"attribute[Test]\" size 0",
                    "description \"\" !hidden !enable field \"attribute[Test]\""},
            {"22c) basic field with defined size",
                    "description \"\" !hidden !enable field \"name\"",
                    "description \"\" !hidden !enable field \"name\" size 100"},
         };
    }

    @Override
    protected IndexCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name)
    {
        return new IndexCI_mxJPO(_name);
    }
}
