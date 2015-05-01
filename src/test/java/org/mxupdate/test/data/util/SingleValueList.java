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

package org.mxupdate.test.data.util;

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mxupdate.test.AbstractTest;

/**
 * Single-value list.
 *
 * @author The MxUpdate Team
 */
public class SingleValueList
    extends AbstractList
{
    /** Data list. */
    private final List<ImmutablePair<String,String>> values = new ArrayList<ImmutablePair<String,String>>();

    /**
     * Defines a value.
     *
     * @param _key      key
     * @param _value    value
     */
    public void def(final String _key,
                    final String _value)
    {
        this.values.add(new ImmutablePair<String,String>(_key, _value));
    }

    /**
     * Appends the defined flags to the TCL code {@code _cmd} of the
     * configuration item file.
     *
     * @param _prefix   prefix in front of the values
     * @param _cmd      string builder with the TCL commands of the
     *                  configuration item file
     */
    @Override()
    public void append4Update(final String _prefix,
                             final StringBuilder _cmd)
    {
        for (final ImmutablePair<String,String> entry : this.values)  {
            if (entry.getValue() != null ) {
                _cmd.append(_prefix).append(entry.getKey()).append(" ")
                    .append(AbstractTest.convertUpdate(entry.getValue().toString()))
                    .append('\n');
            }
        }
    }

    /**
     * Appends the MQL commands to define all values within a create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     */
    public void append4Create(final StringBuilder _cmd)
    {
        for (final ImmutablePair<String,String> entry : this.values)  {
            if (entry.getValue() != null ) {
                _cmd.append(' ').append(entry.getKey()).append(" ").append(AbstractTest.convertMql(entry.getValue().toString()));
            }
        }
    }
}
