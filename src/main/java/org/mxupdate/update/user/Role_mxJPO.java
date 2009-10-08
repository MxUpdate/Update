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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export, create, delete and update roles within MX.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Role_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -1889259829075111308L;

    /**
     * Name of the parameter to define if current MX version supports role
     * types.
     *
     * @see #prepare(ParameterCache_mxJPO)
     */
    private static final String PARAM_SUPPORT_ROLE_TYPES = "UserRoleSupportRoleType";

    /**
     * Set of all ignored URLs from the XML definition for roles.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Role_mxJPO.IGNORED_URLS.add("/homeSite");
        Role_mxJPO.IGNORED_URLS.add("/parentRole");
        // new URIL to be ignored, because read within prepare method
        Role_mxJPO.IGNORED_URLS.add("/roleType");
    }

    /**
     * Set to hold all parent roles.
     *
     * @see #parse(String, String)
     */
    private final Set<String> parentRoles = new TreeSet<String>();

    /**
     * Related site of this role.
     *
     * @see #parse(String, String)
     */
    private String site;

    /**
     * Stores the information about the role type.
     *
     * @see RoleType
     * @see #prepare(ParameterCache_mxJPO)
     */
    private RoleType roleType = RoleType.ROLE;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Role_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * <p>Parses all role specific URL values. This includes:
     * <ul>
     * <li>{@link #parentRoles parent roles}</li>
     * <li>{@link #site}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #IGNORED_URLS
     * @see #prepare(ParameterCache_mxJPO)
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if (!Role_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/parentRole/roleRef".equals(_url))  {
                this.parentRoles.add(_content);
            } else if ("/homeSite/siteRef".equals(_url))  {
                this.site = _content;
            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * Prepares the internal information after the XML export was parsed by
     * adding the information about the role type (because the role type
     * information was numbers within the XML string).
     *
     * @param _paramCache   parameter cache with the MX context
     * @throws MatrixException if the information about the role type could not
     *                         be fetched or if the exception was thrown from
     *                         called super method
     * @see #roleType
     * @see #PARAM_SUPPORT_ROLE_TYPES
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // must the role type evaluated?
        if (_paramCache.getValueBoolean(Role_mxJPO.PARAM_SUPPORT_ROLE_TYPES))  {
            final String testRoleType = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                    .append("escape print role \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                    .append("\" select isanorg isaproject dump"));
            if ("FALSE,TRUE".equals(testRoleType))  {
                this.roleType = RoleType.PROJECT;
            } else if ("TRUE,FALSE".equals(testRoleType)) {
                this.roleType = RoleType.ORGANIZATION;
            }
        }

        super.prepare(_paramCache);
    }

    /**
     * Writes specific information about the cached role to the given
     * writer instance.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
        _out.append(" \\\n    ").append(this.isHidden() ? "hidden" : "!hidden");

        // site
        if (this.site != null)  {
            _out.append(" \\\n    site \"").append(StringUtil_mxJPO.convertTcl(this.site)).append('\"');
        }

        // role type
        switch (this.roleType)  {
            case PROJECT:
                _out.append(" \\\n    asaproject");
                break;
            case ORGANIZATION:
                _out.append(" \\\n    asanorg");
                break;
            case ROLE:
            default:
                // the role is the default value
        }
        // parent roles
        for (final String role : this.parentRoles)  {
            _out.append("\nmql escape mod role \"")
                .append(StringUtil_mxJPO.convertTcl(role))
                .append("\" child \"${NAME}\"");
        }
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this role. Following steps are
     * done:
     * <ul>
     * <li>reset description</li>
     * <li>remove all parent groups</li>
     * <li>define as &quot;normal&quot; role</li>
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
                .append(" remove parent all");
        // define normal role type
        if (this.roleType != RoleType.ROLE)  {
            preMQLCode.append(" asarole");
        }
        // remove site (if exists)
        if (this.site != null)  {
            preMQLCode.append(" site \"\"");
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Enumeration for role types.
     */
    private enum RoleType
    {
        /** Standard case, the role is a &quot;role&quot;. */
        ROLE,
        /** The role is a project role. */
        PROJECT,
        /** The role is an organizational role. */
        ORGANIZATION;
    }
}
