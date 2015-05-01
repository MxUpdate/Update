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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to export configuration items (administration
 * objects) or in other words with the class the TCL update file could be
 * created depending on the configuration item type (administration type).
 *
 * @author The MxUpdate Team
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
            final Collection<File> files = Arrays.asList(new File[]{new File(fileName)});

            // first found related type definition
            for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {

                final Map<String,File> matchFiles = typeDef.matchFileNames(_paramCache, files);

                if (!matchFiles.isEmpty())  {
                    instance = typeDef.newTypeInstance(matchFiles.keySet().iterator().next());
                    break;
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
