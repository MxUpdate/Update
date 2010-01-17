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

package org.mxupdate.test.data.integration;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;
import org.mxupdate.test.data.datamodel.TypeData;

/**
 * Used to define an integration EBOM sync configuration, create them and test
 * the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class IEFEBOMSyncConfigData
    extends AbstractBusData<IEFEBOMSyncConfigData>
{
    /**
     * Initialize this IEF EBOM sync configuration object with given
     * <code>_type</code>, <code>_name</code> and <code>_revision</code>.
     *
     * @param _test     related test implementation (where this IEF global
     *                  configuration object is defined)
     * @param _type     derived type from <code>IEF-EBOMSyncConfig</code>
     * @param _name     name of the IEF EBOM sync configuration object
     * @param _revision revision of the IEF EBOM syncl configuration object
     */
    public IEFEBOMSyncConfigData(final AbstractTest _test,
                                 final TypeData _type,
                                 final String _name,
                                 final String _revision)
    {
        super(_test, AbstractTest.CI.IEF_EBOMSYNC_CONFIG, _type, _name, _revision);
    }
}
