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

package org.mxupdate.update.util;

import java.util.SortedSet;

/**
 * Utility class to compare objects.
 *
 * @author The MxUpdate Team
 */
public final class CompareToUtil_mxJPO
{
    /**
     * Private constructor to avoid initialization.
     */
    private CompareToUtil_mxJPO()
    {
    }

    /**
     * Compares the {@code _lhs} to {@_rhs} including handling of the
     * {@code null} case.
     *
     * @param _comparison   current comparison
     * @param _lhs          left string
     * @param _rhs          right string
     * @return if {@code _comparision} is not 0 {@code _comparision} is
     *         returned, otherwise the comparison result of {@code _lhs} and
     *         {@code _rhs}
     */
    public static int compare(final int _comparison,
                              final String _lhs,
                              final String _rhs)
    {
        final int comparison;
        if (_comparison != 0)  {
            comparison = _comparison;
        } else if (_lhs == _rhs)  {
            comparison = 0;
        } else if (_lhs == null)  {
            comparison = -1;
        } else if (_rhs == null)  {
            comparison = +1;
        } else  {
            comparison = _lhs.compareTo(_rhs);
        }
        return comparison;
    }

    /**
     * Compares the {@code _lhs} to {@_rhs} including handling of the
     * {@code null} case.
     *
     * @param _comparison   current comparison
     * @param _lhs          left string
     * @param _rhs          right string
     * @return if {@code _comparision} is not 0 {@code _comparision} is
     *         returned, otherwise the comparison result of {@code _lhs} and
     *         {@code _rhs}
     */
    public static int compare(final int _comparison,
                              final SortedSet<String> _lhs,
                              final SortedSet<String> _rhs)
    {
        return CompareToUtil_mxJPO.compare(
                _comparison,
                (_lhs == null) ? null : _lhs.toArray(new String[_lhs.size()]),
                (_rhs == null) ? null : _rhs.toArray(new String[_rhs.size()]));
    }

    /**
     * Compares the {@code _lhs} to {@_rhs} including handling of the
     * {@code null} case.
     *
     * @param _comparison   current comparison
     * @param _lhs          left string
     * @param _rhs          right string
     * @return if {@code _comparision} is not 0 {@code _comparision} is
     *         returned, otherwise the comparison result of {@code _lhs} and
     *         {@code _rhs}
     */
    public static int compare(final int _comparison,
                              final String[] _lhs,
                              final String[] _rhs)
    {
        final int comparison;
        if (_comparison != 0)  {
            comparison = _comparison;
        } else if (_lhs == _rhs)  {
            comparison = 0;
        } else if (_lhs == null)  {
            comparison = -1;
        } else if (_rhs == null)  {
            comparison = +1;
        } else if (_lhs.length < _rhs.length)  {
            comparison = -1;
        } else if (_lhs.length > _rhs.length)  {
            comparison = +1;
        } else  {
            int tmp = 0;
            for (int idx = 0; (idx < _lhs.length) && (tmp == 0); idx++)  {
                tmp = CompareToUtil_mxJPO.compare(0, _lhs[idx], _rhs[idx]);
            }
            comparison = tmp;
        }
        return comparison;
    }
}
