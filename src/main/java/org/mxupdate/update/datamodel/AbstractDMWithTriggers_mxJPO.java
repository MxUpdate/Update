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
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Abstract class for all data model administration objects with triggers.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractDMWithTriggers_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /** Stack with all triggers for this type used within parsing. */
    private final Stack<Trigger> triggersStack = new Stack<Trigger>();

    /** Map with all triggers. The key is the name of the trigger. */
    private final Triggers triggers = new Triggers();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public AbstractDMWithTriggers_mxJPO(final TypeDef_mxJPO _typeDef,
                                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Returns all defined {@link #triggers} sorted.
     *
     * @return all defined triggers
     */
    protected Triggers getTriggers()
    {
        return this.triggers;
    }

    /**
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if ("/triggerList".equals(_url))  {
            parsed = true;
        } else if ("/triggerList/trigger".equals(_url))  {
            this.triggersStack.add(new Trigger());
            parsed = true;
        } else if ("/triggerList/trigger/triggerName".equals(_url))  {
            this.triggersStack.peek().name = _content;
            parsed = true;
        } else if ("/triggerList/trigger/programRef".equals(_url))  {
            this.triggersStack.peek().program = _content;
            parsed = true;
        } else if ("/triggerList/trigger/inputArguments".equals(_url))  {
            this.triggersStack.peek().arguments = _content;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * After the type XML file is parsed, the triggers must be sorted.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the preparation from derived class failed
     * @see #triggersStack
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // sort all triggers
        for (final Trigger trigger : this.triggersStack)  {
            this.triggers.put(trigger.name, trigger);
        }

        super.prepare(_paramCache);
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to remove all current assigned triggers.
     * Then the update method of the super class is called.
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
        final StringBuilder preMQLCode = new StringBuilder();
        // remove all triggers
        if (!this.triggers.isEmpty())  {
            preMQLCode.append("escape mod ").append(this.getTypeDef().getMxAdminName())
                      .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                      .append(this.getTypeDef().getMxAdminSuffix());
            for (final Trigger trigger : this.triggers.values())  {
                trigger.appendResetMQLStatement(preMQLCode);
            }
            preMQLCode.append(";\n");
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_paramCache, preMQLCode, _postMQLCode, _preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * Class used to store informations about triggers.
     */
    public static class Trigger
    {
        /** Used to parse the event type of the trigger from the {@link #name}. */
        private static final Pattern PATTERN_EVENTTYPE = Pattern.compile("^.*(?=((action)|(check)|(override))$)");
        /** Used to parse the event kind (&quot;Action&quot;, &quot;Check&quot; or &quot;Override&quot;).*/
        private static final Pattern PATTERN_KIND = Pattern.compile("((action)|(check)|(override))$");

        /** Name of the trigger (like ChangeVaultAction etc...). */
        String name;
        /** Name of the referenced program. */
        String program;
        /** Arguments of the called program. */
        String arguments = "";

        /**
         * Evaluates from the {@link #name} the related event type of the
         * trigger.
         *
         * @return event type of the trigger  (always in lower case)
         * @see #name
         * @see #PATTERN_EVENTTYPE
         */
        protected String getEventType()
        {
            final Matcher matchEventType = AbstractDMWithTriggers_mxJPO.Trigger.PATTERN_EVENTTYPE.matcher(this.name.toLowerCase());
            matchEventType.find();
            return matchEventType.group();
        }

        /**
         * Evaluates from the {@link #name} the related king of the trigger.
         *
         * @return kind of the trigger (always in lower case)
         * @see #name
         * @see #PATTERN_KIND
         */
        protected String getKind()
        {
            final Matcher matchKind = AbstractDMWithTriggers_mxJPO.Trigger.PATTERN_KIND.matcher(this.name.toLowerCase());
            matchKind.find();
            return matchKind.group();
        }

        /**
         * Writes this trigger TCL source code. The trigger event type and kind
         * is parsed by using regular expression {@link #PATTERN_EVENTTYPE} and
         * {@link #PATTERN_KIND} to extract the event type and kind.
         *
         * @param _out  writer instance
         * @param _prefix   prefix written before trigger definition
         * @param _suffix   suffix written before trigger definition
         * @throws IOException if write failed
         */
        protected void write(final Appendable _out,
                             final String _prefix,
                             final String _suffix)
            throws IOException
        {
            _out.append(_prefix)
                .append("trigger ").append(this.getEventType()).append(' ').append(this.getKind())
                .append(" \"").append(StringUtil_mxJPO.convertTcl(this.program)).append("\"")
                .append(" input \"").append(StringUtil_mxJPO.convertTcl(this.arguments)).append("\"")
                .append(_suffix);
        }

        /**
         * Appends the MQL statement to remove all triggers.
         *
         * @param _cmd          string builder with current MQL statement
         */
        protected void appendResetMQLStatement(final StringBuilder _cmd)
        {
            _cmd.append(" remove trigger ").append(this.getEventType()).append(' ').append(this.getKind());
        }
    }


    public static class Triggers
        extends TreeMap<String,Trigger>
    {
        /**
         * Writes the trigger information to the writer instance.
         *
         * @param _out      writer instance
         * @param _prefix   prefix written before trigger definition
         * @param _suffix   suffix written before trigger definition
         * @throws IOException if write failed
         */
        protected void write(final Appendable _out,
                             final String _prefix,
                             final String _suffix)
            throws IOException
        {
            // output of triggers, but sorted!
            for (final Trigger trigger : this.values())  {
                trigger.write(_out, _prefix, _suffix);
            }
        }

        /**
         * Calculates the delta between current trigger definition and this
         * target trigger definitions.
         *
         * @param _current  current triggers
         * @param _cmd      MQL string builder to append the delta
         */
        protected void calcDelta(final Triggers _current,
                                 final StringBuilder _cmd)
        {
            if (_current != null)  {
                for (final Trigger trigger : _current.values())  {
                    if (!this.containsKey(trigger.name))  {
                        _cmd.append(" remove trigger ")
                            .append(trigger.getEventType())
                            .append(' ')
                            .append(trigger.getKind());
                    }
                }
            }
            for (final Trigger trigger : this.values())  {
                _cmd.append(" add trigger ").append(trigger.getEventType()).append(' ').append(trigger.getKind())
                    .append(" \"").append(StringUtil_mxJPO.convertMql(trigger.program)).append("\"")
                    .append(" input \"").append(StringUtil_mxJPO.convertMql(trigger.arguments)).append('\"');
            }
        }
    }
}
