/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
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
