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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Groups the type definition enumeration used for the parameter definitions.
 *
 * @author tmoxter
 * @version $Id$
 */
public class TypeDefGroup_mxJPO
        extends ParameterValues_mxJPO
{
    /**
     * Maps from the name of the type definition group to the related type
     * definition group instance.
     *
     * @see #defineValue(String, String)
     */
    private final static Map<String,TypeDefGroup_mxJPO> MAP
            = new HashMap<String,TypeDefGroup_mxJPO>();

    /**
     * Name list of type definition instances for which this type definition
     * group is defined.
     *
     * @see #getTypeDefList()
     * @see #defineValue(String, String)
     */
    private Collection<String> typeDefList;

    /**
     * The constructor is defined private so that a new instance could only
     * created within this class.
     */
    private TypeDefGroup_mxJPO()
    {
    }

    /**
     * Defines the values of the type definition group.
     *
     * @param _key
     * @param _value
     */
    protected static void defineValue(final String _key,
                                      final String _value)
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        TypeDefGroup_mxJPO group = MAP.get(enumName);
        if (group == null)  {
            group = new TypeDefGroup_mxJPO();
            MAP.put(enumName, group);
        }

        if ("ParameterDesc".equals(key))  {
            group.paramDesc = _value;
        } else if ("ParameterList".equals(key))  {
            group.paramList = Arrays.asList(_value.split(","));
        } else if ("TypeDefList".endsWith(key))  {
            group.typeDefList = Arrays.asList(_value.split(","));
        }
    }

    /**
     * Resets type definition group map.
     *
     * @see #MAP
     */
    protected static void resetValues()
    {
        MAP.clear();
    }

    public static Collection<TypeDefGroup_mxJPO> getGroups()
    {
        return MAP.values();
    }

    /**
     * Returns the name list of type definitions.
     *
     * @return name list of type definitions
     * @see #typeDefList
     */
    public Collection<String> getTypeDefList()
    {
        return this.typeDefList;
    }
}
