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

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.datamodel.NotificationData;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of notification objects.
 *
 * @author The MxUpdate Team
 */
public class NotificationTest
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
                        new NotificationData(this, "HelloTest", "1")},
                new Object[]{
                        "simple object with description",
                        new NotificationData(this, "Hello \"Test\"", "1")
                                .setDescription("a \"description\"")},
                new Object[]{
                        "simple object with description and single apostrophe",
                        new NotificationData(this, "Hello \"Test\" 'with single apostrophe'", "1")
                                .setDescription("a \"description\" with single 'apostrophe'")},
                new Object[]{
                        "complex object",
                        new NotificationData(this, "HelloTest", "1")
                                .setValue("Attachments", "complex \"data\" 'and single 'apostrophe'")},
        };
    }

    /**
     * Cleanup all test notification objects.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_NOTIFICATION);
    }

    /**
     * Tests a new created notification objects and the related export.
     *
     * @param _description      description of the test case
     * @param _notification     notification to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas", description = "test export of new created notification objects")
    public void simpleExport(final String _description,
                             final NotificationData _notification)
        throws Exception
    {
        _notification.create();
        _notification.checkExport(_notification.export());
    }

    /**
     * Tests an update of non existing table. The result is tested with by
     * exporting the table and checking the result.
     *
     * @param _description      description of the test case
     * @param _notification     notification to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "busDatas", description = "test update of non existing table")
    public void simpleUpdate(final String _description,
                             final NotificationData _notification)
        throws Exception
    {
        // first update with original content
        _notification.update((String) null);
        final ExportParser exportParser = _notification.export();
        _notification.checkExport(exportParser);

        // second update with delivered content
        _notification.updateWithCode(exportParser.getOrigCode(), (String) null)
                     .checkExport();
    }
}
