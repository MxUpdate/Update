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

import org.mxupdate.test.data.datamodel.PolicyData;
import org.mxupdate.test.data.datamodel.PolicyData.State;
import org.mxupdate.test.data.datamodel.helper.Access;
import org.testng.annotations.Test;

/**
 * Test class for policy exports.
 *
 * @author The MxUpdate Team
 */
@Test()
public class Policy_StateAccessTest
    extends AbstractPolicy_AccessTest
{
    /**
     * Returns text string &quot;policy with state&quot;.
     */
    @Override()
    public String getDescriptionPrefix()
    {
        return "policy with state";
    }

    /**
     * A new policy with one new state with the access list {@code _accesss} is
     * defined.
     */
    @Override()
    public PolicyData createTestData4Access(final Access... _accesss)
    {
        return new PolicyData(this, "test")
                .addState(new State()
                        .setName("create")
                        .addAccess(_accesss));
    }
}
