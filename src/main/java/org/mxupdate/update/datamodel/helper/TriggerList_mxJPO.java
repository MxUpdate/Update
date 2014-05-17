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
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Handles the list of all defined triggers.
 *
 * @author The MxUpdate Team
 */
public class TriggerList_mxJPO
    extends TreeSet<Trigger>
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
        for (final Trigger trigger : this.triggersStack)  {
            this.add(trigger);
        }
    }

    /**
     * Writes the trigger information to the writer instance.
     *
     * @param _out      writer instance
     * @param _prefix   prefix written before trigger definition
     * @param _suffix   suffix written before trigger definition
     * @throws IOException if write failed
     */
    public void write(final Appendable _out,
                      final String _prefix,
                      final String _suffix)
        throws IOException
    {
        // output of triggers, but sorted!
        for (final Trigger trigger : this)  {
            _out.append(_prefix)
                .append("trigger ").append(trigger.eventType).append(' ').append(trigger.kind)
                .append(" \"").append(StringUtil_mxJPO.convertTcl(trigger.program)).append("\"")
                .append(" input \"").append(StringUtil_mxJPO.convertTcl(trigger.arguments)).append("\"")
                .append(_suffix);
        }
    }

    /**
     * Appends the MQL statement to remove all triggers.
     *
     * @param _cmd          string builder with current MQL statement
     */
    public void appendResetMQLStatement(final StringBuilder _cmd)
    {
        for (final Trigger trigger : this)  {
            _cmd.append(" remove trigger ").append(trigger.eventType).append(' ').append(trigger.kind);
        }
    }

    /**
     * Calculates the delta between current trigger definition and this
     * target trigger definitions.
     *
     * @param _currents current triggers
     * @param _mql      MQL builder to append the delta
     */
    public void calcDelta(final TriggerList_mxJPO _currents,
                          final MqlBuilder_mxJPO _mql)
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
                        .append("remove trigger ").append(current.eventType).append(' ').append(current.kind);
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
                    .append("add trigger ").append(target.eventType).append(' ').append(target.kind)
                    .append(" \"").append(StringUtil_mxJPO.convertMql(target.program)).append("\"")
                    .append(" input \"").append(StringUtil_mxJPO.convertMql(target.arguments)).append('\"');
            }
        }
    }

    /**
     * Calculates the delta between current trigger definition and this
     * target trigger definitions.
     *
     * @param _current  current triggers
     * @param _cmd      MQL string builder to append the delta
     */
    @Deprecated()
    public void calcDelta(final TriggerList_mxJPO _current,
                          final StringBuilder _cmd)
    {
        if (_current != null)  {
            for (final Trigger trigger : _current)  {
                _cmd.append(" remove trigger ").append(trigger.eventType).append(' ').append(trigger.kind);
            }
        }
        for (final Trigger trigger : this)  {
            _cmd.append(" add trigger ").append(trigger.eventType).append(' ').append(trigger.kind)
                .append(" \"").append(StringUtil_mxJPO.convertMql(trigger.program)).append("\"")
                .append(" input \"").append(StringUtil_mxJPO.convertMql(trigger.arguments)).append('\"');
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
         * Compare this range instance with given range instance. The compare
         * is done for each instance variable in this order:
         * <ul>
         * <li>{@link #type}</li>
         * <li>{@link #include1}</li>
         * <li>{@link #include2}</li>
         * <li>{@link #value1}</li>
         * <li>{@link #value2}</li>
         * </ul>
         * If one of the compared instance variables are not equal, the
         * compared value is returned. So only if all instance variable has the
         * same values the ranges itself are identically.
         *
         * @param _trigger  trigger instance to which this instance must be
         *                  compared to
         * @return &quot;0&quot; if both triggers are equal; positive number if
         *         greater; otherwise negative number
         */
        @Override()
        public int compareTo(final Trigger _trigger)
        {
            int ret = this.eventType.compareTo(_trigger.eventType);
            if (ret == 0)  {
                ret = this.kind.compareTo(_trigger.kind);
            }
            if (ret == 0)  {
                ret = (this.program != null)
                      ? ((_trigger.program != null) ? this.program.compareTo(_trigger.program) : 0)
                      : -1;
            }
            if (ret == 0)  {
                ret = (this.arguments != null)
                      ? ((_trigger.arguments != null) ? this.arguments.compareTo(_trigger.arguments) : 0)
                      : -1;
            }
            return ret;
        }
    }
}
