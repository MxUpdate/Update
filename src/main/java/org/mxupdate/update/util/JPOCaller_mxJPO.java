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

package org.mxupdate.update.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;

/**
 * The class could be executed within the TCL update to call the original JPO
 * update instance. To use this feature, the JPO update class must be defined
 * from {@link AbstractPropertyObject_mxJPO}. In the derived class method
 * {@link AbstractPropertyObject_mxJPO#jpoCallExecute(ParameterCache_mxJPO, String...)}
 * must be overwritten.
 *
 * @author The MxUpdate Team
 */
public final class JPOCaller_mxJPO
{
    /**
     * Stores the current caller instance depending on the MX session context
     * id.
     */
    private static final Map<String,TypeDef_mxJPO> CALLER_INSTANCE = new HashMap<String,TypeDef_mxJPO>();

    /**
     * Stores the current caller instance depending on the MX session context
     * id.
     */
    private static final Map<String,ParameterCache_mxJPO> PARAM_CACHE = new HashMap<String,ParameterCache_mxJPO>();

    /**
     * Prefix for the name of the global environment variable to define the
     * name of the class which must be defined to call from MQL. The global
     * environment variable gets a prefix of the MX session id.
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
                                      final TypeDef_mxJPO _typeDef)
            throws MatrixException
    {
        final String sessionId = _paramCache.getContext().getSession().getSessionId();
        JPOCaller_mxJPO.PARAM_CACHE.put(sessionId, _paramCache);
        JPOCaller_mxJPO.CALLER_INSTANCE.put(sessionId, _typeDef);
        MqlUtil_mxJPO.execMql(_paramCache,
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
        MqlUtil_mxJPO.execMql(_paramCache,
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

            // is original JPO calling class not current JPO calling class?
            final String callerClazzName = MqlBuilder_mxJPO.mql().cmd("escape get env global ").arg(JPOCaller_mxJPO.ENV_CLASS_NAME + sessionId).exec(_context);
            if (!"${CLASSNAME}".equals(callerClazzName))  {
                final Class<?> callerClazz = Class.forName("org.mxupdate.update.util." + callerClazzName);
                if (callerClazz == null)  {
                    throw new Error("JPO Caller class " + callerClazzName + " does not exists in the Java VM!");
                }
                final Method method = callerClazz.getMethod("mxMain", Context.class, String[].class);
                if (method == null)  {
                    throw new Error("Static method 'mxMain' of " + callerClazzName + " does not exists!");
                }
                method.invoke(null, _context, _args);
            } else  {
                final ParameterCache_mxJPO paramCache = JPOCaller_mxJPO.PARAM_CACHE.get(sessionId);
                if (paramCache == null)  {
                    throw new Error("Old Parameter Cache instance is not defined for session " + sessionId + "!");
                }

                if (_args.length == 0)  {
                    throw new UpdateException_mxJPO(ErrorKey.JPOCALLER_JPO_CALL_METHOD_NOT_DEFINED);
                } else if ("mxUpdate".equals(_args[0]) && (_args.length == 9))  {
                    final TypeDef_mxJPO typeDef = JPOCaller_mxJPO.CALLER_INSTANCE.get(sessionId);
                    if (typeDef == null)  {
                        throw new Error("JPO Caller type definition is not defined for session " + sessionId + "!");
                    }

                    final String name   = _args[4].replaceAll("@2@2@", "\\\"").replaceAll("@1@1@", "'").replaceAll("@0@0@", "\\\\");
                    final String revi   = _args[5].replaceAll("@2@2@", "\\\"").replaceAll("@1@1@", "'").replaceAll("@0@0@", "\\\\");

                    final AbstractObject_mxJPO<?> instance = typeDef.newTypeInstance((typeDef.getMxAdminName() != null) ? name : name + BusObject_mxJPO.SPLIT_NAME + revi);

                    instance.jpoCallExecute(
                            paramCache.clone(_context),
                            // file
                            _args[2].replaceAll("@2@2@", "\\\"").replaceAll("@1@1@", "'").replaceAll("@0@0@", "\\\\"),
                            // file date
                            _args[3].replaceAll("@2@2@", "\\\"").replaceAll("@1@1@", "'").replaceAll("@0@0@", "\\\\"),
                            // code
                            _args[6].replaceAll("@2@2@", "\\\"").replaceAll("@1@1@", "'").replaceAll("@0@0@", "\\\\"),
                            // create
                            Boolean.valueOf(_args[7].replaceAll("@2@2@", "\\\"").replaceAll("@1@1@", "'").replaceAll("@0@0@", "\\\\")));
                } else if ("logDebug".equals(_args[0]) && (_args.length == 2))  {
                    paramCache.logDebug(_args[1]);
                } else if ("logError".equals(_args[0]) && (_args.length == 2))  {
                    paramCache.logError(_args[1]);
                } else if ("logInfo".equals(_args[0]) && (_args.length == 2))  {
                    paramCache.logInfo(_args[1]);
                } else if ("logTrace".equals(_args[0]) && (_args.length == 2))  {
                    paramCache.logTrace(_args[1]);
                } else if ("logWarning".equals(_args[0]) && (_args.length == 2))  {
                    paramCache.logWarning(_args[1]);
                } else  {
                    throw new UpdateException_mxJPO(ErrorKey.JPOCALLER_JPO_CALL_METHOD_UNKNOWN, Arrays.asList(_args));
                }
            }
        } catch (final Exception e)  {
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
