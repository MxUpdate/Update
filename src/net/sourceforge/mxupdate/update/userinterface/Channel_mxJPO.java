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

import net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.AdminTypeDef;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@InfoAnno_mxJPO(adminType = AdminTypeDef.Channel)
public class Channel_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -8663929923260904020L;

    /**
     * Alt (label) of the channel.
     */
    private String alt = null;

    /**
     * Height of the channel.
     */
    private Integer height = null;

    /**
     * Href of the channel.
     */
    private String href = null;

    /**
     * Label of the channel.
     */
    private String label = null;

    /**
     * Stack with all referenced commands (used while parsing the channel).
     *
     * @see #parse(Context, String)
     * @see #prepare(Context)
     */
    private final Stack<CommandRef> commandRefs = new Stack<CommandRef>();

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
            this.orderCmds.put(cmdRef.order, cmdRef);
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
        _out.append(" \\\n    label \"").append(convertTcl(this.label)).append("\"");
        if (this.href != null)  {
            _out.append(" \\\n    href \"").append(convertTcl(this.href)).append("\"");
        }
        if (this.alt != null)  {
            _out.append(" \\\n    alt \"").append(convertTcl(this.alt)).append("\"");
        }
        if (this.height != null)  {
            _out.append(" \\\n    height \"").append(this.height.toString()).append("\"");
        }
        // settings
        for (final net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add setting \"").append(convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convertTcl(prop.getValue())).append("\"");
            }
        }
        // referenced commands
        for (final CommandRef cmdRef : this.orderCmds.values())  {
            _out.append(" \\\n    place \"").append(convertTcl(cmdRef.name)).append("\" after \"\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this channel:
     * <ul>
     * <li>reset HRef, description, alt and label</li>
     * <li>set height to 0</li>
     * <li>remove all settings and commands</li>
     * </ul>
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _tclCode          TCL code from the file used to update
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        // reset HRef, description, alt, label and height
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getInfoAnno().adminType().getMxName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" href \"\" description \"\" alt \"\" label \"\" height 0");

        // reset settings
        for (final AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                preMQLCode.append(" remove setting \"").append(prop.getName().substring(1)).append('\"');
            }
        }

        // remove commands
        for (final CommandRef cmdRef : this.orderCmds.values())  {
            preMQLCode.append(" remove command \"").append(cmdRef.name).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _tclCode, _tclVariables);
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
