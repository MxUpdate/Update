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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import matrix.db.Context;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to export configuration items (administration
 * objects) or in other words with the class the TCL update file could be
 * created depending on the configuration item type (administration type).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Export_mxJPO
    extends AbstractPlugin_mxJPO
{
    /**
     * Name of the key in the returned mapped for the &quot;name&quot; of the
     * administration object (which administration object is exported).
     */
    private static final String RETURN_KEY_NAME = "name";

    /**
     * Name of the key in the returned mapped for the &quot;code&quot; of the
     * administration object (the TCL update code itself).
     */
    private static final String RETURN_KEY_CODE = "code";

    /**
     * Name of the key in the returned mapped for the &quot;path&quot; of the
     * administration object (where is the TCL update code located in the file
     * system).
     */
    private static final String RETURN_KEY_PATH = "path";

    /**
     * Name of the key in the returned mapped for the &quot;file name&quot; of
     * the administration object (what is the name of the file).
     */
    private static final String RETURN_KEY_FILENAME = "filename";

    /**
     * <p>All given administration objects are exported and the related update
     * file is returned. The administration objects to export in
     * <code>_args</code> are identified by the administration type (see
     * {@link TypeDef_mxJPO}) and the MX names of the administration objects.
     * </p>
     * <p>The returned packed values (packed with method
     * {@link AbstractPlugin_mxJPO#prepareReturn(String, String, Exception, Object)})
     * includes the logging messages itself and for configuration item type
     * a collection of exporting information as map. The exporting information
     * map defines three keys:
     * <ul>
     * <li>{@link #RETURN_KEY_NAME}:        name of the configuration item</li>
     * <li>{@link #RETURN_KEY_PATH}:        path within the file directory of
     *                                      the configuration item (without
     *                                      file name)</li>
     * <li>{@link #RETURN_KEY_CODE}:        TCL update source code of the
     *                                      configuration item</li>
     * <li>{@link #RETURN_KEY_FILENAME}:    file name of the configuration
     *                                      item</li>
     * </ul>
     *
     * @param _context  MX context for this request
     * @param _args     encoded arguments from the Eclipse plug-in:
     *                  <ul>
     *                  <li><b>{@link Map}&lt;{@link String},{@link Collection}
     *                         &lt;{@link String}&gt;&gt;</b>
     *                      <br/>a map depending on the (administration) type
     *                      (as key) and the related list of (administration)
     *                      MX names</li>
     *                  </ul>
     * @return packed return values in maps
     * @throws Exception if export failed
     * @see AbstractPlugin_mxJPO#prepareReturn(String, String, Exception, Object)
     */
    public Map<String,?> exportByName(final Context _context,
                                      final String... _args)
        throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true);
        final Map<String,Collection<String>> exports = this.<Map<String,Collection<String>>>decode(_args[0]);

        // export all objects depending on the type definitions
        final Map<String,Collection<Map<String,String>>> ret = new HashMap<String,Collection<Map<String,String>>>();
        for (final Map.Entry<String,Collection<String>> entry : exports.entrySet())  {
            final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef(entry.getKey());
            final Collection<Map<String,String>> extracts = new HashSet<Map<String,String>>();
            ret.put(entry.getKey(), extracts);
            for (final String mxName : entry.getValue())  {
                final AbstractObject_mxJPO instance = typeDef.newTypeInstance(mxName);
                final StringBuilder code = new StringBuilder();
                instance.export(paramCache, code);
                final Map<String,String> desc = new HashMap<String,String>();
                desc.put(Export_mxJPO.RETURN_KEY_NAME, instance.getName());
                desc.put(Export_mxJPO.RETURN_KEY_CODE, code.toString());
                desc.put(Export_mxJPO.RETURN_KEY_PATH, instance.getPath());
                desc.put(Export_mxJPO.RETURN_KEY_FILENAME, instance.getFileName());
                extracts.add(desc);
            }
        }

        return this.prepareReturn(paramCache.getLogString(),
                                  (String) null,
                                  (Exception) null,
                                  ret);
    }
}
