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
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Context;

import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.util.TypeDef_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
public class Command_mxJPO
        extends AbstractAdminObject_mxJPO
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

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public Command_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

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
        _out.append(" \\\n    label \"").append(convertTcl(this.label)).append("\"")
            .append(" \\\n    href \"").append(convertTcl(this.href)).append("\"")
            .append(" \\\n    alt \"").append(convertTcl(this.alt)).append("\"");
        for (final String user : this.users)  {
            _out.append(" \\\n    add user \"").append(convertTcl(user)).append("\"");
        }
        for (final AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                _out.append(" \\\n    add setting \"").append(convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(convertTcl(prop.getValue())).append("\"");
            }
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this command:
     * <ul>
     * <li>HRef, description, alt and label is set to empty string</li>
     * <li>all settings and users are removed</li>
     * </ul>
     *
     * @param _context          context for this request
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
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        // reset HRef, description, alt and label
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" href \"\" description \"\" alt \"\" label \"\"");

        // reset settings
        for (final AbstractAdminObject_mxJPO.Property prop : this.getPropertiesMap().values())  {
            if (prop.getName().startsWith("%"))  {
                preMQLCode.append(" remove setting \"").append(prop.getName().substring(1)).append('\"');
            }
        }

        // reset users
        for (final String user : this.users)  {
            preMQLCode.append(" remove user \"").append(user).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}