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
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalPathTypeList_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Data model type class.
 * The handled properties are:
 * <ul>
 * <li>uuid</li>
 * <li>symbolic names</li>
 * <li>description</li>
 * <li>{@link #kind}</li>
 * <li>hidden flag</li>
 * <li>{@link #abstractFlag information about is the type abstract}</li>
 * <li>{@link #derived from information from which type this type is
 *     derived}</li>
 * <li>{@link #globalAttributes global attributes}</li>
 * <li>{@link #localAttributes local attributes}</li>
 * <li>{@link #localPathTypes local path types}</li>
 * <li>{@link #methods type methods}</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Type_mxJPO
    extends AbstractDMWithTriggers_mxJPO<Type_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for types. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        Type_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
        Type_mxJPO.IGNORED_URLS.add("/derivedFrom");
        Type_mxJPO.IGNORED_URLS.add("/derivedFrom/typeRefList");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList");
        Type_mxJPO.IGNORED_URLS.add("/localPathTypes");
        Type_mxJPO.IGNORED_URLS.add("/localPathTypes/pathDefList");
        Type_mxJPO.IGNORED_URLS.add("/methodList");
    }

    /** Kind of type. */
    private Kind kind = Kind.Basic;

    /** Is the type abstract? */
    private boolean abstractFlag = false;
    /** From which type is this type derived? */
    private String derived;

    /** Defines all methods of this type. */
    private final SortedSet<String> methods = new TreeSet<>();

    /** Global attributes. */
    private final SortedSet<String> globalAttributes = new TreeSet<>();
    /** Local attributes. */
    private final LocalAttributeList_mxJPO localAttributes = new LocalAttributeList_mxJPO();

    /** Local path types. */
    private final LocalPathTypeList_mxJPO localPathTypes = new LocalPathTypeList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _mxName   MX name of the type object
     */
    public Type_mxJPO(final String _mxName)
    {
        super(EMxAdmin_mxJPO.Type, _mxName);
    }

    @Override
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new TypeParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * <p>Parses all type specific values. This includes:
     * <ul>
     * <li>{@link #abstractFlag information about is the type abstract}</li>
     * <li>{@link #derived from information from which type this type is
     *     derived}</li>
     * <li>{@link #methods type methods}</li>
     * </ul></p>
     * <p>If an <code>_url</code> is included in {@link #IGNORED_URLS}, this
     * URL is ignored.</p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (Type_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/abstract".equals(_url))  {
            this.abstractFlag = true;
            parsed = true;
        } else if ("/derivedFrom/typeRefList/typeRef".equals(_url))  {
            this.derived = _content;
            parsed = true;

        } else if (_url.startsWith("/attributeDefRefList/attributeDefRef"))  {
            this.globalAttributes.add(_content);
            parsed = true;
        } else if (_url.startsWith("/localAttributes/attributeDefList/attributeDef"))  {
            parsed = this.localAttributes.parseAdminXMLExportEvent(_paramCache, _url.substring(46), _content);

        } else if (_url.startsWith("/localPathTypes/pathDefList/pathDef"))  {
            parsed = this.localPathTypes.parseAdminXMLExportEvent(_paramCache, _url.substring(35), _content);

        } else if ("/methodList/programRef".equals(_url))  {
            this.methods.add(_content);
            parsed = true;
        } else if ("/typeKind".equals(_url))  {
            if ("0".equals(_content))  {
                this.kind = Kind.Basic;
                parsed = true;
            } else if ("1".equals(_content))  {
                this.kind = Kind.Composed;
                parsed = true;
            } else  {
                parsed = false;
            }
        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * After the type is parsed, the type and local attributes must be prepared.
     */
    @Override
    protected void prepare()
    {
        super.prepare();
        this.localAttributes.prepare();
        this.localPathTypes.prepare();
    }

    @Override
    public void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .stringNotNull( "uuid",                     this.getProperties().getValue4KeyValue(_updateBuilder.getParamCache(), PropertyDef_mxJPO.UUID))
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .singleIfTrue(  "kind",                     this.kind.name().toLowerCase(),     (this.kind == Kind.Composed))
                .flagIfTrue(    "abstract",          false, this.abstractFlag,                  this.abstractFlag)
                .stringIfTrue(  "derived",                  this.derived,                       (this.derived != null) && !this.derived.isEmpty())
                .flag(          "hidden",                   false, this.isHidden())
                .write(this.getTriggers())
                .list(          "method",                   this.methods)
                .list(          "attribute",                this.globalAttributes)
                .write(this.localAttributes)
                .write(this.localPathTypes)
                .properties(this.getProperties());
    }

    @Override
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final Type_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this, _current);
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcValFlgDelta( _mql,              "abstract",          false, this.abstractFlag,      _current.abstractFlag);
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "hidden",            false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcListDelta(   _mql,              "method",                   this.methods,           _current.methods);

        DeltaUtil_mxJPO.calcListDelta(_paramCache, _mql,    "attribute",
                ErrorKey.DM_TYPE_REMOVE_GLOBAL_ATTRIBUTE, this.getName(),
                ValueKeys.DMTypeAttrIgnore, ValueKeys.DMTypeAttrRemove,                 this.globalAttributes,  _current.globalAttributes);
        this.localAttributes.calcDelta(_paramCache, _mql, this,  ErrorKey.DM_TYPE_REMOVE_LOCAL_ATTRIBUTE, _current.localAttributes);

        this.localPathTypes.calcDelta(_paramCache, _mql, this, ErrorKey.DM_TYPE_REMOVE_LOCAL_PATH_TYPE, _current.localPathTypes);

        this.getTriggers()  .calcDelta(_mql,     _current.getTriggers());
        this.getProperties().calcDelta(_mql, "", _current.getProperties());

        // derived information
        final String thisDerived = (this.derived == null) ? "" : this.derived;
        final String currDerived = (_current.derived == null) ? "" : _current.derived;
        if (!thisDerived.equals(currDerived))  {
            if (!currDerived.isEmpty())  {
                throw new UpdateException_mxJPO(
                        ErrorKey.DM_TYPE_UPDATE_DERIVED,
                        this.getName(),
                        _current.derived,
                        this.derived);
            }
            _mql.newLine().cmd("derived ").arg(thisDerived);
        }

        // kind at least to ensure all properties are set
        if (this.kind != _current.kind)  {
            if (_current.kind != Kind.Basic)  {
                throw new UpdateException_mxJPO(
                        ErrorKey.DM_TYPE_NOT_BASIC_KIND,
                        this.getName(),
                        _current.kind,
                        this.kind);
            }
            _mql.newLine().cmd(this.kind.name().toLowerCase());
        }
    }

    /**
     * Kind of type.
     */
    public enum Kind
    {
        /** Standard type. */
        Basic,
        /** Composed type. */
        Composed;
    }
}
