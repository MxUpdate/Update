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

package org.mxupdate.typedef;

/**
 * Enumeration for MX admin object used as work arround.
 *
 * @author The MxUpdate Team
 */
public enum EMxAdmin_mxJPO
{
    Attribute,
    Interface,
    PathType,
    Relationship,
    Type;

    /**
     * Returns the MX class name for given MX admin object..
     *
     * @return MX class name
     */
    public String mxClass()
    {
        return this.name().toLowerCase();
    }
}
