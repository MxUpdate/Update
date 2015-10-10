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

package org.mxupdate.test.test.update.datamodel.typeci;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Type_mxJPO type CI} export / update.
 *
 * @author The MxUpdate Team
 */
public class TypeCI_3UpdateTest
    extends AbstractDataExportUpdate<TypeData>
{
    /**
     * Data provider for test types.
     *
     * @return object array with all test types
     */
    @IssueLink({"36", "122" })
    @DataProvider(name = "data")
    public Object[][] dataTypes()
    {
        return this.prepareData("types",
                new Object[]{
                        "0a) type without anything",
                        new TypeData(this, "TestType")},
                new Object[]{
                        "0b) type without anything (to test required fields)",
                        new TypeData(this, "TestType")
                                .setValue("description", "")
                                .setFlag("hidden", false),
                        new TypeData(this, "TestType")},
                new Object[]{
                        "0c) type with escaped name",
                        new TypeData(this, "TestType \" 1")},
                // description
                new Object[]{
                        "2) package with description",
                        this.createNewData("Test")
                                .setValue("description", "ABC { } \"")},
                // abstract
                new Object[]{
                        "3a) issue #122: type which is abstract",
                        new TypeData(this, "TestType").setFlag("abstract", true, Create.ViaValue)},
                new Object[]{
                        "3b) issue #122: type which is not abstract (to test default value)",
                        new TypeData(this, "TestType").setFlag("abstract", false, Create.ViaValue),
                        new TypeData(this, "TestType")},
                // trigger
                new Object[]{
                        "4a) type with trigger program",
                        new TypeData(this, "TestType \" 1")
                                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modifyattribute", new MQLProgramData(this, "Test Program")))},
                new Object[]{
                        "4b) type with two trigger program",
                        new TypeData(this, "TestType \" 1")
                                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modifyattribute", new MQLProgramData(this, "Test Program 1")))
                                .addTrigger(new AbstractDataWithTrigger.TriggerCheck( "modifyattribute", new MQLProgramData(this, "Test Program 2"))) },
                // method
                new Object[]{
                        "5a) issue #36: type with one MQL program as method",
                        new TypeData(this, "TestType \" 1")
                                .defData("method", new MQLProgramData(this, "Test Program"))},
                new Object[]{
                        "5b) issue #36: type with two MQL programs as method",
                        new TypeData(this, "TestType \" 1")
                                .defData("method", new MQLProgramData(this, "Test Program 1"))
                                .defData("method", new MQLProgramData(this, "Test Program 2"))}
        );
    }

    /**
     * Test update for existing type with no method and as target no method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with no method for existing type with no method")
    public void t4c_positiveTestUpdateNoMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final TypeData type = new TypeData(this, "TestType")
                .create()
                .update((String) null);

        Assert.assertEquals(this.mql("print type '" + type.getName() + "' select method dump"),
                            "",
                            "check that no method is defined");
    }

    /**
     * Test for an update for an existing type with one method, but after the
     * update without method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with no method for existing type with one method")
    public void t4d_positiveTestUpdateNoMethod4ExistingTypeWithOneMethod()
        throws Exception
    {
        final MQLProgramData method = new MQLProgramData(this, "TestProd1").create();
        final TypeData type = new TypeData(this, "TestType").create();

        this.mql("mod type \"" + AbstractTest.convertMql(type.getName())
                + "\" add method \"" + AbstractTest.convertMql(method.getName()) + "\"");

        type.update((String) null);

        Assert.assertEquals(
                this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
                "",
                "check that no method is defined");
    }

    /**
     * Test an update for an existing type with no method, but after the update
     * with one method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with method for existing type with no method")
    public void t4e_positiveTestUpdateOneMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final MQLProgramData method = new MQLProgramData(this, "TestProd1").create();
        final TypeData type = new TypeData(this, "TestType").create();

        // method must be defined after create (to test the update..)
        type.defData("method", method)
                .update((String) null);

        Assert.assertEquals(this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
                            AbstractTest.convertMql(method.getName()),
                            "check that one method is defined");
    }

    /**
     * Positive test with one global attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with one global attribute")
    public void t5a_positiveTestWithGlobalAttribute()
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
     * Positive test with add of an global attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test with add of an global attribute")
    public void t5b_positiveTestGlobalAttributeAdded()
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
     * Negative test if an global attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if an global attribute is removed")
    public void t5c_negativeTestGlobalAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .failureUpdate(ErrorKey.DM_TYPE_REMOVE_GLOBAL_ATTRIBUTE);
    }

    /**
     * Positive test if an ignored global attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an ignored global attribute is removed")
    public void t5d_positiveTestIgnoredGlobalAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMTypeAttrIgnore.name(), "*");
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .checkExport();
    }

    /**
     * Positive test if an global attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test if an global attribute is removed")
    public void t5e_positiveTestGlobalAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
                .defData("attribute", new AttributeStringData(this, "Test Attribute"))
                .create();
        this.createNewData("Test")
                .update("", ValueKeys.DMTypeAttrRemove.name(), "*")
                .checkExport();
    }

    /**
     * Negative test if an local attribute is removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if an local attribute is removed")
    public void t6_negativeTestLocalAttributesRemoved()
        throws Exception
    {
        final TypeData typeDef = this.createNewData("Test").create();
        this.mql("escape add attribute \"MXUPDATE_Test\" type string owner type \"" + AbstractTest.convertMql(typeDef.getName()) + "\"");
        typeDef.failureUpdate(ErrorKey.DM_TYPE_REMOVE_LOCAL_ATTRIBUTE);
    }

    /**
     * Positive test that a derived type is defined.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test that a derived type is defined")
    public void t7a_positiveTestDerived()
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
     * Negative test that an existing derived type is updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that derived type is changed")
    public void t7b_negativeTestDerivedChanged()
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
                .failureUpdate(ErrorKey.DM_TYPE_UPDATE_DERIVED);
        this.createNewData("Test")
                .setValue("derived", AbstractTest.PREFIX + "Parent1")
                .checkExport();
    }

    /**
     * Positive test for kind composed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "positive test for kind composed")
    public void t8a_positiveTestKindComposed()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "composed")
                .update("")
                .checkExport();
    }

    /**
     * Negative test if the kind is changed back to basic.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if the kind is changed back to basic")
    public void t8b_negativeTestChangeKindBackToBasic()
        throws Exception
    {
        this.createNewData("Test")
                .create()
                .setSingle("kind", "composed")
                .update("")
                .checkExport();
        this.createNewData("Test")
                .setSingle("kind", "basic")
                .failureUpdate(ErrorKey.DM_TYPE_NOT_BASIC_KIND);
        this.createNewData("Test")
                .setSingle("kind", "composed")
                .checkExport();
    }

    @Override()
    protected TypeData createNewData(final String _name)
    {
        return new TypeData(this, _name);
    }

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
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }
}
