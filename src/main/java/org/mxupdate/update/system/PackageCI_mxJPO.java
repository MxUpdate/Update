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

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Handles the export and update of &quot;system packages&quot;.
 * The handled properties are:
 * <ul>
 * <li>uuid</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #custom} flag</li>
 * <li>{@link #usesPackages used packages}</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class PackageCI_mxJPO
    extends AbstractAdminObject_mxJPO<PackageCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList");
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList/member");
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList/member/adminRef");
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList/member/adminRef/adminName");
        PackageCI_mxJPO.IGNORED_URLS.add("/memberList/member/adminRef/adminType");
        PackageCI_mxJPO.IGNORED_URLS.add("/packageList");
    };

    /** Custom flag. */
    private Boolean custom;
    /** Uses packages. */
    private final SortedSet<String> usesPackages = new TreeSet<>();

    /**
     * Initializes this system package configuration item.
     *
     * @param _mxName   name of package
     */
    public PackageCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Package, _mxName);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new PackageParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (PackageCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/custom".equals(_url))  {
            this.custom = true;
            parsed = true;

        } else if ("/memberList/member/protection".equals(_url))  {
            parsed = "public".equals(_content);

        } else if ("/packageList/packageRef".equals(_url))  {
            this.usesPackages.add(_content);
            parsed = true;
        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",            false, this.isHidden())
                .flag(          "custom",            false, this.custom)
                .list(          "usespackage",              this.usesPackages)
                .properties(this.getProperties());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final PackageCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(  _mql, "description",              this.getDescription(),   _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql, "hidden",            false, this.isHidden(),         _current.isHidden());
        DeltaUtil_mxJPO.calcValFlgDelta( _mql, "custom",            false, this.custom,             _current.custom);
        DeltaUtil_mxJPO.calcListDelta(   _mql, "usespackage",              this.usesPackages,       _current.usesPackages);

        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }
}
