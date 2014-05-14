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

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.test.data.datamodel.AttributeDateData;
import org.mxupdate.test.util.Version;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the update and export of date attributes.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeDateTest
    extends AbstractAttributeTest<AttributeDateData>
{
    /**
     * Creates for given <code>_name</code> a new date attribute instance.
     *
     * @param _name     name of the attribute instance
     * @return attribute instance
     */
    @Override()
    protected AttributeDateData createNewData(final String _name)
    {
        return new AttributeDateData(this, _name);
    }

    /**
     * Data provider for test date attributes.
     *
     * @return object array with all test attributes
     */
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        final List<Object[]> ret = new ArrayList<Object[]>();

        // range value flag
        ret.add(new Object[]{
                "real attribute with defined rangevalue flag 'true'",
                this.createNewData("hello")
                        .setFlag("rangevalue", true)
                        .notSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "real attribute with defined rangevalue flag 'false'",
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .notSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "real attribute with no defined rangevalue flag 'false' (to check default value)",
                this.createNewData("hello"),
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .notSupported(Version.V6R2011x)});

        return this.prepareData("date attribute", "01/01/01", "02/02/02", ret.toArray(new Object[ret.size()][]));
    }
}
