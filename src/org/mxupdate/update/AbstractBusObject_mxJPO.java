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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.update.util.StringUtil_mxJPO.match;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOff;
import static org.mxupdate.util.MqlUtil_mxJPO.setHistoryOn;

/**
 * @author Tim Moxter
 * @version $Id$
 */
public abstract class AbstractBusObject_mxJPO
        extends AbstractPropertyObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private final static long serialVersionUID = -5381775541507933947L;

    /**
     * Key used to store default attribute values within the parameter cache.
     *
     * @see #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)
     */
    private final static String PARAMCACHE_KEY_ATTRS = "DefaultAttributeValues";

    /**
     * String used to split the name and revision of administration business
     * object.
     */
    public static final String SPLIT_NAME = "________";

    /**
     * All attribute values of this business object.
     *
     * @see #parse(String, String)      reads the attribute values
     * @see #prepare(Context)           sortes the attribute values
     */
    private final Stack<Attribute> attrValues = new Stack<Attribute>();

    /**
     * Sorted set of attribute values.
     *
     * @see #prepare(Context)           sorted the attribute values
     * @see #getAttrValuesSorted()
     */
    private final Set<Attribute> attrValuesSorted = new TreeSet<Attribute>();

    /**
     * Name of business object.
     *
     * @see #parse(String, String)
     * @see #getBusName()
     */
    private String busName;

    /**
     * Revision of business object.
     *
     * @see #parse(String, String)
     * @see #getBusRevision()
     */
    private String busRevision;

    /**
     * Vault of the business object.
     *
     * @see #parse(String, String)
     */
    private String busVault;

    /**
     * Description of the business object (because the description within the
     * header of the TCL file includes the revision of the business object).
     *
     * @see #parse(String, String)
     * @see #getBusDescription()
     */
    private String busDescription;

    /**
     * Object id of the business object.
     *
     * @see #prepare(Context)
     * @see #getBusOid()
     */
    private String busOid;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    protected AbstractBusObject_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Evaluates the matching names for this administrational business objects.
     * Matching means, that the name and / or revision of the business object
     * matches the collection of given matches.
     *
     * @param _context          context for this request
     * @param _matches          collection of matches
     * @return set of found matching business object names
     */
    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final StringList selects = new StringList();
        selects.addElement("name");
        selects.addElement("revision");

        final Query query = new Query();
        query.open(_context);
        query.setBusinessObjectType(this.getTypeDef().getMxBusType());
        final BusinessObjectWithSelectList list = query.select(_context, selects);
        query.close(_context);

        final Set<String> ret = new TreeSet<String>();
        for (final Object mapObj : list)  {
            final BusinessObjectWithSelect map = (BusinessObjectWithSelect) mapObj;
            final String busName = (String) map.getSelectDataList("name").get(0);
            final String busRevision = (String) map.getSelectDataList("revision").get(0);
            final StringBuilder name = new StringBuilder()
                    .append(map.getSelectDataList("name").get(0));
            if ((busRevision != null) && !"".equals(busRevision))  {
                name.append(SPLIT_NAME)
                    .append(map.getSelectDataList("revision").get(0));
            }
            for (final String match : _matches)  {
                if (match(busName, match) || match(busRevision, match))  {
                    ret.add(name.toString());
                }
            }
        }
        return ret;
    }

    /**
     * Evaluates for given set of files all matching files and returns them as
     * map (key is the file name, value is the name of the matrix
     * administration (business) object).<br/>
     * For the file name without prefix and suffix the name is splitted to get
     * the name and revision of the business object. If the business object
     * name or revision matches one of the collection match strings, the file
     * is added to the map and is returned.
     *
     * @param _files            set of files used to found matching files
     * @param _matches          collection of match strings
     * @return map of files (as key) with the related matrix name (as value)
     * @see #getMatchingFileNames(Set)
     */
    @Override
    public Map<File, String> getMatchingFileNames(final Set<File> _files,
                                                  final Collection<String> _matches)
    {
        final Map<File,String> ret = new TreeMap<File,String>();

        final String suffix = this.getTypeDef().getFileSuffix();
        final int suffixLength = suffix.length();
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;

        for (final File file : _files)  {
            final String fileName = file.getName();
            for (final String match : _matches)  {
                if (((prefix == null) || fileName.startsWith(prefix)) && fileName.endsWith(suffix))  {
                    final String name = fileName.substring(0, fileName.length() - suffixLength)
                                                .substring(prefixLength);
                    final String[] nameRev = name.split(SPLIT_NAME);
                    if (match(nameRev[0], match) || ((nameRev.length > 1) && match(nameRev[1], match)))  {
                        ret.put(file, name);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Prepares the MQL statement to export the business object as XML string.
     *
     * @param _name     name of business object to export (name and revision,
     *                  separated by the splitter string)
     * @return prepared MQL statement
     * @see #SPLIT_NAME used splitter between name and revision
     */
    @Override
    protected String getExportMQL()
    {
        final String[] nameRev = this.getName().split(SPLIT_NAME);

        return new StringBuilder()
                .append("export bus \"")
                .append(this.getBusType())
                .append("\" \"").append(nameRev[0])
                .append("\" \"").append((nameRev.length > 1) ? nameRev[1] : "")
                .append("\" !file !icon !history !relationship !state xml")
                .toString();
    }

    /**
     * Parses the XML Url for the business object XML export file.
     *
     * @param _url      url
     * @param _content  content
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/attributeList".equals(_url))  {
            // to be ignored ...
        } else if ("/attributeList/attribute".equals(_url))  {
            this.attrValues.add(new Attribute());
        } else if ("/attributeList/attribute/name".equals(_url))  {
            this.attrValues.peek().name = _content;
        } else if ("/attributeList/attribute/boolean".equals(_url))  {
            this.attrValues.peek().value = _content;
        } else if ("/attributeList/attribute/integer".equals(_url))  {
            this.attrValues.peek().value = _content;
        } else if ("/attributeList/attribute/string".equals(_url))  {
            this.attrValues.peek().value = _content;
        } else if ("/attributeList/attribute/unknown".equals(_url))  {
            // to be ignored, because no value defined...

        } else if ("/objectType".equals(_url))  {
            // to be ignored ...
        } else if ("/objectName".equals(_url))  {
            this.busName = _content;
        } else if ("/objectRevision".equals(_url))  {
            this.busRevision = _content;
        } else if ("/description".equals(_url))  {
            this.busDescription = _content;
        } else if ("/vaultRef".equals(_url))  {
            this.busVault = _content;
        } else if ("/policyRef".equals(_url))  {
            // to be ignored ...
        } else if ("/owner".equals(_url))  {
            // to be ignored ...
        } else if ("/owner/userRef".equals(_url))  {
            // to be ignored ...
        } else if ("/creationInfo".equals(_url))  {
            // to be ignored ...
        } else if ("/creationInfo/datetime".equals(_url))  {
            // to be ignored ...
        } else if ("/modificationInfo".equals(_url))  {
            // to be ignored ...
        } else if ("/modificationInfo/datetime".equals(_url))  {
            // to be ignored ...
        } else if ("/locker".equals(_url))  {
            // to be ignored ...
        } else if ("/locker/userRef".equals(_url))  {
            // to be ignored ...

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Sorts the attribute values, defines the description for the TCL
     * update script (concatenation of the revision and description) and the
     * name (concatenation of name and revision).
     *
     * @param _context  context for this request
     * @see #attrValues         unsorted attribute values
     * @see #attrValuesSorted   sorted attribute values
     * @see #busDescription     business description
     * @see #busRevision        business revison
     * @see #busOid
     */
    @Override
    protected void prepare(final Context _context) throws MatrixException
    {
        for (final Attribute attrValue : this.attrValues)  {
            if (AdminPropertyDef.AUTHOR.getAttrName().equals(attrValue.name))  {
                this.setAuthor(attrValue.value);
            } else if (AdminPropertyDef.APPLICATION.getAttrName().equals(attrValue.name))  {
                this.setApplication(attrValue.value);
            } else if (AdminPropertyDef.INSTALLEDDATE.getAttrName().equals(attrValue.name))  {
                this.setInstallationDate(attrValue.value);
            } else if (AdminPropertyDef.INSTALLER.getAttrName().equals(attrValue.name))  {
                this.setInstaller(attrValue.value);
            } else if (AdminPropertyDef.VERSION.getAttrName().equals(attrValue.name))  {
                this.setVersion(attrValue.value);
            } else if (!AdminPropertyDef.FILEDATE.getAttrName().equals(attrValue.name))  {
                this.attrValuesSorted.add(attrValue);
            }
        }
        // defines the description
        final StringBuilder desc = new StringBuilder();
        if (this.busRevision != null)  {
            desc.append(this.busRevision);
            if (this.busDescription != null)  {
                desc.append('\n');
            }
        }
        if (this.busDescription != null)  {
            desc.append(this.busDescription);
        }
        setDescription(desc.toString());
        // defines the name
        final StringBuilder name = new StringBuilder().append(this.busName);
        if (this.busRevision != null)  {
            name.append(SPLIT_NAME).append(this.busRevision);
        }
        setName(name.toString());
        // found the business object
        final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                      this.getBusName(),
                                                      this.getBusRevision(),
                                                      this.getBusVault());
        this.busOid = bus.getObjectId(_context);
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
        for (final Attribute attr : this.attrValuesSorted)  {
          _out.append(" \\\n    \"").append(convertTcl(attr.name))
              .append("\" \"").append(convertTcl(attr.value)).append("\"");
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
        for (final Attribute attr : this.attrValuesSorted)  {
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
System.out.println("    - define installer '" + instVal + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.INSTALLER.getAttrName())
                    .append("\" \"").append(instVal).append('\"');
        }
        // exists no application property or application property not equal?
        final String applVal = _tclVariables.get(AdminPropertyDef.APPLICATION.name());
        if ((this.getApplication() == null) || !this.getApplication().equals(applVal))  {
System.out.println("    - define application '" + applVal + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.APPLICATION.getAttrName())
                    .append("\" \"").append(applVal).append('\"');
        }
        // exists no author property or author property not equal?
        final String authVal = _tclVariables.get(AdminPropertyDef.AUTHOR.name());
        if ((this.getAuthor() == null) || !this.getAuthor().equals(authVal))  {
System.out.println("    - define author '" + authVal + "'");
            postMQLCode.append(" \"").append(AdminPropertyDef.AUTHOR.getAttrName())
                    .append("\" \"").append(AdminPropertyDef.AUTHOR.name()).append('\"');
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
    protected Set<Attribute> getAttrValuesSorted()
    {
        return this.attrValuesSorted;
    }

    /**
     * Class used to hold the user access.
     */
    protected class Attribute
            implements Comparable<Attribute>
    {
        /**
         * Holds the user references of a user access.
         */
        public String name = null;

        /**
         * Holds the expression filter of a user access.
         */
        public String value = null;

        /**
         * @param _attribute    attribute instance to compare
         */
        public int compareTo(final Attribute _attribute)
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
}
