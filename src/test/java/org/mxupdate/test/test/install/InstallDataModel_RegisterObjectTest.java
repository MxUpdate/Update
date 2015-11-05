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

import org.mxupdate.install.InstallDataModel_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the JPO method {@link InstallDataModel_mxJPO#registerObject}.
 *
 * @author The MxUpdate Team
 */
public class InstallDataModel_RegisterObjectTest
    extends AbstractTest
{
    /**
     * Positive test to define new symbolic name.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to define new symbolic name")
    public void positiveTestAddNew()
        throws Exception
    {
        final MQLProgramData prog = new MQLProgramData(this, "Test").setSymbolicName(null).create();
        new MQLProgramData(this, "Test")
                .defKeyNotDefined("symbolicname")
                .checkExport();

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new InstallDataModel_mxJPO() {
            {
                this.registerObject(paramCache, "program", prog.getName());
            }
        };

        new MQLProgramData(this, "Test")
                .setValue("symbolicname", "program_MXUPDATE_Test")
                .checkExport();
    }

    /**
     * Positive test wtih existing wrong symbolic name.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test wtih existing wrong symbolic name")
    public void positiveTestWrongSymbolicName()
        throws Exception
    {
        final MQLProgramData prog = new MQLProgramData(this, "Test").setSymbolicName("WRONG_SYMBOLIC_NAME").create();
        new MQLProgramData(this, "Test")
                .setValue("symbolicname", "WRONG_SYMBOLIC_NAME")
                .checkExport();

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new InstallDataModel_mxJPO() {
            {
                this.registerObject(paramCache, "program", prog.getName());
            }
        };

        new MQLProgramData(this, "Test")
                .setValue("symbolicname", "program_MXUPDATE_Test")
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
        this.cleanup(CI.PRG_MQL);
    }
}
