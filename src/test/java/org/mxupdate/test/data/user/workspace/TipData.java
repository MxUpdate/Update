/*
 * Copyright 2008-2009 The MxUpdate Team
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

import java.util.HashSet;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.user.AbstractUserData;

/**
 * The class is used to define all tip objects related to used used to
 * create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <USER> class of the related user for which this tip is defined
 */
public class TipData<USER extends AbstractUserData<?>>
    extends AbstractVisualQueryWorkspaceObjectData<TipData<USER>,USER>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
        TipData.REQUIRED_EXPORT_VALUES.add("user");
        TipData.REQUIRED_EXPORT_VALUES.add("appliesto");
        TipData.REQUIRED_EXPORT_VALUES.add("type");
        TipData.REQUIRED_EXPORT_VALUES.add("name");
        TipData.REQUIRED_EXPORT_VALUES.add("revision");
        TipData.REQUIRED_EXPORT_VALUES.add("vault");
        TipData.REQUIRED_EXPORT_VALUES.add("owner");
    }

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this tip is defined
     * @param _name     name of the tip
     */
    public TipData(final AbstractTest _test,
                   final USER _user,
                   final String _name)
    {
        super(_test, "tip", _user, _name, TipData.REQUIRED_EXPORT_VALUES);
        this.setValue("appliesto", "relationship");
    }
}
