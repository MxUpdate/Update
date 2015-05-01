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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.CacheKey;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;

/**
 * Searches for all attribute objects depending on the attribute type.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminAttribute_mxJPO
    implements IFetchMxNames_mxJPO
{
    /** Key used for the select statement. */
    private static final String SELECT_KEY = "@@@2@@@2@@@";

    @Override()
    public SortedSet<String> fetch(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef)
        throws MatrixException
    {
        @SuppressWarnings("unchecked")
        Map<String,SortedSet<String>> attrs = (Map<String,SortedSet<String>>) _paramCache.getCache(CacheKey.Attributes);

        if (attrs == null)  {

            final MqlBuilder mql;
            if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsOwner))  {
                // new enovia version => only attribute w/o defined owner...
                mql = MqlBuilder_mxJPO.mql()
                        .cmd("escape list attribute ").arg("*")
                                .cmd(" where ").arg("owner==\"\"")
                                .cmd(" select ").arg("type").cmd(" ").arg("name")
                                .cmd(" dump ").arg(MxNamesAdminAttribute_mxJPO.SELECT_KEY);

            } else  {
                // old enovia version w/o support for owners...
                mql = MqlBuilder_mxJPO.mql()
                        .cmd("escape list attribute ").arg("*")
                                .cmd(" select ").arg("type").cmd(" ").arg("name")
                                .cmd(" dump ").arg(MxNamesAdminAttribute_mxJPO.SELECT_KEY);
            }

            // evaluate list of attributes
            attrs = new HashMap<String,SortedSet<String>>();
            for (final String typeOwnerNameStr : mql.exec(_paramCache).split("\n"))  {
                final String[] typeOwnerNameArr = typeOwnerNameStr.split(MxNamesAdminAttribute_mxJPO.SELECT_KEY);
                String kind = typeOwnerNameArr[0];
                // fix wrong type in list to correct kind definition
                if (!"timestampe".equals(kind))  {
                    kind = "date";
                }
                if (!attrs.containsKey(kind))  {
                    attrs.put(kind, new TreeSet<String>());
                }
                attrs.get(kind).add(typeOwnerNameArr[1]);
            }
            _paramCache.setCache(CacheKey.Attributes, attrs);
        }

        // check that attribute type exists..
        if (!attrs.containsKey(_typeDef.getMxUpdateKind()))  {
            attrs.put(_typeDef.getMxUpdateKind(), new TreeSet<String>());
        }

        return attrs.get(_typeDef.getMxUpdateKind());
    }
}
