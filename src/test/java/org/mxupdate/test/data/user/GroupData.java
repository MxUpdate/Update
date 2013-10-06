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

package org.mxupdate.test.data.user;

import org.mxupdate.test.AbstractTest;

/**
 * The class is used to define all group objects used to create / update and
 * to export.
 *
 * @author The MxUpdate Team
 */
public class GroupData
    extends AbstractCollectionUserData<GroupData>
{
    /**
     * Constructor to initialize this role.
     *
     * @param _test     related test implementation (where this group is
     *                  defined)
     * @param _name     name of the group
     */
    public GroupData(final AbstractTest _test,
                     final String _name)
    {
        super(_test, AbstractTest.CI.USR_GROUP, _name);
    }
}
