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

package org.mxupdate.mapping;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
class AbstractValue_mxJPO
{
    /**
     * Used parameter description within the property file.
     *
     * @see #defineValues(String, String)
     */
    private static final String PREFIX_PARAM_DESC = "ParameterDesc";

    /**
     * Used opposite parameter description within the property file.
     *
     * @see #defineValues(String, String)
     */
    private static final String PREFIX_PARAM_DESCOPP = "ParameterDescOpposite";

    /**
     * Used parameter argument list within the property file.
     *
     * @see #defineValues(String, String)
     */
    private static final String PREFIX_PARAM_ARGS = "ParameterArgs";

    /**
     * Used parameter list within the property file.
     *
     * @see #defineValues(String, String)
     */
    private static final String PREFIX_PARAM_LIST = "ParameterList";

    /**
     * Used opposite parameter list within the property file.
     *
     * @see #defineValues(String, String)
     */
    private static final String PREFIX_PARAM_LISTOPP = "ParameterListOpposite";

    /**
     * Parameter to reference to the Wiki.
     *
     * @see #defineValues(String, String)
     */
    private static final String PREFIX_PARAM_WIKI = "Wiki";

    /**
     * Holds the name of the value.
     *
     * @see #getName()
     * @see #AbstractValue_mxJPO(String)
     */
    private final String name;

    /**
     * Defines the parameter description.
     *
     * @see #getParameterDesc()
     * @see #defineValues(String, String)
     */
    private String paramDesc;

    /**
     * Defines the opposite parameter description.
     *
     * @see #getParameterDescOpp()
     * @see #defineValues(String, String)
     */
    private String paramDescOpp;

    /**
     * Defines the list of arguments.
     *
     * @see #getParameterList()
     * @see #defineValues(String, String)
     */
    private Collection<String> paramArgs;

    /**
     * Defines the list of parameters.
     *
     * @see #getParameterList()
     * @see #defineValues(String, String)
     */
    private Collection<String> paramList;

    /**
     * Defines the list of opposite parameters.
     *
     * @see #getParameterListOpp()
     * @see #defineValues(String, String)
     */
    private Collection<String> paramListOpp;

    /**
     * Constructor used to initialize the name of the abstract values.
     *
     * @param _name name of the abstract value
     * @see #name
     */
    protected AbstractValue_mxJPO(final String _name)
    {
        this.name = _name;
    }

    /**
     *
     * @param _key      name of the key
     * @param _value    related value
     * @throws Exception if the key is not known
     */
    protected void defineValues(final String _key,
                                final String _value)
            throws Exception
    {
        if (AbstractValue_mxJPO.PREFIX_PARAM_DESC.equals(_key))  {
            this.paramDesc = _value;
        } else if (AbstractValue_mxJPO.PREFIX_PARAM_DESCOPP.equals(_key))  {
            this.paramDescOpp = _value;
        } else if (AbstractValue_mxJPO.PREFIX_PARAM_ARGS.equals(_key))  {
            if (!"".equals(_value.trim()))  {
                this.paramArgs = Arrays.asList(_value.split(","));
            }
        } else if (AbstractValue_mxJPO.PREFIX_PARAM_LIST.equals(_key))  {
            if (!"".equals(_value.trim()))  {
                this.paramList = Arrays.asList(_value.split(","));
            }
        } else if (AbstractValue_mxJPO.PREFIX_PARAM_LISTOPP.equals(_key))  {
            if (!"".equals(_value.trim()))  {
                this.paramListOpp = Arrays.asList(_value.split(","));
            }
        } else if (!AbstractValue_mxJPO.PREFIX_PARAM_WIKI.equals(_key))  {
            throw new Exception("unknown key " + _key + " with value '" + _value + "' defined!");
        }
    }

    /**
     * Returns the name of the abstract value.
     *
     * @return name
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the description of parameters.
     *
     * @return description of parameter
     * @see #paramDesc
     */
    public String getParameterDesc()
    {
        return this.paramDesc;
    }

    /**
     * Returns the opposite description of parameters.
     *
     * @return description of parameter
     * @see #paramDescOpp
     */
    public String getParameterDescOpp()
    {
        return this.paramDescOpp;
    }

    /**
     * Returns the list of parameter arguments.
     *
     * @return list of parameter argument strings
     * @see #paramArgs
     */
    public Collection<String> getParameterArgs()
    {
        return this.paramArgs;
    }

    /**
     * Returns the list of parameters.
     *
     * @return list of parameter strings
     * @see #paramList
     */
    public Collection<String> getParameterList()
    {
        return this.paramList;
    }

    /**
     * Returns the list of opposite parameters.
     *
     * @return list of opposite parameter strings
     * @see #paramListOpp
     */
    public Collection<String> getParameterListOpp()
    {
        return this.paramListOpp;
    }
}
