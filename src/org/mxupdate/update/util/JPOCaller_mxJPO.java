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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.update.AbstractPropertyObject_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

/**
 * The class could be executed within the TCL update to call the original JPO
 * update instance. To use this feature, the JPO update class must be defined
 * from {@link AbstractPropertyObject_mxJPO}. In the derived class method
 * {@link AbstractPropertyObject_mxJPO#jpoCallExecute(ParameterCache_mxJPO, String...)}
 * must be overwritten.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class JPOCaller_mxJPO
{
    /**
     * Stores the current caller instance depending on the MX session context
     * id.
     *
     * @see #defineInstance(ParameterCache_mxJPO, AbstractPropertyObject_mxJPO)
     * @see #undefineInstance(ParameterCache_mxJPO)
     * @see #mxMain(Context, String[])
     */
    private static final Map<String,AbstractPropertyObject_mxJPO> CALLER_INSTANCE
            = new HashMap<String,AbstractPropertyObject_mxJPO>();

    /**
     * Stores the current caller instance depending on the MX session context
     * id.
     *
     * @see #defineInstance(ParameterCache_mxJPO, AbstractPropertyObject_mxJPO)
     * @see #undefineInstance(ParameterCache_mxJPO)
     * @see #mxMain(Context, String[])
     */
    private static final Map<String,ParameterCache_mxJPO> PARAM_CACHE
            = new HashMap<String,ParameterCache_mxJPO>();

    /**
     * Prefix for the name of the global environment variable to define the
     * name of the class which must be defined to call from MQL. The global
     * environment variable gets a prefix of the MX session id.
     *
     * @see #defineInstance(ParameterCache_mxJPO, AbstractPropertyObject_mxJPO)
     * @see #undefineInstance(ParameterCache_mxJPO)
     * @see #mxMain(Context, String[])
     */
    private static final String ENV_CLASS_NAME = "MXUPDATE_JPOCALLER_CLASS";

    /**
     * The class must have a public constructor so that the class could be
     * initialized from MX (otherwise an illegal access exception is thrown).
     */
    public JPOCaller_mxJPO()
    {
    }

    /**
     * Defines given <code>_instance</code> and <code>_paramCache</code> which
     * will be called within a TCL program. The name of this JPO is also stored
     * in a global environment variable so that the instance and parameter
     * cache could be evaluated in all cases.
     *
     * @param _paramCache       parameter cache with MX context
     * @param _instance         new instance
     * @throws MatrixException if global environment variable which defines the
     *                         instance could not be defined (MQL call failed)
     * @see #PARAM_CACHE
     * @see #CALLER_INSTANCE
     * @see #ENV_CLASS_NAME
     * @see #mxMain(Context, String[])
     */
    public static void defineInstance(final ParameterCache_mxJPO _paramCache,
                                      final AbstractPropertyObject_mxJPO _instance)
            throws MatrixException
    {
        final String sessionId = _paramCache.getContext().getSession().getSessionId();
        JPOCaller_mxJPO.PARAM_CACHE.put(sessionId, _paramCache);
        JPOCaller_mxJPO.CALLER_INSTANCE.put(sessionId, _instance);
        MqlUtil_mxJPO.execMql(_paramCache.getContext(),
                new StringBuilder()
                    .append("escape set env global \"")
                    .append(JPOCaller_mxJPO.ENV_CLASS_NAME).append(StringUtil_mxJPO.convertMql(sessionId))
                    .append("\" \"${CLASSNAME}\""));
    }

    /**
     * Removes from current MX session context the instance which could be
     * called.
     *
     * @param _paramCache       parameter cache with MX context
     * @throws MatrixException if the global environment variable which defines
     *                          the instance could not be removed (MQL call
     *                          failed)
     * @see #PARAM_CACHE
     * @see #CALLER_INSTANCE
     * @see #ENV_CLASS_NAME
     */
    public static void undefineInstance(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        final String sessionId = _paramCache.getContext().getSession().getSessionId();
        JPOCaller_mxJPO.PARAM_CACHE.remove(sessionId);
        JPOCaller_mxJPO.CALLER_INSTANCE.remove(sessionId);
        MqlUtil_mxJPO.execMql(_paramCache.getContext(),
                new StringBuilder()
                    .append("escape unset env global \"")
                    .append(JPOCaller_mxJPO.ENV_CLASS_NAME).append(StringUtil_mxJPO.convertMql(sessionId))
                    .append("\""));
    }

    /**
     * <p>The method calls the original instance stored depending on the matrix
     * session id. If for the current MX session id no instance of no parameter
     * cache defined, errors are thrown.<p/>
     * <p>In some cases (especially if MxUpdate is installed the first time) it
     * could be that {@link #CALLER_INSTANCE} and {@link #PARAM_CACHE} set from
     * {@link #defineInstance(ParameterCache_mxJPO, AbstractPropertyObject_mxJPO)}
     * within another JPO class than the class called for this method. In this
     * case MX makes an automatically compile of the class. But this means also
     * that the caller instance and the parameter cache could not be accessed
     * directly. Instead the original JPO class which is used when the instance
     * was defined is used (via Java reflection).</p>
     *
     * @param _context  context for this request
     * @param _args     list of arguments
     * @throws Exception if execution fails
     * @see #PARAM_CACHE
     * @see #CALLER_INSTANCE
     * @see #ENV_CLASS_NAME
     * @see #defineInstance(ParameterCache_mxJPO, AbstractPropertyObject_mxJPO)
     */
    public static void mxMain(final Context _context,
                              final String[] _args)
            throws Exception
    {
        try  {
            final String sessionId = _context.getSession().getSessionId();

            final String callerClazzName = MqlUtil_mxJPO.execMql(_context,
                    new StringBuilder()
                        .append("escape get env global \"")
                        .append(JPOCaller_mxJPO.ENV_CLASS_NAME).append(StringUtil_mxJPO.convertMql(sessionId))
                        .append("\""));
            // is original JPO calling class not current JPO calling class?
            if ("${CLASSNAME}".equals(callerClazzName))  {
                final Class<?> callerClazz = Class.forName("org.mxupdate.update.util." + callerClazzName);
                if (callerClazz == null)  {
                    throw new Error("JPO Caller class " + callerClazzName
                            + " does not exists in the Java VM!");
                }
                final Method method = callerClazz.getMethod("mxMain", Context.class, String[].class);
                if (method == null)  {
                    throw new Error("Static method 'mxMain' of " + callerClazzName + " does not exists!");
                }
                method.invoke(null, _context, _args);
            } else  {
                final AbstractPropertyObject_mxJPO instance = JPOCaller_mxJPO.CALLER_INSTANCE.get(sessionId);
                final ParameterCache_mxJPO paramCache = JPOCaller_mxJPO.PARAM_CACHE.get(sessionId);
                if (instance == null)  {
                    throw new Error("JPO Caller instance is not defined for session "
                            + sessionId + "!");
                }
                if (paramCache == null)  {
                    throw new Error("Old Parameter Cache instance is not defined for session "
                            + sessionId + "!");
                }
                instance.jpoCallExecute(paramCache.clone(_context), _args);
            }
        } catch (final Exception e)  {
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
