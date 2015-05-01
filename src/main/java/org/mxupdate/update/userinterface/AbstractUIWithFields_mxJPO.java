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

package org.mxupdate.update.userinterface;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.userinterface.Form_mxJPO.Field;
import org.mxupdate.update.userinterface.Table_mxJPO.Column;
import org.mxupdate.update.util.CompareToUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO.UpdateLine;

/**
 * The class if used to handle fields for web forms and columns for web tables.
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public abstract class AbstractUIWithFields_mxJPO<CLASS extends AbstractAdminObject_mxJPO<CLASS>>
    extends AbstractAdminObject_mxJPO<CLASS>
{
    /** Set of all ignored URLs from the XML definition for web forms / web  tables. */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        // web table
        AbstractUIWithFields_mxJPO.IGNORED_URLS.add("/columnList");
        // web form
        AbstractUIWithFields_mxJPO.IGNORED_URLS.add("/fieldList");
    }

    /** Stores all fields / columns of this form  / table instance. */
    private final Stack<AbstractField> fields = new Stack<AbstractField>();

    /**
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractUIWithFields_mxJPO(final TypeDef_mxJPO _typeDef,
                                         final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses the fields of web forms and / or columns of web tables. To parse
     * the content of a field / column
     * {@link AbstractField#parse(ParameterCache_mxJPO, String, String)} is called.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (AbstractUIWithFields_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;

        // -------- web table columns
        } else if ("/columnList/column".equals(_url))  {
            this.fields.add(new Column());
            parsed = true;
        } else if (_url.startsWith("/columnList/column/"))  {
            parsed = this.fields.peek().parse(_paramCache, _url.substring(18), _content);

        // -------- web form fields
        } else if ("/fieldList/field".equals(_url))  {
            this.fields.add(new Field());
            parsed = true;
        } else if (_url.startsWith("/fieldList/field/"))  {
            parsed = this.fields.peek().parse(_paramCache, _url.substring(16), _content);

        } else  {
            parsed = super.parseAdminXMLExportEvent(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Returns the list of all field / columns. The method is the getter for
     * {@link #fields}.
     *
     * @return collection of all defined fields / columns
     * @see #fields
     */
    protected Stack<AbstractField> getFields()
    {
        return this.fields;
    }

    /**
     * Class used to define a column of a web table or a field of a web form.
     */
    protected abstract static class AbstractField
        implements UpdateLine, Comparable<AbstractField>
    {
        /** Set of all ignored URLs from the XML definition for columns / fields. */
        private static final Set<String> IGNORED_URLS = new HashSet<String>();
        static  {
            // ignored, because the XML export is in correct order...
            AbstractUIWithFields_mxJPO.AbstractField.IGNORED_URLS.add("/fieldOrder");
            // the geometry information is defined as sub tags
            AbstractUIWithFields_mxJPO.AbstractField.IGNORED_URLS.add("/geometry");
            AbstractUIWithFields_mxJPO.AbstractField.IGNORED_URLS.add("/fieldSettingList");
            AbstractUIWithFields_mxJPO.AbstractField.IGNORED_URLS.add("/fieldUserList");
        }

        /** Name of the field / column. */
        private String name = "";
        /** Label of the field / column.*/
        private String label = "";
        /** HRef of the field / column. */
        private String href = "";
        /** URL of the range of the field / column. */
        private String range = "";
        /** URL of the update of this field / column.*/
        private String updateURL = "";
        /** Alt label of the column / field. */
        private String alt = "";

        /** Expression of the field / column.*/
        private String expression = "";
        /** Defines how the field / column is sorted. */
        private ExpressionType expressionType = ExpressionType.SELECT;

        /** Defines how the field / column is sorted.*/
        private SortType sortType = SortType.NONE;
        /** Defines the sort program of this field / column.*/
        private String sortProgram = "";

        /** All settings of the field / column.*/
        private final Stack<Setting> settings = new Stack<Setting>();

        /** Set of all users for this field.*/
        private final SortedSet<String> users = new TreeSet<String>();

        /** Scale value for this field.*/
        private Double scale;
        /** Height of this column / field. If not specified the value is {@code 1.0}. */
        private double height = 1.0;
        /** Width of this column / field. If not specified the value is {@code 1.0}. */
        private double width = 1.0;
        /** Minimum height of this column / field.*/
        private double minHeight;
        /** Minimum width of this column / field.*/
        private double minWidth;
        /** Auto height is defined for this field.*/
        private boolean autoHeight = false;
        /** Auto width is defined for this field.*/
        private boolean autoWidth = false;

        /**
         * <p>Is the table column editable? The default value is <i>false</i>.
         * Only if this column is editable method {@link #write(Appendable)}
         * will write the edit flag!</p>
         * <p>The flag only belongs to tables. Forms does not known the flag
         * (and ignored because default value is <i>false</i>).</p>
         */
        private boolean editable = false;

        /**
         * <p>Is the table column hidden? The default value is <i>false</i>.
         * Only if this column is hidden method {@link #write(Appendable)}
         * will write the edit flag!</p>
         * <p>The flag only belongs to tables. Forms does not known the flag
         * (and ignored because default value is <i>false</i>).</p>
         */
        private boolean hidden = false;

        /** Tag used for this field.*/
        private final String tag;

        /**
         * Constructor setting the tag.
         *
         * @param _tag tag to be used
         */
        protected AbstractField(final String _tag)
        {
            this.tag = _tag;
        }

        /**
         * Parses a field / column. This includes:
         * <ul>
         * <li>{@link #alt} label</li>
         * <li>HRef {@link #href}</li>
         * <li>{@link #rangeURL range URL}</li>
         * <li>{@link #updateURL update URL}</li>
         * <li>{@link #label}</li>
         * <li>{@link #name}</li>
         * <li>{@link #sortType}</li>
         * <li>{@link #sortProgram}</li>
         * <li>{@link #expression}</li>
         * <li>{@link #isBusinessObject} or {@link #isRelationship} expression
         *     </li>
         * <li>{@link #users} who have access to a field / column</li>
         * <li>{@link #scale}</li>
         * <li>{@link #height} and {@link #width}</li>
         * <li>{@link #minHeight minimum height} and
         *     {@link #minWidth minimum width}</li>
         * <li>{@link #autoHeight auto height flag}</li>
         * <li>{@link #autoWidth auto width flag}</li>
         * <li>{@link #editable edit flag} for web tables</li>
         * <li>{@link #hidden hidden flag} for web tables</li>
         * <li>{@link #settings}</li>
         * </ul>
         *
         * @param _paramCache   parameter cache with MX context
         * @param _url          URL to parse
         * @param _content      content of the URL to parse
         * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
         *         <i>false</i>
         */
        public boolean parse(final ParameterCache_mxJPO _paramCache,
                             final String _url,
                             final String _content)
        {
            boolean parsed = true;
            if (!AbstractUIWithFields_mxJPO.AbstractField.IGNORED_URLS.contains(_url))  {
                if ("/alt".equals(_url))  {
                    this.alt = _content;
                } else if ("/derivedfield".equals(_url))  {
                    if ((_content != null) && !_content.isEmpty())  {
// TODO:
System.err.println("derived field not null! This is not supported!");
                    }

                } else if ("/href".equals(_url))  {
                    this.href = _content;
                } else if ("/rangeHref".equals(_url))  {
                    this.range = _content;
                } else if ("/updateUrl".equals(_url))  {
                    this.updateURL = _content;
                } else if ("/label".equals(_url))  {
                    this.label = _content;
                } else if ("/name".equals(_url))  {
                    this.name = _content;
                } else if ("/sortType".equals(_url))  {
                    if ("0".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.AbstractField.SortType.NONE;
                    } else if ("1".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.AbstractField.SortType.ALPHANUMERIC;
                    } else if ("2".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.AbstractField.SortType.NUMERIC;
                    } else if ("3".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.AbstractField.SortType.OTHER;
                    } else  {
// TODO: correct error message!
System.err.println("unknown sort type '" + _content + "'");
                    }
                } else if ("/sortProgram".equals(_url))  {
                    this.sortProgram = _content;

                } else if ("/expression".equals(_url) || "/fieldValue".equals(_url))  {
                    this.expression = _content;
                } else if ("/usesBusinessObject".equals(_url))  {
                    this.expressionType = ExpressionType.BUSINESSOBJECT;
                } else if ("/usesRelationship".equals(_url))  {
                    this.expressionType = ExpressionType.RELATIONSHIP;
                } else if ("/fieldType".equals(_url))  {
if (!"select".equals(_content))  {
    System.err.println("unknown field type '" + _content + "'");
}

                } else if ("/global".equals(_url))  {
                    this.users.add("all");
                } else if ("/fieldUserList/user".equals(_url))  {
                    this.users.add(_content);

                } else if ("/fieldSettingList/fieldSetting".equals(_url))  {
                    this.settings.add(new Setting());
                } else if ("/fieldSettingList/fieldSetting/fieldSettingName".equals(_url))  {
                    this.settings.peek().name = _content;
                } else if ("/fieldSettingList/fieldSetting/fieldSettingValue".equals(_url))  {
                    this.settings.peek().value = _content;

                } else if ("/scale".equals(_url))  {
                    this.scale = Double.parseDouble(_content);
                } else if ("/geometry/height".equals(_url))  {
                    this.height = Double.parseDouble(_content.replace(',', '.'));
                } else if ("/geometry/width".equals(_url))  {
                    this.width = Double.parseDouble(_content.replace(',', '.'));
                } else if ("/geometry/minHeight".equals(_url))  {
                    this.minHeight = Double.parseDouble(_content.replace(',', '.'));
                } else if ("/geometry/minWidth".equals(_url))  {
                    this.minWidth = Double.parseDouble(_content.replace(',', '.'));
                } else if ("/geometry/autoHeight".equals(_url))  {
                    this.autoHeight = true;
                } else if ("/geometry/autoWidth".equals(_url))  {
                    this.autoWidth = true;
                } else if ("/geometry/xLocation".equals(_url))  {
                    // must be parsed because of old MX versions (and numbers
                    // are exported with a comma instead of a point)
                    if (Double.parseDouble(_content.replace(',', '.')) != 0.0)  {
// TODO:
System.err.println("x location is not 0.0 and this is currently not supported");
                    }
                } else if ("/geometry/yLocation".equals(_url))  {
                    // must be parsed because of old MX versions (and numbers
                    // are exported with a comma instead of a point)
                    if (Double.parseDouble(_content.replace(',', '.')) != 0.0)  {
// TODO:
System.err.println("y location is not 0.0 and this is currently not supported");
                    }

                // only for web tables...
                } else if ("/editable".equals(_url))  {
                    this.editable = true;
                } else if ("/hidden".equals(_url))  {
                    this.hidden = true;

                } else  {
                    parsed = false;
                }
            }
            return parsed;
        }

        /**
         * Writes all column / field specific values. This includes:
         * <ul>
         * <li>{@link #alt label}</li>
         * <li>{@link #href HRef}</li>
         * <li>{@link #rangeURL range URL}</li>
         * <li>{@link #updateURL update URL}</li>
         * <li>{@link #label}</li>
         * <li>{@link #name}</li>
         * <li>{@link #sortType}</li>
         * <li>{@link #sortProgram}</li>
         * <li>{@link #expression}</li>
         * <li>{@link #isBusinessObject} or {@link #isRelationship} expression
         *     </li>
         * <li>{@link #users} who have access to a field / column</li>
         * <li>{@link #scale}</li>
         * <li>{@link #height} and {@link #width}</li>
         * <li>{@link #minHeight minimum height} and
         *     {@link #minWidth minimum width}</li>
         * <li>{@link #autoHeight auto height flag}</li>
         * <li>{@link #autoWidth auto width flag}</li>
         * <li>{@link #editable edit flag} for web tables</li>
         * <li>{@link #hidden hidden flag} for web tables</li>
         * <li>{@link #settings}</li>
         * </ul>
         *
         * @param _out      appendable instance to the TCL update file
         * @throws IOException if the TCL update code could not be written
         */
        @Deprecated()
        public void write(final Appendable _out)
            throws IOException
        {
            _out.append(" \\\n        name \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\"")
                .append(" \\\n        label \"")
                        .append((this.label != null) ? StringUtil_mxJPO.convertTcl(this.label) : "")
                        .append("\"");
            if (ExpressionType.BUSINESSOBJECT.equals(this.expressionType) && (this.expression != null))  {
                _out.append(" \\\n        businessobject \"")
                    .append(StringUtil_mxJPO.convertTcl(this.expression)).append("\"");
            }
            if (ExpressionType.RELATIONSHIP.equals(this.expressionType) && (this.expression != null))  {
                _out.append(" \\\n        relationship \"")
                    .append(StringUtil_mxJPO.convertTcl(this.expression)).append("\"");
            }

            _out.append(" \\\n        range \"")
                        .append((this.range != null) ? StringUtil_mxJPO.convertTcl(this.range) : "")
                        .append("\"");
            if (this.updateURL != null)  {
                _out.append(" \\\n        update \"").append(StringUtil_mxJPO.convertTcl(this.updateURL)).append("\"");
            }
            _out.append(" \\\n        href \"")
                        .append((this.href != null) ? StringUtil_mxJPO.convertTcl(this.href) : "").append("\"")
                .append(" \\\n        alt \"")
                        .append((this.alt != null) ? StringUtil_mxJPO.convertTcl(this.alt) : "").append("\"");
            for (final String user : this.users)  {
                _out.append(" \\\n        user \"").append(StringUtil_mxJPO.convertTcl(user)).append("\"");
            }
            if ((this.sortType != null) && (this.sortType != SortType.NONE))  {
                _out.append(" \\\n        sorttype ").append(this.sortType.mxValue);
            }
            if (this.sortProgram != null)  {
                _out.append(" \\\n        program \"").append(StringUtil_mxJPO.convertTcl(this.sortProgram)).append("\"");
            }

            // scale
            if (this.scale != null)  {
                _out.append(" \\\n        scale ");
                if (this.scale == Long.valueOf(this.scale.longValue()).doubleValue())  {
                    _out.append(String.valueOf(this.scale.longValue()));
                } else  {
                    _out.append(String.valueOf(this.scale.doubleValue()));
                }
            }
            // minimum size (minimum height and minimum width)
            if ((this.height != 1.0) || (this.width != 1.0))  {
                _out.append(" \\\n        size ").append(String.valueOf(this.width)).append(' ').append(String.valueOf(this.height));
            }
            // minimum size (minimum height and minimum width)
            if ((this.minHeight != 0.0) || (this.minWidth != 0.0))  {
                _out.append(" \\\n        minsize ").append(String.valueOf(this.minWidth)).append(' ').append(String.valueOf(this.minHeight));
            }
            // auto height
            if (this.autoHeight)  {
                _out.append(" \\\n        autoheight true");
            }
            // auto width
            if (this.autoWidth)  {
                _out.append(" \\\n        autowidth true");
            }

            // editable flag for web tables
            if (this.editable)  {
                _out.append(" \\\n        edit true");
            }

            // hidden flag for web tables
            if (this.hidden)  {
                _out.append(" \\\n        hidden");
            }

            // settings
            final Map<String,String> tmpSettings  = new TreeMap<String,String>();
            for (final Setting setting : this.settings)  {
                tmpSettings.put((setting.name == null) ? "" : setting.name,
                                (setting.value == null) ? "" : setting.value);
            }
            for (final Map.Entry<String,String> setting : tmpSettings.entrySet())  {
                final String value = (setting.getValue() == null)
                                     ? ""
                                     : setting.getValue();
                _out.append(" \\\n        setting \"")
                    .append(StringUtil_mxJPO.convertTcl(setting.getKey())).append("\" \"")
                    .append(StringUtil_mxJPO.convertTcl(value)).append("\"");
            }
        }

        @Override()
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            _updateBuilder
                .childStart(this.tag)
                //              tag             | default | value                       | write?
                .string(        "name",                     this.name)
                .string(        "label",                    this.label)
                .stringIfTrue(  "select",                   this.expression,            (ExpressionType.SELECT         == this.expressionType) && (this.expression != null) && !this.expression.isEmpty())
                .stringIfTrue(  "businessobject",           this.expression,            (ExpressionType.BUSINESSOBJECT == this.expressionType) && (this.expression != null) && !this.expression.isEmpty())
                .stringIfTrue(  "relationship",             this.expression,            (ExpressionType.RELATIONSHIP   == this.expressionType) && (this.expression != null) && !this.expression.isEmpty())
                .stringIfTrue(  "range",                    this.range,                 (this.range != null) && !this.range.isEmpty())
                .stringIfTrue(  "href",                     this.href,                  (this.href != null) && !this.href.isEmpty())
                .stringIfTrue(  "alt",                      this.alt,                   (this.alt != null) && !this.alt.isEmpty())
                .flagIfTrue(    "hidden",           false,  this.hidden,                this.hidden)
                .list(          "user",                     this.users)
                .singleIfTrue(  "sorttype",                 this.sortType.mxValue,      (this.sortType != SortType.NONE))
                .list(this.settings)
                .childEnd();
        }

        @Override()
        public int compareTo(final AbstractField _compareTo)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.name,           _compareTo.name);
            ret = CompareToUtil_mxJPO.compare(ret, this.label,          _compareTo.label);
            ret = CompareToUtil_mxJPO.compare(ret, this.expression,     _compareTo.expression);
            ret = CompareToUtil_mxJPO.compare(ret, this.expressionType, _compareTo.expressionType);
            ret = CompareToUtil_mxJPO.compare(ret, this.range,          _compareTo.range);
            ret = CompareToUtil_mxJPO.compare(ret, this.href,           _compareTo.href);
            ret = CompareToUtil_mxJPO.compare(ret, this.alt,            _compareTo.alt);
            ret = CompareToUtil_mxJPO.compare(ret, this.hidden,         _compareTo.hidden);
            ret = CompareToUtil_mxJPO.compare(ret, this.users,          _compareTo.users);
            ret = CompareToUtil_mxJPO.compare(ret, this.sortType,       _compareTo.sortType);
            ret = CompareToUtil_mxJPO.compare(ret, this.settings,       _compareTo.settings);
            return ret;
        }

        /**
         * The delta consists only in creation and
         * the MQL append commands to {@code _mql}.
         *
         * @param _mql          builder to append the MQL commands
         */
        public void calcDelta(final MultiLineMqlBuilder _mql,
                              final Integer _order)
        {
            _mql.newLine()
                .cmd(this.tag)
                .cmd(" name ").arg(this.name != null ? this.name : "")
                .cmd(" label ").arg(this.label)
                .cmd(" range ").arg(this.range)
                .cmd(" href ").arg(this.href)
                .cmd(" alt ").arg(this.alt);

            if (this.expression != null)  {
                switch (this.expressionType) {
                    case BUSINESSOBJECT:
                        _mql.cmd(" businessobject ").arg(this.expression);
                        break;
                    case RELATIONSHIP:
                        _mql.cmd(" relationship ").arg(this.expression);
                        break;
                    default:
                        // different syntax between table columns and form fields..
                        if (this instanceof Table_mxJPO.Column)  {
                            _mql.cmd(" set ").arg(this.expression);
                        } else  {
                            _mql.cmd(" select ").arg(this.expression);
                        }
                        break;
                }
            }
            if (this.hidden)  {
                _mql.cmd(" hidden");
            }
            for (final String user : this.users)  {
                _mql.cmd(" user ").arg(user);
            }
            if (this.sortType != SortType.NONE)  {
                _mql.cmd(" sorttype ").cmd(this.sortType.mxValue);
            }
            for (final Setting setting : this.settings)  {
                setting.calcDelta(_mql);
            }
            _mql.cmd(" order ").arg(String.valueOf(_order));
        }

        /**
         * Enumeration for the different sort types of a web table column.
         */
        public enum SortType  {
            /** None sorting. */
            NONE("none"),
            /** Alpha-numerical sorting. */
            ALPHANUMERIC("alpha"),
            /** Numerical sorting. */
            NUMERIC("numeric"),
            /** Other sorting. */
            OTHER("other");

            /** Related value of the sorting type within MX. */
            final String mxValue;

            /**
             * Initializes the {@link #mxValue value in MX} for the sorting
             * type.
             *
             * @param _mxValue  value in MX
             */
            private SortType(final String _mxValue)
            {
                this.mxValue = _mxValue;
            }
        }

        /**
         * Expression type of the select.
         */
        public enum ExpressionType
        {
            /** Basic case. */
            SELECT,
            /** Business object select. */
            BUSINESSOBJECT,
            /** Relationship select. */
            RELATIONSHIP;
        }
    }

    /**
     * Setting of a field / column.
     */
    public static class Setting
        implements UpdateLine, Comparable<Setting>
    {
        /** Name of the setting. */
        String name = null;
        /** Value of the setting. */
        String value = null;

        @Override()
        public void write(final UpdateBuilder_mxJPO _updateBuilder)
        {
            if (this.name != null && !this.name.isEmpty()) {
                _updateBuilder
                    .stepStartNewLine()
                    .stepSingle("setting")
                    .stepString(this.name)
                    .stepString(this.value)
                    .stepEndLine();
            }
        }

        public void calcDelta(final MultiLineMqlBuilder _mql)
        {
            _mql.cmd(" setting ").arg(this.name).cmd(" ").arg(this.value);
        }

        /**
         * {@inheritDoc}
         * The string representation includes the {@link #name} and the
         * {@link #value}.
         */
        @Override()
        public String toString()
        {
            return "[name=" + this.name + ", value=" + this.value + "]";
        }

        @Override()
        public int compareTo(final Setting _compareTo)
        {
            int ret = 0;
            ret = CompareToUtil_mxJPO.compare(ret, this.name,   _compareTo.name);
            ret = CompareToUtil_mxJPO.compare(ret, this.value,  _compareTo.value);
            return ret;
        }
    }
}
