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

import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Fetches the names for  for all business object of current type definition.
 * The revision of the business object is appended to the name of the business
 * object split by {@link BusObject_mxJPO#SPLIT_NAME}. If types which are
 * derived from original type are used, the type is the prefix of the name with
 * the prefix {@link BusObject_mxJPO#SPLIT_TYPE}.
 *
 * @author The MxUpdate Team
 */
public class MxNamesBusObject_mxJPO
    implements IMatcherMxNames_mxJPO
{
    @Override()
    public SortedSet<String> match(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef,
                                   final Collection<String> _matches)
        throws MatrixException
    {
        final StringList selects = new StringList(3);
        selects.addElement("type");
        selects.addElement("name");
        selects.addElement("revision");

        final Query query = new Query();
        query.open(_paramCache.getContext());
        query.setBusinessObjectType(_typeDef.getMxBusType());
        final BusinessObjectWithSelectList list = query.select(_paramCache.getContext(), selects);
        query.close(_paramCache.getContext());

        final SortedSet<String> ret = new TreeSet<String>();
        for (final Object mapObj : list)  {
            final BusinessObjectWithSelect map = (BusinessObjectWithSelect) mapObj;
            final String busType = (String) map.getSelectDataList("type").get(0);
            final String busName = (String) map.getSelectDataList("name").get(0);
            final String busRevision = (String) map.getSelectDataList("revision").get(0);

            if (StringUtil_mxJPO.match(busName, _matches) || StringUtil_mxJPO.match(busRevision, _matches))  {
                final StringBuilder name = new StringBuilder();
                if (_typeDef.hasMxBusTypeDerived())  {
                    name.append(busType).append(BusObject_mxJPO.SPLIT_TYPE);
                }
                name.append(busName);
                if ((busRevision != null) && !busRevision.isEmpty())  {
                    name.append(BusObject_mxJPO.SPLIT_NAME).append(busRevision);
                }
                ret.add(name.toString());
            }
        }
        return ret;
    }
}
