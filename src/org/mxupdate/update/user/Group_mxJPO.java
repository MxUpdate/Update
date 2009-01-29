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

package org.mxupdate.update.user;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Context;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 *
 * @author tmoxter
 * @version $Id$
 */
public class Group_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -4819262751735809709L;

    /**
     * Set to hold all parent groups.
     */
    final Set<String> parentGroups = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public Group_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Parses all group specific URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                      final String _content)
    {
        if ("/parentGroup".equals(_url))  {
            // to be ignored ...
        } else if ("/parentGroup/groupRef".equals(_url))  {
            this.parentGroups.add(_content);

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Writes specific information about the cached group to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden");
        for (final String group : this.parentGroups)  {
            _out.append("\nmql mod group \"")
                .append(convertTcl(group))
                .append("\" child \"${NAME}\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this group:
     * <ul>
     * <li>reset description</li>
     * <li>remove all parent groups</li>
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
        // description and all parents
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                .append(" description \"\"")
                .append(" remove parent all;\n");

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
