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
import org.mxupdate.update.util.UpdateBuilder_mxJPO;

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
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
    };

    /** Unique flag. */
    private boolean unique;

    /**
     * Initializes this system index configuration item.
     *
     * @param _mxName   name of unique key
     */
    public IndexCI_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.UniqueKey, _mxName);
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
}
