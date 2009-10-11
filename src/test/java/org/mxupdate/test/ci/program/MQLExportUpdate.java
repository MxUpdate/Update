/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.test.ci.program;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.program.MQLProgramData;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of MQL programs.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MQLExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test MQL programs (with and without extensions).
     *
     * @return object array with all test MQL programs
     */
    @DataProvider(name = "mqlprograms")
    public Object[][] getMQLPrograms()
    {
        // without extension with code
        final DataCollection data1 = new DataCollection(this);
        data1.getMQLProgram("Test1")
             .setCode("test");

        // without extension without code
        final DataCollection data2 = new DataCollection(this);
        data2.getMQLProgram("Test2")
             .setCode("");

        // with extension with code
        final DataCollection data3 = new DataCollection(this);
        data3.getMQLProgram("Test3.tcl")
             .setCode("test");

        // with extension without code
        final DataCollection data4 = new DataCollection(this);
        data4.getMQLProgram("Test4.tcl")
             .setCode("");

        return new Object[][]  {
                new Object[]{data1, "Test1"},
                new Object[]{data2, "Test2"},
                new Object[]{data3, "Test3.tcl"},
                new Object[]{data4, "Test4.tcl"},
        };
    }

    /**
     * Cleanups the MX system by deleting the test MQL programs.
     *
     * @throws Exception if cleanup failed
     */
    @BeforeMethod
    @AfterMethod
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.MQL_PROGRAM);
    }


    /**
     * Checks that the new created MQL progam is exported correctly.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "mqlprograms", description = "test export of MQL programs")
    public void testExport(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final MQLProgramData mqlProgram = _data.getMQLProgram(_name);
        mqlProgram.create();
        final Export export = this.export(CI.MQL_PROGRAM, mqlProgram.getName());

        // check oath
        Assert.assertEquals(export.getPath(),
                            mqlProgram.getCI().filePath,
                            "check path is correct");

        // check file name
        Assert.assertEquals(export.getFileName(),
                            mqlProgram.getCIFileName(),
                            "check that the correct file name is returned");

        // check JPO code
        Assert.assertEquals(export.getCode(),
                            mqlProgram.getCode(),
                            "checks MQL program code");
    }

    /**
     * Tests, if the MQL program within MX is created and registered with the
     * correct symbolic name.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=22
     */
    @Test(dataProvider = "mqlprograms", description = "test update of non existing MQL programs")
    public void testUpdate(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final MQLProgramData mqlProgram = _data.getMQLProgram(_name);

        // first update with original content
        this.update(mqlProgram.getCIFileName(), mqlProgram.ciFile());

        Assert.assertTrue(!"".equals(this.mql("list program " + mqlProgram.getName())),
                          "check JPO is created");
        Assert.assertTrue(!"".equals(this.mql("list property to program " + mqlProgram.getName())),
                          "check that the JPO is registered");
        Assert.assertEquals(this.mql("list property to program " + mqlProgram.getName()),
                            "program_" + mqlProgram.getName()
                                    + " on program eServiceSchemaVariableMapping.tcl to program "
                                    + mqlProgram.getName(),
                            "check that the JPO is registered with correct symbolic name");
    }
}
