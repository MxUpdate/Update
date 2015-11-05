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
public class TypeCI_1Parser1Test
    extends AbstractParserTest<Type_mxJPO>
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
                    "symbolicname \"channel_abc\" description \"\" !hidden"},
            {"3b) two symbolic names",
                    "symbolicname \"channel_abc\" symbolicname \"channel_def\" description \"\" !hidden",
                    "symbolicname \"channel_def\" symbolicname \"channel_abc\" description \"\" !hidden"},
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
            // kind
            {"5a) basic kind",
                    "description \"\" !hidden",
                    "description \"\" kind basic !hidden"},
            {"5b) composed kind",
                    "",
                    "description \"\" kind composed !hidden"},
            // abstract flag
            {"6a) not abstract",
                    "description \"\" !hidden",
                    "description \"\" !abstract !hidden"},
            {"6b) abstract",
                    "",
                    "description \"\" abstract !hidden"},
            // derived
            {"7a) derived",
                    "",
                    "description \"\" derived \"123\" !hidden"},
            {"7b) not derived (with empty string)",
                    "description \"\" !hidden",
                    "description \"\" derived \"\" !hidden"},
            // hidden flag
            {"8a) hidden",
                    "",
                    "description \"\" hidden"},
            {"8b) not hidden (not defined)",
                    "description \"\" !hidden",
                    "description \"\""},
            // action trigger
            {"9a) action trigger with input",
                    "",
                    "description \"\" !hidden trigger createevent action \"{}\\\"\" input \"{}\\\"\""},
            {"9b) action trigger w/o input",
                    "description \"\" !hidden trigger createevent action \"{}\\\"\" input \"\"",
                    "description \"\" !hidden trigger createevent action \"{}\\\"\""},
            // check trigger
            {"10a) check trigger with input",
                    "",
                    "description \"\" !hidden trigger createevent check \"{}\\\"\" input \"{}\\\"\""},
            {"10b) check trigger w/o input",
                    "description \"\" !hidden trigger createevent check \"{}\\\"\" input \"\"",
                    "description \"\" !hidden trigger createevent check \"{}\\\"\""},
            // override trigger
            {"11a) override trigger with input",
                    "",
                    "description \"\" !hidden trigger createevent override \"{}\\\"\" input \"{}\\\"\""},
            {"11b) override trigger w/o input",
                    "description \"\" !hidden trigger createevent override \"{}\\\"\" input \"\"",
                    "description \"\" !hidden trigger createevent override \"{}\\\"\""},
            // methods
            {"12a) method",
                    "",
                    "description \"\" !hidden method \"111\""},
            {"12b) method name w/o apostrophe",
                    "description \"\" !hidden method \"111\"",
                    "description \"\" !hidden method 111"},
            {"12c) two method (to check sort)",
                    "description \"\" !hidden method \"111\" method \"222\"",
                    "description \"\" !hidden method \"222\" method \"111\""},
             // attribute
            {"13a) attribute",
                    "",
                    "description \"\" !hidden attribute \"111\""},
            {"13b) attribute name w/o apostrophe",
                    "description \"\" !hidden attribute \"111\"",
                    "description \"\" !hidden attribute 111"},
            {"13c) two attributes (to check sort)",
                    "description \"\" !hidden attribute \"111\" attribute \"222\"",
                    "description \"\" !hidden attribute \"222\" attribute \"111\""},
            // property
            {"14a) property special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\""},
            {"14b) property and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" value \"{}\\\"\""},
            {"14c) property link special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\""},
            {"14d) property link and value special characters",
                    "",
                    "description \"\" !hidden property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected Type_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Type_mxJPO(_name);
    }
}
