/*
 * Copyright 2008-2011 The MxUpdate Team
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
import java.util.HashSet;
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
     * Name of the parameter to define that connections between relationships
     * are from current MX version supported. The parameter is needed to
     * support the case that an old MX version is used....
     *
     * @see #prepare(ParameterCache_mxJPO)
     * @see #update(ParameterCache_mxJPO, File, String)
     */
    private static final String PARAM_SUPPORT_REL_CONS = "DMRelationSupportRelCons";

    /**
     * Set of all ignored URLs from the XML definition for relationships.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/allowAllRelationships");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/allowAllTypes");
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/relationshipDefRefList");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/relationshipDefRefList/relationshipDefRef");
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/typeRefList");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/fromSide/typeRefList/typeRef");
        Relationship_mxJPO.IGNORED_URLS.add("/toSide");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/allowAllRelationships");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/allowAllTypes");
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/relationshipDefRefList");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/relationshipDefRefList/relationshipDefRef");
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/typeRefList");
        // to be ignored, because read within prepare method
        Relationship_mxJPO.IGNORED_URLS.add("/toSide/typeRefList/typeRef");
    }

    /**
     * Set holding all rules referencing this attribute.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #writeObject(ParameterCache_mxJPO, Appendable)
     */
    private final Set<String> rules = new TreeSet<String>();

    /**
     * Prevent duplicates for this relationship.
     */
    private boolean preventDuplicates = false;

    /**
     * From side information.
     */
    private final Side from = new Side("from");

    /**
     * To side information.
     */
    private final Side to = new Side("to");

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
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Relationship_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/accessRuleRef".equals(_url))  {
            this.rules.add(_content);
            parsed = true;

        } else if ("/fromSide/cardinality".equals(_url))  {
            this.from.cardinality = _content.equalsIgnoreCase("1") ? "One" : _content.toUpperCase();
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

        } else if ("/preventDuplicates".equals(_url))  {
            this.preventDuplicates = true;
            parsed = true;

        } else if ("/toSide/cardinality".equals(_url))  {
            this.to.cardinality = _content.equalsIgnoreCase("1") ? "One" : _content.toUpperCase();
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
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Evaluates the from and to type information. Because MX does not handle
     * the to or from side types correctly within XML exports, the from and to
     * side types must be evaluated via a &quot;<code>print relationship ...
     * select fromtype / totype</code>&quot; MQL statement. If this is not done
     * in this way, there is no other possibility to evaluate the information
     * {@link Side#typeAll} or {@link Side#relationAll}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the from / to side types could not be
     *                         evaluated or the prepare from the derived class
     *                         failed
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // evaluate all from types / relationships
        this.from.prepare(_paramCache);

        // evaluate all to types / relationships
        this.to.prepare(_paramCache);

        super.prepare(_paramCache);
    }

    /**
     * Writes all relationship specific information in the TCL update file.
     * The relationship specific informations are:
     * <ul>
     * <li>hidden flag</li>
     * <li>{@link #preventDuplicates prevent duplicates} flag</li>
     * <li>{@link #from} and {@link #to} side informations</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL code to the TCL update file could not be
     *                     written
     * @see Side#write(Appendable)
     */
    @Override()
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

        this.from.write(_out);
        this.to.write(_out);
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
    @Override()
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
        for (final String type : this.from.types)  {
            preMQLCode.append(" from remove type \"").append(StringUtil_mxJPO.convertMql(type)).append('\"');
        }
        // remove all from relationships
        for (final String rel : this.from.relations)  {
            preMQLCode.append(" from remove relationship \"").append(StringUtil_mxJPO.convertMql(rel)).append('\"');
        }
        // remove all to types
        for (final String type : this.to.types)  {
            preMQLCode.append(" to remove type \"").append(StringUtil_mxJPO.convertMql(type)).append('\"');
        }
        // remove all to relationships
        for (final String rel : this.to.relations)  {
            preMQLCode.append(" to remove relationship \"").append(StringUtil_mxJPO.convertMql(rel)).append('\"');
        }

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Stores the information for one side of a relationship.
     */
    private final class Side
    {
        /**
         * Side string of the relationship.
         */
        private final String side;

        /**
         * Side cardinality action.
         */
        private String cardinality = "";

        /**
         * Side clone action.
         */
        private String cloneAction = "";

        /**
         * Side meaning.
         */
        private String meaning = "";

        /**
         * Side propagate connection flag.
         */
        private boolean propagateConnection = false;

        /**
         * Side propagate modify flag.
         */
        private boolean propagateModify = false;

        /**
         * Side revision action.
         */
        private String revisionAction = "";

        /**
         * Side type list.
         *
         * @see #prepare(ParameterCache_mxJPO)
         * @see #fromTypeAll
         */
        private final Set<String> types = new TreeSet<String>();

        /**
         * Are all types on the side allowed?
         *
         * @see #prepare(ParameterCache_mxJPO)
         * @see #types
         */
        private boolean typeAll = false;

        /**
         * From side relationship list.
         *
         * @see #prepare(ParameterCache_mxJPO)
         * @see #relationAll
         */
        private final Set<String> relations = new TreeSet<String>();

        /**
         * Are all relationships on the from side allowed?
         *
         * @see #prepare(ParameterCache_mxJPO)
         * @see #relations
         */
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
        private void prepare(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            // evaluate all to types
            final String[] toTypesArr = MqlUtil_mxJPO.execMql(
                            _paramCache,
                            new StringBuilder("escape print rel \"")
                                    .append(StringUtil_mxJPO.convertMql(Relationship_mxJPO.this.getName()))
                                    .append("\" select ").append(this.side).append("type dump '\n'"))
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
            if (_paramCache.getValueBoolean(Relationship_mxJPO.PARAM_SUPPORT_REL_CONS))  {
                // evaluate all from relationships
                final String[] fromRelsArr = MqlUtil_mxJPO.execMql(
                                _paramCache,
                                new StringBuilder("escape print rel \"")
                                        .append(StringUtil_mxJPO.convertMql(Relationship_mxJPO.this.getName()))
                                        .append("\" select ").append(this.side).append("rel dump '\n'"))
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
         * @param _out          appendable instance to the TCL update file
         * @throws IOException if the TCL code to the TCL update file could not
         *                     be written
         * @see Relationship_mxJPO#writeObject(ParameterCache_mxJPO, Appendable)
         */
        protected void write(final Appendable _out)
            throws IOException
        {
            _out.append(" \\\n    ").append(this.side)
                .append(" \\\n        ").append(this.propagateModify ? "" : "!").append("propagatemodify")
                .append(" \\\n        ").append(this.propagateConnection ? "" : "!").append("propagateconnection")
                .append(" \\\n        meaning \"").append(StringUtil_mxJPO.convertTcl(this.meaning)).append('\"')
                .append(" \\\n        cardinality \"")
                        .append(StringUtil_mxJPO.convertTcl(this.cardinality)).append('\"')
                .append(" \\\n        revision \"")
                        .append(StringUtil_mxJPO.convertTcl(this.revisionAction)).append('\"')
                .append(" \\\n        clone \"").append(StringUtil_mxJPO.convertTcl(this.cloneAction)).append('\"');
            // from types
            if (this.typeAll)  {
                _out.append(" \\\n        add type \"all\"");
            } else  {
                for (final String type : this.types)  {
                    _out.append(" \\\n        add type \"").append(StringUtil_mxJPO.convertTcl(type)).append('\"');
                }
            }
            // from relationships
            if (this.relationAll)  {
                _out.append(" \\\n        add relationship \"all\"");
            } else  {
                for (final String relation : this.relations)  {
                    _out.append(" \\\n        add relationship \"")
                        .append(StringUtil_mxJPO.convertTcl(relation)).append('\"');
                }
            }
        }
    }
}
