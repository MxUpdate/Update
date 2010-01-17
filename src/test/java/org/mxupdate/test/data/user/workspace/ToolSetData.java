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

package org.mxupdate.test.data.user.workspace;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * The class is used to define all tool set objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <USER> class of the related user for which this tool set is defined
 */
public class ToolSetData<USER extends AbstractUserData<?>>
    extends AbstractVisualWorkspaceObjectData<ToolSetData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
    }

    /**
     * Set of all programs defined by this tool set.
     *
     * @see #addProgram(AbstractProgramData)
     * @see #getPrograms()
     * @see #ciFile()
     * @see #append4Create(StringBuilder)
     */
    private final Set<AbstractProgramData<?>> programs = new HashSet<AbstractProgramData<?>>();

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this tip is defined
     * @param _name     name of the tip
     */
    public ToolSetData(final AbstractTest _test,
                       final USER _user,
                       final String _name)
    {
        super(_test, "toolset", _user, _name, ToolSetData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns given <code>_program</code> to this tool set.
     *
     * @param _program  program to assign
     * @return this tool set instance
     * @see #programs
     */
    public ToolSetData<USER> addProgram(final AbstractProgramData<?> _program)
    {
        this.programs.add(_program);
        return this;
    }

    /**
     * Returns the {@link #programs list of all assigned programs}.
     *
     * @return all assigned programs
     * @see #programs
     */
    public Set<AbstractProgramData<?>> getPrograms()
    {
        return this.programs;
    }

    /**
     * Returns the part of the CI file to create this tool set of an user.
     *
     * @return part of the CI file to create this tool set of an user
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder().append(super.ciFile());

        // programs
        for(final AbstractProgramData<?> program : this.programs)  {
            cmd.append(" \\\n    program \"").append(AbstractTest.convertTcl(program.getName())).append("\"");
        }

        return cmd.toString();
    }

    /**
     * Appends the MQL commands to define the {@link #programs} within a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // programs
        for(final AbstractProgramData<?> program : this.programs)  {
            program.create();
            _cmd.append(" program \"").append(AbstractTest.convertMql(program.getName())).append("\"");
        }
    }

    /**
     * Checks the export of this tool set data if all values are correct
     * defined. The {@link #programs} are checked.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // prepare target programs
        final Set<String> targets = new HashSet<String>();
        for(final AbstractProgramData<?> program : this.programs)  {
            targets.add("\"" + AbstractTest.convertTcl(program.getName()) + "\"");
        }
        // check against parsed programs
        for (final String current : _exportParser.getLines("/mql/program/@value"))  {
            final String test = current.replaceAll("\\\\$", "").trim();
            Assert.assertTrue(targets.contains(test), "check that program " + test + " must be defined");
            targets.remove(test);
        }
        Assert.assertTrue(targets.isEmpty(), "check that all programs are defined");
    }
}
