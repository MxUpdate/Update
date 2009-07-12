/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Data model relationship class used to export and update relationships.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Relationship_mxJPO
        extends AbstractDMWithAttributes_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -5246287940374394548L;

    /**
     * Name of the parameter to define that connections between relationships
     * are from current MX version supported. The parameter is needed to
     * support the case that an old MX version is used....
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String PARAM_SUPPORT_REL_CONS = "DMRelationSupportRelCons";

    /**
     * Set holding all rules referencing this attribute.
     *
     * @see #parse(String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final  Set<String> rules = new TreeSet<String>();

    /**
     * Prevent duplicates for this relationship.
     */
    private boolean preventDuplicates = false;

    /**
     * From side cardinality action.
     */
    private String fromCardinality = "";

    /**
     * From side clone action.
     */
    private String fromCloneAction = "";

    /**
     * From side meaning.
     */
    private String fromMeaning = "";

    /**
     * From side propagate connection flag.
     */
    private boolean fromPropagateConnection = false;

    /**
     * From side propagate modify flag.
     */
    private boolean fromPropagateModify = false;

    /**
     * From side revision action.
     */
    private String fromRevisionAction = "";

    /**
     * From side type list.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #fromTypeAll
     */
    private final Set<String> fromTypes = new TreeSet<String>();

    /**
     * Are all types on the from side allowed?
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #fromTypes
     */
    private boolean fromTypeAll = false;

    /**
     * From side relationship list.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #fromRelationAll
     */
    private final Set<String> fromRelations = new TreeSet<String>();

    /**
     * Are all relationships on the from side allowed?
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #fromRelations
     */
    private boolean fromRelationAll = false;

    /**
     * To side cardinality action.
     */
    private String toCardinality = "";

    /**
     * To side clone action.
     */
    private String toCloneAction = "";

    /**
     * To side meaning.
     */
    private String toMeaning = "";

    /**
     * To side propagate connection flag.
     */
    private boolean toPropagateConnection = false;

    /**
     * To side propagate modify flag.
     */
    private boolean toPropagateModify = false;

    /**
     * To side revision action.
     */
    private String toRevisionAction = "";

    /**
     * To side type list.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #toTypeAll
     */
    private final Set<String> toTypes = new TreeSet<String>();

    /**
     * Are all types on the to side allowed?
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #toTypes
     */
    private boolean toTypeAll = false;

    /**
     * To side relationship list.
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #fromRelationAll
     */
    private final Set<String> toRelations = new TreeSet<String>();

    /**
     * Are all relationships on the to side allowed?
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #toRelations
     */
    private boolean toRelationAll = false;

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

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/accessRuleRef".equals(_url))  {
            this.rules.add(_content);

        } else if ("/fromSide".equals(_url))  {
            // to be ignored ...
        } else if ("/fromSide/allowAllRelationships".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/fromSide/allowAllTypes".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/fromSide/cardinality".equals(_url))  {
            this.fromCardinality = _content.equalsIgnoreCase("1")
                                   ? "One"
                                   : _content.toUpperCase();
        } else if ("/fromSide/cloneAction".equals(_url))  {
            this.fromCloneAction = _content;
        } else if ("/fromSide/meaning".equals(_url))  {
            this.fromMeaning = _content;
        } else if ("/fromSide/propagateModify".equals(_url))  {
            this.fromPropagateModify = true;
        } else if ("/fromSide/relationshipDefRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/fromSide/relationshipDefRefList/relationshipDefRef".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/fromSide/revisionAction".equals(_url))  {
            this.fromRevisionAction = _content;
        } else if ("/fromSide/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/fromSide/typeRefList/typeRef".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/fromSide/propagateConnection".equals(_url))  {
            this.fromPropagateConnection = true;

        } else if ("/preventDuplicates".equals(_url))  {
            this.preventDuplicates = true;

        } else if ("/toSide".equals(_url))  {
            // to be ignored ...
        } else if ("/toSide/allowAllRelationships".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/toSide/allowAllTypes".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/toSide/cardinality".equals(_url))  {
            this.toCardinality = _content.equalsIgnoreCase("1")
                                 ? "One"
                                 : _content.toUpperCase();
        } else if ("/toSide/cloneAction".equals(_url))  {
            this.toCloneAction = _content;
        } else if ("/toSide/meaning".equals(_url))  {
            this.toMeaning = _content;
        } else if ("/toSide/propagateModify".equals(_url))  {
            this.toPropagateModify = true;
        } else if ("/toSide/relationshipDefRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/toSide/relationshipDefRefList/relationshipDefRef".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/toSide/revisionAction".equals(_url))  {
            this.toRevisionAction = _content;
        } else if ("/toSide/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/toSide/typeRefList/typeRef".equals(_url))  {
            // to be ignored, because read within prepare method
        } else if ("/toSide/propagateConnection".equals(_url))  {
            this.toPropagateConnection = true;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Evaluates the from and to type information. Because MX does not handle
     * the to or from side types correctly within XML exports, the from and to
     * side types must be evaluated via a &quot;<code>print relationship ...
     * select fromtype / totype</code>&quot; MQL statement. If this is not done
     * in this way, there is no other possibility to evaluate the information
     * {@link #fromTypeAll} or {@link #toTypeAll}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the from / to side types could not be
     *                         evaluated or the prepare from the derived class
     *                         failed
     * @todo support for from / to relationships
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        // evaluate all from types
        final String[] fromTypesArr = MqlUtil_mxJPO.execMql(_paramCache,
                                              new StringBuilder("escape print rel \"")
                                                   .append(this.getName())
                                                   .append("\" select fromtype dump '\n'"))
                                     .split("\n");
        for (final String fromType : fromTypesArr)  {
            if ("all".equals(fromType))  {
                this.fromTypeAll = true;
                this.fromTypes.clear();
            } else if (!"".equals(fromType)) {
                this.fromTypes.add(fromType);
            }
        }
        // evaluate all to types
        final String[] toTypesArr = MqlUtil_mxJPO.execMql(_paramCache,
                                            new StringBuilder("escape print rel \"")
                                                 .append(this.getName())
                                                 .append("\" select totype dump '\n'"))
                                    .split("\n");
        for (final String toType : toTypesArr)  {
            if ("all".equals(toType))  {
                this.toTypeAll = true;
                this.toTypes.clear();
            } else if (!"".equals(toType)) {
                this.toTypes.add(toType);
            }
        }

        // are connections between relationships allowed?
        if (_paramCache.getValueBoolean(Relationship_mxJPO.PARAM_SUPPORT_REL_CONS))  {
            // evaluate all from relationships
            final String[] fromRelsArr = MqlUtil_mxJPO.execMql(_paramCache,
                                                 new StringBuilder("escape print rel \"")
                                                         .append(this.getName())
                                                         .append("\" select fromrel dump '\n'"))
                                         .split("\n");
            for (final String fromRel : fromRelsArr)  {
                if ("all".equals(fromRel))  {
                    this.fromRelationAll = true;
                    this.fromRelations.clear();
                } else if (!"".equals(fromRel)) {
                    this.fromRelations.add(fromRel);
                }
            }
            // evaluate all to relationships
            final String[] toRelsArr = MqlUtil_mxJPO.execMql(_paramCache,
                                                 new StringBuilder("escape print rel \"")
                                                         .append(this.getName())
                                                         .append("\" select torel dump '\n'"))
                                         .split("\n");
            for (final String toRel : toRelsArr)  {
                if ("all".equals(toRel))  {
                    this.toRelationAll = true;
                    this.toRelations.clear();
                } else if (!"".equals(toRel)) {
                    this.toRelations.add(toRel);
                }
            }
        }

        super.prepare(_paramCache);
    }

    /**
     * Writes all relationship specific information in the TCL update file.
     * The relationship specific information are:
     * <ul>
     * <li>hidden flag</li>
     * <li>prevent dupplicates flag (see {@link #preventDuplicates})</li>
     * <li>from and to side informations</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL code to the TCL update file could not be
     *                     written
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(this.isHidden() ? "" : "!").append("hidden")
            .append(" \\\n    ").append(this.preventDuplicates ? "" : "!").append("preventduplicates");
        // rules
        for (final String rule : this.rules)  {
            _out.append(" \\\n    add rule \"").append(StringUtil_mxJPO.convertTcl(rule)).append('\"');
        }
        // triggers
        this.writeTriggers(_out);
        _out.append(" \\\n    from")
            .append(" \\\n        ").append(this.fromPropagateModify ? "" : "!").append("propagatemodify")
            .append(" \\\n        ").append(this.fromPropagateConnection ? "" : "!").append("propagateconnection")
            .append(" \\\n        meaning \"").append(StringUtil_mxJPO.convertTcl(this.fromMeaning)).append('\"')
            .append(" \\\n        cardinality \"")
                    .append(StringUtil_mxJPO.convertTcl(this.fromCardinality)).append('\"')
            .append(" \\\n        revision \"")
                    .append(StringUtil_mxJPO.convertTcl(this.fromRevisionAction)).append('\"')
            .append(" \\\n        clone \"").append(StringUtil_mxJPO.convertTcl(this.fromCloneAction)).append('\"');
        // from types
        if (this.fromTypeAll)  {
            _out.append(" \\\n        add type \"all\"");
        } else  {
            for (final String type : this.fromTypes)  {
                _out.append(" \\\n        add type \"").append(StringUtil_mxJPO.convertTcl(type)).append('\"');
            }
        }
        // from relationships
        if (this.fromRelationAll)  {
            _out.append(" \\\n        add relationship \"all\"");
        } else  {
            for (final String relation : this.fromRelations)  {
                _out.append(" \\\n        add relationship \"")
                    .append(StringUtil_mxJPO.convertTcl(relation)).append('\"');
            }
        }
        _out.append(" \\\n    to")
            .append(" \\\n        ").append(this.toPropagateModify ? "" : "!").append("propagatemodify")
            .append(" \\\n        ").append(this.toPropagateConnection ? "" : "!").append("propagateconnection")
            .append(" \\\n        meaning \"").append(StringUtil_mxJPO.convertTcl(this.toMeaning)).append('\"')
            .append(" \\\n        cardinality \"").append(StringUtil_mxJPO.convertTcl(this.toCardinality)).append('\"')
            .append(" \\\n        revision \"").append(StringUtil_mxJPO.convertTcl(this.toRevisionAction)).append('\"')
            .append(" \\\n        clone \"").append(StringUtil_mxJPO.convertTcl(this.toCloneAction)).append('\"');
        // to types
        if (this.toTypeAll)  {
            _out.append(" \\\n        add type \"all\"");
        } else  {
            for (final String type : this.toTypes)  {
                _out.append(" \\\n        add type \"").append(StringUtil_mxJPO.convertTcl(type)).append('\"');
            }
        }
        // to relationships
        if (this.toRelationAll)  {
            _out.append(" \\\n        add relationship \"all\"");
        } else  {
            for (final String relation : this.toRelations)  {
                _out.append(" \\\n        add relationship \"")
                    .append(StringUtil_mxJPO.convertTcl(relation)).append('\"');
            }
        }
    }


    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this relationship. This
     * includes:
     * <ul>
     * <li>reset description</li>
     * <li>remove hidden and prevent duplicate flag</li>
     * <li>remove all rules</li>
     * <li>reset from and to information</li>
     * <li>remove all from and to types</li>
     * </ul>
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder()
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append('\"')
                // remove hidden, description, prevent duplicate
                .append(" !hidden description \"\" !preventduplicate")
                // reset from information
                .append(" from !propagatemodify !propagateconnection ")
                        .append("meaning \"\" cardinality one revision none clone none ")
                .append(" from remove type all")
                // reset to information
                .append(" to !propagatemodify !propagateconnection ")
                        .append("meaning \"\" cardinality one revision none clone none ")
                .append(" to remove type all");
        // are connections between relationships allowed?
        // => yes, than all relationships must be removed from from / to side
        if (_paramCache.getValueBoolean(Relationship_mxJPO.PARAM_SUPPORT_REL_CONS))  {
            preMQLCode.append(" from remove relationship all")
                      .append(" to remove relationship all");
        }
        // remove all rules
        for (final String rule : this.rules)  {
            preMQLCode.append(" remove rule \"").append(StringUtil_mxJPO.convertMql(rule)).append('\"');
        }
        // remove all from types
        for (final String type : this.fromTypes)  {
            preMQLCode.append(" from remove type \"").append(StringUtil_mxJPO.convertMql(type)).append('\"');
        }
        // remove all from relationships
        for (final String rel : this.fromRelations)  {
            preMQLCode.append(" from remove relationship \"").append(StringUtil_mxJPO.convertMql(rel)).append('\"');
        }
        // remove all to types
        for (final String type : this.toTypes)  {
            preMQLCode.append(" to remove type \"").append(StringUtil_mxJPO.convertMql(type)).append('\"');
        }
        // remove all to relationships
        for (final String rel : this.toRelations)  {
            preMQLCode.append(" to remove relationship \"").append(StringUtil_mxJPO.convertMql(rel)).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
