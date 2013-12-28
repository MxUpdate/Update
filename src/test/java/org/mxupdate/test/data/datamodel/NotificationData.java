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

package org.mxupdate.test.data.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;

/**
 * Used to define an notification object, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class NotificationData
    extends AbstractBusData<NotificationData>
{
    /**
     * Initialize this notification object with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this notification
     *                  object is defined)
     * @param _name     name of the notification object
     * @param _revision revision of the notification
     */
    public NotificationData(final AbstractTest _test,
                            final String _name,
                            final String _revision)
    {
        super(_test, AbstractTest.CI.DM_NOTIFICATION, _name, _revision);
    }
}
