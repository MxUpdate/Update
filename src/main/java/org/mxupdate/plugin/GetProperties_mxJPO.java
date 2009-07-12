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

import java.io.Writer;

import matrix.db.Context;
import matrix.db.MatrixWriter;

import org.mxupdate.update.util.MqlUtil_mxJPO;


/**
 * The JPO class returns the plugin properties stored in MX. To get the
 * properties, following MQL statement must be executed:
 * <pre>
 * exec prog org.mxupdate.plugin.GetProperties
 * </pre>
 * The JPO returns the in MX stored plugin properties as string. The plugin
 * properties was prepared from
 * {@link org.mxupdate.install.InstallDataModel_mxJPO#makePluginProperty(org.mxupdate.update.util.ParameterCache_mxJPO,File)}
 * while the data model was installed.<br/>
 * The key of the properties are defined in this format:
 * <table border="1">
 * <tr><th>property key</th><th>description</th></tr>
 * <tr><td><code>[TypeDefName].FilePrefix</code></td><td>file prefix</td></tr>
 * <tr><td><code>[TypeDefName].FileSuffix</code></td><td>file suffix</td></tr>
 * <tr><td><code>[TypeDefName].Icon</code></td><td>Mime64 encoded Gif image
 *     </td></tr>
 * </table>
 * [TypeDefName] is the name of the type definition specified in the
 * mapping properties.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class GetProperties_mxJPO
{
    /**
     * This is the main method called from MQL. It writes the plugin properties
     * to the matrix writer (so that it could get via the Java MQL console).
     *
     * @param _context  MX context for this request
     * @param _args     arguments from MQL console (not used)
     * @throws Exception if the evaluate of the properties failed
     */
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        final String prop = MqlUtil_mxJPO.execMql(_context, new StringBuilder()
                .append("print prog 'org.mxupdate.plugin.plugin.properties' select code dump;"), true);

        final Writer writer = new MatrixWriter(_context);
        writer.append(prop);
        writer.flush();
        writer.close();
    }
}
