/*
 * Copyright 2008-2010 The MxUpdate Team
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

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.userinterface.ChannelData;
import org.mxupdate.test.data.userinterface.CommandData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for channel exports and updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class ChannelTest
    extends AbstractDataExportUpdate<ChannelData>
{
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

    /**
     * Data provider for test channels.
     *
     * @return object array with all test channels
     */
    @DataProvider(name = "channels")
    public Object[][] getChannels()
    {
        return this.prepareData("channel",
                new Object[]{
                        "channel without anything (to test required fields)",
                        new ChannelData(this, "hello \" test")},
                new Object[]{
                        "channel with other symbolic name",
                        new ChannelData(this, "hello \" test")
                                .setSymbolicName("channel_Test")},
                new Object[]{
                        "channel with description",
                        new ChannelData(this, "hello \" test")
                                .setValue("description", "complex description \"test\"")},
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
     * Tests a new created channel and the related export.
     *
     * @param _description  description of the test case
     * @param _channel       channel to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "channels",
          description = "test export of new created channels")
    public void testExport(final String _description,
                           final ChannelData _channel)
        throws Exception
    {
        _channel.create()
                .checkExport();
    }


    /**
     * Tests an update of non existing channel. The result is tested with by
     * exporting the channel and checking the result.
     *
     * @param _description  description of the test case
     * @param _channel      channel to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "channels",
          description = "test update of non existing channel")
    public void testUpdate(final String _description,
                           final ChannelData _channel)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _channel.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create all assigned commands
        for (final CommandData command : _channel.getCommands())  {
            command.create();
        }

        // first update with original content
        _channel.update();
        final ExportParser exportParser = _channel.export();
        _channel.checkExport(exportParser);

        // second update with delivered content
        this.update(_channel.getCIFileName(), exportParser.getOrigCode());
        _channel.checkExport();
    }

    /**
     * Test update of existing channel that all parameters are cleaned.
     *
     * @param _description  description of the test case
     * @param _channel      channel to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "channels",
          description = "test update of existing channel for cleaning")
    public void testUpdate4Existing(final String _description,
                                    final ChannelData _channel)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _channel.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create all assigned commands
        for (final CommandData command : _channel.getCommands())  {
            command.create();
        }

        // first update with original content
        _channel.update()
                .checkExport();

        // second update with delivered content
        new ChannelData(this, _channel.getName().substring(AbstractTest.PREFIX.length()))
                .update()
                .setValue("description", "")
                .setValue("label", "")
                .checkExport();
    }
}
