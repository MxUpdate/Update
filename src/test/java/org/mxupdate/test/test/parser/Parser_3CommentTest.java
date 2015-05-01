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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for parsing of comments.
 *
 * @author The MxUpdate Team
 */
public class Parser_3CommentTest
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
            {"1) between multi-line-string",            "abc",         "multi-line-string #commen\nabc"},
            {"1) between string",                       "abc",         "string #commen\nabc"},
            {"1) in front of multi-line-string",        "abc",         " #commen\n multi-line-string abc"},
            {"1) in front of string",                   "abc",         " #commen\n string abc"},
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
        Assert.assertEquals(_expString, new ParserTestImpl(new StringReader(_parsingString)).test3Comment());
    }
}
