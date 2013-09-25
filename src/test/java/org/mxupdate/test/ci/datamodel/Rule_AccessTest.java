/*
 * Copyright 2008-2011 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
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
 * @version $Id$
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
