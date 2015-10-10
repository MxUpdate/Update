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

import java.io.File;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.userinterface.TableData;
import org.mxupdate.test.test.update.AbstractDeltaNoChangeTest;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Table_mxJPO table CI} delta calculation for default
 * values.
 *
 * @author The MxUpdate Team
 */
@Test()
public class TableCI_2DeltaDefaultTest
    extends AbstractDeltaNoChangeTest<Table_mxJPO,TableData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0a) simple w/o values",
                    new TableData(this, "Test")},
            {"0b) with defined default values",
                    new TableData(this, "Test")
                            .setValue("description", "")
                            .setFlag("hidden", false)},
            {"1) with field",
                new TableData(this, "Test")
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

        final WrapperCIInstance<Table_mxJPO> currentWrapper = new WrapperCIInstance<>(this.createNewData(paramCache, AbstractTest.PREFIX + "Test"));
        currentWrapper.parseUpdate(new TableData(this, "Test").newField("field").getFormTable());
        currentWrapper.create(paramCache);
        currentWrapper.store(null, paramCache);

        final WrapperCIInstance<Table_mxJPO> previousWrapper = new WrapperCIInstance<>(this.createNewData(paramCache, AbstractTest.PREFIX + "Test"));
        previousWrapper.parseUpdate(new TableData(this, "Test")
                .newField("field")
                .setValue("label", "")
                .setValue("range", "")
                .setValue("href", "")
                .setValue("alt", "")
                .setValue("select", "")
                .getFormTable());

        final MultiLineMqlBuilder mql = currentWrapper.calcDelta(paramCache, (File) null, previousWrapper);

        Assert.assertFalse(mql.hasNewLines(), "no MQL update needed, but found:\n" + mql);
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_TABLE);
    }

    @Override
    protected Table_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Table_mxJPO(_name);
    }
}
