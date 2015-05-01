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

package org.mxupdate.update.datamodel.helper;

import java.io.IOException;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mxupdate.update.datamodel.helper.TriggerList_mxJPO.Trigger;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateList;

/**
 * Handles the list of all defined triggers.
 *
 * @author The MxUpdate Team
 */
public class TriggerList_mxJPO
    extends TreeSet<Trigger>
    implements UpdateList
{
    /** Serial Version UID. */
    private static final long serialVersionUID = -7627052479042191366L;

    /** Used to parse the event type of the trigger from the {@link #name}. */
    private static final Pattern PATTERN_EVENTTYPE = Pattern.compile("^.*(?=((action)|(check)|(override))$)");
    /** Used to parse the event kind (&quot;Action&quot;, &quot;Check&quot; or &quot;Override&quot;).*/
    private static final Pattern PATTERN_KIND = Pattern.compile("((action)|(check)|(override))$");

    /** Stack with all triggers for this type used within parsing. */
    private final Stack<Trigger> triggersStack = new Stack<Trigger>();

    /**
     * Parses the trigger definition.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        boolean parsed = false;

        if ("/trigger".equals(_url))  {
            this.triggersStack.add(new Trigger());
            parsed = true;
        } else if ("/trigger/triggerName".equals(_url))  {
            // calculate event type
            final Matcher matchEventType = TriggerList_mxJPO.PATTERN_EVENTTYPE.matcher(_content.toLowerCase());
            matchEventType.find();
            this.triggersStack.peek().eventType = matchEventType.group();
            // calculate kind
            final Matcher matchKind = TriggerList_mxJPO.PATTERN_KIND.matcher(_content.toLowerCase());
            matchKind.find();
            this.triggersStack.peek().kind = matchKind.group();
            parsed = true;
        } else if ("/trigger/programRef".equals(_url))  {
            this.triggersStack.peek().program = _content;
            parsed = true;
        } else if ("/trigger/inputArguments".equals(_url))  {
            this.triggersStack.peek().arguments = _content;
            parsed = true;
        }

        return parsed;
    }

    /**
     * The trigger instances are sorted.
     */
    public void prepare()
    {
        this.addAll(this.triggersStack);
    }

    @Override()
    public void write(final UpdateBuilder_mxJPO _updateBuilder)
    {
        for (final Trigger trigger : this)  {
            _updateBuilder
                    .stepStartNewLine()
                    .stepCmd("trigger").stepSpace()
                    .stepCmd(trigger.eventType).stepSpace()
                    .stepCmd(trigger.kind).stepSpace()
                    .stepString(trigger.program).stepSpace()
                    .stepCmd("input").stepSpace().stepString(trigger.arguments)
                    .stepEndLine();
        }
    }

    /**
     * Writes the sorted trigger information to the writer instance.
     *
     * @param _out      writer instance
     * @param _prefix   prefix written before trigger definition
     * @throws IOException if write failed
     */
    @Deprecated()
    public void write(final Appendable _out,
                      final String _prefix)
        throws IOException
    {
        for (final Trigger trigger : this)  {
            _out.append(_prefix)
                .append("trigger ").append(trigger.eventType).append(' ').append(trigger.kind)
                .append(" \"").append(StringUtil_mxJPO.convertUpdate(trigger.program)).append("\"")
                .append(" input \"").append(StringUtil_mxJPO.convertUpdate(trigger.arguments)).append("\"\n");
        }
    }

    /**
     * Calculates the delta between current trigger definition and this
     * target trigger definitions.
     *
     * @param _mql      MQL builder to append the delta
     * @param _currents current triggers
     */
    public void calcDelta(final MultiLineMqlBuilder _mql,
                          final TriggerList_mxJPO _currents)
    {
        // remove obsolete triggers
        if (_currents != null)  {
            for (final Trigger current : _currents)  {
                boolean found = false;
                for (final Trigger target : this)  {
                    if (current.compareTo(target) == 0)  {
                        found = true;
                        break;
                    }
                }
                if (!found)  {
                    _mql.newLine()
                        .cmd("remove trigger ").cmd(current.eventType).cmd(" ").cmd(current.kind);
                }
            }
        }
        // append new triggers
        for (final Trigger target : this)  {
            boolean found = false;
            if (_currents != null)  {
                for (final Trigger current : _currents)  {
                    if (target.compareTo(current) == 0)  {
                        found = true;
                        break;
                    }
                }
            }
            if (!found)  {
                _mql.newLine()
                    .cmd("add trigger ").cmd(target.eventType).cmd(" ").cmd(target.kind).cmd(" ").arg(target.program).cmd(" input ").arg(target.arguments);
            }
        }
    }

    /**
     * Class used to store informations about triggers.
     */
    public static class Trigger
        implements Comparable<Trigger>
    {
        /** Event type of the trigger (like check, action or override etc...). */
        private String eventType;
        /** Kind of the trigger. */
        private String kind;
        /** Name of the referenced program. */
        private String program;
        /** Arguments of the called program. */
        private String arguments = "";

        /**
         * Compare this trigger instance with given trigger instance.
         *
         * @param _trigger  trigger instance to which this instance must be
         *                  compared to
         * @return &quot;0&quot; if both triggers are equal; positive number if
         *         greater; otherwise negative number
         */
        @Override()
        public int compareTo(final Trigger _trigger)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.eventType, _trigger.eventType);
            ret = CompareToUtil_mxJPO.compare(ret, this.kind,      _trigger.kind);
            ret = CompareToUtil_mxJPO.compare(ret, this.program,   _trigger.program);
            ret = CompareToUtil_mxJPO.compare(ret, this.arguments, _trigger.arguments);
            return ret;
        }
    }
}
