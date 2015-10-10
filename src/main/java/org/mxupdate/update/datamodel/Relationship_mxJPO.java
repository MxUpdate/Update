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

import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.datamodel.helper.LocalAttributeList_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

import matrix.util.MatrixException;

/**
 * Data model relationship class used to export and update relationships.
 * The handles properties are
 * <ul>
 * <li>description</li>
 * <li>{@link #kind}</li>
 * <li>{@link #abstractFlag abstract flag}</li>
 * <li>{@link #derived}</li>
 * <li>hidden flag</li>
 * <li>{@link #preventDuplicates prevent duplicates} flag</li>
 * <li>{@link #from} and {@link #to} side informations</li>
 * <li>{@link #attributes global attributes}</li>
 * <li>{@link #localAttributes local attributes}</li>
 * <li>properties</li>
 * </ul>
 *
 * @author The MxUpdate Team
 */
public class Relationship_mxJPO
    extends AbstractDMWithTriggers_mxJPO<Relationship_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for relationships. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        Relationship_mxJPO.IGNORED_URLS.add("/attributeDefRefList");
        Relationship_mxJPO.IGNORED_URLS.add("/derivedFromRelationship");
        Relationship_mxJPO.IGNORED_URLS.add("/derivedFromRelationship/relationshipDefRefList");
        Relationship_mxJPO.IGNORED_URLS.add("/localAttributes");
        Relationship_mxJPO.IGNORED_URLS.add("/localAttributes/attributeDefList");
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide");
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/allowAllRelationships");                     // to be ignored, because read within parse method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/allowAllTypes");                             // to be ignored, because read within parse method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/relationshipDefRefList");
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/relationshipDefRefList/relationshipDefRef"); // to be ignored, because read within parse method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/typeRefList");
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/typeRefList/typeRef");                       // to be ignored, because read within parse method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide");
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/allowAllRelationships");                       // to be ignored, because read within parse method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/allowAllTypes");                               // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/relationshipDefRefList");
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/relationshipDefRefList/relationshipDefRef");   // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/typeRefList");
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/typeRefList/typeRef");                         // to be ignored, because read within prepare method
    }

    /** Kind of relationship. */
    private Kind kind = Kind.Basic;

    /** Relationship is abstract. */
    private boolean abstractFlag = false;
    /** Relationship is derived from this relationship. */
    private String derived;

    /** Set holding rule referencing this relationship. */
    private String rule;
    /** Prevent duplicates for this relationship. */
    private boolean preventDuplicates = false;
    /** From side information. */
    private final Side from = new Side("from");
    /** To side information. */
    private final Side to = new Side("to");

    /** Global attributes. */
    private final SortedSet<String> attributes = new TreeSet<>();
    /** Local attributes. */
    private final LocalAttributeList_mxJPO localAttributes = new LocalAttributeList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public Relationship_mxJPO(final TypeDef_mxJPO _typeDef,
                              final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    @Override()
    public void parseUpdate(final File _file,
                            final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new RelationshipParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    /**
     * Evaluates the from and to type information. Because MX does not handle
     * the to or from side types correctly within XML exports, the from and to
     * side types must be evaluated via a &quot;<code>print relationship ...
     * select fromtype / totype</code>&quot; MQL statement. If this is not done
     * in this way, there is no other possibility to evaluate the information
     * {@link Side#typeAll} or {@link Side#relationAll}.
     */
    @Override()
    public void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, ParseException
    {
        // to ensure that the value is the same default as in the DB
        this.from.propagateConnection = false;
        this.to.propagateConnection = false;

        super.parse(_paramCache);

        this.from.eval(_paramCache);
        this.to.eval(_paramCache);
    }

    /**
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
        if (Relationship_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/abstract".equals(_url))  {
            this.abstractFlag = true;
            parsed = true;
        } else if ("/accessRuleRef".equals(_url))  {
            this.rule = _content;
            parsed = true;
        } else if (_url.startsWith("/attributeDefRefList/attributeDefRef"))  {
            this.attributes.add(_content);
            parsed = true;
        } else if (_url.startsWith("/derivedFromRelationship/relationshipDefRefList/relationshipDefRef"))  {
            this.derived = _content;
            parsed = true;

        } else if ("/fromSide/cardinality".equals(_url))  {
            this.from.cardinality = _content.equalsIgnoreCase("1") ? "one" : _content.equalsIgnoreCase("N") ? "many" : _content;
            parsed = true;
        } else if ("/fromSide/cloneAction".equals(_url))  {
            this.from.cloneAction = _content;
            parsed = true;
        } else if ("/fromSide/meaning".equals(_url))  {
            this.from.meaning = _content;
            parsed = true;
        } else if ("/fromSide/propagateModify".equals(_url))  {
            this.from.propagateModify = true;
            parsed = true;
        } else if ("/fromSide/revisionAction".equals(_url))  {
            this.from.revisionAction = _content;
            parsed = true;
        } else if ("/fromSide/propagateConnection".equals(_url))  {
            this.from.propagateConnection = true;
            parsed = true;

        } else if (_url.startsWith("/localAttributes/attributeDefList/attributeDef"))  {
            parsed = this.localAttributes.parseAdminXMLExportEvent(_paramCache, _url.substring(46), _content);

        } else if ("/preventDuplicates".equals(_url))  {
            this.preventDuplicates = true;
            parsed = true;
        } else if ("/relationshipKind".equals(_url))  {
            if ("0".equals(_content))  {
                this.kind = Kind.Basic;
                parsed = true;
            } else if ("1".equals(_content))  {
                this.kind = Kind.Compositional;
                parsed = true;
            } else  {
                parsed = false;
            }

        } else if ("/toSide/cardinality".equals(_url))  {
            this.to.cardinality = _content.equalsIgnoreCase("1") ? "one" : _content.equalsIgnoreCase("N") ? "many" : _content;
            parsed = true;
        } else if ("/toSide/cloneAction".equals(_url))  {
            this.to.cloneAction = _content;
            parsed = true;
        } else if ("/toSide/meaning".equals(_url))  {
            this.to.meaning = _content;
            parsed = true;
        } else if ("/toSide/propagateModify".equals(_url))  {
            this.to.propagateModify = true;
            parsed = true;
        } else if ("/toSide/revisionAction".equals(_url))  {
            this.to.revisionAction = _content;
            parsed = true;
        } else if ("/toSide/propagateConnection".equals(_url))  {
            this.to.propagateConnection = true;
            parsed = true;

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * After the relationship is parsed, the relationship and local attributes
     * must be prepared.
     */
    @Override()
    protected void prepare()
    {
        super.prepare();
        this.localAttributes.prepare();
    }

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .list(          "symbolicname",             this.getSymbolicNames())
                .string(        "description",              this.getDescription())
                .singleIfTrue(  "kind",                     this.kind.name().toLowerCase(),     (this.kind != Kind.Basic))
                .flagIfTrue(    "abstract",          false, this.abstractFlag,                  this.abstractFlag)
                .stringIfTrue(  "derived",                  this.derived,                       (this.derived != null) && !this.derived.isEmpty())
                .flag(          "hidden",                   false, this.isHidden())
                .flag(          "preventduplicates", false, this.preventDuplicates)
                .stringIfTrue(  "rule",                     this.rule,                          (this.rule != null) && !this.rule.isEmpty())
                .write(this.getTriggers())
                .write(this.from)
                .write(this.to)
                .list(          "attribute",                this.attributes)
                .write(this.localAttributes)
                .properties(this.getProperties());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Relationship_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcSymbNames(_paramCache, _mql, this.getTypeDef(), this.getName(), this.getSymbolicNames(), _current.getSymbolicNames());
        DeltaUtil_mxJPO.calcValueDelta(  _mql,              "description",              this.getDescription(),  _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "hidden",            false, this.isHidden(),        _current.isHidden());
        DeltaUtil_mxJPO.calcFlagDelta(   _mql,              "preventduplicates", false, this.preventDuplicates, _current.preventDuplicates);
        DeltaUtil_mxJPO.calcValFlgDelta( _mql,              "abstract",          false, this.abstractFlag,      _current.abstractFlag);

        DeltaUtil_mxJPO.calcListDelta(_paramCache, _mql,    "attribute",
                ErrorKey.DM_RELATION_REMOVE_GLOBAL_ATTRIBUTE, this.getName(),
                ValueKeys.DMRelationAttrIgnore, ValueKeys.DMRelationAttrRemove,         this.attributes,        _current.attributes);
        this.localAttributes.calcDelta(_paramCache, _mql, EMxAdmin_mxJPO.Relationship, this.getName(), ErrorKey.DM_RELATION_REMOVE_LOCAL_ATTRIBUTE, _current.localAttributes);

        // only one rule can exists maximum, but they must be technically handled like as list
        final SortedSet<String> thisRules = new TreeSet<>();
        if ((this.rule != null) && !this.rule.isEmpty())  {
            thisRules.add(this.rule);
        }
        final SortedSet<String> currentRules = new TreeSet<>();
        if ((_current.rule != null) && !_current.rule.isEmpty())  {
            currentRules.add(_current.rule);
        }
        DeltaUtil_mxJPO.calcListDelta( _mql, "rule",                     thisRules,              currentRules);

        this.from           .calcDelta(_mql,     _current.from);
        this.to             .calcDelta(_mql,     _current.to);
        this.getTriggers()  .calcDelta(_mql,     _current.getTriggers());
        this.getProperties().calcDelta(_mql, "", _current.getProperties());

        // derived information
        final String thisDerived = (this.derived == null) ? "" : this.derived;
        final String currDerived = (_current.derived == null) ? "" : _current.derived;
        if (!thisDerived.equals(currDerived))  {
            if (!currDerived.isEmpty())  {
                throw new UpdateException_mxJPO(
                        ErrorKey.DM_RELATION_UPDATE_DERIVED,
                        this.getName(),
                        currDerived,
                        this.derived);
            }
            _mql.newLine().cmd("derived ").arg(thisDerived);
        }

        // kind at least to ensure all properties are set
        if (this.kind != _current.kind)  {
            if (_current.kind != Kind.Basic)  {
                throw new UpdateException_mxJPO(
                        ErrorKey.DM_RELATION_NOT_BASIC_KIND,
                        this.getName(),
                        _current.kind,
                        this.kind);
            }
            _mql.newLine().cmd(this.kind.name().toLowerCase());
        }
    }

    /**
     * Kind of relationship.
     */
    public enum Kind
    {
        /** Standard relationship. */
        Basic,
        /** Compositional relationship. */
        Compositional;
    }

    /**
     * Stores the information for one side of a relationship.
     */
    private final class Side
        implements UpdateList
    {
        /** Side string of the relationship. */
        private final String side;
        /** Side cardinality action. */
        private String cardinality = "many";
        /** Side clone action. */
        private String cloneAction = "none";
        /** Side revision action. */
        private String revisionAction = "none";
        /** Side meaning. */
        private String meaning = "";
        /** Side propagate connection flag. */
        private boolean propagateConnection = true;
        /** Side propagate modify flag. */
        private boolean propagateModify = false;

        /** Side type list. */
        private final SortedSet<String> types = new TreeSet<>();
        /** Are all types on the side allowed? */
        private boolean typeAll = false;

        /** From side relationship list. */
        private final SortedSet<String> relations = new TreeSet<>();
        /** Are all relationships on the from side allowed? */
        private boolean relationAll = false;

        /**
         * Defines the {@link #side} information.
         *
         * @param _side     side string
         */
        private Side(final String _side)
        {
            this.side = _side;
        }

        /**
         * Reads the information about the type / relationship for this side.
         *
         * @param _paramCache       parameter cache with the MX context
         * @throws MatrixException if information could not be read
         */
        private void eval(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            // evaluate all to types
            final String[] toTypesArr = MqlBuilder_mxJPO.mql()
                            .cmd("escape print rel ").arg(Relationship_mxJPO.this.getName())
                            .cmd(" select ").arg(this.side + "type")
                            .cmd(" dump ").arg("\n")
                            .exec(_paramCache)
                            .split("\n");
            for (final String toType : toTypesArr)  {
                if ("all".equals(toType))  {
                    this.typeAll = true;
                    this.types.clear();
                    break;
                } else if (!"".equals(toType)) {
                    this.types.add(toType);
                }
            }

            // are connections between relationships allowed?
            if (_paramCache.getValueBoolean(ValueKeys.DMRelationSupportRelCons))  {
                // evaluate all from relationships
                final String[] fromRelsArr = MqlBuilder_mxJPO.mql()
                                .cmd("escape print rel ").arg(Relationship_mxJPO.this.getName())
                                .cmd(" select ").arg(this.side + "rel")
                                .cmd(" dump ").arg("\n")
                                .exec(_paramCache)
                                .split("\n");
                for (final String fromRel : fromRelsArr)  {
                    if ("all".equals(fromRel))  {
                        this.relationAll = true;
                        this.relations.clear();
                    } else if (!"".equals(fromRel)) {
                        this.relations.add(fromRel);
                    }
                }
            }
        }

        /**
         * Writes all relationship specific information in the TCL update file.
         * The relationship specific information are:
         * <ul>
         * <li>{@link #propagateModify propagate modify} flag</li>
         * <li>{@link #propagateConnection propagate connection} flag</li>
         * <li>{@link #meaning}</li>
         * <li>{@link #revisionAction revision behavior / action}</li>
         * <li>{@link #cloneAction clone behavior / action}</li>
         * </ul>
         *
         * @param _updateBuilder    update builder
         * @see Relationship_mxJPO#writeObject(ParameterCache_mxJPO, Appendable)
         */
        @Override()
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            _updateBuilder
                    .childStart(this.side);

            if ((Relationship_mxJPO.this.derived == null) || Relationship_mxJPO.this.derived.isEmpty())  {
                _updateBuilder
                        .string(        "meaning",                      this.meaning)
                        .single(        "cardinality",                  this.cardinality)
                        .single(        "revision",                     this.revisionAction)
                        .single(        "clone",                        this.cloneAction)
                        .flag(          "propagatemodify",      false,  this.propagateModify)
                        .flag(          "propagateconnection",  false,  this.propagateConnection);
            }

            _updateBuilder
                    .singleIfTrue(  "type",                         "all",                      this.typeAll)
                    .listIfTrue(    "type",                         this.types,                 !this.typeAll)
                    .singleIfTrue(  "relationship",                 "all",                      this.relationAll)
                    .listIfTrue(    "relationship",                 this.relations,             !this.relationAll)
                    .childEnd();
        }

        /**
         * Calculates the delta between given {@code _current} relationship side
         * definition and this target relationship side definition and appends
         * the MQL append commands to {@code _mql}.
         *
         * @param _mql          builder to append the MQL commands
         * @param _current      current relationship side definition
         */
        protected void calcDelta(final MultiLineMqlBuilder _mql,
                                 final Side _current)
        {
            _mql.pushPrefixByAppending(this.side);

            if ((Relationship_mxJPO.this.derived == null) || Relationship_mxJPO.this.derived.isEmpty())  {
                DeltaUtil_mxJPO.calcValueDelta(_mql, "meaning",                    this.meaning,                        _current.meaning);
                DeltaUtil_mxJPO.calcValueDelta(_mql, "cardinality",                this.cardinality,                    _current.cardinality);
                DeltaUtil_mxJPO.calcValueDelta(_mql, "revision",                   this.revisionAction,                 _current.revisionAction);
                DeltaUtil_mxJPO.calcValueDelta(_mql, "clone",                      this.cloneAction,                    _current.cloneAction);
                DeltaUtil_mxJPO.calcFlagDelta( _mql, "propagatemodify",     false, this.propagateModify,                _current.propagateModify);
                DeltaUtil_mxJPO.calcFlagDelta( _mql, "propagateconnection", false, this.propagateConnection,            _current.propagateConnection);
            }
            DeltaUtil_mxJPO.calcListDelta(_mql,  "type",                       this.typeAll, this.types,            _current.typeAll, _current.types);
            DeltaUtil_mxJPO.calcListDelta(_mql,  "relationship",               this.relationAll, this.relations,    _current.relationAll, _current.relations);

            _mql.popPrefix();
        }
    }
}
