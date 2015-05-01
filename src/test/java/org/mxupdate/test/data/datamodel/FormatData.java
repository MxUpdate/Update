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

package org.mxupdate.test.data.datamodel;

import java.util.HashMap;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.util.Version;

/**
 * Used to define a format, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class FormatData
    extends AbstractAdminData<FormatData>
{
    /** Related view program (in pre-{@link Version#V6R2014x}). */
    private MQLProgramData viewProgram;
    /** Related edit program (in pre-{@link Version#V6R2014x}). */
    private MQLProgramData editProgram;
    /** Related print program (in pre-{@link Version#V6R2014x}). */
    private MQLProgramData printProgram;

    /**
     * Initialize this format data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this format is
     *                  defined)
     * @param _name     name of the format
     */
    public FormatData(final AbstractTest _test,
                      final String _name)
    {
        super(_test, AbstractTest.CI.DM_FORMAT, _name,
                new HashMap<String,Object>() {
                    private static final long serialVersionUID = 1L;
                    {
                        this.put("description", "");
                        this.put("version", "");
                        this.put("suffix", "");
                        this.put("mime", "");
                        this.put("type", "");
                        if (_test.getVersion().max(Version.V6R2013x))  {
                            this.put("view", "");
                            this.put("edit", "");
                            this.put("print", "");
                        }
                    }
                },
                null);
    }

    /**
     * Defines the view program for this format.
     *
     * @param _viewProgram  new view program
     * @return this instance
     * @see #viewProgram
     */
    public FormatData setViewProgram(final MQLProgramData _viewProgram)
    {
        this.viewProgram = _viewProgram;
        return this;
    }

    /**
     * Returns the view program of the format.
     *
     * @return view program
     */
    public MQLProgramData getViewProgram()
    {
        return this.viewProgram;
    }

    /**
     * Defines the edit program for this format.
     *
     * @param _editProgram  new view program
     * @return this instance
     * @see #editProgram
     */
    public FormatData setEditProgram(final MQLProgramData _editProgram)
    {
        this.editProgram = _editProgram;
        return this;
    }

    /**
     * Returns the edit program of the format.
     *
     * @return edit program
     */
    public MQLProgramData getEditProgram()
    {
        return this.editProgram;
    }

    /**
     * Defines the print program for this format.
     *
     * @param _printProgram  new view program
     * @return this instance
     * @see #printProgram
     */
    public FormatData setPrintProgram(final MQLProgramData _printProgram)
    {
        this.printProgram = _printProgram;
        return this;
    }

    /**
     * Returns the print program of the format.
     *
     * @return print program
     */
    public MQLProgramData getPrintProgram()
    {
        return this.printProgram;
    }

    /**
     * Returns the TCL update file of this format data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();

        this.append4CIFileHeader(strg);

        strg.append("mxUpdate format \"${NAME}\" {\n");

        // append flags
        this.getFlags().appendUpdate("  ", strg);
        // append values
        this.getValues().appendUpdate("  ", strg);

        if (this.viewProgram != null)  {
            strg.append(" view \"").append(AbstractTest.convertUpdate(this.viewProgram.getName())).append("\"\n");
        }
        if (this.editProgram != null)  {
            strg.append(" edit \"").append(AbstractTest.convertUpdate(this.editProgram.getName())).append("\"\n");
        }
        if (this.printProgram != null)  {
            strg.append(" print \"").append(AbstractTest.convertUpdate(this.printProgram.getName())).append("\"\n");
        }

        // append properties
        this.getProperties().appendUpdate("  ", strg);

        strg.append("}");

        return strg.toString();
    }

    /**
     * Create the related format in MX for this format data instance.
     *
     * @return this format data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public FormatData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add format \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.append4Create(cmd);

            if (this.viewProgram != null)  {
                cmd.append(" view \"").append(AbstractTest.convertMql(this.viewProgram.getName())).append('\"');;
            }
            if (this.editProgram != null)  {
                cmd.append(" edit \"").append(AbstractTest.convertMql(this.editProgram.getName())).append('\"');;
            }
            if (this.printProgram != null)  {
                cmd.append(" print \"").append(AbstractTest.convertMql(this.printProgram.getName())).append('\"');;
            }

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to format \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * Create the depending {@link #viewProgram view},
     * {@link #editProgram edit} and {@link #printProgram print} program.
     *
     * @see #viewProgram
     * @see #editProgram
     * @see #printProgram
     */
    @Override()
    public FormatData createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create programs
        if (this.viewProgram != null)  {
            this.viewProgram.create();
        }
        if (this.editProgram != null)  {
            this.editProgram.create();
        }
        if (this.printProgram != null)  {
            this.printProgram.create();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     * Also the {@link #viewProgram}, {@link #editProgram} and
     * {@link #printProgram} are checked.
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // check for defined values
        this.getValues().checkExport(_exportParser, "");
        // check for defined flags
        this.getFlags().checkExport(_exportParser, "");
        // check for properties
        this.getProperties().checkExport(_exportParser.getLines("/mxUpdate/property/@value"));

        if (this.getTest().getVersion().max(Version.V6R2013x))  {
            this.checkSingleValue(_exportParser, "view program", "view",
                    (this.viewProgram != null) ? "\"" + AbstractTest.convertUpdate(this.viewProgram.getName()) + "\"" : "\"\"");
            this.checkSingleValue(_exportParser, "edit program", "edit",
                    (this.editProgram != null) ? "\"" + AbstractTest.convertUpdate(this.editProgram.getName()) + "\"" : "\"\"");
            this.checkSingleValue(_exportParser, "print program", "print",
                    (this.printProgram != null) ? "\"" + AbstractTest.convertUpdate(this.printProgram.getName()) + "\"" : "\"\"");
        }
    }
}
