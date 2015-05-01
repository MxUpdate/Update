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

package org.mxupdate.test.test.update.util;

import junit.framework.Assert;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO {@link MqlBuilder_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class MqlBuilder_MultiLineTest
    extends AbstractTest
{
    /**
     * Removes the MxUpdate types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    @DataProvider(name = "testData")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"abc"},
            {"abc \" abc"},
            {"abc ' abc"},
        };
    }

    /**
     * Simple positive test to set the description of a type.
     *
     * @param _descr    description to text
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    @Test(description = "simple positive test to set the description of a type",
          dataProvider = "testData")
    public void positiveTestSimple(final String _descr)
        throws Exception
    {
        final TypeData type = new TypeData(this, "name");
        type.create();

        MqlBuilder_mxJPO.multiLineMql()
            .newLine()
            .cmd("escape mod type ").arg(AbstractTest.PREFIX + "name").cmd(" description ").arg(_descr)
            .exec(this.getContext());

        // check result with old method
        final MQLCommand cmd = new MQLCommand();
        cmd.executeCommand(this.getContext(), "print type "+ AbstractTest.PREFIX + "name select description dump");
        Assert.assertEquals(
                _descr,
                cmd.getResult().trim());
    }

    /**
     * Positive test without arguments.
     *
     * @throws Exception
     */
    @Test(description = "positive test without arguments")
    public void positiveTestWithoutArguments()
        throws Exception
    {
        final TypeData type = new TypeData(this, "name");
        type.create();

        MqlBuilder_mxJPO.multiLineMql()
            .newLine()
            .cmd("escape print context")
            .exec(this.getContext());
    }

    /**
     * Negative test that matrix exception is thrown for non existing command.
     *
     * @throws Exception
     */
    @Test(description = "negative test that matrix exception is thrown for non existing command",
          expectedExceptions = MatrixException.class)
    public void negativeTestNonExistingCommand()
        throws Exception
    {
        MqlBuilder_mxJPO.multiLineMql()
            .newLine()
            .cmd("escape mod ddd")
            .exec(this.getContext());
    }


    /**
     * Negative test that command starts not with escape.
     *
     * @throws Exception
     */
    @Test(description = "negative test that command starts not with escape",
          expectedExceptions = MatrixException.class)
    public void negativeTestCommandWOEscape()
        throws Exception
    {
        MqlBuilder_mxJPO.multiLineMql()
            .newLine()
            .cmd("print context")
            .exec(this.getContext());
    }
}
