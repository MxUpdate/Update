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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * @author tmoxter
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
     * Header string of the author.
     *
     * @see #writeHeader(Writer)
     * @see #defineAuthor(Map, StringBuilder)
     */
    private static final String HEADER_AUTHOR = "\n# AUTHOR:\n# ~~~~~~~\n#";

    /**
     * Header string of the symbolic name.
     *
     * @see #writeHeader(Writer)
     * @see #defineSymbolicName(Map, StringBuilder)
     */
    private static final String HEADER_SYMBOLIC_NAME = "\n# SYMBOLIC NAME:\n# ~~~~~~~~~~~~~~\n#";

    /**
     * Length of the header string of the author.
     *
     * @see #defineAuthor(Map, StringBuilder)
     */
    private static final int LENGTH_HEADER_AUTHOR = HEADER_AUTHOR.length();

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
     * Description of the matrix administration object.
     *
     * @see #setDescription(String)
     * @see #getDescription()
     */
    private String description = "";

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


    @Override
    public void export(final Context _context,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        parse(_context, _name);
        final File file = new File(_path, getFileName());
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        write(out);
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

    protected abstract void write(final Writer _out)
            throws IOException;

    /**
     *
     * @param _out      writer instance
     * @throws IOException
     * @todo evaluate already defined symbolic names if exists
     * @see #HEADER_AUTHOR
     * @see #HEADER_SYMBOLIC_NAME
     */
    protected void writeHeader(final Writer _out)
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
            .append("#\n");
        // symbolic name only if an administration type is defined
        if (this.getTypeDef().getMxAdminName() != null)  {
            _out.append(HEADER_SYMBOLIC_NAME)
.append(' ').append(this.getTypeDef().getMxAdminName()).append("_").append(this.getName()).append("\n")
                .append("#\n");
        }
        _out.append("# DESCRIPTION:\n")
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
        _out.append(HEADER_AUTHOR);
        if ((this.author != null) && !"".equals(this.author))  {
            _out.append(" ").append(this.author).append('\n');
        } else {
            _out.append("\n");
        }
        _out.append("################################################################################\n\n");
    }


    /**
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     * {@link #update(Context, CharSequence, CharSequence, Map)}.
     *
     * @param _context          context for this request
     * @param _name             name of object to update
     * @param _file             file with TCL update code
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @see #update(Context, CharSequence, CharSequence, Map)
     * @see #defineAuthor(Map, StringBuilder)
     * @see #defineSymbolicName(Map, StringBuilder)
     */
    @Override
    public void update(final Context _context,
                       final String _name,
                       final File _file,
                       final String _newVersion)
            throws Exception
    {
        // parse objects
        this.parse(_context, _name);

        // defines the version in the TCL variables
        final Map<String,String> tclVariables = new HashMap<String,String>();
        if (_newVersion == null)  {
            tclVariables.put(AdminPropertyDef.VERSION.name(), "");
        } else  {
            tclVariables.put(AdminPropertyDef.VERSION.name(), _newVersion);
        }

        // read code
        final StringBuilder code = this.getCode(_file);

        // define author
        this.defineAuthor(tclVariables, code);

        // define symbolic name
        this.defineSymbolicName(tclVariables, code);

        // define file date
        final DateFormat format = new SimpleDateFormat(AdminPropertyDef.FILEDATE.getValue());
        tclVariables.put(AdminPropertyDef.FILEDATE.name(),
                         format.format(new Date(_file.lastModified())));

        this.update(_context, "", "", "", tclVariables, _file);
    }

    /**
     * Extracts the author from the source code. If no author in the TCL update
     * file is defined, the default value from the mapping is used. This author
     * is stored in the map of TCL variables.
     *
     * @param _tclVariables     map with TCL variables
     * @param _code             TCL update source code
     * @see #HEADER_AUTHOR
     * @see AdminPropertyDef.AUTHOR
     */
    protected void defineAuthor(final Map<String,String> _tclVariables,
                                final StringBuilder _code)
    {
        final int start = _code.indexOf(HEADER_AUTHOR) + LENGTH_HEADER_AUTHOR;
        final String author;
        if ((start > LENGTH_HEADER_AUTHOR) && (_code.charAt(start) == ' '))  {
            final int end = _code.indexOf("\n", start);
            if (end > 0)  {
                final String tmp = _code.substring(start, end).trim();
                if ("".equals(tmp))  {
                    author = AdminPropertyDef.AUTHOR.getValue();
                } else  {
                    author = tmp;
                }
            } else  {
                author = AdminPropertyDef.AUTHOR.getValue();
            }
        } else  {
            author = AdminPropertyDef.AUTHOR.getValue();
        }
        _tclVariables.put(AdminPropertyDef.AUTHOR.name(), author);
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
     * @throws Exception if update failed
     */
    protected void update(final Context _context,
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
        boolean commit = false;
        boolean transActive = _context.isTransactionActive();
        try  {
            if (!transActive)  {
                _context.start(true);
            }
            execMql(_context, cmd);
            if (!transActive)  {
                _context.commit();
            }
            commit = true;
        } finally  {
            if (!commit && !transActive && _context.isTransactionActive())  {
                _context.abort();
            }
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
                    AbstractPropertyObject_mxJPO.this.parse(getUrl(),
                                                            (this.content != null) ? this.content.toString() : null);
                }
            }
        }
    }
}