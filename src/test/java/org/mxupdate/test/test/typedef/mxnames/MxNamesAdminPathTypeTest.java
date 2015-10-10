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
import java.util.TreeSet;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.PathTypeData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the JPO
 * {@link org.mxupdate.typedef.mxnames.MxNamesAdminPathType_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminPathTypeTest
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
        final PathTypeData pathType = new PathTypeData(this, "Test").create();

        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final SortedSet<String> tmp = paramCache.getMapping().getTypeDef(CI.DM_PATHTYPE.updateType).matchMxNames(paramCache, Arrays.asList(new String[]{AbstractTest.PREFIX + "*"}));

        Assert.assertEquals(tmp, new TreeSet<>(Arrays.asList(pathType.getName())));
    }

    /**
     * Positive test that local path types are ignored.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that local path types are ignored")
    public void positiveTestLocalPathTypesIgnored()
        throws Exception
    {
        // create type with local path type
        final TypeData type = new TypeData(this, "Test").create();
        MqlBuilderUtil_mxJPO.mql().cmd("escape add pathtype ").arg(AbstractTest.PREFIX + "MyPathType").cmd(" owner type ").arg(type.getName()).exec(this.getContext());

        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final SortedSet<String> tmp = paramCache.getMapping().getTypeDef(CI.DM_PATHTYPE.updateType).matchMxNames(paramCache, Arrays.asList(new String[]{AbstractTest.PREFIX + "*"}));

        Assert.assertEquals(tmp, new TreeSet<>(Arrays.asList()));
    }

    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_PATHTYPE);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}
