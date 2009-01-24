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
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.util.Mapping_mxJPO.AdminPropertyDef;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.update.util.StringUtil_mxJPO.match;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

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

    /**
     *
     */
    private final Set<String> symbolicNames = new TreeSet<String>();

    @Override
    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list ")
                .append(this.getInfoAnno().adminType().getMxName())
                .append(" ")
                .append(this.getInfoAnno().adminType().getMxSuffix());
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
    protected String getExportMQL()
    {
        return "export " + this.getInfoAnno().adminType().getMxName() + " \"" + this.getName() + "\" xml";
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
     * Sorted the properties, sets the author and version depending on the
     * properties and reads the symbolic names.
     *
     * @param _context      context for this request
     * @throws MatrixException
     * @see #propertiesStack
     * @see #propertiesMap
     * @see #symbolicNames
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
        final Property author = this.propertiesMap.get(AdminPropertyDef.AUTHOR.getPropName());
        if (author != null)   {
            this.setAuthor(author.value);
        }
        // set application depending on the properties
        final Property appl = this.propertiesMap.get(AdminPropertyDef.APPLICATION.getPropName());
        if (appl != null)   {
            this.setApplication(appl.value);
        }
        // sets the installation date depending on the properties
        final Property installationDate = this.propertiesMap.get(AdminPropertyDef.INSTALLEDDATE.getPropName());
        if (installationDate != null)   {
            this.setInstallationDate(installationDate.value);
        }
        // sets the installer depending on the properties
        final Property installer = this.propertiesMap.get(AdminPropertyDef.INSTALLER.getPropName());
        if (installer != null)   {
            this.setInstaller(installer.value);
        }
        // sets the version depending on the properties
        final Property version = this.propertiesMap.get(AdminPropertyDef.VERSION.getPropName());
        if (version != null)   {
            this.setVersion(version.value);
        }

        // reads symbolic names of the administration objects
        final StringBuilder cmd = new StringBuilder()
                .append("list property on program eServiceSchemaVariableMapping.tcl to ")
                    .append(this.getInfoAnno().adminType().getMxName())
                    .append(" \"").append(this.getName()).append("\" ")
                    .append(this.getInfoAnno().adminType().getMxSuffix());
        for (final String symbName : execMql(_context, cmd).split("\n"))  {
            if (!"".equals(symbName))  {
                this.symbolicNames.add(symbName.substring(0, symbName.indexOf(' ')));
            }
        }
    }

        @Override
        protected void write(final Writer _out)
                throws IOException
        {
            writeHeader(_out);
            _out.append("mql mod ")
                .append(this.getInfoAnno().adminType().getMxName())
                .append(" \"${NAME}\"");
            if (!"".equals(this.getInfoAnno().adminType().getMxSuffix()))  {
                _out.append(" ").append(this.getInfoAnno().adminType().getMxSuffix());
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
            if ((AdminPropertyDef.getEnumByPropName(prop.name) == null) && !prop.name.startsWith("%"))  {
                _out.append("\nmql add property \"").append(convertTcl(prop.name)).append("\"")
                    .append(" \\\n    on ")
                    .append(this.getInfoAnno().adminType().getMxName())
                    .append(" \"${NAME}\"");
                if (!"".equals(this.getInfoAnno().adminType().getMxSuffix()))  {
                    _out.append(' ').append(this.getInfoAnno().adminType().getMxSuffix());
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
     * Deletes administration object from given type with given name.
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
        final StringBuilder cmd = new StringBuilder()
                .append("delete ").append(this.getInfoAnno().adminType().getMxName())
                .append(" \"").append(_name).append("\" ")
                .append(this.getInfoAnno().adminType().getMxSuffix());
        execMql(_context, cmd);
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
                .append("add ").append(this.getInfoAnno().adminType().getMxName())
                        .append(" \"").append(_name).append("\" ")
                        .append(this.getInfoAnno().adminType().getMxSuffix()).append(";");
        execMql(_context, cmd);
    }

    /**
     * The method overwrites the original method append MQL code to
     * <ul>
     * <li>remove all existing properties</li>
     * <li>define the TCL variable for the name</li>
     * <li>define property &quot;version&quot;</li>
     * <li>define property &quot;file date&quot;</li>
     * <li>define &quot;installed&quot; date property if not defined</li>
     * <li>define &quot;installer&quot; property if not defined</li>
     * <li>define &quot;original name&quot; property if not defined</li>
     * <li>define &quot;application&quot; property if not set or not equal to
     *     defined value from user</li>
     * <li>define &quot;author&quot; property if not set or not equal to value
     *     from TCL update code (or to default value if not defined in TCL
     *     update code)</li>
     * <li>register administration object (if not already done) and remove all
     *     symbolic names which are not correct</li>
     * </ul>
     * Then the update method of the super class is called.
     *
     * @param _context          context for this request
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
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        // remove all properties
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getInfoAnno().adminType().getMxName())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getInfoAnno().adminType().getMxSuffix());
        for (final Property prop : this.propertiesMap.values())  {
            // % must be ignored because this means settings
            if ((AdminPropertyDef.getEnumByPropName(prop.name) == null) && !prop.name.startsWith("%"))  {
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

        // define version property
        final StringBuilder postMQLCode = new StringBuilder()
                .append(_postMQLCode)
                .append("mod ").append(this.getInfoAnno().adminType().getMxName())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getInfoAnno().adminType().getMxSuffix())
                .append(" add property \"").append(AdminPropertyDef.VERSION.getPropName()).append("\" ")
                .append("value \"").append(_tclVariables.get(AdminPropertyDef.VERSION.name())).append('\"');
        // define file date property
        postMQLCode.append(" add property \"").append(AdminPropertyDef.FILEDATE.getPropName()).append("\" ")
                .append("value \"").append(_tclVariables.get(AdminPropertyDef.FILEDATE.name())).append('\"');
        // is installed date property defined?
        if ((this.getInstallationDate() == null) || "".equals(this.getInstallationDate()))  {
            final DateFormat format = new SimpleDateFormat(AdminPropertyDef.INSTALLEDDATE.getValue());
            postMQLCode.append(" add property \"").append(AdminPropertyDef.INSTALLEDDATE.getPropName()).append("\" ")
                    .append("value \"").append(format.format(new Date())).append('\"');
        }
        // is installer property defined?
        if ((this.getInstaller() == null) || "".equals(this.getInstaller()))  {
            postMQLCode.append(" add property \"").append(AdminPropertyDef.INSTALLER.getPropName()).append("\" ")
                    .append("value \"").append(AdminPropertyDef.INSTALLER.getValue()).append('\"');
        }
        // is original name property defined?
        if (!this.propertiesMap.containsKey(AdminPropertyDef.ORIGINALNAME.getPropName()))  {
            postMQLCode.append(" add property \"").append(AdminPropertyDef.ORIGINALNAME.getPropName()).append("\" ")
                    .append("value \"").append(this.getName()).append('\"');
        }
        // exists no application property or application property not equal?
        if ((this.getApplication() == null) || !AdminPropertyDef.APPLICATION.getValue().equals(this.getApplication()))  {
            postMQLCode.append(" add property \"").append(AdminPropertyDef.APPLICATION.getPropName()).append("\" ")
                    .append("value \"").append(AdminPropertyDef.APPLICATION.getValue()).append('\"');
        }
        // exists no author property or author property not equal?
        final String authVal = _tclVariables.get(AdminPropertyDef.AUTHOR.name());
        if ((this.getAuthor() == null) || !this.getAuthor().equals(authVal))  {
            postMQLCode.append(" add property \"").append(AdminPropertyDef.AUTHOR.getPropName()).append("\" ")
                    .append("value \"").append(authVal).append('\"');
        }
        postMQLCode.append(";\n");

        // check symbolic names
        final String symbName = _tclVariables.get("SYMBOLICNAME");
        if (!this.symbolicNames.contains(symbName))  {
System.out.println("    - register symbolic name '" + symbName + "'");
            postMQLCode.append("add property \"").append(symbName).append("\" ")
                    .append(" on program eServiceSchemaVariableMapping.tcl to ")
                    .append(this.getInfoAnno().adminType().getMxName())
                    .append(" \"").append(this.getName()).append("\" ")
                    .append(this.getInfoAnno().adminType().getMxSuffix())
                    .append(";\n");
        }
        for (final String exSymbName : this.symbolicNames)  {
            if (!symbName.equals(exSymbName))  {
System.out.println("    - remove symbolic name '" + exSymbName + "'");
                postMQLCode.append("delete property \"").append(symbName).append("\" ")
                        .append(" on program eServiceSchemaVariableMapping.tcl to ")
                        .append(this.getInfoAnno().adminType().getMxName())
                        .append(" \"").append(this.getName()).append("\" ")
                        .append(this.getInfoAnno().adminType().getMxSuffix())
                        .append(";\n");
            }
        }

        // prepare map of all TCL variables incl. name of admin object
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.put("NAME", this.getName());
        tclVariables.putAll(_tclVariables);

        super.update(_context, preMQLCode, postMQLCode, _preTCLCode, tclVariables, _sourceFile);
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