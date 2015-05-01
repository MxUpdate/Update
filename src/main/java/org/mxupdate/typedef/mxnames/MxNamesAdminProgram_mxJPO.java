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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.CacheKey;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Searches for all programs objects depending on the program kind.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminProgram_mxJPO
    implements IMatcherMxNames_mxJPO
{
    /** Key used for the select statement. */
    private static final String SELECT_KEY = "@@@2@@@2@@@";

    @Override()
    public SortedSet<String> match(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef,
                                   final Collection<String> _matches)
        throws MatrixException
    {
        @SuppressWarnings("unchecked")
        Map<String,SortedSet<String>> progs = (Map<String,SortedSet<String>>) _paramCache.getCache(CacheKey.Programs);

        if (progs == null)  {
            // prepare MQL statement
            final MqlBuilder mql = MqlBuilder_mxJPO.mql()
                    .cmd("escape list program ").arg("*").cmd(" select ").arg("name")
                            .cmd(" ").arg("isjavaprogram")
                            .cmd(" ").arg("iseklprogram")
                            .cmd(" ").arg("ismqlprogram")
                            .cmd(" dump ").arg(MxNamesAdminProgram_mxJPO.SELECT_KEY);

            // prepare list of programs
            progs = new HashMap<String,SortedSet<String>>();
            progs.put("java", new TreeSet<String>());
            progs.put("ekl",  new TreeSet<String>());
            progs.put("mql",  new TreeSet<String>());

            // evaluate list of programs
            for (final String lineStr : mql.exec(_paramCache).split("\n"))  {
                final String[] lineArr = lineStr.split(MxNamesAdminProgram_mxJPO.SELECT_KEY);
                if ("TRUE".equals(lineArr[1]))  {
                    progs.get("java").add(lineArr[0]);
                } else if ("TRUE".equals(lineArr[2]))  {
                    progs.get("ekl").add(lineArr[0]);
                } else if ("TRUE".equals(lineArr[3]))  {
                    progs.get("mql").add(lineArr[0]);
                }
            }
            _paramCache.setCache(CacheKey.Programs, progs);
        }

        // now prepare list of returned programs
        final SortedSet<String> ret;
        if (_matches == null)  {
            ret = progs.get(_typeDef.getMxUpdateKind());
        } else  {
            ret = new TreeSet<String>();
            for (final String mxName : progs.get(_typeDef.getMxUpdateKind()))  {
                if (StringUtil_mxJPO.match(mxName, _matches))  {
                    ret.add(mxName);
                }
            }
        }

        return ret;
    }
}
