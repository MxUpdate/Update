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

package org.mxupdate.plugin;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.mapping.TypeDefTree_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Evaluates the list of all type definition tree and returns them to the
 * eclipse plug-in.
 *
 * @author The MxUpdate Team
 */
class TypeDefTreeList_mxJPO
    extends AbstractPlugin_mxJPO
{
    /**
     * Key in the map for one tree node entry for the label.
     */
    private static final String RETURN_VALUE_KEY_LABEL = "Label"; //$NON-NLS-1$

    /**
     * Key in the map for one tree node entry for the type definition tree
     * list.
     */
    private static final String RETURN_VALUE_KEY_TREE_LIST = "TypeDefTreeList"; //$NON-NLS-1$

    /**
     * Key in the map for one tree node entry for the type definition list.
     */
    private static final String RETURN_VALUE_KEY_TYPE_LIST = "TypeDefList"; //$NON-NLS-1$

    /**
     * Evaluates the type definition tree list used from the Eclipse plug-in.
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    ignored, because no arguments required
     * @return type definition tree packed in the map
     * @throws Exception if the encoding of the return value failed
     */
    Map<String,Map<String,?>> execute(final ParameterCache_mxJPO _paramCache,
                                      final Map<String,Object> _arguments)
        throws Exception
    {
        // prepare map of all trees
        final Map<String,Map<String,?>> allTrees = new HashMap<String,Map<String,?>>();
        for (final TypeDefTree_mxJPO tree : _paramCache.getMapping().getAllTypeDefTrees())  {
            final Map<String,Object> treeItem = new HashMap<String,Object>();
            treeItem.put(TypeDefTreeList_mxJPO.RETURN_VALUE_KEY_LABEL, tree.getLabel());
            treeItem.put(TypeDefTreeList_mxJPO.RETURN_VALUE_KEY_TREE_LIST, tree.getSubTypeDefTreeList());
            treeItem.put(TypeDefTreeList_mxJPO.RETURN_VALUE_KEY_TYPE_LIST, tree.getTypeDefList());
            allTrees.put(tree.getName(), treeItem);
        }

        return allTrees;
    }
}
