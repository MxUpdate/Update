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
import java.util.TreeSet;

import org.mxupdate.install.InstallDataModel_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the JPO method {@link InstallDataModel_mxJPO#updateTypes}.
 *
 * @author The MxUpdate Team
 */
public class InstallDataModel_UpdateTypesTest
    extends AbstractTest
{
    /**
     * Positive test that attribute is assigned to type.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that attribute is assigned to type")
    public void positiveTestAttributeSimple()
        throws Exception
    {
        final TypeData type = new TypeData(this, "Test").create();
        final AttributeData attr = new AttributeData(this, "Test").setSingle("kind", "string").create();

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        // first call, attribute is appeneded
        new InstallDataModel_mxJPO() {
            {
                this.updateBusTypes(
                        paramCache,
                        new TreeSet<>(Arrays.asList(new String[]{type.getName()})),
                        new TreeSet<>(Arrays.asList(new String[]{attr.getName()})));
            }
        };

        new TypeData(this, "Test")
                .setValue("attribute", attr.getName())
                .checkExport();

        // second call (to check if update works)
        new InstallDataModel_mxJPO() {
            {
                this.updateBusTypes(
                        paramCache,
                        new TreeSet<>(Arrays.asList(new String[]{type.getName()})),
                        new TreeSet<>(Arrays.asList(new String[]{attr.getName()})));
            }
        };

        new TypeData(this, "Test")
                .setValue("attribute", attr.getName())
                .checkExport();
     }

    /**
     * Cleanup all test data.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.DM_ATTRIBUTE);
        this.cleanup(CI.DM_TYPE);
    }
}
