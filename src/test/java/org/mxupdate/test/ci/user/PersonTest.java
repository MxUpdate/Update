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

package org.mxupdate.test.ci.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.PersonData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test cases for the export and update of persons (wit related business
 * object).
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
@Test()
public class PersonTest
    extends AbstractPersonAdminTest<PersonData>
{
    /**
     * Creates for given <code>_name</code> a new person instance.
     *
     * @param _name     name of the person instance
     * @return person instance
     */
    @Override()
    protected PersonData createNewData(final String _name)
    {
        return new PersonData(this, _name);
    }

    /**
     * Data provider for test persons.
     *
     * @return object array with all test persons
     */
    @Override()
    @DataProvider(name = "data")
    public Object[][] getPersons()
    {
        final List<Object[]> data = new ArrayList<Object[]>();
        data.addAll(Arrays.asList(super.getPersons()));

        data.add(new Object[]{
                "person with promote to active",
                this.createNewData("hello test")
                        .setState("Active")});

        data.add(new Object[]{
                "person with no promote",
                this.createNewData("hello test")
                        .setState("Inactive")});

        return data.toArray(new Object[data.size()][]);
    }

    /**
     * {@inheritDoc}
     * Also the person business object's are cleaned / deleted.
     */
    @Override()
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSON);
        super.cleanup();
    }

    /**
     * Checks that that the state for persons are not updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the state for persons are not updated")
    public void checkIgnoredUpdateForState()
        throws Exception
    {
        new PersonData(this, "test")
                .setState("Active")
                .create()
                .setState("Inactive")
                .update("UserPersonIgnoreState", "*")
                .setState("Active")
                .checkExport();
    }

    /**
     * Checks that that the state for persons are not exported.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the state for persons are not exported")
    public void checkIgnoredExportForState()
        throws Exception
    {
        final PersonData person = new PersonData(this, "test")
                .setState("Active")
                .create();

        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonIgnoreState", "*");
        Assert.assertTrue(
                exportParser.getLines("/setState/@value").isEmpty(),
                "check that state definition is in the export file");
    }
}
