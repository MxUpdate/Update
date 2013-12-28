/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.test.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for easy handling of maps.
 *
 * @author The MxUpdate Team
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
