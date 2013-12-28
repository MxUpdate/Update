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

package org.mxupdate.plugin;

import java.io.File;

import matrix.db.Context;
import matrix.db.MatrixWriter;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to return for a MxUpdate file name the related
 * MxUpdate script (or for programs the related source code).<br/>
 * The class will be replaced by {@link Export_mxJPO}.
 *
 * @author The MxUpdate Team
 */
@Deprecated
public class GetMxUpdateCode_mxJPO
{
    /**
     * Main method to return for given MX update file name the stored MxUpdate
     * information.
     *
     * @param _context  MX context for this request
     * @param _args     first index of the arguments defined the file to fetch
     *                  the MxUpdate
     * @throws Exception if the MxUpdate script could not be returned
     */
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true);

        final File file = new File(_args[0]);

        // first found related type definition
        AbstractObject_mxJPO instance = null;
        for (final TypeDef_mxJPO typeDef : paramCache.getMapping().getAllTypeDefsSorted())  {
            if (!typeDef.isFileMatchLast())  {
                instance = typeDef.newTypeInstance(null);
                final String mxName = instance.extractMxName(paramCache, file);
                if (mxName != null)  {
                    instance = typeDef.newTypeInstance(mxName);
                    break;
                } else  {
                    instance = null;
                }
            }
        }
        if (instance == null)  {
            for (final TypeDef_mxJPO typeDef : paramCache.getMapping().getAllTypeDefsSorted())  {
                if (typeDef.isFileMatchLast())  {
                    instance = typeDef.newTypeInstance(null);
                    final String mxName = instance.extractMxName(paramCache, file);
                    if (mxName != null)  {
                        instance = typeDef.newTypeInstance(mxName);
                        break;
                    } else  {
                        instance = null;
                    }
                }
            }
        }

        final StringBuilder cmd = new StringBuilder();
        instance.export(paramCache, cmd);
        final MatrixWriter writer = new MatrixWriter(_context);
        writer.write(cmd.toString());
        writer.flush();
        writer.close();
    }
}
