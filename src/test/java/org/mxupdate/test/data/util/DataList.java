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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mxupdate.test.AbstractTest;
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
    private final List<ImmutablePair<String,DATA>> list = new ArrayList<ImmutablePair<String,DATA>>();

    /** All flag */
    private final Set<String> alls = new HashSet<String>();

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
    @Deprecated()
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
     * @param _tag      tag (name) of the data element
     * @param _elem     element to append
     */
    public void add(final String _tag,
                    final DATA _elem)
    {
        this.list.add(new ImmutablePair<String,DATA>(_tag, _elem));
    }

    /**
     * Appends {@code _elem}.
     *
     * @param _elem     element to append
     */
    @Deprecated()
    public void add(final DATA _elem)
    {
        this.add(_elem.getCI().getMxType(), _elem);
    }

    /**
     * Appends {@code _elems}.
     *
     * @param _elems    elements to append
     */
    @Deprecated()
    public void addAll(final Collection<DATA> _elems)
    {
        for (final DATA elem : _elems)  {
            this.add(elem.getCI().getMxType(), elem);
        }
    }

    /**
     * Defines the {@link #alls}.
     *
     * @param _ci   CI for which all must be defined
     */
    public void addAll(final String _tag)
    {
        this.alls.add(_tag);
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
        for (final String tag : this.alls)  {
            _cmd.append(_prefix).append(tag).append(" all\n");
        }
        for (final ImmutablePair<String,DATA> data : this.list)  {
            _cmd.append(_prefix).append(this.prefix4Update).append(this.appendMxType ? data.left : "")
                .append(" \"").append(AbstractTest.convertUpdate(data.right.getName())).append("\"\n");
        }
    }

    /**
     * Appends all elements from this list within create of parent element.
     *
     * @param _cmd      string builder to append
     */
    public void append4Create(final StringBuilder _cmd)
    {
        for (final String tag: this.alls)  {
            _cmd.append(' ').append(tag).append(" all");
        }
        if (!this.list.isEmpty())  {
            // sort all data elements by MX type
            final Map<String,Set<String>> dataElems = new HashMap<String,Set<String>>();
            for (final ImmutablePair<String,DATA> data : this.list)  {
                if (!dataElems.containsKey(data.left))  {
                    dataElems.put(data.left, new HashSet<String>());
                }
                dataElems.get(data.left).add("\"" + AbstractTest.convertMql(data.right.getName()) + "\"");
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
        for (final ImmutablePair<String,DATA> data : this.list)  {
            _cmd.append(" add ").append(this.prefix4Create).append(this.appendMxType ? data.left : "").append(" \"").append(data.right.getName()).append('\"');
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
        for (final ImmutablePair<String,DATA> data : this.list)  {
            data.right.create();
        }
    }
}
