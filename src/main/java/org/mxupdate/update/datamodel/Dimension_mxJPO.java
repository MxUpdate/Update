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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.dimension.DimensionDefParser_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO.AdminProperty;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * The class is used to export and import / update dimension administration
 * objects.
 *
 * @author The MxUpdate Team
 */
public class Dimension_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Called TCL procedure within the TCL update to parse the new dimension
     * definition. The TCL procedure calls method
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)} with the new
     * dimension definition. All quot's are replaced by <code>@0@0@</code> and
     * all apostroph's are replaced by <code>@1@1@</code>.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String TCL_PROCEDURE
            = "proc updateDimension {_sDimension _lsArgs}  {\n"
                + "regsub -all {'} $_lsArgs {@0@0@} sArg\n"
                + "regsub -all {\\\"} $sArg {@1@1@} sArg\n"
                + "regsub -all {\\\\\\[} $sArg {[} sArg\n"
                + "regsub -all {\\\\\\]} $sArg {]} sArg\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller dimension ${_sDimension} \"${sArg}\"\n"
            + "}\n";

    /**
     * Set of all ignored URLs from the XML definition for dimensions.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
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
    private final Set<Dimension_mxJPO.Unit> units = new TreeSet<Dimension_mxJPO.Unit>();
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

    /**
     * Parses all dimension specific expression URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
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
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if preparation failed
     */
    @Override()
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        if (this.currentUnit != null)  {
            this.units.add(this.currentUnit);
            this.currentUnit = null;
        }
        for (final Unit unit : this.units)  {
            unit.properties.prepare();
            for (final AdminProperty prop : new HashSet<AdminProperty>(unit.properties))  {
                // extract settings
                if (prop.isSetting())  {
                    unit.settings.put(prop.getName().substring(1), prop.getValue());
                    unit.properties.remove(prop);
                } else if ("unit".equals(prop.getRefAdminType()))  {
                    Set<String> units = unit.systemInfos.get(prop.getName());
                    if (units == null)  {
                        units = new TreeSet<String>();
                        unit.systemInfos.put(prop.getName(), units);
                    }
                    units.add(prop.getRefAdminName());
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
     * @see #units
     */
    @Override()
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
            // system information
            for (final Map.Entry<String, Set<String>> systemInfo : unit.systemInfos.entrySet())  {
                for (final String unitName : systemInfo.getValue())  {
                    _out.append("    system \"").append(StringUtil_mxJPO.convertTcl(systemInfo.getKey()))
                        .append("\" to unit \"").append(StringUtil_mxJPO.convertTcl(unitName))
                        .append("\"\n");
                }
            }
            // settings
            for (final Map.Entry<String, String> setting : unit.settings.entrySet())  {
                _out.append("    setting \"").append(StringUtil_mxJPO.convertTcl(setting.getKey()))
                    .append("\" \"").append(StringUtil_mxJPO.convertTcl(setting.getValue()))
                    .append("\"\n");
            }
            // properties
            for (final AdminProperty prop : unit.properties)  {
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

        // append properties
        this.getProperties().writeProperties(_paramCache, _out, "  ");

        _out.append("}");
    }

    /**
     * Only implemented as stub because
     * {@link #write(ParameterCache_mxJPO, Appendable)} is new implemented.
     *
     * @param _paramCache   parameter cache (not used)
     * @param _out          appendable instance to the TCL update file (not
     *                      used)
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
    {
    }

    /**
     * The method overwrites the original method to add the TCL procedure
     * {@link #TCL_PROCEDURE} so that the dimension could be updated with
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        // add TCL code for the procedure
        final StringBuilder tclCode = new StringBuilder()
                .append(Dimension_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, _preMQLCode, _postMQLCode, tclCode, _tclVariables, _sourceFile);
    }

    /**
     * The method is called within the update of an administration object. The
     * method is called directly within the update.
     * <ul>
     * <li>All <code>@0@0@</code> are replaced by quot's and all
     *     <code>@1@1@</code> are replaced by apostroph's.</li>
     * <li>The new dimension definition is parsed.</li>
     * <li>A delta MQL script generated to update the dimension to the new
     *     target definition.</li>
     * <li>The created delta MQL script is executed.</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @param _args         arguments from the TCL procedure
     * @throws Exception if update failed, {@link UpdateException_mxJPO} if
     *                   a unit could not be removed, a default unit is
     *                   changed, a modified or offset of a unit is changed
     * @see #TCL_PROCEDURE
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        if ((_args.length != 3) || !"dimension".equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else if (!this.getName().equals(_args[1])) {
            throw new Exception("Dimension '" + _args[1]
                    + "' is wanted to updated, but have dimension '" + _args[1] + "'!");
        } else  {
            final String code = _args[2].replaceAll("@0@0@", "'")
                                        .replaceAll("@1@1@", "\\\"");

            final DimensionDefParser_mxJPO parser = new DimensionDefParser_mxJPO(new StringReader(code));
            final Dimension_mxJPO dimension = parser.parse(_paramCache, this.getTypeDef(), this.getName());

            final MultiLineMqlBuilder mql = MqlBuilder_mxJPO.multiLine("escape mod dimension $1", this.getName());

            // basic information
            DeltaUtil_mxJPO.calcValueDelta(mql, "description", dimension.getDescription(), this.getDescription());
            // hidden flag
            DeltaUtil_mxJPO.calcFlagDelta(mql, "hidden", dimension.isHidden(), this.isHidden());

            // prepare maps of units depending on the unit name (as key)
            final Map<String,Unit> curUnits = new HashMap<String,Unit>();
            for (final Unit unit : this.units)  {
                curUnits.put(unit.name, unit);
            }
            final Map<String,Unit> tarUnits = new HashMap<String,Unit>();
            for (final Unit unit : dimension.units)  {
                tarUnits.put(unit.name, unit);
            }

            // remove units which not needed anymore (or throw exception)
            for (final Map.Entry<String,Unit> curUnit : curUnits.entrySet())  {
                if (!tarUnits.containsKey(curUnit.getKey()))  {
                    if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowRemoveUnit))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_REMOVEUNIT);
                    }
                    mql.newLine().cmd("remove unit ").arg(curUnit.getKey());
                }
            }

            // create non-existing units
            for (final Map.Entry<String,Unit> tarUnit : tarUnits.entrySet())  {
                if (!curUnits.containsKey(tarUnit.getKey()))  {
                    _paramCache.logDebug("    add unit " + tarUnit.getKey());
                    mql.newLine()
                        .cmd("add unit ").arg(tarUnit.getValue().name).cmd(" ")
                        .cmd(tarUnit.getValue().defaultUnit ? "" : "!").cmd("default ")
                        .cmd("multiplier ").arg(String.valueOf(tarUnit.getValue().multiplier)).cmd(" ")
                        .cmd("offset ").arg(String.valueOf(tarUnit.getValue().offset));
                }
            }

            // update units
            for (final Unit tarUnit : tarUnits.values())  {
                tarUnit.calcDelta(_paramCache, curUnits.get(tarUnit.name), mql);
            }

            // properties
            dimension.getProperties().calcDelta(mql, "", this.getProperties());

            mql.exec(_paramCache);
        }
    }

    /**
     * The class is used to store information about a single unit of a
     * dimension.
     */
    public static class Unit
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
        private double multiplier;

        /**
         * Offset of the unit.
         */
        private double offset;

        /**
         * Is the unit the default unit?
         */
        private boolean defaultUnit;

        /**
         * System information of the unit. Internally the system information
         * is stored as property link and could be evaluated only after
         * complete dimension parsing. The key of the map is the system name,
         * the values are the related names of the units.
         *
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private final Map<String,Set<String>> systemInfos = new TreeMap<String,Set<String>>();

        /**
         * Holds all unit specific settings for a dimension. The settings are
         * stored internally as property. Therefore the settings could  be
         * extracted only after a complete parsing.
         *
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
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
            return (this.name == null)
                    ? (_toCompare.name == null)
                        ? 0
                        : 1
                    : (_toCompare.name == null)
                        ? -1
                        : this.name.compareTo(_toCompare.name);
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
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_MULTIPLIER);
                    }
                    _mql.newLine().cmd("multiplier ").arg(String.valueOf(this.multiplier)).arg(" ");
                }
                if (this.offset != _current.offset)  {
                    if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowUpdateUnitOffs))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_OFFSET);
                    }
                    _mql.newLine().cmd("offset ").arg(String.valueOf(this.offset)).cmd(" ");
                }
                if (this.defaultUnit != _current.defaultUnit)  {
                    if (!_paramCache.getValueBoolean(ValueKeys.DMDimAllowUpdateDefUnit))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_DEFAULTUNIT);
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
