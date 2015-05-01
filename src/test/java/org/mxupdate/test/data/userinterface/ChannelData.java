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

package org.mxupdate.test.data.userinterface;

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * Used to define a channel, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class ChannelData
    extends AbstractAdminData<ChannelData>
{
    /** All commands of the channel. */
    private final List<CommandData> commands = new ArrayList<CommandData>();

    /**
     * Constructor to initialize this channel.
     *
     * @param _test     related test implementation (where this channel is
     *                  defined)
     * @param _name     name of the channel
     */
    public ChannelData(final AbstractTest _test,
                       final String _name)
    {
        super(_test, AbstractTest.CI.UI_CHANNEL, _name);
    }

    /**
     * Appends a new command to {@link #commands}.
     *
     * @param _command      command to add
     * @return this channel instance
     * @see #commands
     */
    public ChannelData addCommand(final CommandData _command)
    {
        this.commands.add(_command);
        return this;
    }

    /**
     * Returns all assigned {@link #commands} of this channel.
     *
     * @return all assigned commands
     * @see #commands
     */
    public List<CommandData> getCommands()
    {
        return this.commands;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this channel.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate channel \"${NAME}\" {\n");
        this.getFlags()     .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.getSingles()   .append4Update("    ", strg);
        this.getKeyValues() .append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        for (final CommandData child : this.commands)  {
            strg.append("    command \"").append(AbstractTest.convertUpdate(child.getName())).append("\"\n");
        }
        strg.append("}");

        return strg.toString();
    }

    /**
     * Creates a this channel with all values and settings.
     *
     * @return this channel instance
     * @throws MatrixException if create failed
     */
    @Override()
    public ChannelData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add channel \"" + AbstractTest.convertMql(this.getName()) + "\"");

            // append all commands
            if (!this.commands.isEmpty())  {
                final List<String> names = new ArrayList<String>();
                for (final CommandData command : this.commands)  {
                    names.add(command.getName());
                }
                cmd.append(" command ").append(StringUtil_mxJPO.joinMql(',', true, names, null));
            }
            this.append4Create(cmd);

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to channel \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * All assigned {@link #commands} are created.
     *
     * @see #commands
     */
    @Override()
    public ChannelData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create all assigned commands
        for (final CommandData command : this.commands)  {
            command.create();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * Also the commands which are defined as place are checked.
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // check symbolic name
        Assert.assertEquals(
               _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        this.getFlags()     .check4Export(_exportParser, "");
        this.getValues()    .check4Export(_exportParser, "");
        this.getSingles()   .check4Export(_exportParser, "");
        this.getKeyValues() .check4Export(_exportParser, "");
        this.getProperties().checkExport(_exportParser.getLines("/mxUpdate/property/@value"));

        // check for commands (in correct order!)
        final List<String> childDefs = new ArrayList<String>(_exportParser.getLines("/mxUpdate/command/@value"));
        // fetch child from this definition
        final List<String> thisDefs = new ArrayList<String>();
        for (final CommandData  command : this.commands)  {
            thisDefs.add("\"" + AbstractTest.convertUpdate(command.getName()) + "\"");
        }
        // and compare
        Assert.assertEquals(childDefs, thisDefs, "check child of menu");
    }
}
