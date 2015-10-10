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

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;


/**
 * Utility methods for the handling of files.
 *
 * @author The MxUpdate Team
 */
public final class FileUtils_mxJPO
{
    /**
     * The constructor is defined to avoid external initialization.
     */
    private FileUtils_mxJPO()
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

    /**
     * Calculates the file name for type definition {@code _typeDef} with name
     * {@code _mxName}. The file name is a concatenation of the defined file
     * prefix within the type definition, the name of the MX object and the
     * file suffix within the type definition. All special characters are
     * converted automatically from
     * {@link StringUtil_mxJPO#convertToFileName(String)}.
     *
     * @param _typeDef      type definition
     * @param _mxName       name of ci object
     * @return file name of this administration (business) object
     */
    public static String calcCIFileName(final TypeDef_mxJPO _typeDef,
                                        final String _mxName)
    {
        final StringBuilder ret = new StringBuilder();
        if (_typeDef.getFilePrefix() != null)  {
            ret.append(_typeDef.getFilePrefix());
        }
        ret.append(_mxName);
        if (_typeDef.getFileSuffix() != null)  {
            ret.append(_typeDef.getFileSuffix());
        }
        return StringUtil_mxJPO.convertToFileName(ret.toString());
    }
}
