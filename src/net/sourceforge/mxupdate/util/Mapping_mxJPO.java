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

package net.sourceforge.mxupdate.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * The class is used to map from used names within the MxUpdate JPOs and the
 * internal used names within Mx.
 *
 * @author tmoxter
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
    private static final String PROP_NAME = "net.sourceforge.mxupdate.mapping.properties";

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
     * Mapping between internal used admin property definitions and the default
     * value of the property.
     */
    private static final Map<AdminPropertyDef,String> ADMINPROPERTY_VALUES = new HashMap<AdminPropertyDef,String>();

    /**
     * Used prefix of admin property value definitions within the property
     * file.
     */
    private static final String PREFIX_ADMINPROPERTYVALUE = "PropertyValue.";

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
     * Mapping between internal used type definitions and the Mx type names.
     */
    private static final Map<BusTypeDef,String> BUS_TYPES = new HashMap<BusTypeDef,String>();

    /**
     * Used prefix of type definitions within the property file.
     */
    private static final String PREFIX_TYPE = "Type.";

    /**
     * Mapping between internal used type definitions and the Mx policy names.
     */
    private static final Map<BusTypeDef,String> BUS_TYPE_POLICIES = new HashMap<BusTypeDef,String>();

    /**
     * Used prefix of policy definitions within the property file.
     */
    private static final String PREFIX_POLICY = "Policy.";

    /**
     * Mapping between internal used type definitions and the Mx vault names.
     */
    private static final Map<BusTypeDef,String> BUS_TYPE_VAULTS = new HashMap<BusTypeDef,String>();

    /**
     * Used prefix of vault definitions within the property file.
     */
    private static final String PREFIX_VAULT = "Vault.";

    /**
     * Mapping between internal used type definitions and the file paths.
     *
     * @see AdminTypeDef#getFilePath()
     */
    private static final Map<AdminTypeDef,String> ADMINTYPE_FILE_PATHS = new HashMap<AdminTypeDef,String>();

    /**
     * Mapping between internal used type definitions and the file paths.
     *
     * @see BusTypeDef#getFilePath()
     */
    private static final Map<BusTypeDef,String> BUSTYPE_FILE_PATHS = new HashMap<BusTypeDef,String>();

    /**
     * Used prefix of type definitions within the property file.
     */
    private static final String PREFIX_FILE_PATH = "FilePath.";

    /**
     * Mapping between internal used administration type definitions and the
     * file prefixes.
     *
     * @see AdminTypeDef#getFilePrefix()
     */
    private static final Map<AdminTypeDef,String> ADMIN_TYPE_FILE_PREFIXES = new HashMap<AdminTypeDef,String>();

    /**
     * Mapping between internal used business type definitions and the file
     * prefixes.
     *
     * @see BusTypeDef#getFilePrefix()
     */
    private static final Map<BusTypeDef,String> BUS_TYPE_FILE_PREFIXES = new HashMap<BusTypeDef,String>();

    /**
     * Used prefix of type definitions within the property file.
     */
    private static final String PREFIX_FILE_PREFIX = "FilePrefix.";

    /**
     * Mapping between internal used administration type definitions and the
     * file suffixes.
     *
     * @see AdminTypeDef#getFileSuffix()
     */
    private static final Map<AdminTypeDef,String> ADMIN_TYPE_FILE_SUFFIXES = new HashMap<AdminTypeDef,String>();

    /**
     * Mapping between internal used business type definitions and the file
     * suffixes.
     *
     * @see BusTypeDef#getFileSuffix()
     */
    private static final Map<BusTypeDef,String> BUS_TYPE_FILE_SUFFIXES = new HashMap<BusTypeDef,String>();

    /**
     * Used file suffix of type definitions within the property file.
     */
    private static final String PREFIX_FILE_SUFFIX = "FileSuffix.";

    /**
     * Mapping between internal used administration type definitions and the
     * logging string.
     *
     * @see AdminTypeDef#getLogging()
     */
    private static final Map<AdminTypeDef,String> ADMIN_TYPE_LOGGINGS = new HashMap<AdminTypeDef,String>();

    /**
     * Mapping between internal used business type definitions and the logging
     * string.
     *
     * @see BusTypeDef#getLogging()
     */
    private static final Map<BusTypeDef,String> BUS_TYPE_LOGGINGS = new HashMap<BusTypeDef,String>();

    /**
     * Used logging text of type definitions within the property file.
     */
    private static final String PREFIX_LOGGING = "Logging.";

    /**
     * Mapping between internal used administration type definitions and the
     * titles.
     *
     * @see AdminTypeDef#getTitle()
     */
    private static final Map<AdminTypeDef,String> ADMIN_TYPE_TITLES = new HashMap<AdminTypeDef,String>();

    /**
     * Mapping between internal used business type definitions and the titles.
     *
     * @see BusTypeDef#getTitle()
     */
    private static final Map<BusTypeDef,String> BUS_TYPE_TITLES = new HashMap<BusTypeDef,String>();

    /**
     * Used title of type definitions within the property file.
     */
    private static final String PREFIX_TITLE = "Title.";

    /**
     * Mapping between internal used business type definitions and the titles.
     *
     * @see RelationDef#getMxName()
     */
    private static final Map<RelationDef,String> RELATIONS = new HashMap<RelationDef,String>();

    /**
     * Used title of type definitions within the property file.
     */
    private static final String PREFIX_RELATION = "Relationship.";

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
            throws MatrixException, IOException
    {
        PROPERTIES.clear();
        ADMINPROPERTY_ENUM2NAMES.clear();
        ADMINPROPERTY_NAMES2ENUM.clear();
        ADMINPROPERTY_VALUES.clear();
        ATTRIBUTES.clear();
        BUS_TYPE_FILE_PREFIXES.clear();
        BUS_TYPE_FILE_SUFFIXES.clear();
        BUS_TYPE_LOGGINGS.clear();
        BUS_TYPE_POLICIES.clear();
        BUS_TYPE_TITLES.clear();
        BUS_TYPE_VAULTS.clear();
        BUS_TYPES.clear();

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
            } else if (key.startsWith(PREFIX_ADMINPROPERTYVALUE))  {
                ADMINPROPERTY_VALUES.put(AdminPropertyDef.valueOf(key.substring(PREFIX_ADMINPROPERTYVALUE.length()).toUpperCase()), value);
            } else if (key.startsWith(PREFIX_ATTRIBUTE))  {
                ATTRIBUTES.put(AttributeDef.valueOf(key.substring(PREFIX_ATTRIBUTE.length())), value);
            } else if (key.startsWith(PREFIX_FILE_PATH))  {
                final String keyFilePath = key.substring(PREFIX_FILE_PATH.length());
                try  {
                    ADMINTYPE_FILE_PATHS.put(AdminTypeDef.valueOf(keyFilePath), value);
                } catch (final IllegalArgumentException e)  {
                    BUSTYPE_FILE_PATHS.put(BusTypeDef.valueOf(keyFilePath), value);
                }
            } else if (key.startsWith(PREFIX_FILE_PREFIX))  {
                final String keyFilePrefix = key.substring(PREFIX_FILE_PREFIX.length());
                try  {
                    ADMIN_TYPE_FILE_PREFIXES.put(AdminTypeDef.valueOf(keyFilePrefix), value);
                } catch (final IllegalArgumentException e)  {
                    BUS_TYPE_FILE_PREFIXES.put(BusTypeDef.valueOf(keyFilePrefix), value);
                }
            } else if (key.startsWith(PREFIX_FILE_SUFFIX))  {
                final String keyFilePrefix = key.substring(PREFIX_FILE_SUFFIX.length());
                try  {
                    ADMIN_TYPE_FILE_SUFFIXES.put(AdminTypeDef.valueOf(keyFilePrefix), value);
                } catch (final IllegalArgumentException e)  {
                    BUS_TYPE_FILE_SUFFIXES.put(BusTypeDef.valueOf(keyFilePrefix), value);
                }
            } else if (key.startsWith(PREFIX_LOGGING))  {
                final String keyFilePrefix = key.substring(PREFIX_LOGGING.length());
                try  {
                    ADMIN_TYPE_LOGGINGS.put(AdminTypeDef.valueOf(keyFilePrefix), value);
                } catch (final IllegalArgumentException e)  {
                    BUS_TYPE_LOGGINGS.put(BusTypeDef.valueOf(keyFilePrefix), value);
                }
            } else if (key.startsWith(PREFIX_RELATION))  {
                RELATIONS.put(RelationDef.valueOf(key.substring(PREFIX_RELATION.length())), value);
            } else if (key.startsWith(PREFIX_TITLE))  {
                final String keyTitle = key.substring(PREFIX_TITLE.length());
                try  {
                    ADMIN_TYPE_TITLES.put(AdminTypeDef.valueOf(keyTitle), value);
                } catch (final IllegalArgumentException e)  {
                    BUS_TYPE_TITLES.put(BusTypeDef.valueOf(keyTitle), value);
                }
            } else if (key.startsWith(PREFIX_POLICY))  {
                BUS_TYPE_POLICIES.put(BusTypeDef.valueOf(key.substring(PREFIX_POLICY.length())), value);
            } else if (key.startsWith(PREFIX_TYPE))  {
                BUS_TYPES.put(BusTypeDef.valueOf(key.substring(PREFIX_TYPE.length())), value);
            } else if (key.startsWith(PREFIX_VAULT))  {
                BUS_TYPE_VAULTS.put(BusTypeDef.valueOf(key.substring(PREFIX_VAULT.length())), value);
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

       /**
         * Returns the related admin property value. The method returns only
         * correct values if the initialize method was called!
         *
         * @return value of the property definition
         * @see Mapping_mxJPO#ADMINPROPERTY_VALUES
         */
        public String getValue()
        {
            return Mapping_mxJPO.ADMINPROPERTY_VALUES.get(this);
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

    /**
     * Interface which the type enumerations must implement.
     *
     * @see AdminTypeDef
     * @see BusTypeDef
     */
    public interface TypeDef
    {
        /**
         *
         *
         * @return file path of the type definition
         */
        public String getFilePath();

        /**
         *
         * @return file prefix of the type definition
         */
        public String getFilePrefix();

        /**
         *
         * @return file suffix of the type definition
         */
        public String getFileSuffix();

        /**
         *
         * @return title of the type definition
         */
        public String getTitle();

        /**
         * @return string used for logging purpose.
         */
        public String getLogging();
    }

    /**
     * Enumeration for administration type definitions.
     */
    public enum AdminTypeDef
            implements TypeDef
    {
        /**
         * Administration type is not defined (or does not exists). The value
         * is used like <code>null</code>.
         */
        Undef(null, null),

        /** Data model attribute. */
        Attribute("attribute", ""),

        /** Data model expression. */
        Expression("expression", ""),

        /** Data model format. */
        Format("format", ""),

        /** Data model policy. */
        Policy("policy", ""),

        /** Data model relationship. */
        Relationship("relationship", ""),

        /** Data model rule. */
        Rule("rule", ""),

        /** Data model type. */
        Type("type", ""),

        /** Program JPO. */
        JPO("program", ""),

        /** Program program. */
        Program("program", ""),

        /** User association. */
        Association("association", ""),

        /** User group. */
        Group("group", ""),

        /** User person. */
        Person("person", ""),

        /** User role. */
        Role("role", ""),

        /** User interface channel. */
        Channel("channel", ""),

        /** User interface command. */
        Command("command", ""),

        /** User interface form. */
        Form("form", ""),

        /** User interface inquiry. */
        Inquiry("inquiry", ""),

        /** User interface menu. */
        Menu("menu", ""),

        /** User interface portal. */
        Portal("portal", ""),

        /** User interface setting. */
        Setting("setting", ""),

        /** User interface web table. */
        Table("table", "system");

        /**
         * Internal Mx used name of the administration type.
         */
        private final String mxName;

        /**
         * Suffix of the administration type (if required, e.g. for web
         * tables). If not defined the suffix is a zero length string.
         */
        private final String mxSuffix;

        /**
         *
         * @param _adminName
         * @param _adminSuffix
         */
        private AdminTypeDef(final String _adminName,
                             final String _adminSuffix)
        {
            this.mxName = _adminName;
            this.mxSuffix = _adminSuffix;
        }

        /**
         * Returns the related administration type name used within Mx. The
         * method returns only correct values if the initialize method was
         * called!
         *
         * @return Mx name of the administration type definition
         * @see #mxName
         */
        public String getMxName()
        {
            return this.mxName;
        }

        /**
         * Returns the related administration type fsufix used within Mx. The
         * method returns only correct values if the initialize method was
         * called!
         *
         * @return Mx suffix of the administration type definition
         * @see #mxSuffix
         */
        public String getMxSuffix()
        {
            return this.mxSuffix;
        }

        /**
         * Returns the related file path. The method returns only correct
         * values if the initialize method was called!
         *
         * @return file path of the administration type definition
         * @see Mapping_mxJPO#ADMINTYPE_FILE_PATHS
         */
        public String getFilePath()
        {
            return Mapping_mxJPO.ADMINTYPE_FILE_PATHS.get(this);
        }

        /**
         * Returns the related file name prefix. The method returns only
         * correct values if the initialize method was called!
         *
         * @return file name prefix of the administration type definition
         * @see Mapping_mxJPO#ADMIN_TYPE_FILE_PREFIXES
         */
        public String getFilePrefix()
        {
            return Mapping_mxJPO.ADMIN_TYPE_FILE_PREFIXES.get(this);
        }

        /**
         * Returns the related file name suffix. The method returns only
         * correct values if the initialize method was called!
         *
         * @return file name prefix of the administration type definition
         * @see Mapping_mxJPO#ADMIN_TYPE_FILE_SUFFIXES
         */
        public String getFileSuffix()
        {
            return Mapping_mxJPO.ADMIN_TYPE_FILE_SUFFIXES.get(this);
        }

        /**
         * Returns the related logging string. The method returns only correct
         * values if the initialize method was called!
         *
         * @return logging string of the administration type definition
         * @see Mapping_mxJPO#ADMIN_TYPE_LOGGINGS
         */
        public String getLogging()
        {
            return Mapping_mxJPO.ADMIN_TYPE_LOGGINGS.get(this);
        }

        /**
         * Returns the related title used in the Mx update files. The method
         * returns only correct values if the initialize method was called!
         *
         * @return title of the business type definition
         * @see Mapping_mxJPO#ADMIN_TYPE_TITLES
         */
        public String getTitle()
        {
            return Mapping_mxJPO.ADMIN_TYPE_TITLES.get(this);
        }
    }

    /**
     * Enumeration for business type definitions.
     */
    public enum BusTypeDef
            implements TypeDef
    {
        /**
         * Business type is not defined (or does not exists). The value is used
         * like <code>null</code>.
         */
        UnDef,

        /**
         * Number generator business type.
         */
        NumberGenerator,

        /**
         * Object generator business type.
         */
        ObjectGenerator,

        /**
         * Person business type.
         */
        Person,

        /**
         * Trigger business type.
         */
        Trigger,

        /**
         * Trigger group business type.
         */
        TriggerGroup;

        /**
         * Returns the related business type name used within Mx. The method
         * returns only correct values if the initialize method was called!
         *
         * @return Mx name of the business type definition
         * @see Mapping_mxJPO#BUS_TYPES
         */
        public String getMxName()
        {
            return Mapping_mxJPO.BUS_TYPES.get(this);
        }

        /**
         * Returns the related business policy name used within Mx. The method
         * returns only correct values if the initialize method was called!
         *
         * @return Mx name of the business type definition
         * @see Mapping_mxJPO#BUS_TYPE_POLICIES
         */
        public String getMxPolicy()
        {
            return Mapping_mxJPO.BUS_TYPE_POLICIES.get(this);
        }

        /**
         * Returns the related business vault name used within Mx. The method
         * returns only correct values if the initialize method was called!
         *
         * @return Mx name of the business vault definition
         * @see Mapping_mxJPO#BUS_TYPE_VAULTS
         */
        public String getMxVault()
        {
            return Mapping_mxJPO.BUS_TYPE_VAULTS.get(this);
        }

        /**
         * Returns the related file path. The method returns only correct
         * values if the initialize method was called!
         *
         * @return file path of the business type definition
         * @see Mapping_mxJPO#BUSTYPE_FILE_PATHS
         */
        public String getFilePath()
        {
            return Mapping_mxJPO.BUSTYPE_FILE_PATHS.get(this);
        }

        /**
         * Returns the related file name prefix. The method returns only
         * correct values if the initialize method was called!
         *
         * @return file name prefix of the business type definition
         * @see Mapping_mxJPO#BUS_TYPE_FILE_PREFIXES
         */
        public String getFilePrefix()
        {
            return Mapping_mxJPO.BUS_TYPE_FILE_PREFIXES.get(this);
        }

        /**
         * Returns the related file name suffix. The method returns only
         * correct values if the initialize method was called!
         *
         * @return file name prefix of the business type definition
         * @see Mapping_mxJPO#BUS_TYPE_FILE_SUFFIXES
         */
        public String getFileSuffix()
        {
            return Mapping_mxJPO.BUS_TYPE_FILE_SUFFIXES.get(this);
        }

        /**
         * Returns the related logging string. The method returns only correct
         * values if the initialize method was called!
         *
         * @return logging string of the business type definition
         * @see Mapping_mxJPO#BUS_TYPE_LOGGINGS
         */
        public String getLogging()
        {
            return Mapping_mxJPO.BUS_TYPE_LOGGINGS.get(this);
        }

        /**
         * Returns the related title used in the Mx update files. The method
         * returns only correct values if the initialize method was called!
         *
         * @return title of the business type definition
         * @see Mapping_mxJPO#BUS_TYPE_TITLES
         */
        public String getTitle()
        {
            return Mapping_mxJPO.BUS_TYPE_TITLES.get(this);
        }
    }

    /**
     * Enumeration used to define all relationship used within MxUpdate
     * application.
     */
    public enum RelationDef
    {
        /**
         * Relationship between trigger groups and triggers or trigger groups.
         */
        TriggerGroup;

        /**
         * Returns the related name used within Mx. The method returns only
         * correct values if the initialize method was called!
         *
         * @return Mx name of the relationship definition
         * @see Mapping_mxJPO#RELATIONS
         */
        public String getMxName()
        {
            return Mapping_mxJPO.RELATIONS.get(this);
        }
    }
}
