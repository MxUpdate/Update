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

package org.mxupdate.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

import org.apache.commons.codec.binary.Base64;
import org.mxupdate.util.MqlUtil_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract test class to connect to MX and disconnect incl. some helper
 * methods to execute MQL commands and JPOs.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class AbstractTest
{
    /**
     * MX context.
     */
    private Context context;

    /**
     * Connects to MX.
     *
     * @throws Exception if connect failed
     */
    @BeforeClass
    public void connect()
        throws Exception
    {
        this.context = new Context("http://172.16.62.130:8080/enovia");
        this.context.resetContext("creator", "", null);
        this.context.connect();
    }

    /**
     * Disconnects from MX.
     *
     * @throws Exception if disconnect failed
     */
    @AfterClass
    public void close()
        throws Exception
    {
        this.context.disconnect();
        this.context.closeContext();
        this.context = null;
    }

    /**
     * Executes given MQL command statement <code>_cmd</code> and returns the
     * result.
     *
     * @param _cmd  MQL command statement to execute
     * @return returned string value from the called MQL command
     *         <code>_cmd</code>
     * @throws MatrixException if MQL execution failed
     */
    protected String mql(final CharSequence _cmd)
        throws MatrixException
    {
        return MqlUtil_mxJPO.execMql(this.context, _cmd);
    }

    /**
     * Calls given <code>_method</code> in <code>_jpo</code>. The MX context
     * {@link #mxContext} is connected to the database if not already done.
     *
     * @param <T>           class which is returned
     * @param _jpo          name of JPO to call
     * @param _method       method of the called <code>_jpo</code>
     * @param _parameters   list of all parameters for the <code>_jpo</code>
     *                      which are automatically encoded encoded
     * @return returned value from the called <code>_jpo</code>
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if the called <code>_jpo</code> throws an
     *                          exception
     * @see #context
     * @see JPOReturn
     */
    protected <T> JPOReturn<T> jpoInvoke(final String _jpo,
                              final String _method,
                              final Object... _parameters)
        throws IOException, MatrixException
    {
        // encode parameters
        final String[] paramStrings = new String[_parameters.length];
        int idx = 0;
        for (final Object parameter : _parameters)  {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(parameter);
            oos.close();
            paramStrings[idx++] = new String(Base64.encodeBase64(out.toByteArray()));
        }

        return new JPOReturn<T>(JPO.invoke(this.context,
                                           _jpo,
                                           null,
                                           _method,
                                           paramStrings,
                                           Object.class));
    }

    /**
     * Class holding the returned values from the JPO.
     *
     * @param <T>   type of the returned values
     * @see AbstractTest#jpoInvoke(String, String, Object...)
     */
    protected final class JPOReturn<T>
    {
        /**
         * Returns map from the JPO invoke.
         */
        private final Map<String,?> jpoReturn;

        /**
         * Constructor to hold the return map from the JPO invoke.
         *
         * @param _jpoReturn    return map from the JPO invoke
         */
        @SuppressWarnings("unchecked")
        private JPOReturn(final Object _jpoReturn)
        {
            if (_jpoReturn instanceof String)  {
                this.jpoReturn = new HashMap<String,Object>();
                ((Map<String,Object>) this.jpoReturn).put("values", _jpoReturn);
            } else  {
                this.jpoReturn = (Map<String,?>) _jpoReturn;
            }
        }

        /**
         * Returns the stored values from the JPO return.
         *
         * @return stored values from the JPO return
         * @see #jpoReturn
         */
        @SuppressWarnings("unchecked")
        public T getValues()
        {
            return (T) this.jpoReturn.get("values");
        }
    }
}
