/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.update.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.AdminPropertyList_mxJPO.AdminProperty;

/**
 *
 *
 * @author The MxUpdate Team
 */
public class AdminPropertyList_mxJPO
    extends TreeSet<AdminProperty>
{
    /** Serial Version UID. */
    private static final long serialVersionUID = -2115416705338825616L;

    /** Set of all ignored URLs from the XML definition for admin properties.*/
    private static final Set<String> IGNORED_URLS = new HashSet<String>(1);
    static  {
        AdminPropertyList_mxJPO.IGNORED_URLS.add("/property/adminRef");
    }

    /**
     * Stack of the properties used while parsing the XML definition of the
     * administration object. After the properties are parsed, they are stored
     * in the properties map {@link #propertiesMap} from
     * {@link #prepare(ParameterCache_mxJPO)}.
     */
    private final Stack<AdminProperty> propertiesStack = new Stack<AdminProperty>();

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
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if (AdminPropertyList_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/property".equals(_url))  {
            this.propertiesStack.add(new AdminProperty());
            parsed = true;
        } else if ("/property/adminRef/adminName".equals(_url))  {
            this.propertiesStack.peek().refAdminName = _content;
            parsed = true;
        } else if ("/property/adminRef/adminType".equals(_url))  {
            this.propertiesStack.peek().refAdminType = "att".equals(_content) ? "attribute" : _content;
            parsed = true;
        } else if ("/property/flags".equals(_url))  {
            this.propertiesStack.peek().flags = _content;
            parsed = true;
        } else if ("/property/name".equals(_url))  {
            this.propertiesStack.peek().name = _content;
            parsed = true;
        } else if ("/property/value".equals(_url))  {
            this.propertiesStack.peek().value = _content;
            parsed = true;
        } else  {
            parsed = false;
        }
        return parsed;
    }

    /**
     * Prepares the properties by sorting all properties.
     */
    public void prepare()
    {
        this.addAll(this.propertiesStack);
    }


    /**
     * Writes the MQL code to add all none standard properties to the TCL
     * update file.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @param _typeDef      type definition
     * @throws IOException if the write to the TCL update file failed
     */
    public void writeUpdateFormat(final ParameterCache_mxJPO _paramCache,
                                  final Appendable _out,
                                  final String _prefix)
        throws IOException
    {
        for (final AdminProperty prop : this)  {
            if ((PropertyDef_mxJPO.getEnumByPropName(_paramCache, prop.getName()) == null) && !prop.isSetting())  {
                _out.append(_prefix)
                    .append("property \"").append(StringUtil_mxJPO.convertTcl(prop.getName())).append("\"");
                if (((prop.getRefAdminName()) != null) && (prop.getRefAdminType() != null))  {
                    _out.append(" to ").append(prop.getRefAdminType())
                        .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getRefAdminName())).append("\"");
                }
                if (prop.getValue() != null)  {
                    _out.append(" value \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append('\"');
                }
                _out.append('\n');
            }
        }
    }

    /**
     * Writes the MQL code to add all none standard properties to the TCL
     * update file.
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @param _typeDef      type definition
     * @throws IOException if the write to the TCL update file failed
     */
    public void writeAddFormat(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out,
                               final TypeDef_mxJPO _typeDef)
        throws IOException
    {
        for (final AdminProperty prop : this)  {
            if ((PropertyDef_mxJPO.getEnumByPropName(_paramCache, prop.getName()) == null) && !prop.isSetting())  {
                _out.append("\nmql escape add property \"").append(StringUtil_mxJPO.convertTcl(prop.getName())).append("\"")
                    .append(" \\\n    on ")
                    .append(_typeDef.getMxAdminName())
                    .append(" \"${NAME}\"");
                if (!_typeDef.getMxAdminSuffix().isEmpty())  {
                    _out.append(' ').append(_typeDef.getMxAdminSuffix());
                }
                if (((prop.getRefAdminName()) != null) && (prop.getRefAdminType() != null))  {
                    _out.append("  \\\n    to ").append(prop.getRefAdminType())
                        .append(" \"").append(StringUtil_mxJPO.convertTcl(prop.getRefAdminName())).append("\"");
                    // if target is a table, a system is required!
                    if ("table".equals(prop.getRefAdminType()))  {
                        _out.append(" system");
                    }
                }
                if (prop.getValue() != null)  {
                    _out.append(" \\\n    value \"").append(StringUtil_mxJPO.convertTcl(prop.getValue())).append("\"");
                }
            }
        }
    }

    /**
     * Calculates the delta between current properties definition and this
     * properties definitions.
     *
     * @param _current  current properties
     * @param _mql      MQL builder to append the delta
     */
    public void calcDelta(final AdminPropertyList_mxJPO _currents,
                          final MqlBuilder_mxJPO _mql)
    {
        // check properties to remove
        if (_currents != null)  {
            for (final AdminProperty curProp : _currents)  {
                boolean found = false;
                for (final AdminProperty tarProp : this)  {
                    if (tarProp.compareTo(curProp) == 0)  {
                        found = true;
                        break;
                    }
                }
                if (!found)  {
                    _mql.newLine()
                        .append("remove property \"");
                    if (curProp.getName() != null)  {
                        _mql.lastLine()
                            .append(StringUtil_mxJPO.convertMql(curProp.getName()));
                    }
                    _mql.lastLine()
                        .append('\"');
                    if ((curProp.getRefAdminName() != null) && (curProp.getRefAdminType() != null))  {
                        _mql.lastLine()
                            .append(" to \"").append(StringUtil_mxJPO.convertMql(curProp.getRefAdminType())).append('\"')
                            .append(" \"").append(StringUtil_mxJPO.convertMql(curProp.getRefAdminName())).append('\"');
                        // if target is a table, a system is required!
                        if ("table".equals(curProp.getRefAdminType()))  {
                            _mql.lastLine()
                                .append(" system");
                        }
                    }
                }
            }
        }
        // check properties to add
        for (final AdminProperty tarProp : this)  {
            boolean found = false;
            if (_currents != null)  {
                for (final AdminProperty curProp : _currents)  {
                    if (tarProp.compareTo(curProp) == 0)  {
                        found = true;
                        break;
                    }
                }
            }
            if (!found)  {
                _mql.newLine()
                    .append("add property \"");
                if (tarProp.getName() != null)  {
                    _mql.lastLine()
                        .append(StringUtil_mxJPO.convertMql(tarProp.getName()));
                }
                _mql.lastLine()
                    .append('\"');
                if ((tarProp.getRefAdminName() != null) && (tarProp.getRefAdminType() != null))  {
                    _mql.lastLine()
                        .append(" to \"").append(StringUtil_mxJPO.convertMql(tarProp.getRefAdminType())).append('\"')
                        .append(" \"").append(StringUtil_mxJPO.convertMql(tarProp.getRefAdminName())).append('\"');
                    // if target is a table, a system is required!
                    if ("table".equals(tarProp.getRefAdminType()))  {
                        _mql.lastLine()
                            .append(" system");
                    }
                }
                if (tarProp.getValue() != null)  {
                    _mql.lastLine()
                        .append(" value \"").append(StringUtil_mxJPO.convertMql(tarProp.getValue())).append('\"');
                }
            }
        }
    }

    /**
     * Calculates the delta between current properties definition and this
     * properties definitions.
     *
     * @param _current  current properties
     * @param _cmd      MQL string builder to append the delta
     */
    @Deprecated()
    public void calcDelta(final AdminPropertyList_mxJPO _currents,
                          final StringBuilder _modUnitCmd,
                          final StringBuilder _cmd)
    {
        // check properties to remove
        if (_currents != null)  {
            for (final AdminProperty curProp : _currents)  {
                boolean found = false;
                for (final AdminProperty tarProp : this)  {
                    if (tarProp.compareTo(curProp) == 0)  {
                        found = true;
                        break;
                    }
                }
                if (!found)  {
                    _cmd.append(_modUnitCmd)
                        .append("remove property \"");
                    if (curProp.getName() != null)  {
                        _cmd.append(StringUtil_mxJPO.convertMql(curProp.getName()));
                    }
                    _cmd.append("\" ");
                    if ((curProp.getRefAdminName() != null) && (curProp.getRefAdminType() != null))  {
                        _cmd.append(" to \"")
                        .append(StringUtil_mxJPO.convertMql(curProp.getRefAdminType()))
                        .append("\" \"")
                        .append(StringUtil_mxJPO.convertMql(curProp.getRefAdminName()))
                        .append("\" ");
                    }
                }
            }
        }
        // check properties to add
        for (final AdminProperty tarProp : this)  {
            boolean found = false;
            if (_currents != null)  {
                for (final AdminProperty curProp : _currents)  {
                    if (tarProp.compareTo(curProp) == 0)  {
                        found = true;
                        break;
                    }
                }
            }
            if (!found)  {
                _cmd.append(_modUnitCmd)
                    .append("property \"");
                if (tarProp.getName() != null)  {
                    _cmd.append(StringUtil_mxJPO.convertMql(tarProp.getName()));
                }
                _cmd.append("\" ");
                if ((tarProp.getRefAdminName() != null) && (tarProp.getRefAdminType() != null))  {
                    _cmd.append(" to \"")
                    .append(StringUtil_mxJPO.convertMql(tarProp.getRefAdminType()))
                    .append("\" \"")
                    .append(StringUtil_mxJPO.convertMql(tarProp.getRefAdminName()))
                    .append("\" ");
                }
                if (tarProp.getValue() != null)  {
                    _cmd.append(" value \"")
                        .append(StringUtil_mxJPO.convertMql(tarProp.getValue()))
                        .append("\" ");
                }
            }
        }
    }

    @Override()
    public String toString()
    {
        return this.propertiesStack.toString();
    }

    /**
     * Property with name, value and referenced administration type. The kind
     * of property (with reference, with value, ...) is stored as flag. A property
     * is only used within administration objects (not business objects).
     */
    public static class AdminProperty
        implements Comparable<AdminProperty>
    {
        /** Name of the property. */
        private String name = null;
        /** Value of the property. */
        private String value = null;
        /** Flag of the property. */
        private String flags = null;
        /** Type of the referenced administration object for this property (if defined). */
        private String refAdminType = null;
        /** Name of the referenced administration object for this property (if defined). */
        private String refAdminName = null;

        /**
         * Checks if current property is a setting (or argument for an inquiry).
         * A property is a setting if the {@link #name} starts with '%'.
         *
         * @return <i>true</i> if the property is a setting; otherwise <i>false</i>
         */
        public boolean isSetting()
        {
            return ((this.name != null) && (this.name.charAt(0) == '%'));
        }

        /**
         * Returns the reference to related administration type.
         *
         * @return referenced administration name
         * @see #refAdminType
         */
        public String getRefAdminType()
        {
            return this.refAdminType;
        }

        /**
         * Returns the reference to related administration name.
         *
         * @return referenced administration name
         * @see #refAdminName
         */
        public String getRefAdminName()
        {
            return this.refAdminName;
        }

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

        /**
         * Getter method for instance variable {@link #flags}.
         *
         * @return value of instance variable {@link #flags}.
         */
        public String getFlags()
        {
            return this.flags;
        }

        /**
         * <p>Compares this administration property instance with
         * <code>_toCompare</code> administration property. The algorithm uses
         * <ul>
         * <li>{@link #name}</li>
         * <li>{@link #refAdminType}</li>
         * <li>{@link #refAdminName}</li>
         * <li>{@link #value}</li>
         * </ul>
         * in the defined order to compare this administration property against
         * <code>_toCompare</code>. A <code>null</code> value is always lower than
         * a non <code>null</code> value.</p>
         *
         * @param _toCompare    admin property instance which must be compared to
         * @return &quot;0&quot; if both administration properties are equal;
         *         &quot;1&quot; if greater; otherwise &quot;-1&quot;
         */
        @Override()
        public int compareTo(final AdminProperty _toCompare)
        {
            int ret = StringUtil_mxJPO.compare(this.name, _toCompare.name);
            if (ret == 0)  {
                ret = StringUtil_mxJPO.compare(this.refAdminType, _toCompare.refAdminType);
            }
            if (ret == 0)  {
                ret = StringUtil_mxJPO.compare(this.refAdminName, _toCompare.refAdminName);
            }
            if (ret == 0)  {
                ret = StringUtil_mxJPO.compare(this.value, _toCompare.value);
            }
            return ret;
        }

        /**
         * Returns the string representation of a property including the
         * {@link #name}, {@link #value} and {@link #flags}.
         *
         * @return string representation of a property
         */
        @Override()
        public String toString()
        {
            return "[name=" + this.name + ", value=" + this.value + ", flags=" + this.flags + "]";
        }
    }
}
