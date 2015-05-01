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
import org.mxupdate.test.data.userinterface.FormData;
import org.mxupdate.test.test.update.AbstractDeltaNoChangeTest;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.userinterface.Form_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Form_mxJPO form CI} delta calculation for default
 * values.
 *
 * @author The MxUpdate Team
 */
@Test()
public class FormCI_2DeltaDefaultTest
    extends AbstractDeltaNoChangeTest<Form_mxJPO,FormData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0a) simple w/o values",
                    new FormData(this, "Test")},
            {"0b) with defined default values",
                    new FormData(this, "Test")
                            .setValue("description", "")
                            .setFlag("hidden", false)},
            {"1) with field",
                new FormData(this, "Test")
                        .setValue("description", "")
                        .setFlag("hidden", false)
                        .newField("field").getFormTable()},

       };
    }


    /**
     * Positive test against not parsed form with field (to check that default
     * values from MX and defined are equal).
     *
     * @param _description  not used
     * @param _currentData  current starting data
     * @throws Exception if test failed
     */
    @Test(description = "positive test against not parsed form with field (to check that default values from MX and defined are equal)")
    public void positivTestAgainstNotParsedWithField()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final WrapperCIInstance<Form_mxJPO> currentWrapper = new WrapperCIInstance<Form_mxJPO>(this.createNewData(paramCache, AbstractTest.PREFIX + "Test"));
        currentWrapper.parseUpdate(new FormData(this, "Test").newField("field").getFormTable());
        currentWrapper.create(paramCache);
        currentWrapper.store(null, paramCache);

        final WrapperCIInstance<Form_mxJPO> previousWrapper = new WrapperCIInstance<Form_mxJPO>(this.createNewData(paramCache, AbstractTest.PREFIX + "Test"));
        previousWrapper.parseUpdate(new FormData(this, "Test")
                .newField("field")
                .setValue("label", "")
                .setValue("range", "")
                .setValue("href", "")
                .setValue("alt", "")
                .setValue("select", "")
                .getFormTable());

        final MultiLineMqlBuilder mql = currentWrapper.calcDelta(paramCache, previousWrapper);

        Assert.assertFalse(mql.hasNewLines(), "no MQL update needed, but found:\n" + mql);
    }

    @Override()
    @BeforeMethod()
 //   @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_FORM);
    }

    @Override()
    protected Form_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Form_mxJPO(_paramCache.getMapping().getTypeDef(CI.UI_FORM.updateType), _name);
    }
}
