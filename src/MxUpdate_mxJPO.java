/*
 * Copyright 2008 The MxUpdate Team
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;

/**
 * <table>
 * <tr>
 * <th></th><th></th><th></th>
 * <tr>
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class MxUpdate_mxJPO
{
    /**
     * Stored the descriptions of all parameters.
     */
    private final static StringBuilder DESCRIPTION = new StringBuilder();

    private enum Mode
    {
        /**
         * Mode 'import' used to import defined administrational objects from
         * file system into Matrix.
         */
        IMPORT,
        /**
         * Mode 'export' used to export defined administration objects from
         * Matrix into a file system.
         */
        EXPORT,
        /**
         * Prints out the help description.
         */
        HELP;
    }

    /**
     * Holds the mapping between the parameter and mode.
     */
    private final static Map<String,Mode> PARAM_MODE = new HashMap<String,Mode>();

    /**
     * All parameters related to export / import are stored in this map. The
     * key is the parameter (including the '-'), the value the related class.
     */
    private final static Map<String,Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>> PARAMS
            = new HashMap<String,Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>>();

    /**
     * Holds all classes used for all exports / imports. The set is needed that
     * the parameter '-a' (--admin) could be defined.
     */
    private final static Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> PARAMS_ADMIN
            = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();

    /**
     * Holds all classes used for the data model import / export.
     */
    private final static Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> PARAMS_DM
            = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();

    /**
     * Holds all classes used for the data model import / export.
     */
    private final static Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> PARAMS_USER
            = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();

    /**
     * Holds all classes used for the user interface import / export.
     */
    private final static Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> PARAMS_UI
            = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();

    static  {
        ////////////////////////////////////////////////////////////////////////
        // mode (export / import / help)

        PARAM_MODE.put("-e", Mode.EXPORT);
        PARAM_MODE.put("--export", Mode.EXPORT);
        appendDescription("Export Mode", "-e", "--export");
        PARAM_MODE.put("-u", Mode.IMPORT);
        PARAM_MODE.put("--update", Mode.IMPORT);
        appendDescription("Update Mode", "-u", "--update");
        PARAM_MODE.put("-h", Mode.HELP);
        PARAM_MODE.put("--help", Mode.HELP);

        ////////////////////////////////////////////////////////////////////////
        // admin

        PARAMS.put("-a", PARAMS_ADMIN);
        PARAMS.put("--admin", PARAMS_ADMIN);
        PARAMS.put("--all", PARAMS_ADMIN);

        ////////////////////////////////////////////////////////////////////////
        // data model
/*
process: 's' --process
*/

        PARAMS.put("-d", PARAMS_DM);
        PARAMS.put("--datamodel", PARAMS_DM);
        appendDescription("Export / Import of data model administrational objects.",
                          "-d","--dm", "--datamodel");
        defineParameter('b', PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Attribute_mxJPO.class,
                        "Export / Import of attributes.",
                        "attribute", "attrib", "attr", "att");
        defineParameter(null, PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Expression_mxJPO.class,
                        "Export / Import of expressions.",
                        "expression", "expr", "exp");
        defineParameter(null, PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Format_mxJPO.class,
                        "Export / Import of formats.",
                        "format");
        defineParameter(null, PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.NumberGenerator_mxJPO.class,
                        "Export / Import of number generators.",
                        "numbergenerator");
        defineParameter(null, PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.ObjectGenerator_mxJPO.class,
                        "Export / Import of object generators.",
                        "objectgenerator");
        defineParameter('r', PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Relationship_mxJPO.class,
                        "Export / Import of relationships.",
                        "relation", "relationship");
        defineParameter(null, PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Rule_mxJPO.class,
                        "Export / Import of rules.",
                        "rule");
        defineParameter('t', PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Type_mxJPO.class,
                        "Export / Import of types.",
                        "type");
        defineParameter('p', PARAMS_DM,
                         net.sourceforge.mxupdate.update.datamodel.Policy_mxJPO.class,
                         "Export / Import of policies.",
                         "policy");
        defineParameter('g', PARAMS_DM,
                        net.sourceforge.mxupdate.update.datamodel.Trigger_mxJPO.class,
                        "Export / Import of triggers.",
                        "trigger", "trig");

        ////////////////////////////////////////////////////////////////////////
        // user
/*
--person
*/
        PARAMS.put("--user", PARAMS_USER);
        appendDescription("Export / Import of user administrational objects.",
                          "--user");
        defineParameter(null, PARAMS_USER,
                        net.sourceforge.mxupdate.update.user.Association_mxJPO.class,
                        "Export / Import of associations.",
                        "association", "asso");
        defineParameter(null, PARAMS_USER,
                        net.sourceforge.mxupdate.update.user.Group_mxJPO.class,
                        "Export / Import of groups.",
                        "group");
        defineParameter(null, PARAMS_USER,
                        net.sourceforge.mxupdate.update.user.Role_mxJPO.class,
                        "Export / Import of roles.",
                        "role");

        ////////////////////////////////////////////////////////////////////////
        // program

        defineParameter('j', null,
                        net.sourceforge.mxupdate.update.program.JPO_mxJPO.class,
                        "Export / Import of JPOs.",
                        "jpo");
        defineParameter(null, null,
                        net.sourceforge.mxupdate.update.program.Program_mxJPO.class,
                        "Export / Import of programs.",
                        "program");

        ////////////////////////////////////////////////////////////////////////
        // user interface

        PARAMS.put("-u", PARAMS_UI);
        PARAMS.put("--ui", PARAMS_UI);
        PARAMS.put("--userinterface", PARAMS_UI);
        appendDescription("Export / Import of user interface administrational objects.",
                          "-u","--ui", "--userinterface");
        defineParameter(null, PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Channel_mxJPO.class,
                        "Export / Import of channels.",
                        "channel");
        defineParameter('c', PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Command_mxJPO.class,
                        "Export / Import of commands.",
                        "command");
        defineParameter('f', PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Form_mxJPO.class,
                        "Export / Import of web forms.",
                        "webform", "form");
        defineParameter('i', PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Inquiry_mxJPO.class,
                        "Export / Import of inquiries.",
                        "inquiry");
        defineParameter('m', PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Menu_mxJPO.class,
                        "Export / Import of menus.",
                        "menu");
        defineParameter(null, PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Portal_mxJPO.class,
                        "Export / Import of portal.",
                        "portal");
        defineParameter('w', PARAMS_UI,
                        net.sourceforge.mxupdate.update.userinterface.Table_mxJPO.class,
                        "Export / Import of web tables.",
                        "webtable", "table");
    }

    /**
     *
     * @param _shortParam       short parameter (or <code>null</code> if not
     *                          defined)
     * @param _paramsList       to which parameter list must the parameter
     *                          appended
     * @param _clazz            class implementing the import / export
     * @param _description      description of the paraemter
     * @param _longParams       list of long parameters strings
     * @throws Error if a short parameter is already defined
     */
    private static void defineParameter(final Character _shortParam,
                                        final Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> _paramsList,
                                        final Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO> _clazz,
                                        final String _description,
                                        final String... _longParams)
    {
        final List<String> allParamStrings = new ArrayList<String>();
        // add to set of all classes parameters set
        PARAMS_ADMIN.add(_clazz);
        // add to given set of parameters
        if (_paramsList != null)  {
            _paramsList.add(_clazz);
        }
        Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> tmp = null;
        // check for short parameter and test for double definition
        if (_shortParam != null)  {
            final String shortParam = "-" + _shortParam;
            tmp = PARAMS.get(shortParam);
            if (tmp != null)  {
                throw new Error("double definition of short parameter '" + shortParam
                                + "'! Found:\n" + tmp + "\nNew Definition:\n" + _clazz);
            }
            tmp = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();
            PARAMS.put(shortParam, tmp);
            allParamStrings.add(shortParam);
        } else  {
            tmp = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();
        }
        tmp.add(_clazz);
        // all long parameters
        for (final String param : _longParams)  {
            final String paramStr = "--" + param;
            tmp = PARAMS.get(paramStr);
            if (tmp == null)  {
                tmp = new HashSet<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>>();
                PARAMS.put(paramStr, tmp);
            }
            tmp.add(_clazz);
            allParamStrings.add(paramStr);
        }

        // store description
        appendDescription(_description, allParamStrings);
    }

    /**
     * Appends a description for a defined list of parameters.
     *
     * @param _description      description to append
     * @param _params           related parameters
     */
    private static void appendDescription(final String _description,
                                          final List<String> _params)
    {
        for (final String paramString : _params)  {
            DESCRIPTION.append(paramString);
        }
        DESCRIPTION.append(' ').append(_description).append('\n');
    }

    /**
     * Appends a description for given parameters. The method is a wrapper
     * method for {@link #appendDescription(String, List)}.
     *
     * @param _description      description to append
     * @param _params           array of parameters to append
     * @see #appendDescription(String, List) used method to append parameter
     *                                       description
     */
    private static void appendDescription(final String _description,
                                          final String... _params)
    {
        final List<String> params = new ArrayList<String>(_params.length);
        for (final String param : _params)  {
            params.add(param);
        }
        appendDescription(_description, params);
    }

    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        try {

            Mode mode = null;

final Map<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,List<String>> clazz2matches
        = new HashMap<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,List<String>>();
//System.err.println("PARAM_MODE="  + Mode.IMPORT);

boolean unknown = false;
String pathStr = null;


        for (int idx = 0; idx < _args.length; idx++)  {
//System.out.println(""+idx+"="+_args[idx]+"="+PARAMS.get(_args[idx]));
            final Set<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>> clazzes = PARAMS.get(_args[idx]);
            if (clazzes != null)  {
                idx++;
                final String name = _args[idx];
                for (final Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO> clazz : clazzes)  {
                    List<String> names = clazz2matches.get(clazz);
                    if (names == null)  {
                        names = new ArrayList<String>();
                        clazz2matches.put(clazz, names);
                    }
                    names.add(name);
                }
            } else if (PARAM_MODE.containsKey(_args[idx])) {
                final Mode tmpMode = PARAM_MODE.get(_args[idx]);
                if (mode != null)  {
                    if ((mode == Mode.HELP) || (tmpMode == Mode.HELP))  {
                        mode = Mode.HELP;
                    } else  {
                        throw new Error("A mode is already defined and could not be defined twice!");
                    }
                } else  {
                    mode = PARAM_MODE.get(_args[idx]);
                }
            } else if ("--path".equals(_args[idx]))  {
                idx++;
                pathStr = _args[idx];
            } else  {
                unknown = true;
System.err.println("unknown pararameter "  + _args[idx]);
            }
        }

//System.out.println("mode="+mode);
//System.out.println("clazz2matches="+clazz2matches);

if (unknown || (Mode.HELP == mode) || (mode == null))  {
    System.out.println("" + DESCRIPTION);
} else if (Mode.EXPORT == mode)  {
    final Map<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Set<String>> clazz2names
            = new HashMap<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Set<String>>();
    for (final Map.Entry<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,List<String>> entry : clazz2matches.entrySet())  {
        net.sourceforge.mxupdate.update.AbstractObject_mxJPO instance = entry.getKey().newInstance();
        clazz2names.put(entry.getKey(), instance.getMatchingNames(_context, entry.getValue()));
    }
    for (final Map.Entry<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Set<String>> entry : clazz2names.entrySet())  {
        for (final String name : entry.getValue())  {
            net.sourceforge.mxupdate.update.AbstractObject_mxJPO instance = entry.getKey().newInstance();
            final File path = new File(pathStr + File.separator + instance.getPath());
System.out.println("export "+instance.getInfoAnno().description() + " '" + name + "'");
            instance.export(_context, path, name);
        }
    }
} else if (Mode.IMPORT == mode) {
    final Set<File> allFiles = getAllFiles(new File(pathStr));
    // get all matching files depending on the update classes
    final Map<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Map<File,String>> clazz2names
            = new HashMap<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Map<File,String>>();
    for (final Map.Entry<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,List<String>> entry : clazz2matches.entrySet())  {
        net.sourceforge.mxupdate.update.AbstractObject_mxJPO instance = entry.getKey().newInstance();
        clazz2names.put(entry.getKey(), instance.getMatchingFileNames(allFiles, entry.getValue()));
    }
    // create if needed
    final Collection<String> wildCardMatch = new HashSet<String>();
    wildCardMatch.add("*");
    for (final Map.Entry<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Map<File,String>> entry : clazz2names.entrySet())  {
        for (final Map.Entry<File, String> fileEntry : entry.getValue().entrySet())  {
            net.sourceforge.mxupdate.update.AbstractObject_mxJPO instance = entry.getKey().newInstance();
            final Set<String> existings = instance.getMatchingNames(_context, wildCardMatch);
            if (!existings.contains(fileEntry.getValue()))  {
System.out.println("create "+instance.getInfoAnno().description() + " '" + fileEntry.getValue() + "'");
                instance.create(_context, fileEntry.getKey(), fileEntry.getValue());
            }
        }
    }
    // update
    for (final Map.Entry<Class<? extends net.sourceforge.mxupdate.update.AbstractObject_mxJPO>,Map<File,String>> entry : clazz2names.entrySet())  {
        for (final Map.Entry<File, String> fileEntry : entry.getValue().entrySet())  {
            net.sourceforge.mxupdate.update.AbstractObject_mxJPO instance = entry.getKey().newInstance();
System.out.println("check "+instance.getInfoAnno().description() + " '" + fileEntry.getValue() + "'");
            instance.update(_context, fileEntry.getValue(), fileEntry.getKey());
        }
    }
}

        } catch (Exception e)  {
            e.printStackTrace();
        }

    }

    /**
     * Evaluates depending on given path all files in the sub directories.
     *
     * @param _path     path for which the files are searched
     * @return set of all found files
     */
    protected static Set<File> getAllFiles(final File _path)
    {
        final Set<File> ret = new HashSet<File>();

        if (_path.isDirectory())  {
            for (final File file : _path.listFiles())  {
                if (file.isDirectory())  {
                    ret.addAll(getAllFiles(file));
                } else  {
                    ret.add(file);
                }
            }
        }

        return ret;
    }
}
