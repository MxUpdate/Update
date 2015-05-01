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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import matrix.util.MatrixException;

import org.apache.commons.lang3.StringUtils;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.data.AbstractData;

/**
 * Implements a list of data objects.
 *
 * @author The MxUpdate Team
 * @param <DATA>
 */
public class DataList<DATA extends AbstractData<?>>
    extends AbstractList
{
    /** Prefix used for update. */
    private final String prefix4Update;
    /** Prefix used for create. */
    private final String prefix4Create;
    private final boolean appendMxType;

    /** Data list. */
    private final List<DATA> list = new ArrayList<DATA>();

    /** All flag */
    private final Set<CI> alls = new HashSet<CI>();

    /**
     * Default data list w/o any prefixes.
     */
    public DataList()
    {
        this("", "", true);
    }

    /**
     * Data list with prefixes.
     *
     * @param _prefix4Update    prefix used for update failed
     * @param _prefix4Create    prefix used for create
     */
    public DataList(final String _prefix4Update,
                    final String _prefix4Create,
                    final boolean _appendMxType)
    {
        this.prefix4Update = _prefix4Update;
        this.prefix4Create = _prefix4Create;
        this.appendMxType  = _appendMxType;
    }

    /**
     * Appends {@code _elem}.
     *
     * @param _elem     element to append
     */
    public void add(final DATA _elem)
    {
        this.list.add(_elem);
    }

    /**
     * Appends {@code _elems}.
     *
     * @param _elems    elements to append
     */
    public void addAll(final Collection<DATA> _elems)
    {
        this.list.addAll(_elems);
    }

    /**
     * Defines the {@link #alls}.
     *
     * @param _ci   CI for which all must be defined
     */
    public void addAll(final CI _ci)
    {
        this.alls.add(_ci);
    }

    /**
     * Defines the {@link #alls}.
     *
     * @param _ci   CI for which all must be removed
     */
    public void removeAll(final CI _ci)
    {
        this.alls.remove(_ci);
    }

    /**
     * Appends all elements from this list.
     *
     * @param _prefix   prefix
     * @param _cmd      string builder to append
     */
    @Override()
    public void append4Update(final String _prefix,
                              final StringBuilder _cmd)
    {
        for (final CI ci: this.alls)  {
            _cmd.append(_prefix).append(this.prefix4Update).append(this.appendMxType ? ci.getMxType() : "").append(" all\n");
        }
        for (final DATA data : this.list)  {
            _cmd.append(_prefix).append(this.prefix4Update).append(this.appendMxType ? data.getCI().getMxType(): "")
                .append(" \"").append(AbstractTest.convertUpdate(data.getName())).append("\"\n");
        }
    }

    /**
     * Appends all elements from this list within create of parent element.
     *
     * @param _cmd      string builder to append
     */
    public void append4Create(final StringBuilder _cmd)
    {
        for (final CI ci: this.alls)  {
            _cmd.append(' ').append(this.prefix4Create).append(this.appendMxType ? ci.getMxType() : "").append(" all");
        }
        if (!this.list.isEmpty())  {
            // sort all data elements by MX type
            final Map<String,Set<String>> dataElems = new HashMap<String,Set<String>>();
            for (final DATA data : this.list)  {
                if (!dataElems.containsKey(data.getCI().getMxType()))  {
                    dataElems.put(data.getCI().getMxType(), new HashSet<String>());
                }
                dataElems.get(data.getCI().getMxType()).add("\"" + AbstractTest.convertMql(data.getName()) + "\"");
            }

            // append all elements
            for (final Entry<String,Set<String>> dataElem : dataElems.entrySet())  {
                _cmd.append(' ').append(this.prefix4Create).append(this.appendMxType ? dataElem.getKey() : "").append(' ').append(StringUtils.join(dataElem.getValue(), ","));
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
        for (final DATA data : this.list)  {
            _cmd.append(" add ").append(this.prefix4Create).append(this.appendMxType ? data.getCI().getMxType() : "").append(" \"").append(data.getName()).append('\"');
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
        for (final DATA data : this.list)  {
            data.create();
        }
    }
}
