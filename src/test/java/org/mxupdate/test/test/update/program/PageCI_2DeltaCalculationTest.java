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

package org.mxupdate.test.test.update.program;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.program.PageData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.program.Page_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Page_mxJPO page CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class PageCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Page_mxJPO,PageData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            // package
            {"1a) new package",
                    new PageData(this, "Test"),
                    new PageData(this, "Test").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new PageData(this, "Test").defData("package", new PackageData(this, "TestPackage1")),
                    new PageData(this, "Test").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new PageData(this, "Test").defData("package", new PackageData(this, "TestPackage")),
                    new PageData(this, "Test").defKeyNotDefined("package")},
            // uuid
            {"1) uuid",
                    new PageData(this, "Test"),
                    new PageData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic name
            {"2a) symbolic name",
                    new PageData(this, "Test"),
                    new PageData(this, "Test").setValue("symbolicname", "interface_123")},
            {"2b) two symbolic name",
                    new PageData(this, "Test"),
                    new PageData(this, "Test").setValue("symbolicname", "interface_123").setValue("symbolicname", "interface_345")},
            // property
            {"3) with property",
                    new PageData(this, "Test"),
                    new PageData(this, "Test").addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.PRG_PAGE);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected Page_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Page_mxJPO(_name);
    }
}
