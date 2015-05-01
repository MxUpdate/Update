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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * Data model type class.
 * The handled properties are:
 * <ul>
 * <li>description</li>
 * <li>{@link #kind}</li>
 * <li>hidden flag</li>
 * <li>{@link #abstractFlag information about is the type abstract}</li>
 * <li>{@link #derived from information from which type this type is
 *     derived}</li>
 * <li>{@link #attributes}</li>
 * <li>{@link #methods type methods}</li>
 *
 * @author The MxUpdate Team
 */
public class Type_mxJPO
    extends AbstractDMWithTriggers_mxJPO<Type_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for types. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Type_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
        Type_mxJPO.IGNORED_URLS.add("/derivedFrom");
        Type_mxJPO.IGNORED_URLS.add("/derivedFrom/typeRefList");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/creationInfo");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/creationInfo/datetime");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/modificationInfo");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/modificationInfo/datetime");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/agent");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/datetime");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/event");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/order");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/historyList/history/string");
        Type_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList/attributeDef/adminProperties/propertyList");
        Type_mxJPO.IGNORED_URLS.add("/methodList");
    }

    /** Kind of type. */
    private Kind kind = Kind.Basic;

    /** Is the type abstract? */
    private boolean abstractFlag = false;
    /** From which type is this type derived? */
    private String derived;

    /** Defines all methods of this type. */
    private final SortedSet<String> methods = new TreeSet<String>();

    /** Attribute list. */
    private final SortedSet<String> attributes = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the type object
     */
    public Type_mxJPO(final TypeDef_mxJPO _typeDef,
                      final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final String _code)
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
    @Override()
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
        } else if (_url.startsWith("/attributeDefRefList/attributeDefRef"))  {
            this.attributes.add(_content);
            parsed = true;
        } else if ("/derivedFrom/typeRefList/typeRef".equals(_url))  {
            this.derived = _content;
            parsed = true;
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

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .singleIfTrue(  "kind",                     this.kind.name().toLowerCase(),     (this.kind == Kind.Composed))
                .flagIfTrue(    "abstract",          false, this.abstractFlag,                  this.abstractFlag)
                .stringIfTrue(  "derived",                  this.derived,                       (this.derived != null) && !this.derived.isEmpty())
                .flag(          "hidden",                   false, this.isHidden())
                .write(this.getTriggers())
                .list(          "method",                   this.methods)
                .list(          "attribute",                this.attributes)
                .properties(this.getProperties());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Type_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this.getTypeDef(), this.getName(), this.getSymbolicNames(), _current.getSymbolicNames());
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcValFlgDelta( _mql,              "abstract",          false, this.abstractFlag,      _current.abstractFlag);
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "hidden",            false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcListDelta(   _mql,              "method",                   this.methods,           _current.methods);
        DeltaUtil_mxJPO.calcListDelta(_paramCache, _mql,    "attribute",
                ErrorKey.DM_TYPE_REMOVE_ATTRIBUTE, this.getName(),
                ValueKeys.DMTypeAttrIgnore, ValueKeys.DMTypeAttrRemove,                 this.attributes,        _current.attributes);

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
