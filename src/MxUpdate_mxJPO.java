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

import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.datamodel.Attribute_mxJPO;
import org.mxupdate.update.datamodel.Expression_mxJPO;
import org.mxupdate.update.datamodel.Format_mxJPO;
import org.mxupdate.update.datamodel.NumberGenerator_mxJPO;
import org.mxupdate.update.datamodel.ObjectGenerator_mxJPO;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.datamodel.Rule_mxJPO;
import org.mxupdate.update.datamodel.TriggerGroup_mxJPO;
import org.mxupdate.update.datamodel.Trigger_mxJPO;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.update.program.JPO_mxJPO;
import org.mxupdate.update.program.Program_mxJPO;
import org.mxupdate.update.user.Association_mxJPO;
import org.mxupdate.update.user.Group_mxJPO;
import org.mxupdate.update.user.Person_mxJPO;
import org.mxupdate.update.user.Role_mxJPO;
import org.mxupdate.update.userinterface.Channel_mxJPO;
import org.mxupdate.update.userinterface.Command_mxJPO;
import org.mxupdate.update.userinterface.Form_mxJPO;
import org.mxupdate.update.userinterface.Inquiry_mxJPO;
import org.mxupdate.update.userinterface.Menu_mxJPO;
import org.mxupdate.update.userinterface.Portal_mxJPO;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.util.Mapping_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

import static org.mxupdate.update.util.StringUtil_mxJPO.match;

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
     * @see #appendDescription(String, List, String)
     */
    private static final int LENGTH_DESC_PARAMS = 42;

    /**
     * Defines the length of one description line.
     *
     * @see #appendDescription(String, List, String)
     */
    private static final int LENGTH_DESC_LINE = 100;

    private enum Mode
    {
        /**
         * Mode 'import' used to import defined administration objects from
         * file system into Matrix.
         */
        IMPORT,
        /**
         * Mode 'export' used to export defined administration objects from
         * Matrix into a file system.
         */
        EXPORT,
        /**
         * Mode 'delete' used to delete in Mx objects which are not defined
         * in the repository (file system).
         */
        DELETE,
        /**
         * Prints out the help description.
         */
        HELP;
    }

    /**
     * Enumeration used to define how the version information is evaluated.
     */
    private enum VersionInfo
    {
        /**
         * The last modified date of the file is used as version information.
         */
        FILEDATE;
    }

    /**
     * Holds the mapping between the parameter and mode.
     */
    private static final Map<String,Mode> PARAM_MODE = new HashMap<String,Mode>();

    /**
     * All parameters related to export / import are stored in this map. The
     * key is the parameter (including the '-'), the value the related class.
     */
    private static final Map<String,Collection<Class<? extends AbstractObject_mxJPO>>> PARAMS
            = new HashMap<String,Collection<Class<? extends AbstractObject_mxJPO>>>();

    /**
     * Holds all classes used for all exports / imports. The set is needed that
     * the parameter '-a' (--admin) could be defined.
     */
    private final static Collection<Class<? extends AbstractObject_mxJPO>> PARAMS_ADMIN
            = new ArrayList<Class<? extends AbstractObject_mxJPO>>();

    /**
     * Holds all parameters related how the version information is set.
     */
    private static final Map<String,VersionInfo> PARAM_VERSION = new HashMap<String,VersionInfo>();

    static  {

        ////////////////////////////////////////////////////////////////////////
        //

        appendDescription("Pattern defining the match of attributes which are ignored "
                                + "within the test attributes of types.",
                          Arrays.asList(new String[]{"--ignoretypeattributes"}),
                          "MATCH");
        appendDescription("Pattern defining the match of attributes which are ignored "
                                + "within the test attributes of relationships.",
                          Arrays.asList(new String[]{"--ignorerelationshipattributes"}),
                          "MATCH");

        appendDescription("Defines the name of application which is defined as property"
                                + " / attribute on administration objects. The value of"
                                + " mapping property 'PropertyValue.Application' will be"
                                + " overwritten.",
                          Arrays.asList(new String[]{"--application"}),
                          "APPLICATIONAME");

        appendDescription("Defines the name of author which is defined as property"
                                + " / attribute on administration objects. The value of"
                                + " mapping property 'PropertyValue.Author' will be"
                                + " overwritten.",
                          Arrays.asList(new String[]{"--author"}),
                          "AUTHORNAME");

        appendDescription("Defines the name of installer which is defined as property"
                                + " / attribute on administration objects. The value of"
                                + " mapping property 'PropertyValue.Installer' will be"
                                + " overwritten.",
                          Arrays.asList(new String[]{"--installer"}),
                          "INSTALLERNAME");

        ////////////////////////////////////////////////////////////////////////
        // mode (export / import / help)

        PARAM_MODE.put("-e", Mode.EXPORT);
        PARAM_MODE.put("--export", Mode.EXPORT);
        appendDescription("Export Mode", "-e", "--export");

        PARAM_MODE.put("-u", Mode.IMPORT);
        PARAM_MODE.put("--update", Mode.IMPORT);
        appendDescription("Update Mode.", "-u", "--update");

        PARAM_MODE.put("--delete", Mode.DELETE);
        appendDescription("Delete Mode.", "--delete");

        PARAM_MODE.put("-?", Mode.HELP);
        PARAM_MODE.put("-h", Mode.HELP);
        PARAM_MODE.put("--help", Mode.HELP);
        appendDescription("Print this Help.", "-h", "-?", "--help");

        ////////////////////////////////////////////////////////////////////////
        // version information

        PARAM_VERSION.put("--usefiledateasversion", VersionInfo.FILEDATE);
        appendDescription("The last modified date of the file is used as version information.",
                          "--usefiledateasversion");

        ////////////////////////////////////////////////////////////////////////
        // admin

        PARAMS.put("-a", PARAMS_ADMIN);
        PARAMS.put("--admin", PARAMS_ADMIN);
        PARAMS.put("--all", PARAMS_ADMIN);
        appendDescription("Export / Import of all administrational objects.",
                          Arrays.asList(new String[]{"-a", "--admin", "--all"}),
                          "MATCH");

        ////////////////////////////////////////////////////////////////////////
        // data model

        final Collection<Class<? extends AbstractObject_mxJPO>> dm = new HashSet<Class<? extends AbstractObject_mxJPO>>();
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
        defineParameter('p', dm,
                        Policy_mxJPO.class,
                        "Export / Import of policies.",
                        "policy");
        defineParameter('r', dm,
                        Relationship_mxJPO.class,
                        "Export / Import of relationships.",
                        "relation", "relationship");
        defineParameter(null, dm,
                        Rule_mxJPO.class,
                        "Export / Import of rules.",
                        "rule");
        defineParameter('g', dm,
                        Trigger_mxJPO.class,
                        "Export / Import of triggers.",
                        "trigger", "trig");
        defineParameter(null, dm,
                        TriggerGroup_mxJPO.class,
                        "Export / Import of triggers groups.",
                        "triggergroup");
        defineParameter('t', dm,
                        Type_mxJPO.class,
                        "Export / Import of types.",
                        "type");

        ////////////////////////////////////////////////////////////////////////
        // user

        final Collection<Class<? extends AbstractObject_mxJPO>> user = new HashSet<Class<? extends AbstractObject_mxJPO>>();
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
                        Person_mxJPO.class,
                        "Export / Import of persons.",
                        "person");
        defineParameter(null, user,
                        Role_mxJPO.class,
                        "Export / Import of roles.",
                        "role");

        ////////////////////////////////////////////////////////////////////////
        // program

        defineParameter('j', null,
                        JPO_mxJPO.class,
                        "Export / Import of JPOs.",
                        "jpo");
        defineParameter(null, null,
                        Program_mxJPO.class,
                        "Export / Import of programs.",
                        "program", "prog");

        ////////////////////////////////////////////////////////////////////////
        // user interface

        final Collection<Class<? extends AbstractObject_mxJPO>> ui = new HashSet<Class<? extends AbstractObject_mxJPO>>();
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
                                        final Collection<Class<? extends AbstractObject_mxJPO>> _paramsList,
                                        final Class<? extends AbstractObject_mxJPO> _clazz,
                                        final String _description,
                                        final String... _longParams)
    {
        final List<String> allParamStrings = new ArrayList<String>();
        final Collection<Class<? extends AbstractObject_mxJPO>> tmp = (_clazz == null)
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
            if (PARAMS.containsKey(shortParam) || PARAM_MODE.containsKey(shortParam) || PARAM_VERSION.containsKey(shortParam))  {
                throw new Error("double definition of short parameter '" + shortParam
                                + "'! Found:\n" + PARAMS.get(shortParam) + "\nNew Definition:\n" + tmp);
            }
            PARAMS.put(shortParam, tmp);
            allParamStrings.add(shortParam);
        }
        // all long parameters
        for (final String param : _longParams)  {
            final String paramStr = "--" + param;
            if (PARAMS.containsKey(paramStr) || PARAM_MODE.containsKey(paramStr) || PARAM_VERSION.containsKey(paramStr))  {
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

        // append description
        first = true;
        for (final String partDesc : _description.split("\n"))  {
            int length = LENGTH_DESC_PARAMS;
            if (first == true)  {
                first = false;
            } else  {
                line.append("\n");
                for (int i = 0; i < LENGTH_DESC_PARAMS; i++)  {
                    line.append(' ');
                }
            }
            for (final String desc : partDesc.split(" "))  {
                if (!"".equals(desc))  {
                    length += desc.length() + 1;
                    if (length > LENGTH_DESC_LINE)  {
                        line.append("\n");
                        for (int i = 0; i < LENGTH_DESC_PARAMS; i++)  {
                            line.append(' ');
                        }
                        length = LENGTH_DESC_PARAMS + desc.length() + 1;
                    }
                    line.append(' ').append(desc);
                }
            }
        }

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
        System.out.println();
        System.out.println("If parameter '--path' exists for the update mode, the <MATCH> arguments are");
        System.out.println("patterns used to evaluate matching names without prefix and suffix. If parameter");
        System.out.println("'--path' is not defined, the <MATCH> arguments are defined as file names");
        System.out.println("(asterisk '*' could be uses..).");
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" Copyright 2008-2009 The MxUpdate Team");
        System.out.println(" Licensed under the Apache License, Version 2.0 (the \"License\");");
        System.out.println(" you may not use this files except in compliance with the License.");
        System.out.println(" You may obtain a copy of the License at");
        System.out.println("");
        System.out.println("      http://www.apache.org/licenses/LICENSE-2.0");
        System.out.println("");
        System.out.println(" Unless required by applicable law or agreed to in writing, software");
        System.out.println(" distributed under the License is distributed on an \"AS IS\" BASIS,");
        System.out.println(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        System.out.println(" See the License for the specific language governing permissions and");
        System.out.println(" limitations under the License.");
        System.out.println("--------------------------------------------------------------------------------");
    }

    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {

        // initialize mapping
        Mapping_mxJPO.init(_context);

        Type_mxJPO.IGNORE_TYPE_ATTRIBUTES.clear();
        Relationship_mxJPO.IGNORE_RELATIONSHIP_ATTRIBUTES.clear();

        try {
            // to be sure....
            MqlUtil_mxJPO.execMql(_context, "verbose off");

            Mode mode = null;

final Map<Class<? extends AbstractObject_mxJPO>,List<String>> clazz2matches
        = new HashMap<Class<? extends AbstractObject_mxJPO>,List<String>>();
//System.err.println("PARAM_MODE="  + Mode.IMPORT);

boolean unknown = false;

VersionInfo versionInfo = null;

final Set<String> paths = new TreeSet<String>();

        for (int idx = 0; idx < _args.length; idx++)  {
//System.out.println(""+idx+"="+_args[idx]+"="+PARAMS.get(_args[idx]));
            final Collection<Class<? extends AbstractObject_mxJPO>> clazzes = PARAMS.get(_args[idx]);
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
            } else if (PARAM_VERSION.containsKey(_args[idx]))  {
                versionInfo = PARAM_VERSION.get(_args[idx]);
            } else if ("--application".equals(_args[idx]))  {
                idx++;
                Mapping_mxJPO.defineApplication(_args[idx]);
            } else if ("--author".equals(_args[idx]))  {
                idx++;
                Mapping_mxJPO.defineAuthor(_args[idx]);
            } else if ("--ignorerelationshipattributes".equals(_args[idx]))  {
                idx++;
                Relationship_mxJPO.IGNORE_RELATIONSHIP_ATTRIBUTES.add(_args[idx]);
            } else if ("--ignoretypeattributes".equals(_args[idx]))  {
                idx++;
                Type_mxJPO.IGNORE_TYPE_ATTRIBUTES.add(_args[idx]);
            } else if ("--installer".equals(_args[idx]))  {
                idx++;
                Mapping_mxJPO.defineInstaller(_args[idx]);
            } else if ("--path".equals(_args[idx]))  {
                idx++;
                paths.add(_args[idx]);
            } else  {
                unknown = true;
System.err.println("unknown pararameter "  + _args[idx]);
            }
        }

//System.out.println("mode="+mode);
//System.out.println("clazz2matches="+clazz2matches);

if (unknown || (Mode.HELP == mode) || (mode == null))  {
    this.printHelp();
} else if (Mode.EXPORT == mode)  {
    this.export(_context, paths, clazz2matches);
} else if (Mode.IMPORT == mode)  {
    this.update(_context, paths, clazz2matches, versionInfo);
} else if (Mode.DELETE == mode)  {
    this.delete(_context, paths, clazz2matches);
}

        } catch (Exception e)  {
            e.printStackTrace(System.out);
            throw e;
        }

    }

    /**
     * Exports matching administration objects to given path.
     *
     * @param _context          context for this request
     * @param _paths            path where the administration objects are
     *                          exported
     * @param _clazz2matches    classes and their matched to export
     * @throws Exception if none path or more than one path is defined or if
     *                   the export failed
     */
    protected void export(final Context _context,
                          final Set<String> _paths,
                          final Map<Class<? extends AbstractObject_mxJPO>,List<String>> _clazz2matches)
            throws Exception
    {
        // check for definition of min. / max. one path
        if (_paths.isEmpty())  {
            throw new Exception("no path is defined, but required for the export!");
        }
        if (_paths.size() > 1)  {
            throw new Exception("more than one path is defined, but maximum is allowed for the export!");
        }
        final String pathStr = _paths.iterator().next();

        // evaluate all matching administration objects
        final Map<Class<? extends AbstractObject_mxJPO>,Set<String>> clazz2names
                = this.getMatching(_context, _clazz2matches);

        // export
        for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,Set<String>> entry : clazz2names.entrySet())  {
            for (final String name : entry.getValue())  {
                AbstractObject_mxJPO instance = entry.getKey().newInstance();
                final File path = new File(pathStr + File.separator + instance.getPath());
System.out.println("export "+instance.getTypeDef().getLogging() + " '" + name + "'");
                instance.export(_context, path, name);
            }
        }
    }

    /**
     *
     * @param _context
     * @param _paths
     * @param _clazz2matches
     */
    protected void update(final Context _context,
                          final Set<String> _paths,
                          final Map<Class<? extends AbstractObject_mxJPO>,List<String>> _clazz2matches,
                          final VersionInfo _versionInfo)
            throws Exception
    {
        // get all matching files depending on the update classes
        final Map<Class<? extends AbstractObject_mxJPO>,Map<File,String>> clazz2names
                = this.evalMatches(_paths, _clazz2matches);
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
        for (Class<? extends AbstractObject_mxJPO> clazz : PARAMS_ADMIN)  {
            final Map<File,String> clazzMap = clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final Set<String> existings = existingNames.get(clazz);
                    if (!existings.contains(fileEntry.getValue()))  {
                         final AbstractObject_mxJPO instance = clazz.newInstance();
System.out.println("create "+instance.getTypeDef().getLogging() + " '" + fileEntry.getValue() + "'");
                        instance.create(_context, fileEntry.getKey(), fileEntry.getValue());
                    }
                }
            }
        }
        // update
        for (Class<? extends AbstractObject_mxJPO> clazz : PARAMS_ADMIN)  {
            final Map<File,String> clazzMap = clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final AbstractObject_mxJPO instance = clazz.newInstance();
System.out.println("check "+instance.getTypeDef().getLogging() + " '" + fileEntry.getValue() + "'");

                    final boolean update;
                    String version = null;
                    if (_versionInfo == VersionInfo.FILEDATE)  {
                        final Date fileDate = new Date(fileEntry.getKey().lastModified());
                        if (fileDate.equals(instance.getMxFileDate(_context, fileEntry.getValue())))  {
                            update = false;
                        } else  {
                            update = true;
System.out.println("    - update to version from " + fileDate);
                            version = Long.toString(fileDate.getTime() / 1000);
                        }
                    } else  {
                        update = true;
System.out.println("    - update");
                    }
                    if (update)  {
                        instance.update(_context,
                                        fileEntry.getValue(),
                                        fileEntry.getKey(),
                                        version);
                    }
                }
            }
        }
    }

    protected void delete(final Context _context,
                          final Set<String> _paths,
                          final Map<Class<? extends AbstractObject_mxJPO>,List<String>> _clazz2matches)
            throws Exception
    {
        // check for definition of min. / max. one path
        if (_paths.isEmpty())  {
            throw new Exception("no path is defined, but required for the delete!");
        }

        // evaluate all matching administration objects
        final Map<Class<? extends AbstractObject_mxJPO>,Set<String>> clazz2MxNames
                = this.getMatching(_context, _clazz2matches);

        // get all matching files depending on the update classes
        final Map<Class<? extends AbstractObject_mxJPO>,Map<File,String>> clazz2FileNames
                = this.evalMatches(_paths, _clazz2matches);

        // and now loop throw the list of file names and compare to existing
        for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,Set<String>> entry : clazz2MxNames.entrySet())  {
            final AbstractObject_mxJPO instance = entry.getKey().newInstance();
            final Collection<String> fileNames = clazz2FileNames.get(entry.getKey()).values();
            for (final String name : entry.getValue())  {
                if (!fileNames.contains(name))  {
System.out.println("delete " + instance.getTypeDef().getLogging() + " '" + name + "'");
                    instance.delete(_context, name);
                }
            }
        }
    }

    /**
     * Evaluates for matching files with the names depending on the update
     * classes.
     *
     * @param _paths            paths to check (or empty if no path parameter
     *                          is defined)
     * @param _clazz2matches    all classes with the depending matches (if
     *                          paths are defined, the names must match against
     *                          the Matrix name; otherwise the found files must
     *                          match)
     * @return map of update classes and the depending files with their names
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected Map<Class<? extends AbstractObject_mxJPO>,Map<File,String>> evalMatches(final Set<String> _paths,
                                                                                      final Map<Class<? extends AbstractObject_mxJPO>,List<String>> _clazz2matches)
            throws InstantiationException, IllegalAccessException
    {
        final Map<Class<? extends AbstractObject_mxJPO>,Map<File,String>> clazz2names
                = new HashMap<Class<? extends AbstractObject_mxJPO>,Map<File,String>>();

        // if no path is defined, the paths are directly defined at the objects
        // to import
        if (_paths.isEmpty())  {
            for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
                final Set<File> allFiles = new HashSet<File>();

                for (final String pathStr : entry.getValue())  {
                    final File pathFile = new File(pathStr);
                    final String match = pathFile.getName();
                    final Set<File> subPathFiles = getAllFiles(pathFile.getParentFile());
                    for (final File file : subPathFiles)  {
                        if (match(file.getName(), match))  {
                            allFiles.add(file);
                        }
                    }
                }
                clazz2names.put(entry.getKey(),
                        entry.getKey().newInstance().getMatchingFileNames(allFiles));
            }
        // path parameter is defined
        } else  {
            final Set<File> allFiles = new HashSet<File>();
            for (final String path : _paths)  {
                allFiles.addAll(getAllFiles(new File(path)));
            }
            // get all matching files depending on the update classes
            for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
                clazz2names.put(entry.getKey(),
                                entry.getKey().newInstance().getMatchingFileNames(allFiles, entry.getValue()));
            }
        }
        return clazz2names;
    }

    /**
     * Evaluates depending on given path all files in the sub directories.
     *
     * @param _path     path for which the files are searched
     * @return set of all found files
     * TODO performance improvement needed if for given path already all files
     *       are evaluated
     */
    protected Set<File> getAllFiles(final File _path)
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

    /**
     * Evaluate all matching administration objects.
     *
     * @param _context          context for this request
     * @param _clazz2matches    map of classes and the depending list of string
     *                          which must match
     * @return map of classes and a set of matching names for this classes
     * @throws Exception if the match fails
     * @see #export(Context, Set, Map)
     * @see #delete(Context, Set, Map)
     */
    protected Map<Class<? extends AbstractObject_mxJPO>,Set<String>> getMatching(final Context _context,
                                                                                 final Map<Class<? extends AbstractObject_mxJPO>,List<String>> _clazz2matches)
            throws Exception
    {
        final Map<Class<? extends AbstractObject_mxJPO>,Set<String>> clazz2names
                = new HashMap<Class<? extends AbstractObject_mxJPO>,Set<String>>();
        for (final Map.Entry<Class<? extends AbstractObject_mxJPO>,List<String>> entry : _clazz2matches.entrySet())  {
            AbstractObject_mxJPO instance = entry.getKey().newInstance();
            clazz2names.put(entry.getKey(), instance.getMatchingNames(_context, entry.getValue()));
        }

        return clazz2names;
    }
}
