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

package org.mxupdate.test.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import matrix.db.Context;

import org.mxupdate.mapping.Mapping_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * @author The MxUpdate Team
 * @version $Id$
 */
public class TestParameterCache
    extends ParameterCache_mxJPO
{
    /**
     * Default constructor.
     *
     * @throws Exception if the parmater cache could not be initialized
     */
    public TestParameterCache()
        throws Exception
    {
        super(null, false);
    }

    /**
     * Initialize the test mapping class (instance of {@link TestMapping}).
     *
     * @param _context      MX context for this request (not used)
     * @return initialized mapping instance
     * @throws Exception if the mapping class could not be initialized
     */
    @Override
    protected Mapping_mxJPO initMapping(final Context _context)
        throws Exception
    {
        return new TestMapping();
    }

    /**
     * Test mapping class to overwrite the read of the properties from a file
     * (instead from an existing program in MX).
     */
    private class TestMapping
        extends Mapping_mxJPO
    {
        /**
         * Default constructor.
         *
         * @throws Exception if the mapping could not be read
         */
        public TestMapping()
            throws Exception
        {
            super(null);
        }

        /**
         * Read the properties directly from the file system at
         * &quot;src/org/mxupdate/mapping.properties&quot;.
         *
         * @param _paramCache       parameter cache
         * @return read properties
         * @throws IOException if the properties from the file could not be
         *                     read
         */
        @Override
        protected Properties readProperties(final ParameterCache_mxJPO _paramCache)
                throws IOException
        {
            final Properties props =  new Properties();
            props.load(new FileInputStream("src/main/resources/org/mxupdate/mapping.properties"));
            return props;
        }
    }
}
