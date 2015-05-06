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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.data.util.PropertyDefList;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;

/**
 * Used to define a dimension, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class DimensionData
    extends AbstractAdminData<DimensionData>
{
    /**
//     * Within export the description must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
//        ExpressionData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /** All units for this dimension. */
    private final List<UnitData> units = new ArrayList<UnitData>();

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
        super(_test, AbstractTest.CI.DM_DIMENSION, _name, DimensionData.REQUIRED_EXPORT_VALUES, null);
    }

    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();

        this.append4CIFileHeader(strg);

        strg.append("updateDimension \"${NAME}\" {\n")
            .append("  hidden \"").append(this.getFlags().get("hidden") != null ? this.getFlags().get("hidden") : false).append("\"\n");

        // append values
        this.getValues().appendUpdate("  ", strg, "\n");

        // append state information
        for (final UnitData unit : this.units)
        {
            unit.append4CIFile(strg);
        }

        // append properties
        this.getProperties().appendCIFileUpdateFormat("  ", strg);

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

            cmd.append(";\n")
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to dimension \"").append(AbstractTest.convertMql(this.getName())).append("\"");

            this.getTest().mql(cmd);
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
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        // check symbolic name
        Assert.assertEquals(
                _exportParser.getSymbolicName(),
                this.getSymbolicName(),
                "check symbolic name");

        // check for all required values
        for (final String valueName : DimensionData.REQUIRED_EXPORT_VALUES.keySet())  {
            Assert.assertEquals(_exportParser.getLines("/updateDimension/" + valueName + "/@value").size(),
                                1,
                                "required check that minimum and maximum one " + valueName + " is defined");
        }

        // check for defined values
        this.getValues().checkExport(_exportParser);

        // check for hidden flag
        if ((this.getFlags().get("hidden") == null) || !this.getFlags().get("hidden"))  {
            this.checkSingleValue(_exportParser,
                                  "hidden flag (must be false)",
                                  "hidden",
                                   "\"false\"");
        } else  {
            this.checkSingleValue(_exportParser,
                                  "hidden flag (must be true)",
                                  "hidden",
                                  "\"true\"");
        }

        // check all units
        for (final UnitData unit : this.units)
        {
            unit.checkExport(_exportParser);
        }

        // check for properties
        this.getProperties().checkExport(_exportParser.getLines("/updateDimension/property/@value"));
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
        /** Values with quotations of this unit. */
        private final Map<String,String> valuesWithQuots = new HashMap<String,String>();
        /** Values w/o quotations of this unit. */
        private final Map<String,String> valuesWOQuots = new HashMap<String,String>();
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
         * Defines a new value entry which is put into {@link #values}.
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
            this.properties.add(_property);
            return this;
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4CIFile(final StringBuilder _cmd)
        {
            _cmd.append("  unit \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\" {\n");
            for (final Map.Entry<String,String> value : this.valuesWithQuots.entrySet())  {
                _cmd.append("      ").append(value.getKey())
                    .append(" \"").append(StringUtil_mxJPO.convertTcl(value.getValue())).append("\"\n");
            }
            for (final Map.Entry<String,String> value : this.valuesWOQuots.entrySet())  {
                _cmd.append("      ").append(value.getKey())
                    .append(" ").append(value.getValue()).append("\n");
            }

            this.properties.appendCIFileUpdateFormat("      ", _cmd);

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
            _cmd.append("  unit \"").append(StringUtil_mxJPO.convertMql(this.name)).append("\"");
            for (final Map.Entry<String,String> value : this.valuesWithQuots.entrySet())  {
                _cmd.append(' ');
                if ("description".equals(value.getKey()))  {
                    _cmd.append("unitdescription");
                } else  {
                    _cmd.append(value.getKey());
                };
                _cmd.append(" \"").append(StringUtil_mxJPO.convertMql(value.getValue())).append('\"');
            }
            for (final Map.Entry<String,String> value : this.valuesWOQuots.entrySet())  {
                _cmd.append(' ');
                if ("default".equals(value.getKey()))  {
                    if (!"true".equalsIgnoreCase(value.getValue().toString()))  {
                        _cmd.append("!");
                    }
                    _cmd.append("default");
                } else  {
                    _cmd.append(value.getKey())
                        .append(" ").append(value.getValue());
                }
            }
            // append properties
            this.properties.append4Create(_cmd);
        }

        /**
         * Checks the export.
         *
         * @param _exportParser     export parsed
         * @throws MatrixException if information could not be fetched
         */
        public void checkExport(final ExportParser _exportParser)
            throws MatrixException
        {
            boolean found = false;
            final String value = "\"" + AbstractTest.convertTcl(this.name) + "\"";
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
                                "\"" + AbstractTest.convertTcl(entry.getValue()) + "\"");
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
            Assert.assertTrue(found, "check that state '" + this.name + "' is found");

            // check properties
            this.properties.checkExport(propLines);
        }
    }
}
