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
import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.datamodel.AttributeBooleanData;
import org.mxupdate.test.data.datamodel.AttributeDateData;
import org.mxupdate.test.data.datamodel.AttributeIntegerData;
import org.mxupdate.test.data.datamodel.AttributeRealData;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.AbstractProgramData;
import org.mxupdate.test.data.program.JPOProgramData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.program.PageData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.userinterface.CommandData;
import org.mxupdate.test.data.userinterface.InquiryData;
import org.mxupdate.test.data.userinterface.MenuData;

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
     * @see #create()
     */
    private final Map<String,AbstractAttributeData<?>> attributes = new HashMap<String,AbstractAttributeData<?>>();

    /**
     * All commands.
     *
     * @see #getCommand(String)
     * @see #create()
     */
    private final Map<String,CommandData> commands = new HashMap<String,CommandData>();

    /**
     * All inquiries.
     *
     * @see #getInquiry(String)
     * @see #create()
     */
    private final Map<String,InquiryData> inquiries = new HashMap<String,InquiryData>();

    /**
     * All interfaces.
     *
     * @see #getInterface(String)
     * @see #create()
     */
    private final Map<String,InterfaceData> interfaces = new HashMap<String,InterfaceData>();

    /**
     * All menus.
     *
     * @see #getMenu(String)
     * @see #create()
     */
    private final Map<String,MenuData> menus = new HashMap<String,MenuData>();

    /**
     * All pages.
     *
     * @see #getPage(String)
     * @see #create()
     */
    private final Map<String,PageData> pages = new HashMap<String,PageData>();

    /**
     * All programs.
     *
     * @see #getMQLProgram(String)
     * @see #getPrograms()
     * @see #create()
     */
    private final Map<String,AbstractProgramData<?>> programs = new HashMap<String,AbstractProgramData<?>>();

    /**
     * All relationships.
     *
     * @see #getRelationship(String)
     * @see #create()
     */
    private final Map<String,RelationshipData> relationships = new HashMap<String,RelationshipData>();

    /**
     * All roles.
     *
     * @see #getRole(String)
     * @see #create()
     */
    private final Map<String,RoleData> roles = new HashMap<String,RoleData>();

    /**
     * All types.
     *
     * @see #getType(String)
     * @see #getTypes()
     * @see #create()
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
        for (final AbstractProgramData<?> program : this.programs.values())  {
            program.create();
        }
        for (final AbstractAttributeData<?> attribute : this.attributes.values())  {
            attribute.create();
        }
        for (final TypeData type : this.types.values())  {
            type.create();
        }
        for (final RelationshipData relationship : this.relationships.values())  {
            relationship.create();
        }
        for (final InterfaceData inter : this.interfaces.values())  {
            inter.create();
        }
        for (final RoleData role : this.roles.values())  {
            role.create();
        }
        for (final CommandData command : this.commands.values())  {
            command.create();
        }
        for (final InquiryData inquiry : this.inquiries.values())  {
            inquiry.create();
        }
        for (final MenuData menu : this.menus.values())  {
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
    public AttributeBooleanData getAttributeBoolean(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeBooleanData(this.test, _name));
        }
        return (AttributeBooleanData) this.attributes.get(_name);
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
    public AttributeDateData getAttributeDate(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeDateData(this.test, _name));
        }
        return (AttributeDateData) this.attributes.get(_name);
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
    public AttributeIntegerData getAttributeInteger(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeIntegerData(this.test, _name));
        }
        return (AttributeIntegerData) this.attributes.get(_name);
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
    public AttributeRealData getAttributeReal(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeRealData(this.test, _name));
        }
        return (AttributeRealData) this.attributes.get(_name);
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
    public AttributeStringData getAttributeString(final String _name)
    {
        if (!this.attributes.containsKey(_name))  {
            this.attributes.put(_name, new AttributeStringData(this.test, _name));
        }
        return (AttributeStringData) this.attributes.get(_name);
    }

    /**
     * Returns all attributes of this data collection.
     *
     * @return collection of all attributes
     * @see #attributes
     */
    public Collection<AbstractAttributeData<?>> getAttributes()
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
    public CommandData getCommand(final String _name)
    {
        if (!this.commands.containsKey(_name))  {
            this.commands.put(_name, new CommandData(this.test, _name));
        }
        return this.commands.get(_name);
    }

    /**
     * Returns the related inquiry instance for <code>_name</code>. If an
     * inquiry is not already defined, a new inquiry instance is created.
     *
     * @param _name     name of searched inquiry
     * @return inquiry instance
     * @see #inquiries
     */
    public InquiryData getInquiry(final String _name)
    {
        if (!this.inquiries.containsKey(_name))  {
            this.inquiries.put(_name, new InquiryData(this.test, _name));
        }
        return this.inquiries.get(_name);
    }

    /**
     * Returns the related interface instance for <code>_name</code>. If an
     * interface is not already defined, a new interface instance is created.
     *
     * @param _name     name of searched inquiry
     * @return interface instance
     * @see #interfaces
     */
    public InterfaceData getInterface(final String _name)
    {
        if (!this.interfaces.containsKey(_name))  {
            this.interfaces.put(_name, new InterfaceData(this.test, _name));
        }
        return this.interfaces.get(_name);
    }

    /**
     * Returns the related menu instance for <code>_name</code>. If a
     * menu is not already defined, a new menu instance is created.
     *
     * @param _name     name of searched menu
     * @return menu instance
     * @see #menus
     */
    public MenuData getMenu(final String _name)
    {
        if (!this.menus.containsKey(_name))  {
            this.menus.put(_name, new MenuData(this.test, _name));
        }
        return this.menus.get(_name);
    }

    /**
     * Returns the related JPO program instance for <code>_name</code>. If a
     * JPO program is not already defined, a JPO program instance is created.
     *
     * @param _name     name of searched JPO program
     * @return JPO program instance
     * @see #programs
     */
    public JPOProgramData getJPOProgram(final String _name)
    {
        if (!this.programs.containsKey(_name))  {
            this.programs.put(_name, new JPOProgramData(this.test, _name));
        }
        return (JPOProgramData) this.programs.get(_name);
    }

    /**
     * Returns the related MQL program instance for <code>_name</code>. If a
     * MQL program is not already defined, a MQL program instance is created.
     *
     * @param _name     name of searched MQL program
     * @return MQL program instance
     * @see #programs
     */
    public MQLProgramData getMQLProgram(final String _name)
    {
        if (!this.programs.containsKey(_name))  {
            this.programs.put(_name, new MQLProgramData(this.test, _name));
        }
        return (MQLProgramData) this.programs.get(_name);
    }

    /**
     * Returns the related page instance for <code>_name</code>. If a
     * page is not already defined, a page instance is created.
     *
     * @param _name     name of searched page
     * @return page instance
     * @see #pages
     */
    public PageData getPage(final String _name)
    {
        if (!this.pages.containsKey(_name))  {
            this.pages.put(_name, new PageData(this.test, _name));
        }
        return this.pages.get(_name);
    }

    /**
     * Returns all programs of this data collection.
     *
     * @return collection of all programs
     * @see #programs
     */
    public Collection<AbstractProgramData<?>> getPrograms()
    {
        return this.programs.values();
    }

    /**
     * Returns the related relationship instance for <code>_name</code>. If a
     * relationship is not already defined, a relationship instance is created.
     *
     * @param _name     name of searched relationship
     * @return relationship instance
     * @see #relationships
     */
    public RelationshipData getRelationship(final String _name)
    {
        if (!this.relationships.containsKey(_name))  {
            this.relationships.put(_name, new RelationshipData(this.test, _name));
        }
        return this.relationships.get(_name);
    }

    /**
     * Returns the related role instance for <code>_name</code>. If a
     * role is not already defined, a role instance is created.
     *
     * @param _name     name of searched role
     * @return role instance
     * @see #roles
     */
    public RoleData getRole(final String _name)
    {
        if (!this.roles.containsKey(_name))  {
            this.roles.put(_name, new RoleData(this.test, _name));
        }
        return this.roles.get(_name);
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
