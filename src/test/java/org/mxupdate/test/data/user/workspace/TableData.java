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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.AbstractUserData;
import org.mxupdate.test.data.userinterface.FieldData;
import org.testng.Assert;

/**
 * The class is used to define all table objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <USER> class of the related user for which this table is defined
 */
public class TableData<USER extends AbstractUserData<?>>
    extends AbstractVisualWorkspaceObjectData<TableData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
    }

    /**
     * All columns of this user specific table.
     *
     * @see #newField(String)
     * @see #append4Create(StringBuilder)
     * @see #ciFile()
     */
    private final List<FieldData<TableData<USER>>> fields = new ArrayList<FieldData<TableData<USER>>>();

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this table is defined
     * @param _name     name of the table
     */
    public TableData(final AbstractTest _test,
                     final USER _user,
                     final String _name)
    {
        super(_test, "table", _user, _name, TableData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Creates a new column / field instance for
     * {@link #fields the list of all columns} in this user specific table.
     *
     * @param _name     name of the field (could be also <code>null</code>).
     * @return new create field (column) instance
     * @see #fields
     */
    public FieldData<TableData<USER>> newField(final String _name)
    {
        final FieldData<TableData<USER>> ret = new FieldData<TableData<USER>>(this.getTest(), this, _name);
        this.fields.add(ret);
        return ret;
    }

    /**
     * Appends the MQL commands to define the {@link #fields} within a create
     * and in front of the definition the units.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #fields
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        _cmd.append(" units points");
        super.append4Create(_cmd);
        for (final FieldData<TableData<USER>> field : this.fields)  {
            _cmd.append(" column");
            field.append4Create(_cmd);
        }
    }

    /**
     * Returns the CI file appended with all
     * {@link #fields column informations} of this user specific table.
     *
     * @return string content of the CI file
     * @see #fields
     */
    @Override()
    public String ciFile()
    {
        // for tables the unit must be defined! (exact behind the user!)
        final StringBuilder cmd = new StringBuilder()
                .append(super.ciFile().replaceFirst("user \"\\$\\{NAME\\}\"",
                                                    "user \"\\$\\{NAME\\}\" \\\\\n    units points"));

        // and all fields...
        for (final FieldData<TableData<USER>> field : this.fields)  {
            cmd.append(" \\\n    column").append(field.ciFile());
        }
        return cmd.toString();
    }

    /**
     * <p>Checks the export of this user specific table that all values are
     * correct defined.</p>
     * <p>For user specific tables, following points must be checked:
     * <ul>
     * <li>{@link #fields table columns}</li>
     * </ul><p>
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check correct units definition
        this.checkSingleValue(_exportParser, "table", "units", "points");

        // fetch all columns of the table
        final List<ExportParser.Line> columnLines = new ArrayList<ExportParser.Line>();
        // first only the table is the root line...
        for (final ExportParser.Line rootLine : _exportParser.getRootLines())  {
            // loop through all columns
            for (final ExportParser.Line line : rootLine.getChildren())  {
                if ("column".equals(line.getTag()))  {
                    columnLines.add(line);
                }
            }
        }

        // and check all columns
        Assert.assertEquals(columnLines.size(),
                            this.fields.size(),
                            "check that all columns / fields are correct defined");
        final Iterator<ExportParser.Line> columnLinesIter = columnLines.iterator();
        final Iterator<FieldData<TableData<USER>>> fieldsIter = this.fields.iterator();
        while (columnLinesIter.hasNext() && fieldsIter.hasNext())  {
            final FieldData<TableData<USER>> field = fieldsIter.next();
            final ExportParser.Line line = columnLinesIter.next();
            field.checkExport(new ExportParser(field.getName(), _exportParser.getLog(), "mql", line.getValue(), line.getChildren()));
        }
    }
}
