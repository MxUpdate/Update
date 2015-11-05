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
    @DataProvider(name = "dataJava2JPO")
    public Object[][] dataJava2JPO()
    {
        return new Object[][] {
            {"1",  true, "MXUPDATE_Test", "MyTest_mxJPO",                "${CLASS:MyTest}"},
            {"2a", true, "MXUPDATE_Test", " MyTest_mxJPO",               " ${CLASS:MyTest}"},
            {"2b", true, "MXUPDATE_Test", " MyTest_mxJPO1",              " ${CLASS:MyTest}"},
            {"2c", true, "MXUPDATE_Test", " MyTest_mxJPOabc",            " ${CLASS:MyTest}"},
            {"2d", true, "MXUPDATE_Test", " MyTest_mxJPO abc",           " ${CLASS:MyTest} abc"},
            {"2e", true, "MXUPDATE_Test", " MyTest_mxJPO1 abc",          " ${CLASS:MyTest} abc"},
            {"2f", true, "MXUPDATE_Test", " MyTest_mxJPOabc abc",        " ${CLASS:MyTest} abc"},
            {"2g", true, "MXUPDATE_Test", " MyTest_mxJPO;",              " ${CLASS:MyTest};"},
            {"3a", true, "MXUPDATE_Test", " MXUPDATE_Test_mxJPO",        " ${CLASSNAME}"},
            {"3b", true, "MXUPDATE_Test", " MXUPDATE_Test_mxJPO1",       " ${CLASSNAME}"},
            {"3c", true, "MXUPDATE_Test", " MXUPDATE_Test_mxJPOabc",     " ${CLASSNAME}"},
            {"3d", true, "MXUPDATE_Test", " MXUPDATE_Test_mxJPOabc abc", " ${CLASSNAME} abc"},
            {"3e", true, "MXUPDATE_Test", " MXUPDATE_Test_mxJPO\nabc",   " ${CLASSNAME}\nabc"},
            {"4a: referencing JPO with package name",
                    true,
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
                    true,
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
                    true,
                    "MXUPDATE_Test",
                    "public class .MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASS:.MXUPDATE_Test}  {\n"
                  + "}",
            },

            // JPO with package
            {"5a: simple JPO with package w/o constructor",
                    true,
                    "org.mxupdate.MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}",
            },
            {"5b: JPO with package with constructor",
                    true,
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

            // backslash convert
            {"6a) backslashes not upgraded",
                    false,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\";\n"
                      + "}\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\\\\\";\n"
                      + "}\n"
                  + "}"
            },
            {"6b) backslashes not upgraded and escaped apostrophe (which must not be escaped!)",
                    false,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\"\";\n"
                      + "}\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\"\";\n"
                      + "}\n"
                  + "}"
            },
            {"6c) backslashes upgraded (and backslashes are not escaped!)",
                    true,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\";\n"
                      + "}\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\";\n"
                      + "}\n"
                  + "}"
            },

            // package
            {"10a: package definition is removed",
                    true,
                    "abc.MXUPDATE_Test",
                    "package abc;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10b: package with JPO-extension definition is removed",
                    true,
                    "abc_mxJPO.MXUPDATE_Test",
                    "package abc_mxJPO;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10c: package definition on wrong place is removed",
                    true,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                  + "package abc;\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10d: complete line with package definition is removed (also import statements!)",
                    true,
                    "abc.MXUPDATE_Test",
                    "package abc;import def;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10e: only first package definition is removed",
                    true,
                    "abc.MXUPDATE_Test",
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
     * Positive test to convert Java code to JPO code.
     *
     * @param _descr                description
     * @param _backslashUpgraded    are the backslashes updgraded? (no means
     *                              that backslashes are escaped by double
     *                              backslashes)
     * @param _progName             name of program
     * @param _input                input Java source code
     * @param _expected             expected JPO source code
     * @throws Exception if test failed
     */
    @Test(description = "positive test to convert Java code to JPO code",
          dataProvider = "dataJava2JPO")
    public void positiveTestConvertJavaToJPO(final String _descr,
                                             final boolean _backslashUpgraded,
                                             final String _progName,
                                             final String _input,
                                             final String _expected)
        throws Exception
    {
        Assert.assertEquals(JPOUtil_mxJPO.convertJavaToJPOCode(_backslashUpgraded, _progName, _input), _expected);
    }

    /**
     * Returns the test data to convert source code from Java to JPO.
     *
     * @return test data
     */
    @DataProvider(name = "dataJPO2Java")
    public Object[][] getdataJPO2Java()
    {
        return new Object[][] {
            {"1",  true, "MXUPDATE_Test", "MyTest_mxJPO",           "${CLASS:MyTest}"},
            {"2a", true, "MXUPDATE_Test", "MyTest_mxJPO",           "${CLASS:MyTest}"},
            {"2g", true, "MXUPDATE_Test", "MyTest_mxJPO;",          "${CLASS:MyTest};"},
            {"3a", true, "MXUPDATE_Test", "MXUPDATE_Test_mxJPO",    "${CLASSNAME}"},
            {"4a: referencing JPO with package name",
                    true,
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
                    true,
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
                    true,
                    "MXUPDATE_Test",
                    "public class .MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASS:.MXUPDATE_Test}  {\n"
                  + "}",
            },

            // JPO with package
            {"5a: simple JPO with package w/o constructor",
                    true,
                    "org.mxupdate.MXUPDATE_Test",
                    "package org.mxupdate;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}",
            },
            {"5b: JPO with package with constructor",
                    true,
                    "org.mxupdate.MXUPDATE_Test",
                    "package org.mxupdate;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "    public class MXUPDATE_Test_mxJPO()  {\n"
                  + "    }\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "    public class ${CLASSNAME}()  {\n"
                  + "    }\n"
                  + "}",
            },

            // backslash convert
            {"6a) backslashes not upgraded",
                    false,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\";\n"
                      + "}\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\\\\\";\n"
                      + "}\n"
                  + "}"
            },
            {"6b) backslashes not upgraded and escaped apostrophe",
                    false,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\"\";\n"
                      + "}\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\"\";\n"
                      + "}\n"
                  + "}"
            },
            {"6c) backslashes upgraded",
                    true,
                    "MXUPDATE_Test",
                    "public class MXUPDATE_Test_mxJPO  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\";\n"
                      + "}\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                      + "public void mxMain() {\n"
                          + "String s = \"\\\\\";\n"
                      + "}\n"
                  + "}"
            },

            // package
            {"10a: package definition is removed",
                    true,
                    "abc.MXUPDATE_Test",
                    "package abc;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10b: package with JPO-extension definition is removed",
                    true,
                    "abc_mxJPO.MXUPDATE_Test",
                    "package abc_mxJPO;\n"
                  + "public class MXUPDATE_Test_mxJPO  {\n"
                  + "}",
                    "public class ${CLASSNAME}  {\n"
                  + "}"
            },
            {"10c: only first package definition is removed",
                    true,
                    "abc.MXUPDATE_Test",
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
     * Positive test to convert JPO code to Java code.
     *
     * @param _descr                description
     * @param _backslashUpgraded    are the backslashes updgraded? (no means
     *                              that backslashes are escaped by double
     *                              backslashes)
     * @param _progName             name of program
     * @param _expected             expected Java source code
     * @param _input                input JPO source code
     * @throws Exception if test failed
     */
    @Test(description = "positive test to convert JPO code to Java code",
          dataProvider = "dataJPO2Java")
    public void positiveTestConvertJPOToJava(final String _descr,
                                             final boolean _backslashUpgraded,
                                             final String _progName,
                                             final String _expected,
                                             final String _input)
        throws Exception
    {
        Assert.assertEquals(JPOUtil_mxJPO.convertJPOToJavaCode(_backslashUpgraded, _progName, _input), _expected);
    }
}
