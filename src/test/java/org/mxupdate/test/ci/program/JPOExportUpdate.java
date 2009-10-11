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
import org.mxupdate.test.data.program.JPOProgramData;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of JPO programs.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class JPOExportUpdate
    extends AbstractTest
{
    /**
     * Data provider for test JPOs.
     *
     * @return object array with all test JPOs
     * @see http://code.google.com/p/mxupdate/issues/detail?id=26
     */
    @DataProvider(name = "jpos")
    public Object[][] getInquiries()
    {
        final DataCollection data1 = new DataCollection(this);
        data1.getJPOProgram("Test1")
             .setCode("public class " + data1.getJPOProgram("Test1").getName() + "_mxJPO {}");

        final DataCollection data2 = new DataCollection(this);
        final String prgName2 = data2.getJPOProgram("test.subtest.Test2").getName();
        data2.getJPOProgram("test.subtest.Test2")
             .setCode("package " + prgName2.replaceAll("\\.[^.]*$", "") + ";\n"
                     + "public class " + prgName2.replaceAll(".*\\.", "") + "_mxJPO {}");

        return new Object[][]  {
                new Object[]{data1, "Test1"},
                new Object[]{data2, "test.subtest.Test2"},
        };
    }

    /**
     * Cleanups the MX system by deleting the test JPOs.
     *
     * @throws Exception if cleanup failed
     */
    @BeforeMethod
    @AfterMethod
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.JPO);
    }

    /**
     * Checks that the new created JPO is exported correctly.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "jpos", description = "test export of JPOs")
    public void testExport(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final JPOProgramData jpo = _data.getJPOProgram(_name);
        jpo.create();
        final Export export = this.export(CI.JPO, jpo.getName());

        // check path
        final String path;
        if (jpo.getName().indexOf('.') < 0)  {
            path = jpo.getCI().filePath;
        } else  {
            path = jpo.getCI().filePath + "/" + jpo.getName().replaceAll("\\.[^.]*$", "").replaceAll("\\.", "/");
        }
        Assert.assertEquals(export.getPath(), path, "check path is correct");

        // check file name
        Assert.assertEquals(export.getFileName(),
                            jpo.getCIFileName(),
                            "check that the correct file name is returned");

        // check JPO code
        Assert.assertEquals(export.getCode(),
                            jpo.getCode(),
                            "checks JPO code");

        // check if package definition exists / not exists
        Assert.assertEquals((export.getCode().indexOf("package") >= 0),
                            (jpo.getCode().indexOf("package") >= 0),
                            "checks that JPO code has correct package definition");
    }

    /**
     * Tests, if the JPO within MX is created and registered with the correct
     * symbolic name.
     *
     * @param _data     data collection to test
     * @param _name     name of the inquiry to test
     * @throws Exception if test failed
     * @see http://code.google.com/p/mxupdate/issues/detail?id=22
     */
    @Test(dataProvider = "jpos", description = "test update of non existing JPOs")
    public void testUpdate(final DataCollection _data,
                           final String _name)
        throws Exception
    {
        final JPOProgramData jpo = _data.getJPOProgram(_name);

        // first update with original content
        this.update(jpo.getCIFileName(), jpo.ciFile());

        Assert.assertTrue(!"".equals(this.mql("list program " + jpo.getName())),
                          "check JPO is created");
        Assert.assertTrue(!"".equals(this.mql("list property to program " + jpo.getName())),
                          "check that the JPO is registered");
        Assert.assertEquals(this.mql("list property to program " + jpo.getName()),
                            "program_" + jpo.getName()
                                    + " on program eServiceSchemaVariableMapping.tcl to program "
                                    + jpo.getName(),
                            "check that the JPO is registered with correct symbolic name");
    }
}
