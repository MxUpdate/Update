/*
 * Copyright 2008-2010 The MxUpdate Team
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
import org.mxupdate.test.data.datamodel.RuleData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for rule exports and updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
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
                        "rule with owner access and filter expression",
                        new RuleData(this, "hello \" test")
                                .setOwnerAccess("modify,read,show", "type==Test")},
                new Object[]{
                        "rule with owner access and filter expression",
                        new RuleData(this, "hello \" test")
                                .setOwnerAccess("modify,read,show", "type==\"Test\"")},
                new Object[]{
                        "rule with owner access and without filter expression",
                        new RuleData(this, "hello \" test")
                                .setOwnerAccess("modify,read,show", null)},

                new Object[]{
                        "rule with owner revoke and filter expression",
                        new RuleData(this, "hello \" test")
                                .setOwnerRevoke("modify,read,show", "type==Test")},
                new Object[]{
                        "rule with owner revoke and filter expression",
                        new RuleData(this, "hello \" test")
                                .setOwnerRevoke("modify,read,show", "type==\"Test\"")},
                new Object[]{
                        "rule with owner revoke and without filter expression",
                        new RuleData(this, "hello \" test")
                                .setOwnerRevoke("modify,read,show", null)},

                new Object[]{
                        "rule with public access and without filter expression",
                        new RuleData(this, "hello \" test")
                                .setPublicAccess("modify,read,show", null)},
                new Object[]{
                        "rule with public access and filter expression",
                        new RuleData(this, "hello \" test")
                                .setPublicAccess("modify,read,show", "type==Test")},
                new Object[]{
                        "rule with public access and escaped filter expression",
                        new RuleData(this, "hello \" test")
                                .setPublicAccess("modify,read,show", "type==\"Test\"")},

                new Object[]{
                        "rule with public revoke and without filter expression",
                        new RuleData(this, "hello \" test")
                                .setPublicRevoke("modify,read,show", null)},
                new Object[]{
                        "rule with public revoke and filter expression",
                        new RuleData(this, "hello \" test")
                                .setPublicRevoke("modify,read,show", "type==Test")},
                new Object[]{
                        "rule with public revoke and escaped filter expression",
                        new RuleData(this, "hello \" test")
                                .setPublicRevoke("modify,read,show", "type==\"Test\"")}
        );
    }

    /**
     * Removes the MxUpdate rules.
     *
     * @throws Exception if MQL execution failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_RULE);
    }
}
