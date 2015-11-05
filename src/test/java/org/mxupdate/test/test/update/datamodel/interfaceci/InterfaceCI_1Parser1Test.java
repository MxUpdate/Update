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

package org.mxupdate.test.test.update.datamodel.interfaceci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Interface_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Interface_mxJPO interface CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class InterfaceCI_1Parser1Test
    extends AbstractParserTest<Interface_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"0) simple",
                    "",
                    "description \"\" !hidden"},
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" description \"\" !hidden"},
            {"1b) package single",
                    "package \"abc\" description \"\" !hidden",
                    "package abc     description \"\" !hidden"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden"},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden"},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"interface_abc\" description \"\" !hidden"},
            {"3b) two symbolic names",
                    "symbolicname \"interface_abc\" symbolicname \"interface_def\" description \"\" !hidden",
                    "symbolicname \"interface_def\" symbolicname \"interface_abc\" description \"\" !hidden"},
            // description
            {"4a) description",
                    "",
                    "description \"abc def\" !hidden"},
            {"4b) description not defined",
                    "description \"\" !hidden",
                    "!hidden"},
            {"4c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden"},
            {"4d) tab's in description",
                    "",
                    "description \"abc\\tdef\" !hidden"},
            {"4e) unicode character in description",
                        "",
                        "description \"abc\\u2019def\" !hidden"},
            // abstract flag
            {"5a) not abstract",
                    "description \"\" !hidden",
                    "description \"\" !abstract !hidden"},
            {"5b) abstract",
                    "",
                    "description \"\" abstract !hidden"},
            // derived
            {"6a) derived",
                    "",
                    "description \"\" derived \"123\" !hidden"},
            {"6b) multiple derived (to test sorting)",
                    "description \"\" derived \"111\" derived \"222\" derived \"333\" !hidden",
                    "description \"\" derived \"222\" derived \"111\" derived \"333\" !hidden"},
            // hidden flag
            {"7a) hidden",
                    "",
                    "description \"\" hidden"},
            {"7b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\""},
            // attribute
            {"8a) attribute",
                    "",
                    "description \"\" !hidden attribute \"111\""},
            {"8b) attribute name w/o apostrophe",
                    "description \"\" !hidden attribute \"111\"",
                    "description \"\" !hidden attribute 111"},
            {"8c) two attributes (to check sort)",
                    "description \"\" !hidden attribute \"111\" attribute \"222\"",
                    "description \"\" !hidden attribute \"222\" attribute \"111\""},
            // properties
            {"9a) property",
                    "",
                    "description \"\" !hidden property \"111\""},
            {"9b) property with value",
                    "",
                    "description \"\" !hidden property \"111\" value \"222\""},
            {"9c) property with referenced admin object",
                    "",
                    "description \"\" !hidden property \"111\" to type \"TestType\""},
            {"9d) property with referenced admin object and value",
                    "",
                    "description \"\" !hidden property \"111\" to type \"TestType\" value \"222\""},
            // two properties for sorting
            {"10a) sorting property",
                     "description \"\" !hidden property \"111\" property \"222\"",
                    "description \"\" !hidden  property \"222\" property \"111\""},
            {"10b) sorting property with value",
                    "description \"\" !hidden property \"111\" value \"222\" property \"111\" value \"333\"",
                    "description \"\" !hidden property \"111\" value \"333\" property \"111\" value \"222\""},
            {"10c) sorting  property with referenced admin object",
                    "description \"\" !hidden property \"111\" to type \"TestType1\" property \"111\" to type \"TestType2\"",
                    "description \"\" !hidden property \"111\" to type \"TestType2\" property \"111\" to type \"TestType1\""},
            {"10d) sorting  property with referenced admin object and value",
                    "description \"\" !hidden property \"111\" to type \"TestType\" value \"222\" property \"111\" to type \"TestType\" value \"333\"",
                    "description \"\" !hidden property \"111\" to type \"TestType\" value \"333\" property \"111\" to type \"TestType\" value \"222\""},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // for relationship / type / interface

            {"101a) for pathtype",
                    "",
                    "description \"\" !hidden for pathtype \"111\""},
            {"101b) for pathtype  all",
                    "",
                    "description \"\" !hidden for pathtype all"},
            {"101c) for pathtype all (and one pathtype must removed)",
                    "description \"\" !hidden for pathtype all",
                    "description \"\" !hidden for pathtype all for pathtype \"111\""},
            {"101d) for two pathtypes (to check sort)",
                    "description \"\" !hidden for pathtype \"111\" for pathtype \"222\"",
                    "description \"\" !hidden for pathtype \"222\" for pathtype \"111\""},

            {"111a) for relationship",
                    "",
                    "description \"\" !hidden for relationship \"111\""},
            {"111b) for relationship all",
                    "",
                    "description \"\" !hidden for relationship all"},
            {"111c) for relationship all (and one relationship must removed)",
                    "description \"\" !hidden for relationship all",
                    "description \"\" !hidden for relationship all for relationship \"111\""},
            {"111d) for two relationships (to check sort)",
                    "description \"\" !hidden for relationship \"111\" for relationship \"222\"",
                    "description \"\" !hidden for relationship \"222\" for relationship \"111\""},

            {"121a) for type",
                    "",
                    "description \"\" !hidden for type \"111\""},
            {"121b) for type all",
                    "",
                    "description \"\" !hidden for type all"},
            {"121c) for type all (and one type must removed)",
                    "description \"\" !hidden for type all",
                    "description \"\" !hidden for type all for type \"111\""},
            {"121d) for two types (to check sort)",
                    "description \"\" !hidden for type \"111\" for type \"222\"",
                    "description \"\" !hidden for type \"222\" for type \"111\""},

            {"131) for type / relationship (to check sorting)",
                    "description \"\" !hidden for relationship \"111\" for type \"111\"",
                    "description \"\" !hidden for type \"111\" for relationship \"111\""},
            {"132) for pathtype ( type / relationship (to check sorting)",
                    "description \"\" !hidden for pathtype \"111\" for relationship \"111\" for type \"111\"",
                    "description \"\" !hidden for type \"111\" for relationship \"111\" for pathtype \"111\""},
        };
    }

    @Override
    protected Interface_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new Interface_mxJPO(_name);
    }
}
