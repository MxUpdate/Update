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

package org.mxupdate.test.test.parser;

import java.io.StringReader;

import org.mxupdate.update.util.AbstractParser_mxJPO.TokenMgrError;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for parsing of strings.
 *
 * @author The MxUpdate Team
 */
public class Parser_2MultilineStringTest
{
    /**
     * Returns the positive test data.
     *
     * @return positive test data
     */
    @DataProvider(name = "positiveTestData")
    public Object[][] getPositiveTestData()
    {
        return new Object[][]
        {
            {"1) single",                       "abc",         "abc"},
            {"2) simple",                       "abc",          "\"abc\""},
            {"3) space",                        "abc def",      "\"abc def\""},
            {"4) escaped backslash",            "abc\\def",     "\"abc\\\\def\""},
            {"5) curly braces open",            "abc{def",      "\"abc{def\""},
            {"6) escaped curly braces open",    "abc{def",      "\"abc{def\""},
            {"7) curly braces close",           "abc}def",      "\"abc\\}def\""},
            {"8) escaped curly braces close",   "abc}def",      "\"abc\\}def\""},
            {"9) escaped quotations mark",      "abc\"def",     "\"abc\\\"def\""},
            {"10) new line",                     "abc\ndef",     "\"abc\ndef\""},
            {"11) escaped new line",             "abc\ndef",     "\"abc\\ndef\""},
        };
    }

    /**
     * Positive test.
     *
     * @param _description      not used
     * @param _expString        expected target string
     * @param _parsingString    string to parse
     * @throws Exception if test failed
     */
    @Test(description = "positive test for parsing multiline string",
          dataProvider = "positiveTestData")
    public void positiveTest(final String _description,
                             final String _expString,
                             final String _parsingString)
        throws Exception
    {
        Assert.assertEquals(_expString, new ParserTestImpl(new StringReader("multi-line-string " + _parsingString)).test2MultilineString());
    }

    /**
     * Returns the negative test data.
     *
     * @return negative test data
     */
    @DataProvider(name = "negativeTestData")
    public Object[][] getNegativeTestData()
    {
        return new Object[][]
        {
            {"1) single curly braces open",     "abc{"},
            {"2) single curly braces close",    "abc}"},
            {"3) not escaped backslahes",       "\"\\\""},
        };
    }

    /**
     * Negative test.
     *
     * @param _description      not used
     * @param _expString        expected target string
     * @param _parsingString    string to parse
     * @throws Exception if test failed
     */
    @Test(description = "negative test for parsing multiline string",
          dataProvider = "negativeTestData",
          expectedExceptions = TokenMgrError.class)
    public void negativeTest(final String _description,
                             final String _parsingString)
        throws Exception
    {
        new ParserTestImpl(new StringReader("multi-line-string " + _parsingString)).test2MultilineString();
    }
}
