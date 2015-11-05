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

package org.mxupdate.test.test.update.system.uniquekeyci;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractDataWithTrigger;
import org.mxupdate.test.data.datamodel.AttributeData;
import org.mxupdate.test.data.datamodel.RelationshipData;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.system.UniqueKeyData;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.update.system.UniqueKeyCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link UniqueKeyCO_mxJPO unique key CI} export / update.
 *
 * @author The MxUpdate Team
 */
public class UniqueKeyCI_3UpdateTest
    extends AbstractDataExportUpdate<UniqueKeyData>
{
    /**
     * Dummy implementation.
     *
     * @return object array with all test interfaces
     */
    @DataProvider(name = "data")
    public Object[][] dataInterfaces()
    {
        return new Object[][]{};
    }

    /**
     * Negative test that the unique key is changed from relationship to type.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that the unique key is changed from relationship to type")
    public void t1a_negativeTest70201ChangeRelation2Type()
        throws Exception
    {
        this.createTestData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for type", "MXUPDATE_Test")
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test that the unique key is changed from type to relationship.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that the unique key is changed from type to relationship")
    public void t1a_negativeTest70201ChangeType2Relation()
        throws Exception
    {
        this.createTestData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for relationship", "MXUPDATE_Test")
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test that the used relationship of a unique key is changed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that the used relationship of a unique key is changed")
    public void t1a_negativeTest70201ChangeRelationName()
        throws Exception
    {
        this.createTestData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for relationship", "MXUPDATE_TestNEW")
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test that the used type of a unique key is changed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test that the used type of a unique key is changed")
    public void t1a_negativeTest70201ChangeTypeName()
        throws Exception
    {
        this.createTestData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for type", "MXUPDATE_TestNEW")
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test if that the global flag for relationship is changed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if that the global flag for relationship is changed")
    public void t1a_negativeTest70201ChangeRelationGlobal()
        throws Exception
    {
        this.createTestData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for relationship", "MXUPDATE_Test")
                .setFlag("global", true)
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test if that the global flag for type is changed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if that the global flag for type is changed")
    public void t1a_negativeTest70201ChangeTypeGlobal()
        throws Exception
    {
        this.createTestData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for type", "MXUPDATE_Test")
                .setFlag("global", true)
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test if that the interface for relationship is changed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if that the interface for relationship is changed")
    public void t1a_negativeTest70201ChangeRelationInterface()
        throws Exception
    {
        this.createTestData("for relationship", new RelationshipData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for relationship", "MXUPDATE_Test")
                .setValue("with interface", "MXUPDATE_Test")
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Negative test if that the interface for type is changed.
     *
     * @throws Exception if test failed
     */
    @Test(description = "negative test if that the interface for type is changed")
    public void t1a_negativeTest70201ChangeTypeInterface()
        throws Exception
    {
        this.createTestData("for type", new TypeData(this, "Test").defData("attribute", new AttributeData(this, "Test").setSingle("kind", "string")));

        new UniqueKeyData(this, "Test")
                .setValue("for type", "MXUPDATE_Test")
                .setValue("with interface", "MXUPDATE_Test")
                .failureUpdate(ErrorKey.SYS_UNIQUEKEY_FOR_CHANGED);
    }

    /**
     * Creates needed test data.
     *
     * @param _key      ci key (for type or for relationship)
     * @param _data     used date
     * @throws Exception if create failed
     */
    private void createTestData(final String _key,
                                final AbstractDataWithTrigger<?> _data)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final UniqueKeyData data = new UniqueKeyData(this, "Test")
                .defData(_key, _data)
                .defText("field", "\"attribute[MXUPDATE_Test]\" size 120")
                .setFlag("enable", true);
        data.createDependings();
        final WrapperCIInstance<UniqueKeyCI_mxJPO> currentWrapper = new WrapperCIInstance<>(new UniqueKeyCI_mxJPO(data.getName()));
        currentWrapper.parseUpdate(data);
        currentWrapper.create(paramCache);
        data.update("");
    }

    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws Exception
    {
        this.cleanup(AbstractTest.CI.SYS_UNIQUEKEY);
        this.cleanup(AbstractTest.CI.DM_ATTRIBUTE);
        this.cleanup(AbstractTest.CI.DM_RELATIONSHIP);
        this.cleanup(AbstractTest.CI.DM_TYPE);
    }

    /**
     * Creates for given {@code _name} a new path type data instance.
     *
     * @param _name     name of the path type data instance
     * @return path type data instance
     */
    @Override
    protected UniqueKeyData createNewData(final String _name)
    {
        return new UniqueKeyData(this, _name).defData("for type", new TypeData(this, "Test"));
    }
}
