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

package org.mxupdate.test.data.userinterface;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AbstractUserData;

/**
 * Used to define a command, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class CommandData
    extends AbstractCommandData<CommandData>
{
    /**
     * All users of the command.
     *
     * @see #addUser(AbstractUserData)
     * @see #getUsers()
     * @see #create()
     * @see #evalAdds4CheckExport(Set)
     */
    private final Set<AbstractUserData<?>> users = new HashSet<AbstractUserData<?>>();

    /**
     * Constructor to initialize this command.
     *
     * @param _test     related test implementation (where this command is
     *                  defined)
     * @param _name     name of the command
     */
    public CommandData(final AbstractTest _test,
                       final String _name)
    {
        super(_test, AbstractTest.CI.UI_COMMAND, _name);
    }

    /**
     * Appends a new user to {@link #users}.
     *
     * @param _user     user to add
     * @return this command instance
     * @see #users
     */
    public CommandData addUser(final AbstractUserData<?> _user)
    {
        this.users.add(_user);
        return this;
    }

    /**
     * Returns all assigned {@link #users} of this command.
     *
     * @return all assigned users
     * @see #users
     */
    public Set<AbstractUserData<?>> getUsers()
    {
        return this.users;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this command.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder();
        this.append4CIFileHeader(cmd);
        cmd.append("mql escape mod command \"${NAME}\"");

        for (final AbstractUserData<?> user : this.users)  {
            cmd.append(" add user \"")
               .append(AbstractTest.convertTcl(user.getName())).append('\"');
        }
        this.append4CIFileValues(cmd);

        return cmd.toString();
    }

    /**
     * Creates a this command with all values and settings.
     *
     * @return this command instance
     * @throws MatrixException if create failed
     */
    @Override()
    public CommandData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add command \"" + AbstractTest.convertMql(this.getName()) + "\"");

            if (!this.users.isEmpty())  {
                cmd.append(" user ");
                boolean first = true;
                for (final AbstractUserData<?> user : this.users)  {
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append('\"').append(AbstractTest.convertMql(user.getName())).append('\"');
                }
            }
            this.append4Create(cmd);

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to command \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * All assigned {@link #users} are created.
     *
     * @see #users
     */
    @Override()
    public CommandData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create assigned users
        for (final AbstractUserData<?> user : this.users)  {
            user.create();
        }

        return this;
    }

    /**
     * Evaluates all 'adds' in the configuration item file (e.g. add user, add
     * setting, ...).
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #users}
     * @see #users
     */
    @Override()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final AbstractUserData<?> user : this.users)  {
            _needAdds.add("user \"" + AbstractTest.convertTcl(user.getName()) + "\"");
        }
    }
}
