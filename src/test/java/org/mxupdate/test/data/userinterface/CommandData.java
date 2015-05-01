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
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * Used to define a command, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class CommandData
    extends AbstractCommandData<CommandData>
{
    /** Command code. */
    private String code;
    /** All users of the command. */
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
     * Defines the {@code #code} of the command.
     *
     * @param _code     command code
     * @return this command instance
     */
    public CommandData setCode(final String _code)
    {
        this.code = _code;
        return this;
    }

    /**
     * Appends a new user to {@link #users}.
     *
     * @param _user     user to add
     * @return this command instance
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
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate command \"${NAME}\" {\n");

        for (final AbstractUserData<?> user : this.users)  {
            strg.append("    user \"").append(AbstractTest.convertUpdate(user.getName())).append("\"\n");
        }
        this.getFlags().appendUpdate("    ", strg);
        this.getValues().appendUpdate("    ", strg);
        this.getSettings().appendUpdate("    ", strg, "\n");
        this.getProperties().appendUpdate("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        if (this.code != null)  {
            strg.append("    code \"\n")
                .append(AbstractTest.convertUpdate(this.code)).append('\n')
                .append("\"\n");
        }
        strg.append("}");

        return strg.toString();
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

            if (this.code != null)  {
                cmd.append(" code \"").append(AbstractTest.convertMql(this.code)).append('\"');
            }

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
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // check symbolic name
        Assert.assertEquals(
                _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        this.getFlags().checkExport(_exportParser, "");
        this.getValues().checkExport(_exportParser, "");
        this.getSettings().checkExport(_exportParser.getLines("/mxUpdate/setting/@value"));
        this.getProperties().checkExport(_exportParser.getLines("/mxUpdate/property/@value"));

        // command code (existing or not existing!)
        if ((this.code != null) && !this.code.isEmpty())  {
            this.checkSingleValue(_exportParser, "code", "code", "\"");
            Assert.assertTrue(
                    _exportParser.getCode().contains("code \"\n" + AbstractTest.convertUpdate(this.code) + "\n\"\n"),
                    "check code 'code \"\n" + AbstractTest.convertUpdate(this.code) + "\n\"\n is defined in " + _exportParser.getCode());
        } else  {
            this.checkNotExistingSingleValue(_exportParser, "code", "code");
        }

        // users
        final Set<String> userDefs = new HashSet<String>(_exportParser.getLines("/mxUpdate/user/@value"));
        for (final AbstractUserData<?> user : this.users)  {
            final String userDefStr = '\"' + AbstractTest.convertUpdate(user.getName()) + '\"';
            Assert.assertTrue(
                    userDefs.contains(userDefStr),
                    "check that user is defined in ci file (have " + userDefStr + ", but found " + userDefs + ")");
            userDefs.remove(userDefStr);
        }
        Assert.assertEquals(userDefs.size(), 0, "check that not too much users are defined (have " + userDefs + ")");
    }
}
