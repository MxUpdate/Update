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

package org.mxupdate.test.test.update.user;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AssociationData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.user.Association_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Association_mxJPO association CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class AssociationCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Association_mxJPO,AssociationData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) uuid",
                    new AssociationData(this, "Test"),
                    new AssociationData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"2a) symbolic name",
                    new AssociationData(this, "Test"),
                    new AssociationData(this, "Test").setValue("symbolicname", "association_123")},
            {"2b) two symbolic name",
                    new AssociationData(this, "Test"),
                    new AssociationData(this, "Test").setValue("symbolicname", "association_123").setValue("symbolicname", "association_345")},
            {"3) description",
                    new AssociationData(this, "Test"),
                    new AssociationData(this, "Test").setValue("description", "abc def")},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_ASSOCIATION);
    }

    @Override
    protected Association_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Association_mxJPO(_name);
    }
}
