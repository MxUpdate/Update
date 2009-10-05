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
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import matrix.db.Context;
import matrix.db.MatrixWriter;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to create or update configuration items defined
 * through given MX update files.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Update_mxJPO
        extends AbstractPlugin_mxJPO
{
    /**
     * Main method to create or update given TCL update file.
     *
     * @param _context  MX context for this request
     * @param _args     first index of the arguments defined the file to update
     * @throws Exception if the update failed
     * @deprecated
     */
    @Deprecated
    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        final Set<File> files = new HashSet<File>();
        files.add(new File(_args[0]));
        final MatrixWriter writer = new MatrixWriter(_context);
        writer.write(this.updateFiles(_context, null, files, false));
        writer.flush();
        writer.close();
    }

    /**
     * Updates MX Update files depending on file names. The method is used from
     * the MX Update Eclipse Plug-In if the MxUpdate Update Deployment tool has
     * access to the files from the Eclipse Plug-In.
     *
     * @param _context  MX context for this request
     * @param _args     encoded arguments from the Eclipse plug-in:
     *                  <ul>
     *                  <li><b>{@link Set}&lt;{@link String}&gt;</b><br/>set of
     *                      file names which must be updated</li>
     *                  <li><b>{@link Boolean}</b><br/> if set to <i>true</i>
     *                      all included JPOs are compiled; otherwise no JPOs
     *                      are compiled. Default is not to compile
     *                      (<i>false</i>)</li>
     *                  <li><b>{@link Map}&lt;{@link String},{@link String}&gt;
     *                      </b><br/>Sets the predefined parameter values (key
     *                      of the map is name of the parameter, value of the
     *                      map is the value of the parameter). Default is no
     *                      predefined parameter valued (<code>null</code>).
     *                      </li>
     *                  </ul>
     * @return logging information from the update
     * @throws Exception if the update failed
     * @see #updateFiles(Context, Map, Set, boolean)
     */
    public String updateByName(final Context _context,
                               final String[] _args)
            throws Exception
    {
        final Set<String> fileNames = this.<Set<String>>decode(_args, 0);
        final boolean compile = this.<Boolean>decode(_args, 1, false);
        final Map<String,String> paramValues = this.<Map<String,String>>decode(_args, 2, null);

        final Set<File> files = new HashSet<File>();
        for (final String fileName : fileNames)  {
            files.add(new File(fileName));
        }

        return this.updateFiles(_context, paramValues, files, compile);
    }

    /**
     * Updates the files depending on the file name with related file content.
     * The method is used from the MX Update Eclipse Plug-In if the MxUpdate
     * Update Deployment tool has <b>NO</b> access to the files from the
     * Eclipse Plug-In (e.g. if a virtual machine is used, or MX is located on
     * a server).
     *
     * @param _context  MX context for this request
     * @param _args     encoded arguments from the Eclipse plug-in:
     *                  <ul>
     *                  <li><b>{@link Map}&lt;{@link String},{@link String}&gt;
     *                      </b><br/>map of the file name and the related file
     *                      content which must be updated</li>
     *                  <li><b>{@link Boolean}</b><br/> if set to <i>true</i>
     *                      all included JPOs are compiled; otherwise no JPOs
     *                      are compiled. Default is not to compile
     *                      (<i>false</i>)</li>
     *                  <li><b>{@link Map}&lt;{@link String},{@link String}&gt;
     *                      </b><br/>Sets the predefined parameter values (key
     *                      of the map is name of the parameter, value of the
     *                      map is the value of the parameter). Default is no
     *                      predefined parameter valued (<code>null</code>).
     *                      </li>
     *                  </ul>
     * @return logging information from the update
     * @throws Exception if the update failed
     * @see #updateFiles(Context, Map, Set, boolean)
     */
    public String updateByContent(final Context _context,
                                  final String[] _args)
            throws Exception
    {
try {
        String ret = "";

        final Map<String,String> files = this.<Map<String,String>>decode(_args, 0);
        final boolean compile = this.<Boolean>decode(_args, 1, false);
        final Map<String,String> paramValues = this.<Map<String,String>>decode(_args, 2, null);

        // create temporary directory
        final File tmpDir = File.createTempFile("Update", "tmp");
        tmpDir.delete();
        tmpDir.mkdir();

        final Set<File> localFiles = new HashSet<File>();
        try  {
            // store all files in temporary directory
            for (final Map.Entry<String,String> fileEntry : files.entrySet())  {
                final String fileName = (new File(fileEntry.getKey())).getName();

                final File localFile = new File(tmpDir, fileName);
                final Writer writer = new FileWriter(localFile);
                writer.write(fileEntry.getValue());
                writer.close();

                localFiles.add(localFile);
            }

            // and call update
            ret = this.updateFiles(_context, paramValues, localFiles, compile);
        } finally  {
            // at least remove all temporary stuff
            for (final File localFile : localFiles)  {
                localFile.delete();
            }
            tmpDir.delete();
        }

        return ret;
} catch (final Exception e)  {
    e.printStackTrace();
    throw e;
}
    }

    /**
     * Updates all configurations items specified through given set of MX
     * update files.
     *
     * @param _context      MX context for this request
     * @param _paramValues  predefined parameters
     * @param _files        set of all files to update
     * @param _compile      if <i>true</i> related JPOs are compiled; if
     *                      <i>false</i> no JPOs are compiled
     * @return logging information from the update
     * @throws Exception if update for the <code>_files</code> failed
     */
    protected String updateFiles(final Context _context,
                                 final Map<String,String> _paramValues,
                                 final Set<File> _files,
                                 final boolean _compile)
            throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true, _paramValues);

        // first found related type definition instances
        final Map<File,AbstractObject_mxJPO> instances = new TreeMap<File,AbstractObject_mxJPO>();
        for (final File file : _files)  {
            boolean found = false;
            for (final TypeDef_mxJPO typeDef : paramCache.getMapping().getAllTypeDefs())  {
                if (!typeDef.isFileMatchLast())  {
                    final AbstractObject_mxJPO instance = typeDef.newTypeInstance(null);
                    final String mxName = instance.extractMxName(paramCache, file);
                    if (mxName != null)  {
                        instances.put(file, typeDef.newTypeInstance(mxName));
                        found  = true;
                        break;
                    }
                }
            }
            if (!found)  {
                for (final TypeDef_mxJPO typeDef : paramCache.getMapping().getAllTypeDefs())  {
                    if (typeDef.isFileMatchLast())  {
                        final AbstractObject_mxJPO instance = typeDef.newTypeInstance(null);
                        final String mxName = instance.extractMxName(paramCache, file);
                        if (mxName != null)  {
                            instances.put(file, typeDef.newTypeInstance(mxName));
                            found = true;
                            break;
                        }
                    }
                }
            }
        }

        // check if objects must be created
        final Map<TypeDef_mxJPO,Set<String>> allMxNames = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final AbstractObject_mxJPO instance : instances.values())  {
            if (!allMxNames.containsKey(instance.getTypeDef()))  {
                allMxNames.put(instance.getTypeDef(), instance.getMxNames(paramCache));
            }
            if (!allMxNames.get(instance.getTypeDef()).contains(instance.getName()))  {
                paramCache.logInfo("created " + instance.getTypeDef().getLogging() + " '" + instance.getName() + "'");
                instance.create(paramCache);
            }
        }

        // update
        for (final Map.Entry<File,AbstractObject_mxJPO> instanceEntry : instances.entrySet())  {
            paramCache.logInfo("updated " + instanceEntry.getValue().getTypeDef().getLogging()
                    + " '" + instanceEntry.getValue().getName() + "'");
            instanceEntry.getValue().update(paramCache, instanceEntry.getKey(), "");
        }

        // at least compile (to be sure that all JPOs are updated)
        if (_compile)  {
            for (final Map.Entry<File,AbstractObject_mxJPO> instanceEntry : instances.entrySet())  {
                try  {
                    if (instanceEntry.getValue().compile(paramCache))  {
                        paramCache.logInfo("compiled " + instanceEntry.getValue().getTypeDef().getLogging()
                                + " '" + instanceEntry.getValue().getName() + "'");
                    }
                } catch (final Exception e)  {
                    paramCache.logInfo("compile of " + instanceEntry.getValue().getTypeDef().getLogging()
                            + " '" + instanceEntry.getValue().getName() + "' failed:\n" + e.toString());
                }
            }
        }

        return paramCache.getLogString();
    }
}
