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

package org.mxupdate.action;

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
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Implements the update action used within MxUpdate.
 *
 * @author The MxUpdate Team
 */
public class UpdateAction_mxJPO
{
    /** Parameter cache. */
    private final ParameterCache_mxJPO paramCache;
    /** Selected files / CI objects. */
    private final SelectTypeDefUtil_mxJPO selects;

    /**
     * Initializes the action.
     *
     * @param _paramCache   parameter cache
     * @param _selects      selected matched files
     */
    public UpdateAction_mxJPO(final ParameterCache_mxJPO _paramCache,
                              final SelectTypeDefUtil_mxJPO _selects)
    {
        this.paramCache = _paramCache;
        this.selects = _selects;
    }

    /**
     * Executes the action.
     *
     * @throws Exception if execute failed
     */
    public void execute()
        throws Exception
    {
        this.update(this.paramCache, this.selects.evalMatches(this.paramCache));
    }

    /**
    *
    * @param _paramCache       parameter cache
    * @param _clazz2names      depending on the type definition the related
    *                          files with MX name which must be updated
    * @throws Exception if update failed
    */
   public void update(final ParameterCache_mxJPO _paramCache,
                      final Map<TypeDef_mxJPO,Map<String,File>> _clazz2names)
       throws Exception
   {
       // fetch existing CI's
       final Map<TypeDef_mxJPO,Set<String>> existingNames = this.getExistingCIs(_paramCache, _clazz2names.keySet());

       // create if needed (and not in the list of existing objects)
       this.create(_paramCache, existingNames, _clazz2names);

       // update
       final List<AbstractObject_mxJPO<?>> compiles = new ArrayList<>();
       for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
           final Map<String,File> clazzMap = _clazz2names.get(typeDef);
           if (clazzMap != null)  {
               final Set<String> existings = existingNames.get(typeDef);

               for (final Entry<String,File> fileEntry : clazzMap.entrySet())  {
                   _paramCache.logInfo("check " + typeDef.getLogging() + " '" + fileEntry.getKey() + "'");

                   final boolean update;
                   if (_paramCache.getValueBoolean(ValueKeys.UpdateCheckFileDate))  {
                       final AbstractObject_mxJPO<?> instance = typeDef.newTypeInstance(this.paramCache, fileEntry.getKey());
                       final Date fileDate = new Date(fileEntry.getValue().lastModified());
                       final String instDateString;
                       if (existings.contains(fileEntry.getKey()))  {
                           instDateString = instance.getPropValue(_paramCache, PropertyDef_mxJPO.FILEDATE);
                       } else  {
                           instDateString = null;
                       }
                       Date instDate;
                       if ((instDateString == null) || instDateString.isEmpty())  {
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
                   } else  {
                       update = true;
                       _paramCache.logDebug("    - update");
                   }
                   // execute update
                   if (update)  {
                       boolean commit = false;
                       final boolean transActive = _paramCache.getContext().isTransactionActive();
                       try  {
                           if (!transActive)  {
                               _paramCache.getContext().start(true);
                           }
                           typeDef.update(_paramCache, !existings.contains(fileEntry.getKey()), fileEntry.getKey(), fileEntry.getValue());
                           if (!transActive)  {
                               _paramCache.getContext().commit();
                           }
                           commit = true;
                           if (_paramCache.getValueBoolean(ValueKeys.Compile))  {
                               compiles.add(typeDef.newTypeInstance(this.paramCache, fileEntry.getKey()));
                           }
                       } finally  {
                           if (!commit && !transActive && _paramCache.getContext().isTransactionActive())  {
                               _paramCache.getContext().abort();
                           }
                       }
                   }

               }
           }
       }

       // compile
       this.compile(_paramCache, compiles);
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
   protected Map<TypeDef_mxJPO,Set<String>> getExistingCIs(final ParameterCache_mxJPO _paramCache,
                                                           final Set<TypeDef_mxJPO> _typeDefs)
       throws Exception
   {
       final Map<TypeDef_mxJPO,Set<String>> existingNames = new HashMap<>();
       for (final TypeDef_mxJPO clazz : _typeDefs)  {
           existingNames.put(clazz, clazz.matchMxNames(_paramCache, null));
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
   protected void create(final ParameterCache_mxJPO _paramCache,
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
                       final AbstractObject_mxJPO<?> instance = clazz.newTypeInstance(_paramCache, fileEntry.getKey());
                       _paramCache.logInfo("create " + clazz.getLogging() + " '" + fileEntry.getKey() + "'");
                       instance.createOld(_paramCache);
                   }
               }
           }
       }
   }

   /**
    * Compiles given <code>_compiles</code> files.
    *
    * @param _paramCache   parameter cache (used for logging purposes)
    * @param _compiles     list of files to compile
    */
   protected void compile(final ParameterCache_mxJPO _paramCache,
                          final List<AbstractObject_mxJPO<?>> _compiles)
   {
       for (final AbstractObject_mxJPO<?> instance : _compiles)  {
           try  {
               if (instance.compile(_paramCache))  {
                   _paramCache.logInfo("compile program '" + instance.getName() + "'");
               }
           } catch (final Exception e)  {
               _paramCache.logError("compile of program '" + instance.getName() + "' failed:\n" + e.toString());
           }
       }
   }
}
