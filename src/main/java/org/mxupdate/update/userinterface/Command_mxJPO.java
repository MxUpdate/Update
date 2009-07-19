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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export and import / update command configuration items.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Command_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -6311434999021971324L;

    /**
     * Set of all ignored URLs from the XML definition for commands.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Command_mxJPO.IGNORED_URLS.add("/code");
        Command_mxJPO.IGNORED_URLS.add("/input");
        Command_mxJPO.IGNORED_URLS.add("/userRefList");
    }

    /**
     * Alt label of the command.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String alt;

    /**
     * Label of the command.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String label;

    /**
     * HRef of the command.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private String href;

    /**
     * Sorted list of assigned users of the command.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> users = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Command_mxJPO(final TypeDef_mxJPO _typeDef,
                         final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all command specific values. This includes:
     * <ul>
     * <li>{@link #alt}</li>
     * <li>{@link #href}</li>
     * <li>user references in {@link #users}</li>
     * </ul>
     *
     * @param _url      URL to parse
     * @param _content  related content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if (!Command_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/alt".equals(_url))  {
                this.alt = _content;
            } else if ("/href".equals(_url))  {
                this.href = _content;
            } else if ("/label".equals(_url))  {
                this.label = _content;
            } else if ("/userRefList/userRef".equals(_url))  {
                this.users.add(_content);
            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the command could not be
     *                     written
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        _out.append(" \\\n    label \"").append(StringUtil_mxJPO.convertTcl(this.label)).append("\"")
            .append(" \\\n    href \"").append(StringUtil_mxJPO.convertTcl(this.href)).append("\"")
            .append(" \\\n    alt \"").append(StringUtil_mxJPO.convertTcl(this.alt)).append("\"");
        for (final String user : this.users)  {
            _out.append(" \\\n    add user \"").append(StringUtil_mxJPO.convertTcl(user)).append("\"");
        }
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                _out.append(" \\\n    add setting \"")
                    .append(StringUtil_mxJPO.convertTcl(prop.getName().substring(1))).append("\"")
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append("\"");
            }
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this command. Following steps
     * are done:
     * <ul>
     * <li>HRef, description, alt and label is set to empty string</li>
     * <li>all settings and users are removed</li>
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
        // reset HRef, description, alt and label
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" href \"\" description \"\" alt \"\" label \"\"");

        // reset settings
        for (final AdminProperty_mxJPO prop : this.getPropertiesMap().values())  {
            if (prop.isSetting())  {
                preMQLCode.append(" remove setting \"")
                          .append(StringUtil_mxJPO.convertMql(prop.getName().substring(1)))
                          .append('\"');
            }
        }

        // reset users
        for (final String user : this.users)  {
            preMQLCode.append(" remove user \"").append(StringUtil_mxJPO.convertMql(user)).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
