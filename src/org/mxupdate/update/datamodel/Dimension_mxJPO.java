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

package org.mxupdate.update.datamodel;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class is used to export and import / update dimension administration
 * objects.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Dimension_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 1831885950156884562L;

    /**
     * Holds the list of already parsed units of this dimension.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     */
    private final Set<Unit> units = new TreeSet<Unit>();

    /**
     * Holds current parsed unit.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     */
    private Unit currentUnit;

    /**
     * Constructor used to initialize the dimension instance with related type
     * definition and dimension name.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the dimension object
     */
    public Dimension_mxJPO(final TypeDef_mxJPO _typeDef,
                           final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all dimension specific expression URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/unitList".equals(_url))  {
            // to be ignored ...
        } else if ("/unitList/unit".equals(_url))  {
            if (this.currentUnit != null)  {
                this.units.add(this.currentUnit);
            }
            this.currentUnit = new Unit();
        } else if ("/unitList/unit/adminProperties".equals(_url))  {
            // to be ignored ...
        } else if ("/unitList/unit/adminProperties/name".equals(_url))  {
            this.currentUnit.name = _content;
        } else if ("/unitList/unit/adminProperties/creationInfo".equals(_url))  {
            // to be ignored ...
        } else if ("/unitList/unit/adminProperties/creationInfo/datetime".equals(_url))  {
            // to be ignored ...
        } else if ("/unitList/unit/adminProperties/description".equals(_url))  {
            this.currentUnit.description = _content;
        } else if ("/unitList/unit/adminProperties/modificationInfo".equals(_url))  {
            // to be ignored ...
        } else if ("/unitList/unit/adminProperties/modificationInfo/datetime".equals(_url))  {
            // to be ignored ...
        } else if ("/unitList/unit/adminProperties/propertyList".equals(_url))  {
            // to be ignored
        } else if ("/unitList/unit/adminProperties/propertyList/property".equals(_url))  {
            if (this.currentUnit.currentUnitProperty != null)  {
                this.currentUnit.properties.add(this.currentUnit.currentUnitProperty);
            }
            this.currentUnit.currentUnitProperty = new AdminProperty_mxJPO();
        } else if (_url.startsWith("/unitList/unit/adminProperties/propertyList/property"))  {
            if (!this.currentUnit.currentUnitProperty.parse(_url.substring(52), _content))  {
                super.parse(_url, _content);
            }
        } else if ("/unitList/unit/unitDefault".equals(_url))  {
            this.currentUnit.defaultUnit = true;
        } else if ("/unitList/unit/unitLabel".equals(_url))  {
            this.currentUnit.label = _content;
        } else if ("/unitList/unit/unitMultiplier".equals(_url))  {
            this.currentUnit.multiplier = Double.parseDouble(_content);
        } else if ("/unitList/unit/unitOffset".equals(_url))  {
            this.currentUnit.offset = Double.parseDouble(_content);
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Prepares unit relation information for dimensions. While parsing current
     * values are stored separately and must be prepared for usage and some
     * unit information is stored in properties and must be extracted:
     * <ul>
     * <li>If a parsed {@link #currentUnit} exists this unit is added to
     *     {@link #units}.</li>
     * <li>If a unit has a parsed {@link Unit#currentUnitProperty} this
     *     property is added to the {@link Unit#properties}.</li>
     * <li>If the {@link Unit#properties} includes settings they are extracted
     *     into {@link Unit#settings}.</li>
     * <li>If the {@link Unit#properties} includes a grouped system information
     *     the values are written into {@link Unit#systemName} and
     *     {@link Unit#systemUnit}.</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if preparation failed
     */
    @Override
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        if (this.currentUnit != null)  {
            this.units.add(this.currentUnit);
            this.currentUnit = null;
        }
        for (final Unit unit : this.units)  {
            if (unit.currentUnitProperty != null)  {
                unit.properties.add(unit.currentUnitProperty);
                unit.currentUnitProperty = null;
            }
            for (final AdminProperty_mxJPO prop : new HashSet<AdminProperty_mxJPO>(unit.properties))  {
                // extract settings
                if (prop.isSetting())  {
                    unit.settings.put(prop.getName().substring(1), prop.getValue());
                    unit.properties.remove(prop);
                } else if ("unit".equals(prop.getRefAdminType()))  {
                    unit.systemName = prop.getName();
                    unit.systemUnit = prop.getRefAdminName();
                    unit.properties.remove(prop);
                }
            }
        }
        super.prepare(_paramCache);
    }

    /**
     * Writes the TCL update files for dimensions. The original method is
     * overwritten because a dimension could not be only updated. A compare
     * must be done in front or otherwise some data is lost.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     * @see #attributes
     */
    @Override
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
            throws IOException
    {
        // write header
        this.writeHeader(_paramCache, _out);

        // write dimension description
        _out.append("updateDimension \"${NAME}\"  {\n")
            .append("  description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"\n")
            .append("  hidden \"").append(Boolean.toString(this.isHidden())).append("\"\n");
        for (final Unit unit : this.units)  {
            _out.append("  unit \"").append(StringUtil_mxJPO.convertTcl(unit.name)).append("\" {\n");
            if (unit.defaultUnit)  {
                _out.append("    default true\n");
            }
            _out.append("    description \"")
                            .append(StringUtil_mxJPO.convertTcl(unit.description)).append("\"\n")
                    .append("    label \"").append(StringUtil_mxJPO.convertTcl(unit.label)).append("\"\n")
                    .append("    multiplier ").append(Double.toString(unit.multiplier)).append("\n")
                    .append("    offset ").append(Double.toString(unit.offset)).append("\n");
            if ((unit.systemName != null) && (unit.systemUnit != null))  {
                _out.append("    system \"").append(StringUtil_mxJPO.convertTcl(unit.systemName))
                    .append("\" to unit \"").append(StringUtil_mxJPO.convertTcl(unit.systemUnit))
                    .append("\"\n");
            }
            // settings
            for (final Map.Entry<String, String> setting : unit.settings.entrySet())  {
                _out.append("    setting \"").append(StringUtil_mxJPO.convertTcl(setting.getKey()))
                    .append("\" \"").append(StringUtil_mxJPO.convertTcl(setting.getValue()))
                    .append("\"\n");
            }
            // properties
            for (final AdminProperty_mxJPO prop : unit.properties)  {
                _out.append("    property \"").append(StringUtil_mxJPO.convertTcl(prop.getName()))
                    .append("\"");
                if ((prop.getRefAdminName() != null) && (prop.getRefAdminType() != null))  {
                    _out.append(" to ").append(prop.getRefAdminType())
                        .append(" \"")
                        .append(StringUtil_mxJPO.convertTcl(prop.getRefAdminName()))
                        .append("\"");
                }
                if (prop.getValue() != null)  {
                    _out.append(" value \"")
                        .append(StringUtil_mxJPO.convertTcl(prop.getValue()))
                        .append("\"");
                }
                _out.append('\n');
            }
            _out.append("  }\n");
        }
        _out.append("}");

        // append properties
        this.writeProperties(_paramCache, _out);
    }

    /**
     * Only implemented as stub because
     * {@link #write(ParameterCache_mxJPO, Appendable)} is new implemented.
     *
     * @param _paramCache   parameter cache (not used)
     * @param _out          appendable instance to the TCL update file (not
     *                      used)
     */
    @Override
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
    {
    }

    /**
     * The class is used to store information about a single unit of a
     * dimension.
     */
    private class Unit
        implements Comparable<Dimension_mxJPO.Unit>
    {
        /**
         * Name of the unit.
         */
        private String name;

        /**
         * Description of the unit.
         */
        private String description;

        /**
         * Label of the unit.
         */
        private String label;

        /**
         * Multiplier of the unit.
         */
        private Double multiplier;

        /**
         * Offset of the unit.
         */
        private Double offset;

        /**
         * Is the unit the default unit?
         */
        private boolean defaultUnit;

        /**
         * System name of the unit (related to {@link #systemUnit}. Internally
         * the system name is stored as property link and could be evaluated
         * only after complete dimension parsing.
         *
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private String systemName;

        /**
         * System unit of the unit (related to {@link #systemName}. Internally
         * the system unit is stored as property link and could be evaluated
         * only after complete dimension parsing.
         *
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private String systemUnit;

        /**
         * Holds all unit specific settings for a dimension. The settings are
         * stored internally as property. Therefore the settings could  be
         * extracted only after a complete parsing.
         *
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private final Map<String,String> settings = new TreeMap<String,String>();

        /**
         * Stores current parsed unit property.
         *
         * @see Dimension_mxJPO#parse(String, String)
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private AdminProperty_mxJPO currentUnitProperty;

        /**
         * Holds all unit specific properties for a single unit. The properties
         * includes settings and the link to the system property. While the
         * dimension is parsed the properties holds also the {@link #settings},
         * {@link #systemName} and {@link #systemUnit}.
         *
         * @see Dimension_mxJPO#parse(String, String)
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private final Set<AdminProperty_mxJPO> properties = new TreeSet<AdminProperty_mxJPO>();

        /**
         * Compares this dimension with <code>_toCompare</code> dimension. The
         * algorithm checks {@link #name} for both dimension and is defined as:
         * <ul>
         * <li>If both {@link #name} are <code>null</code> then a &quot;0&quot;
         *     is returned.</li>
         * <li>If only the {@link #name} of this unit is <code>null</code> then
         *     a &quot;-1&quot; is returned.</li>
         * <li>If only the {@link #name} of the <code>_toCompare</code> unit is
         *     <code>null</code> then a &quot;1&quot; is returned.</li>
         * <li>Otherwise the value of a string compare between both
         *     {@link #name}s is returned.</li>
         * </ul>
         *
         * @param _toCompare    unit instance to which this unit must be
         *                      compared to
         * @return &quot;0&quot; if both units are equal; &quot;1&quot; if
         *         greater; otherwise &quot;-1&quot;
         */
        public int compareTo(final Unit _toCompare)
        {
            return (this.name == null)
                    ? (_toCompare.name == null)
                        ? 0
                        : 1
                    : (_toCompare.name == null)
                        ? -1
                        : this.name.compareTo(_toCompare.name);
        }

        /**
         * Returns the string representation of this unit. The information
         * includes {@link #name}, {@link #description}, {@link #label},
         * {@link #multiplier}, {@link #offset}, {@link #defaultUnit},
         * {@link #systemName}, {@link #systemUnit}, {@link #settings} and
         * {@link #properties}.
         *
         * @return string representation of this unit
         */
        @Override
        public String toString()
        {
            return "[dimension name = " + this.name + ", "
                    + "description = " + this.description + ", "
                    + "label = " + this.label + ", "
                    + "multiplier = " + this.multiplier + ", "
                    + "offset = " + this.offset + ", "
                    + "defaultUnit = " + this.defaultUnit + ", "
                    + "system name = " + this.systemName + ", "
                    + "system unit = " + this.systemUnit + ", "
                    + "settings = " + this.settings + ", "
                    + "properties = " + this.properties + "]";
        }
    }
}
