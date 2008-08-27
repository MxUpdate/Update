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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;
import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;

/**
 * @author tmoxter
 */
public abstract class MatrixObject_mxJPO
{
    /**
     * Name of the matrix object.
     */
    private String name = null;

    String description = "";

    final String prefix;

    final String suffix;

    /**
     * Path of the matrix object.
     */
    private final String path;

    final Stack<Property> propertiesStack = new Stack<Property>();

    final Map<String,Property> propertiesMap = new TreeMap<String,Property>();

    public final static Set<String> IGNORED_PROPERTIES = new HashSet<String>();
    static  {
        MatrixObject_mxJPO.IGNORED_PROPERTIES.add("version");
        MatrixObject_mxJPO.IGNORED_PROPERTIES.add("installed date");
        MatrixObject_mxJPO.IGNORED_PROPERTIES.add("original name");
        MatrixObject_mxJPO.IGNORED_PROPERTIES.add("application");
        MatrixObject_mxJPO.IGNORED_PROPERTIES.add("installer");
        MatrixObject_mxJPO.IGNORED_PROPERTIES.add("author");
    }

    protected MatrixObject_mxJPO(final String _path,
                                 final String _prefix,
                                 final String _suffix)
    {
        this.path = _path;
        this.prefix = _prefix;
        this.suffix = _suffix;
    }

    /**
     * Returns the file name for this matrix object. The file name is a
     * concatenation of the {@link #prefix} in upper case, an underline
     * (&quot;_&quot;), the {@link #name} of the matrix object and
     *  &quot;.tcl&quot; as extension.
     *
     * @return file name of this matrix object
     * @see #name
     * @see #prefix
     */
    public String getFileName()
    {
        return this.prefix.toUpperCase() + "_" + this.name + ".tcl";
    }

    /**
     * Returns the path where the file is located of this matrix object. It is
     * the getter method for instance variable {@link #path}.
     *
     * @return value of instance variable {@link #path}.
     */
    public String getPath()
    {
        return this.path;
    }

    public Set<String> getMatchingNames(final Context _context,
                                        final Collection<String> _matches)
            throws MatrixException
    {
        final MQLCommand mql = new MQLCommand();
        final StringBuilder cmd = new StringBuilder();
        cmd.append("list ").append(this.prefix);
        if (this.suffix != null)  {
            cmd.append(" ").append(this.suffix);
        }
        mql.executeCommand(_context, cmd.toString());
        final Set<String> ret = new TreeSet<String>();
        for (final String name : mql.getResult().split("\n"))  {
            for (final String match : _matches)  {
                if (match(name, match))  {
                    ret.add(name);
                }
            }
        }
        return ret;
    }

    public void parse(final String _url,
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
            this.name = _content;
        } else if ("/adminProperties/description".equals(_url))  {
            this.description = _content;

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
     *
     * @param _context      context for this request
     * @throws MatrixException
     */
    protected void prepare(final Context _context)
            throws MatrixException
    {
        for (final Property property : this.propertiesStack)  {
            final StringBuilder key = new StringBuilder()
            .append(property.name);
            if ((property.refAdminName != null) && (property.refAdminName != null))  {
                key.append("::").append(property.refAdminType)
                   .append("::").append(property.refAdminName);
            }
            this.propertiesMap.put(key.toString(), property);
        }
    }

        public final void write(final Writer _out)
                throws IOException
        {
            writeHeader(_out);
            _out.append("mql mod ").append(this.prefix).append(" \"${NAME}\"");
            if (this.suffix != null)  {
                _out.append(" ").append(this.suffix);
            }
            _out.append(" \\\n    description \"").append(convert(this.description)).append("\"");
            writeObject(_out);
            writeProperties(_out);
            writeEnd(_out);
        }

        protected abstract void writeObject(final Writer _out) throws IOException;

        protected void writeEnd(final Writer _out)
                throws IOException
        {

        }

        void writeHeader(final Writer _out)
                throws IOException
        {
            _out.append("################################################################################\n")
                .append("# ").append(this.prefix.toUpperCase()).append(":\n")
                .append("# ~");
            for (int i = 0; i < this.prefix.length(); i++)  {
                _out.append("~");
            }
            _out.append("\n")
                .append("# ").append(this.name).append("\n")
                .append("#\n")
                .append("# SYMBOLIC NAME:\n")
                .append("# ~~~~~~~~~~~~~~\n")
.append("# ").append(this.prefix).append("_").append(this.name).append("\n")
                .append("#\n")
                .append("# DESCRIPTION:\n")
                .append("# ~~~~~~~~~~~~\n");
            if ((this.description != null) && !"".equals(this.description))  {
                _out.append('#');
                int length = 0;
                for (final String desc : this.description.split(" "))  {
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
            } else  {
                _out.append("#\n");
            }
            _out.append("#\n")
                .append("# AUTHOR:\n")
                .append("# ~~~~~~~\n");
            final Property author = this.propertiesMap.get("author");
            if ((author != null) && (author.value != null) && !"".equals(author.value))  {
                _out.append("# ").append(author.value).append('\n');
            } else {
                _out.append("#\n");
            }
            _out.append("################################################################################\n\n");
        }

    private void writeProperties(final Writer _out)
            throws IOException
    {
        for (final Property prop : this.propertiesMap.values())  {
            if (!IGNORED_PROPERTIES.contains(prop.name) && !prop.name.startsWith("%"))  {
                _out.append("\nmql add property \"").append(convert(prop.name)).append("\"")
                .append(" \\\n    on ").append(this.prefix).append(" \"${NAME}\"");
                if (this.suffix != null)  {
                    _out.append(' ').append(this.suffix);
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
     * Getter method for instance variable {@link #name}.
     *
     * @return value of instance variable {@link #name}.
     */
    protected String getName()
    {
        return this.name;
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
        mql.executeCommand(_context, "export " + this.prefix + " \"" + _name + "\" xml");
        final String xml = mql.getResult();
        // einen XML Reader erzeugen
        XMLReader reader = XMLReaderFactory.createXMLReader();
     // den eigenen Sax Content Handler registrieren
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