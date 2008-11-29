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
import java.util.TreeMap;

import matrix.db.Context;

import net.sourceforge.mxupdate.update.AbstractObject_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Attribute_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Expression_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Format_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.NumberGenerator_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.ObjectGenerator_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Policy_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Relationship_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Rule_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Trigger_mxJPO;
import net.sourceforge.mxupdate.update.datamodel.Type_mxJPO;
import net.sourceforge.mxupdate.update.user.Association_mxJPO;
import net.sourceforge.mxupdate.update.user.Group_mxJPO;
import net.sourceforge.mxupdate.update.user.Role_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Channel_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Command_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Form_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Inquiry_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Menu_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Portal_mxJPO;
import net.sourceforge.mxupdate.update.userinterface.Table_mxJPO;
import net.sourceforge.mxupdate.util.MqlUtil_mxJPO;

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
    private static final Map<String,String> DESCRIPTION = new TreeMap<String,String>();

    /**
     * Defines the length of the parameters strings.
     *
     * @see #appendDescription(String, List)
     */
    private static final int LENGTH_DESC_PARAMS = 42;

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
    private final static Map<String,Set<Class<? extends AbstractObject_mxJPO>>> PARAMS
            = new HashMap<String,Set<Class<? extends AbstractObject_mxJPO>>>();

    /**
     * Holds all classes used for all exports / imports. The set is needed that
     * the parameter '-a' (--admin) could be defined.
     */
    private final static Set<Class<? extends AbstractObject_mxJPO>> PARAMS_ADMIN
            = new HashSet<Class<? extends AbstractObject_mxJPO>>();

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
        final Set<Class<? extends AbstractObject_mxJPO>> dm = new HashSet<Class<? extends AbstractObject_mxJPO>>();
        defineParameter('d', dm,
                        null,
                        "Export / Import of data model administrational objects.",
                        "dm", "datamodel");
        defineParameter('b', dm,
                        Attribute_mxJPO.class,
                        "Export / Import of attributes.",
                        "attribute", "attrib", "attr", "att");
        defineParameter(null, dm,
                        Expression_mxJPO.class,
                        "Export / Import of expressions.",
                        "expression", "expr", "exp");
        defineParameter(null, dm,
                        Format_mxJPO.class,
                        "Export / Import of formats.",
                        "format");
        defineParameter(null, dm,
                        NumberGenerator_mxJPO.class,
                        "Export / Import of number generators.",
                        "numbergenerator");
        defineParameter(null, dm,
                        ObjectGenerator_mxJPO.class,
                        "Export / Import of object generators.",
                        "objectgenerator");
        defineParameter('r', dm,
                        Relationship_mxJPO.class,
                        "Export / Import of relationships.",
                        "relation", "relationship");
        defineParameter(null, dm,
                        Rule_mxJPO.class,
                        "Export / Import of rules.",
                        "rule");
        defineParameter('t', dm,
                        Type_mxJPO.class,
                        "Export / Import of types.",
                        "type");
        defineParameter('p', dm,
                         Policy_mxJPO.class,
                         "Export / Import of policies.",
                         "policy");
        defineParameter('g', dm,
                        Trigger_mxJPO.class,
                        "Export / Import of triggers.",
                        "trigger", "trig");

        ////////////////////////////////////////////////////////////////////////
        // user
/*
--person
*/
        final Set<Class<? extends AbstractObject_mxJPO>> user = new HashSet<Class<? extends AbstractObject_mxJPO>>();
        defineParameter(null, user,
                        null,
                        "Export / Import of user administrational objects.",
                        "user");
        defineParameter(null, user,
                        Association_mxJPO.class,
                        "Export / Import of associations.",
                        "association", "asso");
        defineParameter(null, user,
                        Group_mxJPO.class,
                        "Export / Import of groups.",
                        "group");
        defineParameter(null, user,
                        Role_mxJPO.class,
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
                        "program", "prog");

        ////////////////////////////////////////////////////////////////////////
        // user interface

        final Set<Class<? extends AbstractObject_mxJPO>> ui = new HashSet<Class<? extends AbstractObject_mxJPO>>();
        defineParameter(null, ui,
                        null,
                        "Export / Import of user interface administrational objects.",
                        "ui", "userinterface");
        defineParameter(null, ui,
                        Channel_mxJPO.class,
                        "Export / Import of channels.",
                        "channel");
        defineParameter('c', ui,
                        Command_mxJPO.class,
                        "Export / Import of commands.",
                        "command");
        defineParameter('f', ui,
                        Form_mxJPO.class,
                        "Export / Import of web forms.",
                        "webform", "form");
        defineParameter('i', ui,
                        Inquiry_mxJPO.class,
                        "Export / Import of inquiries.",
                        "inquiry");
        defineParameter('m', ui,
                        Menu_mxJPO.class,
                        "Export / Import of menus.",
                        "menu");
        defineParameter(null, ui,
                        Portal_mxJPO.class,
                        "Export / Import of portal.",
                        "portal");
        defineParameter('w', ui,
                        Table_mxJPO.class,
                        "Export / Import of web tables.",
                        "webtable", "table");
    }

    /**
     *
     * @param _shortParam       short parameter (or <code>null</code> if not
     *                          defined)
     * @param _paramsList       to which parameter list must the parameter
     *                          appended
     * @param _clazz            class implementing the import / export (or
     *                          <code>null</code> if the complete parameter
     *                          list must be used)
     * @param _description      description of the parameter
     * @param _longParams       list of long parameters strings
     * @throws Error if a short parameter is already defined
     */
    private static void defineParameter(final Character _shortParam,
                                        final Set<Class<? extends AbstractObject_mxJPO>> _paramsList,
                                        final Class<? extends AbstractObject_mxJPO> _clazz,
                                        final String _description,
                                        final String... _longParams)
    {
        final List<String> allParamStrings = new ArrayList<String>();
        final Set<Class<? extends AbstractObject_mxJPO>> tmp = (_clazz == null)
                ? _paramsList
                : new HashSet<Class<? extends AbstractObject_mxJPO>>();

        if (_clazz != null)  {
            // add to set of all classes parameters set
            PARAMS_ADMIN.add(_clazz);
            // add to given set of parameters
            if (_paramsList != null)  {
                _paramsList.add(_clazz);
            }
            tmp.add(_clazz);
        }

        // check for short parameter and test for double definition
        if (_shortParam != null)  {
            final String shortParam = "-" + _shortParam;
            if (PARAMS.containsKey(shortParam) || PARAM_MODE.containsKey(shortParam))  {
                throw new Error("double definition of short parameter '" + shortParam
                                + "'! Found:\n" + PARAMS.get(shortParam) + "\nNew Definition:\n" + tmp);
            }
            PARAMS.put(shortParam, tmp);
            allParamStrings.add(shortParam);
        }
        // all long parameters
        for (final String param : _longParams)  {
            final String paramStr = "--" + param;
            if (PARAMS.containsKey(paramStr) || PARAM_MODE.containsKey(paramStr))  {
                throw new Error("double definition of short parameter '" + paramStr
                        + "'! Found:\n" + PARAMS.get(paramStr) + "\nNew Definition:\n" + tmp);
            }
            PARAMS.put(paramStr, tmp);
            allParamStrings.add(paramStr);
        }

        // store description
        appendDescription(_description, allParamStrings, "MATCH");
    }

    /**
     * Appends a description for a defined list of parameters.
     *
     * @param _description      description to append
     * @param _params           related parameters
     * @param _argument         text of the argument for the list of parameters
     *                          (or <code>null</code> if not defined)
     */
    private static void appendDescription(final String _description,
                                          final List<String> _params,
                                          final String _argument)
    {
        // check if first parameter is not a short parameter
        final String firstParam = _params.get(0);
        final char prefix;
        final StringBuilder line = new StringBuilder().append(' ');
        if (firstParam.charAt(1) == '-')  {
            prefix = firstParam.charAt(2);
            line.append("   ");
        } else  {
            prefix = firstParam.charAt(1);
        }

        // append all parameters
        boolean first = true;
        for (final String paramString : _params)  {
            if (first)  {
                first = false;
            } else  {
                line.append(',');
            }
            line.append(paramString);
        }

        // append arguments
        if (_argument != null)  {
            line.append(" <").append(_argument).append('>');
        }

        // append spaces
        if (line.length() > LENGTH_DESC_PARAMS)  {
            line.append("\n ");
            for (int i = 1; i < LENGTH_DESC_PARAMS; i++)  {
                line.append(' ');
            }
        } else  {
            for (int i = line.length(); i < LENGTH_DESC_PARAMS; i++)  {
                line.append(' ');
            }
        }

        line.append(_description);

        DESCRIPTION.put("" + prefix + line, line.toString());
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
        appendDescription(_description, params, null);
    }

    /**
     * Prints the help for the MxUpdate functionality.
     *
     * @see #DESCRIPTION
     */
    private void printHelp()
    {
        System.out.println("usage: exec prog MxUpdate -e | -u ....\n");
        for (final String line : DESCRIPTION.values())  {
            System.out.println(line);
        }
    }

    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        try {
            // to be sure....
            MqlUtil_mxJPO.execMql(_context, "verbose off");

            Mode mode = null;

final Map<Class<? extends AbstractObject_mxJPO>,List<String>> clazz2matches
        = new HashMap<Class<? extends AbstractObject_mxJPO>,List<String>>();
//System.err.println("PARAM_MODE="  + Mode.IMPORT);

boolean unknown = false;
String pathStr = null;


        for (int idx = 0; idx < _args.length; idx++)  {
//System.out.println(""+idx+"="+_args[idx]+"="+PARAMS.get(_args[idx]));
            final Set<Class<? extends AbstractObject_mxJPO>> clazzes = PARAMS.get(_args[idx]);
            if (clazzes != null)  {
                idx++;
                final String name = _args[idx];
                for (final Class<? extends AbstractObject_mxJPO> clazz : clazzes)  {
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
    printHelp();
} else if (Mode.EXPORT == mode)  {
    final Map<Class<? extends AbstractObject_mxJPO>,Set<String>> clazz2names
            = new HashMap<Class<? extends AbstractObject_mxJPO>,Set<String>>();
    for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,List<String>> entry : clazz2matches.entrySet())  {
        AbstractObject_mxJPO instance = entry.getKey().newInstance();
        clazz2names.put(entry.getKey(), instance.getMatchingNames(_context, entry.getValue()));
    }
    for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,Set<String>> entry : clazz2names.entrySet())  {
        for (final String name : entry.getValue())  {
            AbstractObject_mxJPO instance = entry.getKey().newInstance();
            final File path = new File(pathStr + File.separator + instance.getPath());
System.out.println("export "+instance.getInfoAnno().description() + " '" + name + "'");
            instance.export(_context, path, name);
        }
    }
} else if (Mode.IMPORT == mode) {
    final Set<File> allFiles = getAllFiles(new File(pathStr));
    // get all matching files depending on the update classes
    final Map<Class<? extends AbstractObject_mxJPO>,Map<File,String>> clazz2names
            = new HashMap<Class<? extends AbstractObject_mxJPO>,Map<File,String>>();
    for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,List<String>> entry : clazz2matches.entrySet())  {
        AbstractObject_mxJPO instance = entry.getKey().newInstance();
        clazz2names.put(entry.getKey(), instance.getMatchingFileNames(allFiles, entry.getValue()));
    }
    // evaluate for existing administration objects
    final Collection<String> wildCardMatch = new HashSet<String>();
    wildCardMatch.add("*");
    final Map<Class<? extends AbstractObject_mxJPO>,Set<String>> existingNames
            = new HashMap<Class<? extends AbstractObject_mxJPO>,Set<String>>();
    for (final Class<? extends AbstractObject_mxJPO> clazz : clazz2names.keySet())  {
        if (!existingNames.containsKey(clazz))  {
            final AbstractObject_mxJPO instance = clazz.newInstance();
            existingNames.put(clazz,
                              instance.getMatchingNames(_context, wildCardMatch));
        }
    }
    // create if needed (and not in the list of existing objects
    for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,Map<File,String>> entry : clazz2names.entrySet())  {
        for (final Map.Entry<File, String> fileEntry : entry.getValue().entrySet())  {
            final Set<String> existings = existingNames.get(entry.getKey());
            if (!existings.contains(fileEntry.getValue()))  {
                final AbstractObject_mxJPO instance = entry.getKey().newInstance();
                instance.create(_context, fileEntry.getKey(), fileEntry.getValue());
            }
        }
    }
    // update
    for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,Map<File,String>> entry : clazz2names.entrySet())  {
        for (final Map.Entry<File, String> fileEntry : entry.getValue().entrySet())  {
            AbstractObject_mxJPO instance = entry.getKey().newInstance();
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
