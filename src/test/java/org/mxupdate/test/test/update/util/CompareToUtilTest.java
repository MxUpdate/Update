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

import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link CompareToUtil_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class CompareToUtilTest<T>
{
    @DataProvider(name = "simpleData")
    public Object[][] getData()
    {
        return new Object[][]  {
                {"abc",         "abc",              0},
                {"abc",         "abcd",            -1},
                {"abcd",        "abc",              1},
                {1,             1,                  0},
                {1,             2,                 -1},
                {2,             1,                  1},
                {new Comparable<Object>(){
                    @Override
                    public int compareTo(final Object paramT)
                    {
                        return 0;
                    }},          2 ,                0}
        };
    }

    @Test(dataProvider = "simpleData")
    public void compareSimple(final Comparable<T> _input1,
                              final Comparable<T> _input2,
                              final Integer _expected)
    {
        Assert.assertEquals((Object) CompareToUtil_mxJPO.compare(0, _input1, _input2), _expected);
    }
}
