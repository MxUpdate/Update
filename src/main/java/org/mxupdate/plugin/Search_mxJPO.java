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

package org.mxupdate.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Eclipse plug-in method to search for configuration items.
 *
 * @author The MxUpdate Team
 */
class Search_mxJPO
    extends AbstractPlugin_mxJPO
{
    /** Argument key for the match string. */
    private static final String ARGUMENT_KEY_MATCH = "Match"; //$NON-NLS-1$
    /** Argument key for the type definition list. */
    private static final String ARGUMENT_KEY_TYPEDEFLIST = "TypeDefList"; //$NON-NLS-1$
    /** Key in the map for the name of the type definition of found one configuration item.. */
    private static final String RETURN_KEY_TYPEDEF = "TypeDef"; //$NON-NLS-1$
    /** Key in the map for the name of found one configuration item. */
    private static final String RETURN_KEY_NAME = "Name"; //$NON-NLS-1$
    /** Key in the map for the file name of found one configuration item. */
    private static final String RETURN_KEY_FILENAME = "FileName"; //$NON-NLS-1$
    /** Key in the map for the file path of found one configuration item. */
    private static final String RETURN_KEY_FILEPATH = "FilePath"; //$NON-NLS-1$

    /**
     * <p>Main method to execute the search for configuration items.</p>
     * <p>The <code>_arguments</code> are using following two keys
     * <ul>
     * <li>{@link #ARGUMENT_KEY_MATCH}: match for the name of configuration
     *     item</li>
     * <li>{@link #ARGUMENT_KEY_TYPEDEFLIST}: type definition list</li>
     * </ul>
     * </p>
     * <p>The returned packed values (packed with method
     * {@link AbstractPlugin_mxJPO#prepareReturn(String, String, Exception, Object)})
     * includes a collection of found information as map. The found information
     * map defines four keys:
     * <ul>
     * <li>{@link #RETURN_KEY_TYPEDEF}:     type definition of the
     *                                      configuration item</li>
     * <li>{@link #RETURN_KEY_NAME}:        name of the configuration item</li>
     * <li>{@link #RETURN_KEY_FILENAME}:    file name of the configuration
     *                                      item</li>
     * <li>{@link #RETURN_KEY_FILEPATH}:    path within the file directory of
     *                                      the configuration item (without
     *                                      file name)</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    map with all search arguments
     * @return found configuration items matching <code>_arguments</code>
     * @throws Exception if the search failed
     */
    List<Map<String,String>> execute(final ParameterCache_mxJPO _paramCache,
                                     final Map<String,Object> _arguments)
        throws Exception
    {
        final Set<String> typeDefList = this.<Set<String>>getArgument(_arguments, Search_mxJPO.ARGUMENT_KEY_TYPEDEFLIST, new HashSet<String>());
        final String match            = this.<String>getArgument(_arguments, Search_mxJPO.ARGUMENT_KEY_MATCH, "");

        final List<Map<String,String>> ret = new ArrayList<Map<String,String>>();

        // first sort the matches depending on the type definition
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
            if (typeDefList.contains(typeDef.getName()))  {
                for (final String name : typeDef.matchMxNames(_paramCache, Arrays.asList(new String[]{match})))  {
                    final AbstractObject_mxJPO  instance = typeDef.newTypeInstance(name);
                    final Map<String,String> map = new HashMap<String,String>();
                    map.put(Search_mxJPO.RETURN_KEY_TYPEDEF, typeDef.getName());
                    map.put(Search_mxJPO.RETURN_KEY_NAME, name);
                    map.put(Search_mxJPO.RETURN_KEY_FILENAME, instance.getFileName());
                    map.put(Search_mxJPO.RETURN_KEY_FILEPATH, instance.getTypeDef().getFilePath());
                    ret.add(map);
                }
            }
        }

        return ret;
    }
}
