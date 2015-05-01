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

package org.mxupdate.test.data.datamodel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.program.AbstractProgramData;

/**
 * The class is used to define all administration objects which could have
 * triggers used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <DATAWITHTRIGGERS>    defines the class which is derived from this
 *                              class
 */
public abstract class AbstractDataWithTrigger<DATAWITHTRIGGERS extends AbstractDataWithTrigger<?>>
    extends AbstractAdminData<DATAWITHTRIGGERS>
{
    /** Defines all assigned triggers to this administration object. */
    private final Triggers triggers = new Triggers();

    /**
     * Initialize the values for administration objects with triggers.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the administration object
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     * @param _requiredExportFlags  defines the required flags of the export
     *                              within the configuration item file
     */
    protected AbstractDataWithTrigger(final AbstractTest _test,
                                      final CI _ci,
                                      final String _name,
                                      final Map<String,Object> _requiredExportValues,
                                      final Map<String,Boolean> _requiredExportFlags)
    {
        super(_test, _ci, _name, _requiredExportValues, _requiredExportFlags);
    }

    /**
     * Appends a new <code>_trigger</code> to the list of all
     * {@link #triggers}.
     *
     * @param _trigger  trigger to append
     * @return this instance casted to T
     * @see #triggers
     */
    @SuppressWarnings("unchecked")
    public DATAWITHTRIGGERS addTrigger(final AbstractTrigger<?> _trigger)
    {
        this.triggers.add(_trigger);
        return (DATAWITHTRIGGERS) this;
    }

    /**
     * Returns all assigned triggers.
     *
     * @return all assigned triggers
     */
    public Triggers getTriggers()
    {
        return this.triggers;
    }

    @Override()
    @SuppressWarnings("unchecked")
    public DATAWITHTRIGGERS createDependings()
        throws MatrixException
    {
        super.createDependings();

        this.triggers.createDependings();

        return (DATAWITHTRIGGERS) this;
    }

    /**
     * Appends the MQL commands to define all {@link #triggers} within a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if programs could not be created or thrown from
     *                         called method in super class
     * @see #triggers
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);
        this.triggers.append4Create(_cmd);
    }

    /**
     * Defines a common interface for triggers.
     *
     * @param <T>   class of the derived trigger
     */
    public abstract static class AbstractTrigger<T extends AbstractDataWithTrigger.AbstractTrigger<?>>
    {
        /** Event type of this trigger. */
        private final String eventType;
        /** Kind of this trigger (action, check, overrider). */
        private final String kind;
        /** Called program of this trigger. */
        private final AbstractProgramData<?> program;
        /** Input value of this trigger. */
        private String input;

        /**
         * Initializes this trigger.
         *
         * @param _eventType    event type of this trigger
         * @param _kind         kind of this trigger
         * @param _program      used program of this trigger
         */
        private AbstractTrigger(final String _eventType,
                                final String _kind,
                                final AbstractProgramData<?> _program)
        {
            this.eventType = _eventType;
            this.kind = _kind;
            this.program = _program;
        }

        /**
         * Returns the defined trigger {@link #program}.
         *
         * @return trigger program
         */
        public AbstractProgramData<?> getProgram()
        {
            return this.program;
        }

        /**
         * Appends this trigger will so that the range is defined while the
         * administration object is created.
         *
         * @param _cmd  string builder where the MQL trigger will be appended
         * @throws MatrixException if {@link #program} could not be created
         */
        protected void append4Create(final StringBuilder _cmd)
            throws MatrixException
        {
            this.program.create();
            _cmd.append(" trigger ").append(this.eventType)
                .append(' ').append(this.kind)
                .append(" \"").append(AbstractTest.convertMql(this.program.getName()))
                .append('\"');
            if (this.input != null)  {
                _cmd.append(" input \"").append(AbstractTest.convertMql(this.input)).append('\"');
            }
        }

        /**
         * Defines the input value of this trigger.
         *
         * @param _input    input value
         * @return this trigger instance
         */
        @SuppressWarnings("unchecked")
        public T setInput(final String _input)
        {
            this.input = _input;
            return (T) this;
        }
    }

    /**
     * Used to define action triggers.
     */
    public static class TriggerAction
        extends AbstractDataWithTrigger.AbstractTrigger<AbstractDataWithTrigger.TriggerAction>
    {
        /**
         * Initializes this action trigger with the <code>_eventType</code>
         * for the <code>_program</code>.
         *
         * @param _eventType    event type of the trigger
         * @param _program      called program of the trigger
         */
        public TriggerAction(final String _eventType,
                             final AbstractProgramData<?> _program)
        {
            super(_eventType, "action", _program);
        }
    }

    /**
     * Used to define check triggers.
     */
    public static class TriggerCheck
        extends AbstractDataWithTrigger.AbstractTrigger<AbstractDataWithTrigger.TriggerAction>
    {
        /**
         * Initializes this check trigger with the <code>_eventType</code>
         * for the <code>_program</code>.
         *
         * @param _eventType    event type of the trigger
         * @param _program      called program of the trigger
         */
        public TriggerCheck(final String _eventType,
                             final AbstractProgramData<?> _program)
        {
            super(_eventType, "check", _program);
        }
    }

    /**
     * Used to define override triggers.
     */
    public static class TriggerOverride
        extends AbstractDataWithTrigger.AbstractTrigger<AbstractDataWithTrigger.TriggerAction>
    {
        /**
         * Initializes this override trigger with the <code>_eventType</code>
         * for the <code>_program</code>.
         *
         * @param _eventType    event type of the trigger
         * @param _program      called program of the trigger
         */
        public TriggerOverride(final String _eventType,
                               final AbstractProgramData<?> _program)
        {
            super(_eventType, "override", _program);
        }
    }

    /**
     * Handles set of defined triggers.
     */
    public class Triggers
        extends HashSet<AbstractTrigger<?>>
    {
        /** Serial Version UID. */
        private static final long serialVersionUID = 2134980869707730431L;

        /**
         * Appends the defined triggers to the TCL code {@code _cmd} of the
         * configuration item file.
         *
         * @param _prefix   prefix in front of the values
         * @param _cmd      string builder with the TCL commands of the
         *                  configuration item file
         */
        public void appendUpdate(final String _prefix,
                                 final StringBuilder _cmd)
        {
            for (final AbstractTrigger<?> trigger : this)  {
                _cmd.append(_prefix)
                    .append("trigger ").append(trigger.eventType).append(' ').append(trigger.kind)
                    .append(" \"").append(AbstractTest.convertUpdate(trigger.program.getName()))
                    .append("\"").append(" input \"").append(AbstractTest.convertUpdate(trigger.input)).append("\"").append('\n');
            }
        }

        /**
         * Create depending programs for all triggers.
         *
         * @throws MatrixException if create failed
         */
        public void createDependings()
            throws MatrixException
        {
            for (final AbstractTrigger<?> trigger : this)  {
                trigger.program.create();
            }
        }

        /**
         * Appends the MQL commands to define all {@link #triggers} within a
         * create.
         *
         * @param _cmd  string builder used to append MQL commands
         * @throws MatrixException if programs could not be created or thrown from
         *                         called method in super class
         */
        void append4Create(final StringBuilder _cmd)
            throws MatrixException
        {
            for (final AbstractTrigger<?> trigger : this)  {
                trigger.append4Create(_cmd);
            }
        }

        /**
         * Checks that the trigger are correct defined for given {@code _path}.
         *
         * @param _exportParser export parser
         * @param _path         path to check
         */
        public void checkExport(final ExportParser _exportParser,
                                final String _path)
        {
            final Set<String> trigLines = new HashSet<String>();
            for (final AbstractTrigger<?> trigger : this)  {
                trigLines.add(new StringBuilder()
                        .append(trigger.eventType).append(' ').append(trigger.kind)
                        .append(" \"").append(AbstractTest.convertUpdate(trigger.program.getName()))
                        .append("\"").append(" input \"").append(AbstractTest.convertUpdate(trigger.input)).append("\"")
                        .toString());
            }

            _exportParser.checkList((_path.isEmpty() ? "" : _path + "/") + "trigger", trigLines);
        }
    }
}
