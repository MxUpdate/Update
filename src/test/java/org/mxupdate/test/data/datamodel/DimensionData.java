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

package org.mxupdate.test.data.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.util.FlagList;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.data.util.PropertyDefList;
import org.testng.Assert;

import matrix.util.MatrixException;

/**
 * Used to define a dimension, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class DimensionData
    extends AbstractAdminData<DimensionData>
{
    /** All units for this dimension. */
    private final List<UnitData> units = new ArrayList<>();

    /**
     * Initialize this expression data with given {@code _name}.
     *
     * @param _test     related test implementation (where this dimension is
     *                  defined)
     * @param _name     name of the dimension
     */
    public DimensionData(final AbstractTest _test,
                         final String _name)
    {
        super(_test, AbstractTest.CI.DM_DIMENSION, _name);
    }

    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();

        this.append4CIFileHeader(strg);

        strg.append("mxUpdate dimension \"${NAME}\" {\n");

        this.getValues().append4Update("    ", strg);
        this.getFlags().append4Update("    ", strg);

        for (final UnitData unit : this.units)
        {
            unit.append4CIFile(strg);
        }

        // append properties
        this.getProperties().append4Update("    ", strg);

        strg.append("}");

        return strg.toString();
    }

    @Override()
    public DimensionData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            this.createDependings();

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add dimension \"").append(AbstractTest.convertMql(this.getName())).append('\"');
            this.append4Create(cmd);

            // append state information
            for (final UnitData unit : this.units)
            {
                unit.append4Create(cmd);
            }

            this.getTest().mql(cmd);

            this.getTest().mql(new StringBuilder()
                    .append("escape add property ").append(this.getSymbolicName())
                    .append(" on program eServiceSchemaVariableMapping.tcl")
                    .append(" to dimension \"").append(AbstractTest.convertMql(this.getName())).append("\""));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * Creates for all units depending referenced property admin objects.
     */
    @Override()
    public DimensionData createDependings()
        throws MatrixException
    {
        super.createDependings();

        for (final UnitData unit : this.units)  {
            unit.properties.createDependings();
        }

        return this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        // check all units
        for (final UnitData unit : this.units)
        {
            unit.checkExport(_exportParser);
        }
    }

    /**
     * Appends {@code _unit} to this dimension.
     *
     * @param _unit     unit to append
     * @return this dimension instance
     * @see #units
     */
    public DimensionData addUnit(final UnitData _unit)
    {
        this.units.add(_unit);
        return this;
    }

    /**
     * Returns all defined {@link #units} of this dimension.
     *
     * @return defined units
     */
    public List<UnitData> getUnits()
    {
        return this.units;
    }

    /**
     * Definition of a unit.
     */
    public static class UnitData
    {
        /** Name of the unit. */
        private final String name;
        /** Flags of the unit data. */
        private final FlagList flags = new FlagList();
        /** Values with quotations of this unit. */
        private final Map<String,String> valuesWithQuots = new HashMap<>();
        /** Values w/o quotations of this unit. */
        private final Map<String,String> valuesWOQuots = new HashMap<>();
        /** List of properties. */
        private final PropertyDefList properties = new PropertyDefList();

        /**
         * Defines a new unit with name.
         *
         * @param _name     unit name
         */
        public UnitData(final String _name)
        {
            this.name = _name;
        }

        /**
         * Returns the {@link #name} of the unit.
         *
         * @return unit name
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * Defines a new flag entry which is put into {@link #flags}.
         *
         * @param _key      key of the value (e.g. &quot;description&quot;)
         * @param _value    value
         * @return this unit instance
         */
        public UnitData setFlag(final String _key,
                                final boolean _value)
        {
            this.flags.setFlag(_key, _value);
            return this;
        }

        /**
         * Defines a new value entry which is put into
         * {@link #valuesWithQuots values with quotes}.
         *
         * @param _key      key of the value (e.g. &quot;description&quot;)
         * @param _value    value of the value
         * @return this unit instance
         */
        public UnitData setValueWithQuots(final String _key,
                                          final String _value)
        {
            this.valuesWithQuots.put(_key, _value);
            return this;
        }

        /**
         * Returns all {@link #valuesWithQuots values with quotations}.
         *
         * @return values
         */
        public Map<String,String> getValuesWithQuots()
        {
            return this.valuesWithQuots;
        }

        /**
         * Defines a new value entry which is put into {@link #values}.
         *
         * @param _key      key of the value (e.g. &quot;description&quot;)
         * @param _value    value of the value
         * @return this unit instance
         */
        public UnitData setValueWOQuots(final String _key,
                                        final String _value)
        {
            this.valuesWOQuots.put(_key, _value);
            return this;
        }

        /**
         * Returns all {@link #valuesWOQuots values w/o quotations}.
         *
         * @return values
         */
        public Map<String,String> getValuesWOQuots()
        {
            return this.valuesWOQuots;
        }

        /**
         * Assigns {@code _property} to this unit {@link #properties}.
         *
         * @param _property     property to add / assign
         * @return this data piece instance
         */
        public UnitData addProperty(final PropertyDef _property)
        {
            this.properties.addProperty(_property);
            return this;
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4CIFile(final StringBuilder _cmd)
        {
            _cmd.append("    unit \"").append(AbstractTest.convertUpdate(this.name)).append("\" {\n");
            for (final Map.Entry<String,String> value : this.valuesWithQuots.entrySet())  {
                _cmd.append("        ").append(value.getKey()).append(" \"").append(AbstractTest.convertUpdate(value.getValue())).append("\"\n");
            }
            for (final Map.Entry<String,String> value : this.valuesWOQuots.entrySet())  {
                _cmd.append("        ").append(value.getKey()).append(" ").append(AbstractTest.convertUpdate(value.getValue())).append("\n");
            }

            this.properties.append4Update("        ", _cmd);

            _cmd.append("  }\n");
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         * @throws MatrixException if create of referenced admin object for
         *                         properties failed
         */
        protected void append4Create(final StringBuilder _cmd)
            throws MatrixException
        {
            _cmd.append("  unit \"").append(AbstractTest.convertMql(this.name)).append("\"");
            this.flags.append4Create(_cmd);
            for (final Map.Entry<String,String> value : this.valuesWithQuots.entrySet())  {
                _cmd.append(' ');
                if ("description".equals(value.getKey()))  {
                    _cmd.append("unitdescription");
                } else  {
                    _cmd.append(value.getKey());
                };
                _cmd.append(" \"").append(AbstractTest.convertMql(value.getValue())).append('\"');
            }
            for (final Map.Entry<String,String> value : this.valuesWOQuots.entrySet())  {
                _cmd.append(' ').append(value.getKey()).append(" ").append(value.getValue());
            }
            // append properties
            this.properties.append4Create(_cmd);
        }

        /**
         * Checks the export.
         *
         * @param _exportParser     export parsed
         */
        public void checkExport(final ExportParser _exportParser)
        {
            boolean found = false;
            final String value = "\"" + AbstractTest.convertUpdate(this.name) + "\"";
            List<String> propLines = null;
            for (final ExportParser.Line line : _exportParser.getRootLines().get(0).getChildren())  {
                if ("unit".equals(line.getTag()) && line.getValue().startsWith(value))  {
                    found = true;

                    // check for property
                    propLines = line.getLines("property/@value");

                    // check for defined values
                    for (final Map.Entry<String,String> entry : this.valuesWithQuots.entrySet())  {
                        PolicyData.checkSingleValue(
                                line,
                                entry.getKey(),
                                entry.getKey(),
                                "\"" + AbstractTest.convertUpdate(entry.getValue()) + "\"");
                    }
                    for (final Map.Entry<String,String> entry : this.valuesWOQuots.entrySet())  {
                        PolicyData.checkSingleValue(
                                line,
                                entry.getKey(),
                                entry.getKey(),
                                entry.getValue());
                    }
                }
            }
            Assert.assertTrue(found, "check that unit '" + this.name + "' is found");

            // check properties
            this.properties.checkExport(propLines);
        }
    }
}
