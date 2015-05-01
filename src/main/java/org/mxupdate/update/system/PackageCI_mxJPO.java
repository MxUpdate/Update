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

package org.mxupdate.update.system;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateLine;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Handles the export and update of &quot;system packages&quot;.
 * The handled properties are:
 * <ul>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #custom} flag</li>
 * <li>{@link #usesPackages used packages}</li>
 * <li>{@link #members member admin objects}</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class PackageCI_mxJPO
    extends AbstractAdminObject_mxJPO<PackageCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList");
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList/member/adminRef");
        PackageCI_mxJPO.IGNORED_URLS.add("/packageList");
    };

    /** Custom flag. */
    private Boolean custom;
    /** Uses packages. */
    private final SortedSet<String> usesPackages = new TreeSet<String>();
    /** Holds all referenced meber's. */
    private final Stack<MemberRef> members = new Stack<MemberRef>();

    /**
     * Initializes this system package configuration item.
     *
     * @param _typeDef  type definition of package
     * @param _mxName   name of package
     */
    public PackageCI_mxJPO(final TypeDef_mxJPO _typeDef,
                           final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new PackageParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (PackageCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/custom".equals(_url))  {
            this.custom = true;
            parsed = true;

        } else if ("/memberList/member".equals(_url))  {
            this.members.add(new MemberRef());
            parsed = true;
        } else if ("/memberList/member/adminRef/adminType".equals(_url))  {
            this.members.peek().refAdminType = _content;
            parsed = true;
        } else if ("/memberList/member/adminRef/adminName".equals(_url))  {
            this.members.peek().refAdminName = _content;
            parsed = true;
        } else if ("/memberList/member/protection".equals(_url))  {
            parsed = "public".equals(_content);

        } else if ("/packageList/packageRef".equals(_url))  {
            this.usesPackages.add(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * {@inheritDoc}
     * All {@link #members} are sorted.
     */
    @Override()
    protected void prepare()
    {
        super.prepare();

        Collections.sort(this.members);
    }

    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(_paramCache);

        this.writeHeader(_paramCache, updateBuilder.getStrg());

        updateBuilder
                .start("package")
                .string("description", this.getDescription())
                .flag("hidden", false, this.isHidden())
                .flag("custom", false, this.custom)
                .list("usespackage", this.usesPackages)
                .list(this.members)
                .properties(this.getProperties())
                .end();

        _out.append(updateBuilder.toString());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final PackageCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(  _mql, "description",              this.getDescription(),   _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql, "hidden",            false, this.isHidden(),         _current.isHidden());
        DeltaUtil_mxJPO.calcValFlgDelta( _mql, "custom",            false, this.custom,             _current.custom);
        DeltaUtil_mxJPO.calcListDelta(   _mql, "usespackage",              this.usesPackages,       _current.usesPackages);

        // remove not defined members
        for (final MemberRef curMember : _current.members)  {
            boolean found = false;
            for (final MemberRef thisMember : this.members)  {
                if (curMember.compareTo(thisMember) == 0)  {
                    found = true;
                    break;
                }
            }
            if (!found)  {
                _mql.newLine().cmd("remove member ").cmd(curMember.refAdminType).cmd(" ").arg(curMember.refAdminName);
            }
        }
        // append new members
        for (final MemberRef thisMember : this.members)  {
            boolean found = false;
            for (final MemberRef curMember : _current.members)  {
                if (curMember.compareTo(thisMember) == 0)  {
                    found = true;
                    break;
                }
            }
            if (!found)  {
                _mql.newLine().cmd("add member ").cmd(thisMember.refAdminType).cmd(" ").arg(thisMember.refAdminName);
            }
        }

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }

    /**
     * One referenced member.
     */
    public static class MemberRef
        implements Comparable<MemberRef>, UpdateLine
    {
        /** Type of the referenced administration object. */
        private String refAdminType;
        /** Name of the referenced administration object. */
        private String refAdminName;

        @Override()
        public int compareTo(final MemberRef _toCompare)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.refAdminType, _toCompare.refAdminType);
            ret = CompareToUtil_mxJPO.compare(ret, this.refAdminName, _toCompare.refAdminName);
            return ret;
        }

        @Override()
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            _updateBuilder.stepStartNewLine().stepSingle("member").stepSingle(this.refAdminType).stepString(this.refAdminName).stepEndLine();
        }
    }
}
