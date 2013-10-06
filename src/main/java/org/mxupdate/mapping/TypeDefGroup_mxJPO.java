/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.mapping;

import java.util.Arrays;
import java.util.Collection;

/**
 * Groups the type definition enumeration used for the parameter definitions.
 *
 * @author The MxUpdate Team
 */
public final class TypeDefGroup_mxJPO
        extends AbstractValue_mxJPO
{
    /**
     * Name list of type definition instances for which this type definition
     * group is defined.
     *
     * @see #getTypeDefList()
     * @see #defineValue(Mapping_mxJPO, String, String)
     */
    private Collection<String> typeDefList;

    /**
     * The constructor is defined private so that a new instance could only
     * created within this class.
     *
     * @param _name     name of the type definition group
     */
    private TypeDefGroup_mxJPO(final String _name)
    {
        super(_name);
    }

    /**
     * Defines the values of the type definition group.
     *
     * @param _mapping  cache for all mapping
     * @param _key      key of the type definition group (including the name of
     *                  type definition group and the kind of the type
     *                  definition separated by a point)
     * @param _value    value of the related value
     * @throws Exception if key is not found
     */
    protected static void defineValue(final Mapping_mxJPO _mapping,
                                      final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        TypeDefGroup_mxJPO group = _mapping.getTypeDefGroupMap().get(enumName);
        if (group == null)  {
            group = new TypeDefGroup_mxJPO(enumName);
            _mapping.getTypeDefGroupMap().put(enumName, group);
        }

        if ("TypeDefList".endsWith(key))  {
            group.typeDefList = Arrays.asList(_value.split(","));
        } else  {
            group.defineValues(key, _value);
        }
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
