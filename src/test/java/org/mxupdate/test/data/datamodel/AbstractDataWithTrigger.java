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

package org.mxupdate.test.data.datamodel;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.data.AbstractData;
import org.mxupdate.test.data.program.AbstractProgram;

/**
 * The class is used to define all administration objects which could have
 * triggers used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <T>   defines the class which is derived from this class
 */
public abstract class AbstractDataWithTrigger<T extends AbstractDataWithTrigger<?>>
    extends AbstractData<T>
{
    /**
     * Defines all assigned triggers to this administration object.
     *
     * @see #addTrigger(AbstractTrigger)
     */
    private final Set<AbstractTrigger<?>> triggers = new HashSet<AbstractTrigger<?>>();

    /**
     * Initialize the values for administration objects with triggers.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the administration object
     * @param _filePrefix           prefix for the file name
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    protected AbstractDataWithTrigger(final AbstractTest _test,
                                      final CI _ci,
                                      final String _name,
                                      final String _filePrefix,
                                      final Set<String> _requiredExportValues)
    {
        super(_test, _ci, _name, _filePrefix, _requiredExportValues);
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
    public T addTrigger(final AbstractTrigger<?> _trigger)
    {
        this.triggers.add(_trigger);
        return (T) this;
    }

    /**
     * Appends the MQL commands to define all {@link #triggers}Êwithin a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if programs could not be created or thrown from
     *                         called method in super class
     * @see #triggers
     */
    @Override
    protected void append4CreateValues(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4CreateValues(_cmd);
        for (final AbstractTrigger<?> trigger : this.triggers)  {
            trigger.append4Create(_cmd);
        }
    }

    /**
     * Evaluates all 'adds' for triggers in the configuration item file.
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #triggers}
     */
    @Override
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final AbstractTrigger<?> trigger : this.triggers)  {
            trigger.evalAdds4CheckExport(_needAdds);
        }
    }

    /**
     * Defines a common interface for triggers.
     *
     * @param <T>   class of the derived trigger
     */
    public abstract static class AbstractTrigger<T extends AbstractDataWithTrigger.AbstractTrigger<?>>
    {
        /**
         * Event type of this trigger.
         */
        private final String eventType;

        /**
         * Kind of this trigger (action, check, overrider).
         */
        private final String kind;

        /**
         * Called program of this trigger.
         */
        private final AbstractProgram<?> program;

        /**
         * Input value of this trigger.
         *
         * @see #setInput(String)
         */
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
                                final AbstractProgram<?> _program)
        {
            this.eventType = _eventType;
            this.kind = _kind;
            this.program = _program;
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
         * Appends the add statement in TCL code for this trigger.
         *
         * @param _needAdds     set with add strings used to append the adds
         *                      for this trigger
         */
        protected void evalAdds4CheckExport(final Set<String> _needAdds)
        {
            final StringBuilder cmd = new StringBuilder()
                .append("trigger ").append(this.eventType)
                .append(' ').append(this.kind)
                .append(" \"").append(AbstractTest.convertTcl(this.program.getName()))
                .append("\"")
                .append(" input \"");
            if (this.input != null)  {
                cmd.append(AbstractTest.convertMql(this.input));
            }
            cmd.append("\"");
            _needAdds.add(cmd.toString());
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
                             final AbstractProgram<?> _program)
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
                             final AbstractProgram<?> _program)
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
                               final AbstractProgram<?> _program)
        {
            super(_eventType, "override", _program);
        }
    }
}
