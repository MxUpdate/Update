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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.mxupdate.script.ScriptContext_mxJPO;
import org.mxupdate.script.statement.MxUpdateStatement_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the update of a program depending of the file date.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractProgramCI_3UpdateFileDateTest
    extends AbstractTest
{
    /** Test Files. */
    private final File testDir, testJPO;

    /**
     * Initialize the file handlers.
     */
    public AbstractProgramCI_3UpdateFileDateTest()
    {
        this.testDir    = new File(this.getTargetDir(), "program/jpo");
        this.testJPO    = new File(this.testDir, "MXUPDATE_Test_mxJPO.java");
    }

    /**
     * Positive test of update with checking file date.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test of update with checking file date")
    public void positiveTestWithCheckFileDate()
        throws Exception
    {
        final Map<String,String> params = new HashMap<>();
        params.put(ValueKeys.UpdateCheckFileDate.name(), "true");
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false, params);

        final String prgName = AbstractTest.PREFIX + "test";

        this.mql("escape add program \"" + prgName + "\" " + this.getKind());

        this.testJPO.getParentFile().getAbsoluteFile().mkdirs();
        this.testJPO.createNewFile();

        final ScriptContext_mxJPO context = new ScriptContext_mxJPO(paramCache)
                .defVar("NAME", prgName)
                .defVar("FILENAME", "dummy.txt")
                .defVar("FILEDATE", "2001-01-01");

        // first time
        // => code is updated
        FileUtils.write(this.testJPO, "A");
        this.testJPO.setLastModified(10000l);

        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context);;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "A");

        // other file content, but same modified date
        // => code is NOT updated
        FileUtils.write(this.testJPO, "B");
        this.testJPO.setLastModified(10000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context);;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "A");

        // other file content, other modified date
        // => code is updated
        FileUtils.write(this.testJPO, "C");
        this.testJPO.setLastModified(20000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context);;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "C");

        // other file content, other CI file fate
        // => code is updated
        FileUtils.write(this.testJPO, "D");
        this.testJPO.setLastModified(20000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context.defVar("FILEDATE", "9999-01-01"));;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "D");
    }

    /**
     * Test of update without checking of file date.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test of update without checking of file date")
    public void positiveTestWithoutCheckFileDate()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final String prgName = AbstractTest.PREFIX + "test";

        this.mql("escape add program \"" + prgName + "\" " + this.getKind());

        this.testJPO.getParentFile().getAbsoluteFile().mkdirs();
        this.testJPO.createNewFile();

        final ScriptContext_mxJPO context = new ScriptContext_mxJPO(paramCache)
                .defVar("NAME", prgName)
                .defVar("FILENAME", "dummy.txt")
                .defVar("FILEDATE", "2001-01-01");

        // first time
        // => code is updated
        FileUtils.write(this.testJPO, "A");
        this.testJPO.setLastModified(10000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context);;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "A");

        // other file content, but same modified date
        // => code is NOT updated
        FileUtils.write(this.testJPO, "B");
        this.testJPO.setLastModified(10000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context);;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "B");

        // other file content, other modified date
        // => code is updated
        FileUtils.write(this.testJPO, "C");
        this.testJPO.setLastModified(20000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context);;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "C");

        // other file content, other CI file fate
        // => code is updated
        FileUtils.write(this.testJPO, "D");
        this.testJPO.setLastModified(20000l);
        new MxUpdateStatement_mxJPO().setMxUpdateType("program").setCode("file \"" + this.testJPO + "\" kind " + this.getKind()).execute(context.defVar("FILEDATE", "9999-01-01"));;
        Assert.assertEquals(MqlBuilderUtil_mxJPO.mql().cmd("escape print prog ").arg(prgName).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()), "D");
    }

    /**
     * Returns the kind of program to test.
     *
     * @return kind string
     */
    protected abstract String getKind();

    /**
     * Initializes new CI program instance.
     *
     * @param _paramCache   parameter cache
     * @param _prgName      program name
     * @return CI instance
     */
    protected abstract ProgramCI_mxJPO createNew(final ParameterCache_mxJPO _paramCache,
                                                 final String _prgName);

    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.PRG_EKL);
        this.cleanup(CI.PRG_MQL);
        this.cleanup(CI.PRG_JPO);
    }
}
