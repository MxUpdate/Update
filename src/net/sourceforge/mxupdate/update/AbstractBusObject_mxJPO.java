/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;

import net.sourceforge.mxupdate.util.Mapping_mxJPO.AttributeDef;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.setHistoryOff;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.setHistoryOn;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractBusObject_mxJPO
        extends AbstractPropertyObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -5381775541507933947L;

    /**
     * String used to split the name and revision of administrational business
     * object.
     */
    private static final String SPLIT_NAME = "________";

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
    private String busName = null;

    /**
     * Revision of business object.
     *
     * @see #parse(String, String)
     * @see #getBusRevision()
     */
    private String busRevision = null;

    /**
     * Vault of the business object.
     *
     * @see #parse(String, String)
     */
    private String busVault = null;

    /**
     * Description of the business object (because the description within the
     * header of the TCL file includes the revision of the business object).
     *
     * @see #parse(String, String)
     * @see #getBusDescription()
     */
    private String busDescription = null;

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
        query.setBusinessObjectType(getBusType());
        final BusinessObjectWithSelectList list = query.select(_context, selects);
        query.close(_context);

        final Set<String> ret = new TreeSet<String>();
        for (final Object mapObj : list)  {
            final BusinessObjectWithSelect map = (BusinessObjectWithSelect) mapObj;
            final StringBuilder name = new StringBuilder()
                    .append(map.getSelectDataList("name").get(0));
            final String revision = (String) map.getSelectDataList("revision").get(0);
            if ((revision != null) && !"".equals(revision))  {
                name.append(SPLIT_NAME)
                    .append(map.getSelectDataList("revision").get(0));
            }
            for (final String match : _matches)  {
                if (match(name.toString(), match))  {
                    ret.add(name.toString());
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
    protected String getExportMQL(final String _name)
    {
        final String[] nameRev = _name.split(SPLIT_NAME);

        return new StringBuilder()
                .append("export bus \"")
                .append(this.getBusType())
                .append("\" \"").append(nameRev[0])
                .append("\" \"").append((nameRev.length > 1) ? nameRev[1] : "")
                .append("\" !file !icon !history !relationship !state xml")
                .toString();
    }

    /**
     * Parses the XML url for the business object XML export file.
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
     */
    @Override
    protected void prepare(final Context _context) throws MatrixException
    {
        for (final Attribute attrValue : this.attrValues)  {
            if (AttributeDef.CommonAuthor.getMxName().equals(attrValue.name))  {
                this.setAuthor(attrValue.value);
            } else if (AttributeDef.CommonVersion.getMxName().equals(attrValue.name))  {
                this.setVersion(attrValue.value);
            } else if (!AttributeDef.CommonInstalledDate.getMxName().equals(attrValue.name))  {
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
    }

    /**
     * Writes the information to update the business objects.
     *
     * @param _out      writer instance
     */
    @Override
    protected void write(final Writer _out)
            throws IOException
    {
        writeHeader(_out);
        _out.append("mql mod bus \"${OBJECTID}\"")
            .append(" \\\n    description \"").append(convertTcl(this.busDescription)).append("\"");
        for (final Attribute attr : this.attrValuesSorted)  {
          _out.append(" \\\n    \"").append(convertTcl(attr.name))
              .append("\" \"").append(convertTcl(attr.value)).append("\"");
        }
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
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _tclCode          TCL code from the file used to update
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        // found the business object
        final BusinessObject bus = new BusinessObject(this.getBusType(),
                                                      this.getBusName(),
                                                      this.getBusRevision(),
                                                      this.getBusVault());
        final String objectId = bus.getObjectId(_context);

        // resets the description
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod bus ").append(objectId).append(" description \"\"");

        // reset all attributes (if they must not be ignored...)
        final AttributeDef[] ignoreAttrs = this.getInfoAnno().busIgnoreAttributes();
        for (final Attribute attr : this.attrValuesSorted)  {
// TODO: use default attribute value instead of ""
            boolean found = false;
            for (final AttributeDef ignoreAttr : ignoreAttrs)  {
                if (ignoreAttr.getMxName().equals(attr.name))  {
                    found = true;
                    break;
                }
            }
            if (!found)  {
                preMQLCode.append(" \"").append(attr.name).append("\" \"\"");
            }
        }
        preMQLCode.append(";\n");

        // append other pre MQL code
        preMQLCode.append(_preMQLCode);

        // post update MQL statements to define the version
        final StringBuilder postMQLCode = new StringBuilder()
                .append(_postMQLCode)
                .append("mod bus ").append(objectId).append(" \"")
                .append(AttributeDef.CommonVersion.getMxName()).append("\" \"").append(_tclVariables.get("VERSION")).append("\";\n");

        // prepare map of all TCL variables incl. id of business object
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.put("OBJECTID", objectId);
        tclVariables.putAll(_tclVariables);

        // update must be done with history off (because not required...)
        try  {
            setHistoryOff(_context);
            super.update(_context, preMQLCode, postMQLCode, _tclCode, tclVariables);
        } finally  {
            setHistoryOn(_context);
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
        return getInfoAnno().busType().getMxName();
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
