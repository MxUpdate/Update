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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * The class is used to map from used names within the MxUpdate JPOs and the
 * internal used names within MX.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Mapping_mxJPO
{
    /**
     * Name of the Mx program where the mapping definition is stored as
     * properties.
     */
    private static final String PROP_NAME = "org.mxupdate.mapping.properties";

    /**
     * Used prefix of attribute definitions within the property file.
     */
    private static final String PREFIX_ADMINPROPERTYATTRIBUTE = "PropertyAttribute.";

    /**
     * Used prefix of admin property definitions within the property file.
     */
    private static final String PREFIX_ADMINPROPERTYNAME = "PropertyName.";

    /**
     * Used prefix of attribute definitions within the property file.
     */
    private static final String PREFIX_ATTRIBUTE = "Attribute.";

    /**
     * Properties holding all mapping definitions.
     */
    private final Properties properties = new Properties();

    /**
     * Mapping between internal used admin property definitions and the MX
     * attribute names.
     */
    private final Map<AdminPropertyDef,String> adminPropertyAttributes = new HashMap<AdminPropertyDef,String>();

    /**
     * Mapping between internal used admin property definitions and the MX
     * admin property names.
     *
     * @see AdminPropertyDef#getPropName()
     */
    private final Map<AdminPropertyDef,String> adminPropertyEnum2Names = new HashMap<AdminPropertyDef,String>();

    /**
     * Mapping between MX admin property name and the internal used admin
     * property definition.
     *
     * @see AdminPropertyDef#getEnumByPropName(String)
     */
    private final Map<String,AdminPropertyDef> adminPropertyNames2Enum = new HashMap<String,AdminPropertyDef>();

    /**
     * Mapping between internal used attribute definitions and the MX attribute
     * names.
     */
    private final Map<AttributeDef,String> attributeMap = new HashMap<AttributeDef,String>();

    /**
     * Maps from the name of the parameter to the parameter instance.
     *
     * @see ParameterDef_mxJPO#defineValue(Mapping_mxJPO, String, String)
     * @see #getAllParameterDefs()
     * @see #getParameterDef(String)
     * @see #getParameterDefMap()
     */
    private final Map<String,ParameterDef_mxJPO> parameterDefMap = new HashMap<String,ParameterDef_mxJPO>();

    /**
     * Maps from the name of the type definition group to the related type
     * definition group instance.
     *
     * @see TypeDef_mxJPO#defineValue(Context, Mapping_mxJPO, String, String)
     * @see #getTypeDef(String)
     * @see #getAllTypeDefs()
     * @see #getTypeDefMap()
     */
    private final Map<String,TypeDef_mxJPO> typeDefMap = new HashMap<String,TypeDef_mxJPO>();

    /**
     * Map between the JPO name and the class name used within MX for type
     * definition.
     *
     * @see TypeDef_mxJPO#defineValue(Context, Mapping_mxJPO, String, String)
     * @see TypeDef_mxJPO#defineJPOClass(Context, Mapping_mxJPO, String)
     * @see #getTypeDefJPOsMap()
     */
    private final Map<String,String> typeDefJPOsMap = new HashMap<String,String>();

    /**
     * Maps from the name of the type definition group to the related type
     * definition group instance.
     *
     * @see TypeDefGroup_mxJPO#defineValue(Mapping_mxJPO, String, String)
     * @see #getTypeDefGroup(String)
     * @see #getAllTypeDefGroups()
     * @see #getTypeDefGroupMap()
     */
    private final Map<String,TypeDefGroup_mxJPO> typeDefGroupMap = new HashMap<String,TypeDefGroup_mxJPO>();

    /**
     *
     * @param _context  context for this request
     * @throws MatrixException if the property program {@see #PROP_NAME} could
     *                         not be read
     * @throws IOException     if the properties could not be parsed
     */
    public Mapping_mxJPO(final Context _context)
            throws MatrixException, IOException, Exception
    {
        Mode_mxJPO.resetValues();
        UpdateCheck_mxJPO.resetValues();

        this.properties.putAll(MqlUtil_mxJPO.readPropertyProgram(_context, Mapping_mxJPO.PROP_NAME));

        // map attributes and types
        for (final Entry<Object, Object> entry : this.properties.entrySet())  {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();
            if (key.startsWith(Mapping_mxJPO.PREFIX_ADMINPROPERTYATTRIBUTE))  {
                this.adminPropertyAttributes.put(AdminPropertyDef.valueOf(key
                                                       .substring(Mapping_mxJPO.PREFIX_ADMINPROPERTYATTRIBUTE.length())
                                                       .toUpperCase()),
                                                 value);
            } else if (key.startsWith(Mapping_mxJPO.PREFIX_ADMINPROPERTYNAME))  {
                final AdminPropertyDef en
                        = AdminPropertyDef.valueOf(key.substring(Mapping_mxJPO.PREFIX_ADMINPROPERTYNAME.length())
                                                      .toUpperCase());
                this.adminPropertyEnum2Names.put(en, value);
                this.adminPropertyNames2Enum.put(value, en);
            } else if (key.startsWith(Mapping_mxJPO.PREFIX_ATTRIBUTE))  {
                final AttributeDef attr = AttributeDef.valueOf(key.substring(Mapping_mxJPO.PREFIX_ATTRIBUTE.length())
                                                                  .toUpperCase());
                this.attributeMap.put(attr, value);
            } else if (key.startsWith("Mode."))  {
                Mode_mxJPO.defineValue(key.substring(5), value);
            } else if (key.startsWith("ParameterDef."))  {
                ParameterDef_mxJPO.defineValue(this, key.substring(13), value);
            } else if (key.startsWith("TypeDef."))  {
                TypeDef_mxJPO.defineValue(_context, this, key.substring(8), value);
            } else if (key.startsWith("TypeDefGroup."))  {
                TypeDefGroup_mxJPO.defineValue(this, key.substring(13), value);
            } else if (key.startsWith("UpdateCheck."))  {
                UpdateCheck_mxJPO.defineValue(key.substring(12), value);
            }
        }
    }

    /**
     * Returns for given name the related parameter instance.
     *
     * @param _name name of the searched parameter instance
     * @return found parameter instance (or <code>null</code> if not found)
     * @see #parameterDefMap
     */
    public ParameterDef_mxJPO getParameterDef(final String _name)
    {
        return this.parameterDefMap.get(_name);
    }

    /**
     * Returns the list of all parameter instances.
     *
     * @return list of all parameter instances
     * see #MAP
     */
    public Collection<ParameterDef_mxJPO> getAllParameterDefs()
    {
        return this.parameterDefMap.values();
    }

    /**
     * Returns the mapping of parameter names and the related parameter
     * definition instance.
     *
     * @return mapping between type name and the related parameter definition
     *         instance
     * @see #parameterDefMap
     */
    protected Map<String,ParameterDef_mxJPO> getParameterDefJPOsMap()
    {
        return this.parameterDefMap;
    }

    /**
     * Returns for given name the related type definition instance.
     *
     * @param _name name of the searched type definition instance
     * @return found type definition instance (or <code>null</code> if not
     *         found)
     * @see #typeDefMap
     */
    public TypeDef_mxJPO getTypeDef(final String _name)
    {
        return this.typeDefMap.get(_name);
    }

    /**
     * Returns the list of all type definition instances.
     *
     * @return list of all type definition instances
     * @see #typeDefMap
     */
    public Collection<TypeDef_mxJPO> getAllTypeDefs()
    {
        return this.typeDefMap.values();
    }

    /**
     * Returns the mapping between the type definition name and the related
     * type definition instance.
     *
     * @return mapping between type definition name and the related type
     *         definition instance
     * @see #typeDefMap
     */
    protected Map<String,TypeDef_mxJPO> getTypeDefMap()
    {
        return this.typeDefMap;
    }

    /**
     * Returns the mapping of JPOs between names used within MX and the related
     * Java class name for type definition classes.
     *
     * @return mapping between type name and the related type definition
     *         instance
     * @see #typeDefJPOsMap
     */
    protected Map<String,String> getTypeDefJPOsMap()
    {
        return this.typeDefJPOsMap;
    }

    /**
     * Returns for given name the related type definition group instance.
     *
     * @param _name name of the searched type definition group instance
     * @return found type definition group instance (or <code>null</code> if
     *         not found)
     * @see #typeDefGroupMap
     */
    public TypeDefGroup_mxJPO getTypeDefGroup(final String _name)
    {
        return this.typeDefGroupMap.get(_name);
    }

    /**
     * Returns the list of all type definition group instances.
     *
     * @return list of all type definition group instances
     * @see #typeDefGroupMap
     */
    public Collection<TypeDefGroup_mxJPO> getAllTypeDefGroups()
    {
        return this.typeDefGroupMap.values();
    }

    /**
     * Returns the mapping between the type definition group name and the
     * related type definition group instance.
     *
     * @return mapping between type definition group name and the related type
     *         definition group instance
     * @see #typeDefGroupMap
     */
    protected Map<String,TypeDefGroup_mxJPO> getTypeDefGroupMap()
    {
        return this.typeDefGroupMap;
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
         * @param _paramCache   for which parameter cache must the property
         *                      name returned
         * @return MX name of the property definition
         * @see Mapping_mxJPO#adminPropertyEnum2Names
         */
        public String getPropName(final ParameterCache_mxJPO _paramCache)
        {
            return _paramCache.getMapping().adminPropertyEnum2Names.get(this);
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
         * @see Mapping_mxJPO#adminPropertyNames2Enum
         */
        public static AdminPropertyDef getEnumByPropName(final ParameterCache_mxJPO _paramCache,
                                                         final String _propName)
        {
            return _paramCache.getMapping().adminPropertyNames2Enum.get(_propName);
        }

        /**
         * Returns the related attribute name used within Mx. The method
         * returns only correct values if the initialize method was called!
         *
         * @param _paramCache   for which parameter cache must the attribute
         *                      returned
         * @return MX name of the property definition
         * @see Mapping_mxJPO#adminPropertyAttributes
         */
        public String getAttrName(final ParameterCache_mxJPO _paramCache)
        {
            return _paramCache.getMapping().adminPropertyAttributes.get(this);
        }
    }

    /**
     * Enumeration used for attribute definitions.
     */
    public enum AttributeDef
    {
        /**
         * Next number attribute of type {@link BusTypeDef#NumberGenerator}.
         * <b>Attention!</b> The name of the enumeration should not be changed!
         * The name is used within the mapping property file.
         */
        NUMBERGENERATORNEXTNUMBER;

        /**
         * Returns the related name used within MX. The method returns only
         * correct values if the initialize method was called!
         *
         * @param _paramCache   for which parameter cache must the property
         *                      MX name returned
         * @return MX name of the attribute definition
         */
        public String getMxName(final ParameterCache_mxJPO _paramCache)
        {
            return _paramCache.getMapping().attributeMap.get(this);
        }
    }
}
