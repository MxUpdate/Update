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
import java.util.Collection;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class MatrixObject_mxJPO
{
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
     * Returns the file name for this matrix object.
     *
     * @return file name of this matrix object
     */
    public abstract String getFileName();

    /**
     * Returns the path where the file is located of this matrix object. The
     * method used the path annotation.
     *
     * @return path.
     */
    public String getPath()
    {
        return getClass().getAnnotation(net.sourceforge.mxupdate.update.util.Path_mxJPO.class).value();
    }

    public abstract Set<String> getMatchingNames(final Context _context,
                                                 final Collection<String> _matches)
            throws MatrixException;

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

    protected void parse(final Context _context,
            final String _name)
throws MatrixException, SAXException, IOException
{
final MQLCommand mql = new MQLCommand();
mql.executeCommand(_context, getExportMQL(_name));
final String xml = mql.getResult();
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

    protected void writeHeader(final Writer _out)
            throws IOException
    {
        final net.sourceforge.mxupdate.update.util.AdminType_mxJPO adminType
                = getClass().getAnnotation(net.sourceforge.mxupdate.update.util.AdminType_mxJPO.class);
        final net.sourceforge.mxupdate.update.util.BusType_mxJPO busType
                = getClass().getAnnotation(net.sourceforge.mxupdate.update.util.BusType_mxJPO.class);
        final String type = (adminType != null)
                            ? adminType.value()
                            : busType.filePrefix();

        _out.append("################################################################################\n")
            .append("# ").append(type.toUpperCase()).append(":\n")
            .append("# ~");
        for (int i = 0; i < type.length(); i++)  {
            _out.append("~");
        }
        _out.append("\n")
            .append("# ").append(getName()).append("\n")
            .append("#\n");
        if (adminType != null)  {
            _out.append("# SYMBOLIC NAME:\n")
                .append("# ~~~~~~~~~~~~~~\n")
.append("# ").append(type).append("_").append(getName()).append("\n")
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
         * An input source with a zero length strin is returned, because
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
                    MatrixObject_mxJPO.this.parse(getUrl(),
                                                  (this.content != null) ? this.content.toString() : null);
                }
            }
        }
    }

}