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

package org.mxupdate.typedef.mxnames;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.CacheKey;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

import matrix.util.MatrixException;

/**
 * Specific implementation to list path types w/o local path type definitions.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminPathType_mxJPO
    implements IMatcherMxNames_mxJPO
{
    /** Key used for the select statement. */
    private static final String SELECT_KEY = "@@@2@@@2@@@";

    @Override
    public SortedSet<String> match(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef,
                                   final Collection<String> _matches)
        throws MatrixException
    {
        @SuppressWarnings("unchecked")
        SortedSet<String> pathTypes = (SortedSet<String>) _paramCache.getCache(CacheKey.PathTypes);

        if (pathTypes == null)  {
            final String pathTypeStr = MqlBuilderUtil_mxJPO.mql()
                    .cmd("escape list pathtype ").arg("*").cmd(" ")
                            .cmd("select ").arg("name").cmd(" ").arg("owner").cmd(" ")
                            .cmd("dump ").arg(MxNamesAdminPathType_mxJPO.SELECT_KEY)
                            .exec(_paramCache.getContext());

            pathTypes = new TreeSet<>();
            if (!pathTypeStr.isEmpty())  {
                for (final String nameOwnerStr : pathTypeStr.split("\n"))  {
                    final String[] nameOwnerArr = nameOwnerStr.split(MxNamesAdminPathType_mxJPO.SELECT_KEY);
                    if ((nameOwnerArr.length < 2) || nameOwnerArr[1].isEmpty())  {
                        pathTypes.add(nameOwnerArr[0]);
                    }
                }
            }

            _paramCache.setCache(CacheKey.PathTypes, pathTypes);
        }

        // now prepare list of returned path types
        final SortedSet<String> ret;
        if (_matches == null)  {
            ret = pathTypes;
        } else  {
            ret = new TreeSet<>();
            for (final String mxName : pathTypes)  {
                if (StringUtil_mxJPO.match(mxName, _matches))  {
                    ret.add(mxName);
                }
            }
        }

        return ret;
    }
}
