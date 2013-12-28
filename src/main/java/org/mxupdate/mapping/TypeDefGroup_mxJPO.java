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
 * Groups the type definition enumeration used for the parameter definitions.
 *
 * @author The MxUpdate Team
 */
public final class TypeDefGroup_mxJPO
        extends AbstractValue_mxJPO
{
    /**
     * Name list of type definition instances for which this type definition
     * group is defined.
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
    private TypeDefGroup_mxJPO(final String _name)
    {
        super(_name);
    }

    /**
     * Defines the values of the type definition group.
     *
     * @param _mapping  cache for all mapping
     * @param _key      key of the type definition group (including the name of
     *                  type definition group and the kind of the type
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

        TypeDefGroup_mxJPO group = _mapping.getTypeDefGroupMap().get(enumName);
        if (group == null)  {
            group = new TypeDefGroup_mxJPO(enumName);
            _mapping.getTypeDefGroupMap().put(enumName, group);
        }

        if ("TypeDefList".endsWith(key))  {
            group.typeDefList = Arrays.asList(_value.split(","));
        } else  {
            group.defineValues(key, _value);
        }
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
