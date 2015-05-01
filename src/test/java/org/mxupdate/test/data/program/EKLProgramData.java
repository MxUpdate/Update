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

package org.mxupdate.test.data.program;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;
import org.testng.annotations.Test;

/**
 * The class is used to define all MQL program objects used to create / update
 * and to export.
 *
 * @author The MxUpdate Team
 */
@Test()
public class EKLProgramData
    extends AbstractAdminData<EKLProgramData>
{
    /**
     * Initializes this MQL program.
     *
     * @param _test     related test instance
     * @param _name     name of the MQL program
     */
    public EKLProgramData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.PRG_EKL, _name);
    }
}
