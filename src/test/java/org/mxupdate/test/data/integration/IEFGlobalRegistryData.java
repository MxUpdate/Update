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

package org.mxupdate.test.data.integration;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;

/**
 * Used to define an integration global registry, create them and test the
 * result.
 *
 * @author The MxUpdate Team
 */
public class IEFGlobalRegistryData
    extends AbstractBusData<IEFGlobalRegistryData>
{
    /**
     * Initialize this IEF global registry object with given
     * <code>_name</code>.
     *
     * @param _test     related test implementation (where this IEF global
     *                  registry object is defined)
     * @param _name     name of the IEF global registry object
     * @param _revision revision of the IEF global registry object
     */
    public IEFGlobalRegistryData(final AbstractTest _test,
                                 final String _name,
                                 final String _revision)
    {
        super(_test, AbstractTest.CI.IEF_GLOBAL_REGISTRY, _name, _revision);
    }
}
