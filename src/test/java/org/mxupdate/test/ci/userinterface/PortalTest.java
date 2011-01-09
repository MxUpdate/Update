/*
 * Copyright 2008-2011 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
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
 * @version $Id$
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
