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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.mapping.Action_mxJPO;
import org.mxupdate.mapping.ParameterDef_mxJPO;
import org.mxupdate.mapping.TypeDefGroup_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;

/**
 * Implements the help action used within MxUpdate.
 *
 * @author The MxUpdate Team
 */
public class HelpAction_mxJPO
{
    /** Parameter cache. */
    private final ParameterCache_mxJPO paramCache;

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
     * @see #appendDescription(CharSequence, Collection, Collection)
     * @see #prepareParams(ParameterCache_mxJPO)
     */
    private final Set<String> allParams = new HashSet<String>();

    /**
     * Initializes the action.
     *
     * @param _paramCache   parameter cache
     */
    public HelpAction_mxJPO(final ParameterCache_mxJPO _paramCache)
    {
        this.paramCache = _paramCache;
    }

    public void execute()
        throws Exception
    {
        this.prepareParams();

        final String prefix     = this.paramCache.getValueString(ValueKeys.HelpPrefix);
        final int lengthLine    = this.paramCache.getValueInteger(ValueKeys.HelpLengthLine);
        final int lengthParam   = this.paramCache.getValueInteger(ValueKeys.HelpLengthParams);
        final Appendable out = System.out;

        // first print the usage text with the mode parameters
        out.append('\n')
           .append(prefix)
           .append(this.paramCache.getValueString(ValueKeys.HelpUsage).trim())
           .append(' ');
        boolean first = true;
        for (final Action_mxJPO mode : Action_mxJPO.values())  {
            final String param = mode.getParameterList(this.paramCache).iterator().next();
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
        out.append(" | ... \n")
           .append(this.paramCache.getValueString(ValueKeys.HelpDescription));

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
                    for (int i = 0; i < lengthParam; i++)  {
                        line.append(' ');
                    }
                }
                for (final String desc : partDesc.split(" "))  {
                    if (!"".equals(desc))  {
                        if (line.length() > lengthLine)  {
                            out.append(line).append('\n');
                            line = new StringBuilder().append(prefix);
                            for (int i = 0; i < lengthParam; i++)  {
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
        for (final String copyright : this.paramCache.getValueString(ValueKeys.HelpCopyright).split("\n"))  {
            out.append(prefix).append(copyright).append('\n');
        }
        out.append('\n');
    }

    /**
     * Prepares the parameters depending on the <code>_paramCache</code>.
     *
     * @param _paramCache       parameter cache
     * @throws MatrixException if description or parameters could not be
     *                         prepared
     */
    private void prepareParams()
        throws MatrixException
    {
        this.description.clear();

        ////////////////////////////////////////////////////////////////////////
        // parameters

        for (final ParameterDef_mxJPO parameter : this.paramCache.getMapping().getAllParameterDefs())  {
            if (parameter.getParameterList() != null)  {
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
                this.appendDescription(desc, parameter.getParameterList(), parameter.getParameterArgs());
            }
        }

        ////////////////////////////////////////////////////////////////////////
        // modes

        for (final Action_mxJPO mode : Action_mxJPO.values())  {
            this.appendDescription(mode.getParameterDesc(this.paramCache), mode.getParameterList(this.paramCache), null);
        }

        ////////////////////////////////////////////////////////////////////////
        // type definitions

        // first create list all type definitions which could be used...
        final Set<TypeDef_mxJPO> all = new HashSet<TypeDef_mxJPO>();
        for (final TypeDef_mxJPO typeDef : this.paramCache.getMapping().getAllTypeDefsSorted())  {
            if (!typeDef.isBusCheckExists() || typeDef.existsBusType(this.paramCache))  {
                all.add(typeDef);
                if (typeDef.getParameterList() != null)  {
                    this.appendDescription(typeDef.getParameterDesc(), typeDef.getParameterList(), Arrays.asList(new String[]{"MATCH"}));
                }
                if (typeDef.getParameterListOpp() != null)  {
                    this.appendDescription(typeDef.getParameterDescOpp(), typeDef.getParameterListOpp(), Arrays.asList(new String[]{"MATCH"}));
                }
            }
        }

        // define type definition group parameters depending on existing type
        // definitions (and only if at minimum one type definition of the
        // groups exists...)
        for (final TypeDefGroup_mxJPO group : this.paramCache.getMapping().getAllTypeDefGroups())  {
            final Set<TypeDef_mxJPO> curTypeDefs = new HashSet<TypeDef_mxJPO>();
            for (final String typeDefName : group.getTypeDefList())  {
                final TypeDef_mxJPO typeDef = this.paramCache.getMapping().getTypeDef(typeDefName);
                if (all.contains(typeDef))  {
                    curTypeDefs.add(typeDef);
                }
            }
            if (!curTypeDefs.isEmpty())  {
                this.appendDescription(group.getParameterDesc(), group.getParameterList(), Arrays.asList(new String[]{"MATCH"}));
            }
        }
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
}
