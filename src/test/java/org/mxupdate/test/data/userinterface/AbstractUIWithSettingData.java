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

package org.mxupdate.test.data.userinterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Handles user interface test data with settings.
 *
 * @param <T>    related user interface class
 * @author The MxUpdate Team
 */
abstract class AbstractUIWithSettingData<T extends AbstractUIWithSettingData<?>>
    extends AbstractAdminData<T>
{
    /**
     * All settings of this command.
     *
     * @see #append4Create(StringBuilder)
     * @see #getSettings()
     * @see #setSetting(String, String)
     * @see #evalAdds4CheckExport(Set)
     */
    private final Map<String,String> settings = new HashMap<String,String>();

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
    AbstractUIWithSettingData(final AbstractTest _test,
                              final AbstractTest.CI _ci,
                              final String _name,
                              final Map<String,Object> _requiredExportValues)
    {
        super(_test, _ci, _name, _requiredExportValues, null);
    }

    /**
     * Defines a new setting for this command.
     *
     * @param _key      key of the setting
     * @param _value    value of the setting
     * @return this command instance
     * @see #settings
     */
    @SuppressWarnings("unchecked")
    public T setSetting(final String _key,
                        final String _value)
    {
        this.settings.put(_key, _value);
        return (T) this;
    }

    /**
     * Returns all settings for this command.
     *
     * @return settings definitions
     * @see #settings
     */
    protected Map<String,String> getSettings()
    {
        return this.settings;
    }

    /**
     * Appends to the MQL create commands in <code>_cmd</code> the
     * {@link #settings}.
     *
     * @param _cmd      string builder used to append the MQL commands
     * @see #settings
     */
    @Override()
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        super.append4CIFileValues(_cmd);
        // settings
        for (final Map.Entry<String,String> entry : this.settings.entrySet())  {
            _cmd.append(" \\\n  add setting \"").append(AbstractTest.convertTcl(entry.getKey())).append("\" \"")
                .append(AbstractTest.convertTcl(entry.getValue()))
                .append('\"');
        }
    }

    /**
     * Appends to the MQL create commands in <code>_cmd</code> the
     * {@link #settings}.
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
        for (final Map.Entry<String,String> entry : this.settings.entrySet())  {
            _cmd.append(" setting \"").append(AbstractTest.convertMql(entry.getKey())).append("\" \"")
                .append(AbstractTest.convertMql(entry.getValue()))
                .append('\"');
        }
    }

    /**
     * Evaluates all 'adds' in the configuration item file (e.g. add
     * setting, ...).
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #settings}
     * @see #settings
     */
    @Override()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final Map.Entry<String,String> entry : this.settings.entrySet())
        {
            _needAdds.add("setting \"" + AbstractTest.convertTcl(entry.getKey())
                    + "\" \"" + AbstractTest.convertTcl(entry.getValue()) +  "\"");
        }
    }
}
