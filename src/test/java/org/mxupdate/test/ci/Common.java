/*
 * Copyright 2008-2010 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test.ci;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.util.UpdateException_mxJPO.Error;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The class is used to check common test cases related to configuration items.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Common
    extends AbstractTest
{
    /**
     * Test that the correct error code is returned if the encoding of the file
     * name for the configuration item failed.
     *
     * @throws Exception    if test failed
     */
    @Test(description = "test file name with not correct encoding")
    public void updateWrongFileName()
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put("COMMAND_Start@.tcl", "");
        final Map<?,?> bck = this.executeEncoded("Update", null, "FileContents", files);
        final Exception ex = (Exception) bck.get("exception");
        Assert.assertTrue((ex != null), "an matrix exception must be thrown");
        Assert.assertTrue(ex.getMessage().contains("UpdateError #"
                                                    + Error.UTIL_STRINGUTIL_CONVERT_FROM_FILENAME.getCode()
                                                    + ":"),
                          "correct error code is returned");
    }
}
