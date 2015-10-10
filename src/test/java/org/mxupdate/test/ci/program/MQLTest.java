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

package org.mxupdate.test.ci.program;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.program.MQLProgramData;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of MQL programs.
 *
 * @author The MxUpdate Team
 */
@Test()
public class MQLTest
    extends AbstractDataExportUpdate<MQLProgramData>
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return this.prepareData("mql program",
                new Object[]{
                        "without extension with code",
                        new MQLProgramData(this, "Test1").setValue("code", "test")},
                new Object[]{
                        "with extension with code",
                        new MQLProgramData(this, "Test3.tcl").setValue("code", "test")},
                new Object[]{
                        "with extension and CDATA tag end",
                        new MQLProgramData(this, "Test4.tcl").setValue("code", "<]]>")},
                new Object[]{
                        "with extension and description",
                        new MQLProgramData(this, "Test4.tcl").setValue("description", "description \"test\"").setValue("code", "test")},
                new Object[]{
                        "with extension and hidden",
                        new MQLProgramData(this, "Test4.tcl").setFlag("hidden", true).setValue("code", "test")},
                new Object[]{
                        "with extension and needs business object flag",
                        new MQLProgramData(this, "Test4.tcl").setFlag("needsbusinessobject", true).setValue("code", "test")},
                new Object[]{
                        "with extension and pipe flag",
                        new MQLProgramData(this, "Test4.tcl").setFlag("pipe", true).setValue("code", "test")},
                new Object[]{
                        "with extension and pooled flag",
                        new MQLProgramData(this, "Test4.tcl").setFlag("pooled", true).setValue("code", "test")});
    }

    /**
     * Check that the end tag of CDATA is correct translated to
     * 'Inserted_by_ENOVIA'.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the end tag of CDATA is correct translated to 'Inserted_by_ENOVIA'")
    public void checkCDataTranslation()
        throws Exception
    {
        final String name = AbstractTest.PREFIX + "_Test";
        this.mql("add prog " + name + " mql code '<]]>'");
        final String xml = this.mql("export prog " + name + " xml");
        Assert.assertTrue(xml.indexOf("<code><![CDATA[<]Inserted_by_ENOVIA]Inserted_by_ENOVIA>]]></code>") >= 0,
                          "check translation of the CDATA conversion");
    }

    @Override()
    protected MQLProgramData createNewData(final String _name)
    {
        return new MQLProgramData(this, _name + ".tcl");
    }

    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.PRG_MQL);
        this.cleanup(CI.USR_PERSON);
    }
}
