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

package org.mxupdate.test.data.userinterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.testng.Assert;

/**
 * Handles user interface test data with settings.
 *
 * @param <T>    related user interface class
 * @author The MxUpdate Team
 */
abstract class AbstractUIWithSettingData<T extends AbstractUIWithSettingData<?>>
    extends AbstractAdminData<T>
{
    /** All settings of this command. */
    private final Settings settings = new Settings();

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
                              final String _name)
    {
        super(_test, _ci, _name);
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
    protected Settings getSettings()
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

    /**
     * Settings.
     */
    public static class Settings
        extends HashMap<String,String>
    {
        /** Dummy ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Appends the defined settings to the update code {@code _strg} of the
         * configuration item file.
         *
         * @param _prefix   prefix in front of the values
         * @param _strg     string builder with the TCL commands of the
         *                  configuration item file
         * @param _suffix   suffix after the values
         */
        public void appendUpdate(final String _prefix,
                                 final StringBuilder _strg,
                                 final String _suffix)
        {
            for (final Map.Entry<String,String> entry : this.entrySet())  {
                _strg.append(_prefix)
                     .append("setting \"").append(AbstractTest.convertUpdate(entry.getKey())).append("\" ")
                     .append('\"').append(AbstractTest.convertUpdate(entry.getValue())).append('\"')
                     .append(_suffix);
            }
        }

        /**
         * Checks that all settings within the export file are correct defined
         * and equal to the defined properties of this CI file.
         *
         * @param _setLines    setting lines
         */
        public void checkExport(final List<String> _setLines)
        {
            final Set<String> setDefs = new HashSet<String>(_setLines);

            for (final Map.Entry<String,String> entry : this.entrySet())  {
                final String setDefStr = "\"" + AbstractTest.convertUpdate(entry.getKey()) + "\" \"" + AbstractTest.convertUpdate(entry.getValue()) + '\"';
                Assert.assertTrue(
                        setDefs.contains(setDefStr),
                        "check that setting is defined in ci file (have " + setDefStr + ", but found " + setDefs + ")");
                setDefs.remove(setDefStr);
            }
            Assert.assertEquals(setDefs.size(), 0, "check that not too much settings are defined (have " + setDefs + ")");
        }
    }
}
