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

package org.mxupdate.test.test.install;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.install.InstallDataModel_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the JPO method {@link InstallDataModel_mxJPO#evalAttributes}.
 *
 * @author The MxUpdate Team
 */
public class InstallDataModel_EvalAttributesTest
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
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final SortedSet<String> attrs = new TreeSet<>();
        new InstallDataModel_mxJPO() {
            {
                attrs.addAll(this.evalAttributes(paramCache));
            }
        };

        Assert.assertEquals(
                attrs,
                new TreeSet<>(Arrays.asList(new String[]{"MxUpdate File Date", "MxUpdate Installed Date", "MxUpdate Installer", "MxUpdate Sub Path"})));
    }
}
