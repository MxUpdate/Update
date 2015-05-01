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

package org.mxupdate.typedef.filenames;

import java.io.File;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 *
 *
 * @author The MxUpdate Team
 */
public class FileNamesAdmin_mxJPO
    implements IMatcherFileNames_mxJPO
{
    @Override()
    public SortedMap<String,File> match(final ParameterCache_mxJPO _paramCache,
                                        final TypeDef_mxJPO _typeDef,
                                        final Collection<File> _files,
                                        final Collection<String> _matches)
        throws UpdateException_mxJPO
    {
        final SortedMap<String,File> ret = new TreeMap<String,File>();

        for (final File file : _files)  {
            final String mxName = this.evalMxName(_paramCache, _typeDef, file);
            if ((mxName != null)  && this.matchMxName(_paramCache, mxName, _matches))  {
                ret.put(mxName, file);
            }
        }

        return ret;
    }

    /**
     * Checks if given MX name without prefix and suffix matches given match
     * string.
     *
     * @param _paramCache   parameter cache
     * @param _mxName       name of the administration object to check
     * @param _matches      list of string which must be matched (if
     *                      {@code null}, name matched!)
     * @return <i>true</i> if the given MX name matches; otherwise <i>false</i>
     */
    protected boolean matchMxName(final ParameterCache_mxJPO _paramCache,
                                  final String _mxName,
                                  final Collection<String> _matches)
    {
        return StringUtil_mxJPO.match(_mxName, _matches);
    }

    /**
     * Extracts the MX name from given file name if the file prefix and suffix
     * matches. If the file prefix and suffix not matches a <code>null</code>
     * is returned.
     *
     * @param _paramCache   parameter cache
     * @param _file         file for which the MX name is searched
     * @return MX name or <code>null</code> if the file is not an update file
     *         for current type definition
     * @throws UpdateException_mxJPO if the configuration item name could not
     *                               be extracted from the file name
     */
    protected String evalMxName(final ParameterCache_mxJPO _paramCache,
                                final TypeDef_mxJPO _typeDef,
                                final File _file)
        throws UpdateException_mxJPO
    {
        final String suffix = _typeDef.getFileSuffix();
        final int suffixLength = (suffix != null) ? suffix.length() : 0;

        final String prefix = _typeDef.getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;

        final String fileName = _file.getName();
        final String mxName;
        if (((prefix == null) || fileName.startsWith(prefix)) && ((suffix == null) || fileName.endsWith(suffix)))  {
            mxName = StringUtil_mxJPO.convertFromFileName(fileName.substring(0, fileName.length() - suffixLength).substring(prefixLength));
        } else  {
            mxName = null;
        }
        return mxName;
    }
}
