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

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
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
            currentWrapper.parseUpdate(this.strip(currentWrapper.getTypeDef(), _currentData.ciFile()));
            final MultiLineMqlBuilder mql1;
            if ((currentWrapper.getTypeDef().getMxAdminSuffix()) != null && !currentWrapper.getTypeDef().getMxAdminSuffix().isEmpty())  {
                mql1 = MqlBuilder_mxJPO.multiLine("escape mod " + currentWrapper.getTypeDef().getMxAdminName() + " $1 " + currentWrapper.getTypeDef().getMxAdminSuffix(), _currentData.getName());
            } else  {
                mql1 = MqlBuilder_mxJPO.multiLine("escape mod " + currentWrapper.getTypeDef().getMxAdminName() + " $1", _currentData.getName());
            }
            currentWrapper.calcDelta(paramCache, mql1,  new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName())));
            mql1.exec(paramCache);

            // read from MX
            final WrapperCIInstance<DATA> currentWrapper2Mx = new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName()));
            currentWrapper2Mx.parse(paramCache);
            // parse again (to ensure empty not changed values!)
            final WrapperCIInstance<DATA> currentWrapper2Update = new WrapperCIInstance<DATA>(this.createNewData(paramCache, _currentData.getName()));
            currentWrapper2Update.parseUpdate(this.strip(currentWrapper.getTypeDef(), _currentData.ciFile()));
            // calculate delta between MX and new parsed
            final MultiLineMqlBuilder mql2 = MqlBuilder_mxJPO.multiLine("escape mod " + currentWrapper.getTypeDef().getMxAdminName() + " $1 " + currentWrapper.getTypeDef().getMxAdminSuffix(), _currentData.getName());
            currentWrapper2Update.calcDelta(paramCache, mql2, currentWrapper2Mx);

            Assert.assertFalse(mql2.hasNewLines(), "no MQL update needed, but found:\n" + mql2);
        }
    }

    /**
     * Strips the update code.
     *
     * @param _typeDef      type definition
     * @param _generated    code to clean
     * @return stripped update code
     */
    protected String strip(final TypeDef_mxJPO _typeDef,
                           final String _generated)
    {
        final StringBuilder newDef = new StringBuilder();
        final String startIndex = "mxUpdate " + _typeDef.getMxAdminName() + " \"${NAME}\" {";
        final int start = _generated.indexOf(startIndex) + startIndex.length() + 1;
        final int end = _generated.length() - 2;
        if (start < end)  {
            final String temp = _generated.substring(start, end).toString();
            for (final String line : temp.split("\n"))  {
                newDef.append(line.trim()).append(' ');
            }
        }
        return newDef.toString();
    }
}
