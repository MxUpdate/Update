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

package org.mxupdate.test.update.user;

import org.mxupdate.test.update.AbstractParserTest;
import org.mxupdate.update.user.Association_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Association_mxJPO association CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AssociationCI_ParserTest
    extends AbstractParserTest<Association_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                "",
                "description \"\" !hidden definition \"\""},
            // description
            {"2a) description",
                "",
                "description \"abc def\" !hidden definition \"\""},
            {"2b) description not defined",
                "description \"\" !hidden definition \"\"",
                "                 !hidden definition \"\""},
            // hidden flag
            {"3a) hidden",
                "",
                "description \"\" hidden definition \"\""},
            {"3b) not hidden (not defined)",
                "description \"\" !hidden definition \"\"",
                "description \"\"         definition \"\""},
            // definition
            {"4a) site",
                "",
                "description \"\" !hidden definition \"side\""},
            {"4b) empty definition",
                "description \"\" !hidden definition \"\"",
                "description \"\" !hidden "},
            // property
            {"5a) property special characters",
                "",
                "description \"\" !hidden definition \"\" property \"{}\\\"\""},
            {"5b) property and value special characters",
                "",
                "description \"\" !hidden definition \"\" property \"{}\\\"\" value \"{}\\\"\""},
            {"5c) property link special characters",
                "",
                "description \"\" !hidden definition \"\" property \"{}\\\"\" to type \"{}\\\"\""},
            {"5d) property link and value special characters",
                "",
                "description \"\" !hidden definition \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected Association_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new Association_mxJPO(_paramCache.getMapping().getTypeDef(CI.USR_ASSOCIATION.updateType), _name);
    }
}
