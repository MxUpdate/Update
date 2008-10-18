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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import matrix.db.Context;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractPropertyObject_mxJPO
        extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2794355865894159489L;

    /**
     * Name of the matrix object.
     *
     * @see #setName(String)
     * @see #getName()
     */
    private String name = null;

    /**
     * Author of the matrix object.
     *
     * @see #setAuthor(String)
     * @see #getAuthor()
     */
    private String author = null;

    /**
     * Description of the matrix object.
     *
     * @see #setDescription(String)
     * @see #getDescription()
     */
    private String description = "";

    /**
     * Returns the file name for this matrix business object. The file name is
     * a concatenation of the defined file prefix within the information
     * annotation , the name of the matrix object and the file suffix within
     * the information annotation.
     *
     * @return file name of this administration (business) object
     */
    protected String getFileName()
    {
// TODO: throw exception if more than one prefix is found!
        return new StringBuilder()
                .append(getInfoAnno().filePrefix()[0])
                .append(getName())
                .append(getInfoAnno().fileSuffix())
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
        final String xml = execMql(_context, getExportMQL(_name));
        // create XML reader
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        // register Sax Content Handler
        PadSaxHandler handler = new PadSaxHandler ();
        reader.setContentHandler(handler);
        reader.setDTDHandler(handler);
        reader.setEntityResolver(handler);
        // parse the XML string of the export
        InputSource inputSource = new InputSource(new StringReader(xml));
        inputSource.setEncoding("UTF8");
        reader.parse(inputSource);
        // prepare post preparation
        prepare(_context);
    }

    protected abstract String getExportMQL(final String _name);

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
     */
    protected void writeHeader(final Writer _out)
            throws IOException
    {
        final String headerText = getInfoAnno().title();
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
        if (!"".equals(getInfoAnno().adminType()))  {
            _out.append("# SYMBOLIC NAME:\n")
                .append("# ~~~~~~~~~~~~~~\n")
.append("# ").append(getInfoAnno().adminType()).append("_").append(getName()).append("\n")
                .append("#\n");
        }
        _out.append("# DESCRIPTION:\n")
            .append("# ~~~~~~~~~~~~\n");
        if ((getDescription() != null) && !"".equals(getDescription()))  {
            for (final String partDesc : getDescription().split("\n"))  {
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
        _out.append("#\n")
            .append("# AUTHOR:\n")
            .append("# ~~~~~~~\n");
        if ((this.author != null) && !"".equals(this.author))  {
            _out.append("# ").append(this.author).append('\n');
        } else {
            _out.append("#\n");
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
     * @see #update(Context, CharSequence, CharSequence, Map)
     */
    @Override
    public void update(final Context _context,
                       final String _name,
                       final File _file)
            throws Exception
    {
        // parse objects
        this.parse(_context, _name);

        // compare file date as version against version information in Matrix
        final String modified = Long.toString(_file.lastModified() / 1000);
        if (!modified.equals(this.getVersion()))  {
System.out.println("    - update to version '" + modified + "'");

            // read TCL code
            final StringBuilder tclCode = this.getCode(_file);

            final Map<String,String> variables = new HashMap<String,String>();
            variables.put("VERSION", modified);

            this.update(_context, "", "", tclCode, variables);
        }
    }

    /**
     * The method updates this administration (business) object. First all MQL
     * commands are concatenated:
     * <ul>
     * <li>pre MQL commands (from parameter <code>_preCode</code>)</li>
     * <li>reset MQL commands (via {@link #appendResetMQL(StringBuilder)})</li>
     * <li>change to TCL mode</li>
     * <li>set all required TCL variables</li>
     * <li>append TCL update code from file</li>
     * </ul>
     * This MQL statement is executed within a transaction to be sure that the
     * statement is not executed if an error had occurred.
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
     * @throws Exception if update failed
     * @see #appendResetMQL(StringBuilder)
     */
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder().append(_preMQLCode);

        // append reset MQL commands
        appendResetMQL(_context, cmd);

        // append TCL mode
        cmd.append(";\n")
           .append("tcl;\n")
           .append("eval  {\n");

        // define all TCL variables
        for (final Map.Entry<String, String> entry : _tclVariables.entrySet())  {
            cmd.append("set ").append(entry.getKey())
               .append(" \"").append(convert(entry.getValue())).append("\"\n");
        }
        // append TCL code and post MQL statements
        cmd.append(_tclCode)
           .append("\n}\nexit;\n")
           .append(_postMQLCode);

        // execute update
        boolean commit = false;
        try  {
            _context.start(true);
            execMql(_context, cmd);
            _context.commit();
            commit = true;
        } finally  {
            if (!commit)  {
                _context.abort();
            }
        }
    }

    /**
     * Appends the MQL statements to reset the administration (business)
     * object.
     *
     * @param _context  context for this request
     * @param _cmd      string builder used to append the MQL statements
     * @todo remove usage of this method...
     */
    protected void appendResetMQL(final Context _context,
                                  final StringBuilder _cmd)
    {
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
         * An input source with a zero length string is returned, because
         * the XML parser wants to open file &quot;ematrixml.dtd&quot;.
         */
        @Override
        public InputSource resolveEntity (final String _publicId,
                                          final String _systemId)
            throws IOException, SAXException
        {
            return new InputSource(new StringReader(""));
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