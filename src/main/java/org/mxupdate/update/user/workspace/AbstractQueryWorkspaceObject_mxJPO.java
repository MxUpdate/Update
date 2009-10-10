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

package org.mxupdate.update.user.workspace;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store common information of a workspace object with
 * query functionality.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
abstract class AbstractQueryWorkspaceObject_mxJPO
    extends AbstractWorkspaceObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for common stuff of
     * users.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractQueryWorkspaceObject_mxJPO.IGNORED_URLS.add("/queryStatement");
    }

    /**
     * Type pattern.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternType;

    /**
     * Name pattern.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternName;

    /**
     * Revision pattern.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternRevision;

    /**
     * Vault pattern.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternVault;

    /**
     * Owner pattern.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String patternOwner;

    /**
     * Where clause.
     *
     * @see #parse(String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String whereClause;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     * @param _mxAdminType  administration type of the query workspace object
     */
    AbstractQueryWorkspaceObject_mxJPO(final AbstractUser_mxJPO _user,
                                       final String _mxAdminType)
    {
        super(_user, _mxAdminType);
    }

    /**
     * <p>Parses all common query workspace object specific URL values. This
     * includes:
     * <ul>
     * <li>{@link #patternName pattern for the name}</li>
     * <li>{@link #patternOwner pattern for the owner}</li>
     * <li>{@link #patternRevision pattern for the revision}</li>
     * <li>{@link #patternType pattern for the type}</li>
     * <li>{@link #patternVault pattern for the vault}</li>
     * <li>{@link #whereClause where clause}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #IGNORED_URLS
     */
    @Override()
    public void parse(final String _url,
                      final String _content)
    {
        if (!AbstractQueryWorkspaceObject_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/queryStatement/namePattern".equals(_url))  {
                this.patternName = _content;
            } else if ("/queryStatement/ownerPattern".equals(_url))  {
                this.patternOwner = _content;
            } else if ("/queryStatement/revisionPattern".equals(_url))  {
                this.patternRevision = _content;
            } else if ("/queryStatement/typePattern".equals(_url))  {
                this.patternType = _content;
            } else if ("/queryStatement/vaultPattern".equals(_url))  {
                this.patternVault = _content;
            } else if ("/queryStatement/whereClause".equals(_url))  {
                this.whereClause = _content;
            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * <p>Writes all common query workspace object specific values to the TCL
     * update file <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #patternName pattern for the name}</li>
     * <li>{@link #patternOwner pattern for the owner}</li>
     * <li>{@link #patternRevision pattern for the revision}</li>
     * <li>{@link #patternType pattern for the type}</li>
     * <li>{@link #patternVault pattern for the vault}</li>
     * <li>{@link #whereClause where clause}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
    throws IOException
    {
        super.write(_paramCache, _out);
        this.writeTypeNameRevision(_out, this.patternType, this.patternName, this.patternRevision);
        _out.append(" \\\n    vault \"").append(StringUtil_mxJPO.convertTcl(this.patternVault)).append("\"")
            .append(" \\\n    owner \"").append(StringUtil_mxJPO.convertTcl(this.patternOwner)).append("\"");
        if (this.whereClause != null)  {
            _out.append(" \\\n    where \"").append(StringUtil_mxJPO.convertTcl(this.whereClause)).append("\"");
        }
    }

    /**
     * Writes the pattern for the business type, name and revision to the TCL
     * update file in <code>_out</code>.
     *
     * @param _out          appendable instance to the TCL update file
     * @param _type         pattern type
     * @param _name         pattern name
     * @param _revision     pattern revision
     * @throws IOException if the TCL update code could not written
     */
    protected abstract void writeTypeNameRevision(final Appendable _out,
                                                  final String _type,
                                                  final String _name,
                                                  final String _revision)
        throws IOException;
}
