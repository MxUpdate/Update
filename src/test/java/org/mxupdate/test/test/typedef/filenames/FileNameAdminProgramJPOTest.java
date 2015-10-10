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

package org.mxupdate.test.test.typedef.filenames;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link JPOProgram_mxJPO jpo program CI} evaluation of files.
 *
 * @author The MxUpdate Team
 */
public class FileNameAdminProgramJPOTest
    extends AbstractTest
{
    /** Original files. */
    private final File origDir, origJPO, origMXU, origPckJPO, origPckMXU;

    /**
     * Initialize the file handlers.
     */
    public FileNameAdminProgramJPOTest()
    {
        this.origDir    = new File(this.getResourcesDir(), "program/jpo");
        this.origJPO    = new File(this.origDir, "MXUPDATE_Test_mxJPO.java");
        this.origMXU    = new File(this.origDir, "MXUPDATE_Test_mxJPO.java.mxu");
        this.origPckJPO = new File(this.origDir, "MXUPDATE_org/mxupdate/test/TestWithPackage_mxJPO.java");
        this.origPckMXU = new File(this.origDir, "MXUPDATE_org.mxupdate.test.TestWithPackage_mxJPO.java.mxu");
    }

    @DataProvider(name = "data")
    public Object[][] getTest()
    {
        return new Object[][]  {
            {"jpo => jpo",                      new File[]{this.origJPO},                       new Object[]{"MXUPDATE_Test", this.origJPO}},
            {"jpo + mxu => mxu",                new File[]{this.origJPO, this.origMXU},         new Object[]{"MXUPDATE_Test", this.origMXU}},
            {"mxu => mxu",                      new File[]{this.origMXU},                       new Object[]{"MXUPDATE_Test", this.origMXU}},
            {"with package: jpo => jpo",        new File[]{this.origPckJPO},                    new Object[]{"MXUPDATE_org.mxupdate.test.TestWithPackage", this.origPckJPO}},
            {"with package: jpo + mxu => mxu",  new File[]{this.origPckJPO, this.origPckMXU},   new Object[]{"MXUPDATE_org.mxupdate.test.TestWithPackage", this.origPckMXU}},
            {"with package: mxu => mxu",        new File[]{this.origPckMXU},                    new Object[]{"MXUPDATE_org.mxupdate.test.TestWithPackage", this.origPckMXU}},
        };
    }

    /**
     * Positive test to evaluate correct files.
     *
     * @param _description  descrption
     * @param _files        files to evaluate
     * @param _exp          expected values
     * @throws Exception if test failed
     */
    @Test(description = "positive test to evaluate correct files",
            dataProvider = "data")
    public void positiveTest(final String _description,
                             final File[] _files,
                             final Object[] _exp)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final List<File> files = Arrays.asList(_files);

        final SortedMap<String,File> expMap = new TreeMap<>();
        for (int idx = 0; idx < _exp.length; )  {
            expMap.put((String) _exp[idx++], (File) _exp[idx++]);
        }
        Assert.assertEquals(
                paramCache.getMapping().getTypeDef(CI.PRG_JPO.updateType).matchFileNames(paramCache, files),
                expMap);
    }
}
