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
import java.util.Map;

import matrix.util.MatrixException;

import org.apache.commons.io.FileUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.typedef.update.UpdateAdminProgramJPO_mxJPO;
import org.mxupdate.update.program.JPOProgram_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
            {"jpo => jpo",                      this.origJPO,       "MXUPDATE_Test",                                    false, true},
            {"jpo + mxu => mxu",                this.origMXU,       "MXUPDATE_Test",                                    true,  true},
            {"mxu => mxu",                      this.origMXU,       "MXUPDATE_TestWOJpo",                               true,  false},
            {"with package: jpo => jpo",        this.origPckJPO,    "MXUPDATE_org.mxupdate.test.TestWithPackage",       false, true},
            {"with package: jpo + mxu => mxu",  this.origPckMXU,    "MXUPDATE_org.mxupdate.test.TestWithPackage",       true,  true},
            {"with package: mxu => mxu",        this.origPckMXU,    "MXUPDATE_org.mxupdate.test.TestWithPackageWOJpo",  true,  false},
        };
    }

    /**
     * Positive test.
     *
     * @param _description      description
     * @param _file             file
     * @param _name             name
     * @param _expUpdateCalled  expected updated valled
     * @param _expCodeDefined   expected code defined
     * @throws Exception if test failed
     */
    @Test(description = "positive test",
          dataProvider = "data")
    public void positiveTest(final String _description,
                             final File _file,
                             final String _name,
                             final boolean _expUpdateCalled,
                             final boolean _expCodeDefined)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        this.mql("add prog '" + _name + "' java");
        new UpdateAdminProgramJPO_mxJPO() {

            boolean updateCalled = false;

            {
                this.update(paramCache, paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType), false, _name, _file);

                Assert.assertEquals(this.updateCalled, _expUpdateCalled, "update called?");
            }

            @Override()
            protected void executeUpdate(final ParameterCache_mxJPO _paramCache,
                                         final TypeDef_mxJPO _typeDef,
                                         final Map<String,String> _tclVariables,
                                         final File _file)
                throws Exception
            {
                this.updateCalled = true;

                final WrapperCIInstance<JPOProgram_mxJPO> wrapper = new WrapperCIInstance<JPOProgram_mxJPO>((JPOProgram_mxJPO) _typeDef.newTypeInstance(_name));
                wrapper.parseUpdate(FileUtils.readFileToString(_file));
                wrapper.store(_file, _paramCache);
            }
        };

        final String code = this.mql("print prog '" + _name + "' select code dump");

        Assert.assertEquals(
                !code.isEmpty(),
                _expCodeDefined,
                "code defined");
        Assert.assertEquals(
                !this.mql("print prog '" + _name + "' select description dump").isEmpty(),
                _expUpdateCalled,
                "description defined");
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException, IOException
    {
        this.cleanup(CI.PRG_JPO);
    }
}
