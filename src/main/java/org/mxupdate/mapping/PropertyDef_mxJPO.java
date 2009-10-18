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

package org.mxupdate.mapping;

import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Enumerator for admin properties.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public enum PropertyDef_mxJPO
{
    /** Admin property to store the name of the application. */
    APPLICATION,
    /** Admin property to store the author. */
    AUTHOR,
    /** Admin property to store the last modified date of the file. */
    FILEDATE,
    /** Admin property to store the installation date. */
    INSTALLEDDATE,
    /** Admin property to store the installer. */
    INSTALLER,
    /** Admin property to store the original name. */
    ORIGINALNAME,
    /** Admin property to store the version. */
    VERSION;

    /**
     * Used property name within the property file.
     *
     * @see #defineValue(Mapping_mxJPO, String, String)
     */
    private static final String PREFIX_PROPERTY_NAME = "PropertyName";

    /**
     * Used attribute name within the property file.
     *
     * @see #defineValue(Mapping_mxJPO, String, String)
     */
    private static final String PREFIX_ATTRIBUTE_NAME = "AttributeName";

    /**
     * Defines the values of the property definition enumerations.
     *
     * @param _mapping  cache for all mapping
     * @param _key      key with name of enumeration and (separated by a point)
     *                  the key
     * @param _value    value which must be set
     * @throws Exception if the key is not known
     */
    protected static void defineValue(final Mapping_mxJPO _mapping,
                                      final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "").toUpperCase();
        final String key = _key.substring(enumName.length() + 1);

        final PropertyDef_mxJPO propDef = PropertyDef_mxJPO.valueOf(enumName);

        if (key.equals(PropertyDef_mxJPO.PREFIX_ATTRIBUTE_NAME))  {
            _mapping.getPropertyAttributes().put(propDef, _value);
        } else if (key.equals(PropertyDef_mxJPO.PREFIX_PROPERTY_NAME))  {
            _mapping.getPropertyEnum2Names().put(propDef, _value);
            _mapping.getPropertyNames2Enum().put(_value, propDef);
        } else  {
            throw new Exception("unknown key " + _key + " with value '" + _value + "' defined!");
        }
    }

    /**
     * Returns the related admin property name used within Mx. The method
     * returns only correct values if the initialize method was called!
     *
     * @param _paramCache   for which parameter cache must the property
     *                      name returned
     * @return MX name of the property definition
     * @see Mapping_mxJPO#getPropertyEnum2Names()
     */
    public String getPropName(final ParameterCache_mxJPO _paramCache)
    {
        return _paramCache.getMapping().getPropertyEnum2Names().get(this);
    }

    /**
     * Returns the related admin property enumeration element for given
     * property name.
     *
     * @param _paramCache   for which parameter cache must the property
     *                      definition enumeration <code>_propName</code>
     *                      returned
     * @param _propName     name of property for which the admin property
     *                      enumeration is searched
     * @return related admin property enumeration element or
     *         <code>null</code> if not found
     * @see Mapping_mxJPO#getPropertyNames2Enum()
     */
    public static PropertyDef_mxJPO getEnumByPropName(final ParameterCache_mxJPO _paramCache,
                                                      final String _propName)
    {
        return _paramCache.getMapping().getPropertyNames2Enum().get(_propName);
    }

    /**
     * Returns the related attribute name used within Mx. The method
     * returns only correct values if the initialize method was called!
     *
     * @param _paramCache   for which parameter cache must the attribute
     *                      returned
     * @return MX name of the property definition
     * @see Mapping_mxJPO#getPropertyAttributes()
     */
    public String getAttrName(final ParameterCache_mxJPO _paramCache)
    {
        return _paramCache.getMapping().getPropertyAttributes().get(this);
    }
}