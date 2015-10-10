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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

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
     * Calculates the delta for symbolic names.
     *
     * @param _paramCache   parameter cache
     * @param _mql          MQL builder to append the delta
     * @param _target       target ci object
     * @param _current  current ci object
     */
    public static void calcSymbNames(final ParameterCache_mxJPO _paramCache,
                                     final MultiLineMqlBuilder _mql,
                                     final AbstractAdminObject_mxJPO<?> _target,
                                     final AbstractAdminObject_mxJPO<?> _current)
    {
        final String symbProg = _paramCache.getValueString(ValueKeys.RegisterSymbolicNames);

        _mql.pushPrefix("");

        if (_current != null)  {
            for (final String oldSymbName : _current.getSymbolicNames())  {
                if (!_target.getSymbolicNames().contains(oldSymbName))  {
                    _paramCache.logTrace("    - remove symbolic name '" + oldSymbName + "'");
                    _mql.newLine().cmd("escape delete property ").arg(oldSymbName)
                            .cmd(" on program ").arg(symbProg)
                            .cmd(" to ").cmd(_target.getTypeDef().getMxAdminName()).cmd(" ")
                            .arg(_target.getName());
                    if ((_target.getTypeDef().getMxAdminSuffix() != null) && !_target.getTypeDef().getMxAdminSuffix().isEmpty())  {
                        _mql.cmd(" ").cmd(_target.getTypeDef().getMxAdminSuffix());
                    }
                }
            }
        }
        for (final String newSymbName : _target.getSymbolicNames())  {
            if ((_current == null) || !_current.getSymbolicNames().contains(newSymbName))  {
                _paramCache.logTrace("    - register symbolic name '" + newSymbName + "'");
                _mql.newLine().cmd("escape add property ").arg(newSymbName)
                        .cmd(" on program ").arg(symbProg)
                        .cmd(" to ").cmd(_target.getTypeDef().getMxAdminName()).cmd(" ")
                        .arg(_target.getName());
                if ((_target.getTypeDef().getMxAdminSuffix() != null) && !_target.getTypeDef().getMxAdminSuffix().isEmpty())  {
                    _mql.cmd(" ").cmd(_target.getTypeDef().getMxAdminSuffix());
                }
            }
        }

        _mql.popPrefix();
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
    public static void calcValueDelta(final MultiLineMqlBuilder _mql,
                                      final String _kind,
                                      final String _newVal,
                                      final String _curVal)
    {
        final String curVal = (_curVal == null) ? "" : _curVal;
        final String newVal = (_newVal == null) ? "" : _newVal;

        if ((_curVal == null) || !curVal.equals(newVal))  {
            _mql.newLine()
                .cmd(_kind).cmd(" ").arg(newVal);
        }
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _mql              MQL builder to append the delta
     * @param _kind             kind of the delta
     * @param _newVal           new target value
     * @param _newValDefault    default value if {@code _newVal} is not defined
     * @param _curVal           current value in the database
     */
    public static void calcFlagDelta(final MultiLineMqlBuilder _mql,
                                     final String _kind,
                                     final boolean _newValDefault,
                                     final Boolean _newVal,
                                     final Boolean _curVal)
    {
        if ((_curVal == null) || (_curVal != _newVal))  {
            _mql.newLine();
            if (((_newVal == null) && !_newValDefault) || ((_newVal != null) && !_newVal))  {
                _mql.cmd("!");
            }
            _mql.cmd(_kind);
        }
    }

    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _mql              MQL builder to append the delta
     * @param _kind             kind of the delta
     * @param _newVal           new target value
     * @param _newValDefault    default value if {@code _newVal} is not defined
     * @param _curVal           current value in the database
     */
    public static void calcValFlgDelta(final MultiLineMqlBuilder _mql,
                                       final String _kind,
                                       final boolean _newValDefault,
                                       final Boolean _newVal,
                                       final Boolean _curVal)
    {
        if ((_curVal == null) || (_curVal != _newVal))  {
            _mql.newLine()
                .cmd(_kind).cmd(" ").arg(String.valueOf(((_newVal != null) && _newValDefault) || ((_newVal != null) && _newVal)));
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
    public static void calcListDelta(final MultiLineMqlBuilder _mql,
                                     final String _kind,
                                     final SortedSet<String> _new,
                                     final SortedSet<String> _current)
    {
        boolean equal = (_current != null) && (_current.size() == _new.size());
        if (equal)  {
            for (final String format : _current)  {
                if (!_new.contains(format))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            if (_current != null)  {
                for (final String curValue : _current)  {
                    if (!_new.contains(curValue))  {
                        _mql.newLine()
                            .cmd("remove ").cmd(_kind).cmd(" ").arg(curValue);
                    }
                }
            }
            for (final String newValue : _new)  {
                if ((_current == null) || !_current.contains(newValue))  {
                    _mql.newLine()
                        .cmd("add ").cmd(_kind).cmd(" ").arg(newValue);
                }
            }
        }
    }


    /**
     * Calculates the delta between given {@code _current} list definition and
     * the {@code _new} target list definition and appends one MQL append with
     * complete new list.
     *
     * @param _mql          builder to append the MQL commands
     * @param _kind         kind of the delta
     * @param _new          new target list definition
     * @param _current      current list definition
     */
    public static void calcLstOneCallDelta(final MultiLineMqlBuilder _mql,
                                           final String _kind,
                                           final SortedSet<String> _new,
                                           final SortedSet<String> _current)
    {
        boolean equal = (_new.size() == _current.size());
        if (equal)  {
            for (final String attribute : _current)  {
                if (!_new.contains(attribute))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            if (_new.isEmpty())  {
                _mql.newLine().cmd("remove ").cmd(_kind);
            } else  {
                _mql.newLine().cmd(_kind).cmd(" ");
                boolean first = true;
                for (final String elem : _new)  {
                    if (first)  {
                        first = false;
                    } else  {
                        _mql.cmd(",");
                    }
                    _mql.arg(elem);
                }
            }
        }
    }

    /**
     * Calculates the delta between the new and the old list set and if all is
     * defined. If a delta exists, the different elements are added or removed.
     *
     * @param _mql          MQL builder to append the delta
     * @param _kind         kind of the delta
     * @param _newAll       'all' elements are selected for the target
     * @param _new          new target values
     * @param _current      current values in MX
     * @param _currentAll   'all' elements are selected for the current
     */
    public static void calcListDelta(final MultiLineMqlBuilder _mql,
                                     final String _kind,
                                     final boolean _newAll,
                                     final SortedSet<String> _new,
                                     final boolean _currentAll,
                                     final SortedSet<String> _current)
    {
        if (_newAll)  {
            DeltaUtil_mxJPO.calcListDelta( _mql, _kind, new TreeSet<String>(), _current);
            if (!_currentAll)  {
                _mql.newLine().cmd("add ").cmd(_kind).cmd(" all");
            }
        } else  {
            if (_currentAll)  {
                _mql.newLine().cmd("remove ").cmd(_kind).cmd(" all");
            }
            DeltaUtil_mxJPO.calcListDelta( _mql, _kind, _new, _current);
        }
    }

    /**
     * Calculates the delta between given {@code _current} list definition and
     * the {@code _new} target list definition and appends the MQL append
     * {@code add} / {@code remove} commands to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _mql          builder to append the MQL commands
     * @param _kind         kind of the delta
     * @param _errorKey     error key
     * @param _parentName   name of parent name (needed for exception handling)
     * @param _keyIgnore    parameter key to ignore
     * @param _keyRemove    parameter key to remove
     * @param _new          new target list definition
     * @param _current      current list definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    public static void calcListDelta(final ParameterCache_mxJPO _paramCache,
                                     final MultiLineMqlBuilder _mql,
                                     final String _kind,
                                     final ErrorKey _errorKey,
                                     final String _parentName,
                                     final ValueKeys _keyIgnore,
                                     final ValueKeys _keyRemove,
                                     final SortedSet<String> _new,
                                     final SortedSet<String> _current)
        throws UpdateException_mxJPO
    {
        final Set<String> ignoreElems = new HashSet<>();
        final Collection<String> tmp1 = _paramCache.getValueList(_keyIgnore);
        if (tmp1 != null)  {
            ignoreElems.addAll(tmp1);
        }

        final Set<String> removeElems = new HashSet<>();
        final Collection<String> tmp2 = _paramCache.getValueList(_keyRemove);
        if (tmp2 != null)  {
            removeElems.addAll(tmp2);
        }

        boolean equal = (_current != null) && (_new.size() == _current.size());
        if (equal)  {
            for (final String attribute : _current)  {
                if (!_new.contains(attribute))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            if (_current != null)  {
                for (final String curValue : _current)  {
                    if (!_new.contains(curValue))  {
                        boolean ignore = false;
                        for (final String ignoreAttr : ignoreElems)  {
                            if (StringUtil_mxJPO.match(curValue, ignoreAttr))  {
                                ignore = true;
                                _paramCache.logDebug("    - " + _kind +" '" + curValue + "' is not assigned anymore and therefore ignored");
                                break;
                            }
                        }
                        if (!ignore)  {
                            boolean remove = false;
                            for (final String removeAttr : removeElems)  {
                                if (StringUtil_mxJPO.match(curValue, removeAttr))  {
                                    remove = true;
                                    _paramCache.logDebug("    - " + _kind +" '" + curValue + "' is not assigned anymore and therefore removed");
                                    _mql.newLine().cmd("remove ").cmd(_kind).cmd(" ").arg(curValue);
                                    break;
                                }
                            }
                            if (!remove)  {
                                throw new UpdateException_mxJPO(_errorKey, curValue, _parentName);
                            }
                        }
                    }
                }
            }
            for (final String newValue : _new)  {
                if (!_current.contains(newValue))  {
                    _paramCache.logDebug("    - " + _kind +" '" + newValue + "' is added");
                    _mql.newLine().cmd("add ").cmd(_kind).cmd(" ").arg(newValue);
                }
            }
        }
    }

    /**
     * Calculates the delta between given {@code _current} list definition and
     * the {@code _new} target list definition and appends one MQL append with
     * complete new list.
     *
     * @param _paramCache   parameter cache
     * @param _mql          builder to append the MQL commands
     * @param _parentName   name of parent name (needed for exception handling)
     * @param _current      current attribute list definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    public static void calcLstOneCallDelta(final ParameterCache_mxJPO _paramCache,
                                           final MultiLineMqlBuilder _mql,
                                           final String _kind,
                                           final ErrorKey _errorKey,
                                           final String _parentName,
                                           final ValueKeys _keyIgnore,
                                           final ValueKeys _keyRemove,
                                           final SortedSet<String> _new,
                                           final SortedSet<String> _current)
        throws UpdateException_mxJPO
    {
        final Set<String> ignoreElems = new HashSet<>();
        final Collection<String> tmp1 = _paramCache.getValueList(_keyIgnore);
        if (tmp1 != null)  {
            ignoreElems.addAll(tmp1);
        }

        final Set<String> removeElems = new HashSet<>();
        final Collection<String> tmp2 = _paramCache.getValueList(_keyRemove);
        if (tmp2 != null)  {
            removeElems.addAll(tmp2);
        }

        boolean equal = (_new.size() == _current.size());
        if (equal)  {
            for (final String attribute : _current)  {
                if (!_new.contains(attribute))  {
                    equal = false;
                    break;
                }
            }
        }
        if (!equal)  {
            final SortedSet<String> newComplVals = new TreeSet<>(_new);

            for (final String curValue : _current)  {
                if (!_new.contains(curValue))  {
                    boolean ignore = false;
                    for (final String ignoreAttr : ignoreElems)  {
                        if (StringUtil_mxJPO.match(curValue, ignoreAttr))  {
                            newComplVals.add(curValue);
                            ignore = true;
                            _paramCache.logDebug("    - " + _kind +" '" + curValue + "' is not assigned anymore and therefore ignored");
                            break;
                        }
                    }
                    if (!ignore)  {
                        boolean remove = false;
                        for (final String removeAttr : removeElems)  {
                            if (StringUtil_mxJPO.match(curValue, removeAttr))  {
                                remove = true;
                                _paramCache.logDebug("    - " + _kind +" '" + curValue + "' is not assigned anymore and therefore removed");
                                break;
                            }
                        }
                        if (!remove)  {
                            throw new UpdateException_mxJPO(_errorKey, curValue, _parentName);
                        }
                    }
                }
            }
            for (final String newValue : _new)  {
                if (!_current.contains(newValue))  {
                    _paramCache.logDebug("    - " + _kind +" '" + newValue + "' is added");
                }
            }
            // prepare complete update
            if (newComplVals.isEmpty())  {
                _mql.newLine().cmd("remove ").cmd(_kind);
            } else  {
                _mql.newLine().cmd(_kind).cmd(" ");
                boolean first = true;
                for (final String elem : newComplVals)  {
                    if (first)  {
                        first = false;
                    } else  {
                        _mql.cmd(",");
                    }
                    _mql.arg(elem);
                }
            }
        }
    }
}
