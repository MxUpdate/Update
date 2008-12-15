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

package net.sourceforge.mxupdate.update.util;


/**
 *
 * @author tmoxter
 * @version $Id$
 */
public class BusObject_mxJPO
        implements Comparable<BusObject_mxJPO>
{
    /**
     * Type of the business object.
     */
    final String type;

    /**
     * Name of the business object.
     */
    final String name;

    /**
     * Revision of the business object.
     */
    final String revision;

    public BusObject_mxJPO(final String _type,
                           final String _name,
                           final String _revision)
    {
        this.type = _type;
        this.name = _name;
        this.revision = _revision;
    }

    /**
     * Getter method of instance variable {@link #type}.
     *
     * @return type of the business object
     * @see #type
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * Getter method of instance variable {@link #name}.
     *
     * @return name of the business object
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Getter method of instance variable {@link #revision}.
     *
     * @return revision of the business object
     * @see #revision
     */
    public String getRevision()
    {
        return this.revision;
    }

    /**
     * Returns the string representation of the business object. The string
     * representation is a concatenation of {@link #type}, {@link #name} and
     * {@link #revision}.
     *
     * @return string representation of the business object
     * @see #type
     * @see #name
     * @see #revision
     */
    @Override
    public String toString()
    {
        return "[type = " + this.type + ","
               + " name = " + this.name + ","
               + " revision = " + this.revision + "]";
    }

    /**
     * Compares given business object with this business object. First the type
     * is compared. If the types are equal, the names are compared. If the
     * names are equal, the revisions are compared.
     *
     * @param _compare      business object to compare
     */
    public int compareTo(final BusObject_mxJPO _compare)
    {
        return this.type.equals(_compare.type)
               ? this.name.equals(_compare.name)
                       ? this.revision.equals(_compare.revision)
                               ? 0
                               : this.revision.compareTo(_compare.revision)
                       : this.name.compareTo(_compare.name)
               : this.type.compareTo(_compare.type);
    }
}