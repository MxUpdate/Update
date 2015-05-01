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

package org.mxupdate.test.test.update.datamodel.relationshipci;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Relationship_mxJPO relationship CI} export / update.
 *
 * @author The MxUpdate Team
 */
public class RelationshipCI_3UpdateTest
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
                        new RelationshipData(this, "TestRelationship \" 1").setFlag("preventduplicates", false)},
                new Object[]{
                        "relationship with triggers (to test escaped triggers)",
                        new RelationshipData(this, "TestRelationship \" 1").addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test Program")))},
                new Object[]{
                        "relationship with one rule (multiple are not working!)",
                        new RelationshipData(this, "TestRelationship").setRule(new RuleData(this, "Rule"))},
               new Object[]{
                       "relationship abstract true",
                       new RelationshipData(this, "TestRelationship").setFlag("abstract", true, Create.ViaValue).defNotSupported(Version.V6R2011x, Version.V6R2012x)},
               new Object[]{
                       "relationship abstract false",
                       new RelationshipData(this, "TestRelationship").setFlag("abstract", false, Create.ViaValue).defNotSupported(Version.V6R2011x, Version.V6R2012x),
                       new RelationshipData(this, "TestRelationship")},
                // from side
                new Object[]{
                        "relationship without from propagate connection",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship")
                                .from().defFlag("propagateconnection", false)},
                new Object[]{
                        "relationship with from propagate connection true",
                        new RelationshipData(this, "TestRelationship")
                                .from().defFlag("propagateconnection", true)},
                new Object[]{
                        "relationship with from propagate connection false",
                        new RelationshipData(this, "TestRelationship")
                                .from().defFlag("propagateconnection", false)},
                new Object[]{
                        "relationship without from propagate modify",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship")
                                .from().defFlag("propagatemodify", false)},
                new Object[]{
                        "relationship with from propagate modify true",
                        new RelationshipData(this, "TestRelationship").from().defFlag("propagatemodify", true)},
                new Object[]{
                        "relationship with from propagate modify false",
                        new RelationshipData(this, "TestRelationship").from().defFlag("propagatemodify", false)},

                new Object[]{
                        "relationship with escaped from meaning",
                        new RelationshipData(this, "TestRelationship").from().defString("meaning", "this is a \"test\"")},

                new Object[]{
                        "relationship without from cardinality",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").from().defSingle("cardinality", "many")},
                new Object[]{
                        "relationship with from cardinality one",
                        new RelationshipData(this, "TestRelationship").from().defSingle("cardinality", "one")},
                new Object[]{
                        "relationship with from cardinality many",
                        new RelationshipData(this, "TestRelationship").from().defSingle("cardinality", "many")},

                new Object[]{
                        "relationship without from clone behavior",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").from().defSingle("clone", "none")},
                new Object[]{
                        "relationship with from clone behavior none",
                        new RelationshipData(this, "TestRelationship").from().defSingle("clone", "none")},
                new Object[]{
                        "relationship with from clone behavior float",
                        new RelationshipData(this, "TestRelationship").from().defSingle("clone", "float")},
                new Object[]{
                        "relationship with from clone behavior replicate",
                        new RelationshipData(this, "TestRelationship").from().defSingle("clone", "replicate")},

                new Object[]{
                        "relationship without from revision behavior",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").from().defSingle("revision", "none")},
                new Object[]{
                        "relationship with from revision behavior none",
                        new RelationshipData(this, "TestRelationship").from().defSingle("revision", "none")},
                new Object[]{
                        "relationship with from revision behavior float",
                        new RelationshipData(this, "TestRelationship").from().defSingle("revision", "float")},
                new Object[]{
                        "relationship with from revision behavior replicate",
                        new RelationshipData(this, "TestRelationship").from().defSingle("revision", "replicate")},

                new Object[]{
                        "relationship with one from type",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defData("type", new TypeData(this, "Test Type"))},
                new Object[]{
                        "relationship with two escaped from types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defData("type", new TypeData(this, "Test Type \" 1"))
                                .from().defData("type", new TypeData(this, "Test Type \" 2"))},
                new Object[]{
                        "relationship with all from types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defDataAll("type")},
                new Object[]{
                        "relationship with one from relationship",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defData("relationship", new RelationshipData(this, "Test Relationship"))},
                new Object[]{
                        "relationship with two escaped from relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defData("relationship", new RelationshipData(this, "Test Relationship \" 1"))
                                .from().defData("relationship", new RelationshipData(this, "Test Relationship \" 2"))},
                new Object[]{
                        "relationship with all from relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defDataAll("relationship")},
                new Object[]{
                        "relationship with two escaped from types and relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .from().defData("relationship", new RelationshipData(this, "Test Relationship \" 1"))
                                .from().defData("relationship", new RelationshipData(this, "Test Relationship \" 2"))
                                .from().defData("type", new TypeData(this, "Test Type \" 1"))
                                .from().defData("type", new TypeData(this, "Test Type \" 2"))},
                // to side
                new Object[]{
                        "relationship without to propagate connection",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").to().defFlag("propagateconnection", false)},
                new Object[]{
                        "relationship with to propagate connection true",
                        new RelationshipData(this, "TestRelationship").to().defFlag("propagateconnection", true)},
                new Object[]{
                        "relationship with to propagate connection false",
                        new RelationshipData(this, "TestRelationship").to().defFlag("propagateconnection", false)},
                new Object[]{
                        "relationship without to propagate modify",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").to().defFlag("propagatemodify", false)},
                new Object[]{
                        "relationship with to propagate modify true",
                        new RelationshipData(this, "TestRelationship").to().defFlag("propagatemodify", true)},
                new Object[]{
                        "relationship with to propagate modify false",
                        new RelationshipData(this, "TestRelationship").to().defFlag("propagatemodify", false)},

                new Object[]{
                        "relationship with escaped meaning",
                        new RelationshipData(this, "TestRelationship").to().defString("meaning", "this is a \"test\"")},

                new Object[]{
                        "relationship without cardinality",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").to().defSingle("cardinality", "many")},
                new Object[]{
                        "relationship with cardinality one",
                        new RelationshipData(this, "TestRelationship").to().defSingle("cardinality", "one")},
                new Object[]{
                        "relationship with cardinality many",
                        new RelationshipData(this, "TestRelationship").to().defSingle("cardinality", "many")},

                new Object[]{
                        "relationship without clone behavior",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").to().defSingle("clone", "none")},
                new Object[]{
                        "relationship with clone behavior none",
                        new RelationshipData(this, "TestRelationship").to().defSingle("clone", "none")},
                new Object[]{
                        "relationship with clone behavior float",
                        new RelationshipData(this, "TestRelationship").to().defSingle("clone", "float")},
                new Object[]{
                        "relationship with clone behavior replicate",
                        new RelationshipData(this, "TestRelationship").to().defSingle("clone", "replicate")},

                new Object[]{
                        "relationship without to revision behavior",
                        new RelationshipData(this, "TestRelationship"),
                        new RelationshipData(this, "TestRelationship").to().defSingle("revision", "none")},
                new Object[]{
                        "relationship with to revision behavior none",
                        new RelationshipData(this, "TestRelationship").to().defSingle("revision", "none")},
                new Object[]{
                        "relationship with to revision behavior float",
                        new RelationshipData(this, "TestRelationship").to().defSingle("revision", "float")},
                new Object[]{
                        "relationship with to revision behavior replicate",
                        new RelationshipData(this, "TestRelationship").to().defSingle("revision", "replicate")},

                new Object[]{
                        "relationship with one to type",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defData("type", new TypeData(this, "Test Type"))},
                new Object[]{
                        "relationship with two escaped to types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defData("type", new TypeData(this, "Test Type \" 1"))
                                .to().defData("type", new TypeData(this, "Test Type \" 2"))},
                new Object[]{
                        "relationship with all to types",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defDataAll("type")},
                new Object[]{
                        "relationship with one to relationship",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defData("relationship", new RelationshipData(this, "Test Relationship"))},
                new Object[]{
                        "relationship with two escaped to relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defData("relationship", new RelationshipData(this, "Test Relationship \" 1"))
                                .to().defData("relationship", new RelationshipData(this, "Test Relationship \" 2"))},
                new Object[]{
                        "relationship with all to relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defDataAll("relationship")},
                new Object[]{
                        "relationship with two escaped to types and relationships",
                        new RelationshipData(this, "TestRelationship \" 1")
                                .to().defData("relationship", new RelationshipData(this, "Test Relationship \" 1"))
                                .to().defData("relationship", new RelationshipData(this, "Test Relationship \" 2"))
                                .to().defData("type", new TypeData(this, "Test Type \" 1"))
                                .to().defData("type", new TypeData(this, "Test Type \" 2"))}
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
                .from().defData("type", new TypeData(this, "Test1"))
                .from().defData("type", new TypeData(this, "Test2"))
                .create();

        new RelationshipData(this, "Test")
                .from().defDataAll("type")
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
                .from().defDataAll("type")
                .create();

        new RelationshipData(this, "Test")
                .from().defData("type", new TypeData(this, "Test1"))
                .from().defData("type", new TypeData(this, "Test2"))
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
                .from().defData("relationship", new RelationshipData(this, "Test1"))
                .from().defData("relationship", new RelationshipData(this, "Test2"))
                .create();

        new RelationshipData(this, "Test")
                .from().defDataAll("relationship")
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
                .from().defDataAll("relationship")
                .create();

        new RelationshipData(this, "Test")
                .from().defData("relationship", new RelationshipData(this, "Test1"))
                .from().defData("relationship", new RelationshipData(this, "Test2"))
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
    public void t20a_positiveTestWithAttribute()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
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
    public void t20b_positiveTestAttributeAdded()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute 1"))
                .create()
                .defData("attribute", new AttributeStringData(this, "Test Attribute 2"))
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
    public void t20c_negativeTestAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .failureUpdate(ErrorKey.DM_RELATION_REMOVE_ATTRIBUTE);
    }

    /**
     * Positive test if an ignored attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an ignored attribute is removed")
    public void t20d_positiveTestIgnoredAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMRelationAttrIgnore.name(), "*");
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .checkExport();
    }

    /**
     * Positive test if an attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an attribute is removed")
    public void t20e_positiveTestAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMRelationAttrRemove.name(), "*")
                .checkExport();
    }

    /**
     * Positive test for kind compositional.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for kind compositional")
    public void t21a_positiveTestKindCompositional()
        throws Exception
    {
        this.createNewData("Test")
                .setFlag("preventduplicates", true)
                .from().defSingle("cardinality", "one")
                .from().defFlag("propagateconnection", false)
                .to().defSingle("clone", "replicate")
                .to().defSingle("revision", "replicate")
                .to().defFlag("propagateconnection", false)
                .create()
                .setSingle("kind", "compositional")
                .update("")
                .checkExport();
    }

    /**
     * Negative test if the kind is changed back to basic.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if the kind is changed back to basic")
    public void t21b_negativeTestChangeKindBackToBasic()
        throws Exception
    {
        this.createNewData("Test")
                .setFlag("preventduplicates", true)
                .from().defSingle("cardinality", "one")
                .from().defFlag("propagateconnection", false)
                .to().defSingle("clone", "replicate")
                .to().defSingle("revision", "replicate")
                .to().defFlag("propagateconnection", false)
                .create();
        this.createNewData("Test")
                .setFlag("preventduplicates", true)
                .from().defSingle("cardinality", "one")
                .from().defFlag("propagateconnection", false)
                .to().defSingle("clone", "replicate")
                .to().defSingle("revision", "replicate")
                .to().defFlag("propagateconnection", false)
                .setSingle("kind", "compositional")
                .update("")
                .checkExport();
        this.createNewData("Test")
                .setFlag("preventduplicates", true)
                .from().defSingle("cardinality", "one")
                .from().defFlag("propagateconnection", false)
                .to().defSingle("clone", "replicate")
                .to().defSingle("revision", "replicate")
                .to().defFlag("propagateconnection", false)
                .setSingle("kind", "basic")
                .failureUpdate(ErrorKey.DM_RELATION_NOT_BASIC_KIND);
        this.createNewData("Test")
                .setFlag("preventduplicates", true)
                .from().defSingle("cardinality", "one")
                .from().defFlag("propagateconnection", false)
                .to().defSingle("clone", "replicate")
                .to().defSingle("revision", "replicate")
                .to().defFlag("propagateconnection", false)
                .setSingle("kind", "compositional")
                .checkExport();
    }

    /**
     * Positive test that a derived relationship is defined.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that a derived relationship is defined")
    public void t22a_positiveTestDerived()
        throws Exception
    {
        this.createNewData("Parent")
                .create();
        this.createNewData("Test")
                .create()
                .update("")
                .setValue("derived", AbstractTest.PREFIX + "Parent")
                .update("")
                .checkExport();
    }

    /**
     * Negative test that an existing derived relationship is updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that derived relationship is changed")
    public void t22b_negativeTestDerivedChanged()
        throws Exception
    {
        this.createNewData("Parent1")
                .create();
        this.createNewData("Parent2")
                .create();
        this.createNewData("Test")
                .create()
                .setValue("derived", AbstractTest.PREFIX + "Parent1")
                .update("");
        this.createNewData("Test")
                .setValue("derived", AbstractTest.PREFIX + "Parent2")
                .failureUpdate(ErrorKey.DM_RELATION_UPDATE_DERIVED);
        this.createNewData("Test")
                .setValue("derived", AbstractTest.PREFIX + "Parent1")
                .checkExport();
    }

    /**
     * Removes the MxUpdate programs, attributes and types.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.PRG_MQL);
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
