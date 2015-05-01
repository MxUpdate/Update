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

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Interface definition used to evaluate MX names for given files.
 *
 * @author The MxUpdate Team
 */
public interface IMatcherFileNames_mxJPO
{
    /**
     * Extract for given {@code _files} all MX names which {@code _matches} for
     * given {@code _typeDef}.
     *
     * @param _paramCache   parameter cache
     * @param _typeDef      type definition
     * @param _files        files to test
     * @param _matches      matches to fulfill
     * @return map with MX name and depending file
     * @throws UpdateException_mxJPO
     */
    SortedMap<String,File> match(final ParameterCache_mxJPO _paramCache,
                                          final TypeDef_mxJPO _typeDef,
                                          final Collection<File> _files,
                                          final Collection<String> _matches)
        throws UpdateException_mxJPO;
}
