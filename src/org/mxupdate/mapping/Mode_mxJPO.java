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
 * Enumeration of all modes which are supported by the MxUpdate.
 *
 * @author Tim Moxter
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

    /**
     * Maps from the name of the type definition group to the related type
     * definition group instance.
     *
     * @see Mode_mxJPO#defineValue(String, String)
     */
    private final static Map<Mode_mxJPO,AbstractValue_mxJPO> MAP
            = new HashMap<Mode_mxJPO,AbstractValue_mxJPO>();

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
     * @throws Exception if the key is not known
     * @see AbstractValue_mxJPO
     */
    protected static void defineValue(final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        final Mode_mxJPO modeEnum = valueOf(enumName);
        AbstractValue_mxJPO mode = MAP.get(modeEnum);
        if (mode == null)  {
            mode = new AbstractValue_mxJPO(enumName);
            MAP.put(modeEnum, mode);
        }

        mode.defineValues(key, _value);
    }

    /**
     * Returns the description of parameters which defines mode.
     *
     * @return description of parameter
     * @see AbstractValue_mxJPO#paramDesc
     */
    public String getParameterDesc()
    {
        return MAP.get(this).getParameterDesc();
    }

    /**
     * Returns the list of parameters which defines this mode.
     *
     * @return list of parameter strings
     * @see AbstractValue_mxJPO#paramList
     */
    public Collection<String> getParameterList()
    {
        return MAP.get(this).getParameterList();
    }
}