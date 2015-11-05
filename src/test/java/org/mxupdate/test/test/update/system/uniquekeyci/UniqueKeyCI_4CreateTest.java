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

package org.mxupdate.test.test.update.system.uniquekeyci;

import org.mxupdate.script.ScriptContext_mxJPO;
import org.mxupdate.script.statement.MxUpdateStatement_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.datamodel.TypeData;
import org.mxupdate.test.data.system.UniqueKeyData;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the create of system index configuration items.
 *
 * @author The MxUpdate Team
 */
public class UniqueKeyCI_4CreateTest
    extends AbstractTest
{
    /**
     * Simple positive test.
     *
     * @throws Exception if test failed
     */
    @Test(description = "simple positive test")
    public void positiveTestSimple()
        throws Exception
    {
        new TypeData(this, "Test").create();

        final ScriptContext_mxJPO context = new ScriptContext_mxJPO(new ParameterCache_mxJPO(this.getContext(), false))
                .defVar("NAME", AbstractTest.PREFIX + "Test")
                .defVar("FILENAME", "dummy.txt");

        new MxUpdateStatement_mxJPO().setMxUpdateType(CI.SYS_UNIQUEKEY.mxUpdateType).setCode("for type MXUPDATE_Test").execute(context);

        new UniqueKeyData(this, "Test").checkExport();
    }

    /**
     * Cleanup all test data.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(CI.SYS_UNIQUEKEY);
    }
}
