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

package org.mxupdate.test.test.update.datamodel.attributeci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link AttributeCI_mxJPO binary attribute CI} delta
 * calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class AttributeBinaryCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<AttributeCI_mxJPO,AttributeData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1a) new package",
                    new AttributeData(this, "Test").setSingle("kind", "binary"),
                    new AttributeData(this, "Test").setSingle("kind", "binary").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new AttributeData(this, "Test").setSingle("kind", "binary").defData("package", new PackageData(this, "TestPackage1")),
                    new AttributeData(this, "Test").setSingle("kind", "binary").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new AttributeData(this, "Test").setSingle("kind", "binary").defData("package", new PackageData(this, "TestPackage")),
                    new AttributeData(this, "Test").setSingle("kind", "binary").defKeyNotDefined("package")},
            {"2) uuid",
                    new AttributeData(this, "Test").setSingle("kind", "binary"),
                    new AttributeData(this, "Test").setSingle("kind", "binary").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            {"3a) symbolic name",
                    new AttributeData(this, "Test").setSingle("kind", "binary"),
                    new AttributeData(this, "Test").setSingle("kind", "binary").setValue("symbolicname", "attribute_123")},
            {"3b) two symbolic name",
                    new AttributeData(this, "Test").setSingle("kind", "binary"),
                    new AttributeData(this, "Test").setSingle("kind", "binary").setValue("symbolicname", "attribute_123").setValue("symbolicname", "attribute_345")},
       };
    }

    @Override
    @BeforeMethod
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected AttributeCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new AttributeCI_mxJPO(_name);
    }
}
