/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.plugin;

import java.io.File;

import matrix.db.Context;

import org.mxupdate.mapping.Mapping_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to return for a MxUpdate file name the related
 * MxUpdate script (or for programs the related source code).
 *
 * @author Tim Moxter
 * @version $Id$
 */
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
        Mapping_mxJPO.init(_context);

        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true);

        final File file = new File(_args[0]);

        // first found related type definition
        AbstractObject_mxJPO instance = null;
        for (final TypeDef_mxJPO typeDef : TypeDef_mxJPO.values())  {
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
            for (final TypeDef_mxJPO typeDef : TypeDef_mxJPO.values())  {
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
        paramCache.logInfo(cmd.toString());
    }
}
