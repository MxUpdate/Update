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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractAdminObject_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractPropertyObject_mxJPO
{
    /**
     * Is the matrix object hidden?
     *
     * @see #isHidden()
     */
    private boolean hidden = false;

    final Stack<Property> propertiesStack = new Stack<Property>();

    final Map<String,Property> propertiesMap = new TreeMap<String,Property>();

    private final static Set<String> IGNORED_PROPERTIES = new HashSet<String>();
    static  {
        IGNORED_PROPERTIES.add("version");
        IGNORED_PROPERTIES.add("installed date");
        IGNORED_PROPERTIES.add("original name");
        IGNORED_PROPERTIES.add("application");
        IGNORED_PROPERTIES.add("installer");
        IGNORED_PROPERTIES.add("author");
    }

    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list ")
                .append(getInfoAnno().adminType())
                .append(" ")
                .append(getInfoAnno().adminTypeSuffix());
        final Set<String> ret = new TreeSet<String>();
        for (final String name : execMql(_context, cmd).split("\n"))  {
            for (final String match : _matches)  {
                if (match(name, match))  {
                    ret.add(name);
                }
            }
        }
        return ret;
    }

    @Override
    protected String getExportMQL(final String _name)
    {
        return "export " + getInfoAnno().adminType() + " \"" + _name + "\" xml";
    }

    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/adminProperties".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/creationInfo".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/creationInfo/datetime".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/modificationInfo".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/modificationInfo/datetime".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/name".equals(_url))  {
            setName(_content);
        } else if ("/adminProperties/description".equals(_url))  {
            setDescription(_content);
        } else if ("/adminProperties/hidden".equals(_url))  {
            this.hidden = true;

        } else if ("/adminProperties/propertyList".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/propertyList/property".equals(_url))  {
            this.propertiesStack.add(new Property());
        } else if ("/adminProperties/propertyList/property/adminRef".equals(_url))  {
            // to be ignored ...
        } else if ("/adminProperties/propertyList/property/adminRef/adminName".equals(_url))  {
            this.propertiesStack.peek().refAdminName = _content;
        } else if ("/adminProperties/propertyList/property/adminRef/adminType".equals(_url))  {
            this.propertiesStack.peek().refAdminType = _content;
        } else if ("/adminProperties/propertyList/property/flags".equals(_url))  {
            this.propertiesStack.peek().flags = _content;
        } else if ("/adminProperties/propertyList/property/name".equals(_url))  {
            this.propertiesStack.peek().name = _content;
        } else if ("/adminProperties/propertyList/property/value".equals(_url))  {
            this.propertiesStack.peek().value = _content;
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Sorted the properties and sets the author depending on the properties.
     *
     * @param _context      context for this request
     * @throws MatrixException
     * @see #propertiesStack    unsorted properties
     * @see #propertiesMap      sorted map of properties
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        // sort the properties
        for (final Property property : this.propertiesStack)  {
            final StringBuilder key = new StringBuilder().append(property.name);
            if ((property.refAdminName != null) && (property.refAdminName != null))  {
                key.append("::").append(property.refAdminType)
                   .append("::").append(property.refAdminName);
            }
            this.propertiesMap.put(key.toString(), property);
        }
        // set author depending on the properties
        final Property author = this.propertiesMap.get("author");
        if (author != null)   {
            setAuthor(author.value);
        }
        // sets the version depending on the properties
        final Property version = this.propertiesMap.get("version");
        if (version != null)   {
            setVersion(version.value);
        }
    }

        @Override
        protected void write(final Writer _out)
                throws IOException
        {
            writeHeader(_out);
            _out.append("mql mod ")
                .append(getInfoAnno().adminType())
                .append(" \"${NAME}\"");
            if (!"".equals(getInfoAnno().adminTypeSuffix()))  {
                _out.append(" ").append(getInfoAnno().adminTypeSuffix());
            }
            _out.append(" \\\n    description \"").append(convert(getDescription())).append("\"");
            writeObject(_out);
            writeProperties(_out);
            writeEnd(_out);
        }

        protected abstract void writeObject(final Writer _out) throws IOException;

    protected void writeEnd(final Writer _out)
            throws IOException
    {
    }

    protected void writeProperties(final Writer _out)
            throws IOException
    {
        for (final Property prop : this.propertiesMap.values())  {
            if (!IGNORED_PROPERTIES.contains(prop.name) && !prop.name.startsWith("%"))  {
                _out.append("\nmql add property \"").append(convert(prop.name)).append("\"")
                    .append(" \\\n    on ")
                    .append(getInfoAnno().adminType())
                    .append(" \"${NAME}\"");
                if (!"".equals(getInfoAnno().adminTypeSuffix()))  {
                    _out.append(' ').append(getInfoAnno().adminTypeSuffix());
                }
                if ((prop.refAdminName) != null && (prop.refAdminType != null))  {
                    _out.append("  \\\n    to ").append(prop.refAdminType)
                        .append(" \"").append(convert(prop.refAdminName)).append("\"");
                }
                if (prop.value != null)  {
                    _out.append("  \\\n    value \"").append(convert(prop.value)).append("\"");
                }
            }
        }
    }

    /**
     * Creates given administration object from given type with given name.
     *
     * @param _context          context for this request
     * @param _file             file for which the administration object must
     *                          be created (not used)
     * @param _name             name of administration object to create
     */
    @Override
    public void create(final Context _context,
                       final File _file,
                       final String _name)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(getInfoAnno().adminType())
                .append(" \"").append(_name).append("\" ")
                .append(getInfoAnno().adminTypeSuffix());
        execMql(_context, cmd);
    }

    /**
     * Appends the MQL statements to remove all properties within the reset.
     *
     * @param _cmd      string builder used to append the MQL statements
     */
    protected void appendResetProperties(final StringBuilder _cmd)
    {
        for (final Property prop : this.propertiesMap.values())  {
            if (!IGNORED_PROPERTIES.contains(prop.name) && !prop.name.startsWith("%"))  {
                _cmd.append(" remove property \"").append(prop.name).append('\"');
                if ((prop.refAdminName) != null && (prop.refAdminType != null))  {
                    _cmd.append(" to ").append(prop.refAdminType)
                        .append(" \"").append(prop.refAdminName).append('\"');
                }
            }
        }
    }

    /**
     * Getter method for instance variable {@link #hidden}.
     *
     * @return value of instance variable {@link #hidden}.
     */
    protected boolean isHidden()
    {
        return this.hidden;
    }

    /**
     * Getter method for instance variable {@link #propertiesMap}.
     *
     * @return value of instance variable {@link #propertiesMap}.
     */
    protected Map<String,Property>  getPropertiesMap()
    {
        return this.propertiesMap;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Property with name, value and referenced administration type. The kind
     * of property (with reference, with value, ...) is stored as flag.
     */
    protected class Property
    {
        /**
         * Name of the property.
         */
        String name = null;

        /**
         * Value of the property.
         */
        String value = null;

        /**
         * Flag of the property.
         */
        String flags = null;

        /**
         * Type of the referenced administration object for this property (if
         * defined).
         */
        String refAdminType = null;

        /**
         * Name of the referenced administration object for this property (if
         * defined).
         */
        String refAdminName = null;

        /**
         * Getter method for instance variable {@link #name}.
         *
         * @return value of instance variable {@link #name}.
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * Getter method for instance variable {@link #value}.
         *
         * @return value of instance variable {@link #value}.
         */
        public String getValue()
        {
            return this.value;
        }

        @Override
        public String toString()
        {
            return "[name="+name+", value="+value+", flags="+flags+"]";
        }
    }
}