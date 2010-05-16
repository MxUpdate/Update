/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.data.datamodel;

import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.program.MQLProgramData;

/**
 * Used to define a format, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class FormatData
    extends AbstractAdminData<FormatData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        FormatData.REQUIRED_EXPORT_VALUES.put("description", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("version", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("suffix", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("mime", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("type", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("view", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("edit", "");
        FormatData.REQUIRED_EXPORT_VALUES.put("print", "");
    }

    /**
     * Related view program.
     *
     * @see #setViewProgram(MQLProgramData)
     */
    private MQLProgramData viewProgram;

    /**
     * Related edit program.
     *
     * @see #setEditProgram(MQLProgramData)
     */
    private MQLProgramData editProgram;

    /**
     * Related print program.
     *
     * @see #setPrintProgram(MQLProgramData)
     */
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
        super(_test, AbstractTest.CI.DM_FORMAT, _name, FormatData.REQUIRED_EXPORT_VALUES);
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
        final StringBuilder cmd = new StringBuilder();

        this.append4CIFileHeader(cmd);

        cmd.append("mql escape mod format \"${NAME}\"");

        this.append4CIFileValues(cmd);

        if (this.viewProgram != null)  {
            cmd.append(" view \"").append(AbstractTest.convertTcl(this.viewProgram.getName())).append('\"');;
        }
        if (this.editProgram != null)  {
            cmd.append(" edit \"").append(AbstractTest.convertTcl(this.editProgram.getName())).append('\"');;
        }
        if (this.printProgram != null)  {
            cmd.append(" print \"").append(AbstractTest.convertTcl(this.printProgram.getName())).append('\"');;
        }

        // append hidden flag
        if (this.isHidden() != null)  {
            cmd.append(' ');
            if (!this.isHidden())  {
                cmd.append('!');
            }
            cmd.append("hidden");
        }

        return cmd.toString();
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
            } else  {
                cmd.append(" view \"\"");
            }
            if (this.editProgram != null)  {
                cmd.append(" edit \"").append(AbstractTest.convertMql(this.editProgram.getName())).append('\"');;
            } else  {
                cmd.append(" edit \"\"");
            }
            if (this.printProgram != null)  {
                cmd.append(" print \"").append(AbstractTest.convertMql(this.printProgram.getName())).append('\"');;
            } else  {
                cmd.append(" print \"\"");
            }

            // append hidden flag
            if (this.isHidden() != null)  {
                cmd.append(' ');
                if (!this.isHidden())  {
                    cmd.append('!');
                }
                cmd.append("hidden");
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
        super.checkExport(_exportParser);

        this.checkSingleValue(_exportParser, "view program", "view",
                (this.viewProgram != null) ? "\"" + AbstractTest.convertTcl(this.viewProgram.getName()) + "\"" : "\"\"");
        this.checkSingleValue(_exportParser, "edit program", "edit",
                (this.editProgram != null) ? "\"" + AbstractTest.convertTcl(this.editProgram.getName()) + "\"" : "\"\"");
        this.checkSingleValue(_exportParser, "print program", "print",
                (this.printProgram != null) ? "\"" + AbstractTest.convertTcl(this.printProgram.getName()) + "\"" : "\"\"");
    }
}
