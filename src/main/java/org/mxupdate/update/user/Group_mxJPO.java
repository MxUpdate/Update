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
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export, create, delete and update groups within MX.
 * Following properties are supported:
 * <ul>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>site</li>
 * <li>{@link #parentGroups parent groups}</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Group_mxJPO
    extends AbstractUser_mxJPO<Group_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for groups. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
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

    /** Set to hold all parent groups. */
    private final SortedSet<String> parentGroups = new TreeSet<>();

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

    @Override()
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new GroupParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
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
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",            false, this.isHidden())
                .stringIfTrue(  "site",                     this.getSite(),                     (this.getSite() != null) && !this.getSite().isEmpty())
                .list(          "parent",                   this.parentGroups)
                .properties(this.getProperties());
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

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Group_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(     _mql, "description",        this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(      _mql, "hidden",      false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(     _mql, "site",               this.getSite(),         _current.getSite());
        DeltaUtil_mxJPO.calcLstOneCallDelta(_mql, "parent",             this.parentGroups,      _current.parentGroups);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}
