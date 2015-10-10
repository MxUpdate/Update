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

import matrix.util.MatrixException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

/**
 * Fetches all admin persons and removes the persons for which a business object
 * exists.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminUserPersonAdmin_mxJPO
    extends MxNamesAdmin_mxJPO
{
    @Override()
    public SortedSet<String> match(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef,
                                   final Collection<String> _matches)
        throws MatrixException
    {
        final SortedSet<String> ret = super.match(_paramCache, _typeDef, _matches);

        final String personStr = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape temp query bus ").arg(_typeDef.getMxBusType()).cmd(" ").arg("*").cmd(" ").arg("*")
                .cmd(" select ").arg("name")
                .cmd(" dump ").arg("\t")
                .cmd(" recordsep ").arg("\n")
                .exec(_paramCache);
        for (final String lineStr : personStr.split("\n"))  {
            final String[] lineArr = lineStr.split("\t");
            ret.remove(lineArr[3]);
        }

        return ret;
    }
}
