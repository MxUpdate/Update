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
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * Data model type class.
 *
 * @author tmoxter
 */
public class Type_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMObject_mxJPO
{
    /**
     * Is the type abstract?
     */
    private boolean abstractFlag = false;

    /**
     * Is the type hidden?
     */
    private boolean hidden = false;

    /**
     * From which type is this type derived?
     */
    private String derived = "ADMINISTRATION";

    /**
     * List of all attributes for this type.
     */
    private final Set<String> attributes = new TreeSet<String>();

    /**
     * Stack with all triggers for this type.
     */
    final Stack<Trigger> triggersStack = new Stack<Trigger>();

    /**
     * Map with all triggers for this type. The key is the name of the trigger.
     *
     * @see #prepare(Context)   method where the map is prepared
     * @see #triggersStack      stack with trigger from parsing method
     */
    final Map<String,Trigger> triggers = new TreeMap<String,Trigger>();

    public Type_mxJPO()
    {
        super("type");
    }

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if ("/abstract".equals(_url))  {
            this.abstractFlag = true;

        } else if ("/adminProperties/hidden".equals(_url))  {
            this.hidden = true;

        } else if ("/attributeDefRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.attributes.add(_content);

        } else if ("/derivedFrom".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFrom/typeRefList".equals(_url))  {
            // to be ignored ...
        } else if ("/derivedFrom/typeRefList/typeRef".equals(_url))  {
            this.derived = _content;

        } else if ("/triggerList".equals(_url))  {
            // to be ignored ...
        } else if ("/triggerList/trigger".equals(_url))  {
            this.triggersStack.add(new Trigger());
        } else if ("/triggerList/trigger/triggerName".equals(_url))  {
            this.triggersStack.peek().name = _content;
        } else if ("/triggerList/trigger/programRef".equals(_url))  {
            this.triggersStack.peek().program = _content;
        } else if ("/triggerList/trigger/inputArguments".equals(_url))  {
            this.triggersStack.peek().arguments = _content;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * After the type XML file is parsed, the triggers must be sorted.
     *
     * @param _context      context for this request
     * @see #triggersStack
     */
    @Override
    public void prepare(final Context _context)
            throws MatrixException
    {
        // sort all triggers
        for (final Trigger trigger : this.triggersStack)  {
            this.triggers.put(trigger.name, trigger);
        }

        super.prepare(_context);
    }

    @Override
    protected void writeObject(Writer _out) throws IOException
    {
        _out.append(" \\\n    derived \"").append(convert(this.derived)).append("\"")
            .append(" \\\n    ").append(this.hidden ? "" : "!").append("hidden")
            .append(" \\\n    abstract ").append(Boolean.toString(this.abstractFlag));
        // output of triggers, but sorted!
        for (final Trigger trigger : this.triggers.values())  {
            trigger.write(_out);
        }
    }

    @Override
    protected void writeEnd(final Writer _out)
            throws IOException
    {
        _out.append("\n\ntestAttributes -type \"${NAME}\" -attributes [list \\\n");
        for (final String attr : this.attributes)  {
            _out.append("    \"").append(convert(attr)).append("\" \\\n");
        }
        _out.append("]");
    }

    static class Trigger  {

        /**
         * Used to parse the event type of the trigger from the {@link #name}.
         *
         * @see #write(Writer)
         */
        private static final Pattern PATTERN_EVENTTYPE = Pattern.compile("^.*(?=((Action)|(Check)|(Override))$)");

        /**
         * Used to parse the event kind (&quot;Action&quot;, &quot;Check&quot;
         * or &quot;Override&quot;).
         *
         * @see #write(Writer)
         */
        private static final Pattern PATTERN_KIND = Pattern.compile("((Action)|(Check)|(Override))$");


        /**
         * Name of the trigger (like ChangeVaultAction etc...).
         */
        String name;

        /**
         * Name of the referenced program.
         */
        String program;

        /**
         * Arguments of the called program.
         */
        String arguments = "";

        /**
         * Writes this trigger TCL source code. The trigger event type and kind
         * is parsed by using regular expression {@link #PATTERN_EVENTTYPE} and
         * {@link #PATTERN_KIND} to extract the event type and kind.
         *
         * @param _out  writer instance
         * @throws IOException if write failed
         */
        protected void write(Writer _out)
                throws IOException
        {
            // parse event type
            final Matcher matchEventType = PATTERN_EVENTTYPE.matcher(this.name);
            matchEventType.find();
            final String eventType = matchEventType.group();
            // parse kind
            final Matcher matchKind = PATTERN_KIND.matcher(this.name);
            matchKind.find();
            final String kind = matchKind.group();
            _out.append(" \\\n    add trigger ").append(eventType.toLowerCase()).append(' ').append(kind.toLowerCase())
                .append(" \"").append(convert(this.program)).append("\"")
                .append(" input \"").append(convert(this.arguments)).append("\"");
        }
    }
}
