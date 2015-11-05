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

package org.mxupdate.test.test.util;

import java.io.File;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.util.FileUtils_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO methods {@link FileUtils_mxJPO#calcFile(java.io.File, String)}.
 *
 * @author The MxUpdate Team
 */
public class FileUtils_CalcFileTest
    extends AbstractTest
{
    /**
     * Prepares the test data.
     *
     * @return test data
     */
    @DataProvider(name = "Files")
    public Object[][] dataFiles()
    {
        return new Object[][] {
            {new File("/root/File.java"), null,    null},
            {new File("/root/File.java"), "test",  new File("/root/test")},
            {new File("/root/File.java"), "/test", new File("/test")},
        };
    }

    /**
     * Simple positive test.
     *
     * @param _ciFile   file of the ci script
     * @param _test     test file
     * @param _expFile  expected target file
     */
    @Test(description = "simple positive test",
          dataProvider = "Files")
    public void positiveTestSimple(final File _ciFile,
                                   final String _test,
                                   final File _expFile)
    {
        Assert.assertEquals(FileUtils_mxJPO.calcFile(_ciFile, _test), _expFile);
    }
}
