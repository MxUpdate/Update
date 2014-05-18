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

package org.mxupdate.test.data.datamodel;

import java.util.HashMap;
import java.util.Map;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;

/**
 * Used to define a string attribute, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class AttributeStringData
    extends AbstractAttributeData<AttributeStringData>
{
    /**
     * Within export the max length must be defined.
     */
    private static final Map<String,Object> REQUIRED_EXPORT_VALUES = new HashMap<String,Object>();
    static  {
        AttributeStringData.REQUIRED_EXPORT_VALUES.put("maxlength", 0);
    }

    /**
     * Within export the multiline flag must be defined.
     */
    private static final Map<String,Boolean> REQUIRED_EXPORT_FLAGS = new HashMap<String,Boolean>();
    static  {
        AttributeStringData.REQUIRED_EXPORT_FLAGS.put("multiline", false);
    }

    /**
     * Initialize this string attribute with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this attribute is
     *                  defined)
     * @param _name     name of the string attribute
     */
    public AttributeStringData(final AbstractTest _test,
                               final String _name)
    {
        super(_test, CI.DM_ATTRIBUTE_STRING, _name, "string",
              AttributeStringData.REQUIRED_EXPORT_VALUES,
              AttributeStringData.REQUIRED_EXPORT_FLAGS);
    }
}
