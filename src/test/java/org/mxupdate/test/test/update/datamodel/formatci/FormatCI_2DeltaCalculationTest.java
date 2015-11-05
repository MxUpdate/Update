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

package org.mxupdate.test.test.update.datamodel.formatci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.FormatData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.Format_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Format_mxJPO format CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class FormatCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Format_mxJPO,FormatData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) new package",
                    new FormatData(this, "Test"),
                    new FormatData(this, "Test").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new FormatData(this, "Test").defData("package", new PackageData(this, "TestPackage1")),
                    new FormatData(this, "Test").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new FormatData(this, "Test").defData("package", new PackageData(this, "TestPackage")),
                    new FormatData(this, "Test").defKeyNotDefined("package")},
            {"2) uuid",
                    new FormatData(this, "Test"),
                    new FormatData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"3a) symbolic name",
                    new FormatData(this, "Test"),
                    new FormatData(this, "Test").setValue("symbolicname", "format_123")},
            {"3b) two symbolic name",
                    new FormatData(this, "Test"),
                    new FormatData(this, "Test").setValue("symbolicname", "format_123").setValue("symbolicname", "format_345")},
            {"4) description",
                    new FormatData(this, "Test"),
                    new FormatData(this, "Test").setValue("description", "abc def")},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_FORMAT);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected Format_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Format_mxJPO(_name);
    }
}
