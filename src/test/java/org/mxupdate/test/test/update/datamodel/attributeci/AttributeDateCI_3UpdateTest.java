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

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.test.data.datamodel.AttributeDateData;
import org.mxupdate.test.util.IssueLink;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AttributeCI_mxJPO date attribute CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class AttributeDateCI_3UpdateTest
    extends AbstractAttributeWithRangesAndMultiValuesTest<AttributeDateData>
{
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
                "date attribute with defined rangevalue flag 'true'",
                this.createNewData("hello")
                        .setFlag("rangevalue", true)
                        .defNotSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "date attribute with defined rangevalue flag 'false'",
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .defNotSupported(Version.V6R2011x)});
        ret.add(new Object[]{
                "date attribute with no defined rangevalue flag 'false' (to check default value)",
                this.createNewData("hello"),
                this.createNewData("hello")
                        .setFlag("rangevalue", false)
                        .defNotSupported(Version.V6R2011x)});

        return this.prepareData("date attribute", "01/01/01", "02/02/02", ret.toArray(new Object[ret.size()][]));
    }

    /**
     * Negative test that update failed for modified range value flag.
     *
     * @throws Exception if test failed
     */
    @IssueLink("192")
    @Test(description = "issue #192: negative test that update failed for modified range value flag")
    public void negativeTestUpdateRangeValueFlag()
        throws Exception
    {
        new AttributeDateData(this, "test")
                .setFlag("rangevalue", true)
                .create()
                .update((String) null)
                .checkExport()
                .setFlag("rangevalue", false)
                .failureUpdate(ErrorKey.ABSTRACTATTRIBUTE_UPDATE_RANGEVALUEFLAG_UPDATED);
    }

    @Override()
    protected AttributeDateData createNewData(final String _name)
    {
        return new AttributeDateData(this, _name);
    }
}
