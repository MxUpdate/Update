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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Data model type class.
 * The handled properties are:
 * <ul>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #globalAttributes global attributes}</li>
 * <li>{@link #localAttributes local attributes}</li>
 * <li>{@link #from} and {@link #to} side informations</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class PathType_mxJPO
    extends AbstractAdminObject_mxJPO<PathType_mxJPO>
{
    /** From cardinality. */
    private Cardinality fromCardinality = Cardinality.Many;

    /** From / to side type list. */
    private final SortedSet<String> fromTypes = new TreeSet<>(), toTypes = new TreeSet<>();
    /** Are all types on the from / to side allowed? */
    private boolean fromTypeAll = false, toTypeAll = false;

    /** From / to side relationship list. */
    private final SortedSet<String> fromRelations = new TreeSet<>(), toRelations = new TreeSet<>();
    /** Are all relationships on the from /to side allowed? */
    private boolean fromRelationAll = false, toRelationAll = false;

    /** Global attributes. */
    private final SortedSet<String> globalAttributes = new TreeSet<>();
    /** Local attributes. */
    private final LocalAttributeList_mxJPO localAttributes = new LocalAttributeList_mxJPO();

    /**
     * Constructor used to initialize the path type definition.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the type object
     */
    public PathType_mxJPO(final TypeDef_mxJPO _typeDef,
                          final String _mxName)
    {
        super(_typeDef, _mxName);
this.fromCardinality = Cardinality.Many;
this.fromTypeAll = false;this.toTypeAll = false;
this.fromRelationAll = false;this.toRelationAll = false;
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new PathTypeParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * After the path type is parsed, the path type and local attributes must be
     * prepared.
     */
    @Override
    protected void prepare()
    {
        super.prepare();
        this.localAttributes.prepare();
    }

    @Override
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .flag(          "hidden",                   false, this.isHidden())

                .childStart("from")
                .single(        "cardinality",              this.fromCardinality.name().toLowerCase())
                .singleIfTrue(  "type",                     "all",                              this.fromTypeAll)
                .listIfTrue(    "type",                     this.fromTypes,                     !this.fromTypeAll)
                .singleIfTrue(  "relationship",             "all",                              this.fromRelationAll)
                .listIfTrue(    "relationship",             this.fromRelations,                 !this.fromRelationAll)
                .childEnd()

                .childStart("to")
                .singleIfTrue(  "type",                     "all",                              this.toTypeAll)
                .listIfTrue(    "type",                     this.toTypes,                       !this.toTypeAll)
                .singleIfTrue(  "relationship",             "all",                              this.toRelationAll)
                .listIfTrue(    "relationship",             this.toRelations,                   !this.toRelationAll)
                .childEnd()

                .list(          "attribute",                this.globalAttributes)
                .write(this.localAttributes)
                .properties(this.getProperties());
    }

    @Override
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
            final MultiLineMqlBuilder _mql, final PathType_mxJPO _current)
            throws UpdateException_mxJPO
    {
        // TODO Auto-generated method stub

    }

    /**
     * Cardinality.
     */
    public enum Cardinality
    {
        /** Cardinality one. */
        One,
        /** Cardinality many. */
        Many;
    }
}
