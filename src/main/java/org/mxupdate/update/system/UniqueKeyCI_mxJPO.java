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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateLine;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Handles the export and update of &quot;system unqiue keys&quot;.
 * The handled properties are:
 * <ul>
 * <li>uuid</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #global} flag</li>
 * <li>{@link #enable} flag</li>
 * <li>for {@link #forType type} or {@link #forRelation relationship} with
 *     {@link #withInterface interface}</li>
 * <li>list of {@link #fields} with depending sizes</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class UniqueKeyCI_mxJPO
    extends AbstractAdminObject_mxJPO<UniqueKeyCI_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for packages. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
    };

    /** Enabled and global flag. */
    private boolean enable, global;
    /** Unique key is defined for relationship / type with interface. */
    private String forRelation, forType, withInterface;
    /** List of fields. */
    private final Stack<Field> fields = new Stack<>();

    /**
     * Initializes this system unique key configuration item.
     *
     * @param _mxName   name of package
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

    /**
     * Sorted the {@link #fields} and derived information.
     */
    @Override
    protected void prepare()
    {
        super.prepare();
        Collections.sort(this.fields);
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
                .flag(          "enable",           false, this.enable)
                .flagIfTrue(    "global",           false, this.global,                         this.global)
                .stringNotNull( "for type",                this.forType)
                .stringNotNull( "for relationship",        this.forRelation)
                .stringNotNull( "with interface",          this.withInterface)
                .list(this.fields)
                .properties(this.getProperties());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final UniqueKeyCI_mxJPO _current)
        throws UpdateException_mxJPO
    {
    }

    /**
     * One field.
     */
    public static class Field
        implements Comparable<Field>, UpdateLine
    {
        /** Field expression. */
        private String expression;
        /** Field size. */
        private int size;

        @Override
        public int compareTo(final Field _toCompare)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.expression, _toCompare.expression);
            ret = CompareToUtil_mxJPO.compare(ret, this.size,       _toCompare.size);
            return ret;
        }

        @Override
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            _updateBuilder.stepStartNewLine().stepSingle("field").stepString(this.expression);
            if (this.expression.startsWith("attribute["))  {
                _updateBuilder.stepSingle("size").stepSingle(String.valueOf(this.size));
            }
            _updateBuilder.stepEndLine();
        }
    }
}
