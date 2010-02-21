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

package org.mxupdate.test.data.userinterface;

import java.util.HashSet;
import java.util.Set;

import org.mxupdate.test.AbstractTest;

/**
 * Handles test data for commands / menus.
 *
 * @param <T>    related command class
 * @author The MxUpdate Team
 * @version $Id$
 */
abstract class AbstractCommandData<T extends AbstractCommandData<?>>
    extends AbstractUIWithSettingData<T>
{
    /**
     * Within export the description, label and href must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
        AbstractCommandData.REQUIRED_EXPORT_VALUES.add("description");
        AbstractCommandData.REQUIRED_EXPORT_VALUES.add("label");
        AbstractCommandData.REQUIRED_EXPORT_VALUES.add("href");
    }

    /**
     *
     * @param _test         related test implementation (where this command is
     *                      defined)
     * @param _ci           configuration item type
     * @param _name         name of command
     */
    AbstractCommandData(final AbstractTest _test,
                        final AbstractTest.CI _ci,
                        final String _name)
    {
        super(_test, _ci, _name, AbstractCommandData.REQUIRED_EXPORT_VALUES);
    }
}
