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

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.ci.datamodel.AccessTestUtil.IAccessTest;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.mxupdate.test.util.IssueLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for rule exports.
 *
 * @author The MxUpdate Team
 */
@Test()
public class Rule_AccessTest
    extends AbstractDataExportUpdate<RuleData>
    implements IAccessTest
{
    /**
     * Creates for given {@code _name} a new rule instance.
     *
     * @param _name     name of the rule instance
     * @return rule instance
     */
    @Override()
    protected RuleData createNewData(final String _name)
    {
        return new RuleData(this, _name);
    }

    /**
     * Removes the MxUpdate rules.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_RULE);
        this.cleanup(CI.USR_PERSON);
    }

    /**
     * Data provider for test policies.
     *
     * @return object array with all test policies
     */
    @IssueLink({"177", "180", "181"})
    @DataProvider(name = "data")
    public Object[][] getRules()
    {
        return super.prepareData((String) null, AccessTestUtil.getTestData(this));
    }

    /**
     * Returns text string &quot;policy with allstate&quot;.
     */
    @Override()
    public String getDescriptionPrefix()
    {
        return "rule";
    }

    /**
     * A new policy with allstate for the {@code _accesss} is defined.
     */
    @Override()
    public RuleData createTestData4Access(final Access... _accesss)
    {
        return new RuleData(this, "test").addAccess(_accesss);
    }
}
