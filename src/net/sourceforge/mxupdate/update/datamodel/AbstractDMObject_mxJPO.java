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
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;


/**
 * Abstract class for all data model administration objects (not business
 * objects!).
 *
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractDMObject_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO
{
    /**
     * Stack with all triggers for this type.
     */
    private final Stack<Trigger> triggersStack = new Stack<Trigger>();

    /**
     * Map with all triggers for this type. The key is the name of the trigger.
     *
     * @see #prepare(Context)           method where the map is prepared
     * @see #triggersStack              stack with trigger from parsing method
     * @see #writeTriggers(Writer)      write the trigger information
     */
    private final Map<String,Trigger> triggers = new TreeMap<String,Trigger>();

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    public void parse(final String _url,
                      final String _content)
    {
        if ("/triggerList".equals(_url))  {
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
    protected void prepare(final Context _context)
            throws MatrixException
    {
        // sort all triggers
        for (final Trigger trigger : this.triggersStack)  {
            this.triggers.put(trigger.name, trigger);
        }

        super.prepare(_context);
    }

    /**
     * Writes the trigger information to the writter instance.
     *
     * @param _out      writer instance
     * @throws IOException if write failed
     * @see #triggers
     */
    protected void writeTriggers(final Writer _out)
            throws IOException
    {
        // output of triggers, but sorted!
        for (final Trigger trigger : this.triggers.values())  {
            trigger.write(_out);
        }
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
