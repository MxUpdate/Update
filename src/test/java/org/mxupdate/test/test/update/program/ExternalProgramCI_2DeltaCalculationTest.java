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

import org.mxupdate.test.data.program.ExternalProgramData;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.Test;

/**
 * Tests the {@link ProgramCI_mxJPO program CI} delta calculation for external
 * programs.
 *
 * @author The MxUpdate Team
 */
@Test
public class ExternalProgramCI_2DeltaCalculationTest
    extends AbstractProgramCI_2DeltaCalculationTest<ExternalProgramData>
{
    @Override
    protected ProgramCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new ProgramCI_mxJPO(_paramCache.getMapping().getTypeDef(CI.PRG_EXTERNAL.updateType), _name);
    }

    @Override
    protected ExternalProgramData createNewTestData(final String _name)
    {
        return new ExternalProgramData(this, _name);
    }
}
