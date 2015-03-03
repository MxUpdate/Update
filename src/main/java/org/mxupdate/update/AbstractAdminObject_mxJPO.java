/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.update;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.datamodel.AbstractAttribute_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO.AdminProperty;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The class is used to export, create, delete and update administration
 * objects within MX.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractAdminObject_mxJPO
    extends AbstractPropertyObject_mxJPO
{
    /**
     * Key used to store the name of the program where all administration
     * objects must be registered with symbolic names. For an OOTB installation
     * the value is typically &quot;eServiceSchemaVariableMapping.tcl&quot;.
     *
     * @see #readSymbolicNames(ParameterCache_mxJPO)
     * @see #appendSymbolicNameRegistration(ParameterCache_mxJPO, String, StringBuilder)
     */
    private static final String PARAM_SYMB_NAME_PROG = "RegisterSymbolicNames";

    /**
     * Name of the parameter to suppress warnings for not parsed URLs.
     *
     * @see PadSaxHandler#evaluate()
     */
    private static final String PARAM_SUPPRESS_URL_WARNINGS = "SuppressUrlWarnings";

    /**
     * Set of all ignored URLs from the XML definition for all admin objects.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/creationInfo");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/creationInfo/datetime");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/modificationInfo");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/modificationInfo/datetime");
        // because name is defined within constructor
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/name");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/propertyList");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/agent");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/datetime");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/event");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/order");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/string");
    }

    /** Is the MX object hidden? */
    private boolean hidden = false;

    /** All current defined symbolic names for MX administration objects are stored. */
    private final Set<String> symbolicNames = new TreeSet<String>();

    /** List of all properties. */
    private final AdminPropertyList_mxJPO properties = new AdminPropertyList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractAdminObject_mxJPO(final TypeDef_mxJPO _typeDef,
                                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Returns the set of all defined symbolic names of this administration
     * (not business!) object. The method is the getter method for
     * {@link #symbolicNames}.
     *
     * @return all defined symbolic names
     * @see #symbolicNames
     */
    @Override()
    protected Set<String> getSymbolicNames()
    {
        return this.symbolicNames;
    }

    /**
     * Reads the symbolic names for current admin objects and stores them in
     * {@link #symbolicNames}.
     *
     * @param _paramCache       parameter cache
     * @throws MatrixException if the symbolic names could not be read
     * @see #symbolicNames
     */
    protected void readSymbolicNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // context must be checked if used within automatic tests
        if ((this.getTypeDef().getMxAdminName() != null) && (_paramCache.getContext() != null))  {
            final String symbProg = _paramCache.getValueString(AbstractAdminObject_mxJPO.PARAM_SYMB_NAME_PROG);
            final String symbProgIdxOf = new StringBuilder()
                    .append(" on program ").append(symbProg).append(' ').toString();
            final StringBuilder cmd = new StringBuilder()
                    .append("escape list property on program \"")
                                .append(StringUtil_mxJPO.convertMql(symbProg)).append("\" to ")
                        .append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                        .append(this.getTypeDef().getMxAdminSuffix());
            for (final String symbName : MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n"))  {
                if (!"".equals(symbName))  {
                    this.symbolicNames.add(symbName.substring(0, symbName.indexOf(symbProgIdxOf)));
                }
            }
        }
    }

    /**
     * Appends the escaped MQL code to register given symbolic name
     * <code>_symbName</code> depending on current defined symbolic names
     * {@link #symbolicNames}. If not registered, a new registration is done.
     * If wrong symbolic names are defined, they are removed.
     *
     * @param _paramCache   parameter cache
     * @param _symbName     symbolic name which must be set
     * @param _mqlCode      string builder where the MQL command must be
     *                      appended
     * @see #symbolicNames
     */
    protected void appendSymbolicNameRegistration(final ParameterCache_mxJPO _paramCache,
                                                  final String _symbName,
                                                  final StringBuilder _mqlCode)
    {
        if (this.getTypeDef().getMxAdminName() != null)  {
            final String symbProg = _paramCache.getValueString(AbstractAdminObject_mxJPO.PARAM_SYMB_NAME_PROG);
            if (!this.symbolicNames.contains(_symbName))  {
                _paramCache.logTrace("    - register symbolic name '" + _symbName + "'");
                _mqlCode.append("escape add property \"").append(StringUtil_mxJPO.convertMql(_symbName)).append("\" ")
                        .append(" on program \"").append(StringUtil_mxJPO.convertMql(symbProg)).append("\" to ")
                        .append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                        .append(this.getTypeDef().getMxAdminSuffix())
                        .append(";\n");
            }
            for (final String exSymbName : this.symbolicNames)  {
                if (!_symbName.equals(exSymbName))  {
                    _paramCache.logTrace("    - remove symbolic name '" + exSymbName + "'");
                    _mqlCode.append("escape delete property \"")
                                    .append(StringUtil_mxJPO.convertMql(exSymbName)).append("\" ")
                            .append(" on program \"").append(StringUtil_mxJPO.convertMql(symbProg)).append("\" to ")
                            .append(this.getTypeDef().getMxAdminName())
                            .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                            .append(this.getTypeDef().getMxAdminSuffix())
                            .append(";\n");
                }
            }
        }
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
    @Override()
    public Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final StringBuilder cmd = new StringBuilder()
                .append("list ")
                .append(this.getTypeDef().getMxAdminName())
                .append(" ")
                .append(this.getTypeDef().getMxAdminSuffix());
        final Set<String> ret = new TreeSet<String>();
        for (final String name : MqlUtil_mxJPO.execMql(_paramCache, cmd).split("\n"))  {
            if (!"".equals(name))  {
                ret.add(name);
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>A print on the property with a key is internally done.</p>
     */
    @Override()
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
        throws MatrixException
    {
        final String tmp = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                .append("escape print ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix())
                .append(" select property[").append(_prop.getPropName(_paramCache)).append("] dump"));
        final int length = 7 + _prop.getPropName(_paramCache).length();
        return (tmp.length() >= length)
                     ? tmp.substring(length)
                     : "";
    }

    /**
     * Creates a XML representation of the Object to export, parses them and
     * executes the post preparation {@link #prepare(ParameterCache_mxJPO)}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException  if the export of the admin object failed
     * @throws SAXException     if the exported XML document could not be
     *                          parsed
     * @throws IOException      should not happen; only if the input source of
     *                          the string reader which embeds the XML document
     *                          failed
     * @see #getExportMQL()                 used to get the MQL command to get
     *                                      a XML representation
     * @see PadSaxHandler                   SAX handler to parse the XML file
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #prepare(ParameterCache_mxJPO)
     */
    @Override()
    protected void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException
    {
        // create XML reader
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        // register Sax Content Handler
        final PadSaxHandler handler = new PadSaxHandler(_paramCache);
        reader.setContentHandler(handler);
        reader.setDTDHandler(handler);
        reader.setEntityResolver(handler);
        // parse the XML string of the export
        final InputSource inputSource = new InputSource(new StringReader(this.execXMLExport(_paramCache)));
        inputSource.setEncoding("UTF8");
        reader.parse(inputSource);
        // prepare post preparation
        this.prepare(_paramCache);
        // reads symbolic names of the administration objects
        this.readSymbolicNames(_paramCache);
    }

    /**
     * With the MQL commands from {@link #getExportMQL()} the XML export is
     * executed and the result returned. The method could be used to overwrite
     * the returned XML export; e.g. if the XML export from MX has some
     * 'problems'.
     *
     * @param _paramCache   parameter cache
     * @return string from the XML export
     * @throws MatrixException if export failed
     */
    protected String execXMLExport(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        return MqlUtil_mxJPO.execMql(_paramCache, this.getExportMQL());
    }

    /**
     * Returns the MQL export command to export the administration object as
     * XML string.
     *
     * @return string value of the MQL command to export the administration
     *         object as XML string
     */
    protected String getExportMQL()
    {
        return new StringBuilder()
                .append("escape export ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName()))
                .append("\" xml")
                .toString();
    }

    /**
     * Parsed administration object related XML tags. This includes:
     * <ul>
     * <li>description</li>
     * <li>is the object {@link #hidden}</li>
     * <li>properties</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL within the XML
     * @param _content      value of the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (AbstractAdminObject_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/adminProperties/description".equals(_url))  {
            this.setDescription(_content);
            parsed = true;
        } else if ("/adminProperties/hidden".equals(_url))  {
            this.hidden = true;
            parsed = true;

        } else if (_url.startsWith("/adminProperties/propertyList"))  {
            parsed = this.properties.parse(_paramCache, _url.substring(29), _content);
        } else  {
            parsed = false;
        }
        return parsed;
    }

    /**
     * Sorted the properties, sets the author and version depending on the
     * properties and reads the symbolic names.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the symbolic names could not be extracted
     */
    protected void prepare(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // sort the properties
        this.properties.prepare();

        // fetch all required properties
        AdminProperty author = null, appl = null, installationDate = null, installer = null, origName = null, version = null;
        for (final AdminProperty prop : this.properties)  {
            if ((prop.getRefAdminName() == null) && (prop.getRefAdminType() == null))  {
                if (PropertyDef_mxJPO.AUTHOR.equals(prop.getName()))  {
                    author = prop;
                } else if (PropertyDef_mxJPO.APPLICATION.equals(prop.getName()))  {
                    appl = prop;
                } else if (PropertyDef_mxJPO.INSTALLEDDATE.equals(prop.getName()))  {
                    installationDate = prop;
                } else if (PropertyDef_mxJPO.INSTALLER.equals(prop.getName()))  {
                    installer = prop;
                } else if (PropertyDef_mxJPO.ORIGINALNAME.equals(prop.getName()))  {
                    origName = prop;
                } else if (PropertyDef_mxJPO.VERSION.equals(prop.getName()))  {
                    version = prop;
                }
            }
        }

        // set author depending on the properties
        if (author != null)  {
            this.setAuthor(author.getValue());
        }
        // set application depending on the properties
        if (appl != null)  {
            this.setApplication(appl.getValue());
        }
        // sets the installation date depending on the properties
        if (installationDate != null)  {
            this.setInstallationDate(installationDate.getValue());
        }
        // sets the installer depending on the properties
        if (installer != null)  {
            this.setInstaller(installer.getValue());
        }
        // sets the original name depending on the properties
        if (origName != null)  {
            this.setOriginalName(origName.getValue());
        }
        // sets the version depending on the properties
        if (version != null)  {
            this.setVersion(version.getValue());
        }
    }

    /**
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException  if the write of the TCL update to the writer
     *                      instance failed
     * @throws MatrixException if an execution of a MQL command failed
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException, MatrixException
    {
        this.writeHeader(_paramCache, _out);
        _out.append("mql escape mod ")
            .append(this.getTypeDef().getMxAdminName())
            .append(" \"${NAME}\"");
        if (!"".equals(this.getTypeDef().getMxAdminSuffix()))  {
            _out.append(" ").append(this.getTypeDef().getMxAdminSuffix());
        }
        _out.append(" \\\n    description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"");
        this.writeObject(_paramCache, _out);
        this.getProperties().writeAddFormat(_paramCache, _out, this.getTypeDef());
        this.writeEnd(_paramCache, _out);
    }

    /**
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written
     */
    protected abstract void writeObject(final ParameterCache_mxJPO _paramCache,
                                        final Appendable _out)
        throws IOException;

    /**
     * At the end of the write to the TCL update file in some cases some
     * extensions must be defined. This &quot;extensions&quot; depends on the
     * use cases. E.g. for an inquiry, the inquiry code must be written at the
     * end of a TCL update file.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the extension could not be written
     */
    protected void writeEnd(final ParameterCache_mxJPO _paramCache,
                            final Appendable _out)
        throws IOException
    {
    }

    /**
     * Deletes administration object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if delete failed
     */
    @Override
    public void delete(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape delete ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix());
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * Creates given administration object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the new administration object could not be created
     */
    @Override()
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape add ").append(this.getTypeDef().getMxAdminName())
                        .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                        .append(this.getTypeDef().getMxAdminSuffix()).append(";");
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * The method overwrites the original method append MQL code to
     * <ul>
     * <li>remove all existing {@link #propertiesMap properties}</li>
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
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder();

        // remove all properties
        // (only if not attribute, because attributes uses calulated deltas)
        if (!(this instanceof AbstractAttribute_mxJPO))  {
            for (final AdminProperty prop : this.properties)  {
                // % must be ignored because this means settings
                if ((PropertyDef_mxJPO.getEnumByPropName(_paramCache, prop.getName()) == null) && !prop.isSetting())  {
                    // must be done via modify because of properties without names
                    preMQLCode.append("escape mod ").append(this.getTypeDef().getMxAdminName())
                              .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                              .append(this.getTypeDef().getMxAdminSuffix())
                              .append(" remove property \"");
                    if (prop.getName() != null)  {
                        preMQLCode.append(StringUtil_mxJPO.convertMql(prop.getName()));
                    }
                    preMQLCode.append('\"');
                    if ((prop.getRefAdminName() != null) && (prop.getRefAdminType() != null))  {
                        preMQLCode.append(" to ").append(prop.getRefAdminType())
                                  .append(" \"").append(StringUtil_mxJPO.convertMql(prop.getRefAdminName())).append('\"');
                        // if target is a table, a system is required!
                        if ("table".equals(prop.getRefAdminType()))  {
                            preMQLCode.append(" system");
                        }
                    }
                    preMQLCode.append(";\n");
                }
            }
        }

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        // define version property
        final StringBuilder postMQLCode = new StringBuilder()
                .append(_postMQLCode)
                .append("escape mod ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix())
                .append(" add property \"")
                .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.VERSION.getPropName(_paramCache)))
                .append("\" value \"")
                .append(StringUtil_mxJPO.convertMql(_tclVariables.get(PropertyDef_mxJPO.VERSION.name())))
                .append('\"');
        // define file date property
        postMQLCode.append(" add property \"")
                   .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.FILEDATE.getPropName(_paramCache)))
                   .append("\" value \"")
                   .append(StringUtil_mxJPO.convertMql(_tclVariables.get(PropertyDef_mxJPO.FILEDATE.name())))
                   .append('\"');
        // is installed date property defined?
        if ((this.getInstallationDate() == null) || "".equals(this.getInstallationDate()))  {
            final String date = StringUtil_mxJPO.formatInstalledDate(_paramCache, new Date());
            _paramCache.logTrace("    - define installed date '" + date + "'");
            postMQLCode.append(" add property \"")
                       .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.INSTALLEDDATE.getPropName(_paramCache)))
                       .append("\" value \"")
                       .append(StringUtil_mxJPO.convertMql(date))
                       .append('\"');
        }
        // exists no installer property or installer property not equal?
        final String instVal = _tclVariables.get(PropertyDef_mxJPO.INSTALLER.name());
        if ((this.getInstaller() == null) || !this.getInstaller().equals(instVal))  {
            _paramCache.logTrace("    - define installer '" + instVal + "'");
            postMQLCode.append(" add property \"")
                       .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.INSTALLER.getPropName(_paramCache)))
                       .append("\" value \"")
                       .append(StringUtil_mxJPO.convertMql(instVal))
                       .append('\"');
        }
        // is original name property defined?
        final String origNameVal = _tclVariables.get(PropertyDef_mxJPO.ORIGINALNAME.name());
        if ((this.getOriginalName() == null) || !this.getOriginalName().equals(origNameVal))  {
            _paramCache.logTrace("    - define original name '" + origNameVal + "'");
            postMQLCode.append(" add property \"")
                       .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.ORIGINALNAME.getPropName(_paramCache)))
                       .append("\" value \"")
                       .append(StringUtil_mxJPO.convertMql(origNameVal))
                       .append('\"');
        }
        // exists no application property or application property not equal?
        final String applVal = _tclVariables.get(PropertyDef_mxJPO.APPLICATION.name());
        if ((this.getApplication() == null) || !this.getApplication().equals(applVal))  {
            _paramCache.logTrace("    - define application '" + applVal + "'");
            postMQLCode.append(" add property \"")
                       .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.APPLICATION.getPropName(_paramCache)))
                       .append("\" value \"")
                       .append(StringUtil_mxJPO.convertMql(applVal))
                       .append('\"');
        }
        // exists no author property or author property not equal?
        final String authVal = _tclVariables.get(PropertyDef_mxJPO.AUTHOR.name());
        if ((this.getAuthor() == null) || !this.getAuthor().equals(authVal))  {
            _paramCache.logTrace("    - define author '" + authVal + "'");
            postMQLCode.append(" add property \"")
                       .append(StringUtil_mxJPO.convertMql(PropertyDef_mxJPO.AUTHOR.getPropName(_paramCache)))
                       .append("\" value \"")
                       .append(StringUtil_mxJPO.convertMql(authVal))
                       .append('\"');
        }
        postMQLCode.append(";\n");

        // append registration of symbolic names
        this.appendSymbolicNameRegistration(_paramCache,
                                            _tclVariables.get("SYMBOLICNAME"),
                                            postMQLCode);

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
     * Getter method for instance variable {@link #properties}.
     *
     * @return value of instance variable {@link #properties}.
     */
    protected AdminPropertyList_mxJPO getProperties()
    {
        return this.properties;
    }

    /**
     * The string representation of this administration object is returned.
     * The string representation is the MQL update script and so method
     * {@link #write(ParameterCache_mxJPO, Appendable)} is called.
     *
     * @return string representation of this administration object
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
/*    @Override
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
    }*/

    ///////////////////////////////////////////////////////////////////////////

    /**
     * SAX handler used to parse the XML exports from XML.
     */
    public class PadSaxHandler
        extends DefaultHandler
    {
        /**
         * Parameter cache for logging purposed.
         */
        private final ParameterCache_mxJPO paramCache;

        /**
         * Holds the current stack (deep) of the tags. If a start element from
         * {@link #startElement(String, String, String, Attributes)} is called
         * a new string is added. If an evaluation of an element is ended from
         * {@link #endElement(String, String, String)}, this element is removed
         * from the stack.
         *
         * @see #getUrl()
         * @see #startElement(String, String, String, Attributes)
         * @see #endElement(String, String, String)
         */
        private final Stack<String> stack = new Stack<String>();

        /**
         * Holds the string content within a XML tag. The content is build by
         * {@link #characters(char[], int, int)} and reset by
         * {@link #startElement(String, String, String, Attributes)}.
         *
         * @see #characters(char[], int, int)
         */
        private StringBuilder content = null;

        /**
         * Holds a flag if {@link #evaluate()} already called for an element.
         * If not, the value is <i>false</i>. The value is always reset if
         * an element is started in
         * {@link #startElement(String, String, String, Attributes)}.
         *
         * @see #startElement(String, String, String, Attributes)
         * @see #endElement(String, String, String)
         */
        private boolean evaluated = false;

        /**
         * Initializes the {@link #paramCache parameter cache}.
         *
         * @param _paramCache   parameter cache
         */
        PadSaxHandler(final ParameterCache_mxJPO _paramCache)
        {
            this.paramCache = _paramCache;
        }

        /**
         * An input source defining the entity &quot;ematrixProductDtd&quot; to
         * replace the original DTD file &quot;ematrixml.dtd&quot; which some
         * XML parser wants to open.
         *
         * @param _publicId     not used
         * @param _systemId     not used
         * @return input source where only the &quot;ematrixProductDtd&quot;
         *         entity is defined
         */
        @Override()
        public InputSource resolveEntity(final String _publicId,
                                         final String _systemId)
        {
            return new InputSource(new StringReader("<!ENTITY ematrixProductDtd \"\">"));
        }

        /**
         * Adds given characters to {@link #content} if the XML element was not
         * already evaluated (checked with flag {@link #evaluated}).
         *
         * @param _ch   array of characters
         * @param _start    start position within the array of characters
         * @param _length   length of characters within the array of characters
         * @see #content
         */
        @Override()
        public void characters(final char[] _ch,
                               final int _start,
                               final int _length)
        {
            if ((_length > 0) && (!this.evaluated))  {
                if (this.content == null)  {
                    this.content = new StringBuilder();
                }
                this.content.append(new String(_ch,_start,_length));
            }
        }

        /**
         * A definition of a XML element is started. If previous XML element
         * was not evaluated, previous XML element is evaluated by calling
         * {@link #evaluate()}. Current XML element is then added to the
         * {@link #stack}.
         *
         * @param _uri          URI of the XML element (not used)
         * @param _localName    local name of the XML element (not used)
         * @param _qName        current name of the XML element
         * @param _attributes   attributes of the XML element (not used)
         * @see #stack
         * @see #evaluated
         * @see #evaluate()
         */
        @Override()
        public void startElement(final String _uri,
                                 final String _localName,
                                 final String _qName,
                                 final Attributes _attributes)
        {
            if (!this.evaluated)  {
                this.evaluate();
            }
            this.evaluated = false;
            this.content = null;

            this.stack.add(_qName);
        }

        /**
         * A definition of a XML element is ended. If the XML element is not
         * already evaluated (checked with flag {@link #evaluated}), current
         * XML element is evaluated by {@link #evaluate()}. The XML element is
         * removed from {@link #stack}.
         *
         * @param _uri          URI of the XML element (not used)
         * @param _localName    local name of the XML element (not used)
         * @param _qName        current name of the XML element (not used)
         * @see #stack
         * @see #evaluated
         * @see #evaluate()
         */
        @Override()
        public void endElement(final String _uri,
                               final String _localName,
                               final String _qName)
        {
            if (!this.evaluated)  {
                this.evaluate();
                this.evaluated = true;
            }
            this.stack.pop();
        }

        /**
         * Prepares an URL from current {@link #stack}. The URL is build
         * starting with element at <code>_startIndex</code> of the
         * {@link #stack}.
         *
         * @param _startIndex       index which is used as first element in the
         *                          URL
         * @return URL made from {@link #stack}
         * @see #stack
         */
        private String getUrl(final int _startIndex)
        {
            final StringBuilder ret = new StringBuilder();
            for (final String tag : this.stack.subList(_startIndex, this.stack.size()))  {
                ret.append('/').append(tag);
            }
            return ret.toString();
        }

        /**
         * <p>Current XML element is evaluated. The path of the element defined
         * by the {@link #stack} fetched from {@link #getUrl()} is used to
         * identify the XML element. Evaluation of a XML element means to call
         * {@link AbstractAdminObject_mxJPO#parse(ParameterCache_mxJPO, String, String)}.</p>
         * <p>The parser is only called if the deep of a XML element is higher
         * than two, because this XML tags defines the administration element.
         * Deep 0 till 2 is used from MX for administration.</p>
         *
         * @see #stack
         */
        private void evaluate()
        {
            if ((this.stack.size() > 2) && !"creationProperties".equals(this.stack.get(1)))  {
                final boolean parsed = AbstractAdminObject_mxJPO.this.parse(
                        this.paramCache,
                        this.getUrl(2),
                        (this.content != null) ? this.content.toString() : null);
                if (!parsed && !this.paramCache.getValueBoolean(AbstractAdminObject_mxJPO.PARAM_SUPPRESS_URL_WARNINGS))  {
                    this.paramCache.logWarning("Url '" + this.getUrl(1) + "'"
                            + ((this.content != null) ? (" with value '" + this.content.toString().trim() + "'") : "")
                            + " unknown and not parsed!");
                }
            }
        }
    }
}
