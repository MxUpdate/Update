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

package org.mxupdate.test.test.typedef.update;

import java.io.File;
import java.io.IOException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.typedef.update.UpdateAdminProgramJPO_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link JPOProgram_mxJPO jpo program CI} update.
 *
 * @author The MxUpdate Team
 */
public class UpdateAdminProgramJPOTest
    extends AbstractTest
{
    /** Original files. */
    private final File origDir, origJPO, origMXU, origPckJPO, origPckMXU;

    /**
     * Initialize the file handlers.
     */
    public UpdateAdminProgramJPOTest()
    {
        this.origDir         = new File(this.getResourcesDir(), "program/jpo");
        this.origJPO         = new File(this.origDir, "MXUPDATE_Test_mxJPO.java");
        this.origMXU         = new File(this.origDir, "MXUPDATE_Test_mxJPO.java.mxu");
        this.origPckJPO      = new File(this.origDir, "MXUPDATE_org/mxupdate/test/TestWithPackage_mxJPO.java");
        this.origPckMXU      = new File(this.origDir, "MXUPDATE_org.mxupdate.test.TestWithPackage_mxJPO.java.mxu");
    }

    @DataProvider(name = "data")
    public Object[][] getTest()
    {
        return new Object[][]  {
            {"jpo => jpo",                      this.origJPO,       "MXUPDATE_Test",                                    false},
            {"jpo + mxu => mxu",                this.origMXU,       "MXUPDATE_Test",                                    true},
            {"mxu => mxu",                      this.origMXU,       "MXUPDATE_TestWOJpo",                               true},
            {"with package: jpo => jpo",        this.origPckJPO,    "MXUPDATE_org.mxupdate.test.TestWithPackage",       false},
            {"with package: jpo + mxu => mxu",  this.origPckMXU,    "MXUPDATE_org.mxupdate.test.TestWithPackage",       true},
            {"with package: mxu => mxu",        this.origPckMXU,    "MXUPDATE_org.mxupdate.test.TestWithPackageWOJpo",  true},
        };
    }

    /**
     * Positive test.
     *
     * @param _description      description
     * @param _file             file
     * @param _name             name
     * @param _expUpdateCalled  expected updated called
     * @throws Exception if test failed
     */
    @Test(description = "positive test",
          dataProvider = "data")
    public void positiveTest(final String _description,
                             final File _file,
                             final String _name,
                             final boolean _expUpdateCalled)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        this.mql().cmd("escape add prog ").arg(_name).cmd(" java").exec(this.getContext());
        new UpdateAdminProgramJPO_mxJPO().update(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), false, _name, _file);

        final String code = this.mql().cmd("escape print prog ").arg(_name).cmd(" select ").arg("code").cmd(" dump").exec(this.getContext());

        Assert.assertTrue(
                !code.isEmpty(),
                "code defined");
        Assert.assertEquals(
                !this.mql().cmd("escape print prog ").arg(_name).cmd(" select ").arg("description").cmd(" dump").exec(this.getContext()).isEmpty(),
                _expUpdateCalled,
                "description defined");

        // TODO: file date is only defined if not update is called
        if (!_expUpdateCalled)  {
            final String fileDate = this.mql().cmd("escape print prog ").arg(_name).cmd(" select ").arg("property[" + PropertyDef_mxJPO.FILEDATE.getPropName(paramCache) + "]").cmd(" dump").exec(this.getContext());
            Assert.assertEquals(
                    fileDate.isEmpty(),
                    false,
                    "file date is defined (have '" + fileDate + "')");
        }
    }

    /**
     * Positive test that JPO is automatically created.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that JPO is automatically created")
    public void positiveTestCreate()
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        new UpdateAdminProgramJPO_mxJPO().update(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), false, "MXUPDATE_Test", this.origJPO);

        Assert.assertTrue(
                !MqlBuilderUtil_mxJPO.mql().cmd("escape print program ").arg("MXUPDATE_Test").cmd(" select ").arg("code").cmd(" dump").exec(this.getContext()).isEmpty(),
                "code defined");

        Assert.assertEquals(
                MqlBuilderUtil_mxJPO.mql().cmd("escape print program ").arg("MXUPDATE_Test").cmd(" select ").arg("isjavaprogram").cmd(" dump").exec(this.getContext()),
                "TRUE",
                "is java code");
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
    @Test(description = "positive test that the kind of the program is updated to java",
          dataProvider = "allkinds")
    public void positiveTestUpdateKind(final String _createKind)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        MqlBuilderUtil_mxJPO.mql().cmd("escape add program ").arg(AbstractTest.PREFIX + "Test").cmd(" ").cmd(_createKind).exec(this.getContext());

        new UpdateAdminProgramJPO_mxJPO().update(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), false, AbstractTest.PREFIX + "Test", this.origJPO);

        Assert.assertEquals(
                MqlBuilderUtil_mxJPO.mql().cmd("escape print program ").arg("MXUPDATE_Test").cmd(" select ").arg("isjavaprogram").cmd(" dump").exec(this.getContext()),
                "TRUE",
                "is java code");
    }

    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException, IOException
    {
        this.cleanup(CI.PRG_JPO);
    }
}
