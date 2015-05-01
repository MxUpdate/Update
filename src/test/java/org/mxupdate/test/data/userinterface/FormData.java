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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Used to define a web form, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class FormData
    extends AbstractAdminData<FormData>
{
    /** All fields of this form. */
    private final List<FieldData<FormData>> fields = new ArrayList<FieldData<FormData>>();

    /**
     * Constructor to initialize this form.
     *
     * @param _test     related test implementation (where this form is
     *                  defined)
     * @param _name     name of the form
     */
    public FormData(final AbstractTest _test,
                    final String _name)
    {
        super(_test, AbstractTest.CI.UI_FORM, _name);
    }

    /**
     * Creates a new column / field instance for
     * {@link #fields the list of all columns} in this web form.
     *
     * @param _name     name of the field (could be also <code>null</code>).
     * @return new create field (column) instance
     * @see #fields
     */
    public FieldData<FormData> newField(final String _name)
    {
        final FieldData<FormData> ret = new FieldData<FormData>(this.getTest(), this, _name);
        this.fields.add(ret);
        return ret;
    }

    /**
     * Returns all fields of this web form.
     *
     * @return all fields
     * @see #fields
     */
    public List<FieldData<FormData>> getFields()
    {
        return this.fields;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this form.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate form \"${NAME}\" {\n");

        this.getFlags().append4Update("    ", strg);
        this.getValues().append4Update("    ", strg);
        this.getProperties().append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        for (final FieldData<FormData> field : this.fields)  {
            strg.append(field.ciFile());
        }
        strg.append("}");

        return strg.toString();
    }

    /**
     * Creates a this form with all values.
     *
     * @return this form instance
     * @throws MatrixException if create failed
     */
    @Override()
    public FormData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add form \"" + AbstractTest.convertMql(this.getName()) + "\" web");

            this.append4Create(cmd);
            // append all fields
            for (final FieldData<FormData> field : this.fields)  {
                cmd.append(" field");
                field.append4Create(cmd);
            }

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to form \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }


    /**
     * {@inheritDoc}
     * All users defined within {@link #fields} are created.
     *
     * @see #fields
     */
    @Override()
    public FormData createDependings()
        throws MatrixException
    {
        super.createDependings();

        for (final FieldData<FormData> field : this.fields)  {
            field.createDependings();
        }
        return this;
    }

    /**
     * <p>Checks the export of this web form that all values are correct
     * defined.</p>
     * <p>For web forms, following points must be checked:
     * <ul>
     * <li>{@link #fields web form fields}</li>
     * </ul><p>
     *
     * @param _exportParser     parsed export
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        int idx = 0;
        for (final FieldData<FormData> field: this.fields) {
            field.check4Export(_exportParser, "field[" + (idx++) + "]");
        }
    }
}
