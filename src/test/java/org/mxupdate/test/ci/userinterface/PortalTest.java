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

package org.mxupdate.test.ci.userinterface;

import org.mxupdate.test.data.userinterface.ChannelData;
import org.mxupdate.test.data.userinterface.PortalData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for portal exports and updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PortalTest
    extends AbstractUITest<PortalData>
{
    /**
     * Data provider for test portals.
     *
     * @return object array with all test portals
     */
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
                                .setSetting("Setting 1", "Setting Value ' 1")
                                .setSetting("Setting 2", "Setting Value \"2\"")
                                .setSetting("Setting 3", "Value3")
                                .setSetting("Setting \"4\"", "Value 4")},
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
     * Removes the MxUpdate portals and programs.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
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
