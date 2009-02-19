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

package org.mxupdate.update;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.ExpansionWithSelect;
import matrix.db.Query;
import matrix.db.RelationshipWithSelect;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.update.util.StringUtil_mxJPO.formatInstalledDate;
import static org.mxupdate.update.util.StringUtil_mxJPO.match;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOff;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOn;

/**
 * @author Tim Moxter
 * @version $Id$
 */
public class BusObject_mxJPO
        extends AbstractPropertyObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -5381775541507933947L;

    /**
     * Key used to store default attribute values within the parameter cache.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private static final String PARAM_DEFAULT_ATTRS = "BusDefaultAttrValues";

    /**
     * Parameter key to cache allowed attributes for relationships.
     *
     * @see #expand4Parse(ParameterCache_mxJPO, BusinessObject, String, boolean, boolean)
     */
    private static final String PARAM_RELATION_ATTRS = "BusRelationAttributes";

    /**
     * String used to split the name and revision of administration business
     * object.
     */
    public static final String SPLIT_NAME = "________";

    /**
     * Sorted set of attribute values.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getAttrValuesSorted()
     */
    private final Set<AttributeValue> attrValuesSorted = new TreeSet<AttributeValue>();

    /**
     * Name of business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getBusName()
     */
    private final String busName;

    /**
     * Revision of business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getBusRevision()
     */
    private final String busRevision;

    /**
     * Vault of the business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     */
    private String busVault;

    /**
     * Description of the business object (because the description within the
     * header of the TCL file includes the revision of the business object).
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getBusDescription()
     */
    private String busDescription;

    /**
     * Current state of the related business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getBusCurrent()
     */
    private String busCurrent;

    /**
     * All possible states of the related business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     */
    private StringList busStates;

    /**
     * Object id of the business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getBusOid()
     */
    private String busOid;

    /**
     * Holds all connected objects.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #write(ParameterCache_mxJPO, Writer)
     */
    private final Set<Connection> connections = new TreeSet<Connection>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    public BusObject_mxJPO(final TypeDef_mxJPO _typeDef,
                           final String _mxName)
    {
        super(_typeDef, _mxName);

        if (_mxName != null)  {
            final String[] nameRev = this.getName().split(SPLIT_NAME);
            this.busName = nameRev[0];
            this.busRevision = (nameRev.length > 1) ? nameRev[1] : "";
        } else  {
            this.busName = null;
            this.busRevision = null;
        }
    }

    /**
     * Searches for all business object of current type definition and returns
     * all MX names. The revision of the business object is appended to the
     * name of the business object split by {@link #SPLIT_NAME}.
     *
     * @param _paramCache   parameter cache
     * @return set of found business object names (and revision)
     * @throws MatrixException if the query for all business objects for given
     *                         type failed
     */
    @Override
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final StringList selects = new StringList();
        selects.addElement("name");
        selects.addElement("revision");

        final Query query = new Query();
        query.open(_paramCache.getContext());
        query.setBusinessObjectType(this.getTypeDef().getMxBusType());
        final BusinessObjectWithSelectList list = query.select(_paramCache.getContext(), selects);
        query.close(_paramCache.getContext());

        final Set<String> ret = new TreeSet<String>();
        for (final Object mapObj : list)  {
            final BusinessObjectWithSelect map = (BusinessObjectWithSelect) mapObj;
            final String busName = (String) map.getSelectDataList("name").get(0);
            final String busRevision = (String) map.getSelectDataList("revision").get(0);
            final StringBuilder name = new StringBuilder().append(busName);
            if ((busRevision != null) && !"".equals(busRevision))  {
                name.append(SPLIT_NAME).append(busRevision);
            }
            ret.add(name.toString());
        }
        return ret;
    }

    /**
     * Checks if given MX name without prefix and suffix matches given match
     * string. The MX name is split with {@link #SPLIT_NAME} to get
     * the name and revision of the business object. A MX name matches if the
     * business object name or revision matches.
     *
     * @param _paramCache   parameter cache
     * @param _mxName       name (and revision) of the administration business
     *                      object
     * @param _match        string which must be matched
     * @return <i>true</i> if the given MX name matches; otherwise <i>false</i>
     */
    @Override
    public boolean matchMxName(final ParameterCache_mxJPO _paramCache,
                               final String _mxName,
                               final String _match)
    {
        final String[] nameRev = _mxName.split(SPLIT_NAME);
        return (match(nameRev[0], _match) || ((nameRev.length > 1) && match(nameRev[1], _match)));
    }

    /**
     * Parses all information for given administration object.
     * Sorts the attribute values, defines the description for the TCL
     * update script (concatenation of the revision and description).
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the parse failed
     * @see #attrValuesSorted
     * @see #busCurrent
     * @see #busDescription
     * @see #busName
     * @see #busOid
     * @see #busRevision
     * @see #busStates
     * @see #busVault
     */
    @Override
    protected void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                      this.getBusName(),
                                                      this.getBusRevision(),
                                                      null);

        // id, vault, description and current MX state
        final StringList select = new StringList(4);
        select.addElement("id");
        select.addElement("vault");
        select.addElement("description");
        select.addElement("current");
        select.addElement("policy.state");
        final BusinessObjectWithSelect sel = bus.select(_paramCache.getContext(), select);
        this.busOid = sel.getSelectData("id");
        this.busVault = sel.getSelectData("vault");
        this.busDescription = sel.getSelectData("description");
        this.busCurrent = sel.getSelectData("current");
        this.busStates = sel.getSelectDataList("policy.state");

        // define properties (attribute values)
        final AttributeList attrs = bus.getAttributeValues(_paramCache.getContext(), true);
        for (final Object attrObj : attrs)  {
            final Attribute attr = (Attribute) attrObj;
            if (AdminPropertyDef.AUTHOR.getAttrName().equals(attr.getName()))  {
                this.setAuthor(attr.getValue());
            } else if (AdminPropertyDef.APPLICATION.getAttrName().equals(attr.getName()))  {
                this.setApplication(attr.getValue());
            } else if (AdminPropertyDef.INSTALLEDDATE.getAttrName().equals(attr.getName()))  {
                this.setInstallationDate(attr.getValue());
            } else if (AdminPropertyDef.INSTALLER.getAttrName().equals(attr.getName()))  {
                this.setInstaller(attr.getValue());
            } else if (AdminPropertyDef.VERSION.getAttrName().equals(attr.getName()))  {
                this.setVersion(attr.getValue());
            } else if (!AdminPropertyDef.FILEDATE.getAttrName().equals(attr.getName()))  {
                this.attrValuesSorted.add(new AttributeValue(attr));
            }
        }

        // define description
        final StringBuilder desc = new StringBuilder();
        if ((this.busRevision != null) && !"".equals(this.busRevision))  {
            desc.append(this.busRevision);
            if (this.busDescription != null)  {
                desc.append('\n');
            }
        }
        if (this.busDescription != null)  {
            desc.append(this.busDescription);
        }
        this.setDescription(desc.toString());

        // evaluate from connections
        if (this.getTypeDef().getMxBusRelsFrom() != null)  {
            for (final String relation : this.getTypeDef().getMxBusRelsFrom())  {
                this.expand4Parse(_paramCache, bus, relation, true, false);
            }
        }

        // evaluate to connections
        if (this.getTypeDef().getMxBusRelsTo() != null)  {
            for (final String relation : this.getTypeDef().getMxBusRelsTo())  {
                this.expand4Parse(_paramCache, bus, relation, false, true);
            }
        }

        // evaluate both connections
        if (this.getTypeDef().getMxBusRelsBoth() != null)  {
            for (final String relation : this.getTypeDef().getMxBusRelsBoth())  {
                // from
                this.expand4Parse(_paramCache, bus, relation, true, false);
                // to
                this.expand4Parse(_paramCache, bus, relation, false, true);
            }
        }
    }

    /**
     * Expands from the business object depending on the relationship and
     * stores the connection information. If the relationship has attributes,
     * the related values are also evaluated.
     *
     * @param _paramCache   parameter cache
     * @param _bus          the business object to expand
     * @param _relation     name of relationship to expand
     * @param _getFrom      must be set to <i>true</i> to expand from
     *                      connections; otherwise <i>false</i>
     * @param _getTo        must be set to <i>true</i> to expand to
     *                      connections; otherwise <i>false</i>
     * @throws MatrixException if the expand failed
     * @see #connections
     */
    @SuppressWarnings("unchecked")
    private void expand4Parse(final ParameterCache_mxJPO _paramCache,
                              final BusinessObject _bus,
                              final String _relation,
                              final boolean _getFrom,
                              final boolean _getTo)
            throws MatrixException
    {
        // get attributes from relationship
        final Map<String,Map> cache = _paramCache.defineValueMap(PARAM_RELATION_ATTRS, Map.class);
        Map<String,String> attrs = cache.get(_relation);
        if (attrs == null)
        {
            attrs = new HashMap<String,String>();
            cache.put(_relation, attrs);

            final String attrStr = execMql(_paramCache.getContext(),
                    new StringBuilder("escape print rel \"").append(_relation)
                            .append("\" select attribute dump '\n'"));
            if (!"".equals(attrStr))  {
                for (final String attr : attrStr.split("\n"))  {
                    attrs.put("attribute[" + attr + "]", attr);
                }
            };
        }


        final StringList busSelect = new StringList(3);
        busSelect.addElement("type");
        busSelect.addElement("name");
        busSelect.addElement("revision");
        final StringList relSelect = new StringList(1 + attrs.size());
        relSelect.addElement("id");
        relSelect.addAll(attrs.keySet());
        // get from objects
        final ExpansionWithSelect expandTo = _bus.expandSelect(_paramCache.getContext(),
                                                              _relation,
                                                              "*",
                                                              busSelect,
                                                              relSelect,
                                                              _getFrom,
                                                              _getTo,
                                                              (short) 1,
                                                              null,
                                                              null,
                                                              (short) 0,
                                                              true);

        for (final Object obj : expandTo.getRelationships())  {
            this.connections.add(new Connection(_relation,
                                                _getFrom ? "from" : "to",
                                                (RelationshipWithSelect) obj,
                                                attrs));
        }

    }

    /**
     * Writes the information to update the business objects.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException if the TCL update code for the business object could
     *                     not be written
     */
    @Override
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Writer _out)
            throws IOException
    {
        this.writeHeader(_paramCache, _out);
        _out.append("mql mod bus \"${OBJECTID}\"")
            .append(" \\\n    description \"").append(convertTcl(this.busDescription)).append("\"");
        for (final AttributeValue attr : this.attrValuesSorted)  {
            attr.write(_out);
        }
        // write all connected objects
        for (final Connection con : this.connections)  {
            con.write(_out);
        }
        // write promotes to target states if required
        final int target = this.busStates.indexOf(this.busCurrent);
        for (int idx = 0; idx < target; idx++)  {
            _out.append("\nmql promote bus \"${OBJECTID}\"");
        }
    }


    /**
     * Deletes administration business object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if delete of the business object failed
     */
    @Override
    public void delete(final ParameterCache_mxJPO _paramCache)
            throws Exception
    {
        final TypeDef_mxJPO typeDef = this.getTypeDef();
        final BusinessObject bus = new BusinessObject(typeDef.getMxBusType(),
                                                      this.busName,
                                                      this.busRevision,
                                                      null);
        bus.remove(_paramCache.getContext());
    }

    /**
     * Creates for given name the business object.
     *
     * @param _paramCache   parameter cache
     * @param _file         defines the file for which the business object is
     *                      created
     * @throws Exception if the business object could not be created
     */
    @Override
    public void create(final ParameterCache_mxJPO _paramCache,
                       final File _file)
            throws Exception
    {
        final TypeDef_mxJPO busType = this.getTypeDef();
        final BusinessObject bus = new BusinessObject(busType.getMxBusType(),
                                                      this.getBusName(),
                                                      this.getBusRevision(),
                                                      busType.getMxBusVault());
        bus.create(_paramCache.getContext(), busType.getMxBusPolicy());
    }

    /**
     * The method overwrites the original method to
     * <ul>
     * <li>reset the description</li>
     * <li>set the version and author attribute</li>
     * <li>reset all not ignored attributes</li>
     * <li>define the TCL variable &quot;OBJECTID&quot; with the object id of
     *     the represented business object</li>
     * </ul>
     * The original method of the super class if called surrounded with a
     * history off, because if the update itself is done the modified basic
     * attribute and the version attribute of the business object is updated.
     * <br/>
     * The new generated MQL code is set in the front of the already defined
     * MQL code in <code>_preMQLCode</code> and appended to the MQL statements
     * in <code>_postMQLCode</code>.
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
     * @throws Exception if the update executed within derived class failed
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
        // resets the description
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod bus ").append(this.busOid).append(" description \"\"");

        // reset all attributes (if they must not be ignored...)
        final Set<String> ignoreAttrs = (this.getTypeDef().getMxBusIgnoredAttributes() == null)
                                        ? new HashSet<String>(0)
                                        : new HashSet<String>(this.getTypeDef().getMxBusIgnoredAttributes());
        final Map<String,String> defaultAttrValues = _paramCache.defineValueMap(PARAM_DEFAULT_ATTRS, String.class);
        for (final AttributeValue attr : this.attrValuesSorted)  {
            if (!ignoreAttrs.contains(attr.name))  {
                if (!defaultAttrValues.containsKey(attr.name))  {
                    final String def = execMql(_paramCache.getContext(),
                                               new StringBuilder().append("print attr '")
                                                       .append(attr.name).append("' select default dump"));
                    defaultAttrValues.put(attr.name, def);
                }
                preMQLCode.append(" \"").append(attr.name).append("\" \"")
                        .append(defaultAttrValues.get(attr.name)).append('\"');
            }
        }
        preMQLCode.append(";\n");

        // disconnect all objects
        for (final Connection con : this.connections)  {
            preMQLCode.append("disconnect connection ").append(con.conId).append(";\n");
        }

        // append demotes if required
        final int target = this.busStates.indexOf(this.busCurrent);
        for (int idx = 0; idx < target; idx++)  {
            preMQLCode.append("\ndemote bus ").append(this.busOid).append(";\n");
        }

        // append other pre MQL code
        preMQLCode.append(_preMQLCode);

        // post update MQL statements
        final StringBuilder postMQLCode = new StringBuilder()
                .append(_postMQLCode)
                .append("mod bus ").append(this.busOid)
        // define version
                .append(" \"").append(AdminPropertyDef.VERSION.getAttrName())
                        .append("\" \"").append(_tclVariables.get(AdminPropertyDef.VERSION.name())).append('\"')
        // define file date
                .append(" \"").append(AdminPropertyDef.FILEDATE.getAttrName())
                        .append("\" \"").append(_tclVariables.get(AdminPropertyDef.FILEDATE.name())).append('\"');
        // is installed date property defined?
        if ((this.getInstallationDate() == null) || "".equals(this.getInstallationDate()))  {
            final String date = formatInstalledDate(_paramCache, new Date());
            _paramCache.logTrace("    - define installed date '" + date + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.INSTALLEDDATE.getAttrName())
                    .append("\" \"").append(date).append('\"');
        }
        // exists no installer property or installer property not equal?
        final String instVal = _tclVariables.get(AdminPropertyDef.INSTALLER.name());
        if ((this.getInstaller() == null) || !this.getInstaller().equals(instVal))  {
            _paramCache.logTrace("    - define installer '" + instVal + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.INSTALLER.getAttrName())
                    .append("\" \"").append(instVal).append('\"');
        }
        // exists no application property or application property not equal?
        final String applVal = _tclVariables.get(AdminPropertyDef.APPLICATION.name());
        if ((this.getApplication() == null) || !this.getApplication().equals(applVal))  {
            _paramCache.logTrace("    - define application '" + applVal + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.APPLICATION.getAttrName())
                    .append("\" \"").append(applVal).append('\"');
        }
        // exists no author property or author property not equal?
        final String authVal = _tclVariables.get(AdminPropertyDef.AUTHOR.name());
        if ((this.getAuthor() == null) || !this.getAuthor().equals(authVal))  {
            _paramCache.logTrace("    - define author '" + authVal + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.AUTHOR.getAttrName())
                    .append("\" \"").append(authVal).append('\"');
        }
        postMQLCode.append(";\n");

        // prepare map of all TCL variables incl. id of business object
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.put("OBJECTID", this.busOid);
        tclVariables.putAll(_tclVariables);

        // update must be done with history off (because not required...)
        try  {
            setHistoryOff(_paramCache.getContext());
            super.update(_paramCache, preMQLCode, postMQLCode, _preTCLCode, tclVariables, _sourceFile);
        } finally  {
            setHistoryOn(_paramCache.getContext());
        }
    }

    /**
     * Returns the business type of this business object instance. The business
     * type is evaluated from the business type annotation.
     *
     * @return business type
     */
    protected String getBusType()
    {
        return this.getTypeDef().getMxBusType();
    }

    /**
     * Getter method for instance variable {@link #busName}.
     *
     * @return value of instance variable {@link #busName}
     * @see #busName
     */
    protected String getBusName()
    {
        return this.busName;
    }

    /**
     * Getter method for instance variable {@link #busRevision}. If
     * {@link #busRevision} is <code>null</code> an empty string "" is returned.
     *
     * @return value of instance variable {@link #busRevision}
     * @see #busRevision
     */
    protected String getBusRevision()
    {
        return (this.busRevision == null)
               ? ""
               : this.busRevision;
    }

    /**
     * Getter method for instance variable {@link #busVault}.
     *
     * @return value of instance variable {@link #busVault}
     * @see #busVault
     */
    protected String getBusVault()
    {
        return this.busVault;
    }

    /**
     * Getter method for instance variable {@link #busDescription}.
     *
     * @return value of instance variable {@link #busDescription}
     * @see #busDescription
     */
    protected String getBusDescription()
    {
        return this.busDescription;
    }

    /**
     * Getter method for instance variable {@link #busCurrent}.
     *
     * @return value of instance variable {@link #busCurrent}
     * @see #busCurrent
     */
    protected String getBusCurrent()
    {
        return this.busCurrent;
    }

    /**
     * Getter method for instance variable {@link #busOid}.
     *
     * @return value of instance variable {@link #busOid}
     * @see #busOid
     */
    protected String getBusOid()
    {
        return this.busOid;
    }

    /**
     * Getter method for instance variable {@link #attrValuesSorted}.
     *
     * @return value of instance variable {@link #attrValuesSorted}
     * @see #attrValuesSorted
     */
    protected Set<AttributeValue> getAttrValuesSorted()
    {
        return this.attrValuesSorted;
    }

    /**
     * Class used to hold the attributes values. They are comparable by the
     * attribute name. If numbers within attribute are used, the order is
     * done depending on the numbers.
     */
    protected class AttributeValue
            implements Comparable<AttributeValue>
    {
        /**
         * Holds the user references of a user access.
         */
        public final String name;

        /**
         * Holds the expression filter of a user access.
         */
        public final String value;

        /**
         * Defines the name and value of the attribute depending on the
         * attribute.
         *
         * @param _attr attribute
         */
        AttributeValue(final Attribute _attr)
        {
            this.name = _attr.getName();
            this.value = _attr.getValue();
        }

        /**
         * Defines the name and value of the attribute.
         *
         * @param _name     name of the attribute
         * @param _value    value of the attribute
         */
        AttributeValue(final String _name,
                       final String _value)
        {
            this.name = _name;
            this.value = _value;
        }

        /**
         * Compares this attribute name with given attribute name. The related
         * attribute values are not used to compare. If the attribute names
         * includes numbers and the attribute names has the same prefix (before
         * the numbers), the numbers are compared as integer.<br/>
         * <b/>Examples:</b><br/>
         * Temp &gt; Name<br/>
         * Temp = Temp<br/>
         * Temp 5 &lt Temp 10
         *
         * @param _attribute    attribute instance to compare
         * @return <code>0</code> if the attribute names are equal (should not
         *         possible...); a value less than <code>0</code> if this
         *         attribute name is lexicographically less than the compared
         *         attribute name; a value greater than <code>0</code> if this
         *         attribute name is lexicographically greater than the
         *         compared attribute name
         */
        public int compareTo(final AttributeValue _attribute)
        {
            final String name1 = this.name.replaceAll(" [0-9]*$", "");
            final String name2 = _attribute.name.replaceAll(" [0-9]*$", "");
            int ret = 0;
            if (name1.equals(name2) && !name1.equals(this.name))  {
                final int index = name1.length();
                final Integer num1 = Integer.parseInt(this.name.substring(index).trim());
                final Integer num2 = Integer.parseInt(_attribute.name.substring(index).trim());
                ret = num1.compareTo(num2);
            } else  {
                ret = this.name.compareTo(_attribute.name);
            }
            return ret;
        }

        /**
         * Writes the attribute name and value to the TCL update file.
         *
         * @param _out  appendable instance to the TCL update file
         * @throws IOException if attribute information could not be written
         * @see #name
         * @see #value
         */
        public void write(final Appendable _out)
                throws IOException
        {
            _out.append(" \\\n    \"").append(convertTcl(this.name))
                .append("\" \"").append(convertTcl(this.value)).append("\"");
        }
   }

    /**
     * The class is used to store information about one connection including
     * all attributes values and the type, name with revision of the connected
     * business object.
     */
    private class Connection
            implements Comparable<Connection>
    {
        /**
         * Type of the business object.
         */
        final String type;

        /**
         * Name of the business object.
         */
        final String name;

        /**
         * Revision of the business object.
         */
        final String revision;

        /**
         * Id of the connection.
         */
        final String conId;

        /**
         * Direction of the connection.
         */
        final String direction;

        /**
         * Name of the relationship.
         */
        final String relName;

        /**
         * Set of all attribute values of this connection.
         */
        final Set<AttributeValue> values = new TreeSet<AttributeValue>();

        /**
         * Constructor to create a new connection instance.
         *
         * @param _relName      name of the relationship
         * @param _direction    direction of the connection
         * @param _relSelect    relationship selection
         * @param _attrs        selected attributes within the relationship
         *                      selection
         */
        public Connection(final String _relName,
                          final String _direction,
                          final RelationshipWithSelect _relSelect,
                          final Map<String,String> _attrs)
        {
            this.relName = _relName;
            this.direction = _direction;
            final BusinessObjectWithSelect busSelect = _relSelect.getTarget();
            this.type = busSelect.getSelectData("type");
            this.name = busSelect.getSelectData("name");
            this.revision = busSelect.getSelectData("revision");
            this.conId = _relSelect.getSelectData("id");
            for (final Map.Entry<String,String> attr : _attrs.entrySet())  {
                this.values.add(new AttributeValue(attr.getValue(), _relSelect.getSelectData(attr.getKey())));
            }
        }

        /**
         * Returns the string representation of the business object. The string
         * representation is a concatenation of {@link #type}, {@link #name} and
         * {@link #revision}.
         *
         * @return string representation of the business object
         * @see #type
         * @see #name
         * @see #revision
         */
        @Override
        public String toString()
        {
            return "[type = " + this.type + ","
                   + " name = " + this.name + ","
                   + " revision = " + this.revision + "]";
        }

        /**
         * Compares given connection with this connection. The compare is done
         * in this order:
         * <ul>
         * <li>compare connection direction</li>
         * <li>if equal compare relationship name</li>
         * <li>if equal compare business object type</li>
         * <li>if equal compare business object name</li>
         * <li>if equal compare business object revision</li>
         * <li>if equal compare attributes with
         *     {@link #compareToAttr(Connection)}</li>
         * </ul>
         *
         * @param _compare      connection instance to compare
         * @return <code>0</code> if the connections are equal (should not
         *         possible...); a value less than <code>0</code> if this
         *         connection is lexicographically less than the compared
         *         connection; a value greater than <code>0</code> if this
         *         connection is lexicographically greater than the compared
         *         connection
         */
        public int compareTo(final Connection _compare)
        {
            return this.direction.equals(_compare.direction)
                   ? this.relName.equals(_compare.relName)
                       ? this.type.equals(_compare.type)
                           ? this.name.equals(_compare.name)
                               ? this.revision.equals(_compare.revision)
                                   ? this.compareToAttr(_compare)
                                   : this.revision.compareTo(_compare.revision)
                               : this.name.compareTo(_compare.name)
                           : this.type.compareTo(_compare.type)
                       : this.relName.compareTo(_compare.relName)
                   : this.direction.compareTo(_compare.direction);
        }

        /**
         * Compares all attributes values from this connection with the values
         * from the compared connection. If all values of both connections are
         * equal, the connection id's are compared. The attribute values are
         * compare in the order of the attributes (by attribute names).
         *
         * @param _compare  connection to compare
         * @return <code>0</code> if all attributes values and both connection
         *         id's are equal (should not possible...); a value less than
         *         <code>0</code> if one attribute value is lexicographically
         *         less than the compared attribute value; a value greater than
         *         <code>0</code> if one attribute value is lexicographically
         *         greater than the other attribute value
         */
        private int compareToAttr(final Connection _compare)
        {
            final Iterator<AttributeValue> thisIter = this.values.iterator();
            final Iterator<AttributeValue> compIter = _compare.values.iterator();
            int ret = 0;
            while (thisIter.hasNext() && (ret == 0))  {
                ret = thisIter.next().value.compareTo(compIter.next().value);
            }
            return (ret == 0)
                   ? this.conId.compareTo(_compare.conId)
                   : ret;
        }

        /**
         * Writes the attribute name and value to the TCL update file.
         *
         * @param _out          appendable instance to the TCL update file
         * @throws IOException if attribute information could not be written
         * @see #name
         * @see #value
         */
        public void write(final Appendable _out)
                throws IOException
        {
            _out.append("\nmql connect bus \"${OBJECTID}\" \\")
                .append("\n    relationship \"").append(this.relName).append("\" \\")
                .append("\n    ").append(this.direction).append(" \"")
                    .append(convertTcl(this.type)).append("\" \"")
                    .append(convertTcl(this.name)).append("\" \"")
                    .append(convertTcl(this.revision)).append("\"");
            for (final AttributeValue attr : this.values)  {
                attr.write(_out);
            }
        }
    }
}
