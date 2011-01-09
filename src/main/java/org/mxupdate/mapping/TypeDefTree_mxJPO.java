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

package org.mxupdate.mapping;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tree hierarchy of the type definitions used for the eclipse plug-in.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public final class TypeDefTree_mxJPO
{
    /**
     * Sub key property for label of type definition trees.
     */
    private static final String SUBKEY_LABEL = "Label";

    /**
     * Sub key property for the list of sub type definition trees.
     */
    private static final String SUBKEY_SUBTREES = "SubTypeDefTreeList";

    /**
     * Sub key property for the list of type definitions.
     */
    private static final String SUBKEY_TYPEDEFS = "TypeDefList";

    /**
     * Holds the name of the type definition tree.
     *
     * @see #getName()
     * @see #AbstractValue_mxJPO(String)
     */
    private final String name;

    /**
     * Holds the label of the type definition tree.
     *
     * @see #getLabel()
     */
    private String label;

    /**
     * Name list of type definition instances for which this type definition
     * tree is defined.
     *
     * @see #getSubTypeDefTreeList()
     * @see #defineValue(Mapping_mxJPO, String, String)
     */
    private Collection<String> subTypeDefTreeList;

    /**
     * Name list of type definition instances for which this type definition
     * tree is defined.
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
    private TypeDefTree_mxJPO(final String _name)
    {
        this.name = _name;
    }

    /**
     * Defines the values of the type definition tree.
     *
     * @param _mapping  cache for all mapping
     * @param _key      key of the type definition tree (including the name of
     *                  type definition tree and the kind of the type
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

        TypeDefTree_mxJPO tree = _mapping.getTypeDefTreeMap().get(enumName);
        if (tree == null)  {
            tree = new TypeDefTree_mxJPO(enumName);
            _mapping.getTypeDefTreeMap().put(enumName, tree);
        }

        if (TypeDefTree_mxJPO.SUBKEY_LABEL.endsWith(key))  {
            tree.label = _value;
        } else if (TypeDefTree_mxJPO.SUBKEY_SUBTREES.endsWith(key))  {
            tree.subTypeDefTreeList = Arrays.asList(_value.split(","));
        } else if (TypeDefTree_mxJPO.SUBKEY_TYPEDEFS.endsWith(key))  {
            tree.typeDefList = Arrays.asList(_value.split(","));
        } else  {
            throw new Exception("unknown key " + _key + " with value '" + _value + "' defined!");
        }
    }

    /**
     * Returns the {@link #name} of the type definition tree.
     *
     * @return name
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the {@link #label} of the type definition tree.
     *
     * @return label of the type definition tree
     * @see #label
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     * Returns the name list of sub type definition trees.
     *
     * @return name list of sub type definition trees
     * @see #subTypeDefTreeList
     */
    public Collection<String> getSubTypeDefTreeList()
    {
        return this.subTypeDefTreeList;
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
