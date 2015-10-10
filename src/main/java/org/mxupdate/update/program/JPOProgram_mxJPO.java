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

package org.mxupdate.update.program;

import org.mxupdate.typedef.TypeDef_mxJPO;

/**
 * The class is used to export, create, delete and update JPOs within MX.
 *
 * @author The MxUpdate Team
 */
public class JPOProgram_mxJPO
    extends AbstractProgram_mxJPO<JPOProgram_mxJPO>
{
    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the JPO object
     */
    public JPOProgram_mxJPO(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
    {
        super(Kind.JAVA, _typeDef, _mxName);
    }
}
