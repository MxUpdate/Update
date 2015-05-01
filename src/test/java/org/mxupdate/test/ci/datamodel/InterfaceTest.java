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

import java.util.HashSet;
import java.util.Set;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class is used to test interface exports.
 *
 * @author The MxUpdate Team
 */
public class InterfaceTest
    extends AbstractDataExportUpdate<InterfaceData>
{
    @Override()
    protected InterfaceData createNewData(final String _name)
    {
        return new InterfaceData(this, _name);
    }

    /**
     * Data provider for test interfaces.
     *
     * @return object array with all test interfaces
     */
    @IssueLink("123")
    @DataProvider(name = "data")
    public Object[][] dataInterfaces()
    {
        return this.prepareData("interfaces",
                new Object[]{
                        "1) interface without anything (to test required fields)",
                        new InterfaceData(this, "TestInterface")
                                .setValue("description", "")
                                .setFlag("hidden", false),
                        new InterfaceData(this, "TestInterface")},
                new Object[]{
                        "2) interface with escaped name",
                        new InterfaceData(this, "TestInterface \" 1")},

                new Object[]{
                        "3) issue #123: interface which is abstract",
                        new InterfaceData(this, "TestInterface")
                                .setFlag("abstract", true, Create.ViaValue)},

                new Object[]{
                        "4a) interface with one type",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addType(new TypeData(this, "TestType \" 1"))},
                new Object[]{
                        "4b) interface with two types",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addType(new TypeData(this, "TestType \" 1"))
                                .addType(new TypeData(this, "TestType \" 2"))},
                new Object[]{
                        "4c) interface with all types",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addAllTypes()},

                new Object[]{
                        "5a) interface with one relationship",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addRelationship(new RelationshipData(this, "TestRelationship \" 1"))},
                new Object[]{
                        "5b) interface with two relationships",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addRelationship(new RelationshipData(this, "TestRelationship \" 1"))
                                .addRelationship(new RelationshipData(this, "TestRelationship \" 2"))},
                new Object[]{
                        "5c) interface with all relationships",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addAllRelationships()}
        );
    }

    /**
     * Removes the MxUpdate attributes, interfaces, types and relationships.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
//    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_ATTRIBUTE_STRING);
        this.cleanup(CI.DM_INTERFACE);
        this.cleanup(CI.DM_TYPE);
        this.cleanup(CI.DM_RELATIONSHIP);
    }

    /**
     * Positive test with one attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with one attribute")
    public void t6a_positiveTestWithAttribute()
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
    public void t6b_positiveTestAttributeAdded()
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
    public void t6c_negativeTestAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addAttribute(new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .failureUpdate(UpdateException_mxJPO.ErrorKey.DM_INTERFACE_REMOVE_ATTRIBUTE);
    }

    /**
     * Positive test if an ignored attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an ignored attribute is removed")
    public void t6d_positiveTestIgnoredAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addAttribute(new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMInterfaceAttrIgnore.name(), "*");
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
    public void t6e_positiveTestAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addAttribute(new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMInterfaceAttrRemove.name(), "*")
                .checkExport();
    }

    /**
     * Positive test with one parent.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with one parent")
    public void t7a_positiveTestWithParent()
        throws Exception
    {
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "Test Parent"))
                .create()
                .checkExport()
                .update("")
                .checkExport();
    }

    /**
     * Positive test with add of a parent.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with add of a parent")
    public void t7b_positiveTestParentAdded()
        throws Exception
    {
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "Test Parent 1"))
                .create()
                .addParent(new InterfaceData(this, "Test Parent 2"))
                .createDependings()
                .update("")
                .checkExport();
    }

    /**
     * Checks for correct error code if a parent interface must be removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test for interface update to check if removing of parent does not work")
    public void t7c_negativeTestParentRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "TestParent"))
                .create();
        this.createNewData("Test")
                .failureUpdate(UpdateException_mxJPO.ErrorKey.DM_INTERFACE_REMOVE_PARENT);
    }

    /**
     * Positive test if an ignored parent is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an ignored parent is removed")
    public void t7d_positiveTestIgnoredParentRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "TestParent"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMInterfaceParentIgnore.name(), "*");
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "TestParent"))
                .checkExport();
    }

    /**
     * Positive test if an parent is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an parent is removed")
    public void t7e_positiveTestParentRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "TestParent1"))
                .addParent(new InterfaceData(this, "TestParent2"))
                .create();
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "TestParent1"))
                .update("", ValueKeys.DMInterfaceParentRemove.name(), "*")
                .checkExport();
    }

    /**
     * Positive test if all parents are removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if all parents are removed")
    public void t7f_positiveTestAllParentsRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .addParent(new InterfaceData(this, "TestParent"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMInterfaceParentRemove.name(), "*")
                .checkExport();
    }

    /**
     * Update an interface with special characters for all cases.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with special characters")
    public void t8_updateWithSpecialCharacters()
        throws Exception
    {
        final InterfaceData parent1 = new InterfaceData(this, "TestInterfaceParent \" 1").create();
        final InterfaceData parent2 = new InterfaceData(this, "TestInterfaceParent \" 2").create();
        final AttributeStringData attr1 = new AttributeStringData(this, "Attribute \" 1").create();
        final AttributeStringData attr2 = new AttributeStringData(this, "Attribute \" 2").create();
        final TypeData type1 = new TypeData(this, "TestType \" 1").create();
        final TypeData type2 = new TypeData(this, "TestType \" 2").create();
        final RelationshipData rel1 = new RelationshipData(this, "TestRel \" 1").create();
        final RelationshipData rel2 = new RelationshipData(this, "TestRel \" 2").create();

        final InterfaceData inter = new InterfaceData(this, "TestInterface \"")
                .addParent(parent1)
                .addParent(parent2)
                .addAttribute(attr1)
                .addAttribute(attr2)
                .addType(type1)
                .addType(type2)
                .addRelationship(rel1)
                .addRelationship(rel2)
                .update((String) null);

        final Set<String> resultParent = new HashSet<String>();
        resultParent.add(parent1.getName());
        resultParent.add(parent2.getName());
        Assert.assertEquals(
                this.mqlAsSet("print interface '" + inter.getName() + "' select derived dump '\n'"),
                resultParent,
                "check that all parent interfaces are defined");

        final Set<String> resultAttrs = new HashSet<String>();
        resultAttrs.add(attr1.getName());
        resultAttrs.add(attr2.getName());
        Assert.assertEquals(
                this.mqlAsSet("escape print interface \""
                                    + AbstractTest.convertMql(inter.getName()) + "\" select attribute dump '\n'"),
                resultAttrs,
                "check that all types are defined");

        final Set<String> resultTypes = new HashSet<String>();
        resultTypes.add(type1.getName());
        resultTypes.add(type2.getName());
        Assert.assertEquals(
                this.mqlAsSet("escape print interface \""
                                    + AbstractTest.convertMql(inter.getName()) + "\" select type dump '\n'"),
                resultTypes,
                "check that all types are defined");

        final Set<String> resultRels = new HashSet<String>();
        resultRels.add(rel1.getName());
        resultRels.add(rel2.getName());
        Assert.assertEquals(
                this.mqlAsSet("escape print interface \""
                                    + AbstractTest.convertMql(inter.getName()) + "\" select relationship dump '\n'"),
                resultRels,
                "check that all relationships are defined");
    }
}
