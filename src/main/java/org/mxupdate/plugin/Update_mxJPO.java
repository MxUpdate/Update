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

package org.mxupdate.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;
import matrix.db.MatrixWriter;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.UpdateUtil_mxJPO;

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
     * Argument key for the compile flag.
     */
    private static final String ARGUMENT_KEY_COMPILE = "Compile"; //$NON-NLS-1$

    /**
     * Argument key for the file names.
     */
    private static final String ARGUMENT_KEY_FILENAMES = "FileNames"; //$NON-NLS-1$

    /**
     * Argument key for the files with content.
     */
    private static final String ARGUMENT_KEY_FILECONTENTS = "FileContents"; //$NON-NLS-1$

    /**
     * Executes the update.
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    map with all update arguments
     * @return log information from the update / compile
     * @throws Exception if update failed
     */
    Map<String,?> execute(final ParameterCache_mxJPO _paramCache,
                          final Map<String,Object> _arguments)
        throws Exception
    {
        final boolean compile = this.getArgument(_arguments, Update_mxJPO.ARGUMENT_KEY_COMPILE, false);
        final Set<String> fileNames = this.getArgument(_arguments, Update_mxJPO.ARGUMENT_KEY_FILENAMES, null);
        final Map<String,String> fileContents = this.getArgument(_arguments, Update_mxJPO.ARGUMENT_KEY_FILECONTENTS, null);

        String ret = null;

        // if file with content a temporary directory must be created!
        final File tmpDir;
        if (fileContents != null)  {
            // create temporary directory
            tmpDir = File.createTempFile("Update", "tmp");
            tmpDir.delete();
            tmpDir.mkdir();
        } else  {
            tmpDir = null;
        }

        final Set<File> localFiles = new HashSet<File>();
        try  {
            // store all files in temporary directory
            if (fileContents != null)  {
                for (final Map.Entry<String,String> fileEntry : fileContents.entrySet())  {
                    final String fileName = (new File(fileEntry.getKey())).getName();

                    final File localFile = new File(tmpDir, fileName);
                    final Writer writer = new FileWriter(localFile);
                    writer.write(fileEntry.getValue());
                    writer.close();

                    localFiles.add(localFile);
                }
            }

            // and call update
            final Set<File> updateFiles = new HashSet<File>();
            updateFiles.addAll(localFiles);

            // append already existing files
            if (fileNames != null)  {
                for (final String fileName : fileNames)  {
                    updateFiles.add(new File(fileName));
                }
            }

            ret = this.updateFiles(_paramCache, updateFiles, compile);
        } finally  {
            // at least remove all temporary stuff
            for (final File localFile : localFiles)  {
                localFile.delete();
            }
            if (tmpDir != null)  {
                tmpDir.delete();
            }
        }

        return this.prepareReturn(ret, null, null, null);
    }

    /**
     * Depreciated because only used from old Eclipse plug-in.
     *
     * @param _context  MX context for this request
     * @param _args     first index of the arguments defined the file to update
     * @throws Exception if the update failed
     * @deprecated
     */
    @Deprecated()
    public void mxMain(final Context _context,
                       final String... _args)
        throws Exception
    {
        final Set<File> files = new HashSet<File>();
        files.add(new File(_args[0]));
        final MatrixWriter writer = new MatrixWriter(_context);
        writer.write(this.updateFiles(new ParameterCache_mxJPO(_context, true, null), files, false));
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
     *                  </ul>
     * @return logging information from the update
     * @throws Exception if the update failed
     * @see #updateFiles(ParameterCache_mxJPO, Set, boolean)
     * @deprecated
     */
    @Deprecated()
    public String updateByName(final Context _context,
                               final String[] _args)
        throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true, null);

        final Set<String> fileNames = this.<Set<String>>decode(_args, 0);
        final boolean compile = this.<Boolean>decode(_args, 1, false);

        final Set<File> files = new HashSet<File>();
        for (final String fileName : fileNames)  {
            files.add(new File(fileName));
        }

        final String ret = this.updateFiles(paramCache, files, compile);


        return ret;
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
     *                  </ul>
     * @return logging information from the update
     * @throws Exception if the update failed
     * @see #updateFiles(ParameterCache_mxJPO, Set, boolean)
     * @deprecated
     */
    @Deprecated()
    public String updateByContent(final Context _context,
                                  final String[] _args)
        throws Exception
    {
        // initialize mapping
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true, null);

        String ret = "";

        final Map<String,String> files = this.<Map<String,String>>decode(_args, 0);
        final boolean compile = this.<Boolean>decode(_args, 1, false);

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
            ret = this.updateFiles(paramCache, localFiles, compile);
        } finally  {
            // at least remove all temporary stuff
            for (final File localFile : localFiles)  {
                localFile.delete();
            }
            tmpDir.delete();
        }

        return ret;
    }

    /**
     * Updates all configurations items specified through given set of MX
     * update files.
     *
     * @param _paramCache   predefined parameters with MX context
     * @param _files        set of all files to update
     * @param _compile      if <i>true</i> related JPOs are compiled; if
     *                      <i>false</i> no JPOs are compiled
     * @return logging information from the update
     * @throws Exception if update for the <code>_files</code> failed
     */
    protected String updateFiles(final ParameterCache_mxJPO _paramCache,
                                 final Set<File> _files,
                                 final boolean _compile)
        throws Exception
    {
        UpdateUtil_mxJPO.update(_paramCache, this.evalInstances(_paramCache, _files), null);
        return _paramCache.getLogString();
    }

    /**
     * Evaluates depending on the file (names) related MX type definition
     * instances.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _files        set with all files
     * @return map with the files and depending MX type definition instance
     * @throws Exception if instances could not be evaluated
     */
    protected Map<TypeDef_mxJPO,Map<File,String>> evalInstances(final ParameterCache_mxJPO _paramCache,
                                                                final Set<File> _files)
        throws Exception
    {
        final Map<TypeDef_mxJPO,Map<File,String>> ret = new HashMap<TypeDef_mxJPO,Map<File,String>>();
        for (final File file : _files)  {
            boolean found = false;
            for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefs())  {
                if (!typeDef.isFileMatchLast())  {
                    final AbstractObject_mxJPO instance = typeDef.newTypeInstance(null);
                    final String mxName = instance.extractMxName(_paramCache, file);
                    if (mxName != null)  {
                        if (!ret.containsKey(typeDef))  {
                            ret.put(typeDef, new HashMap<File,String>());
                        }
                        ret.get(typeDef).put(file, mxName);
                        found  = true;
                        break;
                    }
                }
            }
            if (!found)  {
                for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefs())  {
                    if (typeDef.isFileMatchLast())  {
                        final AbstractObject_mxJPO instance = typeDef.newTypeInstance(null);
                        final String mxName = instance.extractMxName(_paramCache, file);
                        if (mxName != null)  {
                            if (!ret.containsKey(typeDef))  {
                                ret.put(typeDef, new HashMap<File,String>());
                            }
                            ret.get(typeDef).put(file, mxName);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }
}
