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
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 * Data model relationship class.
 *
 * @author Tim Moxter
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
     */
    private final Set<String> fromTypes = new TreeSet<String>();

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
     */
    private final Set<String> toTypes = new TreeSet<String>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public Relationship_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
// TODO rules:
        if ("/attributeDefRefList".equals(_url))  {
            // to be ignored ...

        } else if ("/fromSide".equals(_url))  {
            // to be ignored ...
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
        } else if ("/fromSide/revisionAction".equals(_url))  {
            this.fromRevisionAction = _content;
        } else if ("/fromSide/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/fromSide/typeRefList/typeRef".equals(_url))  {
            this.fromTypes.add(_content);
        } else if ("/fromSide/propagateConnection".equals(_url))  {
            this.fromPropagateConnection = true;

        } else if ("/preventDuplicates".equals(_url))  {
            this.preventDuplicates = true;

        } else if ("/toSide".equals(_url))  {
            // to be ignored ...
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
        } else if ("/toSide/revisionAction".equals(_url))  {
            this.toRevisionAction = _content;
        } else if ("/toSide/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/toSide/typeRefList/typeRef".equals(_url))  {
            this.toTypes.add(_content);
        } else if ("/toSide/propagateConnection".equals(_url))  {
            this.toPropagateConnection = true;

        } else  {
            super.parse(_url, _content);
        }
    }

    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
// TODO rules:
        _out.append(" \\\n    ").append(isHidden() ? "" : "!").append("hidden")
            .append(" \\\n    ").append(this.preventDuplicates ? "" : "!").append("preventduplicates");
        this.writeTriggers(_out);
        _out.append(" \\\n    from")
            .append(" \\\n        ").append(this.fromPropagateModify ? "" : "!").append("propagatemodify")
            .append(" \\\n        ").append(this.fromPropagateConnection ? "" : "!").append("propagateconnection")
            .append(" \\\n        meaning \"").append(convertTcl(this.fromMeaning)).append("\"")
            .append(" \\\n        cardinality \"").append(convertTcl(this.fromCardinality)).append("\"")
            .append(" \\\n        revision \"").append(convertTcl(this.fromRevisionAction)).append("\"")
            .append(" \\\n        clone \"").append(convertTcl(this.fromCloneAction)).append("\"");
        if (this.fromTypes.isEmpty())  {
            _out.append(" \\\n        add type \"all\"");
        } else  {
            for (final String type : this.fromTypes)  {
                _out.append(" \\\n        add type \"").append(convertTcl(type)).append("\"");
            }
        }
        _out.append(" \\\n    to")
            .append(" \\\n        ").append(this.toPropagateModify ? "" : "!").append("propagatemodify")
            .append(" \\\n        ").append(this.toPropagateConnection ? "" : "!").append("propagateconnection")
            .append(" \\\n        meaning \"").append(convertTcl(this.toMeaning)).append("\"")
            .append(" \\\n        cardinality \"").append(convertTcl(this.toCardinality)).append("\"")
            .append(" \\\n        revision \"").append(convertTcl(this.toRevisionAction)).append("\"")
            .append(" \\\n        clone \"").append(convertTcl(this.toCloneAction)).append("\"");
        if (this.toTypes.isEmpty())  {
            _out.append(" \\\n        add type \"all\"");
        } else  {
            for (final String type : this.toTypes)  {
                _out.append(" \\\n        add type \"").append(convertTcl(type)).append("\"");
            }
        }
    }


    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this relationship:
     * <ul>
     * <li>reset description</li>
     * <li>remove hidden and prevent duplicate flag</li>
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
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append('\"')
                // remove hidden, description, prevent duplicate
                .append(" !hidden description \"\" !preventduplicate")
                // reset from information
                .append(" from !propagatemodify !propagateconnection  meaning \"\" cardinality one revision none clone none ")
                .append(" from remove type all")
                // reset to information
                .append(" to !propagatemodify !propagateconnection  meaning \"\" cardinality one revision none clone none ")
                .append(" to remove type all");
        // remove all from types
        for (final String type : this.fromTypes)  {
            preMQLCode.append(" from remove type \"").append(type).append('\"');
        }
        // remove all to types
        for (final String type : this.toTypes)  {
            preMQLCode.append(" to remove type \"").append(type).append('\"');
        }
     // TODO: remove rules

        // append already existing pre MQL code
        preMQLCode.append(";\n")
                  .append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }
}
