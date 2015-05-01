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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * Handles the select items (match of files or match of configuration items).
 *
 * @author The MxUpdate Team
 */
public class SelectTypeDefUtil_mxJPO
{
    /** Matches depending of the type definition. */
    private final Map<TypeDef_mxJPO,Set<String>> clazz2matches = new HashMap<TypeDef_mxJPO,Set<String>>();
    /** Matched to be ignored depending of the type definition. */
    private final Map<TypeDef_mxJPO,Set<String>> clazz2matchesOpp = new HashMap<TypeDef_mxJPO,Set<String>>();

    /**
     * Defines a match for all type definitions.
     *
     * @param _typeDefs     type definitions
     * @param _match        match
     */
    public void match(final Collection<TypeDef_mxJPO> _typeDefs,
                      final String _match)
    {
        for (final TypeDef_mxJPO typeDef : _typeDefs)  {
            if (!this.clazz2matches.containsKey(typeDef))  {
                this.clazz2matches.put(typeDef, new HashSet<String>());
            }
            this.clazz2matches.get(typeDef).add(_match);
        }
    }

    /**
     * Defines a match of ignores for given type definition
     *
     * @param _typeDef      type definition
     * @param _match        match
     */
    public void ignore(final TypeDef_mxJPO _typeDef,
                       final String _match)
    {
        if (!this.clazz2matchesOpp.containsKey(_typeDef))  {
            this.clazz2matchesOpp.put(_typeDef, new HashSet<String>());
        }
        this.clazz2matchesOpp.get(_typeDef).add(_match);
    }

    /**
     * Evaluate all matching CI objects depending on the selects.
     *
     * @param _paramCache       parameter cache
     * @return map of type definition and a set of matching names for given type
     *         definition
     * @throws Exception if the match fails
     */
    protected Map<TypeDef_mxJPO,Set<String>> getMatching(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        // and now depending on the type definition prepare return list
        final Map<TypeDef_mxJPO,Set<String>> clazz2names = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final Entry<TypeDef_mxJPO,Set<String>> entry : this.clazz2matches.entrySet())  {
            final AbstractObject_mxJPO instance = entry.getKey().newTypeInstance(null);
            final Set<String> matchingMxNames = new TreeSet<String>();
            clazz2names.put(entry.getKey(), matchingMxNames);
            for (final String mxName : instance.getMxNames(_paramCache))  {
                for (final String match : entry.getValue())  {
                    if (instance.matchMxName(_paramCache, mxName, match))  {
                        matchingMxNames.add(mxName);
                        break;
                    }
                }
            }
        }

        // and now remove ignored matches
        final Map<TypeDef_mxJPO,Set<String>> ret = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final Entry<TypeDef_mxJPO,Set<String>> entry : clazz2names.entrySet())  {
            final Collection<String> matchOpps = this.clazz2matchesOpp.get(entry.getKey());
            // ignored match defined? => to test!
            if ((matchOpps != null) && !matchOpps.isEmpty())  {
                final Set<String> names = new TreeSet<String>();
                for (final String nameToTest : entry.getValue())  {
                    boolean allowed = true;
                    for (final String matchOpp : matchOpps)  {
                        if (StringUtil_mxJPO.match(nameToTest, matchOpp))  {
                            allowed = false;
                            break;
                        }
                    }
                    if (allowed)  {
                        names.add(nameToTest);
                    }
                }
                ret.put(entry.getKey(), names);
            } else  {
                ret.put(entry.getKey(), entry.getValue());
            }
        }

        return ret;
    }

    /**
     * Evaluates for matching files with the names depending on the update
     * classes.
     *
     * @param _paramCache       parameter cache with paths to check (or no path
     *                          parameter)
     * @return map of update classes and the depending files with their names
     * @throws Exception if match for the files failed
     */
    public Map<TypeDef_mxJPO,Map<File,String>> evalMatches(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2names = new HashMap<TypeDef_mxJPO,Map<File,String>>();

        // get path parameters
        final Collection<String> ignoreFiles = _paramCache.getValueList(ValueKeys.PathIgnoreFile);
        final Collection<String> ignorePaths = _paramCache.getValueList(ValueKeys.PathIgnorePath);
        final Collection<String> paths       = _paramCache.getValueList(ValueKeys.Path);

        // if no path is defined, the paths are directly defined at the objects
        // to import
        if ((paths == null) || paths.isEmpty())  {
            for (final Entry<TypeDef_mxJPO,Set<String>> entry : this.clazz2matches.entrySet())  {
                // first get all matching files
                final Set<File> allFiles = new HashSet<File>();
                for (final String pathStr : entry.getValue())  {
                    final File pathFile = new File(pathStr);
                    final String match = pathFile.getName();
                    final Set<File> subPathFiles = this.getAllFiles(pathFile.getParentFile(), ignorePaths, ignoreFiles);
                    for (final File file : subPathFiles)  {
                        if (StringUtil_mxJPO.match(file.getName(), match))  {
                            allFiles.add(file);
                        }
                    }
                }
                // get all matching files depending on the update classes
                this.evalMatches(_paramCache, entry.getKey(), clazz2names, allFiles, null);
            }
        // path parameter is defined
        } else  {
            /// get all files
            final Set<File> allFiles = new HashSet<File>();
            for (final String path : paths)  {
                allFiles.addAll(this.getAllFiles(new File(path), ignorePaths, ignoreFiles));
            }
            // get all matching files depending on the update classes
            for (final Entry<TypeDef_mxJPO,Set<String>> entry : this.clazz2matches.entrySet())  {
                this.evalMatches(_paramCache, entry.getKey(), clazz2names, allFiles, entry.getValue());
            }
        }

        // and now remove ignored matches
        final Map<TypeDef_mxJPO,Map<File,String>> ret = new HashMap<TypeDef_mxJPO,Map<File,String>>();
        for (final Entry<TypeDef_mxJPO,Map<File,String>> entry : clazz2names.entrySet())  {
            final Collection<String> matchOpps = this.clazz2matchesOpp.get(entry.getKey());
            if ((matchOpps != null) && !matchOpps.isEmpty())  {
                final Map<File,String> files = new TreeMap<File,String>();
                for (final Map.Entry<File,String> fileEntry : entry.getValue().entrySet())  {
                    boolean allowed = true;
                    for (final String matchOpp : matchOpps)  {
                        if (StringUtil_mxJPO.match(fileEntry.getValue(), matchOpp))  {
                            allowed = false;
                            break;
                        }
                    }
                    if (allowed)  {
                        files.put(fileEntry.getKey(), fileEntry.getValue());
                    }
                }
                ret.put(entry.getKey(), files);
            } else  {
                ret.put(entry.getKey(), entry.getValue());
            }
        }

        return ret;
    }

    /**
     *
     * @param _paramCache           parameter cache
     * @param _selectedTypeDefs     selected type definitions from the MxUpdate
     *                              parameters
     * @param _fileMatchLast        must be <i>true</i> if the last match is
     *                              executed; <i>false</i> otherwise
     * @param _clazz2names
     * @param _matchedFiles         already matched files
     * @param _matches
     * @return ??
     * @throws Exception if the evaluate for the match fails
     */
    private boolean evalMatches(final ParameterCache_mxJPO _paramCache,
                                final TypeDef_mxJPO _selectedTypeDefs,
                                final Map<TypeDef_mxJPO,Map<File,String>> _clazz2names,
                                final Set<File> _allFiles,
                                final Collection<String> _matches)
        throws Exception
    {
        final boolean foundOther = false;

        // fetch map with files matching type definition
        Map<File,String> tmp = _clazz2names.get(_selectedTypeDefs);
        if (tmp == null)  {
            tmp = new TreeMap<File,String>();
            _clazz2names.put(_selectedTypeDefs, tmp);
        }

        final AbstractObject_mxJPO instance = _selectedTypeDefs.newTypeInstance(null);

        for (final File file : _allFiles)  {
            final String mxName = instance.extractMxName(_paramCache, file);
            if (mxName != null)  {
                if (_matches == null)
                {
                    tmp.put(file, mxName);
                }
                else
                {
                    for (final String match : _matches)  {
                        if (instance.matchMxName(_paramCache, mxName, match))  {
                            // only if user had selected them
                            tmp.put(file, mxName);
                        }
                    }
                }
            }
        }

        return foundOther;
    }


    /**
     * Evaluates depending on given path all files in the sub directories.
     *
     * @param _path         path for which the files are searched
     * @param _ignorePaths  match for ignored paths for which files are not
     *                      returned
     * @param _ignoreFiles  match for ignored files which are not returned
     * @return set of all found files
     * TODO performance improvement needed if for given path already all files
     *       are evaluated
     */
    private Set<File> getAllFiles(final File _path,
                                  final Collection<String> _ignorePaths,
                                  final Collection<String> _ignoreFiles)
    {
        final Set<File> ret = new HashSet<File>();

        if (_path.isDirectory())  {
            for (final File file : _path.listFiles())  {
                if (file.isDirectory())  {
                    boolean allowed = true;
                    for (final String match : _ignorePaths)  {
                        if (StringUtil_mxJPO.match(file.getName(), match))  {
                            allowed = false;
                            break;
                        }
                    }
                    if (allowed)  {
                        ret.addAll(this.getAllFiles(file, _ignorePaths, _ignoreFiles));
                    }
                } else  {
                    boolean allowed = true;
                    for (final String match : _ignoreFiles)  {
                        if (StringUtil_mxJPO.match(file.getName(), match))  {
                            allowed = false;
                            break;
                        }
                    }
                    if (allowed)  {
                        ret.add(file);
                    }
                }
            }
        }
        return ret;
    }
}
