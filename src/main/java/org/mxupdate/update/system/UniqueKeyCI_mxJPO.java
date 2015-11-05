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

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MqlBuilder;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.util.StringUtils_mxJPO;

import matrix.util.MatrixException;

/**
 * Handles the export and update of &quot;system unqiue keys&quot;.
 * The handled properties are:
 * <ul>
 * <li>package</li>
 * <li>uuid</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #global} flag</li>
 * <li>enable flag</li>
 * <li>for {@link #forType type} or {@link #forRelation relationship} with
 *     {@link #withInterface interface}</li>
 * <li>list of fields with depending sizes</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public class UniqueKeyCI_mxJPO
    extends AbstractIndexCI_mxJPO<UniqueKeyCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        UniqueKeyCI_mxJPO.IGNORED_URLS.add("/typeRefList");
    };

    /** Global flag. */
    private boolean global = false;
    /** Unique key is defined for relationship / type with interface. */
    private String forRelation, forType, withInterface;

    /**
     * Initializes this system unique key configuration item.
     *
     * @param _mxName   name of unique key
     */
    public UniqueKeyCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.UniqueKey, _mxName);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new UniqueKeyParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override
    public void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException, ParseException
    {
        super.parse(_paramCache);

        // fix field size (correct value differs!)
        for (final Field field : this.getFields())  {
            field.setSize(field.getSize() + 4);
        }
    }

    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (UniqueKeyCI_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;

        } else if ("/global".equals(_url))  {
            this.global = true;
            parsed = true;
        } else if ("/interfaceTypeRef".equals(_url))  {
            this.withInterface = _content;
            parsed = true;
        } else if ("/relationshipDefRef".equals(_url))  {
            this.forRelation = _content;
            parsed = true;
        } else if ("/typeRefList/typeRef".equals(_url))  {
            this.forType = _content;
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
                .stringNotNull( "package",                  this.getPackageRef())
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",            false, this.isHidden())
                .flag(          "enable",            false, this.isEnable())
                .flagIfTrue(    "global",            false, this.global,                         this.global)
                .stringNotNull( "for type",                 this.forType)
                .stringNotNull( "for relationship",         this.forRelation)
                .stringNotNull( "with interface",           this.withInterface)
                .list(this.getFields())
                .properties(this.getProperties());
    }

    @Override
    public void createOld(final ParameterCache_mxJPO _paramCache)
    {
    }

    @Override
    public void create(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final MqlBuilder mql = MqlBuilderUtil_mxJPO.mql().cmd("escape add uniquekey ").arg(this.getName());
        if (!StringUtils_mxJPO.isEmpty(this.forType))  {
            mql.cmd(" type ").arg(this.forType);
        } else if (!StringUtils_mxJPO.isEmpty(this.forRelation))  {
            mql.cmd(" relationship ").arg(this.forRelation);
        }
        if (!StringUtils_mxJPO.isEmpty(this.withInterface))  {
            mql.cmd(" interface ").arg(this.withInterface);
        }
        if (this.global)  {
            mql.cmd(" global");
        }
        mql.exec(_paramCache.getContext());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final UniqueKeyCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        int bck = 0;
        bck = CompareToUtil_mxJPO.compare(bck, this.forRelation,    _current.forRelation);
        bck = CompareToUtil_mxJPO.compare(bck, this.forType,        _current.forType);
        bck = CompareToUtil_mxJPO.compare(bck, this.withInterface,  _current.withInterface);
        bck = CompareToUtil_mxJPO.compare(bck, this.global,         _current.global);
        if (bck != 0)  {
            throw new UpdateException_mxJPO(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED, this.getName());
        }

        super.calcDelta(_paramCache, _mql, _current);
    }
}
