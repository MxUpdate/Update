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
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.program.MQLProgramData;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.util.PropertyDef;
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
    /**
     * Creates for given <code>_name</code> a new MQL program instance.
     *
     * @param _name     name of the MQL program instance
     * @return MQL program instance
     */
    @Override()
    protected MQLProgramData createNewData(final String _name)
    {
        return new MQLProgramData(this, _name + ".tcl");
    }

    /**
     * Data provider for test MQL programs (with and without extensions).
     *
     * @return object array with all test MQL programs
     */
    @DataProvider(name = "data")
    public Object[][] getMQLPrograms()
    {
        return this.prepareData("mql program",
                new Object[]{
                        "without extension with code",
                        new MQLProgramData(this, "Test1").setCode("test")},
                new Object[]{
                        "without extension without code",
                        new MQLProgramData(this, "Test2").setCode("")},
                new Object[]{
                        "with extension with code",
                        new MQLProgramData(this, "Test3.tcl").setCode("test")},
                new Object[]{
                        "with extension without code",
                        new MQLProgramData(this, "Test4.tcl").setCode("")},
                new Object[]{
                        "with extension and CDATA tag end",
                        new MQLProgramData(this, "Test4.tcl").setCode("<]]>")},
                new Object[]{
                        "with extension and user",
                        new MQLProgramData(this, "Test4.tcl").setCode("test").setUser(new PersonAdminData(this, "Test \"Person\""))},
                new Object[]{
                        "with extension and description",
                        new MQLProgramData(this, "Test4.tcl").setValue("description", "description \"test\"").setCode("test")},
                new Object[]{
                        "with extension and hidden",
                        new MQLProgramData(this, "Test4.tcl").setFlag("hidden", true).setCode("test")},
                new Object[]{
                        "with extension and deferred flag",
                        new MQLProgramData(this, "Test4.tcl").setDeferred(true).setCode("test")},
                new Object[]{
                        "with extension and needs business object flag",
                        new MQLProgramData(this, "Test4.tcl").setNeedsBusinessObject(true).setCode("test")},
                new Object[]{
                        "with extension and downloadable flag",
                        new MQLProgramData(this, "Test4.tcl").setDeferred(true).setDownloadable(true).setCode("test")},
                new Object[]{
                        "with extension and pipe flag",
                        new MQLProgramData(this, "Test4.tcl").setPipe(true).setCode("test")},
                new Object[]{
                        "with extension and pooled flag",
                        new MQLProgramData(this, "Test4.tcl").setPooled(true).setCode("test")});
    }

    /**
     * Cleanups the MX system by deleting the test MQL programs.
     *
     * @throws Exception if cleanup failed
     */
    @BeforeMethod()
    @AfterMethod()
    public void cleanup()
        throws Exception
    {
        this.cleanup(CI.PRG_MQL_PROGRAM);
        this.cleanup(CI.USR_PERSONADMIN);
    }

    /**
     * Tests, if the MQL program within MX is created and registered with the
     * correct symbolic name.
     *
     * @param _description  description of the test data
     * @param _mqlProgram   data collection to test
     * @param _expProgram   expected data collection (not used)
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data",
          description = "test update of non existing MQL programs")
    public void simpleUpdate(final String _description,
                             final MQLProgramData _mqlProgram,
                             final MQLProgramData _expProgram)
        throws Exception
    {
        // create user
        if (_mqlProgram.getUser() != null)  {
            _mqlProgram.getUser().create();
        }
        // create referenced property value
        _mqlProgram.getProperties().createDependings();

        // first update with original content
        _mqlProgram.update((String) null);

        // the replace code (removing TCL update commands)
        final StringBuilder cmd = new StringBuilder()
                .append("escape mod program \"").append(AbstractTest.convertMql(_mqlProgram.getName()))
                .append("\" code \"").append(AbstractTest.convertMql(_mqlProgram.getCode())).append("\"");
        this.mql(cmd);

        // export
        final ExportParser exportParser = _mqlProgram.export();
        _mqlProgram.checkExport(exportParser);

        // set creator as user, 'test' as description, not hidden
        this.mql("escape mod program \"" + AbstractTest.convertMql(_mqlProgram.getName())
                + "\" execute user creator execute immediate !needsbusinessobject !downloadable !pipe !pooled description test !hidden");

        // second update with delivered content
        _mqlProgram.updateWithCode(exportParser.getOrigCode(), (String) null)
                   .checkExport();

        // third update with delivered content (without changing the code)
        _mqlProgram.updateWithCode(exportParser.getOrigCode(), (String) null)
                   .checkExport();

        Assert.assertEquals(
                exportParser.getOrigCode(),
                _mqlProgram.export().getOrigCode(),
                "check that separators correct removed");
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

    /**
     * Check that the code in MX does not include the TCL update code.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the code in MX does not include the TCL update code")
    public void checkUpdateRemovingUpdateCode()
        throws Exception
    {
        final String code = "tcl;\n\neval  {\n  puts test;\n}";
        final MQLProgramData mqlProg = new MQLProgramData(this, "Test")
                .setCode(code)
                .addProperty(new PropertyDef("testprop", "propvalue"))
                .update((String) null, "ProgramTclUpdateRemoveInCode", "true");
        final String progName =  AbstractTest.convertMql(mqlProg.getName());
        Assert.assertEquals(
                this.mql("escape print prog \"" + progName + "\" select property[testprop].value dump"),
                "propvalue",
                "check property is set");
        Assert.assertEquals(
                this.mql("escape print prog \"" + progName + "\" select code dump"),
                code,
                "check the TCL update code is not included in the updated code");
    }

    /**
     * Check that the code in MX does include the TCL update code.
     *
     * @throws Exception if test failed
     */
    @Test(description = "check that the code in MX does include the TCL update code")
    public void checkUpdateNotRemovingUpdateCode()
        throws Exception
    {
        final String code = "tcl;\n\neval  {\n  puts test;\n}";
        final MQLProgramData mqlProg = new MQLProgramData(this, "Test")
                .setCode(code)
                .addProperty(new PropertyDef("testprop", "propvalue"))
                .update((String) null, "ProgramTclUpdateRemoveInCode", "false");
        final String progName =  AbstractTest.convertMql(mqlProg.getName());
        Assert.assertEquals(
                this.mql("escape print prog \"" + progName + "\" select property[testprop].value dump"),
                "propvalue",
                "check property is set");
        Assert.assertEquals(
                this.mql("escape print prog \"" + progName + "\" select code dump"),
                mqlProg.ciFile(),
        "check the TCL update code is not included in the updated code");
    }
}
