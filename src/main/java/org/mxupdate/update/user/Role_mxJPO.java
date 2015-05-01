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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * The class is used to export, create, delete and update roles within MX.
 * Following properties are supported:
 * <ul>
 * <li>description</li>
 * <li>{@link #kind}</li>
 * <li>hidden flag</li>
 * <li>site</li>
 * <li>{@link #parentRoles parent roles}</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Role_mxJPO
    extends AbstractUser_mxJPO<Role_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for roles. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Role_mxJPO.IGNORED_URLS.add("/parentRole");
        Role_mxJPO.IGNORED_URLS.add("/roleType");   // URL to be ignored, because read within prepare method
    }

    /**
     * Defines the parameter for the match of roles for which workspace objects
     * are not handled (neither exported nor updated).
     *
     * @see #ignoreWorkspaceObjects(ParameterCache_mxJPO)
     */
    private static final String PARAM_IGNORE_WSO_ROLES = "UserIgnoreWSO4Roles";

    /** Set to hold all parent roles. */
    private final SortedSet<String> parentRoles = new TreeSet<String>();

    /** Stores the information about the role type. */
    private Kind kind = Kind.Role;

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

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new RoleParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * {@inheritDoc}
     * Prepares the internal information after the XML export was parsed by
     * adding the information about the role type (because the role type
     * information was numbers within the XML string).
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, ParseException
    {
        super.parse(_paramCache);

        // must the role type evaluated?
        if (_paramCache.getValueBoolean(ValueKeys.UserRoleSupportRoleType))  {
            final String testRoleType = MqlBuilder_mxJPO.mql()
                    .cmd("escape print role ").arg(this.getName()).cmd(" select ").arg("isanorg").cmd(" ").arg("isaproject").cmd(" dump")
                    .exec(_paramCache);
            if ("FALSE,TRUE".equals(testRoleType))  {
                this.kind = Kind.Project;
            } else if ("TRUE,FALSE".equals(testRoleType)) {
                this.kind = Kind.Organization;
            }
        }
    }

    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
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
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .singleIfTrue(  "kind",                     this.kind.name().toLowerCase(),     (this.kind != Kind.Role))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",            false, this.isHidden())
                .stringIfTrue(  "site",                     this.getSite(),                     (this.getSite() != null) && !this.getSite().isEmpty())
                .list(          "parent",                   this.parentRoles)
                .properties(this.getProperties());
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
            final Collection<String> ignoreMatches = _paramCache.getValueList(ValueKeys.UserRoleSupportRoleType);
            if (ignoreMatches != null)  {
                ignore = StringUtil_mxJPO.match(this.getName(), ignoreMatches);
            }
        }
        return ignore;
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Role_mxJPO _current)
        throws UpdateException_mxJPO
    {
        // kind at first (also needed for parent)
        if (this.kind != _current.kind)  {
            if (_current.kind != Kind.Role)  {
                throw new UpdateException_mxJPO(ErrorKey.USER_ROLE_NOT_ROLE_KIND, this.getName(), _current.kind, this.kind);
            }
            if (this.kind == Kind.Organization)  {
                _mql.newLine().cmd("asanorg");
            } else if (this.kind == Kind.Project)  {
                _mql.newLine().cmd("asaproject");
            }
        }
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this.getTypeDef(), this.getName(), this.getSymbolicNames(), _current.getSymbolicNames());
        DeltaUtil_mxJPO.calcValueDelta(     _mql, "description",        this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(      _mql, "hidden",      false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(     _mql, "site",               this.getSite(),         _current.getSite());
        DeltaUtil_mxJPO.calcLstOneCallDelta(_mql, "parent",             this.parentRoles,       _current.parentRoles);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }

    /**
     * Enumeration for role types.
     */
    public enum Kind
    {
        /** Standard case, the role is a &quot;role&quot;. */
        Role,
        /** The role is a project role. */
        Project,
        /** The role is an organizational role. */
        Organization;
    }
}
