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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractAdminObject_mxJPO
        extends AbstractPropertyObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 6211240989585499402L;

    /**
     * Is the matrix object hidden?
     *
     * @see #isHidden()
     */
    private boolean hidden = false;

    final Stack<Property> propertiesStack = new Stack<Property>();

    final Map<String,Property> propertiesMap = new TreeMap<String,Property>();

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
            _out.append(" \\\n    description \"").append(convertTcl(getDescription())).append("\"");
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
                _out.append("\nmql add property \"").append(convertTcl(prop.name)).append("\"")
                    .append(" \\\n    on ")
                    .append(getInfoAnno().adminType())
                    .append(" \"${NAME}\"");
                if (!"".equals(getInfoAnno().adminTypeSuffix()))  {
                    _out.append(' ').append(getInfoAnno().adminTypeSuffix());
                }
                if ((prop.refAdminName) != null && (prop.refAdminType != null))  {
                    _out.append("  \\\n    to ").append(prop.refAdminType)
                        .append(" \"").append(convertTcl(prop.refAdminName)).append("\"");
                }
                if (prop.value != null)  {
                    _out.append("  \\\n    value \"").append(convertTcl(prop.value)).append("\"");
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
     * The method overwrites the original method to
     * <ul>
     * <li>remove all existing properties</li>
     * <li>define the TCL variable for the name</li>
     * <li>define the property &quot;version&quot;</li>
     * </ul>
     * Then the update method of the super class is called.
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
        // remove all properties
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getInfoAnno().adminType())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getInfoAnno().adminTypeSuffix());
        for (final Property prop : this.propertiesMap.values())  {
            if (!IGNORED_PROPERTIES.contains(prop.name) && !prop.name.startsWith("%"))  {
                preMQLCode.append(" remove property \"").append(prop.name).append('\"');
                if ((prop.refAdminName) != null && (prop.refAdminType != null))  {
                    preMQLCode.append(" to ").append(prop.refAdminType)
                              .append(" \"").append(prop.refAdminName).append('\"');
                }
            }
        }
        preMQLCode.append(";\n");

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        // defined the version property
        final StringBuilder postMQLCode = new StringBuilder()
                .append(_postMQLCode)
                .append("mod ").append(this.getInfoAnno().adminType())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getInfoAnno().adminTypeSuffix())
                .append(" add property version value \"").append(_tclVariables.get("VERSION")).append("\";\n");

        // prepare map of all TCL variables incl. name of admin object
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.put("NAME", this.getName());
        tclVariables.putAll(_tclVariables);

        super.update(_context, preMQLCode, postMQLCode, _tclCode, tclVariables);
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

    /**
     * The string representation of this administration object is returned.
     * The string representation is the MQL update script and so method
     * {@link #write(Writer)} is called.
     *
     * @return string representation of this administration object
     * @see #write(Writer)
     */
    @Override
    public String toString()
    {
        final StringWriter writer = new StringWriter();
        try
        {
            this.write(writer);
        }
        catch (Exception e)
        {
            throw new Error(e);
        }
        return writer.toString();
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Property with name, value and referenced administration type. The kind
     * of property (with reference, with value, ...) is stored as flag.
     */
    // TODO define class in own file
   protected class Property
            implements Serializable
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = 7814222356799301361L;

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