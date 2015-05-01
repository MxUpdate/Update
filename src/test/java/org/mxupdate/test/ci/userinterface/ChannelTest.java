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
                                .setSingle("height", "100")},
               new Object[]{
                        "channel with default height",
                        new ChannelData(this, "hello \" test"),
                        new ChannelData(this, "hello \" test")
                                .setSingle("height", "0")},
                new Object[]{
                        "channel with settings",
                        new ChannelData(this, "hello \" test")
                                .setKeyValue("setting", "Setting 1", "Setting Value ' 1")
                                .setKeyValue("setting", "Setting 2", "Setting Value \"2\"")
                                .setKeyValue("setting", "Setting 3", "Value3")
                                .setKeyValue("setting", "Setting \"4\"", "Value 4")},
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
     * Positive test to change the order of children's.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change the order of children's")
    public void positiveTestChangeOrderChilds()
        throws Exception
    {
        new ChannelData(this, "test")
                .addCommand(new CommandData(this, "child command 0"))
                .addCommand(new CommandData(this, "child command 1"))
                .addCommand(new CommandData(this, "child command 2"))
                .addCommand(new CommandData(this, "child command 3"))
                .create()
                .checkExport();

        new ChannelData(this, "test")
                .addCommand(new CommandData(this, "child command 0"))
                .addCommand(new CommandData(this, "child command 3"))
                .addCommand(new CommandData(this, "child command 2"))
                .addCommand(new CommandData(this, "child command 1"))
                .update("")
                .checkExport();
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
