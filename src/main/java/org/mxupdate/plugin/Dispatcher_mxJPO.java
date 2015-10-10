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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

import matrix.db.Context;
import matrix.db.MatrixWriter;
import matrix.util.MatrixException;
import matrix.util.Mime64;

/**
 * Abstract class which defines common methods to extract called parameters
 * from the Eclipse plug-in.
 *
 * @author The MxUpdate Team
 */
public class Dispatcher_mxJPO
    extends AbstractPlugin_mxJPO
{
    /** Name of the key in the return map for the log message. */
    private static final String RETURN_KEY_LOG = "log"; //$NON-NLS-1$

    /** Name of the key in the return map for the error message. */
    private static final String RETURN_KEY_ERROR = "error"; //$NON-NLS-1$

    /** Name of the key in the return map for the exception. */
    private static final String RETURN_KEY_EXCEPTION = "exception"; //$NON-NLS-1$

    /** Name of the key in the return map for the values. */
    private static final String RETURN_KEY_VALUES = "values"; //$NON-NLS-1$

    /** Name of the &quot;Execute&quot; method within the parameters map. */
    private static final String METHOD_EXECUTE = "Execute"; //$NON-NLS-1$

    /**
     * Main method to dispatch between different plug-in methods.
     *
     * @param _context  MX context
     * @param _args     packed arguments as string with following meaning in
     *                  this order:
     *                  <ul>
     *                  <li>first argument are parameters defined from
     *                      {@link org.mxupdate.mapping.ParameterDef_mxJPO}</li>
     *                  <li>name of the method which must be called from the
     *                      dispatcher</li>
     *                  <li>arguments for the called method</li>
     *                  </ul>
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
        final Map<String,Object> jpoReturn = new HashMap<>(4);
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
