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

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class is used to test interface exports.
 *
 * @author The MxUpdate Team
 */
public class InterfaceTest
    extends AbstractDataWithAttributesExportUpdateTest<InterfaceData>
{
    /**
     * Creates for given <code>_name</code> a new interface data instance.
     *
     * @param _name     name of the interface data instance
     * @return interface data instance
     */
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
                        "interface without anything",
                        new InterfaceData(this, "TestInterface")},
                new Object[]{
                        "interface with escaped name",
                        new InterfaceData(this, "TestInterface \" 1")},

                new Object[]{
                        "issue #123: interface which is abstract",
                        new InterfaceData(this, "TestInterface")
                                .setValue("abstract", "true")},

                new Object[]{
                        "interface with one single parent interface",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addParent(new InterfaceData(this, "TestInterfaceParent"))},
                new Object[]{
                        "interface with two parent interface",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addParent(new InterfaceData(this, "TestInterface Parent \" 1"))
                                .addParent(new InterfaceData(this, "TestInterface Parent \" 2"))},

                new Object[]{
                        "interface with one type",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addType(new TypeData(this, "TestType \" 1"))},
                new Object[]{
                        "interface with two types",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addType(new TypeData(this, "TestType \" 1"))
                                .addType(new TypeData(this, "TestType \" 2"))},
                new Object[]{
                        "interface with all types",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addAllTypes()},

                new Object[]{
                        "interface with one relationship",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addRelationship(new RelationshipData(this, "TestRelationship \" 1"))},
                new Object[]{
                        "interface with two relationships",
                        new InterfaceData(this, "TestInterface \" 1")
                                .addRelationship(new RelationshipData(this, "TestRelationship \" 1"))
                                .addRelationship(new RelationshipData(this, "TestRelationship \" 2"))},
                new Object[]{
                        "interface with all relationships",
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
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_ATTRIBUTE_STRING);
        this.cleanup(CI.DM_INTERFACE);
        this.cleanup(CI.DM_TYPE);
        this.cleanup(CI.DM_RELATIONSHIP);
    }

    /**
     * {@inheritDoc}
     * The original method is overwritten because the parent interfaces could
     * not be removed.
     */
    @Override()
    protected InterfaceData createCleanNewData(final InterfaceData _original)
    {
        return super.createCleanNewData(_original)
                    .addParent(_original.getParents().toArray(new InterfaceData[_original.getParents().size()]));
    }

    /**
     * Updates an non existing interface with one parent interface.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one parent for non existing interface")
    public void updateOneParent4NonExisting()
        throws Exception
    {
        final InterfaceData parent = new InterfaceData(this, "TestInterfaceParent").create();

        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addParent(parent)
                .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select derived dump"),
                            parent.getName(),
                            "check that only one parent interface is defined");
    }

    /**
     * Updates an non existing interface with two parent interfaces.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with two parents for non existing interface")
    public void updateTwoParent4NonExisting()
        throws Exception
    {
        final InterfaceData parent1 = new InterfaceData(this, "TestInerfaceParent1").create();
        final InterfaceData parent2 = new InterfaceData(this, "TestInerfaceParent2").create();

        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addParent(parent1)
                .addParent(parent2);
        inter.update();

        final Set<String> resultParent = new HashSet<String>();
        resultParent.add(parent1.getName());
        resultParent.add(parent2.getName());
        Assert.assertEquals(this.mqlAsSet("print interface '" + inter.getName() + "' select derived dump '\n'"),
                            resultParent,
                            "check that all parent interfaces are defined");
    }

    /**
     * Check for an interface update within one attribute of an non existing
     * interface.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one attribute for non existing interface")
    public void updateOneAttribute4NonExisting()
        throws Exception
    {
        final AttributeStringData attr = new AttributeStringData(this, "Attribute").create();
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addAttribute(attr)
                .create();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select attribute dump"),
                            attr.getName(),
                            "check that only one attribute is defined");
    }

    /**
     * Check for an interface update with one types of an interface with all
     * assigned types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one type for existing interface with all types")
    public void updateOneType4ExistingAllTypes()
        throws Exception
    {
        final TypeData type = new TypeData(this, "TestType").create();
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addAllTypes()
                .create()
                .removeTypes()
                .addType(type)
                .update();

        Assert.assertEquals(
                this.mql("print interface '" + inter.getName() + "' select type dump"),
                type.getName(),
                "check that only one type is defined");
    }

    /**
     * Check for an interface update with one types of non existing interface.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one type for non existing interface")
    public void updateOneType4NonExisting()
        throws Exception
    {
        final TypeData type = new TypeData(this, "TestType").create();
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addType(type)
                .update();

        Assert.assertEquals(
                this.mql("print interface '" + inter.getName() + "' select type dump"),
                type.getName(),
                "check that only one type is defined");
    }

    /**
     * Check for an interface update with one types of non existing interface.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one type for non existing interface")
    public void updateTwoTypes4NonExisting()
        throws Exception
    {
        final TypeData type1 = new TypeData(this, "TestType1").create();
        final TypeData type2 = new TypeData(this, "TestType2").create();

        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addType(type1)
                .addType(type2)
                .update();

        final Set<String> result = new HashSet<String>();
        result.add(type1.getName());
        result.add(type2.getName());
        Assert.assertEquals(this.mqlAsSet("print interface '" + inter.getName() + "' select type dump '\n'"),
                            result,
                            "check that all types are defined");
    }

    /**
     * Check for an interface update with all types of an interface with one
     * assigned type.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with all types for existing interface with one type")
    public void updateAllTypes4ExistingType()
        throws Exception
    {
        final TypeData type = new TypeData(this, "TestType");
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addType(type)
                .create()
                .addAllTypes()
                .update();

        Assert.assertEquals(
                this.mql("print interface '" + inter.getName() + "' select type dump"),
                "all",
                "check that only all type is defined");
    }

    /**
     * Check for an interface update with one relationships of an interface
     * with all assigned relationships.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one relationship for existing interface with another interface")
    public void updateOneRelationship4ExistingOneRelationships()
        throws Exception
    {
        final RelationshipData rel1 = new RelationshipData(this, "TestRel1").create();
        final RelationshipData rel2 = new RelationshipData(this, "TestRel2").create();
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addRelationship(rel1)
                .create()
                .removeRelationships()
                .addRelationship(rel2)
                .update();

        Assert.assertEquals(
                this.mql("print interface '" + inter.getName() + "' select relationship dump"),
                rel2.getName(),
                "check that only second relationship is defined");
    }

    /**
     * Check for an interface update with one relationship of an non existing
     * interface.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one relationship for non existing interface")
    public void updateOneRelationship4NonExisting()
        throws Exception
    {
        final RelationshipData rel = new RelationshipData(this, "TestRel").create();
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addRelationship(rel)
                .update();

        Assert.assertEquals(
                this.mql("print interface '" + inter.getName() + "' select relationship dump"),
                rel.getName(),
                "check that only one relationship is defined");
    }

    /**
     * Check for an interface update with one relationships of an interface
     * with all assigned relationships.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with one relationship for existing interface with all relationships")
    public void updateOneRelationship4ExistingAllRelationships()
        throws Exception
    {
        final RelationshipData rel = new RelationshipData(this, "TestRel").create();
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addAllRelationships()
                .create()
                .removeRelationships()
                .addRelationship(rel)
                .update();

        Assert.assertEquals(
                this.mql("print interface '" + inter.getName() + "' select relationship dump"),
                rel.getName(),
                "check that only one relationship is defined");
    }

    /**
     * Check for an interface update with all relationships of an interface
     * with one assigned relationship.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with all relationships for existing interface with one relationship")
    public void updateAllRelationships4ExistingRelationship()
        throws Exception
    {
        final RelationshipData rel = new RelationshipData(this, "TestRel");
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addRelationship(rel)
                .create()
                .addAllRelationships()
                .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select relationship dump"),
                            "all",
                            "check that only all relationship is defined");
    }


    /**
     * Check for an interface update with no types and no relationships of an
     * existing interface with all assigned types and all relationships.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with non type / relationship for existing interface "
                            + "with all types / relationships")
    public void updateNon4ExistingAllTypesRelationships()
        throws Exception
    {
        final InterfaceData inter = new InterfaceData(this, "TestInterface")
                .addAllRelationships()
                .addAllTypes()
                .create()
                .removeRelationships()
                .removeTypes()
                .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select type dump"),
                            "",
                            "check that no type is defined");
        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select relationship dump"),
                            "",
                            "check that no relationship is defined");
    }


    /**
     * Update an interface with special characters for all cases.
     *
     * @throws Exception if test failed
     */
    @Test(description = "update interface with special characters")
    public void updateWithSpecialCharacters()
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
                .update();

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

    /**
     * Checks for correct error code if a parent interface must be removed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test for interface update to check if removing of parent does not work")
    public void exceptionUpdateRemovingParent()
        throws Exception
    {
        new InterfaceData(this, "TestInterface")
                .addParent(new InterfaceData(this, "TestInterfaceParent1"))
                .addParent(new InterfaceData(this, "TestInterfaceParent2"))
                .create()
                .removeParents()
                .addParent(new InterfaceData(this, "TestInterfaceParent1"))
                .failureUpdate(UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_REMOVING_PARENT);
    }

    /**
     * Checks for correct error code if wrong interface name is used for
     * calling the procedure 'testParents'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test for interface update with wrong interface name for TCL procedure 'testParents'")
    public void exceptionUpdateWrongName()
        throws Exception
    {
        final InterfaceData inter = new InterfaceData(this, "TestInterface");
        inter.failedUpdateWithCode(
                "mql mod interface \"${NAME}\"\ntestParents -interface " + inter.getName() + "1",
                UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_WRONG_NAME);
    }

    /**
     * Checks for correct error code if wrong parameters are used for calling
     * the procedure 'testParents'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test for interface update with wrong parameters for TCL procedure 'testParents'")
    public void exceptionUpdateWrongParentParameter()
        throws Exception
    {
        new InterfaceData(this, "TestInterface").failedUpdateWithCode(
                "mql mod interface \"${NAME}\"\ntestParents -hallo",
                UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_UKNOWN_PARAMETER);
    }
}
