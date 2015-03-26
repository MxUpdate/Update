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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.AbstractAttributeData;
import org.mxupdate.test.data.datamodel.AbstractDataWithAttribute;
import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.annotations.Test;

/**
 * Class is used to test data with attributes exports and updates.
 *
 * @param <DATAWITHATTRIBUTE>     derived data class with attributes
 * @author The MxUpdate Team
 */
public abstract class AbstractDataWithAttributesExportUpdateTest<DATAWITHATTRIBUTE extends AbstractDataWithAttribute<?>>
    extends AbstractDataExportUpdate<DATAWITHATTRIBUTE>
{
    /**
     * Prepares the test data.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _datas    specific test data to append
     * @return prepared test data
     */
    @Override()
    protected Object[][] prepareData(final String _logText,
                                     final Object[]... _datas)
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        ret.add(new Object[]{
                _logText + " with one attribute",
                this.createNewData("Test \" 1")
                        .addAttribute(new AttributeStringData(this, "String Attribute"))});

        ret.add(new Object[]{
                _logText + " with two escaped attributes",
                this.createNewData("Test \" 1")
                        .addAttribute(new AttributeStringData(this, "String Attribute \" ' Hello"))
                        .addAttribute(new AttributeStringData(this, "String Attribute \" { Hello"))});

        ret.add(new Object[]{
                _logText + " with two attributes in braces",
                this.createNewData("Test \" 1")
                        .addAttribute(new AttributeStringData(this, "{String Attribute Hello}"))
                        .addAttribute(new AttributeStringData(this, "String Attribute {} Hello"))});

        ret.add(new Object[]{
                _logText + " with attributes with two attributes, two ignored attributes and two removed attributes",
                this.createNewData("Test \" 1")
                        .addAttribute(new AttributeStringData(this, "String Attribute \" ' Hello 1"))
                        .addAttribute(new AttributeStringData(this, "String Attribute \" { Hello 2"))
                        .addIgnoreAttribute(new AttributeStringData(this, "String Attribute \" ' Hello 3"))
                        .addIgnoreAttribute(new AttributeStringData(this, "String Attribute \" { Hello 4"))
                        .addRemoveAttribute(new AttributeStringData(this, "String Attribute \" ' Hello 5"))
                        .addRemoveAttribute(new AttributeStringData(this, "String Attribute \" { Hello 6"))});

        ret.addAll(Arrays.asList(_datas));
        return super.prepareData(_logText, ret.toArray(new Object[ret.size()][]));
    }

    /**
     * Creates a clean data instance used to update an existing data instance.
     *
     * @param _original     original data instance
     * @return new data instance (where all original data is cleaned)
     */
    @Override()
    protected DATAWITHATTRIBUTE createCleanNewData(final DATAWITHATTRIBUTE _original)
    {
        @SuppressWarnings("unchecked")
        final DATAWITHATTRIBUTE ret = (DATAWITHATTRIBUTE) this.createNewData(_original.getName().substring(AbstractTest.PREFIX.length()))
                                       .addAttribute(_original.getAttributes().toArray(new AbstractAttributeData<?>[_original.getAttributes().size()]));

        // define all required values to empty values so that they are checked
        for (final Map.Entry<String,Object> value : ret.getRequiredExportValues().entrySet())  {
            ret.setValue(value.getKey(), value.getValue());
        }

        return ret;
    }

    /**
     * Test update of already created data with attributes to ignore and to
     * removed.
     *
     * @param _description  description of the test case
     * @param _data         data to test
     * @param _expData      expected data
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data",
          description = "test update of already created data")
    public void testUpdate4Created(final String _description,
                                   final DATAWITHATTRIBUTE _data,
                                   final DATAWITHATTRIBUTE _expData)
        throws Exception
    {
        _data.create()
             .appendIgnoredAttributes()
             .appendRemoveAttributes()
             .update((String) null);
        _expData
             .checkExport();
    }

    /**
     * Test thrown exception of called update with wrong arguments for TCL
     * procedure 'testAttributes'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "test thrown exception of called update with wrong arguments for TCL procedure 'testAttributes'")
    public void testException4TCLAttributesWrongParameters()
        throws Exception
    {
        this.createNewData("Test")
            .failedUpdateWithCode(
                    "testAttributes -wrongparameter",
                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_UKNOWN_PARAMETER);
    }

    /**
     * Test thrown exception of called update with wrong argument for the
     * administration type for TCL procedure 'testAttributes'.
     *
     * @throws Exception if test failed
     */
    @SuppressWarnings("unchecked")
    @Test(description = "test thrown exception of called update with wrong argument for the administration type for TCL procedure 'testAttributes'")
    public void testException4TCLAttributeWrongAdminTypeParameters()
        throws Exception
    {
        final DATAWITHATTRIBUTE data = (DATAWITHATTRIBUTE) this.createNewData("Test").create();
        if (data.getCI() != AbstractTest.CI.DM_INTERFACE)  {
            data.failedUpdateWithCode(
                    "testAttributes -interface ${NAME}",
                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_UKNOWN_PARAMETER);
        }
        if (data.getCI() != AbstractTest.CI.DM_RELATIONSHIP)  {
            data.failedUpdateWithCode(
                    "testAttributes -relationship ${NAME}",
                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_UKNOWN_PARAMETER);
        }
        if (data.getCI() != AbstractTest.CI.DM_TYPE)  {
            data.failedUpdateWithCode(
                    "testAttributes -type ${NAME}",
                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_UKNOWN_PARAMETER);
        }
    }

    /**
     * Test thrown exception of called update with wrong object name for TCL
     * procedure 'testAttributes'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "test thrown exception of called update with wrong object name for TCL procedure 'testAttributes'")
    public void testException4TCLAttributesWrongName()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .create()
            .failedUpdateWithCode(
                    "testAttributes -" + this.createNewData("").getCI().getMxType() + " MyTest",
                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_WRONG_OBJECT);
    }

    /**
     * Test thrown exception of called update code tries to remove an
     * attribute.
     *
     * @throws Exception if test failed
     */
    @Test(description = "test thrown exception of called update code tries to remove an attribute")
    public void testException4TCLAttributesRemoved()
        throws Exception
    {
        this.createNewData("Test")
            .addAttribute(new AttributeStringData(this, "Test Attribute"))
            .create()
            .failedUpdateWithCode(
                    "testAttributes -" + this.createNewData("").getCI().getMxType() + " \"${NAME}\" -attributes [list]",
                    UpdateException_mxJPO.Error.DM_ABSTRACTWITHATTRIBUTES_UPDATE_ATTRIBUTE_REMOVED);
    }
}
