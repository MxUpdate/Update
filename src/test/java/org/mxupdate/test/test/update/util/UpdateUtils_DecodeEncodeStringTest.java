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

import org.mxupdate.update.util.UpdateUtils_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link UpdateUtils_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class UpdateUtils_DecodeEncodeStringTest
{
    /**
     * Returns the data to test.
     *
     * @return test data
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) simple test",              "abc",                  "abc"},
            {"2) new line",                 "abc\nnew line",        "abc\nnew line"},
            {"3) encoding of backslah",     "abc\\new line",        "abc\\\\new line"},
            {"4) encoding of tabulator",    "abc\tnew text",        "abc\\tnew text"},
            {"5) encoding of bell",         "abc\u0007new text",    "abc\\u0007new text"},
            {"6) encoding of ETB",          "abc\u0017new text",    "abc\\u0017new text"},
            {"7) encoding of an char",      "abc\u0110new text",    "abc\\u0110new text"},
            {"8) encoding of euro",         "abc\u20acnew text",    "abc\\u20acnew text"},
            {"10) quotion mark",            "abc\"",                "abc\\\""},
            {"11) braces",                  "{}",                   "{}"},
            {"12) braces",                  "{{}",                  "{\\{}"},
            {"13) braces",                  "{{{}}",                "{{\\{}}"},
            {"14) braces",                  "{{}}}",                "{{}}\\}"},
            {"15) braces",                  "{{}}}}{}",             "{{}}\\}\\}{}"},
        };
    }

    /**
     * Decodes the given {@code _encode} text.
     *
     * @param _descr    description
     * @param _input    orginal string
     * @param _encode   encoded string
     * @throws Exception if test failed
     */
    @Test(description = "positive to decode",
          dataProvider = "data")
    public void positiveTestDecode(final String _descr,
                                   final String _input,
                                   final String _encode)
        throws Exception
    {
        Assert.assertEquals(
                UpdateUtils_mxJPO.decodeText(_encode),
                _input);
    }

    /**
     * Decodes a text with an encoded new line (in the case the user enters
     * them manually}.
     * @throws Exception if test failed
     */
    @Test(description = "decodes encoded new line")
    public void positiveTestDecodeNewLine()
        throws Exception
    {
        Assert.assertEquals(
                UpdateUtils_mxJPO.decodeText("abc\\ndef"),
                "abc\ndef");
    }

    /**
     * Positive test to decode null string to empty string.
     */
    @Test(description = "positive test to decode null string to empty string")
    public void positiveTestDecodeNull()
    {
        Assert.assertEquals(
                UpdateUtils_mxJPO.decodeText(null),
                "");
    }

    /**
     * Encodes the given {@code _input} text.
     *
     * @param _descr    description
     * @param _input    orginal string
     * @param _encode   encoded string
     * @throws Exception if test failed
     */
    @Test(description = "positive to encode",
          dataProvider = "data")
    public void positiveTestEncode(final String _descr,
                                   final String _input,
                                   final String _encode)
    {
        Assert.assertEquals(
                UpdateUtils_mxJPO.encodeText(_input),
                _encode);
    }

    /**
     * Positive test to encode null string to empty string.
     */
    @Test(description = "positive test to encode null string to empty string")
    public void positiveTestEncodeNull()
    {
        Assert.assertEquals(
                UpdateUtils_mxJPO.encodeText(null),
                "");
    }
}
