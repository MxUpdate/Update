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
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

import matrix.util.MatrixException;

/**
 * Data model type class.
 * The handled properties are:
 * <ul>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>description</li>
 * <li>hidden flag</li>
 * <li>{@link #globalAttributes global attributes}</li>
 * <li>{@link #localAttributes local attributes}</li>
 * <li>from and to side informations</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class PathType_mxJPO
    extends AbstractAdminObject_mxJPO<PathType_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for path types. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        PathType_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
        PathType_mxJPO.IGNORED_URLS.add("/fromPathSide");
        PathType_mxJPO.IGNORED_URLS.add("/fromPathSide/relationshipDefRefList");
        PathType_mxJPO.IGNORED_URLS.add("/fromPathSide/typeRefList");
        PathType_mxJPO.IGNORED_URLS.add("/localAttributes");
        PathType_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList");
        PathType_mxJPO.IGNORED_URLS.add("/toPathSide");
        PathType_mxJPO.IGNORED_URLS.add("/toPathSide/relationshipDefRefList");
        PathType_mxJPO.IGNORED_URLS.add("/toPathSide/typeRefList");
    }

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
    private final LocalAttributeList_mxJPO localAttributes = new LocalAttributeList_mxJPO(this);

    /**
     * Constructor used to initialize the path type definition.
     *
     * @param _mxName   MX name of the type object
     */
    public PathType_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.PathType, _mxName);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new PathTypeParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        boolean parsed;
        if (PathType_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;

        // from direction
        } else if ("/fromPathSide/allowAllRelationships".equals(_url))  {
            this.fromRelationAll = true;
            parsed = true;
        } else if ("/fromPathSide/allowAllTypes".equals(_url))  {
            this.fromTypeAll = true;
            parsed = true;
        } else if ("/fromPathSide/cardinality".equals(_url))  {
            if ("1".equals(_content))  {
                this.fromCardinality = Cardinality.One;
            } else  {
                this.fromCardinality = Cardinality.Many;
            }
            parsed = true;
        } else if ("/fromPathSide/relationshipDefRefList/relationshipDefRef".equals(_url))  {
            this.fromRelations.add(_content);
            parsed = true;
        } else if ("/fromPathSide/typeRefList/typeRef".equals(_url))  {
            this.fromTypes.add(_content);
            parsed = true;

        // to direction
        } else if ("/toPathSide/allowAllRelationships".equals(_url))  {
            this.toRelationAll = true;
            parsed = true;
        } else if ("/toPathSide/allowAllTypes".equals(_url))  {
            this.toTypeAll = true;
            parsed = true;
        } else if ("/toPathSide/relationshipDefRefList/relationshipDefRef".equals(_url))  {
            this.toRelations.add(_content);
            parsed = true;
        } else if ("/toPathSide/typeRefList/typeRef".equals(_url))  {
            this.toTypes.add(_content);
            parsed = true;

        // attributes
        } else if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.globalAttributes.add(_content);
            parsed = true;
        } else if (_url.startsWith("/localAttributes/attributeDefList/attributeDef"))  {
            parsed = this.localAttributes.parseAdminXMLExportEvent(_paramCache, _url.substring(46), _content);

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }

        return parsed;
    }

    @Override
    protected void parseSymbolicNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        super.parseSymbolicNames(_paramCache);
        this.localAttributes.parseSymbolicNames(_paramCache);
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
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
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
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final PathType_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  (_current != null) ? _current.getDescription() : null);

        // from
        _mql.pushPrefixByAppending("from");
        DeltaUtil_mxJPO.calcValueDelta(_mql, "cardinality",  this.fromCardinality.name().toLowerCase(), (_current != null) ? _current.fromCardinality.name().toLowerCase() : null);
        DeltaUtil_mxJPO.calcListDelta( _mql, "type",         this.fromTypeAll,     this.fromTypes,      (_current != null) ? _current.fromTypeAll : false,     (_current != null) ? _current.fromTypes : null);
        DeltaUtil_mxJPO.calcListDelta( _mql, "relationship", this.fromRelationAll, this.fromRelations,  (_current != null) ? _current.fromRelationAll : false, (_current != null) ? _current.fromRelations : null);
        _mql.popPrefix();

        // to
        _mql.pushPrefixByAppending("to");
        DeltaUtil_mxJPO.calcListDelta( _mql, "type",         this.toTypeAll,       this.toTypes,        (_current != null) ? _current.toTypeAll : false,       (_current != null) ? _current.toTypes : null);
        DeltaUtil_mxJPO.calcListDelta( _mql, "relationship", this.toRelationAll,   this.toRelations,    (_current != null) ? _current.toRelationAll : false,   (_current != null) ? _current.toRelations : null);
        _mql.popPrefix();

        DeltaUtil_mxJPO.calcListDelta(_paramCache, _mql,
                "attribute",
                ErrorKey.DM_PATHTYPE_REMOVE_GLOBAL_ATTRIBUTE, this.getName(),
                ValueKeys.DMPathTypeAttrIgnore, ValueKeys.DMPathTypeAttrRemove,
                this.globalAttributes, (_current != null) ? _current.globalAttributes : null);
        this.localAttributes.calcDelta(_paramCache, _mql,
                ErrorKey.DM_PATHTYPE_REMOVE_LOCAL_ATTRIBUTE,
                (_current != null) ? _current.localAttributes : null);

        this.getProperties().calcDelta(_mql, "", (_current != null) ? _current.getProperties() : null);
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
