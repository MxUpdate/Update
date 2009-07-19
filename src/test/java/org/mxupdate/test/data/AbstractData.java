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

package org.mxupdate.test.data;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;

/**
 *
 * @param <T>
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractData<T extends AbstractData<?>>
{
    /**
     * Related test case where this data piece was created.
     */
    private final AbstractTest test;

    /**
     * Related configuration item of this data piece.
     */
    private final CI ci;

    /**
     * Name of the data piece.
     */
    private final String name;

    /**
     * Symbolic name of the data piece.
     */
    private String symbolicName;

    /**
     * Values of this data piece.
     *
     * @see #setValue(String, String)
     * @see #getValue(String)
     * @see #getValues()
     */
    private final Map<String,String> values = new HashMap<String,String>();

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test     related test case
     * @param _ci       related configuration type
     * @param _name     name of the administration object
     */
    protected AbstractData(final AbstractTest _test,
                           final CI _ci,
                           final String _name)
    {
        this.test = _test;
        this.ci = _ci;
        this.name = AbstractTest.PREFIX + _name;
        this.symbolicName = _ci.getMxType() + "_"
                + this.name.replaceAll("[^%&()+-0123456789:=ABCDEFGHIJKLMNOPQRSTUVWXYZ^_abcdefghijklmnopqrstuvwxyz~]", "");
    }

    /**
     * Returns related test case with the MQL console.
     *
     * @return related test case
     * @see #test
     */
    protected AbstractTest getTest()
    {
        return this.test;
    }

    /**
     * Returns related configuration item type.
     *
     * @return configuration item type
     * @see #ci
     */
    protected CI getCI()
    {
        return this.ci;
    }

    /**
     * Returns the name of the abstract data element.
     *
     * @return name of the abstract data element
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Defines the symbolic name of this data piece.
     *
     * @param _symbolicName     new symbolic name
     * @return this original data instance
     * @see #symbolicName
     */
    @SuppressWarnings("unchecked")
    public T setSymbolicName(final String _symbolicName)
    {
        this.symbolicName = _symbolicName;
        return (T) this;
    }

    /**
     * Returns the symbolic name of the abstract data element.
     *
     * @return symbolic name of the abstract data element
     * @see #symbolicName
     */
    public String getSymbolicName()
    {
        return this.symbolicName;
    }

    /**
     * Defines a new value entry which is put into {@link #values}.
     *
     * @param _key      key of the value (e.g. &quot;description&quot;)
     * @param _value    value of the value
     * @return this original data instance
     * @see #values
     */
    @SuppressWarnings("unchecked")
    public T setValue(final String _key,
                      final String _value)
    {
        this.values.put(_key, _value);
        return (T) this;
    }

    /**
     * Returns the value for given key from the {@link #values} map.
     *
     * @param _key      name of the searched key
     * @return value for related <code>_key</code>; if not found
     *         <code>null</code>
     * @see #values
     */
    public String getValue(final String _key)
    {
        return this.values.get(_key);
    }

    /**
     * Returns the description of the abstract data element.
     *
     * @return description of the abstract data element
     * @see #description
     */
    public Map<String,String> getValues()
    {
        return this.values;
    }
}
