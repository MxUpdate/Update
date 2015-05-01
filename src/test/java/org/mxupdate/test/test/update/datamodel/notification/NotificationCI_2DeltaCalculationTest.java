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

package org.mxupdate.test.test.update.datamodel.notification;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.NotificationData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the notification CI delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class NotificationCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<BusObject_mxJPO,NotificationData>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1) description",
                    new NotificationData(this, "TestName", "Revision"),
                    new NotificationData(this, "TestName", "Revision").defKeyNotDefined("type").setValue("description", "abc")},
            {"2) Subject Text",
                    new NotificationData(this, "TestName", "Revision"),
                    new NotificationData(this, "TestName", "Revision").defKeyNotDefined("type").setKeyValue("attribute", "Subject Text", "test")},
            {"3) Subject Text",
                    new NotificationData(this, "TestName", "Revision"),
                    new NotificationData(this, "TestName", "Revision").defKeyNotDefined("type").setKeyValue("attribute", "Body Text", "body")},
            {"4a) current inactive -> active",
                    new NotificationData(this, "TestName", "Revision").setValue("current", "Inactive"),
                    new NotificationData(this, "TestName", "Revision").setValue("current", "Active")},
            {"4b) current active -> inactive",
                    new NotificationData(this, "TestName", "Revision").setValue("current", "Active"),
                    new NotificationData(this, "TestName", "Revision").setValue("current", "Inactive")},
       };
    }

    @Override()
    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.DM_NOTIFICATION);
    }

    @Override()
    protected BusObject_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new BusObject_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_NOTIFICATION.updateType), _name);
    }
}
