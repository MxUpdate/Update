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

package org.mxupdate.test.data.datamodel;

import java.util.HashSet;
import java.util.Set;

import matrix.db.AttributeType;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * The class is used to define all types of attributes, to create them and test
 * the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <T>   defines the class which is derived from this class
 */
public abstract class AbstractAttribute<T extends AbstractAttribute<?>>
    extends AbstractDataWithTrigger<T>
{
    /**
     * Within export the description and default value must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(2);
    static  {
        AbstractAttribute.REQUIRED_EXPORT_VALUES.add("description");
        AbstractAttribute.REQUIRED_EXPORT_VALUES.add("default");
    }

    /**
     * Attribute type of the attribute (string, integer, ....).
     */
    private final String attrType;

    /**
     * Ranges of this attribute.
     *
     * @see #addRange(Range)
     */
    private final Set<Range> ranges = new HashSet<Range>();

    /**
     *
     * @param _test     related test instance
     * @param _ci       related configuration type of this attribute
     * @param _name     name of this attribute
     * @param _attrType type of this attribute
     * @param _filePrefix   prefix for the file name
     */
    protected AbstractAttribute(final AbstractTest _test,
                                final AbstractTest.CI _ci,
                                final String _name,
                                final String _attrType,
                                final String _filePrefix)
    {
        super(_test, _ci, _name, _filePrefix, AbstractAttribute.REQUIRED_EXPORT_VALUES);
        this.attrType = _attrType;
    }

    /**
     * Defines a new attribute range.
     *
     * @param _range    range to assign to this attribute
     * @return this instance
     * @see #ranges
     */
    @SuppressWarnings("unchecked")
    public T addRange(final Range _range)
    {
        this.ranges.add(_range);
        return (T) this;
    }

    /**
     * Creates a this attribute with all values and settings.
     *
     * @return this attribute instance
     * @throws MatrixException if create failed
     */
    @Override()
    @SuppressWarnings("unchecked")
    public T create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add attribute \"").append(AbstractTest.convertMql(this.getName()))
               .append("\" type ").append(this.attrType);

            this.append4CreateValues(cmd);

            for (final Range range : this.ranges)  {
                range.appendCreate(cmd);
            }

            this.getTest().mql(cmd);
        }
        return (T) this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    @SuppressWarnings("unchecked")
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check range directly fetched from attribute
        final AttributeType attr = new AttributeType(this.getName());
        attr.open(this.getTest().getContext());
        final StringList list = attr.getChoices(this.getTest().getContext());
        attr.close(this.getTest().getContext());
        final Set<String> l = new HashSet<String>();
        if (list != null)  {
            l.addAll(list);
        }
        for (final Range range : this.ranges)  {
            Assert.assertTrue(l.contains(range.value),
                              "check that range value " + range.value + " is defined");
        }
        Assert.assertEquals(l.size(), this.ranges.size(), "check that all ranges are defined");
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this command.
     *
     * @return code for the configuration item update file
     */
    @Override
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod attribute \"${NAME}\"");
        this.append4CIFileValues(cmd);

        return cmd.toString();
    }

    /**
     * Evaluates all 'adds' for ranges in the configuration item file.
     *
     * @param _needAdds     set with add strings used to append the adds for
     *                      {@link #ranges}
     */
    @Override
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);
        for (final Range range : this.ranges)  {
            range.evalAdds4CheckExport(_needAdds);
        }
    }

    /**
     * Used to define ranges with one value.
     */
    public static class Range
    {
        /**
         * Comparator of this range.
         */
        private final String comparator;

        /**
         * Value of this range.
         */
        private final String value;

        /**
         * Initializes the range values.
         *
         * @param _comparator   comparator for the range
         * @param _value        range value itself
         */
        public Range(final String _comparator,
                     final String _value)
        {
            this.comparator = _comparator;
            this.value = _value;
        }

        /**
         * Appends this range will so that the range is defined while the
         * attribute is created.
         *
         * @param _cmd  string builder where the MQL commands will be appended
         */
        protected void appendCreate(final StringBuilder _cmd)
        {
            _cmd.append(" range ").append(this.comparator)
                .append(" \"").append(AbstractTest.convertMql(this.value))
                .append("\"");
        }

        /**
         * Appends the add statement in TCL code for this range.
         *
         * @param _needAdds     set with add strings used to append the adds
         *                      for this range
         */
        protected void evalAdds4CheckExport(final Set<String> _needAdds)
        {
            final StringBuilder cmd = new StringBuilder()
                    .append("range ").append(this.comparator)
                    .append(" \"").append(AbstractTest.convertTclDoubleEscaped(this.value))
                    .append("\"");
            _needAdds.add(cmd.toString());
        }
    }
}
