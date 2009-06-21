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

import org.mxupdate.update.util.ParameterCache_mxJPO;

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
     * Defines the values of the mode enumerations.
     *
     * @param _mapping  cache for all mapping
     * @param _key      key with name of enumeration and (separated by a point)
     *                  the key
     * @param _value    value which must be set
     * @throws Exception if the key is not known
     * @see AbstractValue_mxJPO
     */
    protected static void defineValue(final Mapping_mxJPO _mapping,
                                      final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "").toUpperCase();
        final String key = _key.substring(enumName.length() + 1);

        final Mode_mxJPO modeEnum = Mode_mxJPO.valueOf(enumName);
        AbstractValue_mxJPO mode = _mapping.getModeMap().get(modeEnum);
        if (mode == null)  {
            mode = new AbstractValue_mxJPO(enumName);
            _mapping.getModeMap().put(modeEnum, mode);
        }

        mode.defineValues(key, _value);
    }

    /**
     * Returns the description of parameters which defines mode.
     *
     * @param _paramCache   for which parameter cache must the parameter
     *                      description returned
     * @return description of parameter
     * @see AbstractValue_mxJPO#paramDesc
     */
    public String getParameterDesc(final ParameterCache_mxJPO _paramCache)
    {
        return _paramCache.getMapping().getModeMap().get(this).getParameterDesc();
    }

    /**
     * Returns the list of parameters which defines this mode.
     *
     * @param _paramCache   for which parameter cache must the parameter
     *                      list returned
     * @return list of parameter strings
     * @see AbstractValue_mxJPO#paramList
     */
    public Collection<String> getParameterList(final ParameterCache_mxJPO _paramCache)
    {
        return _paramCache.getMapping().getModeMap().get(this).getParameterList();
    }
}