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

package org.mxupdate.test.test.update;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractBusData;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Common methods to test business object updates.
 *
 * @author The MxUpdate Team
 * @param <BUSDATA> bus data class definition
 */
public abstract class AbstractBusUpdateTest<BUSDATA extends AbstractBusData<?>>
    extends AbstractTest
{
    /**
     * Data provider for test notification objects.
     *
     * @return object array with all test objects
     */
    @DataProvider(name = "busDatas")
    public Object[][] getData()
    {
        return new Object[][]  {
                new Object[]{
                        "simple object",
                        this.createNewData("HelloTest", "1")},
                new Object[]{
                        "simple object with description",
                        this.createNewData("Hello \"Test\"", "1")
                                .setValue("description", "a \"description\"")},
                new Object[]{
                        "simple object with description and single apostrophe",
                        this.createNewData("Hello \"Test\" 'with single apostrophe'", "1")
                                .setValue("description", "a \"description\" with single 'apostrophe'")},
        };
    }

    /**
     * Creates for given {@code _name} related data instance.
     *
     * @param _name     name of the business object
     * @param _revision revision of the business object
     * @return new create data instance
     */
    protected abstract BUSDATA createNewData(final String _name, final String _revision);

    /**
     * Tests a new created notification objects and the related export.
     *
     * @param _description  description of the test case
     * @param _busData      business object to test
     * @throws Exception if test failed
     */
    @Test(description = "test export of new created notification objects",
          dataProvider = "busDatas")
    public void simpleExport(final String _description,
                             final BUSDATA _busData)
        throws Exception
    {
        _busData.create();
        _busData.checkExport(_busData.export());
    }

    /**
     * Tests an update of non existing table. The result is tested with by
     * exporting the table and checking the result.
     *
     * @param _description  description of the test case
     * @param _busData      business object to test
     * @throws Exception if test failed
     */
    @Test(description = "test update of non existing table",
          dataProvider = "busDatas")
    public void simpleUpdate(final String _description,
                             final BUSDATA _busData)
        throws Exception
    {
        // create object
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef(_busData.getCI().updateType);
        final WrapperCIInstance<?> currentWrapper = new WrapperCIInstance<>(typeDef.newTypeInstance(_busData.getName()));
        currentWrapper.create(paramCache);
        // create the depending objects to be able to connect to them
        _busData.createDependings();

        // first update with original content
        _busData.update((String) null);

        final ExportParser exportParser = _busData.export();
        _busData.checkExport(exportParser);

        // second update with delivered content
        _busData.updateWithCode(exportParser.getOrigCode(), (String) null)
                     .checkExport();
    }

    /**
     * Cleanup all test notification objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass(groups = "close")
    public abstract void cleanup()
        throws MatrixException;
}
