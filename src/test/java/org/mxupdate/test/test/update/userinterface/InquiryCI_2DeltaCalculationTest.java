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
import org.mxupdate.test.data.userinterface.InquiryData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Inquiry_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Inquiry_mxJPO inquiry CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class InquiryCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Inquiry_mxJPO,InquiryData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) symbolic name",
                    new InquiryData(this, "Test"),
                    new InquiryData(this, "Test").setValue("symbolicname", "expression_123")},
            {"1b) two symbolic name",
                    new InquiryData(this, "Test"),
                    new InquiryData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
            {"2) description",
                    new InquiryData(this, "Test"),
                    new InquiryData(this, "Test").setValue("description", "abc def")},
       };
    }

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_INQUIRY);
    }

    @Override()
    protected Inquiry_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Inquiry_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_INQUIRY.updateType), _name);
    }

}
