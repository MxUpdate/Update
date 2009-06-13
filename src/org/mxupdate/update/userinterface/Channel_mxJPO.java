/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.update.userinterface;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Channel_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -8663929923260904020L;

    /**
     * Alt (label) of the channel.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String alt;

    /**
     * Height of the channel.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private Integer height;

    /**
     * Href of the channel.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String href;

    /**
     * Label of the channel.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String label;

    /**
     * Stack with all referenced commands (used while parsing the channel).
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     */
    private final Stack<CommandRef> commandRefs = new Stack<CommandRef>();

    /**
     * Ordered map of referenced commands.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    final Map<Integer,CommandRef> orderCmds = new TreeMap<Integer,CommandRef>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Channel_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all channel specific values. This includes:
     * <ul>
     * <li>command references in {@link #commandRefs}</li>
     * <li>{@link #height}</li>
     * <li>{@link #href}</li>
     * <li>{@link #label}</li>
     * </ul>
     *
     * @param _url      URL to parse
     * @param _content  related content of the URL to parse
     */
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
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed
     * @see #commandRefs        stack of not ordered commands references
     * @see #orderCmds          instance of ordered commands references
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        // order referenced commands
        for (final CommandRef cmdRef : this.commandRefs)  {
            this.orderCmds.put(cmdRef.order, cmdRef);
        }
        super.prepare(_paramCache);
    }

    /**
     * Writes the label, href, alt, height, settings and the referenced
     * commands as MQL code to the writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the channel could not be
     *                     written
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        _out.append(" \\\n    label \"").append(StringUtil_mxJPO.convertTcl(this.label)).append("\"");
        if (this.href != null)  {
            _out.append(" \\\n    href \"").append(StringUtil_mxJPO.convertTcl(this.href)).append("\"");
        }
        if (this.alt != null)  {
            _out.append(" \\\n    alt \"").append(StringUtil_mxJPO.convertTcl(this.alt)).append("\"");
        }
        if (this.height != null)  {
            _out.append(" \\\n    height \"").append(this.height.toString()).append("\"");
        }
        // settings
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                _out.append(" \\\n    add setting \"")
                    .append(StringUtil_mxJPO.convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append("\"");
            }
        }
        // referenced commands
        for (final CommandRef cmdRef : this.orderCmds.values())  {
            _out.append(" \\\n    place \"").append(StringUtil_mxJPO.convertTcl(cmdRef.name)).append("\" after \"\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this channel. Following steps
     * are done:
     * <ul>
     * <li>reset HRef, description, alt and label</li>
     * <li>set height to 0</li>
     * <li>remove all settings and commands</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        // reset HRef, description, alt, label and height
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" href \"\" description \"\" alt \"\" label \"\" height 0");

        // reset settings
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
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

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Stores information about the command reference of channels.
     */
    private class CommandRef
    {
        /**
         * Order of the command reference within a channel.
         */
        Integer order;

        /**
         * Name of the referenced command.
         */
        String name;
    }
}
