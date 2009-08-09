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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractAttribute;
import org.mxupdate.test.data.datamodel.AttributeBoolean;
import org.mxupdate.test.data.datamodel.AttributeDate;
import org.mxupdate.test.data.datamodel.AttributeInteger;
import org.mxupdate.test.data.datamodel.AttributeReal;
import org.mxupdate.test.data.datamodel.AttributeString;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.AbstractProgram;
import org.mxupdate.test.data.program.MQLProgram;
import org.mxupdate.test.data.userinterface.Command;
import org.mxupdate.test.data.userinterface.Menu;

/**
 * Collection of test data.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class DataCollection
{
    /**
     * Related test class for which this data collection is created.
     */
    private final AbstractTest test;

    /**
     * All test attributes.
     *
     * @see #getAttributeBoolean(String)
     * @see #getAttributeDate(String)
     * @see #getAttributeInteger(String)
     * @see #getAttributeReal(String)
     * @see #getAttributeString(String)
     * @see #getAttributes()
     */
    private final Map<String,AbstractAttribute<?>> attributes = new HashMap<String,AbstractAttribute<?>>();

    /**
     * All commands.
     *
     * @see #getCommand(String)
     */
    private final Map<String,Command> commands = new HashMap<String,Command>();

    /**
     * All menus.
     *
     * @see #getMenu(String)
     */
    private final Map<String,Menu> menus = new HashMap<String,Menu>();

    /**
     * All programs.
     *
     * @see #getMQLProgram(String)
     * @see #getPrograms()
     */
    private final Map<String,AbstractProgram<?>> programs = new HashMap<String,AbstractProgram<?>>();

    /**
     * All types.
     *
     * @see #getType(String)
     * @see #getTypes()
     */
    private final Map<String,TypeData> types = new HashMap<String,TypeData>();

    /**
     * Initializes this data collection instance with the test instance.
     *
     * @param _test     related test instance
     */
    public DataCollection(final AbstractTest _test)
    {
        this.test = _test;
    }

    /**
     * Creates all data.
     *
     * @throws MatrixException if create failed
     */
    public void create()
        throws MatrixException
    {
        for (final AbstractProgram<?> program : this.programs.values())  {
            program.create();
        }
        for (final AbstractAttribute<?> attribute : this.attributes.values())  {
            attribute.create();
        }
        for (final TypeData type : this.types.values())  {
            type.create();
        }
        for (final Command command : this.commands.values())  {
            command.create();
        }
        for (final Menu menu : this.menus.values())  {
            menu.create();
        }
    }

    /**
     * Returns the related boolean attribute instance for <code>_name</code>.
     * If a boolean attribute is not already defined, a new boolean attribute
     * instance is created.
     *
     * @param _name     name of searched boolean attribute
     * @return boolean attribute instance
     * @see #attributesBoolean
     */
    public AttributeBoolean getAttributeBoolean(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeBoolean(this.test, _name));
        }
        return (AttributeBoolean) this.attributes.get(_name);
    }

    /**
     * Returns the related date attribute instance for <code>_name</code>.
     * If a date attribute is not already defined, a new date attribute
     * instance is created.
     *
     * @param _name     name of searched date attribute
     * @return date attribute instance
     * @see #attributesDate
     */
    public AttributeDate getAttributeDate(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeDate(this.test, _name));
        }
        return (AttributeDate) this.attributes.get(_name);
    }

    /**
     * Returns the related integer attribute instance for <code>_name</code>.
     * If a integer attribute is not already defined, a new integer attribute
     * instance is created.
     *
     * @param _name     name of searched integer attribute
     * @return integer attribute instance
     * @see #attributesInteger
     */
    public AttributeInteger getAttributeInteger(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeInteger(this.test, _name));
        }
        return (AttributeInteger) this.attributes.get(_name);
    }

    /**
     * Returns the related real attribute instance for <code>_name</code>.
     * If a real attribute is not already defined, a new real attribute
     * instance is created.
     *
     * @param _name     name of searched real attribute
     * @return real attribute instance
     * @see #attributesReal
     */
    public AttributeReal getAttributeReal(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeReal(this.test, _name));
        }
        return (AttributeReal) this.attributes.get(_name);
    }

    /**
     * Returns the related string attribute instance for <code>_name</code>.
     * If a string attribute is not already defined, a new string attribute
     * instance is created.
     *
     * @param _name     name of searched string attribute
     * @return string attribute instance
     * @see #attributesString
     */
    public AttributeString getAttributeString(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeString(this.test, _name));
        }
        return (AttributeString) this.attributes.get(_name);
    }

    /**
     * Returns all attributes of this data collection.
     *
     * @return collection of all attributes
     * @see #attributes
     */
    public Collection<AbstractAttribute<?>> getAttributes()
    {
        return this.attributes.values();
    }

    /**
     * Returns the related command instance for <code>_name</code>. If a
     * command is not already defined, a new command instance is created.
     *
     * @param _name     name of searched command
     * @return command instance
     * @see #commands
     */
    public Command getCommand(final String _name)
    {
        if (!this.commands.containsKey(_name))  {
            this.commands.put(_name, new Command(this.test, _name));
        }
        return this.commands.get(_name);
    }

    /**
     * Returns the related menu instance for <code>_name</code>. If a
     * menu is not already defined, a new menu instance is created.
     *
     * @param _name     name of searched menu
     * @return menu instance
     * @see #menus
     */
    public Menu getMenu(final String _name)
    {
        if (!this.menus.containsKey(_name))  {
            this.menus.put(_name, new Menu(this.test, _name));
        }
        return this.menus.get(_name);
    }

    /**
     * Returns the related program instance for <code>_name</code>. If a
     * MQL program is not already defined, a MQL program instance is created.
     *
     * @param _name     name of searched MQL program
     * @return MQL program instance
     * @see #programs
     */
    public MQLProgram getMQLProgram(final String _name)
    {
        if (!this.programs.containsKey(_name))  {
            this.programs.put(_name, new MQLProgram(this.test, _name));
        }
        return (MQLProgram) this.programs.get(_name);
    }

    /**
     * Returns all programs of this data collection.
     *
     * @return collection of all programs
     * @see #programs
     */
    public Collection<AbstractProgram<?>> getPrograms()
    {
        return this.programs.values();
    }

    /**
     * Returns the related type instance for <code>_name</code>. If a
     * type is not already defined, a type instance is created.
     *
     * @param _name     name of searched type
     * @return type instance
     * @see #types
     */
    public TypeData getType(final String _name)
    {
        if (!this.types.containsKey(_name))  {
            this.types.put(_name, new TypeData(this.test, _name));
        }
        return this.types.get(_name);
    }

    /**
     * Returns all types of this data collection.
     *
     * @return collection of all types
     * @see #types
     */
    public Collection<TypeData> getTypes()
    {
        return this.types.values();
    }
}
