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

package org.mxupdate.test.test.update.datamodel.attributeci;

import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AttributeCI_mxJPO boolean attribute CI}
 * export / update.
 *
 * @author The MxUpdate Team
 */
@Test
public class AttributeBooleanCI_3UpdateTest
    extends Abstract_3UpdateWithRangesAndMultiValuesTest
{
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        return this.prepareData("boolean attribute", "TRUE", "FALSE");
    }

    @Override
    protected String getKind()
    {
        return "boolean";
    }
}
