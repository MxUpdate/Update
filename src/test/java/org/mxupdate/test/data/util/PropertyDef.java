/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data.util;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * The class is used for the property definition of administration objects.
 *
 * @author The MxUpdate Team
 */
public class PropertyDef
{
    /**
     * Name of this property.
     *
     * @see #PropertyDef(String)
     * @see #PropertyDef(String, AbstractAdminData)
     * @see #PropertyDef(String, String)
     * @see #PropertyDef(String, String, AbstractAdminData)
     * @see #getName()
     */
    private final String name;

    /**
     * Value of the property.
     *
     * @see #PropertyDef(String, String)
     * @see #PropertyDef(String, String, AbstractAdminData)
     * @see #getValue()
     */
    private final String value;

    /**
     * Target administration object of this property.
     *
     * @see #PropertyDef(String, AbstractAdminData)
     * @see #PropertyDef(String, String, AbstractAdminData)
     * @see #getTo()
     */
    private final AbstractAdminData<?> to;

    /**
     * Initializes only the {@link #name}.
     *
     * @param _name     name of the property
     */
    public PropertyDef(final String _name)
    {
        this.name = _name;
        this.value = null;
        this.to = null;
    }

    /**
     * Initializes the {@link #name} and {@link #value}.
     *
     * @param _name     name of the property
     * @param _value    value of the property
     */
    public PropertyDef(final String _name,
                       final String _value)
    {
        this.name = _name;
        this.value = _value;
        this.to = null;
    }

    /**
     * Initializes the {@link #name} and {@link #to target object}.
     *
     * @param _name     name of the property
     * @param _to       target object of the property
     */
    public PropertyDef(final String _name,
                       final AbstractAdminData<?> _to)
    {
        this.name = _name;
        this.value = null;
        this.to = _to;
    }

    /**
     * Initializes the {@link #name}, {@link #value} and
     * {@link #to target object}.
     *
     * @param _name     name of the property
     * @param _value    value of the property
     * @param _to       target object of the property
     */
    public PropertyDef(final String _name,
                       final String _value,
                       final AbstractAdminData<?> _to)
    {
        this.name = _name;
        this.value = _value;
        this.to = _to;
    }

    /**
     * Returns the {@link #name} of the property.
     *
     * @return name of property
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the {@link #value} of the property.
     *
     * @return value of property
     * @see #value
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Returns the {@link #to target administration object} of the property.
     *
     * @return target administration object of property
     * @see #to
     */
    public AbstractAdminData<?> getTo()
    {
        return this.to;
    }

    /**
     * Creates the CI TCL string used within CI files.
     *
     * @param _ci   CI type where this property is defined
     * @return CI TCL string
     */
    public String getCITCLString(final AbstractTest.CI _ci)
    {
        final StringBuilder propDef = new StringBuilder()
        .append("mql escape add property \"").append(AbstractTest.convertTcl(this.name))
        .append("\" on ").append(_ci.getMxType())
        .append(" \"${NAME}\"");
        if (_ci == AbstractTest.CI.UI_TABLE)  {
            propDef.append(" system");
        }
        if (this.to != null)  {
            propDef.append(" to ").append(this.to.getCI().getMxType())
                   .append(" \"").append(AbstractTest.convertTcl(this.to.getName())).append('\"');
            if (this.to.getCI() == AbstractTest.CI.UI_TABLE)  {
                propDef.append(" system");
            }
        }
        if (this.value != null)  {
            propDef.append(" value \"").append(AbstractTest.convertTcl(this.value)).append('\"');
        }
        return propDef.toString();
    }
}
