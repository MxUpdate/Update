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
 * Used to define a web form, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class FormData
    extends AbstractAdminData<FormData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>(3);
    static  {
        FormData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /**
     * All fields of this form.
     *
     * @see #newField(String)
     * @see #append4Create(StringBuilder)
     * @see #ciFile()
     */
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
        super(_test, AbstractTest.CI.UI_FORM, _name, FormData.REQUIRED_EXPORT_VALUES, null);
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
        final StringBuilder cmd = new StringBuilder();
        this.append4CIFileHeader(cmd);
        cmd.append("mql escape mod form \"${NAME}\"");

        this.append4CIFileValues(cmd);
        // and all fields...
        for (final FieldData<FormData> field : this.fields)  {
            cmd.append(" \\\n    field").append(field.ciFile());
        }
        return cmd.toString();
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

        // create users
        for (final FieldData<FormData> field : this.fields)  {
            for (final AbstractUserData<?> user : field.getUsers())  {
                user.create();
            }
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
                if ("field".equals(line.getTag()))  {
                    columnLines.add(line);
                }
            }
        }

        // and check all columns
        Assert.assertEquals(columnLines.size(),
                            this.fields.size(),
                            "check that all columns / fields are correct defined");
        final Iterator<ExportParser.Line> columnLinesIter = columnLines.iterator();
        final Iterator<FieldData<FormData>> fieldsIter = this.fields.iterator();
        while (columnLinesIter.hasNext() && fieldsIter.hasNext())  {
            final FieldData<FormData> field = fieldsIter.next();
            final ExportParser.Line line = columnLinesIter.next();
            field.checkExport(new ExportParser(field.getName(), _exportParser.getLog(), "mql", line.getValue(), line.getChildren()));
        }
    }
}
