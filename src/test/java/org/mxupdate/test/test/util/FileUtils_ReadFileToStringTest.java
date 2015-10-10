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

import org.apache.commons.io.FileUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.FileUtils_mxJPO;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the {@link FileUtil_mxJPO file utility}.
 *
 * @author The MxUpdate Team
 */
public class FileUtils_ReadFileToStringTest
    extends AbstractTest
{
    /** Original files. */
    private final File origDir, origJPO;

    /**
     * Initialize the file handlers.
     */
    public FileUtils_ReadFileToStringTest()
    {
        this.origDir         = new File(this.getResourcesDir(), "program/jpo");
        this.origJPO         = new File(this.origDir, "MXUPDATE_Test_mxJPO.java");
    }

    /**
     * Simple positive test.
     *
     * @throws Exception if test failed
     */
    @Test(description = "simple positive test")
    public void positiveTestSimple()
        throws Exception
    {
        Assert.assertEquals(
                FileUtils_mxJPO.readFileToString(this.origJPO),
                FileUtils.readFileToString(this.origJPO).trim() + '\n');
    }

    /**
     * Negative test that file does not exists.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that file does not exists",
          expectedExceptions = UpdateException_mxJPO.class,
          expectedExceptionsMessageRegExp = "^UpdateError #90701: File '.*NON-EXISTING-FILE'.*")
    public void negativeTest90701FileNotExists()
        throws Exception
    {
        FileUtils_mxJPO.readFileToString(new File(this.origDir, "NON-EXISTING-FILE"));
    }
}
