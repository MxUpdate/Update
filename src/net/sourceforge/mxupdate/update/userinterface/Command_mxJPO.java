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
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Context;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "command",
                                                     title = "COMMAND",
                                                     filePrefix = "COMMAND_",
                                                     fileSuffix = ".tcl",
                                                     filePath = "userinterface/command",
                                                     description = "command")
public class Command_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -6311434999021971324L;

    String alt = null;

    /**
     * Label of the command.
     */
    String label = null;

    /**
     * HRef of the command.
     */
    String href = null;

    /**
     * Sorted list of assigned users of the command.
     */
    final Set<String> users = new TreeSet<String>();

    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/alt".equals(_url))  {
            this.alt = _content;
        } else if ("/code".equals(_url))  {
            // to be ignored ...
        } else if ("/href".equals(_url))  {
            this.href = _content;
        } else if ("/input".equals(_url))  {
            // to be ignored ...
        } else if ("/label".equals(_url))  {
            this.label = _content;
        } else if ("/userRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/userRefList/userRef".equals(_url))  {
            this.users.add(_content);
        } else  {
            super.parse(_url, _content);
        }
    }

    @Override
    protected void writeObject(Writer _out) throws IOException
    {
        _out.append(" \\\n    label \"").append(convert(this.label)).append("\"")
            .append(" \\\n    href \"").append(convert(this.href)).append("\"")
            .append(" \\\n    alt \"").append(convert(this.alt)).append("\"");
        for (final String user : this.users)  {
            _out.append(" \\\n    add user \"").append(convert(user)).append("\"");
        }
        for (final net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add setting \"").append(convert(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convert(prop.getValue())).append("\"");
            }
        }
    }


    /**
     * Appends the MQL statement to reset this command:
     * <ul>
     * <li>HRef, description, alt and label is set to empty string</li>
     * <li>all settings, users and properties are removed</li>
     * </ul>
     *
     * @param _context  context for this request
     * @param _cmd      string builder used to append the MQL statements
     */
    @Override
    protected void appendResetMQL(final Context _context,
                                  final StringBuilder _cmd)
    {
        // reset command
        _cmd.append("mod ").append(getInfoAnno().adminType())
            .append(" \"").append(getName()).append('\"')
           .append(" href \"\" description \"\" alt \"\" label \"\"");
        // reset settings
        for (final net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _cmd.append(" remove setting \"").append(prop.getName().substring(1)).append('\"');
            }
        }
        // reset users
        for (final String user : this.users)  {
            _cmd.append(" remove user \"").append(user).append('\"');
        }
        // reset properties
        appendResetProperties(_cmd);
    }
}