/*
 * Copyright 2008-2011 The MxUpdate Team
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
import java.util.HashMap;
import java.util.Map;

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
class Export_mxJPO
    extends AbstractPlugin_mxJPO
{
    /**
     * Argument key for the items.
     */
    private static final String ARGUMENT_KEY_NAME = "Name"; //$NON-NLS-1$

    /**
     * Argument key for the type definition.
     */
    private static final String ARGUMENT_KEY_TYPEDEF = "TypeDef"; //$NON-NLS-1$

    /**
     * Argument key for the file name.
     */
    private static final String ARGUMENT_KEY_FILENAME = "FileName"; //$NON-NLS-1$

    /**
     * Name of the key in the returned mapped for the &quot;Name&quot; of the
     * administration object (which administration object is exported).
     */
    private static final String RETURN_KEY_NAME = "Name"; //$NON-NLS-1$

    /**
     * Name of the key in the returned mapped for the &quot;Code&quot; of the
     * administration object (the TCL update code itself).
     */
    private static final String RETURN_KEY_CODE = "Code"; //$NON-NLS-1$

    /**
     * Name of the key in the returned mapped for the &quot;FilePath&quot; of
     * the administration object (where is the TCL update code located in the
     * file system).
     */
    private static final String RETURN_KEY_PATH = "FilePath"; //$NON-NLS-1$

    /**
     * Name of the key in the returned mapped for the &quot;FileName&quot; of
     * the administration object (what is the name of the file).
     */
    private static final String RETURN_KEY_FILENAME = "FileName"; //$NON-NLS-1$

    /**
     * Key in the map for the name of the type definition of found one
     * configuration item..
     */
    private static final String RETURN_KEY_TYPEDEF = "TypeDef"; //$NON-NLS-1$

    /**
     * <p>All given administration objects are exported and the related update
     * file is returned. The administration objects to export in
     * <code>_args</code> are identified by the administration type (see
     * {@link TypeDef_mxJPO}) and the MX names of the administration objects.
     * </p>
     * <p>The <code>_arguments</code> differs between two different possible
     * modes. The first possibility depends on the file name with the argument
     * key
     * <ul>
     * <li>{@link #ARGUMENT_KEY_FILENAME}: name of the file to export</li>
     * </ul>
     * The second possibility depends on the type definition and the name of
     * the configuration item with the argument keys
     * <ul>
     * <li>{@link #ARGUMENT_KEY_TYPEDEF}: type definition</li>
     * <li>{@link #ARGUMENT_KEY_NAME}: name of the configuration item</li>
     * </ul>
     * </p>
     * <p>The returned packed values (packed with method
     * {@link AbstractPlugin_mxJPO#prepareReturn(String, String, Exception, Object)})
     * includes the logging messages itself and for configuration item type
     * a map with exporting information. The exporting information map defines
     * four keys:
     * <ul>
     * <li>{@link #RETURN_KEY_TYPEDEF}:     type definition of the
     *                                      configuration item</li>
     * <li>{@link #RETURN_KEY_NAME}:        name of the configuration item</li>
     * <li>{@link #RETURN_KEY_PATH}:        path within the file directory of
     *                                      the configuration item (without
     *                                      file name)</li>
     * <li>{@link #RETURN_KEY_CODE}:        TCL update source code of the
     *                                      configuration item</li>
     * <li>{@link #RETURN_KEY_FILENAME}:    file name of the configuration
     *                                      item</li>
     * </ul></p>
     * <p>If an exception is thrown the exception is packed in the returned
     * map.</p>
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    map with all search arguments
     * @return packed return values in maps
     * @throws Exception if export failed
     * @see AbstractPlugin_mxJPO#prepareReturn(String, String, Exception, Object)
     */
    Map<String,String> execute(final ParameterCache_mxJPO _paramCache,
                               final Map<String,Object> _arguments)
        throws Exception
    {
        final String fileName = this.getArgument(_arguments, Export_mxJPO.ARGUMENT_KEY_FILENAME, null);

        AbstractObject_mxJPO instance = null;

        if (fileName != null)  {
            final File file = new File(fileName);

            // first found related type definition
            for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
                if (!typeDef.isFileMatchLast())  {
                    instance = typeDef.newTypeInstance(null);
                    final String mxName = instance.extractMxName(_paramCache, file);
                    if (mxName != null)  {
                        instance = typeDef.newTypeInstance(mxName);
                        break;
                    } else  {
                        instance = null;
                    }
                }
            }
            if (instance == null)  {
                for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
                    if (typeDef.isFileMatchLast())  {
                        instance = typeDef.newTypeInstance(null);
                        final String mxName = instance.extractMxName(_paramCache, file);
                        if (mxName != null)  {
                            instance = typeDef.newTypeInstance(mxName);
                            break;
                        } else  {
                            instance = null;
                        }
                    }
                }
            }
        } else  {
            // initialize arguments
            final String typeDefName = this.getArgument(_arguments, Export_mxJPO.ARGUMENT_KEY_TYPEDEF, null);
            final String item = this.getArgument(_arguments, Export_mxJPO.ARGUMENT_KEY_NAME, null);

            // export all objects depending on the type definitions
            final TypeDef_mxJPO typeDef = _paramCache.getMapping().getTypeDef(typeDefName);
            instance = typeDef.newTypeInstance(item);
        }

        // export code
        final Map<String,String> ret;
        if (instance == null)  {
            ret = null;
        } else  {
            final StringBuilder code = new StringBuilder();
            instance.export(_paramCache, code);

            ret = new HashMap<String,String>();
            ret.put(Export_mxJPO.RETURN_KEY_TYPEDEF,    instance.getTypeDef().getName());
            ret.put(Export_mxJPO.RETURN_KEY_NAME,       instance.getName());
            ret.put(Export_mxJPO.RETURN_KEY_CODE,       code.toString());
            ret.put(Export_mxJPO.RETURN_KEY_PATH,       instance.getPath());
            ret.put(Export_mxJPO.RETURN_KEY_FILENAME,   instance.getFileName());
        }

        return ret;
    }
}
