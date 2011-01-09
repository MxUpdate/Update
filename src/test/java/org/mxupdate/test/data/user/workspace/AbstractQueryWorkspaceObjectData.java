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

package org.mxupdate.test.data.user.workspace;

import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AbstractUserData;

/**
 * The class is used to define all common things for query workspace objects
 * related to users used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <DATA> workspace object class
 * @param <USER> for which user class the workspace object class is defined
 */
abstract class AbstractQueryWorkspaceObjectData<DATA extends AbstractQueryWorkspaceObjectData<?,USER>, USER extends AbstractUserData<?>>
    extends AbstractWorkspaceObjectData<DATA,USER>
{
    /**
     * Default constructor.
     *
     * @param _test                 related test case
     * @param _mxAdminType          MX administration type of the visual query
     *                              workspace object
     * @param _user                 user for which this visual query workspace
     *                              object is defined
     * @param _name                 name of the visual query workspace object
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    AbstractQueryWorkspaceObjectData(final AbstractTest _test,
                                     final String _mxAdminType,
                                     final USER _user,
                                     final String _name,
                                     final Map<String,String> _requiredExportValues)
    {
        super(_test, _mxAdminType, _user, _name, _requiredExportValues);
        this.setValue("type", "*");
        this.setValue("name", "*");
        this.setValue("revision", "*");
        this.setValue("vault", "*");
        this.setValue("owner", "*");
    }
}
