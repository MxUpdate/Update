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

package org.mxupdate.test.data.user.workspace;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.AbstractUserData;
import org.testng.Assert;

/**
 * The class is used to define all common things for workspace objects related
 * to users used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @param <DATA> workspace object class
 * @param <USER> for which user class the workspace object class is defined
 */
public class AbstractVisualWorkspaceObjectData<DATA extends AbstractVisualWorkspaceObjectData<?,USER>, USER extends AbstractUserData<?>>
        extends AbstractWorkspaceObjectData<DATA,USER>
{
    /**
     * Is this visual workspace object active?
     *
     * @see #setActive(boolean)
     * @see #ciFile()
     * @see #append4Create(StringBuilder)
     * @see #checkExport(ExportParser)
     */
    private boolean active = true;

    /**
     * Default constructor.
     *
     * @param _test                 related test case
     * @param _mxAdminType          MX administration type of the visual query
     *                              workspace object
     * @param _user                 user for which this visual query workspace
     *                              object is defined
     * @param _name                 name of the visual query workspace object
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    AbstractVisualWorkspaceObjectData(final AbstractTest _test,
                                      final String _mxAdminType,
                                      final USER _user,
                                      final String _name,
                                      final Map<String,String> _requiredExportValues)
    {
        super(_test, _mxAdminType, _user, _name, _requiredExportValues);
    }

    /**
     * Defines if this visual workspace object object is active or not.
     *
     * @param _active   <i>true</i> if active; otherwise <i>false</i>
     * @return this visual workspace object instance
     */
    @SuppressWarnings("unchecked")
    public DATA setActive(final boolean _active)
    {
        this.active = _active;
        return (DATA) this;
    }

    /**
     * Returns the part of the CI file to create this visual workspace object
     * of an user.
     *
     * @return part of the CI file to create this visual workspace object of an
     *         user
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder().append(super.ciFile());

        // active flag
        if (this.active)  {
            cmd.append(" \\\n    active");
        } else  {
            cmd.append(" \\\n    !active");
        }

        return cmd.toString();
    }


    /**
     * Appends the MQL commands to define the {@link #active flag} within a
     * create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #active
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // active flag
        if (this.active)  {
            _cmd.append(" active");
        } else  {
            _cmd.append(" !active");
        }
    }

    /**
     * Checks the export of this tool set data if all values are correct
     * defined. The {@link #active active flag} is checked.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check active flag
        final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
        if (this.active)  {
            Assert.assertTrue(main.contains("active") || main.contains("active \\"),
                              "check that tool set '" + this.getName() + "' is active");
        } else  {
            Assert.assertTrue(main.contains("!active") || main.contains("!active \\"),
                              "check that tool set '" + this.getName() + "' is not active");
        }
    }
}
