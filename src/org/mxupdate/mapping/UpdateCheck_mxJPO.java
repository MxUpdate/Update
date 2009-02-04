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
 * Enumeration used to define was is checked if an update is done.
 *
 * @author tmoxter
 * @version $Id$
 */
public enum UpdateCheck_mxJPO
{
    /**
     * Check for the defined version against the version property.
     */
    VERSION,

    /**
     * Check for the last modified date of the file against the file date
     * property.
     */
    FILEDATE;

    /**
     * Maps from the name of the type definition group to the related type
     * definition group instance.
     *
     * @see Mode_mxJPO#defineValue(String, String)
     */
    private final static Map<UpdateCheck_mxJPO,ParameterValues_mxJPO> MAP
            = new HashMap<UpdateCheck_mxJPO,ParameterValues_mxJPO>();

    /**
     * Resets type definition map.
     *
     * @see #MAP
     */
    protected static void resetValues()
    {
        MAP.clear();
    }

    /**
     * Defines the values of the mode enumerations.
     *
     * @param _key      key with name of enumeration and (separated by a point)
     *                  the key
     * @param _value    value which must be set
     * @see ParameterValues_mxJPO
     */
    protected static void defineValue(final String _key,
                                      final String _value)
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        final UpdateCheck_mxJPO modeEnum = valueOf(enumName);
        ParameterValues_mxJPO mode = MAP.get(modeEnum);
        if (mode == null)  {
            mode = new ParameterValues_mxJPO();
            MAP.put(modeEnum, mode);
        }

        if ("ParameterDesc".equals(key))  {
            mode.paramDesc = _value;
        } else if ("ParameterList".equals(key))  {
            mode.paramList = Arrays.asList(_value.split(","));
        }
    }

    /**
     * Returns the description of parameters which defines mode.
     *
     * @return description of parameter
     * @see ParameterValues_mxJPO#paramDesc
     */
    public String getParameterDesc()
    {
        return MAP.get(this).getParameterDesc();
    }

    /**
     * Returns the list of parameters which defines this mode.
     *
     * @return list of parameter strings
     * @see ParameterValues_mxJPO#paramList
     */
    public Collection<String> getParameterList()
    {
        return MAP.get(this).getParameterList();
    }
}