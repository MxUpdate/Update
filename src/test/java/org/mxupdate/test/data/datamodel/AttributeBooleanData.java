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

package org.mxupdate.test.data.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;

/**
 * Used to define a boolean attribute, create them and test the result.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class AttributeBooleanData
    extends AbstractAttributeData<AttributeBooleanData>
{
    /**
     * Initialize this boolean attribute with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this attribute is
     *                  defined)
     * @param _name     name of the boolean attribute
     */
    public AttributeBooleanData(final AbstractTest _test,
                            final String _name)
    {
        super(_test, CI.ATTRIBUTE_BOOLEAN, _name, "boolean", "BOOLEAN_");
    }
}
