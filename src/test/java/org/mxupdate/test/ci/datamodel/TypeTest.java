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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.util.IssueLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class is used to test type updates.
 *
 * @author The MxUpdate Team
 */
public class TypeTest
    extends AbstractDataWithAttributesExportUpdateTest<TypeData>
{
    /**
     * Creates for given <code>_name</code> a new type data instance.
     *
     * @param _name     name of the type data instance
     * @return type data instance
     */
    @Override()
    protected TypeData createNewData(final String _name)
    {
        return new TypeData(this, _name);
    }

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
                        "type without anything",
                        new TypeData(this, "TestType")},
                new Object[]{
                        "type with escaped name",
                        new TypeData(this, "TestType \" 1")},
                new Object[]{
                        "issue #122: type which is abstract",
                        new TypeData(this, "TestType")
                                .setValue("abstract", "true")},
                new Object[]{
                        "type with two escaped attributes and trigger program",
                        new TypeData(this, "TestType \" 1")
                                .addAttribute(new AttributeStringData(this, "String Attribute \" ' Hallo"))
                                .addAttribute(new AttributeStringData(this, "String Attribute \" { Hallo"))
                                .addTrigger(new AbstractDataWithTrigger.TriggerAction("modify", new MQLProgramData(this, "Test Program")))},
                new Object[]{
                        "issue #36: type with one MQL program as method",
                        new TypeData(this, "TestType \" 1")
                                .addMethod(new MQLProgramData(this, "Test Program"))},
                new Object[]{
                        "issue #36: type with two MQL programs as method",
                        new TypeData(this, "TestType \" 1")
                                .addMethod(new MQLProgramData(this, "Test Program 1"))
                                .addMethod(new MQLProgramData(this, "Test Program 2"))}
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
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    /**
     * Test update for existing type with no method and as target no method.
     *
     * @throws Exception if test failed
     */
    @IssueLink("36")
    @Test(description = "update type with no method for existing type with no method")
    public void updateNoMethod4ExistingTypeWithNoMethod()
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
    public void updateNoMethod4ExistingTypeWithOneMethod()
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
    public void updateOneMethod4ExistingTypeWithNoMethod()
        throws Exception
    {
        final MQLProgramData method = new MQLProgramData(this, "TestProd1").create();
        final TypeData type = new TypeData(this, "TestType").create();

        // method must be defined after create (to test the update..)
        type.addMethod(method)
                .update((String) null);

        Assert.assertEquals(this.mql("print type '" + AbstractTest.convertMql(type.getName()) + "' select method dump"),
                            AbstractTest.convertMql(method.getName()),
                            "check that one method is defined");
    }
}
