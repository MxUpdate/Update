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

package org.mxupdate.script;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Script context.
 *
 * @author The MxUpdate Team
 */
public class ScriptContext_mxJPO
{
    /** Parameter cache with the MX context. */
    private final ParameterCache_mxJPO paramCache;

    /** All variables of the script context. */
    private final Map<String,String> variables = new HashMap<>();

    /**
     * Constructor to initialize the script context.
     *
     * @param _paramCache       parameter cache
     * @param _initVariables    initialize variables
     */
    public ScriptContext_mxJPO(final ParameterCache_mxJPO _paramCache,
                               final Map<String,String> _initVariables)
    {
        this.paramCache = _paramCache;
        this.variables.putAll(_initVariables);
    }

    /**
     * Constructor to initialize the script context w/o pre-defined variables.
     *
     * @param _paramCache       parameter cache
     */
    public ScriptContext_mxJPO(final ParameterCache_mxJPO _paramCache)
    {
        this.paramCache = _paramCache;
    }

    /**
     * Returns the {@link #paramCache parameter cache}.
     *
     * @return parameter cache
     */
    public ParameterCache_mxJPO getParamCache()
    {
        return this.paramCache;
    }

    /**
     * Returns the value of given variable.
     *
     * @param _name     name of variable
     * @return value of the variable
     */
    public String getVarValue(final String _name)
    {
        return this.variables.get(_name);
    }

    /**
     * Defines new or updates existing variable.
     *
     * @param _varName  name of variable
     * @param _value    value of variable
     * @return this security context
     */
    public ScriptContext_mxJPO defVar(final String _varName,
            final String _value)
    {
        this.variables.put(_varName, _value);
        return this;
    }
}
