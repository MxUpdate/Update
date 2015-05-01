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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import matrix.util.MatrixException;

import org.apache.commons.lang3.StringUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractData;

/**
 * Implements a list of data objects.
 *
 * @author The MxUpdate Team
 * @param <DATA>
 */
public class DataList<DATA extends AbstractData<?>>
    extends ArrayList<DATA>
{
    /** Dummy serial UID. */
    private static final long serialVersionUID = 1L;
    /** Prefix used for update. */
    private final String prefix4Update;
    /** Prefix used for create. */
    private final String prefix4Create;

    /**
     * Default data list w/o any prefixes.
     */
    public DataList()
    {
        this("", "");
    }

    /**
     * Data list with prefixes.
     *
     * @param _prefix4Update    prefix used for update failed
     * @param _prefix4Create    prefix used for create
     */
    public DataList(final String _prefix4Update,
                    final String _prefix4Create)
    {
        this.prefix4Update = _prefix4Update;
        this.prefix4Create = _prefix4Create;
    }

    /**
     * Appends all elements from this list.
     *
     * @param _prefix   prefix
     * @param _cmd      string builder to append
     */
    public void appendUpdate(final String _prefix,
                             final StringBuilder _cmd)
    {
        for (final DATA data : this)  {
            _cmd.append(_prefix).append(this.prefix4Update).append(data.getCI().getMxType()).append(" \"").append(AbstractTest.convertUpdate(data.getName())).append("\"\n");
        }
    }

    /**
     * Appends all elements from this list within create of parent element.
     *
     * @param _cmd      string builder to append
     */
    public void append4Create(final StringBuilder _cmd)
    {
        if (!this.isEmpty())  {
            // sort all data elements by MX type
            final Map<String,Set<String>> dataElems = new HashMap<String,Set<String>>();
            for (final DATA data : this)  {
                if (!dataElems.containsKey(data.getCI().getMxType()))  {
                    dataElems.put(data.getCI().getMxType(), new HashSet<String>());
                }
                dataElems.get(data.getCI().getMxType()).add("\"" + AbstractTest.convertMql(data.getName()) + "\"");
            }

            // append all elements
            for (final Entry<String,Set<String>> dataElem : dataElems.entrySet())  {
                _cmd.append(' ').append(this.prefix4Create).append(dataElem.getKey()).append(' ').append(StringUtils.join(dataElem.getValue(), ","));
            }
        }
    }

    /**
     * Appends all elements from this list within create of parent element.
     *
     * @param _cmd      string builder to append
     */
    public void append4CreateViaAdd(final StringBuilder _cmd)
    {
        for (final DATA data : this)  {
            _cmd.append(" add ").append(this.prefix4Create).append(data.getCI().getMxType()).append(" \"").append(data.getName()).append('\"');
        }
    }

    /**
     * Creates depending objects defined through this list.
     *
     * @throws MatrixException if create failed
     */
    public void createDependings()
        throws MatrixException
    {
        for (final DATA data : this)  {
            data.create();
        }
    }

    /**
     * Returns a string list representation of this data list.
     *
     * @return string list
     */
    public List<String> toUpdateStringList()
    {
        final List<String> ret = new ArrayList<String>();
        for (final DATA data : this)  {
            ret.add("\"" + AbstractTest.convertUpdate(data.getName()) + "\"");
        }
        return ret;
    }

    /**
     * Checks for all defined flags.
     *
     * @param _parentLine   parent line where the flags must be defined
     * @param _errorLabel   label used for shown error
     */
    public void checkExport(final ExportParser _exportParser,
                            final String _path)
    {
        final StringBuilder ciFile = new StringBuilder();
        this.appendUpdate("", ciFile);

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
                _exportParser.checkList((_path.isEmpty() ? "" : _path + "/") + checks.getKey(), checks.getValue());
            }
        }
    }
}
