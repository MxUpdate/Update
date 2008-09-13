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
public abstract class MatrixObject_mxJPO
{
    /**
     * Name of the matrix object.
     */
    private String name = null;

    /**
     * Description of the matrix object.
     */
    private String description = "";

    /**
     * Returns the file name for this matrix object. The file name is a
     * concatenation of the {@link #prefix} in upper case, an underline
     * (&quot;_&quot;), the {@link #name} of the matrix object and
     *  &quot;.tcl&quot; as extension.
     *
     * @return file name of this matrix object
     * @see #name
     * @see #prefix
     */
    public String getFileName()
    {
        return new StringBuilder()
                .append(getClass().getAnnotation(net.sourceforge.mxupdate.update.util.AdminType_mxJPO.class).value().toUpperCase())
                .append('_')
                .append(this.name)
                .append(".tcl")
                .toString();
    }

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

    public abstract Set<String> getMatchingNames(final Context _context,
                                                 final Collection<String> _matches)
            throws MatrixException;

    public abstract void export(final Context _context,
                                final File _path,
                                final String _name)
            throws MatrixException, SAXException, IOException;

    /**
     * Getter method for instance variable {@link #name}.
     *
     * @return value of instance variable {@link #name}.
     */
    protected String getName()
    {
        return this.name;
    }

    /**
     * Setter method for instance variable {@link #name}.
     *
     * @param _name new value for instance variable {@link #name}.
     */
    protected void setName(final String _name)
    {
        this.name = _name;
    }

    /**
     * Getter method for instance variable {@link #description}.
     *
     * @return value of instance variable {@link #description}.
     */
    protected String getDescription()
    {
        return this.description;
    }

    /**
     * Setter method for instance variable {@link #description}.
     *
     * @param _description new value for instance variable {@link #description}.
     */
    protected void setDescription(final String _description)
    {
        this.description = _description;
    }

}