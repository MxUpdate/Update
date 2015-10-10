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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.program.JPOProgramData;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.typedef.export.ExportAdminProgramJPO_mxJPO;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.JPOUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link JPOProgram_mxJPO jpo program CI} export.
 *
 * @author The MxUpdate Team
 */
public class JPOProgramCI_3ExportTest
    extends AbstractTest
{
    /** Original files. */
    private final File origDir, origJPO,          origPckJPO;
    /** Exported files. */
    private final File testDir, testJPO, testMXU, testPckJPO, testPckMXU;

    /**
     * Initialize the file handlers.
     */
    public JPOProgramCI_3ExportTest()
    {
        this.origDir    = new File(this.getResourcesDir(), "program/jpo");
        this.origJPO    = new File(this.origDir, "MXUPDATE_Test_mxJPO.java");
        this.origPckJPO = new File(this.origDir, "MXUPDATE_org/mxupdate/test/TestWithPackage_mxJPO.java");

        this.testDir    = new File(this.getTargetDir(), "program/jpo");
        this.testJPO    = new File(this.testDir, "MXUPDATE_Test_mxJPO.java");
        this.testMXU    = new File(this.testDir, "MXUPDATE_Test_mxJPO.java.mxu");
        this.testPckJPO = new File(this.testDir, "MXUPDATE_org/mxupdate/test/TestWithPackage_mxJPO.java");
        this.testPckMXU = new File(this.testDir, "MXUPDATE_org.mxupdate.test.TestWithPackage_mxJPO.java.mxu");
    }

    @DataProvider(name = "dataEmpty")
    public Object[][] getDataEmpty()
    {
        return new Object[][] {
            {"description",         this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setValue("description", "")},
            {"hidden",              this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("hidden", false)},
            {"needsbusinessobject", this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("needsbusinessobject", false)},
            {"downloadable",        this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("downloadable", false)},
            {"pipe",                this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("pipe", false)},
            {"pooled",              this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("pooled", false)},
            {"deferred",            this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setSingle("execute", "immediate")},
            // with package
            {"description",         this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setValue("description", "")},
            {"hidden",              this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("hidden", false)},
            {"needsbusinessobject", this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("needsbusinessobject", false)},
            {"downloadable",        this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("downloadable", false)},
            {"pipe",                this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("pipe", false)},
            {"pooled",              this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("pooled", false)},
            {"deferred",            this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setSingle("execute", "immediate")},
        };
    }

    @DataProvider(name = "dataNotEmpty")
    public Object[][] getData()
    {
        return new Object[][] {
            {"symbolicname",        this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setValue("symbolicname", "program_abc")},
            {"description",         this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setValue("description", "abc")},
            {"hidden",              this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("hidden", true)},
            {"needsbusinessobject", this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("needsbusinessobject", true)},
            {"downloadable",        this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setSingle("execute", "deferred").setFlag("downloadable", true)},
            {"pipe",                this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("pipe", true)},
            {"pooled",              this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setFlag("pooled", true)},
            {"deferred",            this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").setSingle("execute", "deferred")},
            {"rule",                this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").defData("rule", new RuleData(this, "Test"))},
            {"execute user",        this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").defData("execute user", new PersonAdminData(this, "Test"))},
            {"property",            this.origJPO,    this.testJPO,    this.testMXU,    new JPOProgramData(this, "Test").setSingle("kind", "java").addProperty(new PropertyDef("Test"))},
            // with package
            {"symbolicname",        this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setValue("symbolicname", "program_abc")},
            {"description",         this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setValue("description", "abc")},
            {"hidden",              this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("hidden", true)},
            {"needsbusinessobject", this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("needsbusinessobject", true)},
            {"downloadable",        this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setSingle("execute", "deferred").setFlag("downloadable", true)},
            {"pipe",                this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("pipe", true)},
            {"pooled",              this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setFlag("pooled", true)},
            {"deferred",            this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").setSingle("execute", "deferred")},
            {"rule",                this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").defData("rule", new RuleData(this, "Test"))},
            {"execute user",        this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").defData("execute user", new PersonAdminData(this, "Test"))},
            {"property",            this.origPckJPO, this.testPckJPO, this.testPckMXU, new JPOProgramData(this, "org.mxupdate.test.TestWithPackage").setSingle("kind", "java").addProperty(new PropertyDef("Test"))},
        };
    }

    /**
     * Positive test that only the jpo file is generated.
     *
     * @param _description  description (not used)
     * @param _origJPO      original JPO file
     * @param _testJPO      test JPO file
     * @param _testMXU      test MXU file
     * @param _progData     JPO program with defined properties
     * @throws Exception if test failed
     */
    @Test(description = "positive test that only jpo file is generated",
          dataProvider = "dataEmpty")
    public void positiveTestOnlyJpoFile(final String _description,
                                        final File _origJPO,
                                        final File _testJPO,
                                        final File _testMXU,
                                        final JPOProgramData _progData)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        // create JPO
        this.mql("add prog '" + _progData.getName() + "' java code '" + JPOUtil_mxJPO.convertJavaToJPOCode(_progData.getName(), FileUtils.readFileToString(_origJPO)) + "'");

        // update values
        _progData.createDependings();
        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(new ProgramCI_mxJPO(_progData.getName()));
        wrapper.parseUpdate(_progData);
        wrapper.store((File) null, paramCache);

        // insert again to ensure that the code is defined...
        this.mql("mod prog '" + _progData.getName() + "' code '" + JPOUtil_mxJPO.convertJavaToJPOCode(_progData.getName(), FileUtils.readFileToString(_origJPO)) + "'");

        new ExportAdminProgramJPO_mxJPO().export(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), _progData.getName(), new File(this.getTargetDir()));

        Assert.assertEquals(FileUtils.readFileToString(_origJPO), FileUtils.readFileToString(_testJPO), "check file content equal (" + _origJPO + " = " + _testJPO + ")");
        Assert.assertEquals(this.testDir.list().length, 1, "only jpo file exists");
    }

    /**
     * Positive test that mxu and jpo file are generated.
     *
     * @param _description  description (not used)
     * @param _origJPO      original JPO file
     * @param _testJPO      test JPO file
     * @param _testMXU      test MXU file
     * @param _progData     JPO program with defined properties
     * @throws Exception if test failed
     */
    @Test(description = "positive test that mxu and jpo file are generated",
          dataProvider = "dataNotEmpty")
    public void positiveTestMxuAndJpoFile(final String _description,
                                          final File _origJPO,
                                          final File _testJPO,
                                          final File _testMXU,
                                          final JPOProgramData _progData)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        // create JPO
        this.mql("add prog '" + _progData.getName() + "' java code '" + JPOUtil_mxJPO.convertJavaToJPOCode(_progData.getName(), FileUtils.readFileToString(_origJPO)) + "'");

        // update values
        _progData.createDependings();
        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(new ProgramCI_mxJPO(_progData.getName()));
        wrapper.parseUpdate(_progData);
        wrapper.store((File) null, paramCache);

        // insert again to ensure that the code is defined...
        this.mql("mod prog '" + _progData.getName() + "' code '" + JPOUtil_mxJPO.convertJavaToJPOCode(_progData.getName(), FileUtils.readFileToString(_origJPO)) + "'");

        new ExportAdminProgramJPO_mxJPO().export(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), _progData.getName(), new File(this.getTargetDir()));

        Assert.assertTrue(FileUtils.contentEquals(_origJPO, _testJPO), "check JPO content equal");
        Assert.assertTrue(_testMXU.exists(), "MXU file exists");
        _progData
                .setValue("file", _progData.getName().replaceAll("\\.", "/") + "_mxJPO.java")
                .checkExport(new ExportParser(CI.PRG_JPO, FileUtils.readFileToString(_testMXU), ""));
    }

    /**
     * Positive test to export JPO w/o content with properties.
     *
     * @param _description  description (not used)
     * @param _origJPO      original JPO file
     * @param _testJPO      test JPO file
     * @param _testMXU      test MXU file
     * @param _progData     JPO program with defined properties
     * @throws Exception if test failed
     */
    @Test(description = "positive test to export JPO w/o content with properties",
          dataProvider = "dataNotEmpty")
    public void positiveTestOnlyMxuFileWithProperties(final String _description,
                                                      final File _origJPO,
                                                      final File _testJPO,
                                                      final File _testMXU,
                                                      final JPOProgramData _progData)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        // create JPO
        this.mql("add prog '" + _progData.getName() + "' java code '" + JPOUtil_mxJPO.convertJavaToJPOCode(_progData.getName(), FileUtils.readFileToString(_origJPO)) + "'");

        // update values (and the code will be also null)
        _progData.createDependings();
        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(new ProgramCI_mxJPO(_progData.getName()));
        wrapper.parseUpdate(_progData);
        wrapper.store((File) null, paramCache);

        new ExportAdminProgramJPO_mxJPO().export(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), _progData.getName(), new File(this.getTargetDir()));

        Assert.assertFalse(_testJPO.exists(), "JPO file not exists");
        Assert.assertTrue(_testMXU.exists(), "MXU file exists");
        _progData
                .defKeyNotDefined("file")
                .checkExport(new ExportParser(CI.PRG_JPO, FileUtils.readFileToString(_testMXU), ""));
    }

    /**
     * Positive test to export JPO w/o content w/o properties.
     *
     * @param _description  description (not used)
     * @param _origJPO      original JPO file
     * @param _testJPO      test JPO file
     * @param _testMXU      test MXU file
     * @param _progData     JPO program with defined properties
     * @throws Exception if test failed
     */
    @Test(description = "positive test to export JPO w/o content w/o properties",
          dataProvider = "dataEmpty")
    public void positiveTestOnlyMxuFileWOProperties(final String _description,
                                                    final File _origJPO,
                                                    final File _testJPO,
                                                    final File _testMXU,
                                                    final JPOProgramData _progData)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        // create JPO
        this.mql("add prog '" + _progData.getName() + "' java code '" + JPOUtil_mxJPO.convertJavaToJPOCode(_progData.getName(), FileUtils.readFileToString(_origJPO)) + "'");

        // update values (and the code will be also null)
        _progData.createDependings();
        final WrapperCIInstance<ProgramCI_mxJPO> wrapper = new WrapperCIInstance<>(new ProgramCI_mxJPO(_progData.getName()));
        wrapper.parseUpdate(_progData);
        wrapper.store((File) null, paramCache);

        new ExportAdminProgramJPO_mxJPO().export(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), _progData.getName(), new File(this.getTargetDir()));

        Assert.assertFalse(_testJPO.exists(), "JPO file not exists");
        Assert.assertTrue(_testMXU.exists(), "MXU file exists");
    }

    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException, IOException
    {
        this.cleanup(CI.PRG_EKL);
        this.cleanup(CI.PRG_JPO);
        this.cleanup(CI.DM_RULE);
        this.cleanup(CI.USR_PERSONADMIN);
        FileUtils.deleteDirectory(this.testDir);
    }
}
