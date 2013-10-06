/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data.userinterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * Defines on column / field of a table.
 *
 * @author The MxUpdate Team
 * @param <FORMTABLE>   class of the related table / form
 */
public class FieldData<FORMTABLE extends AbstractAdminData<?>>
    extends AbstractAdminData<FieldData<FORMTABLE>>
{
    /**
     * Related table / form which defines this field.
     *
     * @see #getFormTable()
     */
    private final FORMTABLE tableForm;

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
     * All settings related to this field.
     *
     * @see #setSetting(String, String)
     * @see #ciFile()
     * @see #append4Create(StringBuilder)
     */
    private final Map<String,String> settings = new HashMap<String,String>();

    /**
     * All users related to this field.
     *
     * @see #addUser(AbstractUserData)
     * @see #getUsers()
     * @see #ciFile()
     * @see #append4Create(StringBuilder)
     */
    private final Set<AbstractUserData<?>> users = new HashSet<AbstractUserData<?>>();

    /**
     * Default constructor.
     *
     * @param _test         related test instance
     * @param _tableForm    related table / form which uses this field
     * @param _name         name of the field
     */
    public FieldData(final AbstractTest _test,
                     final FORMTABLE _tableForm,
                     final String _name)
    {
        super(_test, null, _name, null, null);
        this.tableForm = _tableForm;
    }

    /**
     * Returns parent table in which this field / column is defined.
     *
     * @return parent table
     */
    public FORMTABLE getFormTable()
    {
        return this.tableForm;
    }

    /**
     * Defines the {@link #sortType sort type} of this column (only
     * possible for tables).
     *
     * @param _sortType new sort type
     * @return this field instance
     */
    public FieldData<FORMTABLE> setSortType(final String _sortType)
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
    public FieldData<FORMTABLE> setSize(final Double _width,
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
    public FieldData<FORMTABLE> setMinSize(final Double _minWidth,
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
    public FieldData<FORMTABLE> setAutoHeight(final Boolean _autoHeight)
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
    public FieldData<FORMTABLE> setAutoWidth(final Boolean _autoWidth)
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
    public FieldData<FORMTABLE> setScale(final Double _scale)
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
    public FieldData<FORMTABLE> setEditable(final Boolean _editable)
    {
        this.editable = _editable;
        return this;
    }

    /**
     * Defines a new setting for this field.
     *
     * @param _key      key of the setting
     * @param _value    value of the setting
     * @return this field instance
     * @see #settings
     */
    public FieldData<FORMTABLE> setSetting(final String _key,
                                           final String _value)
    {
        this.settings.put(_key, _value);
        return this;
    }

    /**
     * Assigns a new user.
     *
     * @param _user     user to assign
     * @return this field instance
     * @see #users
     */
    public FieldData<FORMTABLE> addUser(final AbstractUserData<?> _user)
    {
        this.users.add(_user);
        return this;
    }

    /**
     * Returns all assigned users of this field.
     *
     * @return all assign users
     * @see #users
     */
    public Set<AbstractUserData<?>> getUsers()
    {
        return this.users;
    }

    /**
     * Returns the column / field specific part of the CI update file.
     *
     * @return column / field specific part of the CI update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder();

        if (this.getName() != null)  {
            cmd.append(" name \"").append(AbstractTest.convertTcl(this.getName())).append("\"");
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
        // setting
        for (final Map.Entry<String,String> setting : this.settings.entrySet())  {
            cmd.append(" setting \"").append(AbstractTest.convertTcl(setting.getKey()))
               .append("\" \"").append(AbstractTest.convertTcl(setting.getValue())).append('\"');
        }
        // users
        for (final AbstractUserData<?> user : this.users)  {
            cmd.append(" user \"").append(AbstractTest.convertTcl(user.getName())).append('\"');
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
    public FieldData<FORMTABLE> create()
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
    public void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        // column name
        if (this.getName() != null)  {
            _cmd.append(" name \"").append(AbstractTest.convertMql(this.getName())).append("\"");
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
        // setting
        for (final Map.Entry<String,String> setting : this.settings.entrySet())  {
            _cmd.append(" setting \"").append(AbstractTest.convertMql(setting.getKey()))
                .append("\" \"").append(AbstractTest.convertMql(setting.getValue())).append('\"');
        }
        // users
        for (final AbstractUserData<?> user : this.users)  {
            user.create();
            _cmd.append(" user \"").append(AbstractTest.convertMql(user.getName())).append('\"');
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
        this.checkSingleValue(_exportParser, "column / field", "sorttype", ((this.sortType != null) && !this.sortType.equals("none")) ? this.sortType : null);

        // hidden flag
        this.checkValueExists(_exportParser, "column / field", "hidden", this.getFlag("hidden") != null ? this.getFlag("hidden") : false);

        // size
        this.checkSingleValue(
                _exportParser,
                "column / field",
                "size",
                ((this.width != null) && (this.height != null)
                                && ((this.width.doubleValue() != 1.0) || (this.height.doubleValue() != 1.0)))
                        ? String.valueOf(this.width.doubleValue()) + " " + String.valueOf(this.height.doubleValue())
                        : null);

        // minimum size
        this.checkSingleValue(
                _exportParser,
                "column / field",
                "minsize",
                ((this.minWidth != null) && (this.minHeight != null)
                                && ((this.minWidth.doubleValue() != 0.0) || (this.minHeight.doubleValue() != 0.0)))
                        ? String.valueOf(this.minWidth.doubleValue()) + " " + String.valueOf(this.minHeight.doubleValue())
                        : null);

        // auto height
        this.checkSingleValue(_exportParser, "column / field", "autoheight", ((this.autoHeight != null) && this.autoHeight) ? "true" : null);

        // auto width
        this.checkSingleValue(_exportParser, "column / field", "autowidth", ((this.autoWidth != null) && this.autoWidth) ? "true" : null);

        // scale
        this.checkSingleValue(_exportParser, "column / field", "scale", (this.scale != null) ? String.valueOf(this.scale.longValue()) : null);

        // editable flag
        this.checkSingleValue(_exportParser, "column / field", "edit", ((this.editable != null) && this.editable)? "true" : null);

        // settings
        final Set<String> curSettings = new HashSet<String>(_exportParser.getLines("/mql/setting/@value"));
        for (final Map.Entry<String,String> setting : this.settings.entrySet())  {
            final String key = new StringBuilder()
                    .append('\"').append(AbstractTest.convertTcl(setting.getKey()))
                    .append("\" \"").append(AbstractTest.convertTcl(setting.getValue())).append('\"')
                    .toString();
            Assert.assertTrue(curSettings.contains(key),
                              "check setting " + setting + " is defined");
            curSettings.remove(key);
        }
        Assert.assertTrue(curSettings.isEmpty(),
                          "check that all settings are defined (have " + curSettings + ")");

        // users
        final Set<String> curUsers = new HashSet<String>(_exportParser.getLines("/mql/user/@value"));
        for (final AbstractUserData<?> user : this.users)  {
            final String key = new StringBuilder()
                    .append('\"').append(AbstractTest.convertTcl(user.getName())).append('\"')
                    .toString();
            Assert.assertTrue(curUsers.contains(key),
                              "check user " + key + " is defined");
            curUsers.remove(key);
        }
        Assert.assertTrue(curUsers.isEmpty(),
                          "check that all users are defined (have " + curUsers + ")");
    }
}
