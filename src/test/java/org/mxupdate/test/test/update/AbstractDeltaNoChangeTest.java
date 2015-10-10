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

package org.mxupdate.test.test.update;

import java.io.File;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Common definitions for delta calculation tests.
 *
 * @author The MxUpdate Team
 * @param <DATA> class of the data
 */
public abstract class AbstractDeltaNoChangeTest<DATA extends AbstractAdminObject_mxJPO<?>,TESTDATA extends AbstractAdminData<?>>
    extends AbstractTest
{
    /**
     * Returns data providers used for testing parses.
     *
     * @return test source code to parse
     */
    @DataProvider(name = "data")
    public abstract Object[][] getData();

    /**
     * Creates for given {@code _name} related data instance.
     *
     * @param _paramCache   parameter cache
     * @param _name         name of the test object
     * @return new create data instance
     */
    protected abstract DATA createNewData(final ParameterCache_mxJPO _paramCache,
                                          final String _name);

    /**
     * Cleanups generated test data.
     *
     * @throws MatrixException if cleanup fails
     */
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public abstract void cleanup()
            throws MatrixException;

    /**
     * Tests the delta calculation.
     *
     * @param _description  not used
     * @param _currentData  current starting data
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data")
    public void positivTest(final String _description,
                            final TESTDATA _currentData)
        throws Exception
    {
        if (_currentData.isSupported(this.getVersion()))  {
            final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

            // work-around: policies must be created manually...
            if (_currentData instanceof PolicyData)  {
                new PolicyData(this, _currentData.getName().substring(AbstractTest.PREFIX.length())).create();
            }
            // create the depending objects to be able to connect to them
            _currentData.createDependings();

            // prepare the current
            final WrapperCIInstance<DATA> currentWrapper = new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName()));
            currentWrapper.create(paramCache);
            currentWrapper.parseUpdate(_currentData);
            currentWrapper.calcDelta(paramCache, (File) null, new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName()))).exec(paramCache);

            // read from MX
            final WrapperCIInstance<DATA> currentWrapper2Mx = new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName()));
            currentWrapper2Mx.parse(paramCache);
            // parse again (to ensure empty not changed values!)
            final WrapperCIInstance<DATA> currentWrapper2Update = new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName()));
            currentWrapper2Update.parseUpdate(_currentData);
            // calculate delta between MX and new parsed
            final MultiLineMqlBuilder mql = currentWrapper2Update.calcDelta(paramCache, (File) null, currentWrapper2Mx);

            Assert.assertFalse(mql.hasNewLines(), "no MQL update needed, but found:\n" + mql);
        }
    }

    /**
     * Positive test against not parsed values (to check that default values
     * from MX and defined are equal).
     *
     * @param _description  not used
     * @param _currentData  current starting data
     * @throws Exception if test failed
     */
    @Test(description = "positive test against not parsed values (to check that default values from MX and defined are equal)")
    public void positivTestAgainstNotParsed()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final WrapperCIInstance<DATA> currentWrapper = new WrapperCIInstance<DATA>(this.createNewData(paramCache, AbstractTest.PREFIX + "Test"));
        currentWrapper.create(paramCache);
        final MultiLineMqlBuilder mql = currentWrapper.calcDelta(paramCache, (File) null, new WrapperCIInstance<DATA>(this.createNewData(paramCache, AbstractTest.PREFIX + "Test")));

        Assert.assertFalse(mql.hasNewLines(), "no MQL update needed, but found:\n" + mql);
    }
}
