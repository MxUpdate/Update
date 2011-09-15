/*
 * Copyright 2008-2011 The MxUpdate Team
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update groups within MX.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Group_mxJPO
    extends AbstractUser_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for groups.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Group_mxJPO.IGNORED_URLS.add("/parentGroup");
    }

    /**
     * Defines the parameter for the match of groups for which workspace
     * objects are not handled (neither exported nor updated).
     *
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
    private static final String PARAM_IGNORE_WSO_GROUPS = "UserIgnoreWSO4Groups";

    /**
     * Set to hold all parent groups.
     */
    private final Set<String> parentGroups = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the group object
     */
    public Group_mxJPO(final TypeDef_mxJPO _typeDef,
                       final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * <p>Parses all group specific URL values. This includes:
     * <ul>
     * <li>{@link #parentGroups parent groups}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Group_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/parentGroup/groupRef".equals(_url))  {
            this.parentGroups.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes specific information about the cached group to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code for the group could not be
     *                     written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        super.writeObject(_paramCache, _out);

        // parent groups
        for (final String group : this.parentGroups)  {
            _out.append("\nmql escape mod group \"")
                .append(StringUtil_mxJPO.convertTcl(group))
                .append("\" child \"${NAME}\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this group. Following steps
     * are done:
     * <ul>
     * <li>reset description</li>
     * <li>remove all parent groups</li>
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
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // description and all parents
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                .append(" description \"\"")
                .append(" remove parent all;\n")
        // append already existing pre MQL code
                .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * <p>Calculates if workspace objects for this group are not handled. This
     * is done by checking if the name of this group matches one of the match
     * lists defined with parameter {@link #PARAM_IGNORE_WSO_GROUPS}. In this
     * case the the workspace objects are ignored for this group.</p>
     *
     * @param _paramCache       parameter cache
     * @return <i>true</i> if the handling of workspace objects for this group
     *         is ignored
     * @see #PARAM_IGNORE_WSO_GROUPS
     */
    @Override()
    protected boolean ignoreWorkspaceObjects(final ParameterCache_mxJPO _paramCache)
    {
        boolean ignore = super.ignoreWorkspaceObjects(_paramCache);
        if (!ignore)  {
            final Collection<String> ignoreMatches = _paramCache.getValueList(Group_mxJPO.PARAM_IGNORE_WSO_GROUPS);
            if (ignoreMatches != null)  {
                ignore = StringUtil_mxJPO.match(this.getName(), ignoreMatches);
            }
        }
        return ignore;
    }
}
