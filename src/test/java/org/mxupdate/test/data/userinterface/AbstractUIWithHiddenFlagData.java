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

package org.mxupdate.test.data.userinterface;

import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Handles user interface test data with hidden flags.
 *
 * @param <T>    related user interface class
 * @author The MxUpdate Team
 * @version $Id$
 */
abstract class AbstractUIWithHiddenFlagData<T extends AbstractUIWithHiddenFlagData<?>>
    extends AbstractAdminData<T>
{
    /**
     *
     * @param _test                 related test implementation (where this
     *                              command is defined)
     * @param _ci                   configuration item type
     * @param _name                 name of command
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    AbstractUIWithHiddenFlagData(final AbstractTest _test,
                                 final AbstractTest.CI _ci,
                                 final String _name,
                                 final Map<String,String> _requiredExportValues)
    {
        super(_test, _ci, _name, _requiredExportValues);
    }

    /**
     * Appends to the MQL create commands in <code>_cmd</code> the hidden flag.
     *
     * @param _cmd      string builder used to append the MQL commands
     * @see #settings
     */
    @Override()
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        super.append4CIFileValues(_cmd);
        // hidden flag
        if ((this.isHidden() != null) && this.isHidden())  {
            _cmd.append(" \\\n  hidden");
        }
    }

    /**
     * Appends to the MQL create commands in <code>_cmd</code> the hidden flag.
     *
     * @param _cmd      string builder used to append the MQL commands
     * @throws MatrixException if append failed
     * @see #settings
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);
        // hidden flag
        if ((this.isHidden() != null) && this.isHidden())  {
            _cmd.append(" hidden");
        }
    }

    /**
     * {@inheritDoc}
     * Check for correct hidden flag.
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);
        // hidden flag
        this.checkValueExists(_exportParser, this.getCI().getMxType(), "hidden", (this.isHidden() != null) && this.isHidden());
    }
}
