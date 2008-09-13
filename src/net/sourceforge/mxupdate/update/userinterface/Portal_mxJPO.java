/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.userinterface;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * The class parses the information about the portal and writes the script used
 * to update portals.
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("portal")
public class Portal_mxJPO
         extends net.sourceforge.mxupdate.update.userinterface.AbstractUIObject_mxJPO
{
    /**
     * Alt (label) of the portal.
     */
    private String alt = null;

    /**
     * Href of the portal.
     */
    private String href = null;

    /**
     * Label of the portal.
     */
    private String label = null;

    /**
     * Stack used to parse the channel references.
     *
     * @see #parse(String, String)
     */
    final private Stack<ChannelRef> channelRefs = new Stack<ChannelRef>();

    /**
     * Ordered channel references by row and column.
     *
     * @see #prepare(Context)
     */
    final Map<Integer,Map<Integer,ChannelRef>> orderedChannelRefs = new TreeMap<Integer,Map<Integer,ChannelRef>>();

    /**
     * Constructor.
     */
    public Portal_mxJPO()
    {
        super("portal");
    }

    /**
     * Parses the {@link #alt}, {@link #href}, {@link #label} and the channel
     * reference {@link #channelRefs}.
     *
     * @param _url      url of the XML tag
     * @param _content  content of the XML tag
     */
    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if ("/alt".equals(_url))  {
            this.alt = _content;
        } else if ("/href".equals(_url))  {
            this.href = _content;
        } else if ("/label".equals(_url))  {
            this.label = _content;

        } else if ("/channelRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/channelRefList/channelRef".equals(_url))  {
            this.channelRefs.add(new ChannelRef());
        } else if ("/channelRefList/channelRef/name".equals(_url))  {
            this.channelRefs.peek().name = _content;
        } else if ("/channelRefList/channelRef/portalRow".equals(_url))  {
            this.channelRefs.peek().row = Integer.parseInt(_content);
        } else if ("/channelRefList/channelRef/portalColumn".equals(_url))  {
            this.channelRefs.peek().column = Integer.parseInt(_content);

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Order the channel references.
     *
     * @param _context  context for this request
     * @see #channelRefs        stack of not ordered channel references
     * @see #orderedChannelRefs ordered channel references
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        // sort the channels by row and column
        for (final ChannelRef channelRef : this.channelRefs)  {
            Map<Integer,ChannelRef> sub = this.orderedChannelRefs.get(channelRef.row);
            if (sub == null)  {
                sub = new TreeMap<Integer,ChannelRef>();
                this.orderedChannelRefs.put(channelRef.row, sub);
            }
            sub.put(channelRef.column, channelRef);
        }

        super.prepare(_context);
    }

    /**
     * Writes specific information about the cached portal to the given writer
     * instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    label \"").append(convert(this.label)).append("\"");
        if (this.href != null)  {
            _out.append(" \\\n    href \"").append(convert(this.href)).append("\"");
        }
        if (this.alt != null)  {
            _out.append(" \\\n    alt \"").append(convert(this.alt)).append("\"");
        }
        // settings
        for (final net.sourceforge.mxupdate.update.MatrixObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add setting \"").append(convert(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convert(prop.getValue())).append("\"");
            }
        }
        // channel references
        boolean firstRow = true;
        for (final Map<Integer,ChannelRef> channelRefs : this.orderedChannelRefs.values())  {
            boolean firstCol = true;
            for (final ChannelRef channelRef : channelRefs.values())  {
                _out.append(" \\\n    place \"").append(convert(channelRef.name)).append("\"");
                if (!firstRow && firstCol)  {
                    _out.append(" newrow");
                }
                _out.append(" after \"\"");
                firstCol = false;
            }
            firstRow = false;
        }
    }

    /**
     * Class holding the channel reference.
     */
    private class ChannelRef
    {
        /**
         * Name of the channel.
         */
        String name = null;

        /**
         * Row of the channel.
         */
        Integer row = null;

        /**
         * Column of the channel.
         */
        Integer column = null;
    }
}
