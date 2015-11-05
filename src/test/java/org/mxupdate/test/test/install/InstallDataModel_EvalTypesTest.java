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
 * Tests the JPO method {@link InstallDataModel_mxJPO#evalTypes}.
 *
 * @author The MxUpdate Test
 */
public class InstallDataModel_EvalTypesTest
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

        final SortedSet<String> types = new TreeSet<>();
        new InstallDataModel_mxJPO() {
            {
                types.addAll(this.evalTypes(paramCache));
            }
        };

        Assert.assertEquals(
                types,
                new TreeSet<>(Arrays.asList(new String[]{
                        "IEF-EBOMSyncConfig", "IEF-GlobalRegistry", "IEF-MassPromoteConfig", "IEF-UnassignedIntegRegistry", "MCADInteg-GlobalConfig",
                        "Notification",
                        "eService Number Generator", "eService Object Generator",
                        "eService Trigger Program Parameters"})));
    }
}
