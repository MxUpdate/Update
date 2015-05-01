/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.update.user;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.xml.sax.SAXException;

/**
 * The class is used to export, create, delete and update roles within MX.
 *
 * @author The MxUpdate Team
 */
public class Role_mxJPO
    extends AbstractUser_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for roles.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Role_mxJPO.IGNORED_URLS.add("/parentRole");
        // new URIL to be ignored, because read within prepare method
        Role_mxJPO.IGNORED_URLS.add("/roleType");
    }

    /**
     * Name of the parameter to define if current MX version supports role
     * types.
     *
     * @see #prepare(ParameterCache_mxJPO)
     */
    private static final String PARAM_SUPPORT_ROLE_TYPES = "UserRoleSupportRoleType";

    /**
     * Defines the parameter for the match of roles for which workspace objects
     * are not handled (neither exported nor updated).
     *
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
    private static final String PARAM_IGNORE_WSO_ROLES = "UserIgnoreWSO4Roles";

    /**
     * Set to hold all parent roles.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private final Set<String> parentRoles = new TreeSet<String>();

    /**
     * Stores the information about the role type.
     *
     * @see RoleType
     * @see #prepare(ParameterCache_mxJPO)
     */
    private RoleType roleType = RoleType.ROLE;

    /**
     * Constructor used to initialize this role definition with related type
     * definition <code>_typeDef</code> for given <code>_name</code>.
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
     * {@inheritDoc}
     * Prepares the internal information after the XML export was parsed by
     * adding the information about the role type (because the role type
     * information was numbers within the XML string).
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        super.parse(_paramCache);

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
    }

    /**
     * <p>Parses all role specific URL values. This includes:
     * <ul>
     * <li>{@link #parentRoles parent roles}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     * @see #prepare(ParameterCache_mxJPO)
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Role_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/parentRole/roleRef".equals(_url))  {
            this.parentRoles.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes specific information about the cached role to the given
     * writer instance. This includes
     * <ul>
     * <li>role type (if parameter {@link #PARAM_SUPPORT_ROLE_TYPES} is
     *     defined)</li>
     * <li>all parent roles</li>
     * </ul>
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
        super.writeObject(_paramCache, _out);

        // role type (only of role types are supported)
        if (_paramCache.getValueBoolean(Role_mxJPO.PARAM_SUPPORT_ROLE_TYPES))  {
            switch (this.roleType)  {
                case PROJECT:
                    _out.append(" \\\n    asaproject");
                    break;
                case ORGANIZATION:
                    _out.append(" \\\n    asanorg");
                    break;
                case ROLE:
                default:
                    _out.append(" \\\n    asarole");
                    break;
            }
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

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * <p>Calculates if workspace objects for this role are not handled. This
     * is done by checking if the name of this role matches one of the match
     * lists defined with parameter {@link #PARAM_IGNORE_WSO_ROLES}. In this
     * case the the workspace objects are ignored for this role.</p>
     *
     * @param _paramCache       parameter cache
     * @return <i>true</i> if the handling of workspace objects for this role
     *         is ignored
     * @see #PARAM_IGNORE_WSO_ROLES
     */
    @Override()
    protected boolean ignoreWorkspaceObjects(final ParameterCache_mxJPO _paramCache)
    {
        boolean ignore = super.ignoreWorkspaceObjects(_paramCache);
        if (!ignore)  {
            final Collection<String> ignoreMatches = _paramCache.getValueList(Role_mxJPO.PARAM_IGNORE_WSO_ROLES);
            if (ignoreMatches != null)  {
                ignore = StringUtil_mxJPO.match(this.getName(), ignoreMatches);
            }
        }
        return ignore;
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
