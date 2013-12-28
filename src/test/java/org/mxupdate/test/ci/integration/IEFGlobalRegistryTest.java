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
import org.mxupdate.test.data.integration.IEFGlobalRegistryData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

/**
 * Test cases for the export and update of integration global registry objects.
 *
 * @author The MxUpdate Team
 */
public class IEFGlobalRegistryTest
    extends AbstractIEFTest<IEFGlobalRegistryData>
{
    /**
     * {@inheritDoc}
     * Returns new IEF global registry instance.
     */
    @Override()
    protected IEFGlobalRegistryData createNewData(final boolean _subType,
                                                  final String _name)
    {
        return new IEFGlobalRegistryData(
                this,
                _name,
                "1");
    }

    /**
     * Cleanup all test integration global registry objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.IEF_GLOBAL_REGISTRY);
    }
}
