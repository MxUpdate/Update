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

package org.mxupdate.test.data.user.organization;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;

/**
 * Used to handle organizational data for testing persons with business object.
 *
 * @author The MxUpdate Team
 * @param <ORGDATA>  organizational class
 */
public abstract class AbstractOrganizationalData<ORGDATA extends AbstractOrganizationalData<ORGDATA>>
    extends AbstractBusData<ORGDATA>
{
    /**
     * Constructor to initialize this organizational instance.
     *
     * @param _test     related test implementation
     * @param _ci       configuration item enumeration
     * @param _name     name of the business unit
     */
    protected AbstractOrganizationalData(final AbstractTest _test,
                                         final AbstractTest.CI _ci,
                                         final String _name)
    {
        super(_test, _ci, _name, "-");
    }
}
