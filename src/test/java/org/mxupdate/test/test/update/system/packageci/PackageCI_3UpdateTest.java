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

package org.mxupdate.test.test.update.system.packageci;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.update.system.PackageCI_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PackageCI_mxJPO package} update.
 *
 * @author The MxUpdate Team
 */
@Test
public class PackageCI_3UpdateTest
    extends AbstractDataExportUpdate<PackageData>
{
    @Override
    protected PackageData createNewData(final String _name)
    {
        return new PackageData(this, _name);
    }

    /**
     * Removes the MxUpdate programs, attributes and types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_RULE);
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
        this.cleanup(AbstractTest.CI.DM_TYPE);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    /**
     * Data provider for test packages.
     *
     * @return object array with all test package
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return this.prepareData("package",
                new Object[]{
                        "1) package without anything (to test required fields)",
                        this.createNewData("Test")
                                .setValue("description", "")
                                .setFlag("hidden", false)
                                .setFlag("custom", false),
                        this.createNewData("Test")},
                new Object[]{
                        "2) package with description",
                        this.createNewData("Test")
                                .setValue("description", "ABC { } \"")},
                new Object[]{
                        "3) package with custom",
                        this.createNewData("Test")
                                .setFlag("custom", true, Create.ViaValue)},
                new Object[]{
                        "4) package with two used packages",
                        this.createNewData("Test")
                                .usePackage(this.createNewData("use 1"))
                                .usePackage(this.createNewData("use 2"))},
                new Object[]{
                        "5) package with two members",
                        this.createNewData("Test")
                                .addMember(new TypeData(this, "type"))
                                .addMember(new RelationshipData(this, "relationship"))}
         );
    }
}
