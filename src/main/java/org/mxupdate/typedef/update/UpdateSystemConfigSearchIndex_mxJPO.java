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

package org.mxupdate.typedef.update;

import java.io.File;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

/**
 * Updates given admin system config system search index.
 *
 * @author The MxUpdate Team
 */
public class UpdateSystemConfigSearchIndex_mxJPO
    implements IUpdate_mxJPO
{
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final TypeDef_mxJPO _typeDef,
                       final boolean _create,
                       final String _name,
                       final File _file)
        throws Exception
    {
        MqlBuilderUtil_mxJPO.mql()
                .cmd("escape set system searchindex file ").arg(_file.toString())
                .exec(_paramCache);
    }
}
