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

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

import matrix.util.MatrixException;

/**
 * Handles the export and update of &quot;system indexs&quot;.
 * The handled properties are:
 * <ul>
 * <li>uuid</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #unique} flag</li>
 * <li>enable flag</li>
 * <li>list of fields with depending sizes</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public class IndexCI_mxJPO
    extends AbstractIndexCI_mxJPO<IndexCI_mxJPO>
{
    /** Unique flag. */
    private boolean unique;

    /**
     * Initializes this system index configuration item.
     *
     * @param _mxName   name of unique key
     */
    public IndexCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Index, _mxName);
    }

    @Override
    public void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException, ParseException
    {
        super.parse(_paramCache);

        // evaluate unique flag directly (because not included in the XML export)
        this.unique = Boolean.valueOf(MqlBuilderUtil_mxJPO.mql().cmd("escape print ").cmd(this.mxClassDef().mxClass()).cmd(" ").arg(this.getName()).cmd(" select ").arg("unique").cmd(" dump").exec(_paramCache.getContext()));
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new IndexParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag            | default | value                              | write?
                .stringNotNull( "uuid",                    this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",            this.getSymbolicNames())
                .string(        "description",             this.getDescription())
                .flag(          "hidden",           false, this.isHidden())
                .flag(          "enable",           false, this.isEnable())
                .flagIfTrue(    "unique",           false, this.unique,                         this.unique)
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
        MqlBuilderUtil_mxJPO.mql().cmd("escape add index ").arg(this.getName()).exec(_paramCache.getContext());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final IndexCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "unique",            false, this.unique,            _current.unique);

        super.calcDelta(_paramCache, _mql, _current);
    }
}
