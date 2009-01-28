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
import java.lang.reflect.InvocationTargetException;
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

import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.util.Mapping_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;
import org.mxupdate.util.TypeDef_mxJPO;
import org.mxupdate.util.Mapping_mxJPO.AdminPropertyDef;

import static org.mxupdate.update.util.StringUtil_mxJPO.match;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

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
    private enum UpdateCheck
    {
        /**
         * The last modified date of the file is check against the version
         * information.
         */
        FILEDATE_AS_VERSION,

        /**
         * Check for the last modified date of the file against the file date
         * property.
         */
        FILEDATE;
    }

    /**
     * Stored the descriptions of all parameters.
     */
    private final Map<String,String> DESCRIPTION = new TreeMap<String,String>();

    /**
     * Holds the mapping between the parameter and mode.
     */
    private final Map<String,Mode> PARAM_MODE = new HashMap<String,Mode>();

    /**
     * All parameters related to export / import are stored in this map. The
     * key is the parameter (including the '-'), the value the related class.
     */
    private final Map<String,Collection<TypeDef_mxJPO>> PARAMS = new HashMap<String,Collection<TypeDef_mxJPO>>();

    /**
     * Holds all classes used for all exports / imports. The set is needed that
     * the parameter '-a' (--admin) could be defined.
     */
    private final Collection<TypeDef_mxJPO> PARAMS_ADMIN = new ArrayList<TypeDef_mxJPO>();

    /**
     * Holds all parameters related how the version information is set.
     */
    private final Map<String,UpdateCheck> PARAM_VERSION = new HashMap<String,UpdateCheck>();

    private void prepareParams(final Context _context)
            throws MatrixException
    {
        this.DESCRIPTION.clear();
        this.PARAM_MODE.clear();
        this.PARAMS.clear();
        this.PARAMS_ADMIN.clear();
        this.PARAM_VERSION.clear();

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

        this.PARAM_MODE.put("-e", Mode.EXPORT);
        this.PARAM_MODE.put("--export", Mode.EXPORT);
        appendDescription("Export Mode", "-e", "--export");

        this.PARAM_MODE.put("-u", Mode.IMPORT);
        this.PARAM_MODE.put("--update", Mode.IMPORT);
        appendDescription("Update Mode.", "-u", "--update");

        this.PARAM_MODE.put("--delete", Mode.DELETE);
        appendDescription("Delete Mode.", "--delete");

        this.PARAM_MODE.put("-?", Mode.HELP);
        this.PARAM_MODE.put("-h", Mode.HELP);
        this.PARAM_MODE.put("--help", Mode.HELP);
        appendDescription("Print this Help.", "-h", "-?", "--help");

        ////////////////////////////////////////////////////////////////////////
        // version information

        this.PARAM_VERSION.put("--usefiledateasversion", UpdateCheck.FILEDATE_AS_VERSION);
        appendDescription("The last modified date in seconds of the file is used as version information. "
                                + "An update of an administration object is needed if the last modified "
                                + "date of the file is not equal to the value stored on the version property.",
                          "--usefiledateasversion");

        this.PARAM_VERSION.put("--checkfiledate", UpdateCheck.FILEDATE);
        appendDescription("Check if an update is required by comparing the last modified date against "
                                + "the value of the file date property.",
                          "--checkfiledate");

        appendDescription("Defines the version of administration objects (e.g. 1-0).",
                          Arrays.asList(new String[]{"--version"}),
                          "VERSIONNUMBER");

        ////////////////////////////////////////////////////////////////////////
        // admin

        this.PARAMS.put("-a", this.PARAMS_ADMIN);
        this.PARAMS.put("--admin", this.PARAMS_ADMIN);
        this.PARAMS.put("--all", this.PARAMS_ADMIN);
        appendDescription("Export / Import of all administrational objects.",
                          Arrays.asList(new String[]{"-a", "--admin", "--all"}),
                          "MATCH");

        ////////////////////////////////////////////////////////////////////////
        // data model

        final Collection<TypeDef_mxJPO> dm = new HashSet<TypeDef_mxJPO>();
        defineParameter(dm,
                        null,
                        "Export / Import of data model administrational objects.",
                        Arrays.asList(new String[]{"d", "dm", "datamodel"}));
        defineParameter(_context, dm, TypeDef_mxJPO.Attribute);
        defineParameter(_context, dm, TypeDef_mxJPO.Expression);
        defineParameter(_context, dm, TypeDef_mxJPO.Format);
        defineParameter(_context, dm, TypeDef_mxJPO.NumberGenerator);
        defineParameter(_context, dm, TypeDef_mxJPO.ObjectGenerator);
        defineParameter(_context, dm, TypeDef_mxJPO.Policy);
        defineParameter(_context, dm, TypeDef_mxJPO.Relationship);
        defineParameter(_context, dm, TypeDef_mxJPO.Rule);
        defineParameter(_context, dm, TypeDef_mxJPO.Trigger);
        defineParameter(_context, dm, TypeDef_mxJPO.TriggerGroup);
        defineParameter(_context, dm, TypeDef_mxJPO.Type);

        ////////////////////////////////////////////////////////////////////////
        // integration

        defineParameter(_context, null, TypeDef_mxJPO.IEFGlobalConfig);

        ////////////////////////////////////////////////////////////////////////
        // user

        final Collection<TypeDef_mxJPO> user = new HashSet<TypeDef_mxJPO>();
        defineParameter(user,
                        null,
                        "Export / Import of user administrational objects.",
                        Arrays.asList(new String[]{"user"}));
        defineParameter(_context, user, TypeDef_mxJPO.Association);
        defineParameter(_context, user, TypeDef_mxJPO.Group);
        defineParameter(_context, user, TypeDef_mxJPO.Person);
        defineParameter(_context, user, TypeDef_mxJPO.Role);

        ////////////////////////////////////////////////////////////////////////
        // program

        defineParameter(_context, null, TypeDef_mxJPO.JPO);
        defineParameter(_context, null, TypeDef_mxJPO.Program);

        ////////////////////////////////////////////////////////////////////////
        // user interface

        final Collection<TypeDef_mxJPO> ui = new HashSet<TypeDef_mxJPO>();
        defineParameter(ui,
                        null,
                        "Export / Import of user interface administrational objects.",
                        Arrays.asList(new String[]{"ui", "userinterface"}));
        defineParameter(_context, ui, TypeDef_mxJPO.Channel);
        defineParameter(_context, ui, TypeDef_mxJPO.Command);
        defineParameter(_context, ui, TypeDef_mxJPO.Form);
        defineParameter(_context, ui, TypeDef_mxJPO.Inquiry);
        defineParameter(_context, ui, TypeDef_mxJPO.Menu);
        defineParameter(_context, ui, TypeDef_mxJPO.Portal);
        defineParameter(_context, ui, TypeDef_mxJPO.Table);
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
    private void defineParameter(final Collection<TypeDef_mxJPO> _paramsList,
                                 final TypeDef_mxJPO _clazz,
                                 final String _description,
                                 final Collection<String> _longParams)
    {
        final List<String> allParamStrings = new ArrayList<String>();
        final Collection<TypeDef_mxJPO> tmp = (_clazz == null)
                ? _paramsList
                : new HashSet<TypeDef_mxJPO>();

        if (_clazz != null)  {
            // add to set of all classes parameters set
            this.PARAMS_ADMIN.add(_clazz);
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
            if (this.PARAMS.containsKey(paramStr) || this.PARAM_MODE.containsKey(paramStr) || this.PARAM_VERSION.containsKey(paramStr))  {
                throw new Error("double definition of parameter '" + paramStr
                        + "'! Found:\n" + this.PARAMS.get(paramStr) + "\nNew Definition:\n" + tmp);
            }
            this.PARAMS.put(paramStr, tmp);
            allParamStrings.add(paramStr);
        }

        // store description
        appendDescription(_description, allParamStrings, "MATCH");
    }

    private void defineParameter(final Context _context,
                                 final Collection<TypeDef_mxJPO> _paramsList,
                                 final TypeDef_mxJPO _typeDef)
            throws MatrixException
    {
        boolean exists = true;
        if (_typeDef.isBusCheckExists())  {
            final String tmp = execMql(_context,
                                       new StringBuilder().append("list type '").append(_typeDef.getMxBusType()).append("'"));
            exists = (tmp.length() > 0);
        }
        if (exists)  {
            this.defineParameter(_paramsList,
                                 _typeDef,
                                 _typeDef.getParameterDesc(),
                                 _typeDef.getParameters());
        }
    }

    /**
     * Appends a description for a defined list of parameters.
     *
     * @param _description      description to append
     * @param _params           related parameters
     * @param _argument         text of the argument for the list of parameters
     *                          (or <code>null</code> if not defined)
     */
    private void appendDescription(final String _description,
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

        this.DESCRIPTION.put("" + prefix + line, line.toString());
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
    private void appendDescription(final String _description,
                                   final String... _params)
    {
        final List<String> params = new ArrayList<String>(_params.length);
        for (final String param : _params)  {
            params.add(param);
        }
        this.appendDescription(_description, params, null);
    }

    /**
     * Prints the help for the MxUpdate functionality.
     *
     * @see #DESCRIPTION
     */
    private void printHelp()
    {
        System.out.println("usage: exec prog MxUpdate -e | -u ....\n");
        for (final String line : this.DESCRIPTION.values())  {
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

        this.prepareParams(_context);

        Type_mxJPO.IGNORE_TYPE_ATTRIBUTES.clear();
        Relationship_mxJPO.IGNORE_RELATIONSHIP_ATTRIBUTES.clear();

        String version = null;

        try {
            // to be sure....
            MqlUtil_mxJPO.execMql(_context, "verbose off");

            Mode mode = null;

            final Map<TypeDef_mxJPO,List<String>> clazz2matches = new HashMap<TypeDef_mxJPO,List<String>>();
//System.err.println("PARAM_MODE="  + Mode.IMPORT);

boolean unknown = false;

UpdateCheck versionInfo = null;

final Set<String> paths = new TreeSet<String>();

        for (int idx = 0; idx < _args.length; idx++)  {
//System.out.println(""+idx+"="+_args[idx]+"="+PARAMS.get(_args[idx]));
            final Collection<TypeDef_mxJPO> clazzes = PARAMS.get(_args[idx]);
            if (clazzes != null)  {
                idx++;
                final String name = _args[idx];
                for (final TypeDef_mxJPO clazz : clazzes)  {
                    List<String> names = clazz2matches.get(clazz);
                    if (names == null)  {
                        names = new ArrayList<String>();
                        clazz2matches.put(clazz, names);
                    }
                    names.add(name);
                }
            } else if (this.PARAM_MODE.containsKey(_args[idx])) {
                final Mode tmpMode = this.PARAM_MODE.get(_args[idx]);
                if (mode != null)  {
                    if ((mode == Mode.HELP) || (tmpMode == Mode.HELP))  {
                        mode = Mode.HELP;
                    } else  {
                        throw new Error("A mode is already defined and could not be defined twice!");
                    }
                } else  {
                    mode = this.PARAM_MODE.get(_args[idx]);
                }
            } else if (this.PARAM_VERSION.containsKey(_args[idx]))  {
                versionInfo = this.PARAM_VERSION.get(_args[idx]);
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
            } else if ("--version".equals(_args[idx]))  {
                idx++;
                version = _args[idx];
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
    this.update(_context, paths, clazz2matches, versionInfo, version);
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
                          final Map<TypeDef_mxJPO,List<String>> _clazz2matches)
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
        final Map<TypeDef_mxJPO,Set<String>> clazz2names = this.getMatching(_context, _clazz2matches);

        // export
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2names.entrySet())  {
            for (final String name : entry.getValue())  {
                AbstractObject_mxJPO instance = entry.getKey().newTypeInstance();
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
     * @param _versionInfo
     * @param _version
     */
    protected void update(final Context _context,
                          final Set<String> _paths,
                          final Map<TypeDef_mxJPO,List<String>> _clazz2matches,
                          final UpdateCheck _versionInfo,
                          final String _version)
            throws Exception
    {
        // get all matching files depending on the update classes
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2names = this.evalMatches(_paths, _clazz2matches);
        // evaluate for existing administration objects
        final Collection<String> wildCardMatch = new HashSet<String>();
        wildCardMatch.add("*");
        final Map<TypeDef_mxJPO,Set<String>> existingNames = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final TypeDef_mxJPO clazz : clazz2names.keySet())  {
            if (!existingNames.containsKey(clazz))  {
                final AbstractObject_mxJPO instance = clazz.newTypeInstance();
                existingNames.put(clazz,
                                  instance.getMatchingNames(_context, wildCardMatch));
            }
        }
        // create if needed (and not in the list of existing objects
        for (final TypeDef_mxJPO clazz : PARAMS_ADMIN)  {
            final Map<File,String> clazzMap = clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final Set<String> existings = existingNames.get(clazz);
                    if (!existings.contains(fileEntry.getValue()))  {
                         final AbstractObject_mxJPO instance = clazz.newTypeInstance();
System.out.println("create "+instance.getTypeDef().getLogging() + " '" + fileEntry.getValue() + "'");
                        instance.create(_context, fileEntry.getKey(), fileEntry.getValue());
                    }
                }
            }
        }
        // update
        for (final TypeDef_mxJPO clazz : PARAMS_ADMIN)  {
            final Map<File,String> clazzMap = clazz2names.get(clazz);
            if (clazzMap != null)  {
                for (final Map.Entry<File, String> fileEntry : clazzMap.entrySet())  {
                    final AbstractObject_mxJPO instance = clazz.newTypeInstance();
System.out.println("check "+instance.getTypeDef().getLogging() + " '" + fileEntry.getValue() + "'");

                    final boolean update;
                    String version = _version;
                    if ((_versionInfo == UpdateCheck.FILEDATE_AS_VERSION) || (_versionInfo == UpdateCheck.FILEDATE))  {
                        final Date fileDate = new Date(fileEntry.getKey().lastModified());
                        final Date instDate = (_versionInfo == UpdateCheck.FILEDATE)
                                              ? instance.getMxFileDate(_context, fileEntry.getValue(), AdminPropertyDef.FILEDATE)
                                              : instance.getMxFileDate(_context, fileEntry.getValue(), AdminPropertyDef.VERSION);
                        if (fileDate.equals(instDate))  {
                            update = false;
                        } else  {
                            update = true;
System.out.println("    - update to version from " + fileDate);
                            if (_versionInfo == UpdateCheck.FILEDATE_AS_VERSION)  {
                                version = Long.toString(fileDate.getTime() / 1000);
                            }
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
                          final Map<TypeDef_mxJPO,List<String>> _clazz2matches)
            throws Exception
    {
        // check for definition of min. / max. one path
        if (_paths.isEmpty())  {
            throw new Exception("no path is defined, but required for the delete!");
        }

        // evaluate all matching administration objects
        final Map<TypeDef_mxJPO,Set<String>> clazz2MxNames
                = this.getMatching(_context, _clazz2matches);

        // get all matching files depending on the update classes
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2FileNames
                = this.evalMatches(_paths, _clazz2matches);

        // and now loop throw the list of file names and compare to existing
        for (final Map.Entry<TypeDef_mxJPO,Set<String>> entry : clazz2MxNames.entrySet())  {
            final AbstractObject_mxJPO instance = entry.getKey().newTypeInstance();
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
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    protected Map<TypeDef_mxJPO,Map<File,String>> evalMatches(final Set<String> _paths,
                                                        final Map<TypeDef_mxJPO,List<String>> _clazz2matches)
            throws SecurityException, IllegalArgumentException, NoSuchMethodException,
                   InstantiationException, IllegalAccessException, InvocationTargetException
    {
        final Map<TypeDef_mxJPO,Map<File,String>> clazz2names = new HashMap<TypeDef_mxJPO,Map<File,String>>();

        // if no path is defined, the paths are directly defined at the objects
        // to import
        if (_paths.isEmpty())  {
            for (final Map.Entry<TypeDef_mxJPO,List<String>> entry : _clazz2matches.entrySet())  {
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
                                entry.getKey().newTypeInstance().getMatchingFileNames(allFiles));
            }
        // path parameter is defined
        } else  {
            final Set<File> allFiles = new HashSet<File>();
            for (final String path : _paths)  {
                allFiles.addAll(getAllFiles(new File(path)));
            }
            // get all matching files depending on the update classes
            for (final Map.Entry<TypeDef_mxJPO,List<String>> entry : _clazz2matches.entrySet())  {
                clazz2names.put(entry.getKey(),
                                entry.getKey().newTypeInstance().getMatchingFileNames(allFiles, entry.getValue()));
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
    protected Map<TypeDef_mxJPO,Set<String>> getMatching(final Context _context,
                                                   final Map<TypeDef_mxJPO,List<String>> _clazz2matches)
            throws Exception
    {
        final Map<TypeDef_mxJPO,Set<String>> clazz2names = new HashMap<TypeDef_mxJPO,Set<String>>();
        for (final Map.Entry<TypeDef_mxJPO,List<String>> entry : _clazz2matches.entrySet())  {
            AbstractObject_mxJPO instance = entry.getKey().newTypeInstance();
            clazz2names.put(entry.getKey(), instance.getMatchingNames(_context, entry.getValue()));
        }

        return clazz2names;
    }
}
