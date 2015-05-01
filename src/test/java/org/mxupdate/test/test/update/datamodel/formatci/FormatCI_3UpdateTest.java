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

package org.mxupdate.test.test.update.datamodel.formatci;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.data.datamodel.FormatData;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.datamodel.Format_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Format_mxJPO format CI} export / update.
 *
 * @author The MxUpdate Team
 */
@Test()
public class FormatCI_3UpdateTest
    extends AbstractDataExportUpdate<FormatData>
{
    /**
     * Data provider for test formats.
     *
     * @return object array with all test formats
     */
    @DataProvider(name = "data")
    public Object[][] getFormats()
    {
        return this.prepareData("format",
                new Object[]{
                        "format without anything (to test required fields)",
                        new FormatData(this, "hello \" test")},
                new Object[]{
                        "format with other symbolic name",
                        new FormatData(this, "hello \" test")
                                .setSymbolicName("format_Test")},
                new Object[]{
                        "format with description",
                        new FormatData(this, "hello \" test")
                                .setValue("description", "complex description \"test\"")},
                new Object[]{
                        "format with version",
                        new FormatData(this, "hello \" test")
                                .setValue("version", "version \"test\"")},
                new Object[]{
                        "format with suffix",
                        new FormatData(this, "hello \" test")
                                .setValue("suffix", "suffix \"test\"")},
                new Object[]{
                        "format with mime",
                        new FormatData(this, "hello \" test")
                                .setValue("mime", "mime \"test\"")},
                new Object[]{
                        "format with type",
                        new FormatData(this, "hello \" test")
                                .setValue("type", "type \"test\"")},
                new Object[]{
                        "issue #212: format with view program",
                        new FormatData(this, "hello \" test")
                                .defNotSupported(Version.V6R2014x, Version.V6R2015x)
                                .setViewProgram(new MQLProgramData(this, "ViewProgram"))},
                new Object[]{
                        "issue #212: format with view program",
                        new FormatData(this, "hello \" test")
                                .defNotSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x)
                                .setViewProgram(new MQLProgramData(this, "ViewProgram")),
                        new FormatData(this, "hello \" test"),
                        "[INFO]     - view program MXUPDATE_ViewProgram ignored (not supported anymore!)"},
                new Object[]{
                        "issue #212: format with edit program",
                        new FormatData(this, "hello \" test")
                                .defNotSupported(Version.V6R2014x, Version.V6R2015x)
                                .setEditProgram(new MQLProgramData(this, "EditProgram"))},
                new Object[]{
                        "issue #212: format with edit program",
                        new FormatData(this, "hello \" test")
                                .defNotSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x)
                                .setEditProgram(new MQLProgramData(this, "EditProgram")),
                        new FormatData(this, "hello \" test"),
                        "[INFO]     - edit program MXUPDATE_EditProgram ignored (not supported anymore!)"},
                new Object[]{
                        "issue #212: format with print program",
                        new FormatData(this, "hello \" test")
                                .defNotSupported(Version.V6R2014x, Version.V6R2015x)
                                .setPrintProgram(new MQLProgramData(this, "PrintProgram"))},
                new Object[]{
                        "issue #212: format with print program",
                        new FormatData(this, "hello \" test")
                                .defNotSupported(Version.V6R2011x, Version.V6R2012x, Version.V6R2013x)
                                .setPrintProgram(new MQLProgramData(this, "PrintProgram")),
                        new FormatData(this, "hello \" test"),
                        "[INFO]     - print program MXUPDATE_PrintProgram ignored (not supported anymore!)"}
        );
    }

    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.DM_FORMAT);
        this.cleanup(CI.PRG_MQL);
    }

    @Override()
    protected FormatData createNewData(final String _name)
    {
        return new FormatData(this, _name);
    }
}
