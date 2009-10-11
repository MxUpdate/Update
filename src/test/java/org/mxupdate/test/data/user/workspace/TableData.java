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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * The class is used to define all table objects related to users used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <USER> class of the related user for which this table is defined
 */
public class TableData<USER extends AbstractUserData<?>>
    extends AbstractVisualWorkspaceObjectData<TableData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
    }

    /**
     * All columns of this user specific table.
     *
     * @see #newField(String)
     * @see #append4Create(StringBuilder)
     * @see #ciFile()
     */
    private final List<FieldData> fields = new ArrayList<FieldData>();

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
    public FieldData newField(final String _name)
    {
        final FieldData ret = new FieldData(this.getTest(), _name);
        this.fields.add(ret);
        return ret;
    }

    /**
     * Appends the MQL commands to define the {@link #fields}Êwithin a create
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
        for (final FieldData field : this.fields)  {
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
        for (final FieldData field : this.fields)  {
            cmd.append(field.ciFile());
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
        final Iterator<FieldData> fieldsIter = this.fields.iterator();
        while (columnLinesIter.hasNext() && fieldsIter.hasNext())  {
            final FieldData field = fieldsIter.next();
            final ExportParser.Line line = columnLinesIter.next();
            field.checkExport(new ExportParser(field.getName(), "mql", line.getValue(), line.getChildren()));
        }
    }

    /**
     * Defines on column / field of a table.
     */
    public class FieldData
        extends AbstractData<TableData<USER>.FieldData>
    {
        /**
         * Sort type of this column / field.
         */
        private String sortType;

        /**
         * Height of this column / field.
         *
         * @see #setSize(Double,Double)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Double height;

        /**
         * Width of this column / field.
         *
         * @see #setSize(Double,Double)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Double width;

        /**
         * Minimum height of this column / field.
         *
         * @see #setMinSize(Double,Double)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Double minHeight;

        /**
         * Minimum width of this column / field.
         *
         * @see #setMinSize(Double,Double)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Double minWidth;

        /**
         * Must be <i>true</i> if the column / field has auto height.
         *
         * @see #setAutoHeight(Boolean)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Boolean autoHeight;

        /**
         * Must be <i>true</i> if the column / field has auto width.
         *
         * @see #setAutoWidth(Boolean)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Boolean autoWidth;

        /**
         * The scale of a column is a natural number, but for test purposes the
         * scale is here a double.
         *
         * @see #setScale(Double)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Double scale;

        /**
         * Must be <i>true</i> if the column / field id editable.
         *
         * @see #setEditable(Boolean)
         * @see #ciFile()
         * @see #append4Create(StringBuilder)
         * @see #checkExport(ExportParser)
         */
        private Boolean editable;

        /**
         * Default constructor.
         *
         * @param _test     related test instance
         * @param _name     name of the field
         */
        public FieldData(final AbstractTest _test,
                         final String _name)
        {
            super(_test, null, _name, null);
        }

        /**
         * Returns parent table in which this field / column is defined.
         *
         * @return parent table
         */
        public TableData<USER> getTable()
        {
            return TableData.this;
        }

        /**
         * Defines the {@link #sortType sort type} of this column (only
         * possible for tables).
         *
         * @param _sortType new sort type
         * @return this field instance
         */
        public FieldData setSortType(final String _sortType)
        {
            this.sortType = _sortType;
            return this;
        }

        /**
         * Defines the size ({@link #width} and {@link #height}) of this
         * column / field.
         *
         * @param _width     width
         * @param _height    height
         * @return this field instance
         * @see #height
         * @see #width
         */
        public FieldData setSize(final Double _width,
                                 final Double _height)
        {
            this.height = _height;
            this.width = _width;
            return this;
        }

        /**
         * Defines the minimum size ({@link #minWidth minimum width} and
         * {@link #minHeight minimum height}) of this column / field.
         *
         * @param _minWidth     minimum width
         * @param _minHeight    minimum height
         * @return this field instance
         * @see #minHeight
         * @see #minWidth
         */
        public FieldData setMinSize(final Double _minWidth,
                                    final Double _minHeight)
        {
            this.minHeight = _minHeight;
            this.minWidth = _minWidth;
            return this;
        }

        /**
         * Defines if the height of the column is auto sized.
         *
         * @param _autoHeight   <i>true</i> if auto sized; otherwise
         *                      <i>false</i>
         * @return this field instance
         */
        public FieldData setAutoHeight(final Boolean _autoHeight)
        {
            this.autoHeight = _autoHeight;
            return this;
        }

        /**
         * Defines if the width of the column is auto sized.
         *
         * @param _autoWidth    <i>true</i> if auto sized; otherwise
         *                      <i>false</i>
         * @return this field instance
         */
        public FieldData setAutoWidth(final Boolean _autoWidth)
        {
            this.autoWidth = _autoWidth;
            return this;
        }

        /**
         * Defines the {@link #scale} of the column.
         *
         * @param _scale    scale value
         * @return this field instance
         */
        public FieldData setScale(final Double _scale)
        {
            this.scale = _scale;
            return this;
        }

        /**
         * Defines if the column / field is {@link #editable}.
         *
         * @param _editable     <i>true</i> if editable; otherwise <i>false</i>
         * @return this field instance
         */
        public FieldData setEditable(final Boolean _editable)
        {
            this.editable = _editable;
            return this;
        }

        /**
         * Returns the column / field specific part of the CI update file.
         *
         * @return column / field specific part of the CI update file
         */
        @Override()
        public String ciFile()
        {
            final StringBuilder cmd = new StringBuilder().append(" \\\n    column");

            if (this.getName() != null)  {
                cmd.append(" name \"").append(AbstractTest.convertMql(this.getName())).append("\"");
            }

            // sort type
            if (this.sortType != null)  {
                cmd.append(" sorttype ").append(this.sortType);
            }

            // size
            if ((this.height != null) && (this.width != null))  {
                cmd.append(" size ").append(this.width.doubleValue()).append(' ').append(this.height.doubleValue());
            }

            // minimum size
            if ((this.minHeight != null) && (this.minWidth != null))  {
                cmd.append(" minsize ").append(this.minWidth.doubleValue()).append(' ').append(this.minHeight.doubleValue());
            }

            // auto height
            if (this.autoHeight != null)  {
                cmd.append(" autoheight ").append(this.autoHeight.booleanValue());
            }

            // auto width
            if (this.autoWidth != null)  {
                cmd.append(" autowidth ").append(this.autoWidth.booleanValue());
            }

            // scale
            if (this.scale != null)  {
                cmd.append(" scale ").append(this.scale.doubleValue());
            }

            // editable flag
            if (this.editable != null)  {
                cmd.append(" edit ").append(this.editable.booleanValue());
            }

            this.append4CIFileValues(cmd);

            return cmd.toString();
        }

        /**
         * Throws always an error because fields could not be created manually
         * and without related form / table.
         *
         * @return does not happen, because always an error is thrown
         * @throws MatrixException always
         */
        @Override()
        public FieldData create()
            throws MatrixException
        {
            throw new MatrixException("field data could not be created!");
        }

        /**
         * Appends all column / field specific values for a create.
         *
         * @param _cmd      string builder with the commands used to append
         * @throws MatrixException if expressions could not be appended
         */
        @Override()
        protected void append4Create(final StringBuilder _cmd)
            throws MatrixException
        {
            _cmd.append(" column");

            // column name
            if (this.getName() != null)  {
                _cmd.append(" name \"").append(AbstractTest.convertMql(this.getName())).append("\"");
            }

            // hidden flag
            if (this.isHidden())  {
                _cmd.append(" hidden");
            }

            // sort type
            if (this.sortType != null)  {
                _cmd.append(" sorttype ").append(this.sortType);
            }

            // size
            if ((this.height != null) && (this.width != null))  {
                _cmd.append(" size ").append(this.width.doubleValue()).append(' ').append(this.height.doubleValue());
            }

            // minimum size
            if ((this.minHeight != null) && (this.minWidth != null))  {
                _cmd.append(" minsize ").append(this.minWidth.doubleValue()).append(' ').append(this.minHeight.doubleValue());
            }

            // auto height
            if (this.autoHeight != null)  {
                _cmd.append(" autoheight ").append(this.autoHeight.booleanValue());
            }

            // auto width
            if (this.autoWidth != null)  {
                _cmd.append(" autowidth ").append(this.autoWidth.booleanValue());
            }

            // scale
            if (this.scale != null)  {
                _cmd.append(" scale ").append(this.scale.doubleValue());
            }

            // editable flag
            if (this.editable != null)  {
                _cmd.append(" edit ").append(this.editable.booleanValue());
            }

            super.append4Create(_cmd);
        }

        /**
         * Checks all specific values of an export for a column / field.
         *
         * @param _exportParser     export parser
         * @throws MatrixException  if test failed
         */
        @Override()
        public void checkExport(final ExportParser _exportParser)
            throws MatrixException
        {
            super.checkExport(_exportParser);

            // sort type (default value is none and must be ignored...)
            this.checkSingleValue(_exportParser, "column", "sorttype", ((this.sortType != null) && !this.sortType.equals("none")) ? this.sortType : null);

            // size
            this.checkSingleValue(
                    _exportParser,
                    "column",
                    "size",
                    ((this.width != null) && (this.height != null)
                                    && ((this.width.doubleValue() != 1.0) || (this.height.doubleValue() != 1.0)))
                            ? String.valueOf(this.width.doubleValue()) + " " + String.valueOf(this.height.doubleValue())
                            : null);

            // minimum size
            this.checkSingleValue(
                    _exportParser,
                    "column",
                    "minsize",
                    ((this.minWidth != null) && (this.minHeight != null)
                                    && ((this.minWidth.doubleValue() != 0.0) || (this.minHeight.doubleValue() != 0.0)))
                            ? String.valueOf(this.minWidth.doubleValue()) + " " + String.valueOf(this.minHeight.doubleValue())
                            : null);

            // auto height
            this.checkSingleValue(_exportParser, "column", "autoheight", ((this.autoHeight != null) && this.autoHeight) ? "true" : null);

            // auto width
            this.checkSingleValue(_exportParser, "column", "autowidth", ((this.autoWidth != null) && this.autoWidth) ? "true" : null);

            // scale
            this.checkSingleValue(_exportParser, "column", "scale", (this.scale != null) ? String.valueOf(this.scale.longValue()) : null);

            // editable flag
            this.checkSingleValue(_exportParser, "column", "edit", ((this.editable != null) && this.editable)? "true" : null);
        }
    }
}
