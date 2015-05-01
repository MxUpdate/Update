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

package org.mxupdate.test.test.update.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.userinterface.PortalData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Portal_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Portal_mxJPO portal CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PortalCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Portal_mxJPO,PortalData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
                {"1) simple test",
                    new PortalData(this, "Portal1"),
                    new PortalData(this, "Portal1").setValue("description", "test").setValue("label", "").setValue("href", "").setValue("alt", "")},
                {"2a) symbolic name",
                    new PortalData(this, "Test"),
                    new PortalData(this, "Test").setValue("symbolicname", "expression_123")},
                {"2b) two symbolic name",
                    new PortalData(this, "Test"),
                    new PortalData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
       };
    }

    @Override
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_PORTAL);
    }

    @Override()
    protected Portal_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Portal_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_PORTAL.updateType), _name);
    }
}
