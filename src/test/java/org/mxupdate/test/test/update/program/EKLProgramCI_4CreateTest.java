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

package org.mxupdate.test.test.update.program;

import org.mxupdate.test.data.program.EKLProgramData;
import org.testng.annotations.Test;

/**
 * Tests the create of EKL
 * {@link org.mxupdate.update.program.ProgramCI_mxJPO program}.
 *
 * @author The MxUpdate Team
 */
@Test
public class EKLProgramCI_4CreateTest
    extends Abstract_4CreateTest
{
    @Override
    protected EKLProgramData createNewTestData(final String _name)
    {
        return new EKLProgramData(this, _name);
    }

    @Override
    protected String getKind()
    {
        return "ekl";
    }
}
