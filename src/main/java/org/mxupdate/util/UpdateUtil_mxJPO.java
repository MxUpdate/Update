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

package org.mxupdate.util;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Utility class for updating configuration item.
 *
 * @author The MxUpdate Team
 */
public final class UpdateUtil_mxJPO
{
    /**
     * Name of the key within the parameter cache that administration objects
     * compiled after they are updated.
     */
    private static final String PARAM_COMPILE = "Compile";

    /**
     * Name of the key within the parameter cache to define if for the update a
     * check of the file date must be done.
     */
    private static final String PARAM_CHECK_FILE_DATE = "UpdateCheckFileDate";

    /**
     * Name of the key within the parameter cache to define if for the update a
     * check for the version must be done.
     */
    private static final String PARAM_CHECK_VERSION = "UpdateCheckVersion";

    /**
     * String of the key within the parameter cache for the use file date as
     * version parameter.
     */
    public static final String PARAM_FILEDATE2VERSION = "FileDate2Version";

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
     * @throws Exception if update failed
     * @see UpdateCheck_mxJPO
     */
    public static void update(final ParameterCache_mxJPO _paramCache,
                              final Map<TypeDef_mxJPO,Map<String,File>> _clazz2names)
        throws Exception
    {
        // fetch existing CI's
        final Map<TypeDef_mxJPO,Set<String>> existingNames = UpdateUtil_mxJPO.getExistingCIs(_paramCache, _clazz2names.keySet());

        // create if needed (and not in the list of existing objects)
        UpdateUtil_mxJPO.create(_paramCache, existingNames, _clazz2names);

        // update
        final List<AbstractObject_mxJPO> compiles = new ArrayList<AbstractObject_mxJPO>();
        final boolean compile = _paramCache.getValueBoolean(UpdateUtil_mxJPO.PARAM_COMPILE);
        for (final TypeDef_mxJPO clazz : _paramCache.getMapping().getAllTypeDefsSorted())  {
            final Map<String,File> clazzMap = _clazz2names.get(clazz);
            if (clazzMap != null)  {
                final Set<String> existings = existingNames.get(clazz);

                for (final Entry<String,File> fileEntry : clazzMap.entrySet())  {
                    final AbstractObject_mxJPO instance = clazz.newTypeInstance(fileEntry.getKey());
                    _paramCache.logInfo("check "+instance.getTypeDef().getLogging() + " '" + fileEntry.getKey() + "'");

                    final boolean update;
                    final String version = _paramCache.getValueBoolean(UpdateUtil_mxJPO.PARAM_FILEDATE2VERSION)
                                    ? Long.toString(fileEntry.getValue().lastModified() / 1000)
                                    : _paramCache.getValueString(ParameterCache_mxJPO.KEY_VERSION);
                    if (_paramCache.getValueBoolean(UpdateUtil_mxJPO.PARAM_CHECK_FILE_DATE))  {
                        final Date fileDate = new Date(fileEntry.getValue().lastModified());
                        final String instDateString;
                        if (existings.contains(fileEntry.getKey()))  {
                            instDateString = instance.getPropValue(_paramCache, PropertyDef_mxJPO.FILEDATE);
                        } else  {
                            instDateString = null;
                        }
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
                    } else if (_paramCache.getValueBoolean(UpdateUtil_mxJPO.PARAM_CHECK_VERSION))  {
                        final String instVersion;
                        if (existings.contains(fileEntry.getKey()))  {
                            instVersion = instance.getPropValue(_paramCache, PropertyDef_mxJPO.VERSION);
                        } else  {
                            instVersion = null;
                        }
                        if ((instVersion != null) && instVersion.equals(version))  {
                            update = false;
                        } else  {
                            update = true;
                            if (_paramCache.getValueBoolean(UpdateUtil_mxJPO.PARAM_FILEDATE2VERSION))  {
                                _paramCache.logDebug("    - update to version from " + new Date(fileEntry.getValue().lastModified()));
                            } else  {
                                _paramCache.logDebug("    - update to version " + version);
                            }
                        }
                    } else  {
                        update = true;
                        _paramCache.logDebug("    - update");
                    }
                    // execute update
                    if (update && UpdateUtil_mxJPO.updateOne(_paramCache, !existings.contains(fileEntry.getKey()), instance, fileEntry.getValue(), version) && compile)  {
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
     * @param _existingNames    already existing names
     * @param _clazz2names      depending on the type definition the related
     *                          files with MX name which must be updated
     * @throws Exception if create failed
     */
    protected static void create(final ParameterCache_mxJPO _paramCache,
                                 final Map<TypeDef_mxJPO,Set<String>> _existingNames,
                                 final Map<TypeDef_mxJPO,Map<String,File>> _clazz2names)
        throws Exception
    {
        // create if needed (and not in the list of existing objects)
        for (final TypeDef_mxJPO clazz : _paramCache.getMapping().getAllTypeDefsSorted())  {
            final Map<String,File> clazzMap = _clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Entry<String,File> fileEntry : clazzMap.entrySet())  {
                    final Set<String> existings = _existingNames.get(clazz);
                    if (!existings.contains(fileEntry.getKey()))  {
                        final AbstractObject_mxJPO instance = clazz.newTypeInstance(fileEntry.getKey());
                        _paramCache.logInfo("create "+instance.getTypeDef().getLogging() + " '" + fileEntry.getKey() + "'");
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
     * @param _paramCache   parameter cache (used to get the MX context)
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _instance     instance to update
     * @param _file         file with target definition
     * @param _version      version to update
     * @return <i>true</i> if update was done; otherwise <i>false</i>
     * @throws Exception if update of the instance failed
     */
    protected static boolean updateOne(final ParameterCache_mxJPO _paramCache,
                                       final boolean _create,
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
            _instance.update(_paramCache, _create, _file, _version);
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
