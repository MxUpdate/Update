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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
import org.mxupdate.update.util.AdminProperty_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

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
     * @see #parse(String, String)
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

    /**
     * Name of the parameter to define that units are allowed to remove.
     *
     * @see #jpoCallExecute(ParameterCache_mxJPO, String...)
     */
    private static final String PARAM_ALLOW_REMOVE_UNIT = "DMDimAllowRemoveUnit";

    /**
     * Name of the parameter to define that a change of the default unit is
     * allowed.
     *
     * @see Dimension_mxJPO.Unit#appendDelta
     */
    private static final String PARAM_ALLOW_UPDATE_DEFAULT_UNIT = "DMDimAllowUpdateDefUnit";

    /**
     * Name of the parameter to define that the multiplier of an unit is
     * allowed to change.
     *
     * @see Dimension_mxJPO.Unit#appendDelta
     */
    private static final String PARAM_ALLOW_UPDATE_UNIT_MULTIPLIER = "DMDimAllowUpdateUnitMult";

    /**
     * Name of the parameter to define that the offset of an unit is allowed to
     * change.
     *
     * @see Dimension_mxJPO.Unit#appendDelta
     */
    private static final String PARAM_ALLOW_UPDATE_UNIT_OFFSET = "DMDimAllowUpdateUnitOffs";

    /**
     * Holds the list of already parsed units of this dimension.
     *
     * @see #parse(String, String)
     * @see #prepare(ParameterCache_mxJPO)
     */
    private final Set<Dimension_mxJPO.Unit> units = new TreeSet<Dimension_mxJPO.Unit>();

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
        if (!Dimension_mxJPO.IGNORED_URLS.contains(_url))  {
            if ("/unitList/unit".equals(_url))  {
                if (this.currentUnit != null)  {
                    this.units.add(this.currentUnit);
                }
                this.currentUnit = new Unit();
            } else if ("/unitList/unit/adminProperties/name".equals(_url))  {
                this.currentUnit.name = _content;
            } else if ("/unitList/unit/adminProperties/description".equals(_url))  {
                this.currentUnit.description = _content;
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
    @Override
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
    @Override
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
            throws Exception
    {
        if (!"dimension".equals(_args[0]))  {
            super.jpoCallExecute(_paramCache, _args);
        } else if (!this.getName().equals(_args[1])) {
            throw new Exception("Dimension '" + _args[1]
                    + "' is wanted to updated, but have dimension '" + _args[1] + "'!");
        } else  {
            final String code = _args[2].replaceAll("@0@0@", "'")
                                        .replaceAll("@1@1@", "\\\"");

            final DimensionDefParser_mxJPO parser = new DimensionDefParser_mxJPO(new StringReader(code));
            final Dimension_mxJPO dimension = parser.dimension(_paramCache, this.getTypeDef(), this.getName());

            final StringBuilder cmd = new StringBuilder()
                    .append("escape mod dimension \"")
                    .append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ");

            // basic information
            Dimension_mxJPO.calcValueDelta(cmd, "description", dimension.getDescription(), this.getDescription());
            // hidden flag, because hidden flag must be set with special syntax
            if (this.isHidden() != dimension.isHidden())  {
                if (!dimension.isHidden())  {
                    cmd.append('!');
                }
                cmd.append("hidden ");
            }

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
                    if (!_paramCache.getValueBoolean(Dimension_mxJPO.PARAM_ALLOW_REMOVE_UNIT))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_REMOVEUNIT);
                    }
                    cmd.append("remove unit \"").append(StringUtil_mxJPO.convertMql(curUnit.getKey())).append("\" ");
                }
            }

            final StringBuilder postCmd = new StringBuilder();
            for (final Map.Entry<String,Unit> tarUnit : tarUnits.entrySet())  {
                if (!curUnits.containsKey(tarUnit.getKey()))  {
                    _paramCache.logDebug("    add unit " + tarUnit.getKey());
                    tarUnit.getValue().appendDelta(_paramCache, null, cmd, postCmd);
                }
            }

            for (final Unit tarUnit : tarUnits.values())  {
                if (curUnits.containsKey(tarUnit.name))  {
                    tarUnit.appendDelta(_paramCache, curUnits.get(tarUnit.name), cmd, postCmd);
                }
            }

            cmd.append(postCmd).append(";");
            MqlUtil_mxJPO.execMql(_paramCache, cmd);
        }
    }
    /**
     * Calculates the delta between the new and the old value. If a delta
     * exists, the kind with the new delta is added to the string builder.
     *
     * @param _out      appendable instance where the delta must be append
     * @param _kind     kind of the delta
     * @param _newVal   new target value
     * @param _curVal   current value in the database
     */
    protected static void calcValueDelta(final StringBuilder _out,
                                         final String _kind,
                                         final String _newVal,
                                         final String _curVal)
    {
        final String curVal = (_curVal == null) ? "" : _curVal;
        final String newVal = (_newVal == null) ? "" : _newVal;

        if (!curVal.equals(newVal))  {
            _out.append(_kind).append(" \"").append(StringUtil_mxJPO.convertMql(newVal)).append("\" ");
        }
    }

    /**
     * The class is used to store information about a single unit of a
     * dimension.
     */
    public static class Unit
        implements Comparable<Dimension_mxJPO.Unit>, Serializable
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = 8304234012828651268L;

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
         * and {@link #systemInfos}.
         *
         * @see Dimension_mxJPO#parse(String, String)
         * @see Dimension_mxJPO#prepare(ParameterCache_mxJPO)
         */
        private final Set<AdminProperty_mxJPO> properties = new TreeSet<AdminProperty_mxJPO>();

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
         * <code>_current</code> to this new target unit definition.
         * <p> FYI: For each setting which is modified a <code>modify
         * unit</code> must be defined as prefix or otherwise only the last
         * setting is correct (issue in MX kernel).</p>
         *
         * @param _paramCache   parameter cache
         * @param _current      current unit definition in MX (or
         *                      <code>null</code> if currently not existing)
         * @param _cmd          string builder used to append MQL commands
         * @param _postCmd      string builder used to append the system
         *                      information (should be defined at least)
         * @throws UpdateException_mxJPO if the multiplier or offset is changed
         */
        protected void appendDelta(final ParameterCache_mxJPO _paramCache,
                                   final Unit _current,
                                   final StringBuilder _cmd,
                                   final StringBuilder _postCmd)
            throws UpdateException_mxJPO
        {
            final StringBuilder modUnitCmd = new StringBuilder()
                    .append("modify unit \"").append(StringUtil_mxJPO.convertMql(this.name)).append("\" ");
            if (_current == null)  {
                _cmd.append("add unit \"").append(StringUtil_mxJPO.convertMql(this.name))
                    .append("\" ");
                if (!this.defaultUnit)  {
                    _cmd.append('!');
                }
                _cmd.append("default ")
                    .append("multiplier ").append(this.multiplier).append(' ')
                    .append("offset ").append(this.offset).append(' ');
            } else  {
                _cmd.append(modUnitCmd);
                if (this.multiplier != _current.multiplier)  {
                    if (!_paramCache.getValueBoolean(Dimension_mxJPO.PARAM_ALLOW_UPDATE_UNIT_MULTIPLIER))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_MULTIPLIER);
                    }
                    _cmd.append("multiplier ").append(this.multiplier).append(' ');
                }
                if (this.offset != _current.offset)  {
                    if (!_paramCache.getValueBoolean(Dimension_mxJPO.PARAM_ALLOW_UPDATE_UNIT_OFFSET))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_OFFSET);
                    }
                    _cmd.append("offset ").append(this.offset).append(' ');
                }
                if (this.defaultUnit != _current.defaultUnit)  {
                    if (!_paramCache.getValueBoolean(Dimension_mxJPO.PARAM_ALLOW_UPDATE_DEFAULT_UNIT))  {
                        throw new UpdateException_mxJPO(UpdateException_mxJPO.Error.DIMENSION_UPDATE_DEFAULTUNIT);
                    }
                    if (!this.defaultUnit)  {
                        _cmd.append('!');
                    }
                    _cmd.append("default ");
                }
            }
            Dimension_mxJPO.calcValueDelta(_cmd, "unitdescription", this.description,
                    (_current != null) ? _current.description : null);
            Dimension_mxJPO.calcValueDelta(_cmd, "label", this.label,
                    (_current != null) ? _current.label : null);
            // check for to many settings
            if (_current != null)  {
                for (final String curSetKey : _current.settings.keySet())  {
                    if (!this.settings.containsKey(curSetKey))  {
                        _cmd.append(modUnitCmd)
                            .append("remove setting \"").append(StringUtil_mxJPO.convertMql(curSetKey)).append("\" ");
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
                    _cmd.append(modUnitCmd)
                        .append("setting \"").append(StringUtil_mxJPO.convertMql(setting.getKey()))
                        .append("\" \"").append(StringUtil_mxJPO.convertMql(setting.getValue()))
                        .append("\" ");
                }
            }
            // check system information to delete
            if (_current != null)  {
                for (final Map.Entry<String,Set<String>> systemInfo : _current.systemInfos.entrySet())  {
                    if (!this.systemInfos.containsKey(systemInfo.getKey()))  {
                        for (final String unitName : systemInfo.getValue())  {
                            _cmd.append(modUnitCmd)
                                .append("remove system \"")
                                .append(StringUtil_mxJPO.convertMql(systemInfo.getKey()))
                                .append("\" to unit \"")
                                .append(StringUtil_mxJPO.convertMql(unitName))
                                .append("\" ");
                        }
                    }
                }
            }
            // check system information to add
            // (system information must be set after all units are created)
            for (final Map.Entry<String,Set<String>> systemInfo : this.systemInfos.entrySet())  {
                if ((_current == null) || !_current.systemInfos.containsKey(systemInfo.getKey()))  {
                    for (final String unitName : systemInfo.getValue())  {
                        _postCmd.append(modUnitCmd)
                                .append("system \"")
                                .append(StringUtil_mxJPO.convertMql(systemInfo.getKey()))
                                .append("\" to unit \"")
                                .append(StringUtil_mxJPO.convertMql(unitName))
                                .append("\" ");
                    }
                } else  {
                    final Set<String> curUnits = _current.systemInfos.get(systemInfo.getKey());
                    for (final String curUnitName : curUnits)  {
                        if (!systemInfo.getValue().contains(curUnitName))  {
                            _postCmd.append(modUnitCmd)
                                    .append("remove system \"")
                                    .append(StringUtil_mxJPO.convertMql(systemInfo.getKey()))
                                    .append("\" to unit \"")
                                    .append(StringUtil_mxJPO.convertMql(curUnitName))
                                    .append("\" ");
                        }
                    }
                    for (final String tarUnitName : systemInfo.getValue())  {
                        if (!curUnits.contains(tarUnitName))  {
                            _postCmd.append(modUnitCmd)
                                    .append("system \"")
                                    .append(StringUtil_mxJPO.convertMql(systemInfo.getKey()))
                                    .append("\" to unit \"")
                                    .append(StringUtil_mxJPO.convertMql(tarUnitName))
                                    .append("\" ");
                        }
                    }
                }
            }
            // check properties to remove
            if (_current != null)  {
                for (final AdminProperty_mxJPO curProp : _current.properties)  {
                    boolean found = false;
                    for (final AdminProperty_mxJPO tarProp : this.properties)  {
                        if (tarProp.compareTo(curProp) == 0)  {
                            found = true;
                            break;
                        }
                    }
                    if (!found)  {
                        _cmd.append(modUnitCmd)
                            .append("remove property \"");
                        if (curProp.getName() != null)  {
                            _cmd.append(StringUtil_mxJPO.convertMql(curProp.getName()));
                        }
                        _cmd.append("\" ");
                        if ((curProp.getRefAdminName() != null) && (curProp.getRefAdminType() != null))  {
                            _cmd.append(" to \"")
                            .append(StringUtil_mxJPO.convertMql(curProp.getRefAdminType()))
                            .append("\" \"")
                            .append(StringUtil_mxJPO.convertMql(curProp.getRefAdminName()))
                            .append("\" ");
                        }
                    }
                }
            }
            // check properties to add
            for (final AdminProperty_mxJPO tarProp : this.properties)  {
                boolean found = false;
                if (_current != null)  {
                    for (final AdminProperty_mxJPO curProp : _current.properties)  {
                        if (tarProp.compareTo(curProp) == 0)  {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found)  {
                    _cmd.append(modUnitCmd)
                        .append("property \"");
                    if (tarProp.getName() != null)  {
                        _cmd.append(StringUtil_mxJPO.convertMql(tarProp.getName()));
                    }
                    _cmd.append("\" ");
                    if ((tarProp.getRefAdminName() != null) && (tarProp.getRefAdminType() != null))  {
                        _cmd.append(" to \"")
                        .append(StringUtil_mxJPO.convertMql(tarProp.getRefAdminType()))
                        .append("\" \"")
                        .append(StringUtil_mxJPO.convertMql(tarProp.getRefAdminName()))
                        .append("\" ");
                    }
                    if (tarProp.getValue() != null)  {
                        _cmd.append(" value \"")
                            .append(StringUtil_mxJPO.convertMql(tarProp.getValue()))
                            .append("\" ");
                    }
                }
            }
        }

        /**
         * Returns the string representation of this unit. The information
         * includes {@link #name}, {@link #description}, {@link #label},
         * {@link #multiplier}, {@link #offset}, {@link #defaultUnit},
         * {@link #systemInfos}, {@link #settings} and {@link #properties}.
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
                    + "system info = " + this.systemInfos + ", "
                    + "settings = " + this.settings + ", "
                    + "properties = " + this.properties + "]";
        }
    }
}
