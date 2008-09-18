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
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class MatrixBusObject_mxJPO
        extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO
{
    /**
     * String used to split the name and revision of administrational business
     * object.
     */
    private static final String SPLIT_NAME = "________";

    /**
     * Attribute name of the author.
     */
    private static final String ATTR_AUTHOR = "emxGerLibUpdateAuthor";

    /**
     * Attribute name of the installation date.
     */
    private static final String ATTR_INSTALLED_DATE = "emxGerLibUpdateInstalledDate";

    /**
     * Attribute name of the updated version.
     */
    private static final String ATTR_UPDATE_VERSION = "emxGerLibUpdateVersion";

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
     * Returns the file name for this matrix business object. The file name is
     * a concatenation of the defined file prefix within the business type
     * annotation in upper case, an underline (&quot;_&quot;),the name of the
     * matrix object and &quot;.tcl&quot; as extension.
     *
     * @return file name of this matrix object
     */
    @Override
    public String getFileName()
    {
        return new StringBuilder()
                .append(getClass().getAnnotation(net.sourceforge.mxupdate.update.util.BusType_mxJPO.class).filePrefix().toUpperCase())
                .append('_')
                .append(getName())
                .append(".tcl")
                .toString();
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
                .append(getBusType())
                .append("\" \"").append(nameRev[0])
                .append("\" \"").append((nameRev.length > 2) ? nameRev[1] : "")
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
        } else if ("/attributeList/attribute/string".equals(_url))  {
            this.attrValues.peek().value = _content;

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
     * update script (concatination of the revision and description) and the
     * name (concationation of name and revision).
     *
     * @param _context  context for this request
     * @see #attrValues         unsorted attribute values
     * @see #attrValuesSorted   sorted attribute values
     * @see #busDescription     business description
     * @see #busRevision        business revison
     */
    @Override
    protected void prepare(Context _context) throws MatrixException
    {
        for (final Attribute attrValue : this.attrValues)  {
            if (ATTR_AUTHOR.equals(attrValue.name))  {
                setAuthor(attrValue.value);
            } else if (!ATTR_INSTALLED_DATE.equals(attrValue.name)
                    && !ATTR_UPDATE_VERSION.equals(attrValue.name))  {
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
            .append(" \\\n    description \"").append(convert(this.busDescription)).append("\"");
        for (final Attribute attr : this.attrValuesSorted)  {
          _out.append(" \\\n    \"").append(convert(attr.name))
              .append("\" \"").append(convert(attr.value)).append("\"");
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
        return getClass().getAnnotation(net.sourceforge.mxupdate.update.util.BusType_mxJPO.class).type();
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
     * Getter method for instance variable {@link #busRevision}.
     *
     * @return value of instance variable {@link #busRevision}
     * @see #busRevision
     */
    protected String getBusRevision()
    {
        return this.busRevision;
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
