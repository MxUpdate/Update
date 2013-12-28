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

package org.mxupdate.mapping;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tree hierarchy of the type definitions used for the eclipse plug-in.
 *
 * @author The MxUpdate Team
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
