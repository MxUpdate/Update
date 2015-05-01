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

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * In MX only one search index exists. Therefore the list only includes
 * {@value #DEFAULT_NAME} as valid MX name.
 *
 * @author The MxUpdate Team
 */
public class MxNamesAdminSystemSearchIndex_mxJPO
    implements IFetchMxNames_mxJPO
{
    /** Used default name of the search configuration. */
    private final static String DEFAULT_NAME = "Config"; //$NON-NLS-1$

    @Override()
    public SortedSet<String> fetch(final ParameterCache_mxJPO _paramCache,
                                   final TypeDef_mxJPO _typeDef)
    {
        final SortedSet<String> names = new TreeSet<String>();
        names.add(MxNamesAdminSystemSearchIndex_mxJPO.DEFAULT_NAME);
        return names;
    }
}
