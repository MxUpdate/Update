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

package org.mxupdate.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.Test;

/**
 * Test cases for the export / update of abstract data.
 *
 * @author The MxUpdate Team
 * @param <DATA> class of the data
 */
public abstract class AbstractDataExportUpdate<DATA extends AbstractAdminData<?>>
    extends AbstractTest
{
    /**
     * Prepares the test data.
     *
     * @param _logText  text used for the description (logging purpose)
     * @param _datas    specific test data to append
     * @return prepared test data
     */
    protected Object[][] prepareData(final String _logText,
                                     final Object[]... _datas)
    {
        final List<Object[]> tmp = new ArrayList<>();
        tmp.addAll(Arrays.asList(_datas));
        if (_logText != null)  {
            tmp.add(new Object[]{
                    _logText + " with property name",
                    this.createNewData("hello \" test")
                                    .addProperty(new PropertyDef("my test \"property\" desc\"\\\\ription"))});
            tmp.add(new Object[]{
                    _logText + " property name and value",
                    this.createNewData("hello \" test")
                                    .addProperty(new PropertyDef("my test \"property\"", "my \"value\" desc\"\\\\ription"))});
            tmp.add(new Object[]{
                    _logText + " property name, value and referenced admin object",
                    this.createNewData("hello \" test")
                                    .addProperty(new PropertyDef("my test \"property\"", "my \"value\"", this.createNewData("property \" admin " + _logText)))});
            tmp.add(new Object[]{
                    _logText + " with multiple properties",
                    this.createNewData("hello \" test")
                                    .addProperty(new PropertyDef("my test \"property\" 1"))
                                    .addProperty(new PropertyDef("my test \"property\" 2", "my \"value\" desc\"\\\\ription"))
                                    .addProperty(new PropertyDef("my test \"property\" 3", this.createNewData("property \" admin " + _logText)))});

            // hidden flag
            tmp.add(new Object[]{
                    _logText + " with hidden flag true",
                    this.createNewData("hello \" test").setFlag("hidden", true)});
            tmp.add(new Object[]{
                    _logText + " with hidden flag false",
                    this.createNewData("hello \" test").setFlag("hidden", false),
                    this.createNewData("hello \" test")});
            tmp.add(new Object[]{
                    _logText + " with default hidden flag",
                    this.createNewData("hello \" test").setFlag("hidden", null),
                    this.createNewData("hello \" test")});
        }

        final List<Object[]> ret = new ArrayList<>();
        for (final Object[] data : tmp)
        {
            @SuppressWarnings("unchecked")
            final DATA testData = (DATA) data[1];
            if (testData.isSupported(this.getVersion()))
            {
                ret.add(new Object[]{data[0], data[1], (data.length > 2) ? data[2] : data[1], (data.length > 3) ? data[3] : null});
            }
        }

        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * Creates for given <code>_name</code> related data instance.
     *
     * @param _name     name of the user
     * @return new create data instance
     */
    protected abstract DATA createNewData(final String _name);

    /**
     * Tests a new created data and the related export.
     *
     * @param _description  description of the test case
     * @param _orgData      original data creating the CI
     * @param _expData      CI definition of the export
     * @param _expUpdateLog expected text in the log for the update (not used)
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data",
          description = "test export of new created test data object")
    public void testExport(final String _description,
                           final DATA _orgData,
                           final DATA _expData,
                           final String _expUpdateLog)
        throws Exception
    {
        _expData.create()
                .checkExport();
    }

    /**
     * Tests an update of non existing data. The result is tested with by
     * exporting the data and checking the result.
     *
     * @param _description  description of the test case
     * @param _data         data to test
     * @param _expUpdateLog expected text in the log for the update
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data",
          description = "test update of non existing data")
    public void testUpdateWithExport(final String _description,
                                     final DATA _orgData,
                                     final DATA _expData,
                                     final String _expUpdateLog)
        throws Exception
    {
        // work around for policies...
        if (_orgData instanceof PolicyData)  {
            new PolicyData(this, _orgData.getName().substring(AbstractTest.PREFIX.length())).create();
        }
        // create object
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef(_orgData.getCI().updateType);
        final WrapperCIInstance<?> currentWrapper = new WrapperCIInstance<>(typeDef.newTypeInstance(paramCache, _orgData.getName()));
        currentWrapper.parseUpdate(this.createNewData(_orgData.getName()));
        currentWrapper.create(paramCache);
        // create the depending objects to be able to connect to them
        _orgData.createDependings();

        // first update with original content
        _orgData.update(_expUpdateLog);
        final ExportParser exportParser = _expData.export();
        _expData.checkExport(exportParser);

        // second update with delivered content
        _expData.updateWithCode(exportParser.getOrigCode(), (String) null)
             .checkExport();
    }

    /**
     * Test update of existing data that all parameters are cleaned.
     *
     * @param _description  description of the test case
     * @param _data         data to test
     * @param _expUpdateLog expected text in the log for the update
     * @throws Exception if test failed
     */
    @SuppressWarnings("unchecked")
    @Test(dataProvider = "data",
          description = "test update of existing data instance for cleaning")
    public void testUpdateWithClean(final String _description,
                                    final DATA _orgData,
                                    final DATA _expData,
                                    final String _expUpdateLog)
        throws Exception
    {
        // work around for policies...
        if (_orgData instanceof PolicyData)  {
            new PolicyData(this, _orgData.getName().substring(AbstractTest.PREFIX.length())).create();
        }
        // create object
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef(_orgData.getCI().updateType);
        final WrapperCIInstance<?> currentWrapper = new WrapperCIInstance<>(typeDef.newTypeInstance(paramCache, _orgData.getName()));
        currentWrapper.parseUpdate(this.createNewData(_orgData.getName()));
        currentWrapper.create(paramCache);
        // create the depending objects to be able to connect to them
        _orgData.createDependings();

        // first update with original content
        _orgData.update(_expUpdateLog);
        _expData.checkExport();

        // second update with delivered content
        final DATA newData = (DATA) this.createCleanNewData(_expData).update((String) null);

        // check export
        newData.checkExport();
    }

    /**
     * Creates a clean data instance used to update an existing data instance.
     *
     * @param _original     original data instance
     * @return new data instance (where all original data is cleaned)
     */
    protected DATA createCleanNewData(final DATA _original)
    {
        final DATA ret = this.createNewData(_original.getName().substring(AbstractTest.PREFIX.length()));

        return ret;
    }
}
