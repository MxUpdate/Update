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

package org.mxupdate.plugin;

import java.util.Map;

import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to execute MQL statements.
 *
 * @author The MxUpdate Team
 */
public class Execute_mxJPO
    extends AbstractPlugin_mxJPO
{
    /**
     * Argument key for the command to execute..
     */
    private static final String ARGUMENT_KEY_COMMAND = "Command"; //$NON-NLS-1$

    /**
     * <p>The given MQL command is executed and returned. The MQL command to
     * execute is defined with with argument {@link #ARGUMENT_KEY_COMMAND} in
     * the <code>_arguments</code>.</p>
     *
     * @param _paramCache   parameter cache with the MX context
     * @param _arguments    map with all arguments
     * @return returned value from the executed MQL command
     * @throws Exception if export failed
     */
    String execute(final ParameterCache_mxJPO _paramCache,
                   final Map<String,Object> _arguments)
        throws Exception
    {
        return MqlUtil_mxJPO.execMql(
                _paramCache,
                this.<String>getArgument(_arguments, Execute_mxJPO.ARGUMENT_KEY_COMMAND, null));
    }
}
