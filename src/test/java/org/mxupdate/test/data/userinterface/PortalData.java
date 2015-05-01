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
import org.mxupdate.test.ExportParser.Line;
import org.mxupdate.test.data.AbstractAdminData;
import org.testng.Assert;

/**
 * Used to define a portal, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class PortalData
    extends AbstractAdminData<PortalData>
{
    /** All channels of the portal. */
    private final List<ChannelData> channels = new ArrayList<ChannelData>();

    /**
     * Constructor to initialize this portal.
     *
     * @param _test     related test implementation (where this portal is
     *                  defined)
     * @param _name     name of the portal
     */
    public PortalData(final AbstractTest _test,
                       final String _name)
    {
        super(_test, AbstractTest.CI.UI_PORTAL, _name);
    }

    /**
     * Appends a new channel to {@link #channels}.
     *
     * @param _channel      channel to add
     * @return this channel instance
     * @see #channels
     */
    public PortalData addChannel(final ChannelData _channel)
    {
        this.channels.add(_channel);
        return this;
    }

    /**
     * Appends a new channel to {@link #channels}.
     *
     * @param _channel      channel to add
     * @return this channel instance
     * @see #channels
     */
    public PortalData addNewRow()
    {
        this.channels.add(null);
        return this;
    }

    /**
     * Returns all assigned {@link #channels} of this portal.
     *
     * @return all assigned channels
     * @see #channels
     */
    public List<ChannelData> getChannels()
    {
        return this.channels;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this portal.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate portal \"${NAME}\" {\n");
        this.getFlags()     .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.getKeyValues() .append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        for (final ChannelData child : this.channels)  {
            if (child == null)  {
                strg.append("    newrow\n");
            } else  {
                strg.append("    channel \"").append(AbstractTest.convertUpdate(child.getName())).append("\"\n");
            }
        }
        strg.append("}");

        return strg.toString();
    }

    /**
     * Creates a this portal with all values and settings.
     *
     * @return this portal instance
     * @throws MatrixException if create failed
     */
    @Override()
    public PortalData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add portal \"" + AbstractTest.convertMql(this.getName()) + "\"");

            this.append4Create(cmd);

            cmd.append(";\n");

            // append all channels after create (because of new rows!)
            if (!this.channels.isEmpty())  {
                cmd.append("escape mod portal \"" + AbstractTest.convertMql(this.getName()) + "\"");
                boolean newRow = false;
                for (final ChannelData child : this.channels)  {
                    if (child == null)  {
                        newRow = true;
                    } else  {
                        cmd.append(" place \"").append(AbstractTest.convertMql(child.getName())).append("\" ").append(newRow ? "newrow" : "").append(" after \"\"");
                        newRow = false;
                    }
                }
                cmd.append(";\n");
            }

            cmd.append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to portal \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * All assigned channels are created.
     *
     * @see #channels
     */
    @Override()
    public PortalData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create all assigned channels
        for (final ChannelData channel : this.channels)  {
            if (channel != null)  {
                channel.create();
            }
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * Also the channels which are defined as place are checked.
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        // fetch child from export file
        final List<String> childDefs = new ArrayList<String>();
        for (final Line line : _exportParser.getRootLines().get(0).getChildren())  {
            if ("channel".equals(line.getTag()))  {
                childDefs.add(line.getTag() + " " + line.getValue());
            } else if ("newrow".equals(line.getTag()))  {
                childDefs.add(line.getTag());
            }
        }

        // fetch child from this definition
        final List<String> thisDefs = new ArrayList<String>();
        for (final ChannelData child : this.channels)  {
            if (child == null)  {
                thisDefs.add("newrow");
            } else  {
                thisDefs.add("channel \"" + AbstractTest.convertUpdate(child.getName()) + "\"");
            }
        }
        // and compare
        Assert.assertEquals(childDefs, thisDefs, "check child of portal");
    }
}
