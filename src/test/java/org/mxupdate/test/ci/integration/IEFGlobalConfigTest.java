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

package org.mxupdate.test.ci.integration;

import matrix.util.MatrixException;

import org.mxupdate.test.data.BusData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of integration global configuration
 * objects.
 *
 * @author The MxUpdate Team
 */
@Test()
public class IEFGlobalConfigTest
    extends AbstractIEFTest
{
    @Override()
    protected BusData createNewData(final boolean _subType,
                                   final String _name)
    {
        return new BusData(
                this,
                CI.IEF_GLOBAL_CONFIG,
                _subType ? new TypeData(this, "GlobalConfig").setValue("derived", "MCADInteg-GlobalConfig") : null,
                _name,
                "1");
    }

    /**
     * Cleanup all test integration global configuration objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.IEF_GLOBAL_CONFIG);
        this.cleanup(CI.DM_TYPE);
    }
}
