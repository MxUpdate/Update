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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * @author Tim Moxter
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
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    protected AbstractAdminObject_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Searches for all administration object of current type definition and
     * returns all found names as set.
     *
     * @param _paramCache   parameter cache
     * @return set of all administration object of current type definition
     * @throws MatrixException if the query for current administration object
     *                         failed
     */
    @Override
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list ")
                .append(this.getTypeDef().getMxAdminName())
                .append(" ")
                .append(this.getTypeDef().getMxAdminSuffix());
        final Set<String> ret = new TreeSet<String>();
        for (final String name : execMql(_paramCache.getContext(), cmd).split("\n"))  {
            if (!"".equals(name))  {
                ret.add(name);
            }
        }
        return ret;
    }

    /**
     * Creates a XML representation of the Object to export, parses them and
     * executes the post preparation {@link #prepare(Context)}.
     *
     * @param _paramCache   parameter cache
     * @param _name         name of object to parse
     * @see #getExportMQL(String)       used to get the MQL command to get a
     *                                  XML representation
     * @see PadSaxHandler               SAX handler to parse the XML file
     * @see #parse(String, String)      parser called within the SAX handler
     * @see #prepare(Context)           called post preparation method
     */
    @Override
    protected void parse(final ParameterCache_mxJPO _paramCache,
                         final String _name)
            throws MatrixException, SAXException, IOException
    {
        this.setName(_name);
        final String xml = execMql(_paramCache.getContext(), this.getExportMQL());

        // create XML reader
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        // register Sax Content Handler
        final PadSaxHandler handler = new PadSaxHandler();
        reader.setContentHandler(handler);
        reader.setDTDHandler(handler);
        reader.setEntityResolver(handler);
        // parse the XML string of the export
        final InputSource inputSource = new InputSource(new StringReader(xml));
        inputSource.setEncoding("UTF8");
        reader.parse(inputSource);
        // prepare post preparation
        this.prepare(_paramCache.getContext());
    }

    protected String getExportMQL()
    {
        return "export " + this.getTypeDef().getMxAdminName() + " \"" + this.getName() + "\" xml";
    }

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
            this.setName(_content);
        } else if ("/adminProperties/description".equals(_url))  {
            this.setDescription(_content);
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
            System.err.println("unkown parsing url: "+_url+"("+_content+")");
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
        if (author != null)  {
            this.setAuthor(author.value);
        }
        // set application depending on the properties
        final Property appl = this.propertiesMap.get(AdminPropertyDef.APPLICATION.getPropName());
        if (appl != null)  {
            this.setApplication(appl.value);
        }
        // sets the installation date depending on the properties
        final Property installationDate = this.propertiesMap.get(AdminPropertyDef.INSTALLEDDATE.getPropName());
        if (installationDate != null)  {
            this.setInstallationDate(installationDate.value);
        }
        // sets the installer depending on the properties
        final Property installer = this.propertiesMap.get(AdminPropertyDef.INSTALLER.getPropName());
        if (installer != null)  {
            this.setInstaller(installer.value);
        }
        // sets the original name depending on the properties
        final Property origName = this.propertiesMap.get(AdminPropertyDef.ORIGINALNAME.getPropName());
        if (origName != null)  {
            this.setOriginalName(origName.value);
        }
        // sets the version depending on the properties
        final Property version = this.propertiesMap.get(AdminPropertyDef.VERSION.getPropName());
        if (version != null)  {
            this.setVersion(version.value);
        }

        // reads symbolic names of the administration objects
        final StringBuilder cmd = new StringBuilder()
                .append("list property on program eServiceSchemaVariableMapping.tcl to ")
                    .append(this.getTypeDef().getMxAdminName())
                    .append(" \"").append(this.getName()).append("\" ")
                    .append(this.getTypeDef().getMxAdminSuffix());
        for (final String symbName : execMql(_context, cmd).split("\n"))  {
            if (!"".equals(symbName))  {
                this.getSymblicNames().add(symbName.substring(0, symbName.indexOf(" on program eServiceSchemaVariableMapping.tcl ")));
            }
        }
    }

    /**
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException
     */
    @Override
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Writer _out)
            throws IOException
    {
        this.writeHeader(_paramCache, _out);
        _out.append("mql mod ")
            .append(this.getTypeDef().getMxAdminName())
            .append(" \"${NAME}\"");
        if (!"".equals(this.getTypeDef().getMxAdminSuffix()))  {
            _out.append(" ").append(this.getTypeDef().getMxAdminSuffix());
        }
        _out.append(" \\\n    description \"").append(convertTcl(getDescription())).append("\"");
        this.writeObject(_out);
        this.writeProperties(_out);
        this.writeEnd(_out);
    }

    protected abstract void writeObject(final Writer _out) throws IOException;

    /**
     * At the end of the write to the TCL update file in some cases some
     * extensions must be defined. This &quot;extensions&quot; depends on the
     * use cases. E.g. for an inquiry, the inquiry code must be written at the
     * end of a TCL update file.
     *
     * @param _out      appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     */
    protected void writeEnd(final Appendable _out)
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
                    .append(this.getTypeDef().getMxAdminName())
                    .append(" \"${NAME}\"");
                if (!"".equals(this.getTypeDef().getMxAdminSuffix()))  {
                    _out.append(' ').append(this.getTypeDef().getMxAdminSuffix());
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
                .append("delete ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(_name).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix());
        execMql(_context, cmd);
    }

    /**
     * Creates given administration object from given type with given name.
     *
     * @param _context          context for this request
     * @param _file             file for which the administration object must
     *                          be created (not used)
     * @param _name             name of administration object to create
     * @throws Exception if the new administration object could not be created
     */
    @Override
    public void create(final Context _context,
                       final File _file,
                       final String _name)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("add ").append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(_name).append("\" ")
                        .append(this.getTypeDef().getMxAdminSuffix()).append(";");
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
     * @throws Exception if the update from the derived class failed
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
        // remove all properties
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix());
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
                .append("mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix())
                .append(" add property \"").append(AdminPropertyDef.VERSION.getPropName()).append("\" ")
                .append("value \"").append(_tclVariables.get(AdminPropertyDef.VERSION.name())).append('\"');
        // define file date property
        postMQLCode.append(" add property \"").append(AdminPropertyDef.FILEDATE.getPropName()).append("\" ")
                .append("value \"").append(_tclVariables.get(AdminPropertyDef.FILEDATE.name())).append('\"');
        // is installed date property defined?
        if ((this.getInstallationDate() == null) || "".equals(this.getInstallationDate()))  {
            final DateFormat format = new SimpleDateFormat(_paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLEDDATEFORMAT));
            final String date = format.format(new Date());
            _paramCache.logTrace("    - define installed date '" + date + "'");
            postMQLCode.append(" add property \"").append(AdminPropertyDef.INSTALLEDDATE.getPropName()).append("\" ")
                    .append("value \"").append(date).append('\"');
        }
        // exists no installer property or installer property not equal?
        final String instVal = _tclVariables.get(AdminPropertyDef.INSTALLER.name());
        if ((this.getInstaller() == null) || !this.getInstaller().equals(instVal))  {
            _paramCache.logTrace("    - define installer '" + instVal + "'");
            postMQLCode.append(" add property \"").append(AdminPropertyDef.INSTALLER.getPropName()).append("\" ")
                    .append("value \"").append(instVal).append('\"');
        }
        // is original name property defined?
        final String origNameVal = _tclVariables.get(AdminPropertyDef.ORIGINALNAME.name());
        if ((this.getOriginalName() == null) || !this.getOriginalName().equals(origNameVal))  {
            _paramCache.logTrace("    - define original name '" + origNameVal + "'");
            postMQLCode.append(" add property \"").append(AdminPropertyDef.ORIGINALNAME.getPropName()).append("\" ")
                    .append("value \"").append(origNameVal).append('\"');
        }
        // exists no application property or application property not equal?
        final String applVal = _tclVariables.get(AdminPropertyDef.APPLICATION.name());
        if ((this.getApplication() == null) || !this.getApplication().equals(applVal))  {
            _paramCache.logTrace("    - define application '" + applVal + "'");
            postMQLCode.append(" add property \"").append(AdminPropertyDef.APPLICATION.getPropName()).append("\" ")
                    .append("value \"").append(applVal).append('\"');
        }
        // exists no author property or author property not equal?
        final String authVal = _tclVariables.get(AdminPropertyDef.AUTHOR.name());
        if ((this.getAuthor() == null) || !this.getAuthor().equals(authVal))  {
            _paramCache.logTrace("    - define author '" + authVal + "'");
            postMQLCode.append(" add property \"").append(AdminPropertyDef.AUTHOR.getPropName()).append("\" ")
                    .append("value \"").append(authVal).append('\"');
        }
        postMQLCode.append(";\n");

        // check symbolic names
        final String symbName = _tclVariables.get("SYMBOLICNAME");
        if (!this.getSymblicNames().contains(symbName))  {
            _paramCache.logTrace("    - register symbolic name '" + symbName + "'");
            postMQLCode.append("add property \"").append(symbName).append("\" ")
                    .append(" on program eServiceSchemaVariableMapping.tcl to ")
                    .append(this.getTypeDef().getMxAdminName())
                    .append(" \"").append(this.getName()).append("\" ")
                    .append(this.getTypeDef().getMxAdminSuffix())
                    .append(";\n");
        }
        for (final String exSymbName : this.getSymblicNames())  {
            if (!symbName.equals(exSymbName))  {
                _paramCache.logTrace("    - remove symbolic name '" + exSymbName + "'");
                postMQLCode.append("delete property \"").append(exSymbName).append("\" ")
                        .append(" on program eServiceSchemaVariableMapping.tcl to ")
                        .append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(this.getName()).append("\" ")
                        .append(this.getTypeDef().getMxAdminSuffix())
                        .append(";\n");
            }
        }

        // prepare map of all TCL variables incl. name of admin object
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.put("NAME", this.getName());
        tclVariables.putAll(_tclVariables);

        super.update(_paramCache, preMQLCode, postMQLCode, _preTCLCode, tclVariables, _sourceFile);
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
            this.write(null, writer);
        }
        catch (final Exception e)
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

    /**
     * Sax handler used to parse the XML exports.
     */
    public class PadSaxHandler extends DefaultHandler
    {
        final Stack<String> stack = new Stack<String>();
        StringBuilder content = null;
        private boolean called = false;

        final Stack<Object> objects = new Stack<Object>();

        private String getUrl()
        {
            final StringBuilder ret = new StringBuilder();
            for (final String tag : stack.subList(2, stack.size()))  {
                ret.append('/').append(tag);
            }
            return ret.toString();
        }

        /**
         * An input source defining the entity &quot;ematrixProductDtd&quot; to
         * replace the original DTD file &quot;ematrixml.dtd&quot; which the
         * XML parser wants to open.
         */
        @Override
        public InputSource resolveEntity(final String _publicId,
                                         final String _systemId)
        {
            return new InputSource(new StringReader("<!ENTITY ematrixProductDtd \"\">"));
        }

        @Override
        public void characters(final char[] _ch,
                               final int _start,
                               final int _length)
            throws SAXException
        {

          if (_length > 0) {
            final String content = new String (_ch,_start,_length);
            if (!this.called)  {
              if (this.content == null)  {
                this.content = new StringBuilder();
              }
              this.content.append(content);
            }
          }
        }

        @Override
        public void endElement (final String uri,
                                final String localName,
                                final String qName)
                throws SAXException
        {
            if (!this.called)
            {
                evaluate();
                this.called = true;
            }
            this.stack.pop();
        }

        @Override
        public void startElement(final String _uri,
                                 final String _localName,
                                 final String _qName,
                                 final Attributes _attributes)
                throws SAXException
        {
            if (!this.called)
            {
                evaluate();
            }
            this.called = false;
            this.content = null;

            this.stack.add(_qName);
        }

        private void evaluate()
        {
            if (this.stack.size() > 2)  {
                final String tag = this.stack.get(1);
                if (!"creationProperties".equals(tag))  {
                    AbstractAdminObject_mxJPO.this.parse(getUrl(),
                                                         (this.content != null) ? this.content.toString() : null);
                }
            }
        }
    }
}