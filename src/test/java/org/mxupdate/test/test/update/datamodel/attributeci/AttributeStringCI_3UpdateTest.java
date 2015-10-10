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

import org.mxupdate.test.data.datamodel.AttributeStringData;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link AttributeCI_mxJPO string attribute CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test
public class AttributeStringCI_3UpdateTest
    extends Abstract_3UpdateWithRangesAndMultiValuesTest<AttributeStringData>
{
    /**
     * Data provider for test string attributes.
     *
     * @return object array with all test string attributes
     */
    @DataProvider(name = "data")
    public Object[][] getAttributes()
    {
        final List<Object[]> ret = new ArrayList<>();

        // max length property
        ret.add(new Object[]{
                "string attribute with max length 0",
                this.createNewData("hello")
                        .setSingle("maxlength", "0")});
        ret.add(new Object[]{
                "string attribute with max length 5",
                this.createNewData("hello")
                        .setSingle("maxlength", "5")});

        // multiline flag
        ret.add(new Object[]{
                "string attribute with multiline flag true",
                this.createNewData("hello")
                        .setFlag("multiline", true)});
        ret.add(new Object[]{
                "string attribute with multiline flag false",
                this.createNewData("hello")
                        .setFlag("multiline", false)});

        return super.prepareData("string attribute", "A \" B", "BCD", ret.toArray(new Object[ret.size()][]));
    }

    @Override
    protected AttributeStringData createNewData(final String _name)
    {
        return new AttributeStringData(this, _name).setSingle("kind", this.getKind());
    }

    @Override
    protected String getKind()
    {
        return "string";
    }
}
