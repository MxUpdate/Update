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

package org.mxupdate.test.test.update.system.uniquekeyci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.system.UniqueKeyCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link UniqueKeyCI_mxJPO unique key} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class UniqueKeyCI_1ParserTest
    extends AbstractParserTest<UniqueKeyCI_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
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
            // global flag
            {"7a) global",
                    "",
                    "description \"\" !hidden !enable global"},
            {"7b) not global (defined)",
                    "description \"\" !hidden !enable",
                    "description \"\" !hidden !enable !global"},
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

            // for type
            {"20a) for type (string)",
                    "",
                    "description \"\" !hidden !enable for type \"TestType\""},
            {"20b) for type (single)",
                    "description \"\" !hidden !enable for type \"TestType\"",
                    "description \"\" !hidden !enable for type TestType"},
            {"21a) for type with interface (string)",
                    "",
                    "description \"\" !hidden !enable for type \"TestType\" with interface \"TestInterface\""},
            {"21b) for type with interface (single)",
                    "description \"\" !hidden !enable for type \"TestType\" with interface \"TestInterface\"",
                    "description \"\" !hidden !enable for type \"TestType\" with interface TestInterface"},

            // for relationship
            {"30a) for relationship (string)",
                    "",
                    "description \"\" !hidden !enable for relationship \"TestRelationship\""},
            {"30b) for relationship (single)",
                    "description \"\" !hidden !enable for relationship \"TestRelationship\"",
                    "description \"\" !hidden !enable for relationship TestRelationship"},
            {"31a) for relationship with interface (string)",
                    "",
                    "description \"\" !hidden !enable for relationship \"TestRelationship\" with interface \"TestInterface\""},
            {"31b) for relationship with interface (single)",
                    "description \"\" !hidden !enable for relationship \"TestRelationship\" with interface \"TestInterface\"",
                    "description \"\" !hidden !enable for relationship \"TestRelationship\" with interface TestInterface"},

            // field
            {"40a) basic field (string)",
                    "",
                    "description \"\" !hidden !enable field \"name\""},
            {"40a) basic field (single)",
                    "description \"\" !hidden !enable field \"name\"",
                    "description \"\" !hidden !enable field   name"},
            {"41a) basic fields sorted",
                    "",
                    "description \"\" !hidden !enable field \"name1\" field \"name2\""},
            {"41b) basic fields unsorted",
                    "description \"\" !hidden !enable field \"name1\" field \"name2\"",
                    "description \"\" !hidden !enable field \"name2\" field \"name1\""},
            {"42a) attribute field with size",
                    "",
                    "description \"\" !hidden !enable field \"attribute[Test]\" size 100"},
            {"42b) attribute field with not-defined size",
                    "description \"\" !hidden !enable field \"attribute[Test]\" size 0",
                    "description \"\" !hidden !enable field \"attribute[Test]\""},
            {"42c) basic field with defined size",
                    "description \"\" !hidden !enable field \"name\"",
                    "description \"\" !hidden !enable field \"name\" size 100"},
         };
    }

    @Override
    protected UniqueKeyCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new UniqueKeyCI_mxJPO(_name);
    }
}
