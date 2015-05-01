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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.action.DeleteAction_mxJPO;
import org.mxupdate.action.ExportAction_mxJPO;
import org.mxupdate.action.HelpAction_mxJPO;
import org.mxupdate.action.SelectTypeDefUtil_mxJPO;
import org.mxupdate.action.UpdateAction_mxJPO;
import org.mxupdate.mapping.Mode_mxJPO;
import org.mxupdate.mapping.ParameterDef_mxJPO;
import org.mxupdate.mapping.TypeDefGroup_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * <tr>
 * <th></th><th></th><th></th>
 * <tr>
 *
 * @author The MxUpdate Team
 */
public class MxUpdate_mxJPO
{
    /** Holds the mapping between the parameter and mode. */
    private final Map<String,Mode_mxJPO> paramsModes = new HashMap<String,Mode_mxJPO>();
    /** Holds the mapping between the parameters and the related parameters. */
    private final Map<String,ParameterDef_mxJPO> paramsParameters = new HashMap<String,ParameterDef_mxJPO>();

    /**
     * All parameters related to export / import are stored in this map. The
     * key is the parameter (including the '-'), the value the related class.
     */
    private final Map<String,Collection<TypeDef_mxJPO>> paramsTypeDefs = new HashMap<String,Collection<TypeDef_mxJPO>>();

    /**
     * All opposite parameters related to export / import are stored in this
     * map. The key is the opposite parameter (including the '-'), the value
     * the related class.
     */
    private final Map<String,TypeDef_mxJPO> paramsTypeDefsOpp = new HashMap<String,TypeDef_mxJPO>();

    /**
     * Prepares the parameters depending on the <code>_paramCache</code>.
     *
     * @param _paramCache       parameter cache
     * @throws MatrixException if description or parameters could not be
     *                         prepared
     */
    private void prepareParams(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        this.paramsModes.clear();
        this.paramsParameters.clear();
        this.paramsTypeDefs.clear();

        ////////////////////////////////////////////////////////////////////////
        // parameters

        for (final ParameterDef_mxJPO parameter : _paramCache.getMapping().getAllParameterDefs())  {
            if (parameter.getParameterList() != null)  {
                for (final String param : parameter.getParameterList())  {
                    final String paramStr = (param.length() > 1)
                                            ? "--" + param
                                            : "-" + param;
                    this.paramsParameters.put(paramStr.toLowerCase(), parameter);
                }
                final StringBuilder desc = new StringBuilder().append(parameter.getParameterDesc());
                if ((parameter.getDefaultValue() != null) && (parameter.getType() != ParameterDef_mxJPO.Type.BOOLEAN))  {
                    desc.append('\n').append("(Default '");
                    if (parameter.getType() == ParameterDef_mxJPO.Type.LIST)  {
                        desc.append(parameter.getDefaultValue().replaceAll(",", ", "));
                    } else  {
                        desc.append(parameter.getDefaultValue());
                    }
                    desc.append("')");
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////
        // modes

        for (final Mode_mxJPO mode : Mode_mxJPO.values())  {
            for (final String param : mode.getParameterList(_paramCache))  {
                final String paramStr = (param.length() > 1)
                                        ? "--" + param
                                        : "-" + param;
                this.paramsModes.put(paramStr.toLowerCase(), mode);
            }
        }

        ////////////////////////////////////////////////////////////////////////
        // type definitions

        // first create list all type definitions which could be used...
        final Set<TypeDef_mxJPO> all = new HashSet<TypeDef_mxJPO>();
        for (final TypeDef_mxJPO typeDef : _paramCache.getMapping().getAllTypeDefsSorted())  {
            if (!typeDef.isBusCheckExists() || typeDef.existsBusType(_paramCache))  {
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
                        this.paramsTypeDefsOpp.put(paramStr.toLowerCase(), typeDef);
                    }
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
            this.paramsTypeDefs.put(paramStr.toLowerCase(), tmp);
        }
    }

    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        try {
            final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, false);

            this.prepareParams(paramCache);

            // to be sure....
            MqlUtil_mxJPO.execMql(_context, "verbose off", false);

            Mode_mxJPO mode = null;

            final SelectTypeDefUtil_mxJPO selectHandler = new SelectTypeDefUtil_mxJPO();

            boolean unknown = false;

            for (int idx = 0; idx < _args.length; idx++)  {
                final String arg = _args[idx].toLowerCase();
                if (this.paramsTypeDefs.containsKey(arg))  {
                    selectHandler.match(this.paramsTypeDefs.get(arg), _args[++idx]);
                } else if (this.paramsTypeDefsOpp.containsKey(arg))  {
                    selectHandler.ignore(this.paramsTypeDefsOpp.get(arg), _args[++idx]);
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
                    idx = paramCache.evalParameter(this.paramsParameters.get(arg), _args, idx);
                } else  {
                    unknown = true;
                    paramCache.logError("unknown pararameter "  + arg);
                }
            }

            if (unknown || (Mode_mxJPO.HELP == mode) || (mode == null))  {
                new HelpAction_mxJPO(paramCache).execute();
            } else if (Mode_mxJPO.EXPORT == mode)  {
                new ExportAction_mxJPO(paramCache, selectHandler).execute();
            } else if (Mode_mxJPO.IMPORT == mode)  {
                new UpdateAction_mxJPO(paramCache, selectHandler).execute();
            } else if (Mode_mxJPO.DELETE == mode)  {
                new DeleteAction_mxJPO(paramCache, selectHandler).execute();
            }

        } catch (final Exception e)  {
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
