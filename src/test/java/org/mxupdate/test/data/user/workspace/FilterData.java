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
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * The class is used to define all filter objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <USER> class of the related user for which this filter is defined
 */
public class FilterData<USER extends AbstractUserData<?>>
    extends AbstractVisualQueryWorkspaceObjectData<FilterData<USER>,USER>
{
    /**
     * Defines the direction of the connections to which the filter applies.
     */
    private Direction direction = Direction.BOTH;

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this filter is defined
     * @param _name     name of the filter
     */
    public FilterData(final AbstractTest _test,
                      final USER _user,
                      final String _name)
    {
        super(_test, "filter", _user, _name);
        this.setValue("appliesto", "relationship");
    }

    /**
     * Defines the direction of the connections this filter applies.
     *
     * @param _direction    direction of the connections
     * @return this filter data instance
     * @see #direction
     */
    public FilterData<USER> setDirection(final Direction _direction)
    {
        this.direction = _direction;
        return this;
    }

    /**
     * Returns the part of the CI file to create this filter of an user.
     *
     * @return part of the CI file to create this filter of an user
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder().append(super.ciFile());

        // active flag
        if (this.direction != null)  {
            cmd.append(" \\\n    ").append(this.direction.toString().toLowerCase());
        }

        return cmd.toString();
    }

    /**
     * Appends the MQL commands to define the {@link #direction} within a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #values
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // direction
        if (this.direction != null)  {
            _cmd.append(" ").append(this.direction.toString().toLowerCase());
        }
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * The {@link #active active flag} is checked.
     *
     * @param _exportParser     parsed export
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        // check direction
        final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
        switch (this.direction)  {
            case TO:
                Assert.assertTrue(main.contains("to") || main.contains("to \\"),        "check to direction");
                Assert.assertFalse(main.contains("from") || main.contains("from \\"),   "check no from direction");
                Assert.assertFalse(main.contains("both") || main.contains("both \\"),   "check no both direction");
                break;
            case FROM:
                Assert.assertFalse(main.contains("to") || main.contains("to \\"),       "check no to direction");
                Assert.assertTrue(main.contains("from") || main.contains("from \\"),    "check from direction");
                Assert.assertFalse(main.contains("both") || main.contains("both \\"),   "check no both direction");
                break;
            case BOTH:
            default:
                Assert.assertFalse(main.contains("to") || main.contains("to \\"),       "check no to direction");
                Assert.assertFalse(main.contains("from") || main.contains("from \\"),   "check no from direction");
                Assert.assertTrue(main.contains("both") || main.contains("both \\"),    "check both direction");
        }
    }

    /**
     * Enumeration for the direction of connection.
     */
    public enum Direction
    {
        /** To direction. */
        TO,
        /** From direction. */
        FROM,
        /** Both (to and from) directions. */
        BOTH;
    }
}
