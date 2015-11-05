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

package org.mxupdate.test.test.mxfeaturecheck;

import org.mxupdate.test.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests that the backslash handling implemented from MxUpdate works as expected
 * (w/o new features of new MX versions).
 *
 * @author The MxUpdate Team
 */
public class JPOBackslashHandlingTest
    extends AbstractTest
{
    /** Program name of the test JPO. */
    private final String prg = AbstractTest.PREFIX + "Test";
    /** One backslash. */
    private final String bslsh = "\\";

    @DataProvider(name = "backslahData")
    public Object[][] dataBackslashData()
    {
        return new Object[][] {
            {"\\",      this.bslsh + this.bslsh + this.bslsh + this.bslsh},
            {"\\\\",    this.bslsh + this.bslsh + this.bslsh + this.bslsh + this.bslsh + this.bslsh + this.bslsh + this.bslsh},
            {"\"",      this.bslsh + "\""},
        };
    }

    /**
     * Positive test to define backslash.
     *
     * @param _expected     expected value
     * @param _input        input value
     * @throws Exception if test failed
     */
    @Test(description = "positive test to define backslash",
          dataProvider = "backslahData")
    public void positiveTest(final String _expected,
                             final String _input)
        throws Exception
    {

        this.mql().cmd("escape add program ").arg(this.prg).cmd(" java code ")
                .arg("public class ${CLASSNAME}  {\n"
                            + "public void mxMain(matrix.db.Context ctx, String... args) throws Exception {\n"
                                + "final matrix.db.MatrixWriter writer = new matrix.db.MatrixWriter(ctx);\n"
                                + "writer.write(\"" + _input + "\");\n"
                                + "writer.flush();\n"
                                + "writer.close();\n"
                            + "}\n"
                        + "}\n")
                .exec(this.getContext());

        this.mql().cmd("escape compile program ").arg(this.prg).exec(this.getContext());

        Assert.assertEquals(
                this.mql().cmd("escape exec prog ").arg(this.prg).exec(this.getContext()),
                _expected);
    }

    /**
     * Cleanup all test attributes.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.PRG_JPO);
    }
}
