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

package org.mxupdate.test.data.user.organization;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;

/**
 * Used to handle organizational data for testing persons with business object.
 *
 * @author The MxUpdate Team
 * @param <ORGDATA>  organizational class
 */
public abstract class AbstractOrganizationalData<ORGDATA extends AbstractOrganizationalData<ORGDATA>>
    extends AbstractBusData<ORGDATA>
{
    /**
     * Constructor to initialize this organizational instance.
     *
     * @param _test     related test implementation
     * @param _ci       configuration item enumeration
     * @param _name     name of the business unit
     */
    protected AbstractOrganizationalData(final AbstractTest _test,
                                         final AbstractTest.CI _ci,
                                         final String _name)
    {
        super(_test, _ci, _name, "-");
    }
}
