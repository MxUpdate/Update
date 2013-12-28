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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * Used to define a portal, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class PortalData
    extends AbstractUIWithSettingData<PortalData>
{
    /**
     * Within export the description and label must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>(3);
    static  {
        PortalData.REQUIRED_EXPORT_VALUES.put("description", "");
        PortalData.REQUIRED_EXPORT_VALUES.put("label", "");
    }

    /**
     * All channels of the portal.
     *
     * @see #addChannel(ChannelData)
     * @see #getChannels()
     * @see #create()
     * @see #evalAdds4CheckExport(Set)
     */
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
        super(_test, AbstractTest.CI.UI_PORTAL, _name, PortalData.REQUIRED_EXPORT_VALUES);
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
        final StringBuilder cmd = new StringBuilder();
        this.append4CIFileHeader(cmd);
        cmd.append("mql escape mod portal \"${NAME}\"");

        // append channels
        for (final ChannelData channel : this.channels)  {
            cmd.append(" \\\n  place \"").append(AbstractTest.convertTcl(channel.getName())).append("\" after \"\"");
        }

        this.append4CIFileValues(cmd);

        return cmd.toString();
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

            // append all channels
            if (!this.channels.isEmpty())  {
                final List<String> names = new ArrayList<String>();
                for (final ChannelData channel : this.channels)  {
                    names.add(channel.getName());
                }
                cmd.append(" channel ").append(StringUtil_mxJPO.joinMql(',', true, names, null));
            }
            this.append4Create(cmd);

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
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
            channel.create();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * Also the channels which are defined as place are checked.
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);
        // check for channels (they are not add's!)
        final Set<String> needPlaces = new HashSet<String>();
        for (final ChannelData channel : this.channels)  {
            needPlaces.add("\"" + AbstractTest.convertTcl(channel.getName()) + "\" after \"\"");
        }
        final List<String> foundPlaces = _exportParser.getLines("/mql/place/@value");
        Assert.assertEquals(foundPlaces.size(), needPlaces.size(), "all adds defined (found places = " + foundPlaces + "; need places = " + needPlaces + ")");
        for (final String foundPlace : foundPlaces)  {
            Assert.assertTrue(needPlaces.contains(foundPlace), "check that place '" + foundPlace + "' is defined");
        }
    }
}
