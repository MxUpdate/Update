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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
public abstract class AbstractPropertyObject_mxJPO
        extends AbstractObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2794355865894159489L;

    /**
     * String of the key within the parameter cache for the export application
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTAPPLICATION = "ExportApplication";

    /**
     * String of the key within the parameter cache for the export author
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTAUTHOR = "ExportAuthor";

    /**
     * String of the key within the parameter cache for the export installer
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTINSTALLER = "ExportInstaller";

    /**
     * String of the key within the parameter cache for the export original
     * name parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTORIGINALNAME = "ExportOriginalName";

    /**
     * String of the key within the parameter cache for the export version
     * parameter.
     *
     * @see #writeHeader(ParameterCache_mxJPO, Writer)
     */
    private static final String PARAM_EXPORTVERSION = "ExportVersion";

    /**
     * Header string of the application.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_APPLICATION = "\n# APPLICATION:\n# ~~~~~~~~~~~~\n#";

    /**
     * Header string of the author.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_AUTHOR = "\n# AUTHOR:\n# ~~~~~~~\n#";

    /**
     * Header string of the installer name.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_INSTALLER = "\n# INSTALLER:\n# ~~~~~~~~~~\n#";

    /**
     * Header string of the original name.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_ORIGINALNAME = "\n# ORIGINAL NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the symbolic name.
     *
     * @see #writeHeader(Writer)
     * @see #defineSymbolicName(Map, StringBuilder)
     */
    private static final String HEADER_SYMBOLIC_NAME = "\n# SYMBOLIC NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Header string of the version.
     *
     * @see #writeHeader(Writer)
     * @see #update(ParameterCache_mxJPO, String, File, String)
     */
    private static final String HEADER_VERSION = "\n# VERSION:\n# ~~~~~~~~\n#";

    /**
     * Length of the header string of the symbolic name.
     *
     * @see #defineSymbolicName(Map, StringBuilder)
     */
    private static final int LENGTH_HEADER_SYMBOLIC_NAME = HEADER_SYMBOLIC_NAME.length();

    /**
     * Name of the matrix administration object.
     *
     * @see #setName(String)
     * @see #getName()
     */
    private String name = null;

    /**
     * Author of the matrix administration object.
     *
     * @see #setAuthor(String)
     * @see #getAuthor()
     */
    private String author;

    /**
     * Application of the matrix administration object.
     *
     * @see #setApplication(String)
     * @see #getApplication()
     */
    private String application;

    /**
     * Description of the matrix administration object.
     *
     * @see #setDescription(String)
     * @see #getDescription()
     */
    private String description = "";

    /**
     * Installation date of the matrix administration object.
     *
     * @see #setInstallationDate(String)
     * @see #getInstallationDate()
     */
    private String installationDate;

    /**
     * Installer of the matrix administration object.
     *
     * @see #setInstaller(String)
     * @see #getInstaller()
     */
    private String installer;

    /**
     * Original name of the matrix administration object.
     *
     * @see #setOriginalName(String)
     * @see #getOriginalName()
     */
    private String originalName;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    protected AbstractPropertyObject_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        super(_typeDef);
    }

    /**
     * Returns the file name for this MxUpdate administration object. The file
     * name is a concatenation of the defined file prefix within the
     * information annotation , the name of the Mx object and the file suffix
     * within the information annotation.
     *
     * @return file name of this administration (business) object
     */
    protected String getFileName()
    {
        return new StringBuilder()
                .append(this.getTypeDef().getFilePrefix())
                .append(this.getName())
                .append(this.getTypeDef().getFileSuffix())
                .toString();
    }

    /**
     * Getter method for instance variable {@link #name}.
     *
     * @return value of instance variable {@link #name}.
     */
    protected String getName()
    {
        return this.name;
    }

    /**
     * Setter method for instance variable {@link #name}.
     *
     * @param _name new value for instance variable {@link #name}.
     */
    protected void setName(final String _name)
    {
        this.name = _name;
    }

    /**
     * Getter method for instance variable {@link #author}.
     *
     * @return value of instance variable {@link #author}.
     * @see #author
     */
    protected String getAuthor()
    {
        return this.author;
    }

    /**
     * Setter method for instance variable {@link #author}.
     *
     * @param _author new value for instance variable {@link #author}
     * @see #author
     */
    protected void setAuthor(final String _author)
    {
        this.author = _author;
    }

    /**
     * Getter method for instance variable {@link #application}.
     *
     * @return value of instance variable {@link #application}.
     * @see #application
     */
    protected String getApplication()
    {
        return this.application;
    }

    /**
     * Setter method for instance variable {@link #application}.
     *
     * @param _author new value for instance variable {@link #application}
     * @see #application
     */
    protected void setApplication(final String _application)
    {
        this.application = _application;
    }

    /**
     * Getter method for instance variable {@link #description}.
     *
     * @return value of instance variable {@link #description}.
     */
    protected String getDescription()
    {
        return this.description;
    }

    /**
     * Setter method for instance variable {@link #description}.
     *
     * @param _description new value for instance variable {@link #description}.
     */
    protected void setDescription(final String _description)
    {
        this.description = _description;
    }


    /**
     * Getter method for instance variable {@link #installationDate}.
     *
     * @return value of instance variable {@link #installationDate}.
     * @see #installationDate
     */
    protected String getInstallationDate()
    {
        return this.installationDate;
    }

    /**
     * Setter method for instance variable {@link #installationDate}.
     *
     * @param _author new value for instance variable {@link #installationDate}
     * @see #installationDate
     */
    protected void setInstallationDate(final String _installationDate)
    {
        this.installationDate = _installationDate;
    }

    /**
     * Getter method for instance variable {@link #installer}.
     *
     * @return value of instance variable {@link #installer}.
     * @see #installer
     */
    protected String getInstaller()
    {
        return this.installer;
    }

    /**
     * Setter method for instance variable {@link #installer}.
     *
     * @param _author new value for instance variable {@link #installer}
     * @see #installer
     */
    protected void setInstaller(final String _installer)
    {
        this.installer = _installer;
    }

    /**
     * Getter method for instance variable {@link #originalName}.
     *
     * @return value of instance variable {@link #originalName}.
     */
    protected String getOriginalName()
    {
        return this.originalName;
    }

    /**
     * Setter method for instance variable {@link #originalName}.
     *
     * @param _originalName     new value for instance variable
     *                          {@link #originalName}.
     */
    protected void setOriginalName(final String _originalName)
    {
        this.originalName = _originalName;
    }

    /**
     *
     */
    @Override
    public void export(final ParameterCache_mxJPO _paramCache,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        this.parse(_paramCache.getContext(), _name);
        final File file = new File(_path, getFileName());
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        this.write(_paramCache, out);
        out.flush();
        out.close();
    }

    /**
     * Creates a XML representation of the Object to export, parses them and
     * executes the post preparation {@link #prepare(Context)}.
     *
     * @param _context  context for this request
     * @param _name     name of object to parse
     * @see #getExportMQL(String)       used to get the MQL command to get a
     *                                  XML representation
     * @see PadSaxHandler               SAX handler to parse the XML file
     * @see #parse(String, String)      parser called within the SAX handler
     * @see #prepare(Context)           called post preparation method
     */
    protected void parse(final Context _context,
                         final String _name)
            throws MatrixException, SAXException, IOException
    {
        this.setName(_name);
        final String xml = execMql(_context, this.getExportMQL());

        // create XML reader
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        // register Sax Content Handler
        final PadSaxHandler handler = new PadSaxHandler();
        reader.setContentHandler(handler);
        reader.setDTDHandler(handler);
        reader.setEntityResolver(handler);
        // parse the XML string of the export
        InputSource inputSource = new InputSource(new StringReader(xml));
        inputSource.setEncoding("UTF8");
        reader.parse(inputSource);
        // prepare post preparation
        this.prepare(_context);
    }

    protected abstract String getExportMQL();

    protected void parse(final String _url,
                         final String _content)
    {
        System.err.println("unkown parsing url: "+_url+"("+_content+")");
    }

    protected abstract void prepare(final Context _context)
            throws MatrixException;

    protected abstract void write(final ParameterCache_mxJPO _paramCache,
                                  final Writer _out)
            throws IOException;

    /**
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException
     * @todo evaluate already defined symbolic names if exists
     * @see #HEADER_APPLICATION
     * @see #HEADER_AUTHOR
     * @see #HEADER_INSTALLER
     * @see #HEADER_ORIGINALNAME
     * @see #HEADER_SYMBOLIC_NAME
     * @see #HEADER_VERSION
     * @see #PARAM_EXPORTAPPLICATION
     * @see #PARAM_EXPORTAUTHOR
     * @see #PARAM_EXPORTINSTALLER
     * @see #PARAM_EXPORTORIGINALNAME
     * @see #PARAM_EXPORTVERSION
     */
    protected void writeHeader(final ParameterCache_mxJPO _paramCache,
                               final Writer _out)
            throws IOException
    {
        final String headerText = this.getTypeDef().getTitle();
        _out.append("################################################################################\n")
            .append("# ").append(headerText).append(":\n")
            .append("# ~");
        for (int i = 0; i < headerText.length(); i++)  {
            _out.append("~");
        }
        _out.append("\n")
            .append("# ").append(getName()).append("\n")
            .append("#");
        // symbolic name only if an administration type is defined
        if (this.getTypeDef().getMxAdminName() != null)  {
            _out.append(HEADER_SYMBOLIC_NAME)
// TODO: use stored symbolic name!
.append(' ').append(this.getTypeDef().getMxAdminName()).append("_").append(this.getName()).append("\n");
        }
        // original name
        // (only if an administration type and related parameter is defined)
        if ((this.getTypeDef().getMxAdminName() != null) && _paramCache.getValueBoolean(PARAM_EXPORTORIGINALNAME))  {
            _out.append('#').append(HEADER_ORIGINALNAME);
            if ((this.getOriginalName() != null) && !"".equals(this.getOriginalName()))  {
                _out.append(" ").append(this.getOriginalName()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // description
        _out.append("#\n# DESCRIPTION:\n")
            .append("# ~~~~~~~~~~~~\n");
        if ((this.getDescription() != null) && !"".equals(this.getDescription()))  {
            for (final String partDesc : this.getDescription().split("\n"))  {
                _out.append('#');
                int length = 0;
                for (final String desc : partDesc.split(" "))  {
                    if (!"".equals(desc))  {
                        length += desc.length() + 1;
                        if (length > 79)  {
                            _out.append("\n#");
                            length = desc.length() + 1;
                        }
                        _out.append(' ').append(desc);
                    }
                }
                _out.append("\n");
            }
        } else  {
            _out.append("#\n");
        }
        // write header
        if (_paramCache.getValueBoolean(PARAM_EXPORTAUTHOR))  {
            _out.append('#').append(HEADER_AUTHOR);
            if ((this.getAuthor() != null) && !"".equals(this.getAuthor()))  {
                _out.append(" ").append(this.getAuthor()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write installer
        if (_paramCache.getValueBoolean(PARAM_EXPORTINSTALLER))  {
            _out.append('#').append(HEADER_INSTALLER);
            if ((this.getInstaller() != null) && !"".equals(this.getInstaller()))  {
                _out.append(" ").append(this.getInstaller()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write application
        if (_paramCache.getValueBoolean(PARAM_EXPORTAPPLICATION))  {
            _out.append('#').append(HEADER_APPLICATION);
            if ((this.getApplication() != null) && !"".equals(this.getApplication()))  {
                _out.append(" ").append(this.getApplication()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        // write version
        if (_paramCache.getValueBoolean(PARAM_EXPORTVERSION))  {
            _out.append('#').append(HEADER_VERSION);
            if ((this.getVersion() != null) && !"".equals(this.getVersion()))  {
                _out.append(" ").append(this.getVersion()).append('\n');
            } else {
                _out.append("\n");
            }
        }
        _out.append("################################################################################\n\n");
    }


    /**
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     * {@link #update(Context, CharSequence, CharSequence, Map)}.
     *
     * @param _paramCache       parameter cache
     * @param _name             name of object to update
     * @param _file             file with TCL update code
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @see #update(Context, CharSequence, CharSequence, Map)
     * @see #extractFromCode(StringBuilder, String, String)
     * @see #defineSymbolicName(Map, StringBuilder)
     * @see #HEADER_APPLICATION
     * @see #HEADER_AUTHOR
     * @see #HEADER_INSTALLER
     * @see #HEADER_ORIGINALNAME
     * @see #HEADER_VERSION
     */
    @Override
    public void update(final ParameterCache_mxJPO _paramCache,
                       final String _name,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        // parse objects
        this.parse(_paramCache.getContext(), _name);

        // read code
        final StringBuilder code = this.getCode(_file);

        // defines the version in the TCL variables
        final Map<String,String> tclVariables = new HashMap<String,String>();
        if (_newVersion == null)  {
            tclVariables.put(AdminPropertyDef.VERSION.name(),
                              this.extractFromCode(code,
                                                   HEADER_VERSION,
                                                   ""));
        } else  {
            tclVariables.put(AdminPropertyDef.VERSION.name(), _newVersion);
        }

        // define author
        final String author;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_AUTHOR))  {
            author = _paramCache.getValueString(ParameterCache_mxJPO.KEY_AUTHOR);
        } else  {
            author = this.extractFromCode(code,
                                          HEADER_AUTHOR,
                                          _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAUTHOR));
        }
        tclVariables.put(AdminPropertyDef.AUTHOR.name(), author);

        // define application
        final String appl;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_APPLICATION))  {
            appl = _paramCache.getValueString(ParameterCache_mxJPO.KEY_APPLICATION);
        } else  {
            appl = this.extractFromCode(code,
                                        HEADER_APPLICATION,
                                        _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAPPLICATION));
        }
        tclVariables.put(AdminPropertyDef.APPLICATION.name(), appl);

        // define installer
        final String installer;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_INSTALLER))  {
            installer = _paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLER);
        } else  {
            installer = this.extractFromCode(code,
                                             HEADER_INSTALLER,
                                             _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTINSTALLER));
        }
        tclVariables.put(AdminPropertyDef.INSTALLER.name(), installer);

        if (this.getTypeDef().getMxAdminName() != null)  {
            // define symbolic name
            this.defineSymbolicName(tclVariables, code);

            // define original name
            final String origName;
            if ((this.getOriginalName() != null) && !"".equals(this.getOriginalName()))  {
                origName = this.getOriginalName();
            } else  {
                origName = _name;
            }
            tclVariables.put(AdminPropertyDef.ORIGINALNAME.name(), origName);
        }

        // define file date
        final DateFormat format = new SimpleDateFormat(_paramCache.getValueString(ParameterCache_mxJPO.KEY_FILEDATEFORMAT));
        tclVariables.put(AdminPropertyDef.FILEDATE.name(),
                         format.format(new Date(_file.lastModified())));

        this.update(_paramCache, "", "", "", tclVariables, _file);
    }

    /**
     * Extracts the author from the source code. If no author in the TCL update
     * file is defined, the default value from the mapping is used. This author
     * is stored in the map of TCL variables.
     *
     * @param _paramCache       parameter cache
     * @param _tclVariables     map with TCL variables
     * @param _code             TCL update source code
     * @see #HEADER_AUTHOR
     * @see AdminPropertyDef.AUTHOR
     */
    protected String extractFromCode(final StringBuilder _code,
                                     final String _headerText,
                                     final String _default)
    {
        final int length = _headerText.length();
        final int start = _code.indexOf(_headerText) + length;
        final String value;
        if ((start > length) && (_code.charAt(start) == ' '))  {
            final int end = _code.indexOf("\n", start);
            if (end > 0)  {
                final String tmp = _code.substring(start, end).trim();
                if ("".equals(tmp))  {
                    value = _default;
                } else  {
                    value = tmp;
                }
            } else  {
                value = _default;
            }
        } else  {
            value = _default;
        }
        return value;
    }

    /**
     *
     * @param _tclVariables     map with TCL variables
     * @param _code             TCL update source code
     */
    protected void defineSymbolicName(final Map<String,String> _tclVariables,
                                      final StringBuilder _code)
    {
        if (this.getTypeDef().getMxAdminName() != null)  {
            final int start = _code.indexOf(HEADER_SYMBOLIC_NAME) + LENGTH_HEADER_SYMBOLIC_NAME;
            String symbName = new StringBuilder().append(this.getTypeDef().getMxAdminName())
                                                 .append("_").append(this.getName().replaceAll(" ", ""))
                                                 .toString();
            if ((start > LENGTH_HEADER_SYMBOLIC_NAME) && (_code.charAt(start) == ' '))  {
                final int end = _code.indexOf("\n", start);
                if (end > 0)  {
                    final String tmp = _code.substring(start, end).trim();
                    if (!"".equals(tmp))  {
                        if (tmp.startsWith(this.getTypeDef().getMxAdminName()))  {
                            symbName = tmp;
                        } else  {
System.out.println("ERROR! Symbolic name does not start correctly! So '" + symbName + "' will be used (instead of '" + tmp + "')");
                        }
                    }
                }
            }
            _tclVariables.put("SYMBOLICNAME", symbName);
        }
    }

    /**
     * The method updates this administration (business) object. First all MQL
     * commands are concatenated:
     * <ul>
     * <li>pre MQL commands (from parameter <code>_preMQLCode</code>)</li>
     * <li>change to TCL mode</li>
     * <li>set all required TCL variables (from parameter <code>_tclVariables
     *     </code>)</li>
     * <li>append TCL update code from file (from parameter <code>_tclCode
     *     </code>)</li>
     * <li>change back to MQL mode</li>
     * <li>append post MQL statements (from parameter <code>_postMQLCode
     *     </code>)</li>
     * </ul>
     * This MQL statement is executed within a transaction to be sure that the
     * statement is not executed if an error had occurred.
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
     * @throws Exception if update failed
     */
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder().append(_preMQLCode);

        // append TCL mode
        cmd.append("tcl;\n")
           .append("eval  {\n");

        // define all TCL variables
        for (final Map.Entry<String, String> entry : _tclVariables.entrySet())  {
            cmd.append("set ").append(entry.getKey())
               .append(" \"").append(convertTcl(entry.getValue())).append("\"\n");
        }
        // append TCL code, end of TCL mode and post MQL statements
        // (source with the file must be replace for windows ...)
        cmd.append(_preTCLCode)
           .append("\nsource \"").append(_sourceFile.toString().replaceAll("\\\\", "/")).append("\"")
           .append("\n}\nexit;\n")
           .append(_postMQLCode);

        // execute update
        execMql(_paramCache.getContext(), cmd);
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
                    AbstractPropertyObject_mxJPO.this.parse(getUrl(),
                                                            (this.content != null) ? this.content.toString() : null);
                }
            }
        }
    }
}