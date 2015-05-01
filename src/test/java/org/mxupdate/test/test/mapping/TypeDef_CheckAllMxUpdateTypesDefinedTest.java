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

package org.mxupdate.test.test.mapping;

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Checks that all MxUpdate types are defined inside {@link TypeDef_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class TypeDef_CheckAllMxUpdateTypesDefinedTest
    extends AbstractTest
{
    /**
     * Returns the test data.
     *
     * @return test data
     * @throws Exception if initialize failed
     */
    @DataProvider(name = "data")
    public Object[][] getData()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final List<Object[]> ret = new ArrayList<Object[]>();
        for (final TypeDef_mxJPO typeDef : paramCache.getMapping().getAllTypeDefsSorted())  {
            ret.add(new Object[]{typeDef});
        }
        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * Positive test to check mxUpdate types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to check mxUpdate types",
          dataProvider = "data")
    public void positiveTestCheck(final TypeDef_mxJPO _typeDef)
        throws Exception
    {
        Assert.assertTrue(
                (_typeDef.getMxUpdateType() != null) && !_typeDef.getMxUpdateType().isEmpty(),
                "check MxUpdate type is not defined for " + _typeDef.getName());
    }
}
