/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.RelationshipData.Behavior;
import org.mxupdate.test.data.datamodel.RelationshipData.Cardinality;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class is used to test relationship updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class RelationshipTest
    extends AbstractDataExportUpdate<RelationshipData>
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
                        "relationship without defined preventduplicates flag (to test default value)",
                        new RelationshipData(this, "TestRelationship \" 1"),
                        new RelationshipData(this, "TestRelationship \" 1")
                                .setFlag("preventduplicates", false)},
                new Object[]{
                        "relationship with triggers (to test escaped triggers)",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test Program")))},
                new Object[]{
                        "relationship with one rule (multiple are not working!)",
                        new RelationshipData(this, "TestRelationship")
                                .setRule(new RuleData(this, "Rule"))},
                // from side
                new Object[]{
                        "relationship without from propagate connection",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship")
                                .from().setFlag("propagateconnection", false)},
                new Object[]{
                        "relationship with from propagate connection true",
                        new RelationshipData(this, "TestRelationship")
                                .from().setFlag("propagateconnection", true)},
                new Object[]{
                        "relationship with from propagate connection false",
                        new RelationshipData(this, "TestRelationship")
                                .from().setFlag("propagateconnection", false)},
                new Object[]{
                        "relationship without from propagate modify",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship")
                                .from().setFlag("propagatemodify", false)},
                new Object[]{
                        "relationship with from propagate modify true",
                        new RelationshipData(this, "TestRelationship")
                                .from().setFlag("propagatemodify", true)},
                new Object[]{
                        "relationship with from propagate modify false",
                        new RelationshipData(this, "TestRelationship")
                                .from().setFlag("propagatemodify", false)},

                new Object[]{
                        "relationship with escaped from meaning",
                        new RelationshipData(this, "TestRelationship")
                                .from().setMeaning("this is a \"test\"")},

                new Object[]{
                        "relationship without from cardinality",
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(null),
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(Cardinality.MANY)},
                new Object[]{
                        "relationship with from cardinality one",
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(Cardinality.ONE)},
                new Object[]{
                        "relationship with from cardinality many",
                        new RelationshipData(this, "TestRelationship")
                                .from().setCardinality(Cardinality.MANY)},

                new Object[]{
                        "relationship without from clone behavior",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(null),
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(Behavior.NONE)},
                new Object[]{
                        "relationship with from clone behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(Behavior.NONE)},
                new Object[]{
                        "relationship with from clone behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(Behavior.FLOAT)},
                new Object[]{
                        "relationship with from clone behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .from().setClone(Behavior.REPLICATE)},

                new Object[]{
                        "relationship without from revision behavior",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(null),
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(Behavior.NONE)},
                new Object[]{
                        "relationship with from revision behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(Behavior.NONE)},
                new Object[]{
                        "relationship with from revision behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(Behavior.FLOAT)},
                new Object[]{
                        "relationship with from revision behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .from().setRevision(Behavior.REPLICATE)},

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
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship")
                                .to().setFlag("propagateconnection", false)},
                new Object[]{
                        "relationship with to propagate connection true",
                        new RelationshipData(this, "TestRelationship")
                                .to().setFlag("propagateconnection", true)},
                new Object[]{
                        "relationship with to propagate connection false",
                        new RelationshipData(this, "TestRelationship")
                                .to().setFlag("propagateconnection", false)},
                new Object[]{
                        "relationship without to propagate modify",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship")
                                .to().setFlag("propagatemodify", false)},
                new Object[]{
                        "relationship with to propagate modify true",
                        new RelationshipData(this, "TestRelationship")
                                .to().setFlag("propagatemodify", true)},
                new Object[]{
                        "relationship with to propagate modify false",
                        new RelationshipData(this, "TestRelationship")
                                .to().setFlag("propagatemodify", false)},

                new Object[]{
                        "relationship with escaped meaning",
                        new RelationshipData(this, "TestRelationship")
                                .to().setMeaning("this is a \"test\"")},

                new Object[]{
                        "relationship without cardinality",
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(null),
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(Cardinality.MANY)},
                new Object[]{
                        "relationship with cardinality one",
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(Cardinality.ONE)},
                new Object[]{
                        "relationship with cardinality many",
                        new RelationshipData(this, "TestRelationship")
                                .to().setCardinality(Cardinality.MANY)},

                new Object[]{
                        "relationship without clone behavior",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(null),
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(Behavior.NONE)},
                new Object[]{
                        "relationship with clone behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(Behavior.NONE)},
                new Object[]{
                        "relationship with clone behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(Behavior.FLOAT)},
                new Object[]{
                        "relationship with clone behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .to().setClone(Behavior.REPLICATE)},

                new Object[]{
                        "relationship without to revision behavior",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(null),
                        new RelationshipData(this, "TestRelationship")
                                 .to().setRevision(Behavior.NONE)},
                new Object[]{
                        "relationship with to revision behavior none",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(Behavior.NONE)},
                new Object[]{
                        "relationship with to revision behavior float",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(Behavior.FLOAT)},
                new Object[]{
                        "relationship with to revision behavior replicate",
                        new RelationshipData(this, "TestRelationship")
                                .to().setRevision(Behavior.REPLICATE)},

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
     * Positive test to change from multiple types to all types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change from multiple types to all types")
    public void positiveTestChangeToTypeAll()
        throws Exception
    {
        new RelationshipData(this, "Test")
                .from().addType(new TypeData(this, "Test1"))
                .from().addType(new TypeData(this, "Test2"))
                .create();

        new RelationshipData(this, "Test")
                .from().addAllTypes()
                .update("")
                .checkExport();
    }

    /**
     * Positive test to change from all types to multiple types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change from all types to multiple types")
    public void positiveTestChangeFromTypeAll()
        throws Exception
    {
        new RelationshipData(this, "Test")
                .from().addAllTypes()
                .create();

        new RelationshipData(this, "Test")
                .from().addType(new TypeData(this, "Test1"))
                .from().addType(new TypeData(this, "Test2"))
                .createDependings()
                .update("")
                .checkExport();
    }

    /**
     * Positive test to change from multiple relationships to all relationships.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change from multiple types to all types")
    public void positiveTestChangeToRelationshipAll()
        throws Exception
    {
        new RelationshipData(this, "Test")
                .from().addRelationship(new RelationshipData(this, "Test1"))
                .from().addRelationship(new RelationshipData(this, "Test2"))
                .create();

        new RelationshipData(this, "Test")
                .from().addAllTypes()
                .update("")
                .checkExport();
    }

    /**
     * Positive test to change from all relationships to multiple relationships.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test to change from all relationships to multiple relationships")
    public void positiveTestChangeFromRelationshipAll()
        throws Exception
    {
        new RelationshipData(this, "Test")
                .from().addAllRelationships()
                .create();

        new RelationshipData(this, "Test")
                .from().addRelationship(new RelationshipData(this, "Test1"))
                .from().addRelationship(new RelationshipData(this, "Test2"))
                .createDependings()
                .update("")
                .checkExport();
    }

    /**
     * Positive test with one attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with one attribute")
    public void positiveTestWithAttribute()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .create()
            .checkExport()
            .update("")
            .checkExport();
    }

    /**
     * Positive test with add of an attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with add of an attribute")
    public void positiveTestAttributeAdded()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute 1"))
            .create()
            .addAttribute(new AttributeStringData(this, "Test Attribute 2"))
            .createDependings()
            .update("")
            .checkExport();
    }

    /**
     * Negative test if an attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if an attribute is removed")
    public void negativeTestAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .create();
        this.createNewData("Test")
            .failureUpdate(UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_ATTRIBUTE_REMOVED);
    }

    /**
     * Positive test if an ignored attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an ignored attribute is removed")
    public void positiveTestIgnoredAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .create();
        this.createNewData("Test")
            .update("", ValueKeys.DMRelationAttrIgnore.name(), "*");
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .checkExport();
    }

    /**
     * Positive test if an attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an attribute is removed")
    public void positiveTestAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .create();
        this.createNewData("Test")
            .update("", ValueKeys.DMRelationAttrRemove.name(), "*")
            .checkExport();
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
        this.cleanup(AbstractTest.CI.DM_RULE);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}
