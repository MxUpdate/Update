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

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.datamodel.RuleData;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.datamodel.Rule_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Rule_mxJPO rule CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class RuleCI_3UpdateTest
    extends AbstractDataExportUpdate<RuleData>
{
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
                                .defNotSupported(Version.V6R2011x)},
                new Object[]{
                        "issue #220: rule with negative defined enforce reserve access flag (which will not part of the CI file)",
                        new RuleData(this, "test").setFlag("enforcereserveaccess", false)
                                .defNotSupported(Version.V6R2011x),
                        new RuleData(this, "test")}
        );
    }

    @Override()
    protected RuleData createNewData(final String _name)
    {
        return new RuleData(this, _name);
    }

    @BeforeMethod()
    @AfterClass(groups = "close")
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_RULE);
    }
}
