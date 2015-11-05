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

package org.mxupdate.test.test.update.userinterface;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.data.userinterface.CommandData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Command_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Command_mxJPO command CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class CommandCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Command_mxJPO,CommandData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            // package
            {"1a) new package",
                    new CommandData(this, "Test"),
                    new CommandData(this, "Test").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new CommandData(this, "Test").defData("package", new PackageData(this, "TestPackage1")),
                    new CommandData(this, "Test").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new CommandData(this, "Test").defData("package", new PackageData(this, "TestPackage")),
                    new CommandData(this, "Test").defKeyNotDefined("package")},
            // uuid
            {"1) uuid",
                    new CommandData(this, "Test"),
                    new CommandData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic names
            {"2a) symbolic name",
                    new CommandData(this, "Test"),
                    new CommandData(this, "Test").setValue("symbolicname", "expression_123")},
            {"2b) two symbolic name",
                    new CommandData(this, "Test"),
                    new CommandData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
            // description
            {"3) description",
                    new CommandData(this, "Test"),
                    new CommandData(this, "Test").setValue("description", "abc def")},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_COMMAND);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected Command_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Command_mxJPO(_name);
    }
}
