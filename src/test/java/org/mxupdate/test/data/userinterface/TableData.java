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

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;

import matrix.util.MatrixException;

/**
 * Used to define a web table, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class TableData
    extends AbstractAdminData<TableData>
{
    /** All fields of this table. */
    private final List<FieldData<TableData>> fields = new ArrayList<>();

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
        super(_test, AbstractTest.CI.UI_TABLE, _name);
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
        final FieldData<TableData> ret = new FieldData<>(this.getTest(), this, _name);
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
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate table \"${NAME}\" {\n");

        this.getFlags()         .append4Update("    ", strg);
        this.getValues()        .append4Update("    ", strg);
        this.getProperties()    .append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        for (final FieldData<TableData> field : this.fields)  {
            strg.append(field.ciFile());
        }
        strg.append("}");

        return strg.toString();
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

            this.getTest().mql(cmd);

            this.getTest().mql(new StringBuilder()
                    .append(";\n")
                    .append("escape add property ").append(this.getSymbolicName())
                    .append(" on program eServiceSchemaVariableMapping.tcl")
                    .append(" to table \"").append(AbstractTest.convertMql(this.getName())).append("\" system"));
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

        for (final FieldData<TableData> field : this.fields)  {
            field.createDependings();
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
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        int idx = 0;
        for (final FieldData<TableData> field: this.fields) {
            field.check4Export(_exportParser, "column[" + (idx++) + "]");
        }
    }
}
