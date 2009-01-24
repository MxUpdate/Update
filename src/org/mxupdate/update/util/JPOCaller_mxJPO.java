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

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import matrix.db.Context;
import matrix.util.Mime64;

/**
 *
 * @author tmoxter
 * @version $Id$
 * @todo description
 */
public class JPOCaller_mxJPO
{
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
            final ByteArrayInputStream bis = new ByteArrayInputStream(Mime64.decode(_args[0]));
            final ObjectInputStream ois = new ObjectInputStream(bis);

            // prepare arguments
            final String[] args = new String[_args.length - 1];
            System.arraycopy(_args, 1, args, 0, _args.length - 1);

            // execute JPO instance
            final JPOCallerInterface jpo = (JPOCallerInterface) ois.readObject();
            jpo.jpoCallExecute(_context, args);
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
         * @param _context      context for this request
         * @param _args         list of arguments
         * @throws Exception
         */
        public void jpoCallExecute(final Context _context,
                                   final String... _args)
                throws Exception;
    }
}
