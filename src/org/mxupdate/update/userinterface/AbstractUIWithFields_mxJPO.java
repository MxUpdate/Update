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
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
 * The class if used to handle fields for web forms and columns for web tables.
 *
 * @author Tim Moxter
 * @version $Id$
 */
abstract class AbstractUIWithFields_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -7882575497009341882L;

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
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        // -------- web table columns
        if ("/columnList".equals(_url))  {
            // to be ignored...
        } else if ("/columnList/column".equals(_url))  {
            this.fields.add(new Field());
        } else if (_url.startsWith("/columnList/column/"))  {
            this.fields.peek().parse(_url.substring(18), _content);

        // -------- web form fields
        } else if ("/fieldList".equals(_url))  {
            // to be ignored ...
        } else if ("/fieldList/field".equals(_url))  {
            this.fields.add(new Field());
        } else if (_url.startsWith("/fieldList/field/"))  {
            this.fields.peek().parse(_url.substring(16), _content);

        } else  {
            super.parse(_url, _content);
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
    class Field
    {
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
         * HRef of the range of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String rangeHref;

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
         * Sort type of the field / column.
         *
         * @see #parse(String, String)
         * @see #write(Appendable)
         */
        private String sortType;

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
         * Parses a field / column. This includes:
         * <ul>
         * <li>{@link #alt} label</li>
         * <li>HRef {@link #href}</li>
         * <li>range HRef in {@link #rangeHref}</li>
         * <li>{@link #label}</li>
         * <li>{@link #name}</li>
         * <li>{@link #sortType}</li>
         * <li>{@link #expression}</li>
         * <li>{@link #isBusinessObject} or {@link #isRelationship} expression
         *     </li>
         * <li>{@link #users} who have access to a field / column</li>
         * <li>{@link #settings}</li>
         * </ul>
         *
         * @param _url      URL to parse
         * @param _content  content of the URL to parse
         */
        void parse(final String _url,
                   final String _content)
        {
            if ("/alt".equals(_url))  {
                this.alt = _content;
            } else if ("/href".equals(_url))  {
                this.href = _content;
            } else if ("/rangeHref".equals(_url))  {
                this.rangeHref = _content;
            } else if ("/label".equals(_url))  {
                this.label = _content;
            } else if ("/name".equals(_url))  {
                this.name = _content;
            } else if ("/sortType".equals(_url))  {
                this.sortType = _content;

            } else if ("/expression".equals(_url) || "/fieldValue".equals(_url))  {
                this.expression = _content;
            } else if ("/usesBusinessObject".equals(_url))  {
                this.isBusinessObject = true;
            } else if ("/usesRelationship".equals(_url))  {
                this.isRelationship = true;

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
            }
        }

        /**
         *
         * @param _out      appendable instance to the TCL update file
         * @throws IOException if the TCL update code could not be written
         */
        public void write(final Appendable _out)
                throws IOException
        {
            _out.append(" \\\n        name \"").append(convertTcl(this.name)).append("\"")
                .append(" \\\n        label \"")
                        .append((this.label != null) ? convertTcl(this.label) : "")
                        .append("\"");
            if (this.isBusinessObject && (this.expression != null))  {
                _out.append(" \\\n        businessobject \"").append(convertTcl(this.expression)).append("\"");
            }
            if (this.isRelationship && (this.expression != null))  {
                _out.append(" \\\n        relationship \"").append(convertTcl(this.expression)).append("\"");
            }

            _out.append(" \\\n        range \"")
                        .append((this.rangeHref != null) ? convertTcl(this.rangeHref) : "")
                        .append("\"")
                .append(" \\\n        href \"").append((this.href != null) ? convertTcl(this.href) : "").append("\"")
                .append(" \\\n        alt \"").append((this.alt != null) ? convertTcl(this.alt) : "").append("\"");
            for (final String user : this.users)  {
                _out.append(" \\\n        user \"").append(convertTcl(user)).append("\"");
            }
            if ("3".equals(this.sortType))  {
                _out.append(" \\\n        sorttype other");
            }

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
                    .append(convertTcl(setting.getKey())).append("\" \"")
                    .append(convertTcl(value)).append("\"");
            }
        }

        /**
         * Returns the name of the form field / table column. The method is the
         * getter method for instance variable {@see #name}.
         *
         * @return name of field / column name
         * @see #name
         */
        public String getName()
        {
            return this.name;
        }
    }
}