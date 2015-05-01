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
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.UpdateUtil_mxJPO;

/**
 * The JPO class is the plug-in to create or update configuration items defined
 * through given MX update files.
 *
 * @author The MxUpdate Team
 */
class Update_mxJPO
    extends AbstractPlugin_mxJPO
{
    /** Argument key for the compile flag. */
    private static final String ARGUMENT_KEY_COMPILE = "Compile"; //$NON-NLS-1$

    /** Argument key for the file names. */
    private static final String ARGUMENT_KEY_FILENAMES = "FileNames"; //$NON-NLS-1$

    /** Argument key for the files with content. */
    private static final String ARGUMENT_KEY_FILECONTENTS = "FileContents"; //$NON-NLS-1$

    /**
     * Executes the update.
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    map with all update arguments
     * @return log information from the update / compile
     * @throws Exception if update failed
     */
    Object execute(final ParameterCache_mxJPO _paramCache,
                   final Map<String,Object> _arguments)
        throws Exception
    {
        final boolean compile = this.getArgument(_arguments, Update_mxJPO.ARGUMENT_KEY_COMPILE, false);
        final Set<String> fileNames = this.getArgument(_arguments, Update_mxJPO.ARGUMENT_KEY_FILENAMES, null);
        final Map<String,String> fileContents = this.getArgument(_arguments, Update_mxJPO.ARGUMENT_KEY_FILECONTENTS, null);

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

            this.updateFiles(_paramCache, updateFiles, compile);
        } finally  {
            // at least remove all temporary stuff
            for (final File localFile : localFiles)  {
                localFile.delete();
            }
            if (tmpDir != null)  {
                tmpDir.delete();
            }
        }

        return null;
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
        UpdateUtil_mxJPO.update(_paramCache, this.evalInstances(_paramCache, _files));
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
    protected Map<TypeDef_mxJPO,Map<String,File>> evalInstances(final ParameterCache_mxJPO _paramCache,
                                                                final Set<File> _files)
        throws Exception
    {
        final Map<TypeDef_mxJPO,Map<String,File>> ret = new HashMap<TypeDef_mxJPO,Map<String,File>>();
        for (final File file : _files)  {
            for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
                final AbstractObject_mxJPO instance = typeDef.newTypeInstance(null);
                final String mxName = instance.evalMxName(_paramCache, file);
                if (mxName != null)  {
                    if (!ret.containsKey(typeDef))  {
                        ret.put(typeDef, new HashMap<String,File>());
                    }
                    ret.get(typeDef).put(mxName, file);
                    break;
                }
            }
        }
        return ret;
    }
}
