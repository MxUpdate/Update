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

package org.mxupdate.test.test.update.program;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Abstract definition of the update of
 * {@link org.mxupdate.update.program.ProgramCI_mxJPO programs}.
 *
 * @author The MxUpdate Team
 */
public abstract class Abstract_3Update1Test
    extends AbstractDataExportUpdate<AbstractProgramData<?>>
{
    @DataProvider(name = "data")
    public Object[][] dataPrograms()
    {
        return this.prepareData(this.getKind() + " program");
    }

    /**
     * Returns the list of all possible program kinds.
     *
     * @return list of all possible program kinds
     */
    @DataProvider(name = "allkinds")
    public Object[][] dataAllKinds()
    {
        return new Object[][]{{""}, {"external"}, {"ekl"}, {"java"}, {"mql"}};
    }

    /**
     * Positive test that the kind of the program is updated.
     *
     * @param _createKind   kind used within create
     * @throws Exception if test failed
     */
    @Test(description = "positive test that the kind of the program is updated",
          dataProvider = "allkinds")
    public void positiveTestUpdateKind(final String _createKind)
        throws Exception
    {
        MqlBuilderUtil_mxJPO.mql().cmd("escape add program ").arg(AbstractTest.PREFIX + "Test").cmd(" ").cmd(_createKind).exec(this.getContext());
        this.createNewData("Test").update("").checkExport();
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
        this.cleanup(CI.PRG_EKL);
        this.cleanup(CI.PRG_EXTERNAL);
        this.cleanup(CI.PRG_JPO);
        this.cleanup(CI.PRG_MQL);
    }

    /**
     * Returns the kind of attribute.
     *
     * @return kind of attribute
     */
    protected abstract String getKind();
}
