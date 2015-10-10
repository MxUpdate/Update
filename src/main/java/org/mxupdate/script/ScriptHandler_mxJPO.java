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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mxupdate.script.statement.AbstractStatement_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Handler for one complete script.
 *
 * @author The MxUpdate Team
 */
public class ScriptHandler_mxJPO
{
    /** List of all statements. */
    private final List<AbstractStatement_mxJPO> statements = new ArrayList<>();

    /** All variables used for the execution of this script. */
    private final Map<String,String> variables = new HashMap<>();

    /**
     * Parses the given {@code _code}.
     *
     * @param _code     code to parse
     * @return this script handler
     * @throws Exception if parse fails
     */
    public ScriptHandler_mxJPO parse(final String _code)
        throws Exception
    {
        new ScriptParser_mxJPO(new StringReader(_code)).parse(this);
        return this;
    }

    /**
     * Appends a new statement to this script handler.
     *
     * @param _statement    statement
     * @return this script handler
     */
    public ScriptHandler_mxJPO addStatement(final AbstractStatement_mxJPO _statement)
    {
        this.statements.add(_statement);
        return this;
    }

    /**
     * Defines a new variable with value used within intialization.
     *
     * @param _varName  name of variable
     * @param _value    name of value
     * @return this script handler
     */
    public ScriptHandler_mxJPO defVar(final String _varName,
                                      final String _value)
    {
        this.variables.put(_varName, _value);
        return this;
    }

    /**
     * Executes the script.
     *
     * @param _paramCache   parameter cache with the MX context
     * @throws Exception if update failed
     */
    public void execute(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final ScriptContext_mxJPO context = new ScriptContext_mxJPO(_paramCache, this.variables);
        for (final AbstractStatement_mxJPO stmt : this.statements)  {
            stmt.execute(context);
        }
    }
}
