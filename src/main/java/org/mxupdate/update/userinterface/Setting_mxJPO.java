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

package org.mxupdate.update.userinterface;

/**
 * Handles one setting within a user interface object.
 *
 * @author The MxUpdate Team
 */
class Setting_mxJPO
{
    /**
     * Name of the setting.
     */
    String name = null;

    /**
     * Value of the setting.
     */
    String value = null;

    /**
     * {@inheritDoc}
     * The string representation includes the {@link #name} and the
     * {@link #value}.
     */
    @Override()
    public String toString()
    {
        return "[name=" + this.name + ", value=" + this.value + "]";
    }
}
