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

import java.util.HashSet;
import java.util.Set;

import org.mxupdate.test.ExportParser;

/**
 * List of keys which must not be defined.
 *
 * @author The MxUpdate Team
 */
public class KeyNotDefinedList
{
    /** All not defined key. */
    private final Set<String> keys = new HashSet<String>();

    /**
     * Defines key which must not be defined.
     *
     * @param _key  not defined key
     */
    public void defKeyNotDefined(final String _key)
    {
        this.keys.add(_key);
    }

    /**
     * Checks for all defined values.
     *
     * @param _exportParser     export parser
     * @param _path             path
     */
    public void check4Export(final ExportParser _exportParser,
                             final String _path)
    {
        for (final String key : this.keys)  {
            _exportParser.checkNotExistingValue(_path.isEmpty() ? key : _path + "/" + key);
        }
    }
}
