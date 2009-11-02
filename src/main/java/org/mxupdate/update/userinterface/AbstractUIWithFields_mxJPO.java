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

package org.mxupdate.update.userinterface;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * The class if used to handle fields for web forms and columns for web tables.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractUIWithFields_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for web forms / web
     * tables.
     *
     * @see #parse(String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        // web table
        AbstractUIWithFields_mxJPO.IGNORED_URLS.add("/columnList");
        // web form
        AbstractUIWithFields_mxJPO.IGNORED_URLS.add("/fieldList");
    }

    /**
     * Stores all fields / columns of this form  / table instance.
     *
     * @see #parse(String, String)
     * @see Field
     */
    private final Stack<Field> fields = new Stack<Field>();

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
     * the content of a field / column {@link Field#parse(String, String)} is
     * called.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override()
    protected void parse(final String _url,
                         final String _content)
    {
        if (!AbstractUIWithFields_mxJPO.IGNORED_URLS.contains(_url))  {
            // -------- web table columns
            if ("/columnList/column".equals(_url))  {
                this.fields.add(new Field());
            } else if (_url.startsWith("/columnList/column/"))  {
                this.fields.peek().parse(_url.substring(18), _content);

            // -------- web form fields
            } else if ("/fieldList/field".equals(_url))  {
                this.fields.add(new Field());
            } else if (_url.startsWith("/fieldList/field/"))  {
                this.fields.peek().parse(_url.substring(16), _content);

            } else  {
                super.parse(_url, _content);
            }
        }
    }

    /**
     * Returns the list of all field / columns. The method is the getter for
     * {@link #fields}.
     *
     * @return collection of all defined fields / columns
     * @see #fields
     */
    protected Collection<Field> getFields()
    {
        return this.fields;
    }

    /**
     * Class used to define a column of a web table or a field of a web form.
     */
    public static class Field
    {
        /**
         * Set of all ignored URLs from the XML definition for columns /
         * fields.
         *
         * @see #parse(String, String)
         */
        private static final Set<String> IGNORED_URLS = new HashSet<String>();
        static  {
            // ignored, because the XML export is in correct order...
            AbstractUIWithFields_mxJPO.Field.IGNORED_URLS.add("/fieldOrder");
            // the geometry information is defined as sub tags
            AbstractUIWithFields_mxJPO.Field.IGNORED_URLS.add("/geometry");
            AbstractUIWithFields_mxJPO.Field.IGNORED_URLS.add("/fieldSettingList");
            AbstractUIWithFields_mxJPO.Field.IGNORED_URLS.add("/fieldUserList");
        }

        /**
         * Name of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String name;

        /**
         * Label of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String label;

        /**
         * HRef of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String href;

        /**
         * URL of the range of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String rangeURL;

        /**
         * URL of the update of this field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String updateURL;

        /**
         * Alt label of the column / field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String alt;

        /**
         * Expression of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String expression;

        /**
         * If set to <i>true</i> the {@link #expression} of the field belongs
         * to business objects.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private boolean isBusinessObject = false;

        /**
         * If set to <i>true</i> the {@link #expression} of the field belongs
         * to connections.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private boolean isRelationship = false;

        /**
         * Defines how the field / column is sorted.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private SortType sortType = SortType.NONE;

        /**
         * Defines the sort program of this field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String sortProgram;

        /**
         * All settings of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private final Stack<Setting_mxJPO> settings = new Stack<Setting_mxJPO>();

        /**
         * Set of all users for this field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private final Set<String> users = new TreeSet<String>();

        /**
         * Scale value for this field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private Double scale;

        /**
         * Height of this column / field. If not specified the value is
         * <code>1.0</code>.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private double height = 1.0;

        /**
         * Width of this column / field. If not specified the value is
         * <code>1.0</code>.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private double width = 1.0;

        /**
         * Minimum height of this column / field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private double minHeight;

        /**
         * Minimum width of this column / field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private double minWidth;

        /**
         * Auto height is defined for this field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private boolean autoHeight = false;

        /**
         * Auto width is defined for this field.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private boolean autoWidth = false;

        /**
         * <p>Is the table column editable? The default value is <i>false</i>.
         * Only if this column is editable method {@link #write(Appendable)}
         * will write the edit flag!</p>
         * <p>The flag only belongs to tables. Forms does not known the flag
         * (and ignored because default value is <i>false</i>).</p>
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private boolean editable = false;

        /**
         * <p>Is the table column hidden? The default value is <i>false</i>.
         * Only if this column is hidden method {@link #write(Appendable)}
         * will write the edit flag!</p>
         * <p>The flag only belongs to tables. Forms does not known the flag
         * (and ignored because default value is <i>false</i>).</p>
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private boolean hidden = false;

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
         * @param _url      URL to parse
         * @param _content  content of the URL to parse
         */
        public void parse(final String _url,
                          final String _content)
        {
            if (!AbstractUIWithFields_mxJPO.Field.IGNORED_URLS.contains(_url))  {
                if ("/alt".equals(_url))  {
                    this.alt = _content;
                } else if ("/derivedfield".equals(_url))  {
                    if (_content != null)  {
// TODO:
System.err.println("derived field not null! This is not supported!");
                    }

                } else if ("/href".equals(_url))  {
                    this.href = _content;
                } else if ("/rangeHref".equals(_url))  {
                    this.rangeURL = _content;
                } else if ("/updateUrl".equals(_url))  {
                    this.updateURL = _content;
                } else if ("/label".equals(_url))  {
                    this.label = _content;
                } else if ("/name".equals(_url))  {
                    this.name = _content;
                } else if ("/sortType".equals(_url))  {
                    if ("0".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.Field.SortType.NONE;
                    } else if ("1".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.Field.SortType.ALPHANUMERIC;
                    } else if ("2".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.Field.SortType.NUMERIC;
                    } else if ("3".equals(_content))  {
                        this.sortType = AbstractUIWithFields_mxJPO.Field.SortType.OTHER;
                    } else  {
// TODO: correct error message!
System.err.println("unknown sort type '" + _content + "'");
                    }
                } else if ("/sortProgram".equals(_url))  {
                    this.sortProgram = _content;

                } else if ("/expression".equals(_url) || "/fieldValue".equals(_url))  {
                    this.expression = _content;
                } else if ("/usesBusinessObject".equals(_url))  {
                    this.isBusinessObject = true;
                } else if ("/usesRelationship".equals(_url))  {
                    this.isRelationship = true;
                } else if ("/fieldType".equals(_url))  {
if (!"select".equals(_content))  {
    System.err.println("unknown field type '" + _content + "'");
}

                } else if ("/global".equals(_url))  {
                    this.users.add("all");
                } else if ("/fieldUserList/user".equals(_url))  {
                    this.users.add(_content);

                } else if ("/fieldSettingList/fieldSetting".equals(_url))  {
                    this.settings.add(new Setting_mxJPO());
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
                    System.err.println("unknown column / field parsing url " + _url + "(" + _content + ")");
                }
            }
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
        public void write(final Appendable _out)
                throws IOException
        {
            _out.append(" \\\n        name \"").append(StringUtil_mxJPO.convertTcl(this.name)).append("\"")
                .append(" \\\n        label \"")
                        .append((this.label != null) ? StringUtil_mxJPO.convertTcl(this.label) : "")
                        .append("\"");
            if (this.isBusinessObject && (this.expression != null))  {
                _out.append(" \\\n        businessobject \"")
                    .append(StringUtil_mxJPO.convertTcl(this.expression)).append("\"");
            }
            if (this.isRelationship && (this.expression != null))  {
                _out.append(" \\\n        relationship \"")
                    .append(StringUtil_mxJPO.convertTcl(this.expression)).append("\"");
            }

            _out.append(" \\\n        range \"")
                        .append((this.rangeURL != null) ? StringUtil_mxJPO.convertTcl(this.rangeURL) : "")
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
            for (final Setting_mxJPO setting : this.settings)  {
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

        /**
         * Returns the name of the form field / table column. The method is the
         * getter method for instance variable {@link #name}.
         *
         * @return name of field / column name
         * @see #name
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * Enumeration for the different sort types of a web table column.
         */
        public enum SortType  {
            /**
             * None sorting.
             */
            NONE("none"),

            /**
             * Alpha-numerical sorting.
             */
            ALPHANUMERIC("alpha"),

            /**
             * Numerical sorting.
             */
            NUMERIC("numeric"),

            /**
             * Other sorting.
             */
            OTHER("other");

            /**
             * Related value of the sorting type within MX.
             */
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
    }
}
