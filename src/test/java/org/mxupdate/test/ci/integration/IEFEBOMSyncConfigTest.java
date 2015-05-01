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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.BusData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of IEF EBOM sync configuration objects.
 *
 * @author The MxUpdate Team
 */
@Test()
public class IEFEBOMSyncConfigTest
    extends AbstractIEFTest
{
    @Override()
    protected BusData createNewData(final boolean _subType,
                                    final String _name)
    {
        return new BusData(
                this,
                CI.IEF_EBOMSYNC_CONFIG,
                _subType ? new TypeData(this, "EBOMSync").setValue("derived", "IEF-EBOMSyncConfig") : null,
                _name,
                "-");
    }

    /**
     * Cleanup all test integration EBOM sync configuration objects and data
     * model types (because created as derived types).
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.IEF_EBOMSYNC_CONFIG);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}
