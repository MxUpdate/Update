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

/**
 * Used to define a command, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class CommandData
    extends AbstractCommandData<CommandData>
{
    /**
     * Constructor to initialize this command.
     *
     * @param _test     related test implementation (where this command is
     *                  defined)
     * @param _name     name of the command
     */
    public CommandData(final AbstractTest _test,
                       final String _name)
    {
        super(_test, AbstractTest.CI.UI_COMMAND, _name);
    }
}
