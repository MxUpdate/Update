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
