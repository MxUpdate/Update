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

package org.mxupdate.update.datamodel;

import org.mxupdate.mapping.TypeDef_mxJPO;

/**
 * The class is used to evaluate information from real attributes within MX
 * used to export, delete and update a real attribute.
 *
 * @author The MxUpdate Team
 */
public class AttributeReal_mxJPO
    extends AbstractAttribute_mxJPO<AttributeReal_mxJPO>
{
    /**
     * Constructor used to initialize the real attribute instance with
     * related type definition and attribute name.
     *
     * @param _typeDef  defines the related type definition
     * @param _mxName   MX name of the real attribute object
     */
    public AttributeReal_mxJPO(final TypeDef_mxJPO _typeDef,
                               final String _mxName)
    {
        super(_typeDef, _mxName, Kind.Real);
    }
}
