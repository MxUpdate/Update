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

package org.mxupdate.update.util;

import matrix.db.Context;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 * @todo description
 */
public class JPOCaller_mxJPO
{
    /**
     * Stores the current caller instance.
     */
    private static JPOCallerInterface CALLER_INSTANCE;

    /**
     * Stores the current caller instance.
     */
    private static ParameterCache_mxJPO PARAM_CACHE;

    /**
     * Defines new instance which will be called within a TCL program.
     *
     * @param _paramCache       parameter cache
     * @param _instance         new instance
     */
    public static void defineInstance(final ParameterCache_mxJPO _paramCache,
                                      final JPOCallerInterface _instance)
    {
        PARAM_CACHE = _paramCache;
        CALLER_INSTANCE = _instance;
    }

    /**
     * Undefines new instance which will be called within a TCL program.
     *
     * @param _instance new instance
     */
    public static void undefineInstance(final ParameterCache_mxJPO _paramCache,
                                        final JPOCallerInterface _instance)
    {
        PARAM_CACHE = null;
        CALLER_INSTANCE = null;
    }

    /**
     *
     * @param _context          context for this request
     * @param _args             list of arguments, first argument is the Mime64
     *                          coded serialized class, rest of arguments are
     *                          used for the JPO call
     * @throws Exception if execution fails
     */
    public void mxMain(final Context _context,
                       final String[] _args)
            throws Exception
    {
        try  {
            CALLER_INSTANCE.jpoCallExecute(PARAM_CACHE.clone(_context), _args);
        } catch (Exception e)  {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     */
    public interface JPOCallerInterface
    {
        /**
         *
         * @param _paramCache   parameter cache
         * @param _args         list of arguments
         * @throws Exception
         */
        public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                                   final String... _args)
                throws Exception;
    }
}
