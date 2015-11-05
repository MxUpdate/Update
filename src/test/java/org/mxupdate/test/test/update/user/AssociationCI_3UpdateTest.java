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

package org.mxupdate.test.test.update.user;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AssociationData;
import org.mxupdate.update.user.Association_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Association_mxJPO association CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test
public class AssociationCI_3UpdateTest
    extends AbstractDataExportUpdate<AssociationData>
{
    /**
     * Data provider for test packages.
     *
     * @return object array with all test package
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return this.prepareData("association",
                new Object[]{
                        "1) association without anything (to test required fields)",
                        this.createNewData("Test")
                                .setValue("description", "")
                                .setFlag("hidden", false)
                                .setValue("definition", ""),
                        this.createNewData("Test")},
                new Object[]{
                        "2) association with description",
                        this.createNewData("Test")
                                .setValue("description", "ABC { } \"")}
         );
    }


    /**
     * Positive test for update of definition.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for update of definition")
    public void positiveTestCodeUpdate()
        throws Exception
    {
        new AssociationData(this, "Test")
                .create()
                .setValue("definition", "\"!User Agent\"")
                .update("")
                .checkExport();
        new AssociationData(this, "Test")
                .setValue("definition", "!Employee")
                .update("")
                .checkExport();
    }

    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.USR_ASSOCIATION);
    }

    @Override
    protected AssociationData createNewData(final String _name)
    {
        return new AssociationData(this, _name);
    }
}
