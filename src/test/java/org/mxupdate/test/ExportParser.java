/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mxupdate.test.AbstractTest.CI;

/**
 * The class parses informations from an MxUpdate export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class ExportParser
{
    /**
     * Defines the string where the header of symbolic names starts.
     */
    private static final String HEADER_SYMBOLIC_NAME = "# SYMBOLIC NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Defines the string which defines the header start and end.
     */
    private static final String HEADER_START_END
            = "\n################################################################################\n";

    /**
     * Reference to the configuration item enumeration.
     */
    private final CI ci;

    /**
     * Parsed name from the header.
     */
    private final String name;

    /**
     * Parsed symbolic name from the header.
     */
    private final String symbolicName;

    /**
     * Related code of the export.
     */
    private final String code;

    /**
     * List of all root lines.
     */
    private final List<Line> rootLines = new ArrayList<Line>();

    /**
     *
     * @param _ci       configuration item type
     * @param _code     code of the configuration item
     */
    public ExportParser(final CI _ci,
                        final String _code)
    {
        this.ci = _ci;
        // parse symbolic name
        final int posSymbolicName = _code.indexOf(ExportParser.HEADER_SYMBOLIC_NAME);
        if (posSymbolicName >= 0)  {
            final int start = posSymbolicName + ExportParser.HEADER_SYMBOLIC_NAME.length();
            final int end = _code.indexOf('\n', start);
            this.symbolicName = _code.substring(start, end).trim();
        } else  {
            this.symbolicName = null;
        }
        // parse name in the header
        final int posName = _code.indexOf("# " + this.ci.header + ":\n#");
        if (posName >= 0)  {
            final int start = posName + (this.ci.header.length() * 2) + 9;
            final int end = _code.indexOf('\n', start);
            this.name = _code.substring(start, end).trim();
        } else  {
            this.name = null;
        }
        // extract update code
        final int posHeaderEnd = _code.lastIndexOf(ExportParser.HEADER_START_END);
        if (posHeaderEnd >=0)  {
            this.code = _code.substring(posHeaderEnd + ExportParser.HEADER_START_END.length()).trim();
        } else  {
            this.code = _code.trim();
        }
        // parse all lines
        new Line(Arrays.asList(this.code.split("\n")).iterator(), null);
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
        final String[] path = _path.split("/");
        for (final Line line : this.rootLines)  {
            line.evalPath(path, 1, ret);
        }
        return ret;
    }

    /**
     * Returns the name from the parsed header.
     *
     * @return parsed name
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the symbolic name from the parsed header.
     *
     * @return parsed symbolic name
     * @see #symbolicName
     */
    public String getSymbolicName()
    {
        return this.symbolicName;
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
        /**
         * Complete text of this line.
         */
        private final String line;

        /**
         * First characters before a space of the trimmed line.
         */
        private final String tag;

        /**
         * Rest of the line after the tag.
         */
        private final String value;

        /**
         * Parent line (where the {@link #shifting} is lower than the
         * {@link #shifting} of this line).
         */
        private final Line parent;

        /**
         * Children lines (where the {@link #shifting} is greater than the
         * {@link #shifting} of this line).
         */
        private final List<ExportParser.Line> children = new ArrayList<ExportParser.Line>();

        /**
         * Count of spaces before first character of the line.
         */
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
            } else  {
                Line curPar = _prevLine;
                while ((curPar.shifting >= this.shifting) && (curPar != null))  {
                    curPar = curPar.parent;
                }
                this.parent = curPar;
                this.parent.children.add(this);
            }
            if (_lineIter.hasNext())  {
                new Line(_lineIter, this);
            }
        }

        /**
         *
         * @param _path     array of path to evaluate
         * @param _index    current index (level) in the <code>_path</code>
         * @param _ret      list of all found strings
         */
        protected void evalPath(final String[] _path,
                                final int _index,
                                final List<String> _ret)
        {
            final String searchedTag = _path[_index];
            if (this.tag.equals(searchedTag))  {

                if (_index < (_path.length - 2))  {
                    for (final Line child : this.children)  {
                        child.evalPath(_path, _index + 1, _ret);
                    }
                } else if ("@value".equals(_path[_index + 1]))  {
                    _ret.add(this.value);
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
