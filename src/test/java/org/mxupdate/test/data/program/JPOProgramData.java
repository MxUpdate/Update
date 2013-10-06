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

package org.mxupdate.test.data.program;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * The class is used to define all JPO program objects used to create / update
 * and to export.
 *
 * @author The MxUpdate Team
 */
public class JPOProgramData
    extends AbstractProgramData<JPOProgramData>
{
    /**
     * Start marker for the update code.
     */
    private static final String MARK_START
            = "################################################################################\n"
            + "# START NEEDED MQL UPDATE FOR THIS JPO PROGRAM                                 #\n"
            + "################################################################################";

    /**
     * End marker for the update code.
     */
    private static final String MARK_END
            = "################################################################################\n"
            + "# END NEEDED MQL UPDATE FOR THIS JPO PROGRAM                                   #\n"
            + "################################################################################";

    /**
     * Initializes this JPO program.
     *
     * @param _test     related test instance
     * @param _name     name of the JPO program
     */
    public JPOProgramData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.PRG_JPO, _name);
    }

    /**
     * Returns the configuration item file name of this JPO. The configuration
     * item file name of a JPO excludes the package name and has the suffix of
     * <code>_mxJPO.java</code>.
     *
     * @return file name of a JPO
     */
    @Override()
    public String getCIFileName()
    {
        final String ciFileName = super.getCIFileName();

        return ciFileName.replaceAll("\\.tcl$", "")
                         .replaceAll(".*\\.", "")
                        + "_mxJPO.java";
    }

    /**
     * The related configuration item file is the {@link #code} of the program.
     *
     *  @return {@link #code} of the program
     *  @see #code
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
           .append(JPOProgramData.MARK_START)
           .append("\nmql escape mod program ${NAME} ");
        this.append4CIFileValues(cmd);
        cmd.append("\n")
           .append(JPOProgramData.MARK_END)
           .append("\n");

        final StringBuilder ciFile = new StringBuilder();
        for (final String line : cmd.toString().split("\n"))  {
            ciFile.append("//").append(line).append('\n');
        }
        ciFile.append('\n').append(this.getCode());

        return ciFile.toString();
    }

    /**
     * Creates this JPO program within MX.
     *
     * @return this JPO program instance
     * @throws MatrixException if create of the JPO program failed
     */
    @Override()
    public JPOProgramData create() throws MatrixException
    {
        if (!this.isCreated())  {
            final StringBuilder cmd = new StringBuilder()
                .append("escape add program \"").append(AbstractTest.convertMql(this.getName()))
                .append("\" java ")
                .append("code \"").append(AbstractTest.convertMql(this.getCode().replaceAll("^package .*;", ""))).append("\"");
            this.append4Create(cmd);
            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to program \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.setCreated(true);

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * The define Java package within the source code is checked.
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check if package definition exists / not exists
        Assert.assertEquals((_exportParser.getOrigCode().indexOf("package") >= 0),
                            (this.getCode().indexOf("package") >= 0),
                            "checks that JPO code has correct package definition");
    }
}
