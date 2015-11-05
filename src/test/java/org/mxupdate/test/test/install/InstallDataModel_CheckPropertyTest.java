
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
 * Tests the JPO method {@link InstallDataModel_mxJPO#checkProperty}.
 *
 * @author The MxUpdate Team
 */
public class InstallDataModel_CheckPropertyTest
    extends AbstractTest
{
    /**
     * Positive test to define new property value.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to define new property value")
    public void positiveTestAddNew()
        throws Exception
    {
        final MQLProgramData prog = new MQLProgramData(this, "Test").create();
        new MQLProgramData(this, "Test")
                .defKeyNotDefined("property")
                .checkExport();

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new InstallDataModel_mxJPO() {
            {
                this.checkProperty(paramCache, "program", prog.getName(), "PropName", "PropValue", false);
            }
        };

        new MQLProgramData(this, "Test")
                .defText("property", "\"PropName\" value \"PropValue\"")
                .checkExport();
    }

    /**
     * Positive test to update existing property value.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to update existing property value")
    public void positiveTestUpdateExisting()
        throws Exception
    {
        final MQLProgramData prog = new MQLProgramData(this, "Test").create();
        new MQLProgramData(this, "Test")
                .defKeyNotDefined("property")
                .checkExport();

        this.mql().cmd("escape mod prog ").arg(prog.getName()).cmd(" add property ").arg("PropName").cmd(" value ").arg("OldValue").exec(this.getContext());

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new InstallDataModel_mxJPO() {
            {
                this.checkProperty(paramCache, "program", prog.getName(), "PropName", "PropValue", false);
            }
        };

        new MQLProgramData(this, "Test")
                .defText("property", "\"PropName\" value \"PropValue\"")
                .checkExport();
    }

    /**
     * Positive test not update existing property.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test not update existing property")
    public void positiveTestNoUpdateIfExists()
        throws Exception
    {
        final MQLProgramData prog = new MQLProgramData(this, "Test").create();
        new MQLProgramData(this, "Test")
                .defKeyNotDefined("property")
                .checkExport();

        this.mql().cmd("escape mod prog ").arg(prog.getName()).cmd(" add property ").arg("PropName").cmd(" value ").arg("OldValue").exec(this.getContext());

        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new InstallDataModel_mxJPO() {
            {
                this.checkProperty(paramCache, "program", prog.getName(), "PropName", "PropValue", true);
            }
        };

        new MQLProgramData(this, "Test")
                .defText("property", "\"PropName\" value \"OldValue\"")
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
