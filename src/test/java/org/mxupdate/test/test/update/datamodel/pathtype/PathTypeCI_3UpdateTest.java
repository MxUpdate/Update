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

package org.mxupdate.test.test.update.datamodel.pathtype;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.PathTypeData;
import org.mxupdate.update.datamodel.PathType_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PathType_mxJPO path type CI} export / update.
 *
 * @author The MxUpdate Team
 */
public class PathTypeCI_3UpdateTest
    extends AbstractDataExportUpdate<PathTypeData>
{
    /**
     * Dummy implementation.
     *
     * @return object array with all test interfaces
     */
    @DataProvider(name = "data")
    public Object[][] dataInterfaces()
    {
        return new Object[][]{};
    }

    /**
     * Negative test if an global attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if an global attribute is removed")
    public void t1a_negativeTestGlobalAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeData(this, "Test Attribute").setSingle("kind", "string"))
                .create();
        this.createNewData("Test")
                .failureUpdate(ErrorKey.DM_PATHTYPE_REMOVE_GLOBAL_ATTRIBUTE);
    }

    /**
     * Positive test if an ignored global attribute is not removed (because
     * ignored).
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an ignored global attribute is not removed (because ignored)")
    public void t1b_positiveTestIgnoredGlobalAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeData(this, "Test Attribute").setSingle("kind", "string"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMPathTypeAttrIgnore.name(), "*");
        this.createNewData("Test")
                .defData("attribute", new AttributeData(this, "Test Attribute").setSingle("kind", "string"))
                .checkExport();
    }

    /**
     * Positive test if an global attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an global attribute is removed")
    public void t1c_positiveTestGlobalAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeData(this, "Test Attribute").setSingle("kind", "string"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMPathTypeAttrRemove.name(), "*")
                .checkExport();
    }

    /**
     * Negative test if an local attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if an local attribute is removed")
    public void t2_negativeTestLocalAttributesRemoved()
        throws Exception
    {
        final PathTypeData typeDef = this.createNewData("Test").create();
        this.mql("escape add attribute \"MXUPDATE_Test\" type string owner pathtype \"" + AbstractTest.convertMql(typeDef.getName()) + "\"");
        typeDef.failureUpdate(ErrorKey.DM_PATHTYPE_REMOVE_LOCAL_ATTRIBUTE);
    }

    @BeforeMethod
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
        this.cleanup(AbstractTest.CI.DM_PATHTYPE);
    }

    /**
     * Creates for given {@code _name} a new path type data instance.
     *
     * @param _name     name of the path type data instance
     * @return path type data instance
     */
    @Override
    protected PathTypeData createNewData(final String _name)
    {
        return new PathTypeData(this, _name);
    }
}
