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

import junit.framework.Assert;

import org.mxupdate.util.FileUtils_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests method {@link FileUtils_mxJPO#extraceSubPath(String, String)}.
 *
 * @author The MxUpdate Team
 */
public class FileUtils_ExtractSubPathTest
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]{
            {"1) found",                        "usecase/subusecase",   "/home/testuser/project/40_mxupdate/datamodel/type/usecase/subusecase/TYPE_Test.mxu",                   "datamodel/type"},
            {"2) multiple times defined",       "usecase/subusecase",   "/home/testuser/project/40_mxupdate/datamodel/type/datamodel/type/usecase/subusecase/TYPE_Test.mxu",    "datamodel/type"},
            {"3) ci path only string match",    null,                   "/home/testuser/project/40_mxupdate/datamodel/usertype/usecase/subusecase/TYPE_Test.mxu",               "datamodel/type"},
            {"4) directly in ci path",          null,                   "/home/testuser/project/40_mxupdate/datamodel/type/TYPE_Test.mxu",                                      "datamodel/type"},
            {"5) not found",                    null,                   "/home/testuser/project/temp/TYPE_Test.mxu",                                                            "datamodel/type"},
            {"6) file name is null",            null,                   null,                                                                                                   "datamodel/type"},
            {"7) ci path is null",              null,                   "/home/testuser/project/40_mxupdate/null/usecase/subusecase/TYPE_Test.mxu",                             null},
        };
    }

    /**
     * Positive test to check that the sub patch is correct calculated.
     *
     * @param _expected
     * @param _fileName
     * @param _ciPath
     */
    @Test(description = "positive test to check that the sub patch is correct calculated",
          dataProvider = "data")
    public void positiveTestSimple(final String _description,
                                   final String _expected,
                                   final String _fileName,
                                   final String _ciPath)
    {
        Assert.assertEquals(_expected, FileUtils_mxJPO.extraceSubPath(_fileName, _ciPath));
    }
}
