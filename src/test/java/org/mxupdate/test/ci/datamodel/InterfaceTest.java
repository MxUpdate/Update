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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.DataCollection;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.test.data.datamodel.InterfaceData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class is used to test interface exports.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class InterfaceTest
    extends AbstractTest
{

    /**
     * Makes an update for given administration <code>_object</code>
     * definition.
     *
     * @param _object       object if the update definition
     * @param _errorCode    expected error code
     * @throws Exception if update with failure failed
     */
    @Deprecated()
    private void updateFailure(final AbstractAdminData<?> _object,
                               final UpdateException_mxJPO.Error _errorCode)
        throws Exception
    {
        this.updateFailure(_object.getCIFileName(), _object.ciFile(), _errorCode);
    }

    /**
     * Makes an update for given <code>_fileName</code> and <code>_code</code>.
     *
     * @param _fileName     name of the file to update
     * @param _code         TCL update code
     * @param _errorCode    expected error code
     * @throws Exception if update with failure failed
     */
    @Deprecated()
    private void updateFailure(final String _fileName,
                               final String _code,
                               final UpdateException_mxJPO.Error _errorCode)
        throws Exception
    {
        final Map<?,?> bck = this.update(_fileName, _code);
        final Exception ex = (Exception) bck.get("exception");
         Assert.assertNotNull(ex, "check that action is not allowed");
         Assert.assertTrue(ex.getMessage().indexOf("UpdateError #" + _errorCode.getCode() + ":") >= 0,
                           "check for correct error code #" + _errorCode.getCode());
    }

    /**
     * Makes an update for given <code>_code</code>.
     *
     * @param _fileName     name of the file to update
     * @param _code         TCL update code
     * @param _params       parameters
     * @return values from the called dispatcher
     * @throws Exception  if update failed
     */
    @Deprecated()
    private Map<?,?> update(final String _fileName,
                            final String _code,
                            final String... _params)
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put(_fileName, _code);
        final Map<String,String> params = new HashMap<String,String>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }
        final Map<?,?> bck = this.executeEncoded("Update", params, "FileContents", files);

        return bck;
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
     * Check to export an interface with one single parent interface.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with one parent interface")
    public void exportWithOneParent()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent = data.getInterface("TestInterfaceParent");
        final InterfaceData inter = data.getInterface("TestInterface").addParent(parent);
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface with two parent interfaces.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with two parent interfaces")
    public void exportWithTwoParents()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent1 = data.getInterface("TestInterfaceParent1");
        final InterfaceData parent2 = data.getInterface("TestInterfaceParent2");
        final InterfaceData inter = data.getInterface("TestInterface")
                .addParent(parent1)
                .addParent(parent2);
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface with one single string attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with one attribute")
    public void exportWithOneAttribute()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final AttributeStringData attr = data.getAttributeString("Attribute");
        final InterfaceData inter = data.getInterface("TestInterface").addAttribute(attr);
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface for one single type.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with one single type")
    public void exportWithOneType()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final TypeData type = data.getType("TestType");
        final InterfaceData inter = data.getInterface("TestInterface").addType(type);
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface for two types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with two types")
    public void exportWithTwoTypes()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final TypeData type1 = data.getType("TestType1");
        final TypeData type2 = data.getType("TestType2");
        final InterfaceData inter = data.getInterface("TestInterface")
                .addType(type1)
                .addType(type2);
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface for all types.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with all types")
    public void exportWithAllTypes()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final InterfaceData inter = data.getInterface("TestInterface").addAllTypes();
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface for one single relationship.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with one single relationship")
    public void exportWithOneRelationship()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final RelationshipData rel = data.getRelationship("TestRelationship");
        final InterfaceData inter = data.getInterface("TestInterface").addRelationship(rel);
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface for all relationships.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with all relationships")
    public void exportWithAllRelationships()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final InterfaceData inter = data.getInterface("TestInterface").addAllRelationships();
        data.create();

        inter.checkExport(inter.export());
    }

    /**
     * Check to export an interface with special characters for all cases.
     *
     * @throws Exception if test failed
     */
    @Test(description = "export interface with special characters")
    public void exportWithSpecialCharacters()
        throws Exception
    {
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent1 = data.getInterface("TestInerfaceParent \" 1");
        final InterfaceData parent2 = data.getInterface("TestInerfaceParent \" 2");
        final AttributeStringData attr1 = data.getAttributeString("Attribute \" 1");
        final AttributeStringData attr2 = data.getAttributeString("Attribute \" 2");
        final TypeData type1 = data.getType("TestType \" 1");
        final TypeData type2 = data.getType("TestType \" 2");
        final RelationshipData rel1 = data.getRelationship("TestRel \" 1");
        final RelationshipData rel2 = data.getRelationship("TestRel \" 2");
        final InterfaceData inter = data.getInterface("TestInterface \"")
                .addParent(parent1)
                .addParent(parent2)
                .addAttribute(attr1)
                .addAttribute(attr2)
                .addType(type1)
                .addType(type2)
                .addRelationship(rel1)
                .addRelationship(rel2);
        data.create();

        inter.checkExport(inter.export());
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
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent = data.getInterface("TestInterfaceParent");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface").addParent(parent);
        inter.update();

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
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent1 = data.getInterface("TestInerfaceParent1");
        final InterfaceData parent2 = data.getInterface("TestInerfaceParent2");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface")
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
        final DataCollection data = new DataCollection(this);
        final AttributeStringData attr = data.getAttributeString("Attribute");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface").addAttribute(attr);
        inter.update();

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
        final DataCollection data = new DataCollection(this);
        final TypeData type = data.getType("TestType");
        final InterfaceData inter = data.getInterface("TestInterface").addAllTypes();
        data.create();

        inter.removeTypes()
             .addType(type)
             .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select type dump"),
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
        final DataCollection data = new DataCollection(this);
        final TypeData type = data.getType("TestType");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface").addType(type);
        inter.update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select type dump"),
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
        final DataCollection data = new DataCollection(this);
        final TypeData type1 = data.getType("TestType1");
        final TypeData type2 = data.getType("TestType2");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface")
                .addType(type1)
                .addType(type2);
        inter.update();

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
        final DataCollection data = new DataCollection(this);
        final TypeData type = data.getType("TestType");
        final InterfaceData inter = data.getInterface("TestInterface").addType(type);
        data.create();

        inter.addAllTypes()
             .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select type dump"),
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
        final DataCollection data = new DataCollection(this);
        final RelationshipData rel1 = data.getRelationship("TestRel1");
        final RelationshipData rel2 = data.getRelationship("TestRel2");
        final InterfaceData inter = data.getInterface("TestInterface").addRelationship(rel1);
        data.create();

        inter.removeRelationships()
             .addRelationship(rel2)
             .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select relationship dump"),
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
        final DataCollection data = new DataCollection(this);
        final RelationshipData rel = data.getRelationship("TestType");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface").addRelationship(rel);
        inter.update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select relationship dump"),
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
        final DataCollection data = new DataCollection(this);
        final RelationshipData rel = data.getRelationship("TestRel");
        final InterfaceData inter = data.getInterface("TestInterface").addAllRelationships();
        data.create();

        inter.removeRelationships()
             .addRelationship(rel)
             .update();

        Assert.assertEquals(this.mql("print interface '" + inter.getName() + "' select relationship dump"),
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
        final DataCollection data = new DataCollection(this);
        final RelationshipData rel = data.getRelationship("TestType");
        final InterfaceData inter = data.getInterface("TestInterface").addRelationship(rel);
        data.create();

        inter.addAllRelationships()
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
        final DataCollection data = new DataCollection(this);
        final InterfaceData inter = data.getInterface("TestInterface")
                .addAllRelationships()
                .addAllTypes();
        data.create();

        inter.removeRelationships()
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
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent1 = data.getInterface("TestInterfaceParent \" 1");
        final InterfaceData parent2 = data.getInterface("TestInterfaceParent \" 2");
        final AttributeStringData attr1 = data.getAttributeString("Attribute \" 1");
        final AttributeStringData attr2 = data.getAttributeString("Attribute \" 2");
        final TypeData type1 = data.getType("TestType \" 1");
        final TypeData type2 = data.getType("TestType \" 2");
        final RelationshipData rel1 = data.getRelationship("TestRel \" 1");
        final RelationshipData rel2 = data.getRelationship("TestRel \" 2");
        data.create();

        final InterfaceData inter = data.getInterface("TestInterface \"")
                .addParent(parent1)
                .addParent(parent2)
                .addAttribute(attr1)
                .addAttribute(attr2)
                .addType(type1)
                .addType(type2)
                .addRelationship(rel1)
                .addRelationship(rel2);
        inter.update();

        final Set<String> resultParent = new HashSet<String>();
        resultParent.add(parent1.getName());
        resultParent.add(parent2.getName());
        Assert.assertEquals(this.mqlAsSet("print interface '" + inter.getName() + "' select derived dump '\n'"),
                            resultParent,
                            "check that all parent interfaces are defined");

        final Set<String> resultAttrs = new HashSet<String>();
        resultAttrs.add(attr1.getName());
        resultAttrs.add(attr2.getName());
        Assert.assertEquals(this.mqlAsSet("escape print interface \""
                                    + AbstractTest.convertMql(inter.getName()) + "\" select attribute dump '\n'"),
                            resultAttrs,
                            "check that all types are defined");

        final Set<String> resultTypes = new HashSet<String>();
        resultTypes.add(type1.getName());
        resultTypes.add(type2.getName());
        Assert.assertEquals(this.mqlAsSet("escape print interface \""
                                    + AbstractTest.convertMql(inter.getName()) + "\" select type dump '\n'"),
                            resultTypes,
                            "check that all types are defined");

        final Set<String> resultRels = new HashSet<String>();
        resultRels.add(rel1.getName());
        resultRels.add(rel2.getName());
        Assert.assertEquals(this.mqlAsSet("escape print interface \""
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
        final DataCollection data = new DataCollection(this);
        final InterfaceData parent1 = data.getInterface("TestInterfaceParent1");
        final InterfaceData parent2 = data.getInterface("TestInterfaceParent2");
        final InterfaceData inter = data.getInterface("TestInterface")
                .addParent(parent1)
                .addParent(parent2);
        data.create();

        inter.removeParents()
             .addParent(parent1);

        this.updateFailure(inter, UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_REMOVING_PARENT);
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
        final DataCollection data = new DataCollection(this);
        final InterfaceData inter = data.getInterface("TestInterface");
        this.updateFailure(inter.getCIFileName(),
                           "mql mod interface \"${NAME}\"\ntestParents -interface "
                                        + inter.getName() + "1",
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
        final DataCollection data = new DataCollection(this);
        final InterfaceData inter = data.getInterface("TestInterface");
        this.updateFailure(inter.getCIFileName(),
                           "mql mod interface \"${NAME}\"\ntestParents -hallo",
                           UpdateException_mxJPO.Error.DM_INTERFACE_UPDATE_UKNOWN_PARAMETER);
    }
}
