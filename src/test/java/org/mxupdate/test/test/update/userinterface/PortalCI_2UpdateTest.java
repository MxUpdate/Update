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

import org.mxupdate.test.ci.userinterface.AbstractUITest;
import org.mxupdate.test.data.userinterface.ChannelData;
import org.mxupdate.test.data.userinterface.PortalData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for portal exports and updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PortalCI_2UpdateTest
    extends AbstractUITest<PortalData>
{
    @DataProvider(name = "data")
    public Object[][] getPortals()
    {
        return this.prepareData("portal",
                new Object[]{
                        "portal with href",
                        new PortalData(this, "hello \" test")
                                .setValue("href", "href \"test\"")},
                new Object[]{
                        "portal with alt",
                        new PortalData(this, "hello \" test")
                                .setValue("alt", "alt \"test\"")},
                new Object[]{
                        "portal with settings",
                        new PortalData(this, "hello \" test")
                                .setKeyValue("setting", "Setting 1", "Setting Value ' 1")
                                .setKeyValue("setting", "Setting 2", "Setting Value \"2\"")
                                .setKeyValue("setting", "Setting 3", "Value3")
                                .setKeyValue("setting", "Setting \"4\"", "Value 4")},
                new Object[]{
                        "portal with one command",
                        new PortalData(this, "hello \" test")
                                .addChannel(new ChannelData(this, "Command \"test\""))},
                new Object[]{
                        "portal with two command",
                        new PortalData(this, "hello \" test")
                                .addChannel(new ChannelData(this, "Command \"test 1\""))
                                .addChannel(new ChannelData(this, "Command \"test 2\""))}
        );
    }

    /**
     * Positive test to change the order of children's.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change the order of children's")
    public void positiveTestChangeOrderChilds()
        throws Exception
    {
        new PortalData(this, "test")
                .addChannel(new ChannelData(this, "child channel 0"))
                .addChannel(new ChannelData(this, "child channel 1"))
                .addNewRow()
                .addChannel(new ChannelData(this, "child channel 2"))
                .addChannel(new ChannelData(this, "child channel 3"))
                .create()
                .checkExport();

        new PortalData(this, "test")
                .addChannel(new ChannelData(this, "child channel 0"))
                .addNewRow()
                .addChannel(new ChannelData(this, "child channel 3"))
                .addChannel(new ChannelData(this, "child channel 2"))
                .addChannel(new ChannelData(this, "child channel 1"))
                .update("")
                .checkExport();
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.UI_PORTAL);
        this.cleanup(CI.UI_CHANNEL);
    }

    /**
     * Creates for given <code>_name</code> a new portal instance.
     *
     * @param _name     name of the portal instance
     * @return portal instance
     */
    @Override()
    protected PortalData createNewData(final String _name)
    {
        return new PortalData(this, _name);
    }
}
