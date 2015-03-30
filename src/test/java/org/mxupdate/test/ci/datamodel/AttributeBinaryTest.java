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

package org.mxupdate.test.ci.datamodel;

import org.mxupdate.test.data.datamodel.AttributeBinaryData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the update and export of binary attributes.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeBinaryTest
    extends AbstractAttributeTest<AttributeBinaryData>
{
    /**
     * Creates for given {@code _name} a new binary attribute instance.
     *
     * @param _name     name of the attribute instance
     * @return attribute instance
     */
    @Override()
    protected AttributeBinaryData createNewData(final String _name)
    {
        return new AttributeBinaryData(this, _name);
    }

    /**
     * Data provider for test binary attributes.
     *
     * @return object array with all test binary attributes
     */
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        return this.prepareData("binary attribute", "TRUE", "FALSE");
    }
}
