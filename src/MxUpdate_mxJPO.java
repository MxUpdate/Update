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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.mapping.Mode_mxJPO;
import org.mxupdate.mapping.ParameterDef_mxJPO;
import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDefGroup_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.UpdateCheck_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * <tr>
 * <th></th><th></th><th></th>
 * <tr>
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MxUpdate_mxJPO
{
    /**
     * String of the key within the parameter cache for the ignore file
     * parameter.
     *
     * @see #evalMatches(ParameterCache_mxJPO, Map)
     */
    private static final String PARAM_IGNOREFILE = "PathIgnoreFile";

    /**
     * String of the key within the parameter cache for the ignore path
     * parameter.
     *
     * @see #evalMatches(ParameterCache_mxJPO, Map)
     */
    private static final String PARAM_IGNOREPATH = "PathIgnorePath";

    /**
     * String of the key within the parameter cache for the path parameter.
     *
     * @see #delete(ParameterCache_mxJPO, Map)
     * @see #evalMatches(ParameterCache_mxJPO, Map)
     * @see #export(ParameterCache_mxJPO, Map)
     */
    private static final String PARAM_PATH = "Path";

    /**
     * String of the key within the parameter cache for the copyright used for
     * the help description.
     *
     * @see #printHelp(ParameterCache_mxJPO)
     */
    private static final String PARAM_HELP_COPYRIGHT = "HelpCopyright";

    /**
     * String of the key within the parameter cache for the length of a
     * description line used for the help description.
     *
     * @see #printHelp(ParameterCache_mxJPO)
     */
    private static final String PARAM_HELP_LENGTHLINE = "HelpLengthLine";

    /**
     * String of the key within the parameter cache for the length of the
     * parameter line used for the help description.
     *
     * @see #printHelp(ParameterCache_mxJPO)
     */
    private static final String PARAM_HELP_LENGTHPARAMS = "HelpLengthParams";

    /**
     * String of the key within the parameter cache for the prefix used for
     * the help description.
     *
     * @see #printHelp(ParameterCache_mxJPO)
     */
    private static final String PARAM_HELP_PREFIX = "HelpPrefix";

    /**
     * String of the key within the parameter cache for the help usage used for
     * the help description.
     *
     * @see #printHelp(ParameterCache_mxJPO)
     */
    private static final String PARAM_HELP_USAGE = "HelpUsage";

    /**
     * Stored the descriptions of all parameters sorted by the parameters.
     * The key are the parameters. For the alpha numerical sort, each parameter
     * line must start with a '-' and a character. If the third character of
     * the parameter line is an underscore ('_'), this is parameter list is
     * does not contains a short parameter and will be removed within the help
     * print. Otherwise the list of parameters contains a short parameter and
     * must be printed completely.
     *
     * @see #appendDescription(CharSequence, Collection, Collection)
     * @see #printHelp(ParameterCache_mxJPO)
     */
    private final Map<String,String> description = new TreeMap<String,String>();

    /**
     * Stores all parameters to be sure that a parameter is defined only once.
     *
     * @see #appendDescription(String, Collection, Collection)
     */
    private final Set<String> allParams = new HashSet<String>();

    /**
     * Holds the mapping between the parameter and mode.
     */
    private final Map<String,Mode_mxJPO> paramsModes = new HashMap<String,Mode_mxJPO>();

    /**
     * Holds the mapping between the parameters and the related parameters.
     */
    private final Map<String,ParameterDef_mxJPO> paramsParameters = new HashMap<String,ParameterDef_mxJPO>();

    /**
     * All parameters related to export / import are stored in this map. The
     * key is the parameter (including the '-'), the value the related class.
     */
    private final Map<String,Collection<TypeDef_mxJPO>> paramsTypeDefs
            = new HashMap<String,Collection<TypeDef_mxJPO>>();

    /**
     * All opposite parameters related to export / import are stored in this
     * map. The key is the opposite parameter (including the '-'), the value
     * the related class.
     */
    private final Map<String,TypeDef_mxJPO> paramsTypeDefsOpp
            = new HashMap<String,TypeDef_mxJPO>();

    /**
     * Holds all parameters related how the version information is set.
     */
    private final Map<String,UpdateCheck_mxJPO> paramsUpdateChecks = new HashMap<String,UpdateCheck_mxJPO>();

    /**
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException
     */
    private void prepareParams(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        this.description.clear();
        this.allParams.clear();
        this.paramsModes.clear();
        this.paramsParameters.clear();
        this.paramsTypeDefs.clear();
        this.paramsUpdateChecks.clear();

        ////////////////////////////////////////////////////////////////////////
        // parameters

        for (final ParameterDef_mxJPO parameter : _paramCache.getMapping().getAllParameterDefs())  {
            if (parameter.getParameterList() != null)  {
                for (final String param : parameter.getParameterList())  {
                    final String paramStr = (param.length() > 1)
                                            ? "--" + param
                                            : "-" + param;
                    this.paramsParameters.put(paramStr, parameter);
                }
                final StringBuilder desc = new StringBuilder().append(parameter.getParameterDesc());
                if ((parameter.getDefaultValue() != null)
                        && (parameter.getType() != ParameterDef_mxJPO.Type.BOOLEAN))  {
                    desc.append('\n').append("(Default '");
                    if (parameter.getType() == ParameterDef_mxJPO.Type.LIST)  {
                        desc.append(parameter.getDefaultValue().replaceAll(",", ", "));
                    } else  {
                        desc.append(parameter.getDefaultValue());
                    }
                    desc.append("')");
                }
                this.appendDescription(desc,
                                       parameter.getParameterList(),
                                       parameter.getParameterArgs());
            }
        }

        ////////////////////////////////////////////////////////////////////////
        // modes

        for (final Mode_mxJPO mode : Mode_mxJPO.values())  {
            for (final String param : mode.getParameterList(_paramCache))  {
                final String paramStr = (param.length() > 1)
                                        ? "--" + param
                                        : "-" + param;
                this.paramsModes.put(paramStr, mode);
            }
            this.appendDescription(mode.getParameterDesc(_paramCache),
                                   mode.getParameterList(_paramCache),
                                   null);
        }

        ////////////////////////////////////////////////////////////////////////
        // update checks

        for (final UpdateCheck_mxJPO updateCheck : UpdateCheck_mxJPO.values())  {
            for (final String param : updateCheck.getParameterList(_paramCache))  {
                if (param.length() == 1)  {
                    this.paramsUpdateChecks.put("-" + param, updateCheck);
                } else  {
                    this.paramsUpdateChecks.put("--" + param, updateCheck);
                }
            }
            this.appendDescription(updateCheck.getParameterDesc(_paramCache),
                                   updateCheck.getParameterList(_paramCache),
                                   null);
        }

        ////////////////////////////////////////////////////////////////////////
        // type definitions

        // first create list all type definitions which could be used...
        final Set<TypeDef_mxJPO> all = new HashSet<TypeDef_mxJPO>();
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefs())  {
            if (!typeDef.isBusCheckExists() || typeDef.existsBusType(_paramCache.getContext()))  {
                all.add(typeDef);
                if (typeDef.getParameterList() != null)  {
                    this.defineParameter(null,
                            typeDef,
                            typeDef.getParameterDesc(),
                            typeDef.getParameterList());
                }
                if (typeDef.getParameterListOpp() != null)  {
                    for (final String param : typeDef.getParameterListOpp())  {
                        final String paramStr = (param.length() == 1)
                                                ? "-" + param
                                                : "--" + param;
                        this.paramsTypeDefsOpp.put(paramStr, typeDef);
                    }
                    // store description
                    this.appendDescription(typeDef.getParameterDescOpp(),
                            typeDef.getParameterListOpp(),
                            Arrays.asList(new String[]{"MATCH"}));
                }
            }
        }

        // define type definition group parameters depending on existing type
        // definitions (and only if at minimum one type definition of the
        // groups exists...)
        for (final TypeDefGroup_mxJPO group : _paramCache.getMapping().getAllTypeDefGroups())  {
            final Set<TypeDef_mxJPO> curTypeDefs = new HashSet<TypeDef_mxJPO>();
            for (final String typeDefName : group.getTypeDefList())  {
                final TypeDef_mxJPO typeDef = _paramCache.getMapping().getTypeDef(typeDefName);
                if (all.contains(typeDef))  {
                    curTypeDefs.add(typeDef);
                }
            }
            if (!curTypeDefs.isEmpty())  {
                this.defineParameter(curTypeDefs,
                                     null,
                                     group.getParameterDesc(),
                                     group.getParameterList());
            }
        }
   }

    /**
     *
     * @param _paramsList       to which parameter list must the parameter
     *                          appended
     * @param _clazz            class implementing the import / export (or
     *                          <code>null</code> if the complete parameter
     *                          list must be used)
     * @param _description      description of the parameter
     * @param _longParams       list of long parameters strings
     */
    private void defineParameter(final Collection<TypeDef_mxJPO> _paramsList,
                                 final TypeDef_mxJPO _clazz,
                                 final String _description,
                                 final Collection<String> _longParams)
    {
        final Collection<TypeDef_mxJPO> tmp;
        if (_clazz == null)  {
            tmp = _paramsList;
        } else  {
            tmp = new HashSet<TypeDef_mxJPO>();
            // add to given set of parameters
            if (_paramsList != null)  {
                _paramsList.add(_clazz);
            }
            tmp.add(_clazz);
        }

        // check for long parameters and test for double definition
        for (final String param : _longParams)  {
            final String paramStr = (param.length() == 1)
                                    ? "-" + param
                                    : "--" + param;
            this.paramsTypeDefs.put(paramStr, tmp);
        }

        // store description
        this.appendDescription(_description,
                               _longParams,
                               Arrays.asList(new String[]{"MATCH"}));
    }

    /**
     * Appends a description for a defined list of parameters. If a parameter
     * is defined twice, an error is thrown.
     *
     * @param _description  description to append
     * @param _params       related parameters
     * @param _args         text of the arguments for the list of parameters
     *                      (or <code>null</code> if not defined)
     * @see #allParams
     * @see #description
     */
    private void appendDescription(final CharSequence _description,
                                   final Collection<String> _params,
                                   final Collection<String> _args)
    {
        // check for double parameter definitions
        for (final String param : _params)  {
            if (this.allParams.contains(param))  {
                throw new Error("double definition of parameter '" + param
                        + "' with description '" + _description + "'");
            }
            this.allParams.add(param);
        }

        // check if first parameter is not a short parameter
        final String firstParam = _params.iterator().next();
        final StringBuilder param = new StringBuilder();
        if (firstParam.length() > 1)  {
            param.append('-').append(firstParam.charAt(0)).append('_');
        }


        // append all parameters to the description text
        boolean first = true;
        for (final String paramString : _params)  {
            if (first)  {
                first = false;
            } else  {
                param.append(',');
            }
            if (paramString.length() > 1)  {
                param.append('-');
            }
            param.append('-').append(paramString);
        }

        // append arguments
        if (_args != null)  {
            for (final String arg : _args)  {
                param.append(" <").append(arg).append('>');
            }
        }

        if (_description == null)  {
            throw new Error("descriptions for parameter " + param + " not defined!");
        }

        this.description.put(param.toString(), _description.toString());
    }

    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        try {
            final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, false);

            this.prepareParams(paramCache);

            // to be sure....
            MqlUtil_mxJPO.execMql(_context, "verbose off");

            Mode_mxJPO mode = null;

            final Map<Collection<TypeDef_mxJPO>,List<String>> clazz2matches
                    = new HashMap<Collection<TypeDef_mxJPO>,List<String>>();
            final Map<TypeDef_mxJPO,List<String>> clazz2matchesOpp
                    = new HashMap<TypeDef_mxJPO,List<String>>();

            boolean unknown = false;

            UpdateCheck_mxJPO versionInfo = null;


            for (int idx = 0; idx < _args.length; idx++)  {
                final String arg = _args[idx];
                if (this.paramsTypeDefs.containsKey(arg))  {
                    final Collection<TypeDef_mxJPO> clazzes = this.paramsTypeDefs.get(arg);
                    final String name = _args[++idx];
                    List<String> names = clazz2matches.get(clazzes);
                    if (names == null)  {
                        names = new ArrayList<String>();
                        clazz2matches.put(clazzes, names);
                    }
                    names.add(name);
                } else if (this.paramsTypeDefsOpp.containsKey(arg))  {
                    final TypeDef_mxJPO typeDef = this.paramsTypeDefsOpp.get(arg);
                    final String name = _args[++idx];
                    List<String> names = clazz2matchesOpp.get(typeDef);
                    if (names == null)  {
                        names = new ArrayList<String>();
                        clazz2matchesOpp.put(typeDef, names);
                    }
                    names.add(name);
                } else if (this.paramsModes.containsKey(arg)) {
                    final Mode_mxJPO tmpMode = this.paramsModes.get(arg);
                    if (mode != null)  {
                        if ((mode == Mode_mxJPO.HELP) || (tmpMode == Mode_mxJPO.HELP))  {
                            mode = Mode_mxJPO.HELP;
                        } else  {
                            throw new Error("A mode is already defined and could not be defined twice!");
                        }
                    } else  {
                        mode = tmpMode;
                    }
                } else if (this.paramsParameters.containsKey(arg))  {
                    idx = paramCache.evalParameter(this.paramsParameters.get(arg),
                                                   _args,
                                                   idx);
                } else if (this.paramsUpdateChecks.containsKey(arg))  {
                    versionInfo = this.paramsUpdateChecks.get(arg);
                } else  {
                    unknown = true;
                    paramCache.logError("unknown pararameter "  + arg);
                }
            }

            if (unknown || (Mode_mxJPO.HELP == mode) || (mode == null))  {
                this.printHelp(paramCache);
            } else if (Mode_mxJPO.EXPORT == mode)  {
                this.export(paramCache, clazz2matches, clazz2matchesOpp);
            } else if (Mode_mxJPO.IMPORT == mode)  {
                this.update(paramCache, clazz2matches, clazz2matchesOpp, versionInfo);
            } else if (Mode_mxJPO.DELETE == mode)  {
                this.delete(paramCache, clazz2matches, clazz2matchesOpp);
            }

        } catch (final Exception e)  {
            e.printStackTrace(System.out);
            throw e;
        }

    }

    /**
     * Prints the help for the MxUpdate functionality.
     *
     * @param _paramCache   parameter cache
     * @throws IOException
     * @see #description
     */
    private void printHelp(final ParameterCache_mxJPO _paramCache)
            throws IOException
    {
        final String prefix = _paramCache.getValueString(MxUpdate_mxJPO.PARAM_HELP_PREFIX);
        final int lengthLine = _paramCache.getValueInteger(MxUpdate_mxJPO.PARAM_HELP_LENGTHLINE);
        final int lengthParam = _paramCache.getValueInteger(MxUpdate_mxJPO.PARAM_HELP_LENGTHPARAMS);
        final Appendable out = System.out;

        // first print the usage text with the mode parameters
        out.append('\n')
           .append(prefix)
           .append(_paramCache.getValueString(MxUpdate_mxJPO.PARAM_HELP_USAGE).trim())
           .append(' ');
        boolean first = true;
        for (final Mode_mxJPO mode : Mode_mxJPO.values())  {
            final String param = mode.getParameterList(_paramCache).iterator().next();
            if (first)  {
                first = false;
            } else  {
                out.append(" | ");
            }
            if (param.length() > 1)  {
                out.append('-');
            }
            out.append('-');
            out.append(param);
        }
        out.append(" | ... \n\n");

        // print all parameters with description
        for (final Map.Entry<String,String> descLine : this.description.entrySet())  {

            StringBuilder line = new StringBuilder().append(prefix);

            // append list of parameters
            if ((descLine.getKey().length() > 3) && (descLine.getKey().charAt(2) == '_'))  {
                line.append("   ").append(descLine.getKey().substring(3));
            } else  {
                line.append(descLine.getKey());
            }

            // append spaces behind parameter list
            if (line.length() > lengthParam)  {
                out.append(line).append('\n');
                line = new StringBuilder().append(prefix);
            }
            for (int i = line.length(); i < lengthParam; i++)  {
                line.append(' ');
            }

            // append parameter description
            first = true;
            for (final String partDesc : descLine.getValue().toString().split("\n"))  {
                if (first == true)  {
                    first = false;
                } else  {
                    out.append(line).append('\n');
                    line = new StringBuilder().append(prefix);
                    for (int i = 1; i < lengthParam; i++)  {
                        line.append(' ');
                    }
                }
                for (final String desc : partDesc.split(" "))  {
                    if (!"".equals(desc))  {
                        if (line.length() > lengthLine)  {
                            out.append(line).append('\n');
                            line = new StringBuilder().append(prefix);
                            for (int i = 1; i < lengthParam; i++)  {
                                line.append(' ');
                            }
                        }
                        line.append(' ').append(desc);
                    }
                }
            }
            out.append(line).append('\n');
        }

        // print copyright
        for (final String copyright : _paramCache.getValueString(PARAM_HELP_COPYRIGHT).split("\n"))  {
            out.append(prefix).append(copyright).append('\n');
        }
        out.append('\n');
    }

    /**
     * Exports matching administration objects to given path.
     *
     * @param _paramCache       parameter cache
     * @param _clazz2matches    classes and their matched to export
     * @throws Exception if none path or more than one path is defined or if
     *                   the export failed
     */
    protected void export(final ParameterCache_mxJPO _paramCache,
                          final Map<Collection<TypeDef_mxJPO>,List<String>> _clazz2matches,
                          final Map<TypeDef_mxJPO,List<String>> _clazz2matchesOpp)
            throws Exception
    {
        final Collection<String> paths = _paramCache.getValueList(MxUpdate_mxJPO.PARAM_PATH);

        // check for definition of min. / max. one path
        if (paths.isEmpty())  {
            throw new Exception("no path is defined, but required for the export!");
        }
        if (paths.size() > 1)  {
            throw new Exception("more than one path is defined, but maximum is allowed for the export!");
        }
        final String pathStr = paths.iterator().next();

        // evaluate all matching administration objects
        final Map<TypeDef_mxJPO,Set<String>> clazz2names
                = this.getMatching(_paramCache, _clazz2matches, _clazz2matchesOpp);

        // export
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2names.entrySet())  {
            for (final String name : entry.getValue())  {
                final AbstractObject_mxJPO instance = entry.getKey().newTypeInstance(name);
                final File path = new File(pathStr + File.separator + instance.getPath());
                _paramCache.logInfo("export "+instance.getTypeDef().getLogging() + " '" + name + "'");
                instance.export(_paramCache, path);
            }
        }
    }

    /**
     *
     * @param _paramCache       parameter cache
     * @param _paths
     * @param _clazz2matches
     */
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final Map<Collection<TypeDef_mxJPO>,List<String>> _clazz2matches,
                          final Map<TypeDef_mxJPO,List<String>> _clazz2matchesOpp,
                          final UpdateCheck_mxJPO _versionInfo)
            throws Exception
    {
        // get all matching files depending on the update classes
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2names
                = this.evalMatches(_paramCache, _clazz2matches, _clazz2matchesOpp);

        // evaluate for existing administration objects
        final Collection<String> wildCardMatch = new HashSet<String>();
        wildCardMatch.add("*");
        final Map<TypeDef_mxJPO,Set<String>> existingNames = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final TypeDef_mxJPO clazz : clazz2names.keySet())  {
            if (!existingNames.containsKey(clazz))  {
                final AbstractObject_mxJPO instance = clazz.newTypeInstance(null);
                existingNames.put(clazz, instance.getMxNames(_paramCache));
            }
        }
        // create if needed (and not in the list of existing objects
        for (final TypeDef_mxJPO clazz : _paramCache.getMapping().getAllTypeDefs())  {
            final Map<File,String> clazzMap = clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final Set<String> existings = existingNames.get(clazz);
                    if (!existings.contains(fileEntry.getValue()))  {
                         final AbstractObject_mxJPO instance = clazz.newTypeInstance(fileEntry.getValue());
                         _paramCache.logInfo("create "+instance.getTypeDef().getLogging()
                                 + " '" + fileEntry.getValue() + "'");
                        instance.create(_paramCache);
                    }
                }
            }
        }
        // update
        for (final TypeDef_mxJPO clazz : _paramCache.getMapping().getAllTypeDefs())  {
            final Map<File,String> clazzMap = clazz2names.get(clazz);
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
                    if (update)  {
                        boolean commit = false;
                        final boolean transActive = _paramCache.getContext().isTransactionActive();
                        try  {
                            if (!transActive)  {
                                _paramCache.getContext().start(true);
                            }
                            instance.update(_paramCache,
                                            fileEntry.getKey(),
                                            version);
                            if (!transActive)  {
                                _paramCache.getContext().commit();
                            }
                            commit = true;
                        } finally  {
                            if (!commit && !transActive && _paramCache.getContext().isTransactionActive())  {
                                _paramCache.getContext().abort();
                            }
                        }
                    }
                }
            }
        }
    }


    protected void delete(final ParameterCache_mxJPO _paramCache,
                          final Map<Collection<TypeDef_mxJPO>,List<String>> _clazz2matches,
                          final Map<TypeDef_mxJPO,List<String>> _clazz2matchesOpp)
            throws Exception
    {
        // check for definition of min. / max. one path
        if (_paramCache.getValueList(MxUpdate_mxJPO.PARAM_PATH).isEmpty())  {
            throw new Exception("no path is defined, but required for the delete!");
        }

        // evaluate all matching administration objects
        final Map<TypeDef_mxJPO,Set<String>> clazz2MxNames
                = this.getMatching(_paramCache, _clazz2matches, _clazz2matchesOpp);

        // get all matching files depending on the update classes
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2FileNames
                = this.evalMatches(_paramCache, _clazz2matches, _clazz2matchesOpp);

        // and now loop throw the list of file names and compare to existing
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2MxNames.entrySet())  {
            final Collection<String> fileNames = clazz2FileNames.containsKey(entry.getKey())
                                                 ? clazz2FileNames.get(entry.getKey()).values()
                                                 : null;
            for (final String name : entry.getValue())  {
                if ((fileNames == null) || !fileNames.contains(name))  {
                    _paramCache.logInfo("delete " + entry.getKey().getLogging() + " '" + name + "'");
                    boolean commit = false;
                    final boolean transActive = _paramCache.getContext().isTransactionActive();
                    try  {
                        if (!transActive)  {
                            _paramCache.getContext().start(true);
                        }
                        entry.getKey().newTypeInstance(name).delete(_paramCache);
                        if (!transActive)  {
                            _paramCache.getContext().commit();
                        }
                        commit = true;
                    } finally  {
                        if (!commit && !transActive && _paramCache.getContext().isTransactionActive())  {
                            _paramCache.getContext().abort();
                        }
                    }
                }
            }
        }
    }

    /**
     * Evaluates for matching files with the names depending on the update
     * classes.
     *
     * @param _paramCache       parameter cache with paths to check (or no path
     *                          parameter)
     * @param _clazz2matches    all classes with the depending matches (if
     *                          paths are defined, the names must match against
     *                          the Matrix name; otherwise the found files must
     *                          match)
     * @return map of update classes and the depending files with their names
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see #PARAM_IGNOREFILE
     * @see #PARAM_IGNOREPATH
     * @see #PARAM_PATH
     */
    protected Map<TypeDef_mxJPO,Map<File,String>> evalMatches(final ParameterCache_mxJPO _paramCache,
                                                              final Map<Collection<TypeDef_mxJPO>,List<String>> _clazz2matches,
                                                              final Map<TypeDef_mxJPO,List<String>> _clazz2matchesOpp)
            throws Exception
    {
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2names = new HashMap<TypeDef_mxJPO,Map<File,String>>();

        // get path parameters
        final Collection<String> ignoreFiles = _paramCache.getValueList(MxUpdate_mxJPO.PARAM_IGNOREFILE);
        final Collection<String> ignorePaths = _paramCache.getValueList(MxUpdate_mxJPO.PARAM_IGNOREPATH);
        final Collection<String> paths = _paramCache.getValueList(MxUpdate_mxJPO.PARAM_PATH);

        // if no path is defined, the paths are directly defined at the objects
        // to import
        if ((paths == null) || paths.isEmpty())  {
            for (final Map.Entry<Collection<TypeDef_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
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
                // (and NOT last file match)
                final Set<File> matchedFiles = new HashSet<File>();
                final boolean lastFileMatchNeeded = this.evalMatches(_paramCache,
                                                                     entry.getKey(),
                                                                     false,
                                                                     clazz2names,
                                                                     allFiles,
                                                                     matchedFiles,
                                                                     null);
                // get all matching files depending on the update classes
                // (and LAST file match)
                if (lastFileMatchNeeded)  {
                    final Set<File> notMatchedFiles = new HashSet<File>();
                    for (final File file : allFiles)  {
                        if (!matchedFiles.contains(file))  {
                            notMatchedFiles.add(file);
                        }
                    }
                    this.evalMatches(_paramCache,
                                     entry.getKey(),
                                     true,
                                     clazz2names,
                                     notMatchedFiles,
                                     null,
                                     null);
                }
            }
        // path parameter is defined
        } else  {
            /// get all files
            final Set<File> allFiles = new HashSet<File>();
            for (final String path : paths)  {
                allFiles.addAll(this.getAllFiles(new File(path), ignorePaths, ignoreFiles));
            }
            final Set<File> matchedFiles = new HashSet<File>();
            // get all matching files depending on the update classes
            // (and NOT last file match)
            boolean lastFileMatchNeeded = false;
            for (final Map.Entry<Collection<TypeDef_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
                lastFileMatchNeeded |= this.evalMatches(_paramCache,
                                                        entry.getKey(),
                                                        false,
                                                        clazz2names,
                                                        allFiles,
                                                        matchedFiles,
                                                        entry.getValue());
            }
            // get all matching files depending on the update classes
            // (and LAST file match)
            if (lastFileMatchNeeded)  {
                final Set<File> notMatchedFiles = new HashSet<File>();
                for (final File file : allFiles)  {
                    if (!matchedFiles.contains(file))  {
                        notMatchedFiles.add(file);
                    }
                }
                for (final Map.Entry<Collection<TypeDef_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
                    this.evalMatches(_paramCache,
                                     entry.getKey(),
                                     true,
                                     clazz2names,
                                     notMatchedFiles,
                                     null,
                                     entry.getValue());
                }
            }
        }

        // and now remove ignored matches
        final Map<TypeDef_mxJPO,Map<File,String>> ret = new HashMap<TypeDef_mxJPO,Map<File,String>>();
        for (final Map.Entry<TypeDef_mxJPO,Map<File,String>> entry : clazz2names.entrySet())  {
            final Collection<String> matchOpps = _clazz2matchesOpp.get(entry.getKey());
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
     * @param _paramCache       parameter cache
     * @param _typeDefs
     * @param _fileMatchLast
     * @param _clazz2names
     * @param _allFiles
     * @param _matchedFiles
     * @param _matches
     * @return ??
     * @throws Exception if the evaluate for the match fails
     */
    private boolean evalMatches(final ParameterCache_mxJPO _paramCache,
                                final Collection<TypeDef_mxJPO> _typeDefs,
                                final boolean _fileMatchLast,
                                final Map<TypeDef_mxJPO,Map<File,String>> _clazz2names,
                                final Set<File> _allFiles,
                                final Set<File> _matchedFiles,
                                final Collection<String> _matches)
            throws Exception
    {
        boolean foundOther = false;
        for (final TypeDef_mxJPO typeDef : _typeDefs)  {
            if (typeDef.isFileMatchLast() != _fileMatchLast)  {
                foundOther = true;
            } else  {
                final AbstractObject_mxJPO instance = typeDef.newTypeInstance(null);
                for (final File file : _allFiles)  {
                    final String mxName = instance.extractMxName(_paramCache, file);
                    if (mxName != null)  {
                        Map<File,String> tmp = _clazz2names.get(typeDef);
                        if (tmp == null)  {
                            tmp = new TreeMap<File,String>();
                            _clazz2names.put(typeDef, tmp);
                        }
                        if (_matches == null)
                        {
                            tmp.put(file, mxName);
                            if (_matchedFiles != null)  {
                                _matchedFiles.add(file);
                            }
                        }
                        else
                        {
                            for (final String match : _matches)  {
                                if (instance.matchMxName(_paramCache, mxName, match))  {
                                    tmp.put(file, mxName);
                                    if (_matchedFiles != null)  {
                                        _matchedFiles.add(file);
                                    }
                                }
                            }
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
    protected Set<File> getAllFiles(final File _path,
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

    /**
     * Evaluate all matching administration objects within MX.
     *
     * @param _paramCache       parameter cache
     * @param _clazz2matches    map of collection classes and the depending
     *                          list of string which must match
     * @return map of classes and a set of matching names for this classes
     * @throws Exception if the match fails
     * @see #export(ParameterCache_mxJPO, Map)
     * @see #delete(ParameterCache_mxJPO, Map)
     */
    protected Map<TypeDef_mxJPO,Set<String>> getMatching(final ParameterCache_mxJPO _paramCache,
                                                         final Map<Collection<TypeDef_mxJPO>,List<String>> _clazz2matches,
                                                         final Map<TypeDef_mxJPO,List<String>> _clazz2matchesOpp)
            throws Exception
    {
        // first sort the matches depending on the type definition
        final Map<TypeDef_mxJPO,Set<String>> typeDef2Matches = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final Map.Entry<Collection<TypeDef_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
            for (final TypeDef_mxJPO typeDef : entry.getKey())  {
                Set<String> matches = typeDef2Matches.get(typeDef);
                if (matches == null)  {
                    matches = new HashSet<String>();
                    typeDef2Matches.put(typeDef, matches);
                }
                matches.addAll(entry.getValue());
            }
        }
        // and now depending on the type definition prepare return list
        final Map<TypeDef_mxJPO,Set<String>> clazz2names = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : typeDef2Matches.entrySet())  {
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
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2names.entrySet())  {
            final Collection<String> matchOpps = _clazz2matchesOpp.get(entry.getKey());
            if ((matchOpps != null) && !matchOpps.isEmpty())  {
                final Set<String> files = new TreeSet<String>();
                for (final String fileEntry : entry.getValue())  {
                    boolean allowed = true;
                    for (final String matchOpp : matchOpps)  {
                        if (StringUtil_mxJPO.match(fileEntry, matchOpp))  {
                            allowed = false;
                            break;
                        }
                    }
                    if (allowed)  {
                        files.add(fileEntry);
                    }
                }
                ret.put(entry.getKey(), files);
            } else  {
                ret.put(entry.getKey(), entry.getValue());
            }
        }

        return ret;
    }
}
