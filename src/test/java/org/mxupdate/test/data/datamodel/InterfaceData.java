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

package org.mxupdate.test.data.datamodel;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.testng.Assert;

/**
 * Used to define an interface, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class InterfaceData
    extends AbstractAdminData<InterfaceData>
{
    /**
     * Within export the description and abstract must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(2);
    static  {
        InterfaceData.REQUIRED_EXPORT_VALUES.add("description");
        InterfaceData.REQUIRED_EXPORT_VALUES.add("abstract");
    }

    /**
     * All parent interfaces of this interface.
     *
     * @see #addParent(InterfaceData)
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<InterfaceData> parents = new HashSet<InterfaceData>();

    /**
     * All attributes of this interface.
     *
     * @see #addAttribute(AbstractAttributeData)
     * @see #create()
     * @see #checkExport(ExportParser)
     */
    private final Set<AbstractAttributeData<?>> attributes = new HashSet<AbstractAttributeData<?>>();

    /**
     * All assigned types for this interface.
     *
     * @see #addType(TypeData)
     * @see #create()
     * @see #evalAdds4CheckExport(Set)
     * @see #allTypes
     */
    private final Set<TypeData> types = new HashSet<TypeData>();

    /**
     * All types are allowed for this interface.
     *
     * @see #addAllTypes()
     * @see #create()
     * @see #evalAdds4CheckExport(Set)
     * @see #types
     */
    private boolean allTypes = false;

    /**
     * All assigned relationships for this interface.
     *
     * @see #addRelationship(RelationshipData)
     * @see #create()
     * @see #evalAdds4CheckExport(Set)
     * @see #allRelationships
     */
    private final Set<RelationshipData> relationships = new HashSet<RelationshipData>();

    /**
     * All types are allowed for this interface.
     *
     * @see #addAllRelationships()
     * @see #create()
     * @see #evalAdds4CheckExport(Set)
     * @see #relationships
     */
    private boolean allRelationships = false;

    /**
     * Initialize this interface data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this interface is
     *                  defined)
     * @param _name     name of the interface
     */
    public InterfaceData(final AbstractTest _test,
                         final String _name)
    {
        super(_test, AbstractTest.CI.DM_INTERFACE, _name,
              InterfaceData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns the <code>_parent</code> interface to this interface.
     *
     * @param _parent   parent interface to assign
     * @return this interface data instance
     * @see #parents
     */
    public InterfaceData addParent(final InterfaceData _parent)
    {
        this.parents.add(_parent);
        return this;
    }

    /**
     * Clears the list of all {@link #parents}.
     *
     * @return this interface data instance
     * @see #parents
     */
    public InterfaceData removeParents()
    {
        this.parents.clear();
        return this;
    }

    /**
     * Assigns the <code>_attribute</code> to this interface.
     *
     * @param _attribute        attribute to assign
     * @return this interface data instance
     * @see #attributes
     */
    public InterfaceData addAttribute(final AbstractAttributeData<?> _attribute)
    {
        this.attributes.add(_attribute);
        return this;
    }

    /**
     * Assigns <code>_type</code> to the {@link #types list of assigned types}
     * for this interface.
     *
     * @param _type     type to assign
     * @return this interface data instance
     * @see #types
     */
    public InterfaceData addType(final TypeData _type)
    {
        this.types.add(_type);
        return this;
    }

    /**
     * Defines that the interface is assigned to all types.
     *
     * @return this interface data instance
     * @see #allTypes
     */
    public InterfaceData addAllTypes()
    {
        this.allTypes = true;
        return this;
    }

    /**
     * Clears the list of all {@link #types} and unset's the information
     * about {@link #allTypes all types}.
     *
     * @return this interface data instance
     * @see #allTypes
     * @see #types
     */
    public InterfaceData removeTypes()
    {
        this.allTypes = false;
        this.types.clear();
        return this;
    }

    /**
     * Assigns <code>_relationship</code> to the
     * {@link #relationships list of assigned relationships} for this
     * interface.
     *
     * @param _relationship     relationship to assign
     * @return this interface data instance
     * @see #relationships
     */
    public InterfaceData addRelationship(final RelationshipData _relationship)
    {
        this.relationships.add(_relationship);
        return this;
    }

    /**
     * Defines that the interface is assigned to all relationships.
     *
     * @return this interface data instance
     * @see #allRelationships
     */
    public InterfaceData addAllRelationships()
    {
        this.allRelationships = true;
        return this;
    }

    /**
     * Clears the list of all {@link #relationships} and unset's the information
     * about {@link #allRelationships all relationships}.
     *
     * @return this interface data instance
     * @see #allRelationships
     * @see #relationships
     */
    public InterfaceData removeRelationships()
    {
        this.allRelationships = false;
        this.relationships.clear();
        return this;
    }

    /**
     * Appends the adds for the {@link #types} and  {@link #relationships}.
     *
     * @param _needAdds     set with add strings used to append the adds
     * @see #allTypes
     * @see #types
     */
    @Override()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
        super.evalAdds4CheckExport(_needAdds);

        // append types
        if (this.allTypes)  {
            _needAdds.add("type all");
        } else  {
            for (final TypeData type : this.types)  {
                final StringBuilder cmd = new StringBuilder()
                        .append("type \"").append(AbstractTest.convertTcl(type.getName())).append("\"");
                _needAdds.add(cmd.toString());
            }
        }

        // append relationships
        if (this.allRelationships)  {
            _needAdds.add("relationship all");
        } else  {
            for (final RelationshipData relationship : this.relationships)  {
                final StringBuilder cmd = new StringBuilder()
                        .append("relationship \"").append(AbstractTest.convertTcl(relationship.getName())).append("\"");
                _needAdds.add(cmd.toString());
            }
        }
    }

    /**
     * Returns the TCL update file of this interface data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod interface \"${NAME}\"");
        this.append4CIFileValues(cmd);

        // append attributes
        cmd.append("\n\ntestAttributes -interface \"${NAME}\" -attributes [list \\\n");
        for (final AbstractAttributeData<?> attribute : this.attributes)  {
            cmd.append("    \"").append(AbstractTest.convertTcl(attribute.getName())).append("\" \\\n");
        }
        cmd.append("]\n");

        // append parent interfaces
        cmd.append("\n\ntestParents -interface \"${NAME}\" -parents [list \\\n");
        for (final InterfaceData parent : this.parents)  {
            cmd.append("    \"").append(AbstractTest.convertTcl(parent.getName())).append("\" \\\n");
        }
        cmd.append("]\n");

        return cmd.toString();
    }

    /**
     * Create the related interface in MX for this type data instance and
     * the {@link #types}.
     *
     * @return this interface data instance
     * @throws MatrixException if create failed
     * @see #types
     */
    @Override()
    public InterfaceData create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.setCreated(true);

            final StringBuilder cmd = new StringBuilder();
            cmd.append("escape add interface \"").append(AbstractTest.convertMql(this.getName())).append('\"');

            // add parent interfaces
            if (!this.parents.isEmpty())  {
                cmd.append(" derived ");
                boolean first = true;
                for (final InterfaceData parent : this.parents)  {
                    parent.create();
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append("\"").append(AbstractTest.convertMql(parent.getName())).append("\"");
                }
            }

            // add types
            if (this.allTypes)  {
                cmd.append(" type all");
            } else if (!this.types.isEmpty())  {
                cmd.append(" type ");
                boolean first = true;
                for (final TypeData type : this.types)  {
                    type.create();
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append("\"").append(AbstractTest.convertMql(type.getName())).append("\"");
                }
            }

            // add relationships
            if (this.allRelationships)  {
                cmd.append(" relationship all");
            } else if (!this.relationships.isEmpty())  {
                cmd.append(" relationship ");
                boolean first = true;
                for (final RelationshipData relationship : this.relationships)  {
                    relationship.create();
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append("\"").append(AbstractTest.convertMql(relationship.getName())).append("\"");
                }
            }

            // append attributes
            for (final AbstractAttributeData<?> attribute : this.attributes)  {
                attribute.create();
                cmd.append(" attribute \"").append(AbstractTest.convertMql(attribute.getName())).append("\"");
            }

            this.append4Create(cmd);

            this.getTest().mql(cmd);
        }
        return this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check parent interfaces
        final Set<String> pars = new HashSet<String>(_exportParser.getLines("/testParents/"));
        for (final InterfaceData parent : this.parents)  {
            final String parentName = "\"" + AbstractTest.convertTcl(parent.getName()) + "\" \\";
            Assert.assertTrue(pars.contains(parentName),
                              "check that parent interface '" + parent.getName() + "' is defined");
        }
        Assert.assertEquals(pars.size(),
                            this.parents.size(),
                            "check all parent interfaces are defined");

        // check attributes
        final Set<String> attrs = new HashSet<String>(_exportParser.getLines("/testAttributes/"));
        for (final AbstractAttributeData<?> attribute : this.attributes)  {
            final String attrName = "\"" + AbstractTest.convertTcl(attribute.getName()) + "\" \\";
            Assert.assertTrue(attrs.contains(attrName),
                              "check that attribute '" + attribute.getName() + "' is defined");
        }
        Assert.assertEquals(attrs.size(),
                            this.attributes.size(),
                            "check all attributes are defined");
    }
}
