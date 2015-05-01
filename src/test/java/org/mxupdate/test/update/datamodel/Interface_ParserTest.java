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

package org.mxupdate.test.update.datamodel;

import org.mxupdate.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Interface_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Interface_mxJPO interface} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class Interface_ParserTest
    extends AbstractParserTest<Interface_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                "",
                "description \"\" !hidden"},
            // description
            {"2a) description",
                "",
                "description \"abc def\" !hidden"},
            {"2b) description not defined",
                "description \"\" !hidden",
                "!hidden"},
            // abstract flag
            {"3a) not abstract",
                "description \"\" !hidden",
                "description \"\" !abstract !hidden"},
            {"3b) abstract",
                "",
                "description \"\" abstract !hidden"},
            // derived
            {"4a) derived",
                "",
                "description \"\" derived \"123\" !hidden"},
            {"4b) multiple derived (to test sorting)",
                "description \"\" derived \"111\" derived \"222\" derived \"333\" !hidden",
                "description \"\" derived \"222\" derived \"111\" derived \"333\" !hidden"},
            // hidden flag
            {"5a) hidden",
                "",
                "description \"\" hidden"},
            {"5b) not hidden (not defined)",
                "description \"\" !hidden",
                "description \"\""},
            // attribute
            {"6a) attribute",
                "",
                "description \"\" !hidden attribute \"111\""},
            {"6b) attribute name w/o apostrophe",
                "description \"\" !hidden attribute \"111\"",
                "description \"\" !hidden attribute 111"},
            {"6c) two attributes (to check sort)",
                "description \"\" !hidden attribute \"111\" attribute \"222\"",
                "description \"\" !hidden attribute \"222\" attribute \"111\""},
            // for relationship / type
            {"7a) for relationship",
                "",
                "description \"\" !hidden for relationship \"111\""},
            {"7b) for type",
                "",
                "description \"\" !hidden for type \"111\""},
            {"7c) for relationship all",
                "",
                "description \"\" !hidden for relationship all"},
            {"7d) for type all",
                "",
                "description \"\" !hidden for type all"},
            {"7e) for relationship all (and one relationship must removed)",
                "description \"\" !hidden for relationship all",
                "description \"\" !hidden for relationship all for relationship \"111\""},
            {"7f) for type all (and one type must removed)",
                "description \"\" !hidden for type all",
                "description \"\" !hidden for type all for type \"111\""},
            {"7g) for type / relationship (to check sorting)",
                "description \"\" !hidden for relationship \"111\" for type \"111\"",
                "description \"\" !hidden for type \"111\" for relationship \"111\""},
            {"7h) for two relationships (to check sort)",
                "description \"\" !hidden for relationship \"111\" for relationship \"222\"",
                "description \"\" !hidden for relationship \"222\" for relationship \"111\""},
            {"7i) for two types (to check sort)",
                "description \"\" !hidden for type \"111\" for type \"222\"",
                "description \"\" !hidden for type \"222\" for type \"111\""},
            // properties
            {"8a) property",
                "",
                "description \"\" !hidden property \"111\""},
            {"8b) property with value",
                "",
                "description \"\" !hidden property \"111\" value \"222\""},
            {"8c) property with referenced admin object",
                "",
                "description \"\" !hidden property \"111\" to type \"TestType\""},
            {"8d) property with referenced admin object and value",
                "",
                "description \"\" !hidden property \"111\" to type \"TestType\" value \"222\""},
            // two properties for sorting
            {"9a) sorting property",
                 "description \"\" !hidden property \"111\" property \"222\"",
                "description \"\" !hidden  property \"222\" property \"111\""},
            {"9b) sorting property with value",
                "description \"\" !hidden property \"111\" value \"222\" property \"111\" value \"333\"",
                "description \"\" !hidden property \"111\" value \"333\" property \"111\" value \"222\""},
            {"9c) sorting  property with referenced admin object",
                "description \"\" !hidden property \"111\" to type \"TestType1\" property \"111\" to type \"TestType2\"",
                "description \"\" !hidden property \"111\" to type \"TestType2\" property \"111\" to type \"TestType1\""},
            {"9d) sorting  property with referenced admin object and value",
                "description \"\" !hidden property \"111\" to type \"TestType\" value \"222\" property \"111\" to type \"TestType\" value \"333\"",
                "description \"\" !hidden property \"111\" to type \"TestType\" value \"333\" property \"111\" to type \"TestType\" value \"222\""},
        };
    }

    @Override()
    protected Interface_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new Interface_mxJPO(_paramCache.getMapping().getTypeDef("Interface"), _name);
    }
}
