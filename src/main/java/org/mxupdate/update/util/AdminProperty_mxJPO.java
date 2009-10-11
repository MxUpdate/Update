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

package org.mxupdate.update.util;

import java.io.Serializable;

/**
 * Property with name, value and referenced administration type. The kind
 * of property (with reference, with value, ...) is stored as flag. A property
 * is only used within administration objects (not business objects).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class AdminProperty_mxJPO
        implements Serializable, Comparable<AdminProperty_mxJPO>
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = 7814222356799301361L;

    /**
     * Name of the property.
     */
    private String name = null;

    /**
     * Value of the property.
     */
    private String value = null;

    /**
     * Flag of the property.
     */
    private String flags = null;

    /**
     * Type of the referenced administration object for this property (if
     * defined).
     */
    private String refAdminType = null;

    /**
     * Name of the referenced administration object for this property (if
     * defined).
     */
    private String refAdminName = null;

    /**
     * Parses administration property related XML elements and updates this
     * property instance.
     *
     * @param _url      URL of the XML elements
     * @param _content  content within the <code>_url</code> of XML elements
     * @return <i>true</i> if the <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    public boolean parse(final String _url,
                         final String _content)
    {
        boolean parsed = true;
        if ("/adminRef".equals(_url))  {
            // to be ignored ...
        } else if ("/adminRef/adminName".equals(_url))  {
            this.refAdminName = _content;
        } else if ("/adminRef/adminType".equals(_url))  {
            this.refAdminType = _content;
        } else if ("/flags".equals(_url))  {
            this.flags = _content;
        } else if ("/name".equals(_url))  {
            this.name = _content;
        } else if ("/value".equals(_url))  {
            this.value = _content;
        } else  {
            parsed = false;
        }
        return parsed;
    }

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
     * <li>{@link #refAdminType}</li>
     * <li>{@link #refAdminName}</li>
     * <li>{@link #name}</li>
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
    public int compareTo(final AdminProperty_mxJPO _toCompare)
    {
        return ((this.refAdminType != null) && (this.refAdminName != null))
               ? ((_toCompare.refAdminType != null) && (_toCompare.refAdminName != null))
                       ? this.refAdminType.equals(_toCompare.refAdminType)
                               ? this.refAdminName.equals(_toCompare.refAdminName)
                                       ? this.name.equals(_toCompare.name)
                                               ? (this.value != null)
                                                       ? (_toCompare.value != null)
                                                               ? this.value.compareTo(_toCompare.value)
                                                               : 1
                                                       : (_toCompare.value != null)
                                                               ? -1
                                                               : 0
                                               : this.name.compareTo(_toCompare.name)
                                       : this.refAdminName.compareTo(_toCompare.refAdminName)
                               : this.refAdminType.compareTo(_toCompare.refAdminType)
                       : 1
               : ((_toCompare.refAdminType != null) && (_toCompare.refAdminName != null))
                       ? -1
                       : this.name.equals(_toCompare.name)
                               ? (this.value != null)
                                       ? (_toCompare.value != null)
                                               ? this.value.compareTo(_toCompare.value)
                                               : 1
                                       : (_toCompare.value != null)
                                               ? -1
                                               : 0
                               : this.name.compareTo(_toCompare.name);
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
