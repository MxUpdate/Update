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

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.mxupdate.test.AbstractTest;

/**
 * Key/value list.
 *
 * @author The MxUpdate Team
 */
public class KeyValueList
    extends AbstractList
{
    /** All key values with prefix. */
    private final List<ImmutableTriple<String,String,String>> keyValues = new ArrayList<ImmutableTriple<String,String,String>>();

    /**
     * Defines key / value for given {@code _tag}.
     *
     * @param _tag          used tag (name) of the flag
     * @param _key          key
     * @param _value        value
     */
    public void addKeyValue(final String _tag,
                            final String _key,
                            final String _value)
    {
        this.keyValues.add(new ImmutableTriple<String,String,String>(_tag, _key, _value));
    }

    /**
     * Appends the defined key/values pair to the TCL code {@code _cmd} of the
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
        for (final ImmutableTriple<String,String,String> item : this.keyValues)  {
            _cmd.append(_prefix).append(item.left)
                .append(" \"").append(AbstractTest.convertUpdate(item.middle)).append('\"')
                .append(" \"").append(AbstractTest.convertUpdate(item.right)).append('\"')
                .append('\n');
        }
    }

    /**
     * Appends the MQL commands to define all flags.
     *
     * @param _cmd  string builder used to append MQL commands
     */
    public void append4Create(final StringBuilder _cmd)
    {
        for (final ImmutableTriple<String,String,String> item : this.keyValues)  {
            _cmd.append(' ').append(item.left)
                .append(" \"").append(AbstractTest.convertMql(item.middle)).append('\"')
                .append(" \"").append(AbstractTest.convertMql(item.right)).append('\"');
        }
    }
}
