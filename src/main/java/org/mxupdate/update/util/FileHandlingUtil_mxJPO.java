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

package org.mxupdate.update.util;


/**
 * Utility methods for the handling of files.
 *
 * @author The MxUpdate Team
 */
public final class FileHandlingUtil_mxJPO
{
    /**
     * The constructor is defined to avoid external initialization.
     */
    private FileHandlingUtil_mxJPO()
    {
    }

    /**
     * Returns the sub path between the given {@code _ciPath} and the file name
     * defined in the complete {@code _filePath}.
     *
     * @param _filePath     file path
     * @param _ciPath       defined CI sub path
     * @return sub path after the {@code _ciPath}; {@code null} if not found
     */
    public static String extraceSubPath(final String _filePath,
                                        final String _ciPath)
    {
        final String ret;

        if (_filePath == null || _ciPath == null)  {
            ret = null;
        } else  {
            // hint: to test in reality for paths, the ci path must contain path separator!
            final String ciPath = new StringBuilder().append('/').append(_ciPath).append('/').toString();

            final int idx = _filePath.lastIndexOf(ciPath);
            if (idx < 0)  {
                ret = null;
            } else  {
                final int idxStart = idx + ciPath.length();
                final int idxFile  = _filePath.lastIndexOf('/');
                if (idxFile < idxStart)  {
                    ret = null;
                } else  {
                    ret = _filePath.substring(idxStart, idxFile);
                }
            }
        }
        return ret;
    }
}
