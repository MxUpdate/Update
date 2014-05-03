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
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
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
            .append("  hidden \"").append(this.getFlag("hidden") != null ? this.getFlag("hidden") : false).append("\"\n");

        // values
        for (final Map.Entry<String,Object> entry : this.getValues().entrySet())  {
            if (entry.getValue() instanceof Character)  {
                strg.append(' ').append(entry.getKey()).append(' ').append(entry.getValue());
            } else  {
                strg.append(' ').append(entry.getKey()).append(" \"")
                    .append(AbstractTest.convertTcl(entry.getValue().toString()))
                    .append('\"');
            }
        }

        // append state information
        for (final UnitData unit : this.units)
        {
            unit.append4CIFile(strg);
        }

        strg.append("}");

        // append properties
        for (final PropertyDef prop : this.getProperties())  {
            strg.append('\n').append(prop.getCITCLString(this.getCI()));
        }

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
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        this.checkExportProperties(_exportParser);

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
        for (final Map.Entry<String,Object> entry : this.getValues().entrySet())  {
            if (entry.getValue() instanceof Character)  {
                this.checkSingleValue(_exportParser,
                        entry.getKey(),
                        entry.getKey(),
                        entry.getValue().toString());
            } else  {
                this.checkSingleValue(_exportParser,
                                      entry.getKey(),
                                      entry.getKey(),
                                      "\"" + AbstractTest.convertTcl(entry.getValue().toString()) + "\"");
            }
        }

        // check for hidden flag
        if ((this.getFlag("hidden") == null) || !this.getFlag("hidden"))  {
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

        // check all states
        for (final UnitData unit : this.units)
        {
            unit.checkExport(_exportParser);
        }
    }

    /**
     * {@inheritDoc}
     * The original method is overwritten because for dimensions another path
     * exists for the values.
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _value            value to check (or <code>null</code> if value
     *                          is not defined)
     */
    @Override()
    public void checkSingleValue(final ExportParser _exportParser,
                                 final String _kind,
                                 final String _tag,
                                 final String _value)
    {
        if (_value != null)  {
            Assert.assertEquals(
                    _exportParser.getLines("/updateDimension/" + _tag + "/@value").size(),
                    1,
                    "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is defined");
            Assert.assertEquals(
                    _exportParser.getLines("/updateDimension/" + _tag + "/@value").get(0),
                    _value,
                    "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is " + _value);

        } else  {
            Assert.assertEquals(
                    _exportParser.getLines("/updateDimension/" + _tag + "/@value").size(),
                    0,
                    "check " + _kind + " '" + this.getName() + "' that no " + _tag + " is defined");
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
        /** Values with quotations of this unit. */
        private final Map<String,String> valuesWithQuots = new HashMap<String,String>();
        /** Values w/o quotations of this unit. */
        private final Map<String,String> valuesWOQuots = new HashMap<String,String>();

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

            _cmd.append("  }\n");
        }

        /**
         * Appends the MQL statements to create the policy.
         *
         * @param _cmd  string builder where to append the MQL statements
         */
        protected void append4Create(final StringBuilder _cmd)
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
            for (final ExportParser.Line line : _exportParser.getRootLines().get(0).getChildren())  {
                if ("unit".equals(line.getTag()) && line.getValue().startsWith(value))  {
                    found = true;

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
        }
    }
}
