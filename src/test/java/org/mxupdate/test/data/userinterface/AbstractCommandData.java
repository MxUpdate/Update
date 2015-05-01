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

package org.mxupdate.test.data.userinterface;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Handles test data for commands / menus.
 *
 * @param <T>    related command class
 * @author The MxUpdate Team
 */
abstract class AbstractCommandData<T extends AbstractCommandData<?>>
    extends AbstractAdminData<T>
{
    /**
     *
     * @param _test         related test implementation (where this command is
     *                      defined)
     * @param _ci           configuration item type
     * @param _name         name of command
     */
    AbstractCommandData(final AbstractTest _test,
                        final AbstractTest.CI _ci,
                        final String _name)
    {
        super(_test, _ci, _name);
    }
}
