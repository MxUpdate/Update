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

import java.lang.reflect.Method;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Common definitions for delta calculation tests.
 *
 * @author The MxUpdate Team
 * @param <DATA> class of the data
 */
public abstract class AbstractDeltaCalculationTest<DATA extends AbstractAdminObject_mxJPO<?>,TESTDATA extends AbstractAdminData<?>>
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
     * Tests the delta calculation.
     *
     * @param _description  not used
     * @param _currentData  current starting data
     * @param _targetData   target and expected data
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data")
    public void positivTest(final String _description,
                            final TESTDATA _currentData,
                            final TESTDATA _targetData)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        Assert.assertEquals(_currentData.getName(), _targetData.getName(), "check that ci names are equal");

        // prepare the current
        final Wrapper currentWrapper = new Wrapper(this.createNewData(paramCache, _currentData.getName()));
        currentWrapper.data.create(paramCache);
        currentWrapper.data.parseUpdate(this.strip(currentWrapper.data.getTypeDef(), _currentData.ciFile()));
        final MultiLineMqlBuilder mql1;
        if ((currentWrapper.data.getTypeDef().getMxAdminSuffix()) != null && !currentWrapper.data.getTypeDef().getMxAdminSuffix().isEmpty())  {
            mql1 = MqlBuilder_mxJPO.multiLine("escape mod " + currentWrapper.data.getTypeDef().getMxAdminName() + " $1 " + currentWrapper.data.getTypeDef().getMxAdminSuffix(), _currentData.getName());
        } else  {
            mql1 = MqlBuilder_mxJPO.multiLine("escape mod " + currentWrapper.data.getTypeDef().getMxAdminName() + " $1", _currentData.getName());
        }
        currentWrapper.calcDelta(paramCache, mql1, this.createNewData(paramCache, _currentData.getName()));
        mql1.exec(paramCache);

        // prepare the target form
        final Wrapper targetWrapper = new Wrapper(this.createNewData(paramCache, _targetData.getName()));
        targetWrapper.data.parseUpdate(this.strip(targetWrapper.data.getTypeDef(), _targetData.ciFile()));

        // delta between current and target
        final MultiLineMqlBuilder mql2;
        if ((targetWrapper.data.getTypeDef().getMxAdminSuffix()) != null && !targetWrapper.data.getTypeDef().getMxAdminSuffix().isEmpty())  {
            mql2 = MqlBuilder_mxJPO.multiLine("escape mod " + targetWrapper.data.getTypeDef().getMxAdminName() + " $1 " + targetWrapper.data.getTypeDef().getMxAdminSuffix(), _currentData.getName());
        } else  {
            mql2 = MqlBuilder_mxJPO.multiLine("escape mod " + targetWrapper.data.getTypeDef().getMxAdminName() + " $1", targetWrapper.data.getName());
        }
        targetWrapper.calcDelta(paramCache, mql2, currentWrapper.data);
        mql2.exec(paramCache);

        // check result from MX defined from calculated delta
        final Wrapper resultWrapper = new Wrapper(this.createNewData(paramCache, _targetData.getName()));
        resultWrapper.parse(paramCache);
        _targetData.checkExport(new ExportParser(_targetData.getCI(), resultWrapper.write(paramCache), ""));
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

    /**
     * Wrapper class around the data used to call non-visibale methods.
     */
    private class Wrapper
    {
        /** Data instance to wrap. */
        private final DATA data;

        public Wrapper(final DATA _data)
        {
            this.data = _data;
        }

        public void parse(final ParameterCache_mxJPO _paramCache)
            throws Exception
        {
            final Method write = this.data.getClass().getDeclaredMethod("parse", ParameterCache_mxJPO.class);
            write.setAccessible(true);
            try {
                write.invoke(this.data, _paramCache);
            } finally  {
                write.setAccessible(false);
            }
        }

        public void calcDelta(final ParameterCache_mxJPO _paramCache,
                              final MultiLineMqlBuilder _mql,
                              final DATA _current)
            throws Exception
        {
            final Method write = this.data.getClass().getDeclaredMethod("calcDelta", ParameterCache_mxJPO.class, MultiLineMqlBuilder.class, this.data.getClass());
            write.setAccessible(true);
            try {
                write.invoke(this.data, _paramCache, _mql, _current);
            } finally  {
                write.setAccessible(false);
            }
        }

        public String write(final ParameterCache_mxJPO _paramCache)
            throws Exception
        {
            final StringBuilder generated = new StringBuilder();
            final Method write = this.data.getClass().getDeclaredMethod("write", ParameterCache_mxJPO.class, Appendable.class);
            write.setAccessible(true);
            try {
                write.invoke(this.data, _paramCache, generated);
            } finally  {
                write.setAccessible(false);
            }
            return generated.toString();
        }
    }
}
