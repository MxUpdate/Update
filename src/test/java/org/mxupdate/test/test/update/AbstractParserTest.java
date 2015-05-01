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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Common definitions for parser tests.
 *
 * @author The MxUpdate Team
 * @param <DATA> class of the data
 */
public abstract class AbstractParserTest<DATA extends AbstractObject_mxJPO<?>>
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
     * Parsed the {@code _definition} code and compares the result with
     * {@code _toTest}.
     *
     * @param _description  description of the test
     * @param _toTest       expected result (if empty string
     *                      {@code _definition} is the expected result)
     * @param _definition   text of the definition to test
     * @throws Exception if {@code _definition} could not parsed
     */
    @Test(dataProvider = "data")
    public void positiveTest(final String _description,
                             final String _toTest,
                             final String _definition)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final WrapperCIInstance<DATA> data = new WrapperCIInstance<DATA>(this.createNewData(paramCache, AbstractTest.PREFIX + "_Test"));
        data.parseUpdateWOStrip(_definition);

        final String generated = data.write(paramCache);

        final StringBuilder oldDefBuilder = new StringBuilder();
        for (final String line : _toTest.isEmpty() ? _definition.split("\n") : _toTest.split("\n"))  {
            oldDefBuilder.append(line.trim()).append(' ');
        }
        int length = 0;
        String oldDef = oldDefBuilder.toString();
        while (length != oldDef.length())  {
            length = oldDef.length();
            oldDef = oldDef.replaceAll("  ", " ");
        }

        final String temp = data.strip(generated);
        final StringBuilder newDefBuilder = new StringBuilder();
        for (final String line : temp.split("\n"))  {
            newDefBuilder.append(line.trim()).append(' ');
        }
        length = 0;
        String newDef = newDefBuilder.toString();
        while (length != newDef.length())  {
            length = newDef.length();
            newDef = newDef.replaceAll("  ", " ");
        }

        Assert.assertEquals(newDef.toString().trim(), oldDef.trim());
    }
}
