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

package org.mxupdate.test.test.update.datamodel.numbergeneratorci;

import matrix.util.MatrixException;

import org.mxupdate.test.data.BusData;
import org.mxupdate.test.test.update.AbstractBusUpdateTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the number generator CI export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class NumberGeneratorCI_3UpdateTest
    extends AbstractBusUpdateTest<BusData>
{
    /**
     * Positive test that next number is not updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that next number is not updated")
    public void positveTestUpdateNextNumber()
        throws Exception
    {
        final BusData busDataInit   = this.createNewData("Test", "Test").setKeyValue("attribute", "eService Next Number", "123");
        final BusData busDataUpdate = this.createNewData("Test", "Test").setKeyValue("attribute", "eService Next Number", "1");

        // first update with original content
        busDataInit
                .create()
                .update((String) null)
                .checkExport();

        // second update with delivered content
        busDataUpdate
                .update((String) null);

        busDataInit
                .checkExport();
    }

    @Override()
    protected BusData createNewData(final String _name,
                                    final String _revision)
    {
        return new BusData(this, CI.DM_NUMBERGENERATOR, _name, _revision);
    }

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.DM_NUMBERGENERATOR);
    }
}
