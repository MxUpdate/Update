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

import org.mxupdate.update.util.AbstractParser_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link AbstractParser_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class Parser_GetStringTest
{
    @DataProvider(name = "simpleData")
    public Object[][] getData()
    {
        return new Object[][]  {
                {"simple",                                  "abc",         "abc"},
                {"simple in apostrophe",                    "\"abc\"",     "abc"},
                {"simple with starting apostrophe",         "\"abc",       "\"abc"},
                {"simple with ending apostrophe",           "abc\"",       "abc\""},
                {"escaped new line",                        "abc\\ndef",   "abc\ndef"},
                {"escaped apostrophe",                      "abc\\\"def",  "abc\"def"},
                {"escaped curly bracken open",              "abc\\{def",   "abc{def"},
                {"escaped curly bracken close",             "abc\\}def",   "abc}def"},
                {"escaped backslash",                       "abc\\\\def",  "abc\\def"},
                {"complex with escaped backslash and n",    "abc\\\\ndef", "abc\\ndef"},
        };
    }

    /**
     * Positive test to test unescaping {@code _input} against expected
     * {@code _value}.
     *
     * @param _descr        description
     * @param _input        input string to test
     * @param _expected     expected result
     */
    @Test(dataProvider = "simpleData")
    public void positiveTestSimple(final String _descr,
                                   final String _input,
                                   final String _expected)
    {
        new AbstractParser_mxJPO()
        {
            {
                Assert.assertEquals(this.getString(_input), _expected);
            }
        };
    }
}
