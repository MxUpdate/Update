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

package org.mxupdate.test.test.update.datamodel;

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.AllState;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.testng.annotations.Test;

/**
 * Tests the {@link Policy_mxJPO policy CI} allstate access export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PolicyCI_5AllStateAccessTest
    extends AbstractPolicy_AccessTest
{
    /**
     * Returns text string &quot;policy with allstate&quot;.
     */
    @Override()
    public String getDescriptionPrefix()
    {
        return "policy with allstate";
    }

    /**
     * A new policy with allstate for the {@code _accesss} is defined.
     */
    @Override()
    public PolicyData createTestData4Access(final Access... _accesss)
    {
        return new PolicyData(this, "test")
                .setAllState(new AllState()
                        .addAccess(_accesss));
    }
}
