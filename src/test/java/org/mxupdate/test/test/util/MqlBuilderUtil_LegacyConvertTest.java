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

package org.mxupdate.test.test.util;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MqlBuilder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the legacy convert of MQL statements in JPO {@link MqlBuilderUtil_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class MqlBuilderUtil_LegacyConvertTest
{
    /**
     * Returns the test data.
     *
     * @return test data
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"prefix \"version\" suffix",       "prefix ",  " suffix", new String[]{"version"}},
            {"prefix \"$1$\" suffix",           "prefix ",  " suffix", new String[]{"$1$"}},
            {"prefix \"abc\"\"$1$\" suffix",    "prefix ",  " suffix", new String[]{"abc", "$1$"}},
            {"prefix$3 suffix",                 "prefix$3", " suffix", new String[]{}},
            {"",                                "",         "",        new String[]{}},
            {"\"abc\"",                         "",         "",        new String[]{"abc"}},
            {"\"\\\"abc\\\"\"",                 "",         "",        new String[]{"\"abc\""}},
            {"\"\\\\abc\"",                     "",         "",        new String[]{"\\abc"}},
        };
    }

    /**
     * Positive test.
     *
     * @param _exp          expected value
     * @param _prefix       prefix in front of arguments
     * @param _suffix       suffix after all arguments
     * @param _args         arguments used to replace
     * @throws Exception if test failed
     */
    @Test(description = "positive test",
          dataProvider = "data")
    public void positiveTest(final String _exp,
            final String _prefix,
            final String _suffix,
            final String[] _args)
        throws Exception
    {
        final MqlBuilder mql = MqlBuilderUtil_mxJPO.mql().cmd(_prefix);
        for (final String arg : _args)  {
            mql.cmd("").arg(arg);
        }
        mql.cmd(_suffix);

        final Method meth = mql.getClass().getDeclaredMethod("mqlConvertLegacy");
        meth.setAccessible(true);
        final String tmp = (String) meth.invoke(mql);

        Assert.assertEquals(_exp, tmp);
    }
}
