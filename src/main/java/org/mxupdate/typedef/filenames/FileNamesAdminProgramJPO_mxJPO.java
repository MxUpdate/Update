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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.program.JPOProgram_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.FileUtil_mxJPO;

public class FileNamesAdminProgramJPO_mxJPO
    extends FileNamesAdmin_mxJPO
{

    /**
     * Regular expression for the package line. The package name must be
     * extracted to get the real name of the JPO used within MX.
     */
    private static final Pattern PATTERN_JPO_PACKAGE = Pattern.compile("(?<=package)[ \\t]+[A-Za-z0-9\\._]*[ \\t]*;");

    /**
     * {@inheritDoc}
     *
     */
    @Override()
    public SortedMap<String,File> match(final ParameterCache_mxJPO _paramCache,
                                        final TypeDef_mxJPO _typeDef,
                                        final Collection<File> _files,
                                        final Collection<String> _matches)
        throws UpdateException_mxJPO
    {
        final SortedMap<String,File> ret = super.match(_paramCache, _typeDef, _files, _matches);

        for (final File file : _files)  {
            if (file.getName().endsWith(JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION))  {
                // file identified as JPO

                final String code = FileUtil_mxJPO.readFileToString(file);
                String mxName = file.getName().substring(0, file.getName().length() - JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION_LENGTH);

                // prefix with package name
                for (final String line : code.split("\n"))  {
                    final Matcher pckMatch = FileNamesAdminProgramJPO_mxJPO.PATTERN_JPO_PACKAGE.matcher(line);
                    if (pckMatch.find())  {
                        mxName = pckMatch.group().replace(';', ' ').trim() + "." + mxName;
                        break;
                    }
                }

                if (!ret.containsKey(mxName) && this.matchMxName(_paramCache, mxName, _matches))  {
                    ret.put(mxName, file);
                }
            }
        }
        return ret;
    }
}
