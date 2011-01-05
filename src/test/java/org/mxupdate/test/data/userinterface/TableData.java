/*
 * Copyright 2008-2011 The MxUpdate Team
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

package org.mxupdate.test.data.userinterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * Used to define a web table, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class TableData
    extends AbstractAdminData<TableData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        TableData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /**
     * All fields of this table.
     *
     * @see #newField(String)
     * @see #append4Create(StringBuilder)
     * @see #ciFile()
     */
    private final List<FieldData<TableData>> fields = new ArrayList<FieldData<TableData>>();

    /**
     * Constructor to initialize this table.
     *
     * @param _test     related test implementation (where this table is
     *                  defined)
     * @param _name     name of the table
     */
    public TableData(final AbstractTest _test,
                     final String _name)
    {
        super(_test, AbstractTest.CI.UI_TABLE, _name, TableData.REQUIRED_EXPORT_VALUES, null);
    }

    /**
     * Creates a new column / field instance for
     * {@link #fields the list of all columns} in this web table.
     *
     * @param _name     name of the field (could be also <code>null</code>).
     * @return new create field (column) instance
     * @see #fields
     */
    public FieldData<TableData> newField(final String _name)
    {
        final FieldData<TableData> ret = new FieldData<TableData>(this.getTest(), this, _name);
        this.fields.add(ret);
        return ret;
    }

    /**
     * Returns all fields of this web table.
     *
     * @return all fields
     * @see #fields
     */
    public List<FieldData<TableData>> getFields()
    {
        return this.fields;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this table.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder();
        this.append4CIFileHeader(cmd);
        cmd.append("mql escape mod table \"${NAME}\" system");

        this.append4CIFileValues(cmd);
        // and all fields...
        for (final FieldData<TableData> field : this.fields)  {
            cmd.append(" \\\n    column").append(field.ciFile());
        }
        return cmd.toString();
    }

    /**
     * Creates a this table with all values.
     *
     * @return this table instance
     * @throws MatrixException if create failed
     */
    @Override()
    public TableData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add table \"" + AbstractTest.convertMql(this.getName()) + "\" system");

            this.append4Create(cmd);
            // append all fields
            for (final FieldData<TableData> field : this.fields)  {
                cmd.append(" column");
                field.append4Create(cmd);
            }

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to table \"").append(AbstractTest.convertMql(this.getName())).append("\" system");

            this.getTest().mql(cmd);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * The used users within the fields are created.
     *
     * @see #fields
     */
    @Override()
    public TableData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create users
        for (final FieldData<TableData> field : this.fields)  {
            for (final AbstractUserData<?> user : field.getUsers())  {
                user.create();
            }
        }

        return this;
    }

    /**
     * <p>Checks the export of this web table that all values are correct
     * defined.</p>
     * <p>For web tables, following points must be checked:
     * <ul>
     * <li>{@link #fields web table fields}</li>
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
        final Iterator<FieldData<TableData>> fieldsIter = this.fields.iterator();
        while (columnLinesIter.hasNext() && fieldsIter.hasNext())  {
            final FieldData<TableData> field = fieldsIter.next();
            final ExportParser.Line line = columnLinesIter.next();
            field.checkExport(new ExportParser(field.getName(), _exportParser.getLog(), "mql", line.getValue(), line.getChildren()));
        }
    }
}
