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

import java.util.Set;

/**
 * The JPO class holds utilities for calculating delta's.
 *
 * @author The MxUpdate Team
 */
public final class DeltaUtil_mxJPO
{
    /**
     * The constructor is defined so that no instance of the delta utility
     * could be created.
     */
    private DeltaUtil_mxJPO()
    {
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _mql      MQL builder to append the delta
     * @param _kind     kind of the delta
     * @param _newVal   new target value
     * @param _curVal   current value in the database
     */
    public static void calcValueDelta(final MqlBuilder_mxJPO _mql,
                                      final String _kind,
                                      final String _newVal,
                                      final String _curVal)
    {
        final String curVal = (_curVal == null) ? "" : _curVal;
        final String newVal = (_newVal == null) ? "" : _newVal;

        if (!curVal.equals(newVal))  {
            _mql.newLine().append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newVal)).append('\"');
        }
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _out      appendable instance where the delta must be append
     * @param _kind     kind of the delta
     * @param _newVal   new target value
     * @param _curVal   current value in the database
     */
    public static void calcValueDelta(final StringBuilder _out,
                                      final String _kind,
                                      final String _newVal,
                                      final String _curVal)
    {
        final String curVal = (_curVal == null) ? "" : _curVal;
        final String newVal = (_newVal == null) ? "" : _newVal;

        if (!curVal.equals(newVal))  {
            _out.append(' ').append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newVal)).append('\"');
        }
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _mql      MQL builder to append the delta
     * @param _kind     kind of the delta
     * @param _newVal   new target value
     * @param _curVal   current value in the database
     */
    public static void calcFlagDelta(final MqlBuilder_mxJPO _mql,
                                     final String _kind,
                                     final boolean _newVal,
                                     final boolean _curVal)
    {
        if (_curVal != _newVal)  {
            _mql.newLine();
            if (!_newVal)  {
                _mql.lastLine().append('!');
            }
            _mql.lastLine().append(_kind).append(' ');
        }
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _out      appendable instance where the delta must be append
     * @param _kind     kind of the delta
     * @param _newVal   new target value
     * @param _curVal   current value in the database
     */
    public static void calcFlagDelta(final StringBuilder _out,
                                     final String _kind,
                                     final boolean _newVal,
                                     final boolean _curVal)
    {
        if (_curVal != _newVal)  {
            _out.append(' ');
            if (!_newVal)  {
                _out.append('!');
            }
            _out.append(_kind).append(' ');
        }
    }

    /**
     * Calculates the delta between the new and the old list set. If a delta
     * exists, the different elements are added or removed.
     *
     * @param _mql      MQL builder to append the delta
     * @param _kind     kind of the delta
     * @param _new      new target values
     * @param _current  current values in MX
     */
    public static void calcListDelta(final MqlBuilder_mxJPO _mql,
                                     final String _kind,
                                     final Set<String> _new,
                                     final Set<String> _current)
    {
        boolean equal = (_current.size() == _new.size());
        if (equal)  {
            for (final String format : _current)  {
                if (!_new.contains(format))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            for (final String curValue : _current)  {
                if (!_new.contains(curValue))  {
                    _mql.newLine().append("remove ").append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(curValue)).append('\"');
                }
            }
            for (final String newValue : _new)  {
                if (!_current.contains(newValue))  {
                    _mql.newLine().append("add ").append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newValue)).append('\"');
                }
            }
        }
    }

    /**
     * Calculates the delta between the new and the old list set. If a delta
     * exists, the different elements are added or removed.
     *
     * @param _out      appendable instance where the delta must be append
     * @param _kind     kind of the delta
     * @param _new      new target values
     * @param _current  current values in MX
     */
    public static void calcListDelta(final StringBuilder _out,
                                     final String _kind,
                                     final Set<String> _new,
                                     final Set<String> _current)
    {
        boolean equal = (_current.size() == _new.size());
        if (equal)  {
            for (final String format : _current)  {
                if (!_new.contains(format))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            for (final String curValue : _current)  {
                if (!_new.contains(curValue))  {
                    _out.append(" remove ").append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(curValue)).append('\"');
                }
            }
            for (final String newValue : _new)  {
                if (!_current.contains(newValue))  {
                    _out.append(" add ").append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newValue)).append('\"');
                }
            }
        }
    }
}
