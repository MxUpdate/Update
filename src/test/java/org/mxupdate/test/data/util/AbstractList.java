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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mxupdate.test.ExportParser;

/**
 * Abstract list used for the handling of parts from the configuration item
 * file.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractList
{
    /** All elements of the list must be container? */
    private boolean checkAll = true;

    /**
     * Defines if defined expected elements must be equal to current elements.
     *
     * @param _checkAll     all elements to check?
     */
    public void setCheckAllElemens(final boolean _checkAll)
    {
        this.checkAll = _checkAll;
    }

    /**
     * Appends given list to the configuration item file.
     *
     * @param _prefix   prefix
     * @param _cmd      string builder of the configuration item file
     */
    public abstract void append4Update(final String _prefix,
                                       final StringBuilder _cmd);

    /**
     * Checks for all defined values.
     *
     * @param _parentLine   parent line where the flags must be defined
     * @param _errorLabel   label used for shown error
     */
    public void check4Export(final ExportParser _exportParser,
                             final String _path)
    {
        final StringBuilder ciFile = new StringBuilder();
        this.append4Update("", ciFile);

        if (!ciFile.toString().trim().isEmpty())  {
            final Map<String,Set<String>> allChecks = new HashMap<String,Set<String>>();
            for (final String line : ciFile.toString().split("\n"))  {
                final int idx = line.indexOf(' ');
                final String key   = line.substring(0, idx).trim();
                final String value = line.substring(idx).trim();
                if (!allChecks.containsKey(key))  {
                    allChecks.put(key, new HashSet<String>());
                }
                allChecks.get(key).add(value);
            }

            for (final Entry<String,Set<String>> checks : allChecks.entrySet())  {
                if (this.checkAll)  {
                    _exportParser.checkList((_path.isEmpty() ? "" : _path + "/") + checks.getKey(), checks.getValue());
                } else  {
                    _exportParser.checkListContains((_path.isEmpty() ? "" : _path + "/") + checks.getKey(), checks.getValue());
                }
            }
        }
    }
}
