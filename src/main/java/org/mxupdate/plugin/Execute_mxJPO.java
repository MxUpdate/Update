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

package org.mxupdate.plugin;

import java.util.Map;

import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The JPO class is the plug-in to execute MQL statements.
 *
 * @author The MxUpdate Team
 * @version $Id$
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
