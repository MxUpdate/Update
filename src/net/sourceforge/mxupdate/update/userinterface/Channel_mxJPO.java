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
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "channel",
                                                     filePrefix = "CHANNEL",
                                                     filePath = "userinterface/channel",
                                                     description = "channel")
public class Channel_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO
{
    /**
     * Alt (label) of the channel.
     */
    String alt = null;

    /**
     * Height of the channel.
     */
    Integer height = null;

    /**
     * Href of the channel.
     */
    String href = null;

    /**
     * Label of the channel.
     */
    String label = null;

    /**
     * Stack with all referenced commands (used while parsing the channel).
     *
     * @see #parse(Context, String)
     * @see #prepare(Context)
     */
    final Stack<CommandRef> commandRefs = new Stack<CommandRef>();

    /**
     * Ordered map of referenced commands.
     *
     * @see #prepare(Context)
     * @see #writeObject(Writer)
     */
    final Map<Integer,CommandRef> orderCmds = new TreeMap<Integer,CommandRef>();

    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/alt".equals(_url))  {
            this.alt = _content;

        } else if ("/commandRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/commandRefList/commandRef".equals(_url))  {
            this.commandRefs.add(new CommandRef());
        } else if ("/commandRefList/commandRef/name".equals(_url))  {
            this.commandRefs.peek().name = _content;
        } else if ("/commandRefList/commandRef/order".equals(_url))  {
            this.commandRefs.peek().order = Integer.parseInt(_content);

        } else if ("/height".equals(_url))  {
            this.height = Integer.parseInt(_content);
        } else if ("/href".equals(_url))  {
            this.href = _content;
        } else if ("/label".equals(_url))  {
            this.label = _content;
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Order the command references.
     *
     * @param _context  context for this request
     * @see #commandRefs        stack of not ordered commands references
     * @see #orderCmds          instance of ordered commands references
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        // order referenced commands
        for (final CommandRef cmdRef : this.commandRefs)  {
            orderCmds.put(cmdRef.order, cmdRef);
        }
        super.prepare(_context);
    }

    /**
     * Writes the label, href, alt, height, settings and the referenced
     * commands as MQL code to the writer instance.
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
        if (this.height != null)  {
            _out.append(" \\\n    height \"").append(this.height.toString()).append("\"");
        }
        // settings
        for (final net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add setting \"").append(convert(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convert(prop.getValue())).append("\"");
            }
        }
        // referenced commands
        for (final CommandRef cmdRef : this.orderCmds.values())  {
            _out.append(" \\\n    place \"").append(convert(cmdRef.name)).append("\" after \"\"");
        }
    }

    /**
     * Stores information about the command reference of channels
     */
    private class CommandRef
    {
        /**
         * Order of the command reference within a channel.
         */
        Integer order = null;

        /**
         * Name of the referenced command.
         */
        String name = null;
    }
}
