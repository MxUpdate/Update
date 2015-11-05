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
import org.mxupdate.test.data.userinterface.ChannelData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.userinterface.Channel_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link Channel_mxJPO channel CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class ChannelCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<Channel_mxJPO,ChannelData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            // package
            {"1a) new package",
                    new ChannelData(this, "Test"),
                    new ChannelData(this, "Test").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new ChannelData(this, "Test").defData("package", new PackageData(this, "TestPackage1")),
                    new ChannelData(this, "Test").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new ChannelData(this, "Test").defData("package", new PackageData(this, "TestPackage")),
                    new ChannelData(this, "Test").defKeyNotDefined("package")},
            // uuid
            {"2) uuid",
                    new ChannelData(this, "Test"),
                    new ChannelData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic names
            {"3a) symbolic name",
                    new ChannelData(this, "Test"),
                    new ChannelData(this, "Test").setValue("symbolicname", "channel_123")},
            {"3b) two symbolic name",
                    new ChannelData(this, "Test"),
                    new ChannelData(this, "Test").setValue("symbolicname", "channel_123").setValue("symbolicname", "channel_345")},
            // description
            {"4) description",
                    new ChannelData(this, "Test"),
                    new ChannelData(this, "Test").setValue("description", "abc def")},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_CHANNEL);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected Channel_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                             final String _name)
    {
        return new Channel_mxJPO(_name);
    }
}
