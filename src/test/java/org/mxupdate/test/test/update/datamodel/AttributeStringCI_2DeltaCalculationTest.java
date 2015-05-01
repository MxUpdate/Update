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

package org.mxupdate.test.test.update.datamodel;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.AttributeString_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AttributeString_mxJPO attribute} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeStringCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<AttributeString_mxJPO,AttributeStringData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"with maxlength",
                    new AttributeStringData(this, "Test"),
                    new AttributeStringData(this, "Test").setSingle("maxlength", "5")},
       };
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_STRING);
    }

    @Override()
    protected AttributeString_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                                  final String _name)
    {
        return new AttributeString_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_ATTRIBUTE_STRING.updateType), _name);
    }
}
