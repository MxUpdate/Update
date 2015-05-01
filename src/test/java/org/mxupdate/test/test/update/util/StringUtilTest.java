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

package org.mxupdate.test.test.update.util;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link StringUtil_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class StringUtilTest
    extends AbstractTest
{
    @DataProvider(name = "simpleData")
    public Object[][] getData()
    {
        return new Object[][]  {
                {"abc",         "abc"},
                {"abc\"",       "abc\\\""},
                {"\\",          "\\\\"},
                {"{}",          "{}"},
                {"{{}",         "{\\{}"},
                {"{{{}}",       "{{\\{}}"},
                {"{{}}}",       "{{}}\\}"},
                {"{{}}}}{}",    "{{}}\\}\\}{}"},
        };
    }

    @Test(dataProvider = "simpleData")
    public void positiveTestSimple(final String _input,
                                   final String _expected)
    {
        Assert.assertEquals(StringUtil_mxJPO.convertUpdate(_input), _expected);
    }
}
