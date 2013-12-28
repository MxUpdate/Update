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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.ci.datamodel.AccessTestUtil.IAccessTest;
import org.mxupdate.test.util.IssueLink;
import org.testng.annotations.DataProvider;

/**
 * Abstract definition for policy access tests (for allstate and a single
 * state).
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractPolicy_AccessTest
    extends AbstractPolicyTest
    implements IAccessTest
{
    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"99", "177", "180", "181"})
    @DataProvider(name = "data")
    public Object[][] getPolicies()
    {
        return super.prepareData((String) null, AccessTestUtil.getTestData(this));
    }
}
