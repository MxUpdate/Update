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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Defines on column / field of a table.
 *
 * @author The MxUpdate Team
 * @param <FORMTABLE>   class of the related table / form
 */
public class FieldData<FORMTABLE extends AbstractAdminData<?>>
    extends AbstractAdminData<FieldData<FORMTABLE>>
{
    /** Related table / form which defines this field. */
    private final FORMTABLE tableForm;

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
        super(_test, null, _name);
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
     * Returns the column / field specific part of the CI update file.
     *
     * @return column / field specific part of the CI update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();

        if (this.tableForm instanceof FormData) {
            strg .append("    field {\n");
        } else {
            strg .append("    column {\n");
        }
        strg.append("        name \"").append(AbstractTest.convertUpdate(this.getName())).append("\"\n");

        this.getValues()   .append4Update("        ", strg);
        this.getSingles()  .append4Update("        ", strg);
        this.getFlags()    .append4Update("        ", strg);
        this.getKeyValues().append4Update("        ", strg);
        this.getDatas()    .append4Update("        ", strg);

        strg.append("    }\n");
        return strg.toString();
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
        _cmd.append(" name \"").append(AbstractTest.convertMql(this.getName())).append('\"');

        super.append4Create(_cmd);
    }

    /**
     * Checks all specific values of an export for a column / field.
     *
     * @param _exportParser     parsed export
     * @param _path             sub path
     */
    public void check4Export(final ExportParser _exportParser,
                             final String _path)
    {
        this.getFlags()     .check4Export(_exportParser, _path);
        this.getValues()    .check4Export(_exportParser, _path);
        this.getSingles()   .check4Export(_exportParser, _path);
        this.getKeyValues() .check4Export(_exportParser, _path);
        this.getDatas()     .check4Export(_exportParser, _path);
    }
}
