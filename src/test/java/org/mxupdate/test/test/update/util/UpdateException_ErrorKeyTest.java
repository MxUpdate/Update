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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests that the messages for all error keys are correct defined and the
 * arguments are evaluated.
 *
 * @author The MxUpdate Team
 */
public class UpdateException_ErrorKeyTest
{
    /**
     * Returns all error keys to test.
     *
     * @return test data
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();
        for (final ErrorKey errorKey : ErrorKey.values())  {
            ret.add(new Object[]{errorKey});
        }
        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * Positive test to check error keys.
     *
     * @param _errorKey     error key to test
     */
    @Test(description = "positive test to check error keys",
          dataProvider = "data")
    public void positiveTest(final ErrorKey _errorKey)
    {
        final List<String> args = new ArrayList<String>();
        for (int num = 0; num < StringUtils.countMatches(_errorKey.getText(), '{'); num++)  {
            args.add("a");
        }
        final String msg = MessageFormat.format(_errorKey.getText(), args.toArray());
        Assert.assertEquals(
                StringUtils.countMatches(msg, '{'),
                0,
                "check that all indexes are evaluated for [" + msg + "]");
    }
}
