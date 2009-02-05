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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the definition of all parameters.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class ParameterDef_mxJPO
        extends AbstractValue_mxJPO
{
    public enum Type
    {
        /**
         * If the parameter is boolean, that means the parameter is defined
         * (true) or not (false).
         */
        BOOLEAN,
        /**
         * A list of strings is used this type. That means that every argument
         * of a parameter is append as string to the list.
         */
        LIST,
        /**
         * If the parameter is defined, the value is set to a string. If e.g.
         * the parameter is defined twice, the second definition of the
         * parameter overwrites the first definition.
         */
        STRING;
    }

    /**
     * Maps from the name of the parameter to the parameter instance.
     *
     * @see #defineValue(String, String)
     */
    private final static Map<String,ParameterDef_mxJPO> MAP
            = new HashMap<String,ParameterDef_mxJPO>();

    /**
     * Default value of the parameter.
     *
     * @see #getDefaultValue()
     */
    private String defaultValue;

    /**
     * Type of the parameter.
     *
     * @see Type
     * @see #getType()
     */
    private Type type;

    /**
     * The constructor is defined private so that a new instance could only
     * created within this class.
     *
     * @param _name     name of the parameter definition
     */
    private ParameterDef_mxJPO(final String _name)
    {
        super(_name);
    }

    /**
     * Resets parameter map.
     *
     * @see #MAP
     */
    protected static void resetValues()
    {
        MAP.clear();
    }

    /**
     * Defines the values of the type definition group.
     *
     * @param _key
     * @param _value
     * @throws Exception if key is not found
     */
    protected static void defineValue(final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        ParameterDef_mxJPO param = MAP.get(enumName);
        if (param == null)  {
            param = new ParameterDef_mxJPO(enumName);
            MAP.put(enumName, param);
        }

        if ("Default".equals(key))  {
            param.defaultValue = _value;
        } else if ("Type".equals(key))  {
            param.type = Type.valueOf(_value.toUpperCase());
        } else  {
            param.defineValues(key, _value);
        }
    }

    /**
     * Returns for given name the related parameter instance.
     *
     * @param _name name of the searched parameter instance
     * @return found parameter instance (or <code>null</code> if not found)
     * @see #MAP
     */
    public static ParameterDef_mxJPO valueOf(final String _name)
    {
        return MAP.get(_name);
    }

    /**
     * Returns the list of all parameter instances.
     *
     * @return list of all parameter instances
     * @return #MAP
     */
    public static Collection<ParameterDef_mxJPO> values()
    {
        return MAP.values();
    }

    /**
     * Getter method for instance variable {@link #defaultValue} returning the
     * default value.
     *
     * @return default value of the parameter
     * @see #defaultValue
     */
    public String getDefaultValue()
    {
        return this.defaultValue;
    }

    /**
     * Getter method for instance variable {@link #type} returning the
     * parameter type.
     *
     * @return parameter type
     * @see #type
     */
    public Type getType()
    {
        return this.type;
    }

    /**
     * Prepares the string representation of the parameter definition as
     * concatenation of the name and type.
     *
     * @return string representation of the parameter definition
     */
    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("[Parameter Definition ")
                .append("'").append(this.getName()).append("', ")
                .append("type = ").append(this.type)
            .append("]")
            .toString();
    }
}
