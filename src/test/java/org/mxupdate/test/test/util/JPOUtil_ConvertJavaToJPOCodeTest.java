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

import org.mxupdate.util.JPOUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests that Java source code is correct converted to JPO source code.
 *
 * @author The MxUpdate Team
 */
public class JPOUtil_ConvertJavaToJPOCodeTest
{
    /**
     * Returns the test data to convert source code from Java to JPO.
     *
     * @return test data
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1",  "MXUPDATE_Test", "MyTest_mxJPO",                "${CLASS:MyTest}"},
            {"2a", "MXUPDATE_Test", " MyTest_mxJPO",               " ${CLASS:MyTest}"},
            {"2b", "MXUPDATE_Test", " MyTest_mxJPO1",              " ${CLASS:MyTest}"},
            {"2c", "MXUPDATE_Test", " MyTest_mxJPOabc",            " ${CLASS:MyTest}"},
            {"2d", "MXUPDATE_Test", " MyTest_mxJPO abc",           " ${CLASS:MyTest} abc"},
            {"2e", "MXUPDATE_Test", " MyTest_mxJPO1 abc",          " ${CLASS:MyTest} abc"},
            {"2f", "MXUPDATE_Test", " MyTest_mxJPOabc abc",        " ${CLASS:MyTest} abc"},
            {"2g", "MXUPDATE_Test", " MyTest_mxJPO;",              " ${CLASS:MyTest};"},
            {"3a", "MXUPDATE_Test", " MXUPDATE_Test_mxJPO",        " ${CLASSNAME}"},
            {"3b", "MXUPDATE_Test", " MXUPDATE_Test_mxJPO1",       " ${CLASSNAME}"},
            {"3c", "MXUPDATE_Test", " MXUPDATE_Test_mxJPOabc",     " ${CLASSNAME}"},
            {"3d", "MXUPDATE_Test", " MXUPDATE_Test_mxJPOabc abc", " ${CLASSNAME} abc"},
            {"3e", "MXUPDATE_Test", " MXUPDATE_Test_mxJPO\nabc",   " ${CLASSNAME}\nabc"},
            {"4a: referencing JPO with package name",
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                  + "        org.mxupdate.OtherClass_mxJPO a;\n"
                  + "        public MXUPDATE_Test_mxJPO()  {\n"
                  + "        }\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "        ${CLASS:org.mxupdate.OtherClass} a;\n"
                  + "        public ${CLASSNAME}()  {\n"
                  + "        }\n"
                  + "}",
            },
            {"4b: referencing JPO with package name and point at the end (wrong Java code...)",
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                  + "        org.mxupdate.OtherClass_mxJPO. a;\n"
                  + "        public MXUPDATE_Test_mxJPO()  {\n"
                  + "        }\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "        ${CLASS:org.mxupdate.OtherClass}. a;\n"
                  + "        public ${CLASSNAME}()  {\n"
                  + "        }\n"
                  + "}",
            },
            {"4c: point at the begin is interpretted as part of the JPO name",
                    "MXUPDATE_Test",
                    "public class .MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASS:.MXUPDATE_Test}  {\n"
                  + "}",
            },

            // JPO with package
            {"5a: simple JPO with package w/o constructor",
                  "org.mxupdate.MXUPDATE_Test",
                  "public class MXUPDATE_Test_mxJPO  {\n"
                + "}",
                  "public class ${CLASSNAME}  {\n"
                + "}",
            },
            // JPO with package
            {"5b: JPO with package with constructor",
                  "org.mxupdate.MXUPDATE_Test",
                  "public class MXUPDATE_Test_mxJPO  {\n"
                + "    public class MXUPDATE_Test_mxJPO()  {\n"
                + "    }\n"
                + "}",
                  "public class ${CLASSNAME}  {\n"
                + "    public class ${CLASSNAME}()  {\n"
                + "    }\n"
                + "}",
            },

            // package
            {"10a: package definition is removed",
                    "MXUPDATE_Test",
                    "package abc;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10b: package with JPO-extension definition is removed",
                    "MXUPDATE_Test",
                    "package abc_mxJPO;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10c: package definition on wrong place is removed",
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                  + "package abc;\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10d: complete line with package definition is removed (also import statements!)",
                    "MXUPDATE_Test",
                    "package abc;import def;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10e: only first package definition is removed",
                    "MXUPDATE_Test",
                    "package abc;\n"
                  + "package def;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "package def;\n"
                  + "public class ${CLASSNAME}  {\n"
                  + "}"
            },
        };
    }

    /**
     * Tests the convert.
     *
     * @param _descr    description
     * @param _progName name of program
     * @param _input    input Java source code
     * @param _expected expected JPO source code
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data")
    public void positiveTest(final String _descr,
                             final String _progName,
                             final String _input,
                             final String _expected)
        throws Exception
    {
        Assert.assertEquals(JPOUtil_mxJPO.convertJavaToJPOCode(_progName, _input), _expected);
    }
}
