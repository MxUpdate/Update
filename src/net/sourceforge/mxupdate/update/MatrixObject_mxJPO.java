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

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

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
                                        final String _match)
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
            if (match(name, _match))  {
                ret.add(name);
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
            } else if ("/adminProperties/propertyList/property/name".equals(_url))  {
                this.propertiesStack.peek().name = _content;
            } else if ("/adminProperties/propertyList/property/value".equals(_url))  {
                this.propertiesStack.peek().value = _content;
            } else if ("/adminProperties/propertyList/property/flags".equals(_url))  {
                this.propertiesStack.peek().flags = _content;
            } else  {
System.out.println(""+_url+"("+_content+")");
            }
        }


        public void prepare(final Context _context)
                throws MatrixException
        {
            for (final Property property : this.propertiesStack)  {
                propertiesMap.put(property.name, property);
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
            _out.append(" \\\n    description \"").append(net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert(this.description)).append("\"");
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
                _out.append("\nmql add property \"").append(net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert(prop.name)).append("\"")
                .append(" \\\n    on ").append(this.prefix).append(" \"${NAME}\"");
                if (this.suffix != null)  {
                    _out.append(' ').append(this.suffix);
                }
                if (prop.value != null)  {
                    _out.append("  \\\n    value \"").append(net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert(prop.value)).append("\"");
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