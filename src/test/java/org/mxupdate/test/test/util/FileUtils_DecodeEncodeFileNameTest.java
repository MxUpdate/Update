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

import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.FileUtils_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO methods {@link FileUtils_mxJPO#decodeFileName(String)} and
 * {@link FileUtils_mxJPO#encodeFileName(String)}.
 *
 * @author The MxUpdate Team
 */
public class FileUtils_DecodeEncodeFileNameTest
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
            {"2) new line",                 "abc\nnew line",        "abc@0anew line"},
            {"3) encoding of @",            "abc@new line",         "abc@@new line"},
            {"4) encoding of tabulator",    "abc\tnew text",        "abc@09new text"},
            {"5) encoding of bell",         "abc\u0007new text",    "abc@07new text"},
            {"6) encoding of ETB",          "abc\u0017new text",    "abc@17new text"},
            {"7) encoding of an char",      "abc\u0110new text",    "abc@u0110new text"},
            {"8) encoding of euro",         "abc\u20acnew text",    "abc@u20acnew text"},
            {"10) quotion mark",            "abc\"",                "abc@22"},
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
                FileUtils_mxJPO.decodeFileName(_encode),
                _input);
    }

    /**
     * Positive test to decode null string to empty string.
     */
    @Test(description = "positive test to decode null string to empty string")
    public void positiveTestDecodeNull()
        throws Exception
    {
        Assert.assertEquals(
                FileUtils_mxJPO.decodeFileName(null),
                "");
    }

    /**
     * Returns the file names which are wrong encoded.
     *
     * @return file names
     */
    @DataProvider(name = "wrongFileNames")
    public Object[][] dataWrongFileNames()
    {
        return new Object[][] {
            {"abc@"},
            {"abc@0"},
            {"abc@u"},
            {"abc@u0"},
            {"abc@u00"},
            {"abc@u000"},
        };
    }

    /**
     * Negative test for wrong encoded file name.
     *
     * @param _fileName     file name to test
     * @throws Exception if test failed
     */
    @Test(description = "negative test for wrong encoded file name",
          dataProvider = "wrongFileNames",
          expectedExceptions = UpdateException_mxJPO.class,
          expectedExceptionsMessageRegExp = "^UpdateError #90703.*$")
    public void negativeTestDecodeWrongEncodedFileName(final String _fileName)
        throws Exception
    {
        FileUtils_mxJPO.decodeFileName(_fileName);
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
                FileUtils_mxJPO.encodeFileName(_input),
                _encode);
    }

    /**
     * Positive test to encode null string to empty string.
     */
    @Test(description = "positive test to encode null string to empty string")
    public void positiveTestEncodeNull()
    {
        Assert.assertEquals(
                FileUtils_mxJPO.encodeFileName(null),
                "");
    }
}
