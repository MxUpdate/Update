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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.Query;
import matrix.db.RelationshipWithSelect;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.xml.sax.SAXException;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
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
    private static final String PARAMCACHE_KEY_ATTRS = "DefaultAttributeValues";

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
    private String busName;

    /**
     * Revision of business object.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #getBusRevision()
     */
    private String busRevision;

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
     * Holds all to connected objects.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #write(ParameterCache_mxJPO, Writer)
     */
    private final Map<String,Set<Connection>> tos = new TreeMap<String,Set<Connection>>();

    /**
     * Holds all from connected objects.
     *
     * @see #parse(ParameterCache_mxJPO, String)
     * @see #write(ParameterCache_mxJPO, Writer)
     */
    private final Map<String,Set<Connection>> froms = new TreeMap<String,Set<Connection>>();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    public BusObject_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
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
     * update script (concatenation of the revision and description) and the
     * name (concatenation of name and revision).
     *
     * @param _paramCache   parameter cache
     * @param _name         name of administration object which must be parsed
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
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
    protected void parse(final ParameterCache_mxJPO _paramCache,
                         final String _name)
            throws MatrixException
    {
        final String[] nameRev = _name.split(SPLIT_NAME);

        this.busName = nameRev[0];
        this.busRevision = (nameRev.length > 1) ? nameRev[1] : "";

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

        // define name
        final StringBuilder name = new StringBuilder().append(this.busName);
        if ((this.busRevision != null) && !"".equals(this.busRevision))  {
            name.append(SPLIT_NAME).append(this.busRevision);
        }
        this.setName(name.toString());

        // evaluate from connections
        if (this.getTypeDef().getMxBusRelsFrom() != null)  {
            for (final String relation : this.getTypeDef().getMxBusRelsFrom())  {
                final Set<Connection> cons = new TreeSet<Connection>();
                this.froms.put(relation, cons);
                this.expand4Parse(_paramCache, bus, relation, cons, true, false);
            }
        }

        // evaluate to connections
        if (this.getTypeDef().getMxBusRelsTo() != null)  {
            for (final String relation : this.getTypeDef().getMxBusRelsTo())  {
                final Set<Connection> cons = new TreeSet<Connection>();
                this.tos.put(relation, cons);
                this.expand4Parse(_paramCache, bus, relation, cons, false, true);
            }
        }

        // evaluate both connections
        if (this.getTypeDef().getMxBusRelsBoth() != null)  {
            for (final String relation : this.getTypeDef().getMxBusRelsBoth())  {
                // from
                Set<Connection> cons = this.froms.get(relation);
                if (cons == null)  {
                    cons = new TreeSet<Connection>();
                    this.froms.put(relation, cons);
                }
                this.expand4Parse(_paramCache, bus, relation, cons, true, false);
                // to
                cons = this.tos.get(relation);
                if (cons == null)  {
                    cons = new TreeSet<Connection>();
                    this.tos.put(relation, cons);
                }
                this.expand4Parse(_paramCache, bus, relation, cons, false, true);
            }
        }
    }

    private void expand4Parse(final ParameterCache_mxJPO _paramCache,
                              final BusinessObject _bus,
                              final String _relation,
                              final Set<Connection> _cons,
                              final boolean _getFrom,
                              final boolean _getTo)
            throws MatrixException
    {
        final StringList busSelect = new StringList(3);
        busSelect.addElement("type");
        busSelect.addElement("name");
        busSelect.addElement("revision");
        final StringList relSelect = new StringList(1);
        relSelect.addElement("id");
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
            final RelationshipWithSelect rel = (RelationshipWithSelect) obj;
            final BusinessObjectWithSelect map = rel.getTarget();
            _cons.add(new Connection(map.getSelectData("type"),
                                     map.getSelectData("name"),
                                     map.getSelectData("revision"),
                                     rel.getSelectData("id")));
        }

    }

    /**
     * Writes the information to update the business objects.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
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
          _out.append(" \\\n    \"").append(convertTcl(attr.name))
              .append("\" \"").append(convertTcl(attr.value)).append("\"");
        }
        // write all from objects
        for (final Map.Entry<String, Set<Connection>> fromEntry : this.froms.entrySet())  {
            for (final Connection con : fromEntry.getValue())  {
                _out.append("\nmql connect bus \"${OBJECTID}\" \\")
                    .append("\n    relationship \"").append(fromEntry.getKey()).append("\" \\")
                    .append("\n    from \"").append(con.type).append("\" \"")
                            .append(con.name).append("\" \"")
                            .append(con.revision).append("\"");
            }
        }
        // write all to objects
        for (final Map.Entry<String, Set<Connection>> toEntry : this.tos.entrySet())  {
            for (final Connection con: toEntry.getValue())  {
                _out.append("\nmql connect bus \"${OBJECTID}\" \\")
                    .append("\n    relationship \"").append(toEntry.getKey()).append("\" \\")
                    .append("\n    to \"").append(con.type).append("\" \"")
                            .append(con.name).append("\" \"")
                            .append(con.revision).append("\"");
            }
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
     * @param _context      context for this request
     * @param _name         name of object to delete
     * @throws Exception if delete failed
     */
    @Override
    public void delete(final Context _context,
                       final String _name)
            throws Exception
    {
        final TypeDef_mxJPO typeDef = this.getTypeDef();
        final String[] nameRev = _name.split(SPLIT_NAME);
        final StringBuilder cmd = new StringBuilder()
                .append("delete bus \"").append(typeDef.getMxBusType()).append('\"')
                .append(" \"").append(nameRev[0]).append("\" ")
                .append(" \"").append((nameRev.length > 1) ? nameRev[1] : "").append("\";");
        execMql(_context, cmd);
    }

    /**
     * Creates for given name the business object.
     *
     * @param _context  context for this request
     * @param _file     defines the file for which the business object is
     *                  created
     * @param _name     name and revision of the business object
     */
    @Override
    public void create(final Context _context,
                       final File _file,
                       final String _name)
            throws Exception
    {
        final TypeDef_mxJPO busType = this.getTypeDef();
        final String[] nameRev = _name.split(SPLIT_NAME);
        final BusinessObject bus = new BusinessObject(busType.getMxBusType(),
                                                      nameRev[0],
                                                      (nameRev.length > 1) ? nameRev[1] : "",
                                                      busType.getMxBusVault());
        bus.create(_context, busType.getMxBusPolicy());
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
        final Map<String,String> defaultAttrValues = _paramCache.defineValueMap(PARAMCACHE_KEY_ATTRS);
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

        // disconnect all from objects
        for (final Map.Entry<String, Set<Connection>> fromEntry : this.froms.entrySet())  {
            for (final Connection con : fromEntry.getValue())  {
                preMQLCode.append("disconnect connection ").append(con.conId).append(";\n");
            }
        }

        // disconnect all to objects
        for (final Map.Entry<String, Set<Connection>> toEntry : this.tos.entrySet())  {
            for (final Connection con: toEntry.getValue())  {
                preMQLCode.append("disconnect connection ").append(con.conId).append(";\n");
            }
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
            final DateFormat format = new SimpleDateFormat(_paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLEDDATEFORMAT));
            postMQLCode.append(" \"").append(AdminPropertyDef.INSTALLEDDATE.getAttrName())
                    .append("\" \"").append(format.format(new Date())).append('\"');
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
     * Class used to hold the user access.
     */
    protected class AttributeValue
            implements Comparable<AttributeValue>
    {
        /**
         * Holds the user references of a user access.
         */
        public String name = null;

        /**
         * Holds the expression filter of a user access.
         */
        public String value = null;

        AttributeValue(final Attribute _attr)
        {
            this.name = _attr.getName();
            this.value = _attr.getValue();
        }

        /**
         * @param _attribute    attribute instance to compare
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
   }

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

        public Connection(final String _type,
                          final String _name,
                          final String _revision,
                          final String _conId)
        {
            this.type = _type;
            this.name = _name;
            this.revision = _revision;
            this.conId = _conId;
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
         * Compares given business object with this business object. First the type
         * is compared. If the types are equal, the names are compared. If the
         * names are equal, the revisions are compared.
         *
         * @param _compare      business object to compare
         */
        public int compareTo(final Connection _compare)
        {
            return this.type.equals(_compare.type)
                   ? this.name.equals(_compare.name)
                           ? this.revision.equals(_compare.revision)
                                   ? 0
                                   : this.revision.compareTo(_compare.revision)
                           : this.name.compareTo(_compare.name)
                   : this.type.compareTo(_compare.type);
        }
    }
}
