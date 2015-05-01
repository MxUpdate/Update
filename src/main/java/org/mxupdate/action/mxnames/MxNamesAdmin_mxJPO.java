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

package org.mxupdate.action.mxnames;

import java.util.SortedSet;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Searches for all administration object of given type definition and
 * returns all found names as set.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdmin_mxJPO
    implements IFetchMxNames_mxJPO
{
    @Override()
    public SortedSet<String> fetch(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef)
        throws MatrixException
    {
        final String listStr = MqlBuilder_mxJPO.mql()
                .cmd("escape list ").cmd(_typeDef.getMxAdminName()).cmd(" ").arg("*").cmd(" ").cmd(_typeDef.getMxAdminSuffix())
                .exec(_paramCache);

        final SortedSet<String> ret = new TreeSet<String>();
        if (!listStr.isEmpty())  {
            for (final String name : listStr.split("\n"))  {
                ret.add(name);
            }
        }
        return ret;
    }
}
