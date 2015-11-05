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

package org.mxupdate.util;

/**
 * String utility.
 *
 * @author The MxUpdate Team
 */
public final class StringUtils_mxJPO
{
    /**
     * Private constructor to avoid external initialization.
     */
    private StringUtils_mxJPO()
    {
    }

    /**
     * Checks if {@code _toTest} is empty ("") or {@code null}.
     * <pre>
     * StringUtils_mxJPO.isEmpty(null)      = true
     * StringUtils_mxJPO.isEmpty("")        = true
     * StringUtils_mxJPO.isEmpty(" ")       = false
     * StringUtils_mxJPO.isEmpty("abc")     = false
     * StringUtils_mxJPO.isEmpty("  abc  ") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if {@code _toTest} is empty or {@code null}
     */
    public static boolean isEmpty(final CharSequence _toTest)
    {
        return (_toTest == null) || (_toTest.length() == 0);
    }
}
