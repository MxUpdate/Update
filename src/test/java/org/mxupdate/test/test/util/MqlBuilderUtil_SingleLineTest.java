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

import junit.framework.Assert;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link MqlBuilderUtil_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class MqlBuilderUtil_SingleLineTest
    extends AbstractTest
{
    /**
     * Removes the MxUpdate types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    /**
     * Simple positive test.
     *
     * @param _descr    description to text
     * @throws Exception
     */
    @Test(description = "simple positive test")
    public void positiveTestSimple()
        throws Exception
    {
        final TypeData type = new TypeData(this, "name");
        type.create();

        Assert.assertEquals(
                MqlBuilderUtil_mxJPO.mql()
                        .cmd("escape print type ").arg(AbstractTest.PREFIX + "name").cmd(" select ").arg("name").cmd(" dump")
                        .exec(this.getContext()),
                AbstractTest.PREFIX + "name");
    }
}
