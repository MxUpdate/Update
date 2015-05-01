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

package org.mxupdate.test.test.typedef.mxnames;

import java.util.Arrays;
import java.util.SortedSet;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.userinterface.TableData;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the JPO
 * {@link org.mxupdate.typedef.mxnames.MxNamesAdminUserInterfaceTable_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminUserInterfaceTableTest
    extends AbstractTest
{
    /**
     * Simple positive test.
     *
     * @throws Exception if test failed
     */
    @Test(description = "simple positive test")
    public void positiveTestSimple()
        throws Exception
    {
        final TableData data = new TableData(this, "Test").create();

        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final SortedSet<String> tmp = paramCache.getMapping().getTypeDef(CI.UI_TABLE.updateType).matchMxNames(paramCache, Arrays.asList(new String[]{data.getName()}));
        Assert.assertEquals(
                tmp.size(),
                1);
        Assert.assertEquals(
                tmp.iterator().next(),
                data.getName());
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_TABLE);
    }
}
