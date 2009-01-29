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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.datamodel.Attribute_mxJPO;
import org.mxupdate.update.datamodel.Expression_mxJPO;
import org.mxupdate.update.datamodel.Format_mxJPO;
import org.mxupdate.update.datamodel.NumberGenerator_mxJPO;
import org.mxupdate.update.datamodel.ObjectGenerator_mxJPO;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.datamodel.Rule_mxJPO;
import org.mxupdate.update.datamodel.TriggerGroup_mxJPO;
import org.mxupdate.update.datamodel.Trigger_mxJPO;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.update.integration.IEFGlobalConfig_mxJPO;
import org.mxupdate.update.program.JPO_mxJPO;
import org.mxupdate.update.program.Program_mxJPO;
import org.mxupdate.update.user.Association_mxJPO;
import org.mxupdate.update.user.Group_mxJPO;
import org.mxupdate.update.user.Person_mxJPO;
import org.mxupdate.update.user.Role_mxJPO;
import org.mxupdate.update.userinterface.Channel_mxJPO;
import org.mxupdate.update.userinterface.Command_mxJPO;
import org.mxupdate.update.userinterface.Form_mxJPO;
import org.mxupdate.update.userinterface.Inquiry_mxJPO;
import org.mxupdate.update.userinterface.Menu_mxJPO;
import org.mxupdate.update.userinterface.Portal_mxJPO;
import org.mxupdate.update.userinterface.Table_mxJPO;

/**
 * Enumeration for administration type definitions.
 *
 * @author tmoxter
 * @version $Id$
 */
public enum TypeDef_mxJPO
{
    /**
     * Administration type is not defined (or does not exists). The value
     * is used like <code>null</code>.
     */
    Undef(null, null, null),

    /** Data model attribute. */
    Attribute("attribute", "", Attribute_mxJPO.class),

    /** Data model expression. */
    Expression("expression", "", Expression_mxJPO.class),

    /** Data model format. */
    Format("format", "", Format_mxJPO.class),

    /** Number generator business type. */
    NumberGenerator(null, null, NumberGenerator_mxJPO.class),

    /** Object generator business type. */
    ObjectGenerator(null, null, ObjectGenerator_mxJPO.class),

    /** Data model policy. */
    Policy("policy", "", Policy_mxJPO.class),

    /** Data model relationship. */
    Relationship("relationship", "", Relationship_mxJPO.class),

    /** Data model rule. */
    Rule("rule", "", Rule_mxJPO.class),

    /** Trigger business type. */
    Trigger(null, null, Trigger_mxJPO.class),

    /** Trigger group business type. */
    TriggerGroup(null, null, TriggerGroup_mxJPO.class),

    /** Data model type. */
    Type("type", "", Type_mxJPO.class),

    /** Integration IEF global configuration object */
    IEFGlobalConfig(null, null, IEFGlobalConfig_mxJPO.class),

    /** Program JPO. */
    JPO("program", "", JPO_mxJPO.class),

    /** Program program. */
    Program("program", "", Program_mxJPO.class),

    /** User association. */
    Association("association", "", Association_mxJPO.class),

    /** User group. */
    Group("group", "", Group_mxJPO.class),

    /** User person. */
    Person("person", "", Person_mxJPO.class),

    /** User role. */
    Role("role", "", Role_mxJPO.class),

    /** User interface channel. */
    Channel("channel", "", Channel_mxJPO.class),

    /** User interface command. */
    Command("command", "", Command_mxJPO.class),

    /** User interface form. */
    Form("form", "", Form_mxJPO.class),

    /** User interface inquiry. */
    Inquiry("inquiry", "", Inquiry_mxJPO.class),

    /** User interface menu. */
    Menu("menu", "", Menu_mxJPO.class),

    /** User interface portal. */
    Portal("portal", "", Portal_mxJPO.class),

    /** User interface web table. */
    Table("table", "system", Table_mxJPO.class);

    private static class TypeDefValues
    {
        /**
         * Maps the type definition enumeration to related type definition
         * value.
         *
         * @see TypeDef_mxJPO#resetTypeDefValues()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private final static Map<TypeDef_mxJPO, TypeDefValues> VALUES = new HashMap<TypeDef_mxJPO, TypeDefValues>();

        /**
         * Maps the administration object class to related type definition
         * enumeration.
         *
         * @see TypeDef_mxJPO#valueOf(AbstractObject_mxJPO)
         */
        private static final Map<Class<? extends AbstractObject_mxJPO>, TypeDef_mxJPO> MAP_CLASS2TYPEDEF
                = new HashMap<Class<? extends AbstractObject_mxJPO>, TypeDef_mxJPO>();

        /**
         * Used prefix of ignored attributes for business objects within the
         * property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_BUS_IGNOREATTRIBUTES = "BusIgnoreAttributes";

        /**
         * Used prefix of check exists definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_BUS_CHECKEXISTS = "BusCheckExists";

        /**
         * Used prefix of policy definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_BUS_POLICY = "BusPolicy";

        /**
         * Used prefix of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_BUS_TYPE = "BusType";

        /**
         * Used prefix of vault definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_BUS_VAULT = "BusVault";

        /**
         * Used prefix of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_FILE_PATH = "FilePath";

        /**
         * Used prefix of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_FILE_PREFIX = "FilePrefix";

        /**
         * Used file suffix of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_FILE_SUFFIX = "FileSuffix";

        /**
         * Used logging text of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_TEXT_LOGGING = "TextLogging";

        /**
         * Used title of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_TEXT_TITLE = "TextTitle";

        /**
         * Used parameter description of type definitions within the property
         * file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_PARAM_DESC = "ParameterDesc";

        /**
         * Used parameter list of type definitions within the property file.
         *
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private static final String PREFIX_PARAM_LIST = "ParameterList";

        /**
         * Must be checked if the business type exists?
         *
         * @see TypeDef_mxJPO#isBusCheckExists
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private boolean busCheckExists = false;

        /**
         * Defines the list of attributes which are automatically ignored
         * within the update.
         *
         * @see TypeDef_mxJPO#getMxBusIgnoredAttributes()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private Collection<String> busIgnoredAttributes;

        /**
         * Mapping between internal used type definitions and the Mx policy
         * names.
         *
         * @see TypeDef_mxJPO#getMxBusPolicy()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String busPolicy;

        /**
         * Mapping between internal used type definitions and the Mx type
         * names.
         *
         * @see TypeDef_mxJPO#getMxBusType()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String busType;

        /**
         * Mapping between internal used type definitions and the Mx vault
         * names.
         *
         * @see TypeDef_mxJPO#getMxBusVault()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String busVault;

        /**
         * Mapping between internal used type definitions and the file paths.
         *
         * @see TypeDef_mxJPO#getFilePath()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String filePath;

        /**
         * Mapping between internal used administration type definitions and the
         * file prefixes.
         *
         * @see TypeDef_mxJPO#getFilePrefix()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String filePrefix;

        /**
         * Mapping between internal used administration type definitions and the
         * file suffixes.
         *
         * @see TypeDef_mxJPO#getFileSuffix()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String fileSuffix;

        /**
         * Mapping between internal used administration type definitions and the
         * logging string.
         *
         * @see TypeDef_mxJPO#getLogging()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String textLogging;

        /**
         * Mapping between internal used administration type definitions and the
         * titles.
         *
         * @see TypeDef_mxJPO#getTitle()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String textTitle;

        /**
         * Defines the parameter description.
         *
         * @see TypeDef_mxJPO#getParameterDesc()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private String paramDesc;

        /**
         * Defines the list of parameters.
         *
         * @see TypeDef_mxJPO#getParameters()
         * @see TypeDef_mxJPO#defineTypeDefValue(String, String)
         */
        private Collection<String> paramList;
    }

    /**
     * Resets type definition values map.
     *
     * @see TypeDefValues#VALUES
     */
    protected static void resetTypeDefValues()
    {
        TypeDefValues.VALUES.clear();
    }

    protected static void defineTypeDefValue(final String _key,
                                             final String _value)
    {
        final String key = _key.replaceAll("\\..*", "");
        final String enumName = _key.substring(key.length() + 1);

        final TypeDef_mxJPO typeDef = TypeDef_mxJPO.valueOf(enumName);
        TypeDefValues value = TypeDefValues.VALUES.get(typeDef);
        if (value == null)  {
            value = new TypeDefValues();
            TypeDefValues.VALUES.put(typeDef, value);
        }

        if (key.equals(TypeDefValues.PREFIX_BUS_CHECKEXISTS))  {
            value.busCheckExists = _value.equalsIgnoreCase("true");
        } else if (key.equals(TypeDefValues.PREFIX_BUS_IGNOREATTRIBUTES))  {
            value.busIgnoredAttributes = Arrays.asList(_value.split(","));
        } else if (key.equals(TypeDefValues.PREFIX_BUS_POLICY))  {
            value.busPolicy = _value;
        } else if (key.equals(TypeDefValues.PREFIX_BUS_TYPE))  {
            value.busType = _value;
        } else if (key.equals(TypeDefValues.PREFIX_BUS_VAULT))  {
            value.busVault = _value;
        } else if (key.equals(TypeDefValues.PREFIX_FILE_PATH))  {
            value.filePath = _value;
        } else if (key.equals(TypeDefValues.PREFIX_FILE_PREFIX))  {
            value.filePrefix = _value;
        } else if (key.equals(TypeDefValues.PREFIX_FILE_SUFFIX))  {
            value.fileSuffix = _value;
        } else if (key.equals(TypeDefValues.PREFIX_PARAM_DESC))  {
            value.paramDesc = _value;
        } else if (key.equals(TypeDefValues.PREFIX_PARAM_LIST))  {
            value.paramList = Arrays.asList(_value.split(","));
        } else if (key.equals(TypeDefValues.PREFIX_TEXT_LOGGING))  {
            value.textLogging = _value;
        } else if (key.equals(TypeDefValues.PREFIX_TEXT_TITLE))  {
            value.textTitle = _value;
        }
    }

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
     * Stores the class implementing the MxUpdate functionality.
     */
    private final Class<? extends AbstractObject_mxJPO> typeClazz;

    /**
     *
     * @param _adminName    name of the administration type
     * @param _adminSuffix  suffix of the administration type (used e.g.
     *                      for the export, update etc...)
     * @param _typeClazz    type class
     * @see TypeDefValues#MAP_CLASS2TYPEDEF
     */
    private TypeDef_mxJPO(final String _adminName,
                    final String _adminSuffix,
                    final Class<? extends AbstractObject_mxJPO> _typeClazz)
    {
        this.mxName = _adminName;
        this.mxSuffix = _adminSuffix;
        this.typeClazz = _typeClazz;
        if (_typeClazz != null)  {
            TypeDefValues.MAP_CLASS2TYPEDEF.put(_typeClazz, this);
        }
    }

    /**
     * Evaluates for given administration object related type definition.
     *
     * @param _obj  administration object for which the type definition is
     *              searched
     * @return related type definition instance
     * @see TypeDefValues#MAP_CLASS2TYPEDEF
     */
    public static TypeDef_mxJPO valueOf(final AbstractObject_mxJPO _obj)
    {
        System.out.println("valueof._obj.getClass()="+_obj.getClass());
        System.out.println("valueof._obj.getClass()="+_obj.getClass().getSuperclass());
/*InfoAnno_mxJPO ret = this.getClass().getAnnotation(InfoAnno_mxJPO.class);
if (ret == null)  {
    Class<?> clazz = this.getClass().getSuperclass();
    while ((clazz != null) && (ret == null))  {
        ret = clazz.getAnnotation(InfoAnno_mxJPO.class);
        clazz = clazz.getSuperclass();
    }
}
return ret;
*/
return TypeDefValues.MAP_CLASS2TYPEDEF.get(_obj.getClass());
    }

    /**
     * Returns the related administration type name used within Mx. The
     * method returns only correct values if the initialize method was
     * called!
     *
     * @return Mx name of the administration type definition
     * @see #mxName
     */
    public String getMxAdminName()
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
    public String getMxAdminSuffix()
    {
        return this.mxSuffix;
    }

    /**
     * Must be checked if the business type exists? The method returns only
     * correct values if the initialize method was called!
     *
     * @return <i>true</i> if a check must be done if the type exists;
     *         otherwise <i>false</i>
     * @see TypeDefValues#busCheckExists
     */
    public boolean isBusCheckExists()
    {
        return TypeDefValues.VALUES.get(this).busCheckExists;
    }

    /**
     * Returns the list of attributes for business object which are ignored for
     * the update (means this attributes are not reseted). The method returns
     * only correct values if the initialize method was called!
     *
     * @return Mx name of the business type definition
     * @see TypeDefValues#busIgnoredAttributes
     */
    public Collection<String> getMxBusIgnoredAttributes()
    {
        return TypeDefValues.VALUES.get(this).busIgnoredAttributes;
    }

    /**
     * Returns the related business policy name used within Mx. The method
     * returns only correct values if the initialize method was called!
     *
     * @return Mx name of the business type definition
     * @see TypeDefValues#busPolicy
     */
    public String getMxBusPolicy()
    {
        return TypeDefValues.VALUES.get(this).busPolicy;
    }

    /**
     * Returns the related business type name used within Mx. The method
     * returns only correct values if the initialize method was called!
     *
     * @return Mx name of the business type definition
     * @see TypeDefValues#busType
     */
    public String getMxBusType()
    {
        return TypeDefValues.VALUES.get(this).busType;
    }

    /**
     * Returns the related business vault name used within Mx. The method
     * returns only correct values if the initialize method was called!
     *
     * @return Mx name of the business vault definition
     * @see TypeDefValues#busVault
     */
    public String getMxBusVault()
    {
        return TypeDefValues.VALUES.get(this).busVault;
    }

    /**
     * Returns the related file path. The method returns only correct
     * values if the initialize method was called!
     *
     * @return file path of the administration type definition
     * @see TypeDefValues#filePath
     */
    public String getFilePath()
    {
        return TypeDefValues.VALUES.get(this).filePath;
    }

    /**
     * Returns the related file name prefix. The method returns only
     * correct values if the initialize method was called!
     *
     * @return file name prefix of the administration type definition
     * @see TypeDefValues#filePrefix
     */
    public String getFilePrefix()
    {
        return TypeDefValues.VALUES.get(this).filePrefix;
    }

    /**
     * Returns the related file name suffix. The method returns only
     * correct values if the initialize method was called!
     *
     * @return file name prefix of the administration type definition
     * @see TypeDefValues#fileSuffix
     */
    public String getFileSuffix()
    {
        return TypeDefValues.VALUES.get(this).fileSuffix;
    }

    /**
     * Returns the description of parameters which defines the administration
     * object used to export / import / delete.
     *
     * @return description of parameter
     * @see TypeDefValues#paramDesc
     */
    public String getParameterDesc()
    {
        return TypeDefValues.VALUES.get(this).paramDesc;
    }

    /**
     * Returns the list of parameters which defines the administration object
     * class used to export / import / delete.
     *
     * @return list of parameter strings
     * @see TypeDefValues#paramList
     */
    public Collection<String> getParameters()
    {
        return TypeDefValues.VALUES.get(this).paramList;
    }

    /**
     * Returns the related logging string. The method returns only correct
     * values if the initialize method was called!
     *
     * @return logging string of the administration type definition
     * @see TypeDefValues#textLogging
     */
    public String getLogging()
    {
        return TypeDefValues.VALUES.get(this).textLogging;
    }

    /**
     * Returns the related title used in the Mx update files. The method
     * returns only correct values if the initialize method was called!
     *
     * @return title of the business type definition
     * @see TypeDefValues#textTitle
     */
    public String getTitle()
    {
        return TypeDefValues.VALUES.get(this).textTitle;
    }

    public AbstractObject_mxJPO newTypeInstance()
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                   InstantiationException, IllegalAccessException, InvocationTargetException
    {
        return this.typeClazz.getConstructor(TypeDef_mxJPO.class).newInstance(this);
    }
}