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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

/**
 * Class is used to test relationship updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class RelationshipTest
    extends AbstractDataWithAttributesExportUpdateTest<RelationshipData>
{
    /**
     * Creates for given <code>_name</code> a new relationship data instance.
     *
     * @param _name     name of the relationship data instance
     * @return relationship data instance
     */
    @Override()
    protected RelationshipData createNewData(final String _name)
    {
        return new RelationshipData(this, _name);
    }

    /**
     * Data provider for test relationships.
     *
     * @return object array with all test relationships
     */
    @DataProvider(name = "data")
    public Object[][] getRelationships()
    {
        return this.prepareData("relationships",
                new Object[]{
                        "relationship without anything (to test required fields)",
                        new RelationshipData(this, "TestRelationship")},
                new Object[]{
                        "relationship without anything (to test escaped characters)",
                        new RelationshipData(this, "TestRelationship \" 1")},
                new Object[]{
                        "relationship without defined prevenduplicate flag (to test default value)",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .setPreventDuplicates(null)},
                new Object[]{
                        "relationship with attributes and triggers (to test escaped attributes and triggers)",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .addAttribute(new AttributeStringData(this, "String Attribute \" ' Hello"))
                                .addAttribute(new AttributeStringData(this, "String Attribute \" { Hello"))
                                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test Program")))},
                // from side
                new Object[]{
                        "relationship without from propagate connection",
                        new RelationshipData(this, "TestRelationship")
                                .from().setPropagateConnection(null)},
                new Object[]{
                        "relationship with from propagate connection true",
                        new RelationshipData(this, "TestRelationship")
                                .from().setPropagateConnection(true)},
                new Object[]{
                        "relationship with from propagate connection false",
                        new RelationshipData(this, "TestRelationship")
                                .from().setPropagateConnection(false)},
                new Object[]{
                        "relationship without from propagate modify",
                        new RelationshipData(this, "TestRelationship")
                                .from().setPropagateModify(null)},
                new Object[]{
                        "relationship with from propagate modify true",
                        new RelationshipData(this, "TestRelationship")
                                .from().setPropagateModify(true)},
                new Object[]{
                        "relationship with from propagate modify false",
                        new RelationshipData(this, "TestRelationship")
                                .from().setPropagateModify(false)},

                new Object[]{
                        "relationship with escaped from meaning",
                        new RelationshipData(this, "TestRelationship")
                                .from().setMeaning("this is a \"test\"")},

                new Object[]{
                        "relationship without from cardinality",
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(null)},
                new Object[]{
                        "relationship with from cardinality one",
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(RelationshipData.Cardinality.ONE)},
                new Object[]{
                        "relationship with from cardinality many",
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(RelationshipData.Cardinality.MANY)},

                new Object[]{
                        "relationship without from clone behavior",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(null)},
                new Object[]{
                        "relationship with from clone behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(RelationshipData.Behavior.NONE)},
                new Object[]{
                        "relationship with from clone behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(RelationshipData.Behavior.FLOAT)},
                new Object[]{
                        "relationship with from clone behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(RelationshipData.Behavior.REPLICATE)},

                new Object[]{
                        "relationship without from revision behavior",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(null)},
                new Object[]{
                        "relationship with from revision behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(RelationshipData.Behavior.NONE)},
                new Object[]{
                        "relationship with from revision behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(RelationshipData.Behavior.FLOAT)},
                new Object[]{
                        "relationship with from revision behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(RelationshipData.Behavior.REPLICATE)},

                new Object[]{
                        "relationship with one from type",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addType(new TypeData(this, "Test Type"))},
                new Object[]{
                        "relationship with two escaped from types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addType(new TypeData(this, "Test Type \" 1"))
                                .from().addType(new TypeData(this, "Test Type \" 2"))},
                new Object[]{
                        "relationship with all from types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addAllTypes()},
                new Object[]{
                        "relationship with one from relationship",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addRelationship(new RelationshipData(this, "Test Relationship"))},
                new Object[]{
                        "relationship with two escaped from relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addRelationship(new RelationshipData(this, "Test Relationship \" 1"))
                                .from().addRelationship(new RelationshipData(this, "Test Relationship \" 2"))},
                new Object[]{
                        "relationship with all from relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addAllRelationships()},
                new Object[]{
                        "relationship with two escaped from types and relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().addRelationship(new RelationshipData(this, "Test Relationship \" 1"))
                                .from().addRelationship(new RelationshipData(this, "Test Relationship \" 2"))
                                .from().addType(new TypeData(this, "Test Type \" 1"))
                                .from().addType(new TypeData(this, "Test Type \" 2"))},
                // to side
                new Object[]{
                        "relationship without to propagate connection",
                        new RelationshipData(this, "TestRelationship")
                                .to().setPropagateConnection(null)},
                new Object[]{
                        "relationship with to propagate connection true",
                        new RelationshipData(this, "TestRelationship")
                                .to().setPropagateConnection(true)},
                new Object[]{
                        "relationship with to propagate connection false",
                        new RelationshipData(this, "TestRelationship")
                                .to().setPropagateConnection(false)},
                new Object[]{
                        "relationship without to propagate modify",
                        new RelationshipData(this, "TestRelationship")
                                .to().setPropagateModify(null)},
                new Object[]{
                        "relationship with to propagate modify true",
                        new RelationshipData(this, "TestRelationship")
                                .to().setPropagateModify(true)},
                new Object[]{
                        "relationship with to propagate modify false",
                        new RelationshipData(this, "TestRelationship")
                                .to().setPropagateModify(false)},

                new Object[]{
                        "relationship with escaped meaning",
                        new RelationshipData(this, "TestRelationship")
                                .to().setMeaning("this is a \"test\"")},

                new Object[]{
                        "relationship without cardinality",
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(null)},
                new Object[]{
                        "relationship with cardinality one",
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(RelationshipData.Cardinality.ONE)},
                new Object[]{
                        "relationship with cardinality many",
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(RelationshipData.Cardinality.MANY)},

                new Object[]{
                        "relationship without clone behavior",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(null)},
                new Object[]{
                        "relationship with clone behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(RelationshipData.Behavior.NONE)},
                new Object[]{
                        "relationship with clone behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(RelationshipData.Behavior.FLOAT)},
                new Object[]{
                        "relationship with clone behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(RelationshipData.Behavior.REPLICATE)},

                new Object[]{
                        "relationship without to revision behavior",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(null)},
                new Object[]{
                        "relationship with to revision behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(RelationshipData.Behavior.NONE)},
                new Object[]{
                        "relationship with to revision behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(RelationshipData.Behavior.FLOAT)},
                new Object[]{
                        "relationship with to revision behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(RelationshipData.Behavior.REPLICATE)},

                new Object[]{
                        "relationship with one to type",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addType(new TypeData(this, "Test Type"))},
                new Object[]{
                        "relationship with two escaped to types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addType(new TypeData(this, "Test Type \" 1"))
                                .to().addType(new TypeData(this, "Test Type \" 2"))},
                new Object[]{
                        "relationship with all to types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addAllTypes()},
                new Object[]{
                        "relationship with one to relationship",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addRelationship(new RelationshipData(this, "Test Relationship"))},
                new Object[]{
                        "relationship with two escaped to relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addRelationship(new RelationshipData(this, "Test Relationship \" 1"))
                                .to().addRelationship(new RelationshipData(this, "Test Relationship \" 2"))},
                new Object[]{
                        "relationship with all to relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addAllRelationships()},
                new Object[]{
                        "relationship with two escaped to types and relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().addRelationship(new RelationshipData(this, "Test Relationship \" 1"))
                                .to().addRelationship(new RelationshipData(this, "Test Relationship \" 2"))
                                .to().addType(new TypeData(this, "Test Type \" 1"))
                                .to().addType(new TypeData(this, "Test Type \" 2"))}
        );
    }

    /**
     * Removes the MxUpdate programs, attributes and types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.PRG_MQL_PROGRAM);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_BOOLEAN);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_DATE);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_INTEGER);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_REAL);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE_STRING);
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    /**
     * Creates all depending administration objects for given
     * <code>_relationship</code>.
     *
     * @param _relationship     relationship with depending objects
     * @throws Exception if create failed
     */
    @Override()
    protected void createDependings(final RelationshipData _relationship)
        throws Exception
    {
        // create referenced property value
        for (final PropertyDef prop : _relationship.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }
        // create attributes
        for (final AbstractAttributeData<?> attr : _relationship.getAttributes())  {
            attr.create();
        }
        // create programs
        for (final AbstractDataWithTrigger.AbstractTrigger<?> trig : _relationship.getTriggers())  {
            trig.getProgram().create();
        }
        // create from types
        for (final TypeData type : _relationship.from().getTypes())  {
            type.create();
        }
        // create from relationships
        for (final RelationshipData relationship : _relationship.from().getRelationships())  {
            relationship.create();
        }
        // create to types
        for (final TypeData type : _relationship.to().getTypes())  {
            type.create();
        }
        // create to relationships
        for (final RelationshipData relationship : _relationship.to().getRelationships())  {
            relationship.create();
        }
    }
}
