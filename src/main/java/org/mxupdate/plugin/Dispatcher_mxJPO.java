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
import java.util.Map;

import matrix.db.Context;
import matrix.db.MatrixWriter;
import matrix.util.Mime64;

import org.mxupdate.update.util.ParameterCache_mxJPO;

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
     * Name of the &quot;Export&quot; method within the parameters map.
     */
    private static final String METHOD_EXPORT = "Export";

    /**
     * Name of the &quot;Search&quot; method within the parameters map.
     */
    private static final String METHOD_SEARCH = "Search";

    /**
     * Name of the &quot;TypeDefTreeList&quot; method within the parameters
     * map.
     */
    private static final String METHOD_TYPEDEFTREELIST = "TypeDefTreeList";

    /**
     * Name of the &quot;Update&quot; method within the parameters map.
     */
    private static final String METHOD_UPDATE = "Update";

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
        Map<String,?> ret = null;
        try {
            final Map<String,String> params     = this.<Map<String,String>>decode(_args, 0, null);
            final String method                 = this.<String>decode(_args, 1, null);
            final Map<String,Object> arguments  = this.<Map<String,Object>>decode(_args, 2, null);

            // initialize mapping
            final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(_context, true, params);

            if (Dispatcher_mxJPO.METHOD_EXPORT.equals(method))  {
                ret = new Export_mxJPO().execute(paramCache, arguments);
            } else if (Dispatcher_mxJPO.METHOD_SEARCH.equals(method))  {
                ret = new Search_mxJPO().execute(paramCache, arguments);
            } else if (Dispatcher_mxJPO.METHOD_TYPEDEFTREELIST.equals(method))  {
                ret = new TypeDefTreeList_mxJPO().execute(paramCache, arguments);
            } else if (Dispatcher_mxJPO.METHOD_UPDATE.equals(method))  {
                ret = new Update_mxJPO().execute(paramCache, arguments);
            } else  {
                throw new Exception("unknown plug-in method '" + method + "'");
            }
        } catch (final Exception exception)  {
            ret = this.prepareReturn(null, null, exception, null);
        }

        // and write return values to the matrix writer
        final MatrixWriter writer = new MatrixWriter(_context);
        writer.write(this.encode(ret));
        writer.flush();
        writer.close();
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
