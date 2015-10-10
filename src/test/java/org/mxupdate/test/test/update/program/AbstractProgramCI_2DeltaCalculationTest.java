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

import org.apache.commons.io.FileUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.user.PersonData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link MQLProgram_mxJPO mql program CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractProgramCI_2DeltaCalculationTest<TESTDATA extends AbstractAdminData<?>>
    extends AbstractDeltaCalculationTest<ProgramCI_mxJPO,TESTDATA>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) uuid",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"2a) symbolic name",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setValue("symbolicname", "program_123")},
            {"2b) two symbolic name",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setValue("symbolicname", "program_123").setValue("symbolicname", "program_345")},
            {"3) descrption",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setValue("description", "test")},
            {"4) hidden",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setFlag("hidden", true)},
            {"5) needsbusinessobject",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setFlag("needsbusinessobject", true)},
            {"6) downloadable (and deferred)",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setSingle("execute", "deferred").setFlag("downloadable", true)},
            {"7) pipe",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setFlag("pipe", true)},
            {"8) pooled",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setFlag("pooled", true)},
            {"9) rule",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).defData("rule", new RuleData(this, "Test"))},
            {"10a) execute immediate",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setSingle("execute", "deferred"),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setSingle("execute", null)},
            {"10b) execute deferred",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setSingle("execute", "deferred")},
            {"10c) execute user",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).defData("execute user", new PersonData(this, "Test"))},
            {"10d) remove execute user",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).defData("execute user", new PersonData(this, "Test")),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setSingle("execute", null)},
            {"11) code",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).setValue("code", "abcdef")},
            {"12a) property name",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).addProperty(new PropertyDef("property"))},
            {"12b) property name and value",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).addProperty(new PropertyDef("property", "value"))},
            {"12c) property name and link",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).addProperty(new PropertyDef("property", this.createNewTestData("RefTest")))},
            {"12d) property name, value and link",
                    this.createNewTestData("Test").setSingle("kind", this.getKind()),
                    this.createNewTestData("Test").setSingle("kind", this.getKind()).addProperty(new PropertyDef("property", "value", this.createNewTestData("RefTest")))},
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

        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(this.createNewData(paramCache, prog.getName()));
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
        final TESTDATA prog = (TESTDATA) this.createNewTestData("Test").create().setSingle("kind", this.getKind()).setValue("file", "program/mql/test.tcl");

        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(this.createNewData(paramCache, prog.getName()));
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
        final TESTDATA prog = (TESTDATA) this.createNewTestData("Test").create().setSingle("kind", this.getKind()).setValue("file", this.getResourcesDir() + "/program/mql/test.tcl");

        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(this.createNewData(paramCache, prog.getName()));
        wrapper.parseUpdate(prog);
        wrapper.store(new File(this.getResourcesDir(), "test.mxu"), paramCache);

        Assert.assertEquals(
                this.mqlWOTrim("escape print prog \"" + AbstractTest.convertMql(prog.getName()) + "\" select code dump"),
                FileUtils.readFileToString(new File(this.getResourcesDir(), "program/mql/test.tcl")) + "\n");
    }

    /**
     * Returns the kind of program to test.
     *
     * @return kind string
     */
    protected abstract String getKind();

    /**
     * Creates for given {@code _name} related test data instance.
     *
     * @param _name         name of the test object
     * @return new create test data instance
     */
    protected abstract TESTDATA createNewTestData(final String _name);

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.PRG_EKL);
        this.cleanup(CI.PRG_MQL);
        this.cleanup(CI.DM_RULE);
        this.cleanup(CI.USR_PERSON);
    }
}
