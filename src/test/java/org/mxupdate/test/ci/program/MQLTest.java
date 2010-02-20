/*
 * Copyright 2008-2010 The MxUpdate Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
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
 * @version $Id$
 */
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
    @DataProvider(name = "mqlprograms")
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
                        new MQLProgramData(this, "Test4.tcl").setHidden(true).setCode("test")},
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
     * Checks that the new created MQL progam is exported correctly.
     *
     * @param _description  description of the test data
     * @param _mqlProgram   data collection to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "mqlprograms", description = "test export of MQL programs")
    public void simpleExport(final String _description,
                             final MQLProgramData _mqlProgram)
        throws Exception
    {
        _mqlProgram.create();
        final ExportParser exportParser = _mqlProgram.export();
        // to be sure the check works correct...
        _mqlProgram.checkExport(exportParser);

        // check MQL code
        Assert.assertEquals(this.mql("escape print program \"" + AbstractTest.convertMql(_mqlProgram.getName()) + "\" select code dump"),
                            _mqlProgram.getCode(),
                            "checks MQL program code");
    }

    /**
     * Tests, if the MQL program within MX is created and registered with the
     * correct symbolic name.
     *
     * @param _description  description of the test data
     * @param _mqlProgram   data collection to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "mqlprograms", description = "test update of non existing MQL programs")
    public void simpleUpdate(final String _description,
                             final MQLProgramData _mqlProgram)
        throws Exception
    {
        // create user
        if (_mqlProgram.getUser() != null)  {
            _mqlProgram.getUser().create();
        }
        // create referenced property value
        for (final PropertyDef prop : _mqlProgram.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }

        // first update with original content
        _mqlProgram.update();

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
        this.update(_mqlProgram.getCIFileName(), exportParser.getOrigCode());
        _mqlProgram.checkExport(_mqlProgram.export());

        // third update with delivered content (without changing the code)
        this.update(_mqlProgram.getCIFileName(), exportParser.getOrigCode());
        _mqlProgram.checkExport(_mqlProgram.export());

        Assert.assertEquals(exportParser.getOrigCode(),
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
}
