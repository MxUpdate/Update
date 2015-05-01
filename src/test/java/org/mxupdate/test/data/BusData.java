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

package org.mxupdate.test.data;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.data.datamodel.TypeData;

/**
 * Used to define an business object object, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class BusData
    extends AbstractBusData<BusData>
{
    /**
     * Initialize this notification object with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this notification
     *                  object is defined)
     * @param _ci       ci definition
     * @param _name     name of the notification object
     * @param _revision revision of the notification
     */
    public BusData(final AbstractTest _test,
                   final CI _ci,
                   final String _name,
                   final String _revision)
    {
        super(_test, _ci, _name, _revision);
    }

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _type                 derived type
     * @param _name                 name of the business object
     * @param _revision             revision of the business object
     */
    public BusData(final AbstractTest _test,
                      final CI _ci,
                      final TypeData _type,
                      final String _name,
                      final String _revision)
    {
        super(_test, _ci, _type, _name, _revision);
    }
}
