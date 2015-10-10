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

import org.mxupdate.update.program.ExternalProgram_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link ExternalProgram_mxJPOs external program CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class ExternalProgramCI_1ParserTest
    extends AbstractProgramCI_1ParserTest<ExternalProgram_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return this.getData("external");
    }

    @Override()
    protected ExternalProgram_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                                  final String _name)
    {
        return new ExternalProgram_mxJPO(_paramCache.getMapping().getTypeDef(CI.PRG_EXTERNAL.updateType), _name);
    }
}
