/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * Data model relationship class.
 *
 * @author tmoxter
 */
@net.sourceforge.mxupdate.update.util.TagName_mxJPO("relationshipDef")
public class Relationship_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMObject_mxJPO
{
    /**
     * List of all attributes for this type.
     */
    private final Set<String> attributes = new TreeSet<String>();

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

    public Relationship_mxJPO()
    {
        super("relationship");
    }

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if ("/attributeDefRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.attributes.add(_content);

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
    protected void writeObject(Writer _out) throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "" : "!").append("hidden")
            .append(" \\\n    ").append(this.preventDuplicates ? "" : "!").append("preventduplicates");
        writeTriggers(_out);
        _out.append(" \\\n    from")
            .append(" \\\n        ").append(this.fromPropagateModify ? "" : "!").append("propagatemodify")
            .append(" \\\n        ").append(this.fromPropagateConnection ? "" : "!").append("propagateconnection")
            .append(" \\\n        meaning \"").append(convert(this.fromMeaning)).append("\"")
            .append(" \\\n        cardinality \"").append(convert(this.fromCardinality)).append("\"")
            .append(" \\\n        revision \"").append(convert(this.fromRevisionAction)).append("\"")
            .append(" \\\n        clone \"").append(convert(this.fromCloneAction)).append("\"");
        if (this.fromTypes.isEmpty())  {
            _out.append(" \\\n        add type \"all\"");
        } else  {
            for (final String type : this.fromTypes)  {
                _out.append(" \\\n        add type \"").append(convert(type)).append("\"");
            }
        }
        _out.append(" \\\n    to")
            .append(" \\\n        ").append(this.toPropagateModify ? "" : "!").append("propagatemodify")
            .append(" \\\n        ").append(this.toPropagateConnection ? "" : "!").append("propagateconnection")
            .append(" \\\n        meaning \"").append(convert(this.toMeaning)).append("\"")
            .append(" \\\n        cardinality \"").append(convert(this.toCardinality)).append("\"")
            .append(" \\\n        revision \"").append(convert(this.toRevisionAction)).append("\"")
            .append(" \\\n        clone \"").append(convert(this.toCloneAction)).append("\"");
        if (this.toTypes.isEmpty())  {
            _out.append(" \\\n        add type \"all\"");
        } else  {
            for (final String type : this.toTypes)  {
                _out.append(" \\\n        add type \"").append(convert(type)).append("\"");
            }
        }
    }

    @Override
    protected void writeEnd(final Writer _out)
            throws IOException
    {
        _out.append("\n\ntestAttributes -relationship \"${NAME}\" -attributes [list \\\n");
        for (final String attr : this.attributes)  {
            _out.append("    \"").append(convert(attr)).append("\" \\\n");
        }
        _out.append("]");
    }
}
