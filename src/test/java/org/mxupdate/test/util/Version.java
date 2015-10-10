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

/**
 * MX Versions to define not supported test data.
 *
 * @author The MxUpdate Team
 */
public enum Version
{
    /** MX version V6R2011x. */
    V6R2011x(1),
    /** MX version V6R2012x. */
    V6R2012x(2),
    /** MX version V6R2013x. */
    V6R2013x(3),
    /** MX version V6R2014x. */
    V6R2014x(4),
    /** MX version V6R2015x. */
    V6R2015x(5),
    /** MX version V6R2016x. */
    V6R2016x(6);

    /** Index. */
    private final int idx;

    /**
     * Constructor.
     *
     * @param _idx  index
     */
    private Version(final int _idx)
    {
        this.idx = _idx;
    }

    /**
     * Checks if this version has maximum {@code _version}.
     *
     * @param _version  maximum version
     * @return <i>true</i> if this version is greater or equal;
     *         otherwise <i>false</i>
     */
    public boolean max(final Version _version)
    {
        return (this.idx <= _version.idx);
    }

    /**
     * Checks if this version has minimum {@code _version}.
     *
     * @param _version  minimum version
     * @return <i>true</i> if this version is less or equal;
     *         otherwise <i>false</i>
     */
    public boolean min(final Version _version)
    {
        return (this.idx >= _version.idx);
    }
}
