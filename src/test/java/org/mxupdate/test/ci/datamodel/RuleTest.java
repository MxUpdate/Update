/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.datamodel.RuleData;
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
                        new RuleData(this, "hello \" test")}
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
