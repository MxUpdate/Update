/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 *
 * @author tmoxter
 * @version $Id$
 * @todo description
 * @todo program ranges
 */
@net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO(adminType = "attribute",
                                                     title = "ATTRIBUTE",
                                                     filePrefix = {"BOOLEAN_", "DATE_", "INTEGER_", "REAL_", "STRING_"},
                                                     fileSuffix = ".tcl",
                                                     filePath = "datamodel/attribute",
                                                     description = "attribute")
public class Attribute_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMWithTriggers_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -502565364090887306L;

    /**
     * Set holding all rules referencing this attribute.
     */
    final Set<String> rules = new TreeSet<String>();

    /**
     * Stores the ranges of the attribute (used while parsing the XML
     * attribute).
     *
     * @see #parse(String, String)
     */
    final Stack<Range> ranges = new Stack<Range>();

    /**
     * All ranges but sorted after they are prepared.
     *
     * @see #ranges
     * @see #prepare(Context)   sort the ranges
     */
    final Set<Range> rangesSorted = new TreeSet<Range>();

    /**
     * Stores the attribute type of this attribute.
     */
    private String type = null;

    /**
     * Default value of the attribute.
     */
    private String defaultValue = null;

    /**
     * The attribute is a multi line attribute.
     */
    private boolean multiline = false;

    /**
     * Returns the file name for this matrix object. The file name is a
     * concatenation of the {@link #type} in upper case (for date time
     * attribute type only &quot;DATE&quot;), an underline
     * (&quot;_&quot;), the {@link #name} of the matrix object and
     *  &quot;.tcl&quot; as extension.<br/>
     * The method overwrites the original method, because the attribute file
     * name is not the same than all other update file names.
     *
     * @return file name of this matrix object
     * @see #type
     */
    @Override
    public String getFileName()
    {
        return this.type.toUpperCase().replaceAll("TIME$", "") + "_" + getName() + ".tcl";
    }

    /**
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/accessRuleRef".equals(_url))  {
            this.rules.add(_content);
        } else if ("/defaultValue".equals(_url))  {
            this.defaultValue = _content;
        } else if ("/multiline".equals(_url))  {
            this.multiline = true;
        } else if ("/primitiveType".equals(_url))  {
            this.type = _content;

        } else if ("/rangeList".equals(_url))  {
            // to be ignored ...
        } else if ("/rangeList/range".equals(_url))  {
            this.ranges.add(new Range());
        } else if ("/rangeList/range/rangeType".equals(_url))  {
            this.ranges.peek().type = _content;
        } else if ("/rangeList/range/rangeValue".equals(_url))  {
            this.ranges.peek().value1 = _content;
        } else if ("/rangeList/range/includingValue".equals(_url))  {
            this.ranges.peek().include1 = true;
        } else if ("/rangeList/range/rangeSecondValue".equals(_url))  {
            this.ranges.peek().value2 = _content;
        } else if ("/rangeList/range/includingSecondValue".equals(_url))  {
            this.ranges.peek().include2 = true;

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * The ranges are sorted.
     *
     * @param _context   context for this request
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        for (final Range range : this.ranges)  {
            this.rangesSorted.add(range);
        }
        super.prepare(_context);
    }

    /**
     * Writes specific information about the cached attribute to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden");
        if ("string".equalsIgnoreCase(this.type))  {
            _out.append(" \\\n    ").append(this.multiline ? "" : "!").append("multiline");
        }
        for (final String rule : this.rules)  {
            _out.append(" \\\n    add rule \"").append(convert(rule)).append("\"");
        }
        if (this.defaultValue != null)  {
            _out.append(" \\\n    default \"").append(convert(this.defaultValue)).append("\"");
        } else  {
            _out.append(" \\\n    default \"\"");
        }
        // append triggers
        this.writeTriggers(_out);
        // append ranges
        for (final Range range : this.rangesSorted)  {
            range.write(_out);
        }
    }

    /**
     * Creates given attribute from given type with given name. Because the
     * type of the attribute is defined with the file name (as prefix), the
     * original create method must be overwritten.
     *
     * @param _context          context for this request
     * @param _file             file for which the attribute must be created
     *                          (needed to define the attribute
     *                          type)
     * @param _name             name of attribute to create
     */
    @Override
    public void create(final Context _context,
                       final File _file,
                       final String _name)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(getInfoAnno().adminType())
                .append(" \"").append(_name).append("\" ")
                .append(" type ").append(_file.getName().replaceAll("_.*", "").toLowerCase());
        this.execMql(_context, cmd);
    }

    /**
     * Appends the MQL statement to reset this attribute:
     * <ul>
     * <li>set to not hidden</li>
     * <li>reset description and default value</li>
     * <li>remove all triggers and ranges</li>
     * </ul>
     *
     * @param _context  context for this request
     * @param _cmd      string builder used to append the MQL statements
     */
    @Override
    protected void appendResetMQL(final Context _context,
                                  final StringBuilder _cmd)
    {
        _cmd.append("mod ").append(getInfoAnno().adminType())
            .append(" \"").append(getName()).append('\"')
            .append(" !hidden description \"\" default \"\"");
        // reset triggers
        this.appendResetTriggerMQLStatements(_cmd);
        // remove rules
        for (final String rule : this.rules)  {
            _cmd.append(" remove rule \"").append(rule).append('\"');
        }
        // remove ranges
// TODO: between? program?
        for (final Range range : this.rangesSorted)  {
            _cmd.append(" remove range ").append(range.type)
                .append(" \"").append(range.value1).append("\"");
        }
    }

    /**
     * Class holding range values of this attribute.
     */
    private class Range
            implements Comparable<Range>
    {
        /**
         * Holds the range type. Typically following range types are known:
         * <ul>
         * <li>between<li>
         * <li>equal<li>
         * <li>greaterthan<li>
         * <li>greaterthanequal<li>
         * <li>lessthan<li>
         * <li>lessthanequal<li>
         * <li>match<li>
         * <li>notequal<li>
         * <li>notmatch<li>
         * <li>notsmatch<li>
         * <li>smatch<li>
         * </ul>
         */
        String type = null;

        /**
         * Hold the first range value.
         */
        String value1 = null;

        /**
         * Holds the second range value.
         */
        String value2 = null;

        /**
         * Include first value (used for range type &quot;between&quot;).
         */
        Boolean include1 = false;

        /**
         * Include second value (used for range type &quot;between&quot;).
         */
        Boolean include2 = false;

        /**
         * Write this range value to the writer instance.
         * Following range types are converted:
         * <ul>
         * <li>&quot;equal&quot; to &quot;=&quot;</li>
         * <li>&quot;greaterthan&quot; to &quot;&gt;&quot;</li>
         * <li>&quot;greaterthanequal&quot; to &quot;&gt;=&quot;</li>
         * <li>&quot;lessthan&quot; to &quot;&lt;&quot;</li>
         * <li>&quot;lessthanequal&quot; to &quot;&lt;=&quot;</li>
         * <li>&quot;notequal&quot; to &quot;!=&quot;</li>
         * <li>&quot;notmatch&quot; to &quot;!match&quot;</li>
         * <li>&quot;notsmatch&quot; to &quot;!smatch&quot;</li>
         * </ul>
         *
         * @param _out  writer instance
         * @throws IOException if write to the writer instance is not possible
         */
        private void write(final Writer _out)
                throws IOException
        {
            _out.append(" \\\n    add range ");
            if ("equal".equals(this.type))  {
                _out.append('=');
            } else if ("greaterthan".equals(this.type))  {
                _out.append(">");
            } else if ("greaterthanequal".equals(this.type))  {
                _out.append(">=");
            } else if ("lessthan".equals(this.type))  {
                _out.append("<");
            } else if ("lessthanequal".equals(this.type))  {
                _out.append("<=");
            } else if ("notequal".equals(this.type))  {
                _out.append("!=");
            } else if ("notmatch".equals(this.type))  {
                _out.append("!match");
            } else if ("notsmatch".equals(this.type))  {
                _out.append("!smatch");
            } else  {
                _out.append(this.type);
            }
            _out.append(" \"").append(convert(this.value1)).append("\"");
            if ("between".equals(this.type))  {
                if (this.include1)  {
                    _out.append(" inclusive");
                } else  {
                    _out.append(" exclusive");
                }
                _out.append(" \"").append(convert(this.value2)).append("\"");
                if (this.include2)  {
                    _out.append(" inclusive");
                } else  {
                    _out.append(" exclusive");
                }
            }
        }

        /**
         * Compare this range instance with given range instance. The compare
         * is done for each instance variable in this order:
         * <ul>
         * <li>{@link #type}</li>
         * <li>{@link #include1}</li>
         * <li>{@link #include2}</li>
         * <li>{@link #value1}</li>
         * <li>{@link #value2}</li>
         * </ul>
         * If one of the compared instance variables are not equal, the
         * compared value is returned. So only if all instance variable has the
         * same values the ranges itself are identically.
         *
         * @param _range        range instance to which this instance must be
         *                      compared to
         */
        public int compareTo(final Range _range)
        {
            int ret = this.type.compareTo(_range.type);
            if (ret == 0)  {
                ret = this.include1.compareTo(_range.include1);
            }
            if (ret == 0)  {
                ret = this.include2.compareTo(_range.include2);
            }
            if (ret == 0)  {
                ret = (this.value1 != null)
                      ? ((_range.value1 != null) ? this.value1.compareTo(_range.value1) : 0)
                      : -1;
            }
            if (ret == 0)  {
                ret = (this.value2 != null)
                      ? ((_range.value2 != null) ? this.value2.compareTo(_range.value2) : 0)
                      : 1;
            }
            return ret;
        }
    }
}
