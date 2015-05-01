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

package org.mxupdate.update.datamodel;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.AdminPropertyList_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO.AdminProperty;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * The class is used to export and import / update dimension administration
 * objects.
 *
 * @author The MxUpdate Team
 */
public class Dimension_mxJPO
    extends AbstractAdminObject_mxJPO<Dimension_mxJPO>
{
    /** Set of all ignored URLs from the XML definition for dimensions. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        Dimension_mxJPO.IGNORED_URLS.add("/unitList");
        Dimension_mxJPO.IGNORED_URLS.add("/unitList/unit/adminProperties");
        Dimension_mxJPO.IGNORED_URLS.add("/unitList/unit/adminProperties/creationInfo");
        Dimension_mxJPO.IGNORED_URLS.add("/unitList/unit/adminProperties/creationInfo/datetime");
        Dimension_mxJPO.IGNORED_URLS.add("/unitList/unit/adminProperties/modificationInfo");
        Dimension_mxJPO.IGNORED_URLS.add("/unitList/unit/adminProperties/modificationInfo/datetime");
        Dimension_mxJPO.IGNORED_URLS.add("/unitList/unit/adminProperties/propertyList");
    }

    /** Holds the list of already parsed units of this dimension. */
    private final Set<Unit> units = new TreeSet<Unit>();
    /** Holds current parsed unit. */
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

    @Override()
    public void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException
    {
        new DimensionParser_mxJPO(new StringReader(_code)).parse(this);
        this.prepare();
    }

    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Dimension_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/unitList/unit".equals(_url))  {
            if (this.currentUnit != null)  {
                this.units.add(this.currentUnit);
            }
            this.currentUnit = new Unit();
            parsed = true;
        } else if ("/unitList/unit/adminProperties/name".equals(_url))  {
            this.currentUnit.name = _content;
            parsed = true;
        } else if ("/unitList/unit/adminProperties/description".equals(_url))  {
            this.currentUnit.description = _content;
            parsed = true;
        } else if (_url.startsWith("/unitList/unit/adminProperties/propertyList"))  {
            parsed = this.currentUnit.properties.parse(_paramCache, _url.substring(43), _content);
        } else if ("/unitList/unit/unitDefault".equals(_url))  {
            this.currentUnit.defaultUnit = true;
            parsed = true;
        } else if ("/unitList/unit/unitLabel".equals(_url))  {
            this.currentUnit.label = _content;
            parsed = true;
        } else if ("/unitList/unit/unitMultiplier".equals(_url))  {
            this.currentUnit.multiplier = Double.parseDouble(_content);
            parsed = true;
        } else if ("/unitList/unit/unitOffset".equals(_url))  {
            this.currentUnit.offset = Double.parseDouble(_content);
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
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
     *     the values are written into {@link Unit#systemInfos}.</li>
     * </ul>
     */
    @Override()
    protected void prepare()
    {
        if (this.currentUnit != null)  {
            this.units.add(this.currentUnit);
            this.currentUnit = null;
        }
        for (final Unit unit : this.units)  {
            unit.properties.prepare();
            // extract settings
            for (final AdminProperty prop : new HashSet<AdminProperty>(unit.properties.getSettings()))  {
                unit.settings.put(prop.getName().substring(1), prop.getValue());
                unit.properties.getSettings().remove(prop);
            }
            // extract system information
            for (final AdminProperty prop : new HashSet<AdminProperty>(unit.properties.getProperties()))  {
                if ("unit".equals(prop.getRefAdminType()))  {
                    Set<String> units = unit.systemInfos.get(prop.getName());
                    if (units == null)  {
                        units = new TreeSet<String>();
                        unit.systemInfos.put(prop.getName(), units);
                    }
                    units.add(prop.getRefAdminName());
                    unit.properties.getProperties().remove(prop);
                }
            }
        }
        super.prepare();
    }

    @Override()
    protected void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder)
    {
        _updateBuilder
                //              tag             | default | value                              | write?
                .string(        "description",             this.getDescription())
                .flag(          "hidden",           false, this.isHidden());

        for (final Unit unit : this.units)  {
            _updateBuilder
                    .childStart("unit", unit.name)
                    //              tag             | default | value                              | write?
                    .flagIfTrue("default",              false, unit.defaultUnit,                    unit.defaultUnit)
                    .string(    "description",                 unit.description)
                    .string(    "label",                       unit.label)
                    .single(    "multiplier",                  Double.toString(unit.multiplier))
                    .single(    "offset",                      Double.toString(unit.offset));
            // system information
            for (final Map.Entry<String, Set<String>> systemInfo : unit.systemInfos.entrySet())  {
                for (final String unitName : systemInfo.getValue())  {
                    _updateBuilder.stepStartNewLine().stepSingle("system").stepString(systemInfo.getKey()).stepSingle("to").stepSingle("unit").stepString(unitName).stepEndLine();
                }
            }
            // settings
            for (final Map.Entry<String, String> setting : unit.settings.entrySet())  {
                _updateBuilder.stepStartNewLine().stepSingle("setting").stepString(setting.getKey()).stepString(setting.getValue()).stepEndLine();
            }

            _updateBuilder
                    .properties(unit.properties)
                    .childEnd();
        }

        _updateBuilder
                .properties(this.getProperties());
    }

    @Override()
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
                             final Dimension_mxJPO _current)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",         this.getDescription(),   _current.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",      false,  this.isHidden(),         _current.isHidden());

        // prepare maps of units depending on the unit name (as key)
        final Map<String,Unit> curUnits = new HashMap<String,Unit>();
        for (final Unit unit : _current.units)  {
            curUnits.put(unit.name, unit);
        }
        final Map<String,Unit> tarUnits = new HashMap<String,Unit>();
        for (final Unit unit : this.units)  {
            tarUnits.put(unit.name, unit);
        }

        // remove units which not needed anymore (or throw exception)
        for (final Map.Entry<String,Unit> curUnit : curUnits.entrySet())  {
            if (!tarUnits.containsKey(curUnit.getKey()))  {
                if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowRemoveUnit))  {
                    throw new UpdateException_mxJPO(ErrorKey.DIMENSION_UPDATE_REMOVEUNIT);
                }
                _mql.newLine().cmd("remove unit ").arg(curUnit.getKey());
            }
        }

        // create non-existing units
        for (final Map.Entry<String,Unit> tarUnit : tarUnits.entrySet())  {
            if (!curUnits.containsKey(tarUnit.getKey()))  {
                _paramCache.logDebug("    add unit " + tarUnit.getKey());
                _mql.newLine()
                    .cmd("add unit ").arg(tarUnit.getValue().name).cmd(" ")
                    .cmd(tarUnit.getValue().defaultUnit ? "" : "!").cmd("default ")
                    .cmd("multiplier ").arg(String.valueOf(tarUnit.getValue().multiplier)).cmd(" ")
                    .cmd("offset ").arg(String.valueOf(tarUnit.getValue().offset));
            }
        }

        // update units
        for (final Unit tarUnit : tarUnits.values())  {
            tarUnit.calcDelta(_paramCache, curUnits.get(tarUnit.name), _mql);
        }

        // properties
        this.getProperties().calcDelta(_mql, "", _current.getProperties());
    }

    /**
     * The class is used to store information about a single unit of a
     * dimension.
     */
    public static class Unit
        implements Comparable<Dimension_mxJPO.Unit>
    {
        /** Name of the unit. */
        private String name;

        /** Description of the unit. */
        private String description;

        /** Label of the unit. */
        private String label;

        /** Multiplier of the unit. */
        private double multiplier;

        /** Offset of the unit. */
        private double offset;

        /** Is the unit the default unit? */
        private boolean defaultUnit;

        /**
         * System information of the unit. Internally the system information
         * is stored as property link and could be evaluated only after
         * complete dimension parsing. The key of the map is the system name,
         * the values are the related names of the units.
         */
        private final Map<String,Set<String>> systemInfos = new TreeMap<String,Set<String>>();

        /**
         * Holds all unit specific settings for a dimension. The settings are
         * stored internally as property. Therefore the settings could  be
         * extracted only after a complete parsing.
         */
        private final Map<String,String> settings = new TreeMap<String,String>();

        /**
         * Holds all unit specific properties for a single unit. The properties
         * includes settings and the link to the system property. While the
         * dimension is parsed the properties holds also the {@link #settings},
         * and {@link #systemInfos}.
         */
        private final AdminPropertyList_mxJPO properties = new AdminPropertyList_mxJPO();

        /**
         * Constructor to initialize the {@link #properties}.
         */
        public Unit()
        {
            this.properties.setOtherPropTag("setting");
        }

        /**
         * Compares this dimension with <code>_toCompare</code> dimension. The
         * algorithm checks {@link #name} for both dimension and is defined as:
         * <ul>
         * <li>If both {@link #name} are <code>null</code> then a &quot;0&quot;
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
        @Override()
        public int compareTo(final Unit _toCompare)
        {
            return CompareToUtil_mxJPO.compare(0, this.name, _toCompare.name);
        }

        /**
         * <p>Appends the MQL delta commands to updated current unit definition
         * <{@code _current} to this new target unit definition.
         * <p> FYI: For each setting which is modified a {@code modify unit}
         * must be defined as prefix or otherwise only the last setting is
         * correct (issue in MX kernel).</p>
         *
         * @param _paramCache   parameter cache
         * @param _current      current unit definition in MX (or
         *                      {@code null} if currently not existing)
         * @param _mql          MQL builder used to append MQL commands
         * @throws UpdateException_mxJPO if the multiplier or offset is changed
         */
        protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                                 final Unit _current,
                                 final MultiLineMqlBuilder _mql)
            throws UpdateException_mxJPO
        {
            _mql.pushPrefixByAppending("modify unit $2", this.name);

            if (_current != null)  {
                if (this.multiplier != _current.multiplier)  {
                    if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowUpdateUnitMult))  {
                        throw new UpdateException_mxJPO(ErrorKey.DIMENSION_UPDATE_MULTIPLIER);
                    }
                    _mql.newLine().cmd("multiplier ").arg(String.valueOf(this.multiplier)).arg(" ");
                }
                if (this.offset != _current.offset)  {
                    if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowUpdateUnitOffs))  {
                        throw new UpdateException_mxJPO(ErrorKey.DIMENSION_UPDATE_OFFSET);
                    }
                    _mql.newLine().cmd("offset ").arg(String.valueOf(this.offset)).cmd(" ");
                }
                if (this.defaultUnit != _current.defaultUnit)  {
                    if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowUpdateDefUnit))  {
                        throw new UpdateException_mxJPO(ErrorKey.DIMENSION_UPDATE_DEFAULTUNIT);
                    }
                    _mql.newLine();
                    if (!this.defaultUnit)  {
                        _mql.cmd("!");
                    }
                    _mql.cmd("default");
                }
            }
            DeltaUtil_mxJPO.calcValueDelta(_mql, "unitdescription", this.description, (_current != null) ? _current.description : null);
            DeltaUtil_mxJPO.calcValueDelta(_mql, "label", this.label, (_current != null) ? _current.label : null);
            // check for to many settings
            if (_current != null)  {
                for (final String curSetKey : _current.settings.keySet())  {
                    if (!this.settings.containsKey(curSetKey))  {
                        _mql.newLine().cmd("remove setting ").arg(curSetKey);
                    }
                }
            }
            // check for non existing settings or not equal values
            for (final Map.Entry<String,String> setting : this.settings.entrySet())  {
                final String thisValue = (setting.getValue() != null) ? setting.getValue() : "";
                final String curValue = ((_current != null) && (_current.settings.get(setting.getKey()) != null))
                                        ? _current.settings.get(setting.getKey())
                                        : "";
                if ((_current == null)
                        || !_current.settings.containsKey(setting.getKey()) || !thisValue.equals(curValue))  {
                    _mql.newLine().cmd("setting ").arg(setting.getKey()).cmd(" ").arg(setting.getValue());
                }
            }
            // check system information to delete
            if (_current != null)  {
                for (final Map.Entry<String,Set<String>> systemInfo : _current.systemInfos.entrySet())  {
                    if (!this.systemInfos.containsKey(systemInfo.getKey()))  {
                        for (final String unitName : systemInfo.getValue())  {
                            _mql.newLine().cmd("remove system ").arg(systemInfo.getKey()).cmd(" to unit ").arg(unitName);
                        }
                    }
                }
            }
            // check system information to add
            // (system information must be set after all units are created)
            for (final Map.Entry<String,Set<String>> systemInfo : this.systemInfos.entrySet())  {
                if ((_current == null) || !_current.systemInfos.containsKey(systemInfo.getKey()))  {
                    for (final String unitName : systemInfo.getValue())  {
                        _mql.newLine().cmd("system ").arg(systemInfo.getKey()).cmd(" to unit ").arg(unitName);
                    }
                } else  {
                    final Set<String> curUnits = _current.systemInfos.get(systemInfo.getKey());
                    for (final String curUnitName : curUnits)  {
                        if (!systemInfo.getValue().contains(curUnitName))  {
                            _mql.newLine().cmd("remove system ").arg(systemInfo.getKey()).cmd(" to unit ").arg(curUnitName);
                        }
                    }
                    for (final String tarUnitName : systemInfo.getValue())  {
                        if (!curUnits.contains(tarUnitName))  {
                            _mql.newLine().cmd("system ").arg(systemInfo.getKey()).cmd(" to unit ").arg(tarUnitName);
                        }
                    }
                }
            }

            // delta for properties
            this.properties.calcDelta(_mql, "", (_current != null) ? _current.properties : null);

            _mql.popPrefix();
        }

        /**
         * Returns the string representation of this unit. The information
         * includes {@link #name}, {@link #description}, {@link #label},
         * {@link #multiplier}, {@link #offset}, {@link #defaultUnit},
         * {@link #systemInfos}, {@link #settings} and {@link #properties}.
         *
         * @return string representation of this unit
         */
        @Override()
        public String toString()
        {
            return "[dimension name = " + this.name + ", "
                    + "description = " + this.description + ", "
                    + "label = " + this.label + ", "
                    + "multiplier = " + this.multiplier + ", "
                    + "offset = " + this.offset + ", "
                    + "defaultUnit = " + this.defaultUnit + ", "
                    + "system info = " + this.systemInfos + ", "
                    + "settings = " + this.settings + ", "
                    + "properties = " + this.properties + "]";
        }
    }
}
