/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.util;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.UpdateCheck_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Utility class for updating configuration item.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public final class UpdateUtil_mxJPO
{
    /**
     * String of the key within the parameter cache that administration objects
     * compiled after they are updated.
     */
    private static final String PARAM_COMPILE = "Compile";

    /**
     * The constructor is defined so that no instance of the update utility
     * could be created.
     */
    private UpdateUtil_mxJPO()
    {
    }

    /**
     *
     * @param _paramCache       parameter cache
     * @param _clazz2names      depending on the type definition the related
     *                          files with MX name which must be updated
     * @param _versionInfo      defines how it is checked if an update is
     *                          required (see {@link UpdateCheck_mxJPO})
     * @throws Exception if update failed
     * @see UpdateCheck_mxJPO
     */
    public static void update(final ParameterCache_mxJPO _paramCache,
                              final Map<TypeDef_mxJPO,Map<File,String>> _clazz2names,
                              final UpdateCheck_mxJPO _versionInfo)
        throws Exception
    {
        // create if needed (and not in the list of existing objects)
        UpdateUtil_mxJPO.create(_paramCache, _clazz2names);

        // update
        final List<AbstractObject_mxJPO> compiles = new ArrayList<AbstractObject_mxJPO>();
        final boolean compile = _paramCache.getValueBoolean(UpdateUtil_mxJPO.PARAM_COMPILE);
        for (final TypeDef_mxJPO clazz : _paramCache.getMapping().getAllTypeDefs())  {
            final Map<File,String> clazzMap = _clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final AbstractObject_mxJPO instance = clazz.newTypeInstance(fileEntry.getValue());
                    _paramCache.logInfo("check "+instance.getTypeDef().getLogging()
                           + " '" + fileEntry.getValue() + "'");

                    final boolean update;
                    final String version = _paramCache.getValueBoolean(ParameterCache_mxJPO.KEY_FILEDATE2VERSION)
                                    ? Long.toString(fileEntry.getKey().lastModified() / 1000)
                                    : _paramCache.getValueString(ParameterCache_mxJPO.KEY_VERSION);
                    if (_versionInfo == UpdateCheck_mxJPO.FILEDATE)  {
                        final Date fileDate = new Date(fileEntry.getKey().lastModified());
                        final String instDateString = instance.getPropValue(_paramCache,
                                                                            PropertyDef_mxJPO.FILEDATE);
                        Date instDate;
                        if ((instDateString == null) || "".equals(instDateString))  {
                            instDate = null;
                        } else  {
                            try {
                                instDate = StringUtil_mxJPO.parseFileDate(_paramCache, instDateString);
                            } catch (final ParseException e) {
                                instDate = null;
                            }
                        }
                        if (fileDate.equals(instDate))  {
                            update = false;
                        } else  {
                            update = true;
                            _paramCache.logDebug("    - update to version from " + fileDate);
                        }
                    } else if (_versionInfo == UpdateCheck_mxJPO.VERSION)  {
                        final String instVersion = instance.getPropValue(_paramCache,
                                                                        PropertyDef_mxJPO.VERSION);
                        if (instVersion.equals(version))  {
                            update = false;
                        } else  {
                            update = true;
                            if (_paramCache.getValueBoolean(ParameterCache_mxJPO.KEY_FILEDATE2VERSION))  {
                                _paramCache.logDebug("    - update to version from "
                                       + new Date(fileEntry.getKey().lastModified()));
                            } else  {
                                _paramCache.logDebug("    - update to version " + version);
                            }
                        }
                    } else  {
                        update = true;
                        _paramCache.logDebug("    - update");
                    }
                    // execute update
                    if (update && UpdateUtil_mxJPO.updateOne(_paramCache, instance, fileEntry.getKey(), version) && compile)  {
                        compiles.add(instance);
                    }
                }
            }
        }

        // compile
        UpdateUtil_mxJPO.compile(_paramCache, compiles);
    }

    /**
     * Searches for given <code>_typeDefs</code> related existing CI's.
     *
     * @param _paramCache   parameter cache (used to get the MX context)
     * @param _typeDefs     set of type definitions for which existing CI's are
     *                      searched
     * @return found existing CI's
     * @throws Exception if existing CI's could not be found
     */
    protected static Map<TypeDef_mxJPO,Set<String>> getExistingCIs(final ParameterCache_mxJPO _paramCache,
                                                                   final Set<TypeDef_mxJPO> _typeDefs)
        throws Exception
    {
        final Map<TypeDef_mxJPO,Set<String>> existingNames = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final TypeDef_mxJPO clazz : _typeDefs)  {
            if (!existingNames.containsKey(clazz))  {
                final AbstractObject_mxJPO instance = clazz.newTypeInstance(null);
                existingNames.put(clazz, instance.getMxNames(_paramCache));
            }
        }
        return existingNames;
    }

    /**
     * Creates not existing CI's.
     *
     * @param _paramCache       parameter cache (used to get the MX context)
     * @param _clazz2names      depending on the type definition the related
     *                          files with MX name which must be updated
     * @throws Exception if create failed
     */
    protected static void create(final ParameterCache_mxJPO _paramCache,
                                 final Map<TypeDef_mxJPO,Map<File,String>> _clazz2names)
        throws Exception
    {
        // evaluate for existing administration objects
        final Map<TypeDef_mxJPO,Set<String>> existingNames = UpdateUtil_mxJPO.getExistingCIs(_paramCache, _clazz2names.keySet());

        // create if needed (and not in the list of existing objects)
        for (final TypeDef_mxJPO clazz : _paramCache.getMapping().getAllTypeDefs())  {
            final Map<File,String> clazzMap = _clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final Set<String> existings = existingNames.get(clazz);
                    if (!existings.contains(fileEntry.getValue()))  {
                        final AbstractObject_mxJPO instance = clazz.newTypeInstance(fileEntry.getValue());
                        _paramCache.logInfo("create "+instance.getTypeDef().getLogging() + " '" + fileEntry.getValue() + "'");
                        instance.create(_paramCache);
                    }
                }
            }
        }
    }

    /**
     * Updates on <code>_file</code> for <code>_instance</code> within a
     * transaction (if a transaction was not already started).
     *
     * @param _paramCache       parameter cache (used to get the MX context)
     * @param _instance         instance to update
     * @param _file             file with target definition
     * @param _version          version to update
     * @return <i>true</i> if update was done; otherwise <i>false</i>
     * @throws Exception if update of the instance failed
     */
    protected static boolean updateOne(final ParameterCache_mxJPO _paramCache,
                                       final AbstractObject_mxJPO _instance,
                                       final File _file,
                                       final String _version)
        throws Exception
    {
        boolean commit = false;
        final boolean transActive = _paramCache.getContext().isTransactionActive();
        try  {
            if (!transActive)  {
                _paramCache.getContext().start(true);
            }
            _instance.update(_paramCache, _file, _version);
            if (!transActive)  {
                _paramCache.getContext().commit();
            }
            commit = true;
        } finally  {
            if (!commit && !transActive && _paramCache.getContext().isTransactionActive())  {
                _paramCache.getContext().abort();
            }
        }
        return commit;
    }

    /**
     * Compiles given <code>_compiles</code> files.
     *
     * @param _paramCache   parameter cache (used for logging purposes)
     * @param _compiles     list of files to compile
     */
    protected static void compile(final ParameterCache_mxJPO _paramCache,
                                  final List<AbstractObject_mxJPO> _compiles)
    {
        for (final AbstractObject_mxJPO instance : _compiles)  {
            try  {
                if (instance.compile(_paramCache))  {
                    _paramCache.logInfo("compile " + instance.getTypeDef().getLogging()
                           + " '" + instance.getName() + "'");
                }
            } catch (final Exception e)  {
                _paramCache.logError("compile of " + instance.getTypeDef().getLogging()
                       + " '" + instance.getName() + "' failed:\n" + e.toString());
            }
        }
    }
}
