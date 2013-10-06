/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.ci.userinterface;

import org.mxupdate.test.data.userinterface.ChannelData;
import org.mxupdate.test.data.userinterface.CommandData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for channel exports and updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class ChannelTest
    extends AbstractUITest<ChannelData>
{
    /**
     * Data provider for test channels.
     *
     * @return object array with all test channels
     */
    @DataProvider(name = "data")
    public Object[][] getChannels()
    {
        return this.prepareData("channel",
                new Object[]{
                        "channel with href",
                        new ChannelData(this, "hello \" test")
                                .setValue("href", "href \"test\"")},
                new Object[]{
                        "channel with alt",
                        new ChannelData(this, "hello \" test")
                                .setValue("alt", "alt \"test\"")},
                new Object[]{
                        "channel with height",
                        new ChannelData(this, "hello \" test")
                                .setValue("height", "100")},
                new Object[]{
                        "channel with settings",
                        new ChannelData(this, "hello \" test")
                                .setSetting("Setting 1", "Setting Value ' 1")
                                .setSetting("Setting 2", "Setting Value \"2\"")
                                .setSetting("Setting 3", "Value3")
                                .setSetting("Setting \"4\"", "Value 4")},
                new Object[]{
                        "channel with one command",
                        new ChannelData(this, "hello \" test")
                                .addCommand(new CommandData(this, "Command \"test\""))},
                new Object[]{
                        "channel with two command",
                        new ChannelData(this, "hello \" test")
                                .addCommand(new CommandData(this, "Command \"test 1\""))
                                .addCommand(new CommandData(this, "Command \"test 2\""))}
        );
    }

    /**
     * Removes the MxUpdate channels and programs.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.UI_CHANNEL);
        this.cleanup(CI.UI_COMMAND);
    }

    /**
     * Creates for given <code>_name</code> a new channel instance.
     *
     * @param _name     name of the channel instance
     * @return channel instance
     */
    @Override()
    protected ChannelData createNewData(final String _name)
    {
        return new ChannelData(this, _name);
    }
}
