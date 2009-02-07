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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.util.MqlUtil_mxJPO;


/**
 * The class is used to map from used names within the MxUpdate JPOs and the
 * internal used names within Mx.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public final class Mapping_mxJPO
{
    /**
     * Properties holding all mapping definitions.
     */
    private static final Properties PROPERTIES = new Properties();

    /**
     * Name of the Mx program where the mapping definition is stored as
     * properties.
     */
    private static final String PROP_NAME = "org.mxupdate.mapping.properties";

    /**
     * Mapping between internal used admin property definitions and the Mx
     * attribute names.
     */
    private static final Map<AdminPropertyDef,String> ADMINPROPERTY_ATTRIBUTES = new HashMap<AdminPropertyDef,String>();

    /**
     * Used prefix of attribute definitions within the property file.
     */
    private static final String PREFIX_ADMINPROPERTYATTRIBUTE = "PropertyAttribute.";

    /**
     * Mapping between internal used admin property definitions and the Mx
     * admin property names.
     *
     * @see AdminPropertyDef#getPropName()
     */
    private static final Map<AdminPropertyDef,String> ADMINPROPERTY_ENUM2NAMES = new HashMap<AdminPropertyDef,String>();

    /**
     * Mapping between MX admin property name and the inernal used admin
     * property definition.
     *
     * @see AdminPropertyDef#getEnumByPropName(String)
     */
    private static final Map<String,AdminPropertyDef> ADMINPROPERTY_NAMES2ENUM = new HashMap<String,AdminPropertyDef>();

    /**
     * Used prefix of admin property definitions within the property file.
     */
    private static final String PREFIX_ADMINPROPERTYNAME = "PropertyName.";

    /**
     * Mapping between internal used attribute definitions and the Mx attribute
     * names.
     */
    private static final Map<AttributeDef,String> ATTRIBUTES = new HashMap<AttributeDef,String>();

    /**
     * Used prefix of attribute definitions within the property file.
     */
    private static final String PREFIX_ATTRIBUTE = "Attribute.";

    /**
     * Dummy constructor so that no new instance of this final mapping class
     * could be created.
     */
    private Mapping_mxJPO()
    {
    }

    /**
     *
     * @param _context  context for this request
     * @throws MatrixException if the property program {@see #PROP_NAME} could
     *                         not be read
     * @throws IOException     if the properties could not be parsed
     */
    public static void init(final Context _context)
            throws MatrixException, IOException, Exception
    {
        Mode_mxJPO.resetValues();
        ParameterDef_mxJPO.resetValues();
        TypeDef_mxJPO.resetValues();
        TypeDefGroup_mxJPO.resetValues();
        UpdateCheck_mxJPO.resetValues();
        PROPERTIES.clear();
        ADMINPROPERTY_ENUM2NAMES.clear();
        ADMINPROPERTY_NAMES2ENUM.clear();
        ATTRIBUTES.clear();

        PROPERTIES.putAll(MqlUtil_mxJPO.readPropertyProgram(_context, PROP_NAME));

        // map attributes and types
        for (final Entry<Object, Object> entry : PROPERTIES.entrySet())  {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();
            if (key.startsWith(PREFIX_ADMINPROPERTYATTRIBUTE))  {
                ADMINPROPERTY_ATTRIBUTES.put(AdminPropertyDef.valueOf(key.substring(PREFIX_ADMINPROPERTYATTRIBUTE.length()).toUpperCase()), value);
            } else if (key.startsWith(PREFIX_ADMINPROPERTYNAME))  {
                AdminPropertyDef en = AdminPropertyDef.valueOf(key.substring(PREFIX_ADMINPROPERTYNAME.length()).toUpperCase());
                ADMINPROPERTY_ENUM2NAMES.put(en, value);
                ADMINPROPERTY_NAMES2ENUM.put(value, en);
            } else if (key.startsWith(PREFIX_ATTRIBUTE))  {
                ATTRIBUTES.put(AttributeDef.valueOf(key.substring(PREFIX_ATTRIBUTE.length())), value);
            } else if (key.startsWith("Mode."))  {
                Mode_mxJPO.defineValue(key.substring(5), value);
            } else if (key.startsWith("ParameterDef."))  {
                ParameterDef_mxJPO.defineValue(key.substring(13), value);
            } else if (key.startsWith("TypeDef."))  {
                TypeDef_mxJPO.defineValue(_context, key.substring(8), value);
            } else if (key.startsWith("TypeDefGroup."))  {
                TypeDefGroup_mxJPO.defineValue(key.substring(13), value);
            } else if (key.startsWith("UpdateCheck."))  {
                UpdateCheck_mxJPO.defineValue(key.substring(12), value);
            }
        }
    }

    /**
     * Enumerator for admin properties.
     */
    public enum AdminPropertyDef
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
         * Returns the related admin property name used within Mx. The method
         * returns only correct values if the initialize method was called!
         *
         * @return Mx name of the property definition
         * @see Mapping_mxJPO#ADMINPROPERTY_ENUM2NAMES
         */
        public String getPropName()
        {
            return Mapping_mxJPO.ADMINPROPERTY_ENUM2NAMES.get(this);
        }

        /**
         * Returns the related admin property enum element for given property
         * name.
         *
         * @param _propName name of property for which the admin property enum
         *                  is searched
         * @return related admin property enum element or <code>null</code> if
         *         not found
         * @see Mapping_mxJPO#ADMINPROPERTY_NAMES2ENUM
         */
        static public AdminPropertyDef getEnumByPropName(final String _propName)
        {
            return Mapping_mxJPO.ADMINPROPERTY_NAMES2ENUM.get(_propName);
        }

        /**
         * Returns the related attribute name used within Mx. The method
         * returns only correct values if the initialize method was called!
         *
         * @return Mx name of the property definition
         * @see Mapping_mxJPO#ADMINPROPERTY_ATTRIBUTES
         */
        public String getAttrName()
        {
            return Mapping_mxJPO.ADMINPROPERTY_ATTRIBUTES.get(this);
        }
    }

    /**
     * Enum for attribute definitions.
     */
    public enum AttributeDef
    {
        /**
         * Next number attribute of type {@link BusTypeDef#NumberGenerator}.
         */
        NumberGeneratorNextNumber;

        /**
         * Returns the related name used within Mx. The method returns only
         * correct values if the initialize method was called!
         *
         * @return Mx name of the attribute definition
         */
        public String getMxName()
        {
            return Mapping_mxJPO.ATTRIBUTES.get(this);
        }
    }
}
