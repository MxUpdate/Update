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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.data.AbstractData;

/**
 * The class is used to define all program objects used to create / update and
 * to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <T>   defines the class which is derived from this class
 */
public abstract class AbstractProgramData<T extends AbstractProgramData<?>>
    extends AbstractData<T>
{
    /**
     * Related code of this program.
     *
     * @see #setCode(String)
     * @see #getCode()
     */
    private String code = "";

    /**
     * Initialize the values for program objects.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the program
     * @param _filePrefix           prefix for the file name
     * @param _ciPath               path of the configuration item file
     */
    protected AbstractProgramData(final AbstractTest _test,
                              final CI _ci,
                              final String _name,
                              final String _filePrefix,
                              final String _ciPath)
    {
        super(_test, _ci, _name, _filePrefix, _ciPath, null);
    }

    /**
     * The related configuration item file is the {@link #code} of the program.
     *
     *  @return {@link #code} of the program
     *  @see #code
     */
    @Override
    public String ciFile()
    {
        return this.code;
    }

    /**
     * Defines the {@link #code} for this program.
     *
     * @param _code     new code of this program
     * @return this program instance
     * @see #code
     */
    @SuppressWarnings("unchecked")
    public T setCode(final String _code)
    {
        this.code = _code;
        return (T) this;
    }

    /**
     * Returns the {@link #code} of this program.
     *
     * @return related {@link #code} of this program
     * @see #code
     */
    public String getCode()
    {
        return this.code;
    }
}
