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

import java.util.Set;

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
public abstract class AbstractProgram<T extends AbstractProgram<?>>
    extends AbstractData<T>
{
    /**
     * Initialize the values for program objects.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the program
     * @param _filePrefix           prefix for the file name
     * @param _ciPath               path of the configuration item file
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    protected AbstractProgram(final AbstractTest _test,
                              final CI _ci,
                              final String _name,
                              final String _filePrefix,
                              final String _ciPath,
                              final Set<String> _requiredExportValues)
    {
        super(_test, _ci, _name, _filePrefix, _ciPath, _requiredExportValues);
    }
}
