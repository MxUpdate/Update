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

package org.mxupdate.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.codehaus.plexus.util.StringUtils;
import org.mxupdate.test.AbstractTest.CI;
import org.testng.Assert;

/**
 * The class parses informations from an MxUpdate export.
 *
 * @author The MxUpdate Team
 */
public class ExportParser
{
    /**
     * Defines the string which defines the header start and end.
     */
    private static final String HEADER_START_END
            = "\n################################################################################\n";

    /** Reference to the configuration item enumeration. */
    private final CI ci;

    /** Original configuration item update code. */
    private final String origCode;

    /** Related code of the export without header. */
    private final String code;

    /** Logging information. */
    private final String log;

    /**List of all root lines. */
    private final List<Line> rootLines = new ArrayList<Line>();

    /**
     *
     * @param _ci       configuration item type
     * @param _code     exported code (configuration item update file)
     * @param _log      logging information
     */
    public ExportParser(final CI _ci,
                        final String _code,
                        final String _log)
    {
        this.origCode =_code;
        this.log = _log;
        this.ci = _ci;
        // extract update code
        this.code = this.extractUpdateCode(this.origCode);
        // parse all lines
        new Line(Arrays.asList(this.code.split("\n")).iterator(), null);
    }

    /**
     * Creates a new export parser used e.g. to check elements within a sub
     * structure.
     *
     * @param _name         name of the administration object
     * @param _symbolicName symbolic name
     * @param _log          logging information
     * @param _rootLines    depending root lines
     */
    public ExportParser(final String _log,
                        final Line...  _rootLines)
    {
        this.origCode = null;
        this.ci = null;
        this.log = _log;
        this.code = null;
        this.rootLines.addAll(Arrays.asList(_rootLines));
    }

    /**
     * Used to check an export depending on a deep hierarchy and to change the
     * tag of the root.
     *
     * @param _log          logging information
     * @param _lineTag      tag of the parsed line
     * @param _lineValue    value of the parsed line
     * @param _children     all child lines of the parsed line
     */
    public ExportParser(final String _log,
                        final String _lineTag,
                        final String _lineValue,
                        final List<Line> _children)
    {
        this.origCode = null;
        this.ci = null;
        this.log = _log;
        this.code = null;
        this.rootLines.add(new Line(_lineTag, _lineValue, _children));
    }

    /**
     * Extracts from the <code>_origCode</code> the update code without header.
     *
     * @param _origCode     original code from which the update code must be
     *                      extracted
     * @return extracted update code (without header)
     */
    protected String extractUpdateCode(final String _origCode)
    {
        final String ret;
        final int posHeaderEnd = _origCode.lastIndexOf(ExportParser.HEADER_START_END);
        if (posHeaderEnd >=0)  {
            ret = _origCode.substring(posHeaderEnd + ExportParser.HEADER_START_END.length()).trim();
        } else  {
            ret = _origCode.trim();
        }
        return ret;
    }

    /**
     * Checks that no value for given {@code _path} exits.
     *
     * @param _path     path to check
     * @return this export parser instance
     */
    public ExportParser checkNotExistingValue(final String _path)
    {
        final List<String> valueLines = this.getRootLines().get(0).getLines(_path + "/@name");
        Assert.assertEquals(valueLines.size(), 0, "no line for path '" + _path + "' must be found");
        return this;
    }

    /**
     * Checks that given {@code _value} is defined for given {@code _path}.
     *
     * @param _path     path to check
     * @param _value    value
     * @return this export parser instance
     */
    public ExportParser checkValue(final String _path,
                                   final String _value)
    {
        final List<String> valueLines = this.getRootLines().get(0).getLines(_path + "/@value");
        if (_value == null) {
            Assert.assertEquals(valueLines.size(), 0,      "exact none line for path '" + _path + "' must be found");
        } else {
            Assert.assertEquals(valueLines.size(), 1,      "exact one line for path '" + _path + "' must be found");
            Assert.assertEquals(valueLines.get(0), _value, "value for line with path '" + _path + "' must be correct");
        }
        return this;
    }

    /**
     * Checks that given {@code _value}  flag is defined for given
     * {@code _path}.
     *
     * @param _path     path to check
     * @param _value    value
     * @return this export parser instance
     */
    public ExportParser checkFlag(final String _path,
                                  final Boolean _value)
    {
        final String[] paths = _path.split("/");
        final String key = paths[paths.length - 1];
        paths[paths.length - 1] = "!" + key;
        final String negativPath = StringUtils.join(paths, "/");

        final List<String> positivLines = this.getRootLines().get(0).getLines(_path       + "/@name");
        final List<String> negativLines = this.getRootLines().get(0).getLines(negativPath + "/@name");
        if (_value != null)  {
            // check flag is defined
            final List<String> lines = _value ? positivLines : negativLines;
            Assert.assertEquals(lines.size(), 1, "check flag " + _path + " is defined");
            Assert.assertEquals(lines.get(0), _value ? key : ("!" + key), "check flag " + key + " is defined");
            // check that inverted flag is NOT defined
            final List<String> linesInv = _value ? negativLines : positivLines;
            Assert.assertEquals(linesInv.size(), 0, "check that " + _path + " does not contain flag " + !_value);
        } else  {
            // check flag is NOT defined
            Assert.assertEquals(positivLines.size(), 0, "check flag  positive flag for " + _path + " is not defined");
            // check that inverted flag is NOT defined
            Assert.assertEquals(negativLines.size(), 0, "check flag  negative flag for " + _path + " is not defined");
        }
        return this;
    }

    /**
     * Checks that given {@code _list} is defined for given {@code _path}.
     *
     * @param _path     path to check
     * @param _value    value
     * @return this export parser instance
     */
    public ExportParser checkList(final String _path,
                                  final Collection<String> _list)
    {
        Assert.assertEquals(
                new TreeSet<String>(this.getRootLines().get(0).getLines(_path + "/@value")),
                new TreeSet<String>(_list),
                "check that list is defined for " + _path);

        return this;
    }

    /**
     * Returns depending on the <code>_path</code> all found lines.
     *
     * @param _path     path of the lines
     * @return list of all found strings
     * @deprecated use check methods
     */
    @Deprecated()
    public List<String> getLines(final String _path)
    {
        final List<String> ret = new ArrayList<String>();
        final String[] path = _path.split("/");
        for (final Line line : this.rootLines)  {
            if (path[1].equals(line.tag))  {
                line.evalPath(path, 2, ret);
            }
        }

        return ret;
    }

    /**
     * Returns the {@link #rootLines list of all root lines} for the parsed
     * export.
     *
     * @return list of all root lines
     */
    public List<Line> getRootLines()
    {
        return this.rootLines;
    }

    /**
     * Returns the original configuration item update code.
     *
     * @return original code
     * @see #origCode
     */
    public String getOrigCode()
    {
        return this.origCode;
    }

    /**
     * Returns the code (without header).
     *
     * @return code
     * @see #code
     */
    public String getCode()
    {
        return this.code;
    }

    /**
     * Returns the logging text.
     *
     * @return log
     * @see #log
     */
    public String getLog()
    {
        return this.log;
    }

    /**
     * <p>The class is used to build a hierarchy of lines depending of the
     * {@link Line#shifting} where the text of a line begins. The
     * {@link Line#shifting} for all {@link Line#children} of a line is always
     * greater.<p>
     *
     * <p><b>Example:</b><br/>
     * <pre>
     * mql mod form "${NAME}" \
     *     description "DESCRIPTION" \
     *     field name "ABC1" \
     *          add setting "ABC1" "DEF" \
     *     field name "ABC2" \
     *          add setting "ABC2" "DEF"
     * </pre>
     * This source code builds a structure of lines like (tag names with the
     * value in braces):
     * <ul>
     * <li>mql (mod form "${NAME}")
     *     <ul>
     *     <li>description ("DESCRIPTION")</li>
     *     <li>field (name "ABC1")<ul>
     *         <li>add (setting "ABC1" "DEF")</li></ul></li>
     *     <li>field (name "ABC2")<ul>
     *         <li>add (setting "ABC2" "DEF")</li></ul></li>
     *     </ul>
     *     </li>
     * </ul>
     * </p>
     */
    public final class Line
    {
        /** Complete text of this line. */
        private final String line;
        /** First characters before a space of the trimmed line. */
        private final String tag;
        /** Rest of the line after the tag. */
        private final String value;
        /** Parent line (where the {@link #shifting} is lower than the {@link #shifting} of this line). */
        private final Line parent;
        /** Children lines (where the {@link #shifting} is greater than the {@link #shifting} of this line). */
        private final List<ExportParser.Line> children = new ArrayList<ExportParser.Line>();
        /** Count of spaces before first character of the line. */
        private final int shifting;

        /**
         *
         * @param _lineIter     line iterator
         * @param _prevLine     previsous line
         */
        private Line(final Iterator<String> _lineIter,
                     final Line _prevLine)
        {
            this.line = _lineIter.next();
            this.tag = this.line.trim().replaceAll(" .*", "");
            this.value = this.line.trim().substring(this.tag.length()).replaceAll("\\\\$", "").trim();
            this.shifting = this.line.length() - this.line.trim().length();
            if (_prevLine == null)  {
                this.parent = null;
                ExportParser.this.rootLines.add(this);
            } else if ("".equals(this.line))  {
                this.parent = null;
            } else  {
                Line curPar = _prevLine;
                while ((curPar != null) && (curPar.shifting >= this.shifting))  {
                    curPar = curPar.parent;
                }
                this.parent = curPar;
                if (this.parent == null)  {
                    ExportParser.this.rootLines.add(this);
                } else  {
                    this.parent.children.add(this);
                }
            }
            // evaluate next line
            if (_lineIter.hasNext())  {
                new Line(_lineIter, this);
            }
        }

        /**
         * Used to simulate a parsed export but with another root tag.
         *
         * @param _tag          tag of the line
         * @param _value        value of the line
         * @param _children     all child lines
         * @see ExportParser#ExportParser(String, String, String, List)
         */
        private Line(final String _tag,
                     final String _value,
                     final List<ExportParser.Line> _children)
        {
            this.line = null;
            this.parent = null;
            this.shifting = 0;
            this.tag = _tag;
            this.value = _value;
            this.children.addAll(_children);
        }

        /**
         * Returns the {@link #tag} of this line.
         *
         * @return tag of this line
         * @see #tag
         */
        public String getTag()
        {
            return this.tag;
        }

        /**
         * Returns the {@link #value} of this line.
         *
         * @return value of this line
         * @see #value
         */
        public String getValue()
        {
            return this.value;
        }

        /**
         * Returns all {@link #children child lines} for this line definition.
         *
         * @return all child lines
         * @see #children
         */
        public List<Line> getChildren()
        {
            return this.children;
        }

        /**
         * Returns depending on the <code>_path</code> all found lines.
         *
         * @param _path     path of the lines
         * @return list of all found strings
         */
        public List<String> getLines(final String _path)
        {
            final List<String> ret = new ArrayList<String>();

            this.evalPath(_path.split("/"), 0, ret);
            return ret;
        }

        /**
         * Returns found value f
         *
         * @return found value, if not found {@code null}
         */
        public String evalSingleValue(final String _searchedTag)
        {
            String ret = null;
            for (final ExportParser.Line line : this.children)  {
                if (line.tag.equals(_searchedTag))  {
                    Assert.assertTrue(line.children.isEmpty(), "check no child exists");
                    Assert.assertNull(ret, "check is defined only once");
                    ret = line.getValue();
                }
            }
            return ret;
        }

        /**
         *
         * @param _path         array of path to evaluate
         * @param _pathIndex    current index (level) in the {@code _path}
         * @param _ret      list of all found strings
         */
        private void evalPath(final String[] _path,
                              final int _pathIndex,
                              final List<String> _ret)
        {
            String searchedTag = _path[_pathIndex];
            int searchedIdx = -1;
            final int strColIdx = searchedTag.indexOf("[");
            final int endColIdx = searchedTag.indexOf("]");
            if ((strColIdx > 0) && (endColIdx > strColIdx))  {
                searchedIdx = Integer.valueOf(searchedTag.substring(strColIdx + 1, endColIdx));
                searchedTag = searchedTag.substring(0, strColIdx);
            }

            int idx = -1;
            for (final Line line : this.children)  {
                if (line.tag.equals(searchedTag))  {
                    idx++;
                    if ((searchedIdx < 0) || (searchedIdx == idx))  {
                        if (_pathIndex < (_path.length - 2))  {
                            line.evalPath(_path, _pathIndex + 1, _ret);
                        } else if (_pathIndex == (_path.length - 1))  {
                            _ret.add(line.line.trim());
                        } else if ("@name".equals(_path[_pathIndex + 1]))  {
                            _ret.add(line.tag);
                        } else if ("@value".equals(_path[_pathIndex + 1]))  {
                            _ret.add(line.value);
                        }
                    }
                }
            }
        }

        /**
         * String representation of this line including the {@link #tag} and
         * {@link #value}.
         *
         * @return string representation of this line
         */
        @Override
        public String toString()
        {
            return "[line " + this.tag + " (" + this.value + ")]";
        }
    }
}
