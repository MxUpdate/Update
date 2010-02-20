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

package org.mxupdate.test.ci.userinterface;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractDataExportUpdate;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.AbstractUserData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.data.userinterface.FieldData;
import org.mxupdate.test.data.userinterface.FormData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export / update of web forms.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class FormTest
    extends AbstractDataExportUpdate<FormData>
{
    /**
     * Creates for given <code>_name</code> a new form instance.
     *
     * @param _name     name of the form instance
     * @return form instance
     */
    @Override()
    protected FormData createNewData(final String _name)
    {
        return new FormData(this, _name);
    }

    /**
     * Data provider for test forms.
     *
     * @return object array with all test forms
     */
    @DataProvider(name = "forms")
    public Object[][] getForms()
    {
        return this.prepareData("form",
                new Object[]{
                        "form without anything (to test required fields)",
                        new FormData(this, "hello \" test")},
                new Object[]{
                        "simple form",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")},
                new Object[]{
                        "simple form with two fields",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field \"1\"").getFormTable()
                            .newField("field \"2\"").getFormTable()},
                new Object[]{
                        "simple form with complex field",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field")
                                    .setValue("label", "an \"label\"")
                                    .setValue("range", "an \"range\"")
                                    .setValue("href", "an \"url\"")
                                    .setValue("alt", "an \"alt\"")
                                    .setValue("update", "an \"alt\"")
                                    .setSetting("first \"key\"", "first \"value\"")
                                    .setSetting("second \"key\"", "second \"value\"")
                                    .getFormTable()},
                new Object[]{
                        "form with business object select",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field")
                                    .setValue("businessobject", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "form with relationship select",
                        new FormData(this, "hello \" test")
                            .setValue("description", "\"\\\\ hello")
                            .newField("field")
                                    .setValue("relationship", "select \"expression\"")
                                    .getFormTable()},
                new Object[]{
                        "form with one role and one group",
                        new FormData(this, "hello \" test")
                            .newField("field")
                                    .addUser(new RoleData(this, "user \"role\""))
                                    .addUser(new GroupData(this, "user \"group\""))
                                    .getFormTable()}
        );
    }

    /**
     * Cleanup all test web forms.
     *
     * @throws MatrixException if cleanup failed
     */
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.UI_FORM);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.USR_GROUP);
    }


    /**
     * Tests a new created form and the related export.
     *
     * @param _description  description of the test case
     * @param _form         form to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "forms", description = "test export of new created form")
    public void simpleExport(final String _description,
                             final FormData _form)
        throws Exception
    {
        _form.create();
        _form.checkExport(_form.export());
    }

    /**
     * Tests an update of non existing form. The result is tested with by
     * exporting the form and checking the result.
     *
     * @param _description  description of the test case
     * @param _form         form to test
     * @throws Exception if test failed
     */
    @Test(dataProvider = "forms", description = "test update of non existing form")
    public void simpleUpdate(final String _description,
                             final FormData _form)
        throws Exception
    {
        // create users
        for (final FieldData<FormData> field : _form.getFields())  {
            for (final AbstractUserData<?> user : field.getUsers())  {
                user.create();
            }
        }
        // create referenced property value
        for (final PropertyDef prop : _form.getProperties())  {
            if (prop.getTo() != null)  {
                prop.getTo().create();
            }
        }

        // first update with original content
        _form.update();
        final ExportParser exportParser = _form.export();
        _form.checkExport(exportParser);

        // second update with delivered content
        this.update(_form.getCIFileName(), exportParser.getOrigCode());
        _form.checkExport(_form.export());
    }
}
