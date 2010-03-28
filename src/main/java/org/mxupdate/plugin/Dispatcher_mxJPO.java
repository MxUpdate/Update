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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;
import matrix.db.MatrixWriter;
import matrix.util.MatrixException;
import matrix.util.Mime64;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Abstract class which defines common methods to extract called parameters
 * from the Eclipse plug-in.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Dispatcher_mxJPO
    extends AbstractPlugin_mxJPO
{
    /**
     * Name of the key in the return map for the log message.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_LOG = "log"; //$NON-NLS-1$

    /**
     * Name of the key in the return map for the error message.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_ERROR = "error"; //$NON-NLS-1$

    /**
     * Name of the key in the return map for the exception.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_EXCEPTION = "exception"; //$NON-NLS-1$

    /**
     * Name of the key in the return map for the values.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_VALUES = "values"; //$NON-NLS-1$

    /**
     * Name of the &quot;Execute&quot; method within the parameters map.
     */
    private static final String METHOD_EXECUTE = "Execute"; //$NON-NLS-1$

    /**
     * Name of the &quot;Export&quot; method within the parameters map.
     */
    private static final String METHOD_EXPORT = "Export"; //$NON-NLS-1$

    /**
     * Name of the &quot;GetProperty&quot; method within the parameters map.
     */
    private static final String METHOD_GET_PROPERTY = "GetProperty"; //$NON-NLS-1$

    /**
     * Name of the &quot;GetVersion&quot; method within the parameters map.
     */
    private static final String METHOD_GET_VERSION = "GetVersion"; //$NON-NLS-1$

    /**
     * Name of the &quot;Search&quot; method within the parameters map.
     */
    private static final String METHOD_SEARCH = "Search"; //$NON-NLS-1$

    /**
     * Name of the &quot;TypeDefTreeList&quot; method within the parameters
     * map.
     */
    private static final String METHOD_TYPEDEFTREELIST = "TypeDefTreeList"; //$NON-NLS-1$

    /**
     * Name of the &quot;Update&quot; method within the parameters map.
     */
    private static final String METHOD_UPDATE = "Update"; //$NON-NLS-1$

    /**
     * Main method to dispatch between different plug-in methods.
     *
     * @param _context  MX context
     * @param _args     packed arguments
     * @throws IOException if returned values could not be written
     */
    public void mxMain(final Context _context,
                       final String... _args)
        throws IOException
    {
        Map<String,Object> ret = null;
        final PrintStream orgErr = System.err;
        final PrintStream orgOut = System.out;
        try {
            final Map<String,String> params     = this.<Map<String,String>>decode(_args, 0, null);
            final String method                 = this.<String>decode(_args, 1, null);
            final Map<String,Object> arguments  = this.<Map<String,Object>>decode(_args, 2, null);

            // initialize mapping
            final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true, params);

            try  {
                // define error stream
                final StringBuilder textErr = new StringBuilder();
                System.setErr(new PrintStream(new OutputStream() {
                    @Override()
                    public void write(final int _char)
                    {
                        if (_char == 10)  {
                            paramCache.logError(textErr.toString());
                            textErr.delete(0, textErr.length());
                        } else  {
                            textErr.append((char) _char);
                        }
                    }
                }));
                // define output stream
                final StringBuilder textOut = new StringBuilder();
                System.setOut(new PrintStream(new OutputStream() {
                    @Override()
                    public void write(final int _char)
                    {
                        if (_char == 10)  {
                            paramCache.logInfo(textOut.toString());
                            textOut.delete(0, textOut.length());
                        } else  {
                            textOut.append((char) _char);
                        }
                    }
                }));

                final Object bck;
                if (Dispatcher_mxJPO.METHOD_EXECUTE.equals(method))  {
                    bck = new Execute_mxJPO().execute(paramCache, arguments);
                } else if (Dispatcher_mxJPO.METHOD_EXPORT.equals(method))  {
                    bck = new Export_mxJPO().execute(paramCache, arguments);
                } else if (Dispatcher_mxJPO.METHOD_GET_PROPERTY.equals(method))  {
                    bck = new GetProperties_mxJPO().execute(paramCache, arguments);
                } else if (Dispatcher_mxJPO.METHOD_GET_VERSION.equals(method))  {
                    bck = new GetVersion_mxJPO().execute(paramCache, arguments);
                } else if (Dispatcher_mxJPO.METHOD_SEARCH.equals(method))  {
                    bck = new Search_mxJPO().execute(paramCache, arguments);
                } else if (Dispatcher_mxJPO.METHOD_TYPEDEFTREELIST.equals(method))  {
                    bck = new TypeDefTreeList_mxJPO().execute(paramCache, arguments);
                } else if (Dispatcher_mxJPO.METHOD_UPDATE.equals(method))  {
                    bck = new Update_mxJPO().execute(paramCache, arguments);
                } else  {
                    throw new Exception("unknown plug-in method '" + method + "'");
                }

                if (textErr.length() > 0)  {
                    paramCache.logError(textErr.toString());
                }
                if (textOut.length() > 0)  {
                    paramCache.logInfo(textOut.toString());
                }

                ret = this.prepareReturn(paramCache.getLogString(),
                                         (String) null,
                                         (Exception) null,
                                         bck);
            } catch (final Exception exception)  {
                ret = this.prepareReturn(paramCache.getLogString(), null, exception, null);
            }
        // in the case an exception occurred while param cache is initialized
        } catch (final Exception exception)  {
            ret = this.prepareReturn(null, null, exception, null);
        } finally  {
            System.setErr(orgErr);
            System.setOut(orgOut);
        }

        // and write return values to the matrix writer
        final MatrixWriter writer = new MatrixWriter(_context);
        writer.write(this.encode(ret));
        writer.write(10);
        writer.flush();
        writer.close();
    }

    /**
     * Packed the values to return in a map.
     *
     * @param <T>           defines the Java type of the values
     * @param _log          log message
     * @param _error        error message
     * @param _exception    throws exception
     * @param _values       values itself
     * @return arguments packed in a map
     */
    protected <T> Map<String, Object> prepareReturn(final String _log,
                                                    final String _error,
                                                    final Exception _exception,
                                                    final T _values)
    {
        final Map<String,Object> jpoReturn = new HashMap<String,Object>(4);
        jpoReturn.put(Dispatcher_mxJPO.RETURN_KEY_LOG,       _log);
        jpoReturn.put(Dispatcher_mxJPO.RETURN_KEY_ERROR,     _error);
        // MatrixException could not serialized and must be converted
        if (_exception instanceof MatrixException)  {
            final Exception newEx = new Exception(((MatrixException) _exception).toJniFormat());
            newEx.setStackTrace(_exception.getStackTrace());
            jpoReturn.put(Dispatcher_mxJPO.RETURN_KEY_EXCEPTION, newEx);
        } else if (_exception instanceof UpdateException_mxJPO)  {
            final Exception newEx = new Exception(_exception.getMessage());
            newEx.setStackTrace(_exception.getStackTrace());
            jpoReturn.put(Dispatcher_mxJPO.RETURN_KEY_EXCEPTION, newEx);
        } else  {
            jpoReturn.put(Dispatcher_mxJPO.RETURN_KEY_EXCEPTION, _exception);
        }
        jpoReturn.put(Dispatcher_mxJPO.RETURN_KEY_VALUES,    _values);
        return jpoReturn;
    }

    /**
     * Packs the parameters into a map
     * (with {@link #prepareReturn(String, String, Exception, Object)}) and
     * encodes this map as string (in Base64).
     *
     * @param _values       values itself
     * @return packed return values including log etc. as string encoded
     * @throws IOException if encoding failed
     */
    protected final String encode(final Map<String,?> _values)
        throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(_values);
        oos.close();

        return Mime64.encode(out.toByteArray());
    }
}
