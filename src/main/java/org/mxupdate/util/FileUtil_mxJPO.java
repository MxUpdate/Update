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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * Utility for file.
 *
 * @author The MxUpdate Team
 */
public final class FileUtil_mxJPO
{
    /**
     * Private constructor to avoid initalize of this class.
     */
    private FileUtil_mxJPO()
    {
    }

    /**
     * Reads for given file the content and returns them.
     *
     * @param _file     file used to read
     * @return read content of the file
     * @throws UpdateException_mxJPO if the file could not be opened or read
     */
    public static String readFileToString(final File _file)
        throws UpdateException_mxJPO
    {
        // read code
        final StringBuilder code = new StringBuilder();
        try  {
            BufferedReader reader = null;
            try  {
                reader = new BufferedReader(new FileReader(_file));
            } catch (final FileNotFoundException e)  {
                throw new UpdateException_mxJPO(ErrorKey.UTIL_FILEUTIL_READ_FILE_NOT_EXISTS, _file);
            }
            String line = reader.readLine();
            while (line != null)  {
                code.append(line).append('\n');
                line = reader.readLine();
            }
            if (reader != null)  {
                reader.close();
            }
        } catch (final IOException e)  {
            throw new UpdateException_mxJPO(ErrorKey.UTIL_FILEUTIL_READ_FILE_UNEXPECTED, _file, e.getMessage());
        }

        return code.toString();
    }
}
