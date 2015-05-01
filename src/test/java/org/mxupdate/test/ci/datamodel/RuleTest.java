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
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.util.Version;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for rule exports and updates.
 *
 * @author The MxUpdate Team
 */
@Test()
public class RuleTest
    extends AbstractDataExportUpdate<RuleData>
{
    /**
     * Creates for given <code>_name</code> a new rule instance.
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
     * Data provider for test rules.
     *
     * @return object array with all test rules
     */
    @DataProvider(name = "data")
    public Object[][] getRules()
    {
        return this.prepareData("rule",
                new Object[]{
                        "rule without anything (to test required fields)",
                        new RuleData(this, "hello \" test")},
                new Object[]{
                        "issue #220: rule with enforce reserve access flag",
                        new RuleData(this, "test").setFlag("enforcereserveaccess", true)
                                .notSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #220: rule with negative defined enforce reserve access flag (which will not part of the CI file)",
                        new RuleData(this, "test").setFlag("enforcereserveaccess", false)
                                .notSupported(Version.V6R2011x),
                        new RuleData(this, "test")}
        );
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
    }
}
