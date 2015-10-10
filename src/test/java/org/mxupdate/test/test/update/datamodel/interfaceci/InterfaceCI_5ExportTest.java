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

package org.mxupdate.test.test.update.datamodel.interfaceci;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the export of {@link Relationship_mxJPO relationship CI}.
 *
 * @author The MxUpdate Team
 */
public class InterfaceCI_5ExportTest
    extends AbstractTest
{
    /**
     * Positive test local attributes.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test local attributes")
    public void positiveTestSortLocalAttributes()
        throws Exception
    {
        final String code = new InterfaceData(this, "Test")
                .create()
                .addLocalAttribute(new AttributeData(this, "Attribute 2").setSingle("kind", "string"))
                .addLocalAttribute(new AttributeData(this, "Attribute 1").setSingle("kind", "string"))
                .update("")
                .export()
                .getCode();
        Assert.assertTrue(
                code.indexOf("Attribute 1") < code.indexOf("Attribute 2"),
                "check correct order");
    }

    /**
     * Removes the created test data.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_INTERFACE);
    }
}
