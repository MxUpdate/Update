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
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Used to define an expression, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class ExpressionData
    extends AbstractAdminData<ExpressionData>
{
    /**
     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        ExpressionData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /**
     * Initialize this expression data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this expression is
     *                  defined)
     * @param _name     name of the expression
     */
    public ExpressionData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.DM_EXPRESSION, _name, ExpressionData.REQUIRED_EXPORT_VALUES, null);
    }

    /**
     * Returns the TCL update file of this expression data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();

        this.append4CIFileHeader(strg);

        strg.append("mxUpdate expression \"${NAME}\" {\n");

        this.getFlags().append4CIFileValues("    ", strg, "\n");
        this.getValues().appendUpdate("    ", strg, "\n");
        this.getProperties().appendCIFileUpdateFormat("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    /**
     * Create the related expression in MX for this expression data instance.
     *
     * @return this expression data instance
     * @throws MatrixException if create failed
     */
    @Override()
    public ExpressionData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add expression \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.append4Create(cmd);

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to expression \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
        }
        return this;
    }

    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        this.getValues().checkExport(_exportParser);
        this.getFlags().checkExport(_exportParser.getRootLines().get(0), "");
        this.getProperties().checkExport(_exportParser.getLines("/mxUpdate/property/@value"));
    }
}
