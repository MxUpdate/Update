/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.xml.sax.SAXException;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractObject_mxJPO
{
    /**
     * Returns the path where the file is located of this matrix object. The
     * method used the path annotation.
     *
     * @return path.
     */
    public String getPath()
    {
        return getClass().getAnnotation(net.sourceforge.mxupdate.update.util.Path_mxJPO.class).value();
    }

    /**
     *
     * @param _context  context for this request
     * @param _matches  collection of match strings
     * @return set of names of this type matching the collection of strings
     * @throws MatrixException
     */
    public abstract Set<String> getMatchingNames(final Context _context,
                                                 final Collection<String> _matches)
            throws MatrixException;

    /**
     *
     * @param _context  context for this request
     * @param _path     path to write through
     * @param _name     name of object to export
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     */
    public abstract void export(final Context _context,
                                final File _path,
                                final String _name)
            throws MatrixException, SAXException, IOException;
}