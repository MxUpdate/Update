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

import matrix.util.MatrixException;

import org.apache.commons.io.FileUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.program.MQLProgram_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link MQLProgram_mxJPO mql program CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractProgramCI_2DeltaCalculationTest<DATA extends AbstractAdminObject_mxJPO<?>,TESTDATA extends AbstractAdminData<?>>
    extends AbstractDeltaCalculationTest<DATA,TESTDATA>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) symbolic name",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setValue("symbolicname", "program_123")},
            {"1b) two symbolic name",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setValue("symbolicname", "program_123").setValue("symbolicname", "program_345")},
            {"2) descrption",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setValue("description", "test")},
            {"3) hidden",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setFlag("hidden", true)},
            {"4) needsbusinessobject",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setFlag("needsbusinessobject", true)},
            {"5) downloadable (and deferred)",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setSingle("execute", "deferred").setFlag("downloadable", true)},
            {"6) pipe",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setFlag("pipe", true)},
            {"7) pooled",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setFlag("pooled", true)},
            {"8) rule",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").defData("rule", new RuleData(this, "Test"))},
            {"9a) execute immediate",
                    this.createNewTestData("Test").setSingle("execute", "deferred"),
                    this.createNewTestData("Test").setSingle("execute", null)},
            {"9b) execute deferred",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setSingle("execute", "deferred")},
            {"9c) execute user",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").defData("execute user", new PersonAdminData(this, "Test"))},
            {"9d) remove execute user",
                    this.createNewTestData("Test").defData("execute user", new PersonAdminData(this, "Test")),
                    this.createNewTestData("Test").setSingle("execute", null)},
            {"10) code",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").setValue("code", "abcdef")},
            {"11a) property name",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").addProperty(new PropertyDef("property"))},
            {"11b) property name and value",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").addProperty(new PropertyDef("property", "value"))},
            {"11c) property name and link",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").addProperty(new PropertyDef("property", this.createNewTestData("RefTest")))},
            {"11d) property name, value and link",
                    this.createNewTestData("Test"),
                    this.createNewTestData("Test").addProperty(new PropertyDef("property", "value", this.createNewTestData("RefTest")))},
       };
    }

    @DataProvider(name = "CodeTestData")
    public Object[][] getCodeTestData()
    {
        return new Object[][] {
            {"test"},
            {"abc\ndef"},
            {"abc\ndef\n"},
        };
    }

    /**
     * Positive test that program code is correct updated.
     *
     * @param _code     code to test
     * @throws Exception if update failed
     */
    @Test(description = "positive test that program code is correct updated",
          dataProvider = "CodeTestData")
    public void positiveTestCodeLine(final String _code)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        @SuppressWarnings("unchecked")
        final TESTDATA prog = (TESTDATA) this.createNewTestData("Test").create().setValue("code", _code);

        final WrapperCIInstance<DATA> wrapper = new WrapperCIInstance<DATA>(this.createNewData(paramCache, prog.getName()));
        wrapper.parseUpdate(prog);
        wrapper.store((File) null, paramCache);

        Assert.assertEquals(
                this.mqlWOTrim("escape print prog \"" + AbstractTest.convertMql(prog.getName()) + "\" select code dump"),
                _code + "\n");
    }

    /**
     * Positive test with referenced file.
     *
     * @throws Exception if update failed
     */
    @Test(description = "positive test with referenced file")
    public void positiveTestWithFile()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        @SuppressWarnings("unchecked")
        final TESTDATA prog = (TESTDATA) this.createNewTestData("Test").create().setValue("file", "program/mql/test.tcl");

        final WrapperCIInstance<DATA> wrapper = new WrapperCIInstance<DATA>(this.createNewData(paramCache, prog.getName()));
        wrapper.parseUpdate(prog);
        wrapper.store(new File(this.getResourcesDir(), "test.mxu"), paramCache);

        Assert.assertEquals(
                this.mqlWOTrim("escape print prog \"" + AbstractTest.convertMql(prog.getName()) + "\" select code dump"),
                FileUtils.readFileToString(new File(this.getResourcesDir(), "program/mql/test.tcl")) + "\n");
    }

    /**
     * Positive test with referenced file with absolute path.
     *
     * @throws Exception if update failed
     */
    @Test(description = "positive test with referenced file with absolute path")
    public void positiveTestWithAbsoluteFile()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        @SuppressWarnings("unchecked")
        final TESTDATA prog = (TESTDATA) this.createNewTestData("Test").create().setValue("file", this.getResourcesDir() + "/program/mql/test.tcl");

        final WrapperCIInstance<DATA> wrapper = new WrapperCIInstance<DATA>(this.createNewData(paramCache, prog.getName()));
        wrapper.parseUpdate(prog);
        wrapper.store(new File(this.getResourcesDir(), "test.mxu"), paramCache);

        Assert.assertEquals(
                this.mqlWOTrim("escape print prog \"" + AbstractTest.convertMql(prog.getName()) + "\" select code dump"),
                FileUtils.readFileToString(new File(this.getResourcesDir(), "program/mql/test.tcl")) + "\n");
    }

    /**
     * Creates for given {@code _name} related test data instance.
     *
     * @param _name         name of the test object
     * @return new create test data instance
     */
    protected abstract TESTDATA createNewTestData(final String _name);

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.PRG_EKL);
        this.cleanup(CI.PRG_MQL);
        this.cleanup(CI.DM_RULE);
        this.cleanup(CI.USR_PERSONADMIN);
    }
}
