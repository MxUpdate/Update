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
 * @param <USER> class of the related user for which this tool set is defined
 */
public class ToolSetData<USER extends AbstractUserData<?>>
    extends AbstractVisualWorkspaceObjectData<ToolSetData<USER>,USER>
{
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
        super(_test, "toolset", _user, _name);
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
     * {@inheritDoc}
     * Creates all {@link #programs}.
     *
     * @see #programs
     */
    @Override()
    public ToolSetData<USER> createDependings()
        throws MatrixException
    {
        super.createDependings();

        for (final AbstractProgramData<?> prog : this.programs)  {
            prog.create();
        }

        return this;
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
