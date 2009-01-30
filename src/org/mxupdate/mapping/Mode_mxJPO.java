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
 * Enumeration of all modes which are supported by the MxUpdate.
 *
 * @author tmoxter
 * @version $Id$
 */
public enum Mode_mxJPO
{
    /**
     * Mode 'import' used to import defined administration objects from
     * file system into Matrix.
     */
    IMPORT,
    /**
     * Mode 'export' used to export defined administration objects from
     * Matrix into a file system.
     */
    EXPORT,
    /**
     * Mode 'delete' used to delete in Mx objects which are not defined
     * in the repository (file system).
     */
    DELETE,
    /**
     * Prints out the help description.
     */
    HELP;

    private static class ModeValue
    {
        /**
         * Maps from the name of the type definition group to the related type
         * definition group instance.
         *
         * @see Mode_mxJPO#defineValue(String, String)
         */
        private final static Map<Mode_mxJPO,ModeValue> MAP
                = new HashMap<Mode_mxJPO,ModeValue>();

        /**
         * Defines the parameter description.
         *
         * @see Mode_mxJPO#getParameterDesc()
         * @see Mode_mxJPO#defineValue(String, String)
         */
        private String paramDesc;

        /**
         * Defines the list of parameters.
         *
         * @see Mode_mxJPO#getParameterList()
         * @see Mode_mxJPO#defineValue(String, String)
         */
        private Collection<String> paramList;
    }

    /**
     * Resets type definition map.
     *
     * @see ModeValue#MAP
     */
    protected static void resetValues()
    {
        ModeValue.MAP.clear();
    }

    /**
     * Defines the values of the mode enumerations.
     *
     * @param _key      key with name of enumeration and (separated by a point)
     *                  the key
     * @param _value    value which must be set
     * @see ModeValue
     */
    protected static void defineValue(final String _key,
                                      final String _value)
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        final Mode_mxJPO modeEnum = valueOf(enumName);
        ModeValue mode = ModeValue.MAP.get(modeEnum);
        if (mode == null)  {
            mode = new ModeValue();
            ModeValue.MAP.put(modeEnum, mode);
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
     * @see ModeValue#paramDesc
     */
    public String getParameterDesc()
    {
        return ModeValue.MAP.get(this).paramDesc;
    }

    /**
     * Returns the list of parameters which defines this mode.
     *
     * @return list of parameter strings
     * @see ModeValue#paramList
     */
    public Collection<String> getParameterList()
    {
        return ModeValue.MAP.get(this).paramList;
    }
}