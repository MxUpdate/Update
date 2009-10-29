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

package org.mxupdate.test.data.program;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * The class is used to define all page program objects used to create / update
 * and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class PageData
    extends AbstractAdminData<PageData>
{
    /**
     * Related code of this program.
     *
     * @see #setCode(String)
     * @see #getCode()
     */
    private String code = "";

    /**
     * Initializes this MQL program.
     *
     * @param _test     related test instance
     * @param _name     name of the MQL program
     */
    public PageData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.PRG_PAGE, _name, null);
    }

    /**
     * Defines the {@link #code} for this page.
     *
     * @param _code     new code of this page
     * @return this page instance
     * @see #code
     */
    public PageData setCode(final String _code)
    {
        this.code = _code;
        return this;
    }

    /**
     * Returns the {@link #code} of this page.
     *
     * @return related {@link #code} of this page
     * @see #code
     */
    public String getCode()
    {
        return this.code;
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
        return this.code;
    }

    /**
     * Creates this MQL program within MX.
     *
     * @return this MQL program instance
     * @throws MatrixException if create of the MQL program failed
     */
    @Override()
    public PageData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);
            this.getTest().mql("escape add page \"" + AbstractTest.convertMql(this.getName()) + "\" "
                    + "content \"" + AbstractTest.convertMql(this.code) + "\"");
        }
        return this;
    }

    /**
     * Returns the configuration item file name of this MQL program. The
     * configuration item file name of a MQL program excludes the
     * <code>.tcl</code> extension.
     *
     * @return file name of a JPO
     */
    @Override()
    public String getCIFileName()
    {
        final String ciFileName = super.getCIFileName();
        return ciFileName.replaceAll("\\.tcl$", "");
    }
}
