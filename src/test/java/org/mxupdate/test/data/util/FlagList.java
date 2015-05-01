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
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.ExportParser.Line;
import org.testng.Assert;

/**
 * The class is used for the flag definition of administration objects.
 *
 * @author The MxUpdate Team
 */
public class FlagList
{
    /** All flags. */
    private final HashMap<String,MutablePair<Boolean,Create>> flags = new HashMap<String,MutablePair<Boolean,Create>>();

    /**
     * Defines new flag with value.
     *
     * @param _tag          used tag (name) of the flag
     * @param _value        value
     */
    public void setFlag(final String _tag,
                        final Boolean _value)
    {
        this.flags.put(_tag, new MutablePair<Boolean,Create>(_value, Create.ViaFlag));
    }

    /**
     * Defines new flag with value.
     *
     * @param _tag          used tag (name) of the flag
     * @param _value        value
     * @param _createConf   how the flag must be defined within MQL create
     */
    public void setFlag(final String _tag,
                        final Boolean _value,
                        final Create _createConf)
    {
        this.flags.put(_tag, new MutablePair<Boolean,Create>(_value, _createConf));
    }

    /**
     * Returns for {@code _key} defined value.
     *
     * @param _key  search key
     * @return defined value; {@code null} if not defined
     */
    public Boolean getValue(final String _key)
    {
        return this.flags.containsKey(_key) ? this.flags.get(_key).getLeft() : null;
    }

    /**
     * Checks if {@code _key} is defined as flag.
     *
     * @param _key  search key
     * @return <i>true</i> if {@code _key} is defined as flag; otherwise
     *         <i>false</i>
     */
    public boolean contains(final String _key)
    {
        return this.flags.containsKey(_key);
    }

    /**
     * Appends the defined flags to the TCL code {@code _cmd} of the
     * configuration item file.
     *
     * @param _prefix   prefix in front of the values
     * @param _cmd      string builder with the TCL commands of the
     *                  configuration item file
     */
    public void append4Update(final String _prefix,
                              final StringBuilder _cmd)
    {
        for (final Map.Entry<String,MutablePair<Boolean,Create>> entry : this.flags.entrySet())  {
            if (entry.getValue().getLeft() != null)  {
                _cmd.append(_prefix);
                if (!entry.getValue().getLeft())  {
                    _cmd.append('!');
                }
                _cmd.append(entry.getKey()).append('\n');
            }
        }
    }

    /**
     * Appends the MQL commands to define all flags.
     *
     * @param _cmd  string builder used to append MQL commands
     */
    public void append4Create(final StringBuilder _cmd)
    {
        for (final Map.Entry<String,MutablePair<Boolean,Create>> entry : this.flags.entrySet())  {
            if (entry.getValue().getLeft() != null)  {
                _cmd.append(' ');
                if (!entry.getValue().getLeft() && (entry.getValue().getRight() == Create.ViaFlag))  {
                    _cmd.append('!');
                }
                _cmd.append(entry.getKey());
                if (entry.getValue().getRight() == Create.ViaValue)  {
                    _cmd.append(" ").append(entry.getValue().getLeft());
                }
            }
        }
    }

    /**
     * Checks for all defined flags.
     *
     * @param _exportParser parsed export
     * @param _url          URL where to find the flags
     * @param _errorLabel   label used for shown error
     */
    @Deprecated()
    public void checkExport(final ExportParser _exportParser,
                            final String _url,
                            final String _errorLabel)
    {
        final Set<String> main = new HashSet<String>(_exportParser.getLines(_url));
        for (final Map.Entry<String,MutablePair<Boolean,Create>> flag : this.flags.entrySet())  {
            if (flag.getValue().getLeft() != null)  {
                // check flag is defined
                final String key = flag.getValue().getLeft() ? flag.getKey() : "!" + flag.getKey();
                Assert.assertTrue(
                        main.contains(key) || main.contains(key + " \\"),
                        "check that " + _errorLabel + "' contains flag " + key);
                // check that inverted flag is NOT defined
                final String keyInv = flag.getValue().getLeft() ? "!" + flag.getKey() : flag.getKey();
                Assert.assertTrue(
                        !main.contains(keyInv) && !main.contains(keyInv + " \\"),
                        "check that " + _errorLabel + " does not contain flag " + keyInv);
            }
        }
    }

    /**
     * Checks for all defined flags.
     *
     * @param _exportParser     parsed export
     * @param _path             sub path
     */
    public void check4Export(final ExportParser _exportParser,
                             final String _path)
    {
        for (final Map.Entry<String,MutablePair<Boolean,Create>> flag : this.flags.entrySet())  {
            _exportParser.checkFlag((_path.isEmpty() ? "" : _path + "/") + flag.getKey(), flag.getValue().getLeft());
        }
    }

    /**
     * Checks for all defined flags.
     *
     * @param _parentLine   parent line where the flags must be defined
     * @param _errorLabel   label used for shown error
     */
    @Deprecated()
    public void checkExport(final Line _parentLine,
                            final String _errorLabel)
    {
        for (final Map.Entry<String,MutablePair<Boolean,Create>> flag : this.flags.entrySet())  {
            if (flag.getValue().getLeft() != null)  {
                // check flag is defined
                final String key = flag.getValue().getLeft() ? flag.getKey() : "!" + flag.getKey();
                Assert.assertEquals(
                        _parentLine.getLines(key + "/@name").size(),
                        1,
                        "check flag " + key + " is defined");
                Assert.assertEquals(
                        _parentLine.getLines(key + "/@name").get(0),
                        key,
                        "check flag " + key + " is defined");
                // check that inverted flag is NOT defined
                final String keyInv = flag.getValue().getLeft() ? "!" + flag.getKey() : flag.getKey();
                Assert.assertEquals(
                        _parentLine.getLines(keyInv + "/@name").size(),
                        0,
                        "check that " + _errorLabel + " does not contain flag " + keyInv);
            } else  {
                // check flag is NOT defined
                Assert.assertEquals(
                        _parentLine.getLines(flag.getKey() + "/@name").size(),
                        0,
                        "check flag " + flag.getKey() + " is not defined");
                // check that inverted flag is NOT defined
                Assert.assertEquals(
                        _parentLine.getLines("!" + flag.getKey() + "/@name").size(),
                        0,
                        "check flag !" + flag.getKey() + " is not defined");
            }
        }
    }

    /**
     * Defines how the flag must be defined within MQL create.
     */
    public enum Create
    {
        /** Create statement is defined as flag with '!'. */
        ViaFlag,
        /** Create statement is defined via a value. */
        ViaValue;
    }
}
