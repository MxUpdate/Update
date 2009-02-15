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

import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;

import org.mxupdate.update.AbstractPropertyObject_mxJPO;

/**
 * The class could be executed within the TCL update to call the original JPO
 * update instance. To use this feature, the JPO update class must be defined
 * from {@link AbstractPropertyObject_mxJPO}. There method
 * {@link AbstractPropertyObject_mxJPO#jpoCallExecute(ParameterCache_mxJPO, String...)
 * is defined which must be overwritten.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class JPOCaller_mxJPO
{
    /**
     * Stores the current caller instance depending on the MX session context
     * id.
     */
    private static final Map<String,AbstractPropertyObject_mxJPO> CALLER_INSTANCE
            = new HashMap<String,AbstractPropertyObject_mxJPO>();

    /**
     * Stores the current caller instance depending on the MX session context
     * id.
     */
    private static final Map<String,ParameterCache_mxJPO> PARAM_CACHE
            = new HashMap<String,ParameterCache_mxJPO>();

    /**
     * Defines new instance which will be called within a TCL program.
     *
     * @param _paramCache       parameter cache with MX context
     * @param _instance         new instance
     */
    public static void defineInstance(final ParameterCache_mxJPO _paramCache,
                                      final AbstractPropertyObject_mxJPO _instance)
    {
        final String sessionId = _paramCache.getContext().getSession().getSessionId();

        PARAM_CACHE.put(sessionId, _paramCache);
        CALLER_INSTANCE.put(sessionId, _instance);
    }

    /**
     * Removes from current MX session context the instance which could be
     * called.
     *
     * @param _paramCache       parameter cache with MX context
     */
    public static void undefineInstance(final ParameterCache_mxJPO _paramCache)
    {
        final String sessionId = _paramCache.getContext().getSession().getSessionId();
        PARAM_CACHE.remove(sessionId);
        CALLER_INSTANCE.remove(sessionId);
    }

    /**
     * The method calls the original instance stored depending on the matrix
     * session id. If for the current MX session id no instance of no parameter
     * cache defined, errors are thrown.
     *
     * @param _context  context for this request
     * @param _args     list of arguments
     * @throws Exception if execution fails
     */
    public void mxMain(final Context _context,
                       final String[] _args)
            throws Exception
    {
        try  {
            final String sessionId = _context.getSession().getSessionId();
            final AbstractPropertyObject_mxJPO instance = CALLER_INSTANCE.get(sessionId);
            final ParameterCache_mxJPO paramCache = PARAM_CACHE.get(sessionId);
            if (instance == null)  {
                throw new Error("JPO Caller instance is not defined for session "
                        + sessionId + "!");
            }
            if (paramCache == null)  {
                throw new Error("Old Parameter Cache instance is not defined for session "
                        + sessionId + "!");
            }
            instance.jpoCallExecute(paramCache.clone(_context), _args);
        } catch (final Exception e)  {
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
