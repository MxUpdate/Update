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

import org.mxupdate.util.StringUtils_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link StringUtils_mxJPO#isEmpty(CharSequence)} method.
 *
 * @author The MxUpdate Team
 */
public class StringUtils_IsEmptyTest
{
    /**
     * Prepares the test data to check.
     *
     * @return test data
     */
    @DataProvider(name = "strings")
    public Object[][] dataStrings()
    {
        return new Object[][] {
            {null,      true},
            {"",        true},
            {" ",       false},
            {"abc",     false},
            {"  abc  ", false},
        };
    }

    /**
     * Simple positive test.
     *
     * @param _input        input string to test
     * @param _expected     expected result
     */
    @Test(description = "simple positive test",
          dataProvider = "strings")
    public void positiveTestSimple(final String _input,
                                   final boolean _expected)
    {
        Assert.assertEquals(
                StringUtils_mxJPO.isEmpty(_input),
                _expected);
    }
}
