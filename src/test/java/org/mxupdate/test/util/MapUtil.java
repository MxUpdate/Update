/*
 * Copyright 2008-2011 The MxUpdate Team
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

package org.mxupdate.test.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for easy handling of maps.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public final class MapUtil
{
    /**
     * Private constructor so that the utility class could not be initalized.
     */
    private MapUtil()
    {
    }

    /**
     * Combines <code>_map1</code> and <code>_map2</code> to one map. The maps
     * are only combined if they are not <code>null</code>.
     *
     * @param <S>       key class of the map
     * @param <T>       value class of the map
     * @param _map1     first map
     * @param _map2     second map
     * @return combined map
     */
    public static <S,T> Map<S,T> combine(final Map<S,T> _map1,
                                         final Map<S,T> _map2)
    {
        final Map<S,T> ret = new HashMap<S,T>();
        if (_map1 != null)  {
            ret.putAll(_map1);
        }
        if (_map2 != null)  {
            ret.putAll(_map2);
        }
        return ret;
    }
}
