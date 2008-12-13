/*
 * Copyright 2008 The MxUpdate Team
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
    private static final Map<TypeDef,String> TYPES = new HashMap<TypeDef,String>();

    /**
     * Used prefix of type definitions within the property file.
     */
    private static final String PREFIX_TYPE = "Type.";

    /**
     * Dummy constructor so that no new instance of this final mapping class
     * could be created.
     */
    private Mapping_mxJPO()
    {
    }

    public static void init(final Context _context)
            throws MatrixException, IOException
    {
        PROPERTIES.clear();
        ATTRIBUTES.clear();
        TYPES.clear();

        PROPERTIES.putAll(MqlUtil_mxJPO.readPropertyProgram(_context, PROP_NAME));

        // map attributes and types
        for (final Entry<Object, Object> entry : PROPERTIES.entrySet())  {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();
            if (key.startsWith(PREFIX_ATTRIBUTE))  {
                ATTRIBUTES.put(AttributeDef.valueOf(key.substring(PREFIX_ATTRIBUTE.length())),
                               value);
            } else if (key.startsWith(PREFIX_TYPE))  {
                TYPES.put(TypeDef.valueOf(key.substring(PREFIX_TYPE.length())),
                          value);
            }
        }
    }

    /**
     * Enum for attribute definitions.
     */
    public enum AttributeDef
    {
        /**
         * Author attribute.
         */
        CommonAuthor,

        /**
         * File date attribute.
         */
        CommonFileDate,

        /**
         * Installed date attribute.
         */
        CommonInstalledDate,

        /**
         * Version attribute.
         */
        CommonVersion,

        /**
         * Next number attribute of type {@link TypeDef#NumberGenerator}.
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
     * Enum for type definitions.
     */
    public enum TypeDef
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
        Trigger;

        /**
         * Returns the related name used within Mx. The method returns only
         * correct values if the initialize method was called!
         *
         * @return Mx name of the type definition
         */
        public String getMxName()
        {
            return Mapping_mxJPO.TYPES.get(this);
        }
    }
}
