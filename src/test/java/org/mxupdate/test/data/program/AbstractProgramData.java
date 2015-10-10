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

package org.mxupdate.test.data.program;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.data.AbstractAdminData;

import matrix.util.MatrixException;

/**
 * The class is used to define all program objects used to create / update and
 * to export.
 *
 * @author The MxUpdate Team
 * @param <T>   defines the class which is derived from this class
 */
public abstract class AbstractProgramData<DATA extends AbstractProgramData<?>>
    extends AbstractAdminData<DATA>
{
    /**
     * Initialize the values for program objects.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the program
     */
    protected AbstractProgramData(final AbstractTest _test,
                                  final CI _ci,
                                  final String _name)
    {
        super(_test, _ci, _name);
    }

    /**
     * Creates this program.
     *
     * @return this admin data instance
     * @throws MatrixException if create failed
     */
    @Override
    @SuppressWarnings("unchecked")
    public DATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.createDependings();

            final String kind = this.getSingles().remove("kind");

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add " + this.getCI().getMxType() + " \"" + AbstractTest.convertMql(this.getName()) + "\"");
            if (kind != null)  {
                cmd.append(" ").append(kind);
            }

            this.append4Create(cmd);

            this.getTest().mql(cmd);

            this.getTest().mql(new  StringBuilder()
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to " + this.getCI().getMxType() + " \"").append(AbstractTest.convertMql(this.getName())).append("\""));

            this.setCreated(true);

            if (kind != null)  {
                this.setSingle("kind", kind);
            }
        }

        return (DATA) this;
    }
}
