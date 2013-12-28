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

package org.mxupdate.test.ci.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.user.PersonData;
import org.mxupdate.test.data.user.organization.BusinessUnitData;
import org.mxupdate.test.data.user.organization.CompanyData;
import org.mxupdate.test.data.user.organization.DepartmentData;
import org.mxupdate.test.data.user.organization.PlantData;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.util.Version;
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
     * {@inheritDoc}
     * <p><b>Hint:</b> In V6R2011x the icon mail is NOT automatically
     * deactivated from user agent if the person is disabled.</p>
     */
    @Override()
    protected PersonData createCleanNewData(final PersonData _original)
    {
        final PersonData ret = super.createCleanNewData(_original);

        if (this.getVersion() != Version.V6R2011x)  {
            ret.setWantsIconMail(false);
        }

        return ret;
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

        data.add(new Object[]{
                "person with promote to active",
                this.createNewData("hello test")
                        .setState("Active")});

        data.add(new Object[]{
                "person with no promote",
                this.createNewData("hello test")
                        .setState("Inactive")});

        // employee of
        final CompanyData compData = new CompanyData(this, "test company").setValue("Organization ID", "1111").setValue("Primary Key", "1111");
        data.add(new Object[]{
                "person employee of a company",
                this.createNewData("test")
                        .addEmployeeOf(compData)
                        .addMemberOf(compData, "Project Member", "")
                        .addProperty(new PropertyDef("Company Key", "1111"))
                        .setState("Active")});
        data.add(new Object[]{
                "person employee of a department",
                this.createNewData("test")
                        .addEmployeeOf(
                                new DepartmentData(this, "test 'department'"))
                        .addProperty(new PropertyDef("Company Key"))
                        .setState("Active")});

        // representative of
        data.add(new Object[]{
                "person representative of a company and department",
                this.createNewData("test")
                        .addRepresentativeOf(
                                new CompanyData(this, "test company"),
                                new DepartmentData(this, "test 'department'"))
                        .setState("Active")});
        data.add(new Object[]{
                "person representative of a business unit and plant",
                this.createNewData("test")
                        .addRepresentativeOf(
                                new BusinessUnitData(this, "test business unit"),
                                new PlantData(this, "test 'plant'"))
                        .setState("Active")});

        // member of
        data.add(new Object[]{
                "person member of a company and department",
                this.createNewData("test")
                        .addMemberOf(new CompanyData(this, "test company"), "Project Member", "role_Employee")
                        .addMemberOf(new DepartmentData(this, "test 'department'"), "Project Member", "role_Employee")
                        .setState("Active")});


        final List<Object[]> ret = new ArrayList<Object[]>();
        ret.addAll(Arrays.asList(super.getPersons()));
        ret.addAll(Arrays.asList(super.prepareData((String) null, data.toArray(new Object[data.size()][]))));

        return ret.toArray(new Object[ret.size()][]);
    }

    /**
     * {@inheritDoc}
     * Also the person business object's and dependings are cleaned / deleted.
     */
    @Override()
    @BeforeMethod()
    @AfterClass()
    public void cleanup()
        throws MatrixException
    {
        try  {
            this.mql("trigger off");
            this.cleanup(AbstractTest.CI.USR_PERSON);
        } finally  {
            this.mql("trigger on");
        }
        this.cleanup(AbstractTest.CI.OTHER_COMPANY);
        this.cleanup(AbstractTest.CI.OTHER_BUSINESSUNIT);
        this.cleanup(AbstractTest.CI.OTHER_DEPARTMENT);
        this.cleanup(AbstractTest.CI.OTHER_PLANT);
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
                "check that state definition is not in the export file");
    }

    /**
     * Checks that that the employee connection for persons are not
     * updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the employee connection for persons are not updated")
    public void checkIgnoredUpdateForEmployee()
        throws Exception
    {
        final CompanyData company = new CompanyData(this, "test company");
        new PersonData(this, "test")
                .addEmployeeOf(company)
                .create()
                .removeEmployeeOf(company)
                .update("UserPersonEmployeeIgnore", "*")
                .addEmployeeOf(company)
                .checkExport();
    }


    /**
     * Checks that that the employee connection for persons are not
     * exported.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the employee connection for persons are not exported")
    public void checkIgnoredExportEmployee()
        throws Exception
    {
        final PersonData person = new PersonData(this, "test")
                .addEmployeeOf(new CompanyData(this, "test company"))
                .create();

        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonEmployeeIgnore", "*");
        Assert.assertTrue(
                exportParser.getLines("/setEmployeeOf/@value").isEmpty(),
                "check that employee definition is not in the export file");
    }

    /**
     * Checks that that the representative connection for persons are not
     * updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the representative connection for persons are not updated")
    public void checkIgnoredUpdateForMember()
        throws Exception
    {
        final CompanyData company = new CompanyData(this, "test company");
        new PersonData(this, "test")
                .addMemberOf(company, "Project Member", "")
                .create()
                .removeMemberOf(company)
                .update("UserPersonMemberIgnore", "*")
                .addMemberOf(company, "Project Member", "")
                .checkExport();
    }

    /**
     * Checks that that the member connection for persons are not
     * exported.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the member connection for persons are not exported")
    public void checkIgnoredExportMember()
        throws Exception
    {
        final PersonData person = new PersonData(this, "test")
                .addMemberOf(new CompanyData(this, "test company"), "Project Member", "")
                .create();

        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonMemberIgnore", "*");
        Assert.assertTrue(
                exportParser.getLines("/setMemberOf/@value").isEmpty(),
                "check that member definition is not in the export file");
    }

    /**
     * Checks that that the representative connection for persons are not
     * updated.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the representative connection for persons are not updated")
    public void checkIgnoredUpdateForRepresentative()
        throws Exception
    {
        final CompanyData company = new CompanyData(this, "test company");
        new PersonData(this, "test")
                .addRepresentativeOf(company)
                .create()
                .removeRepresentativeOf(company)
                .update("UserPersonRepresentativeIgnore", "*")
                .addRepresentativeOf(company)
                .checkExport();
    }


    /**
     * Checks that that the representative connection for persons are not
     * exported.
     *
     * @throws Exception if test failed
     */
    @Test(description = "checks that that the representative connection for persons are not exported")
    public void checkIgnoredExportRepresentative()
        throws Exception
    {
        final PersonData person = new PersonData(this, "test")
                .addRepresentativeOf(new CompanyData(this, "test company"))
                .create();

        // check not defined within update file
        final ExportParser exportParser = person.export("UserPersonRepresentativeIgnore", "*");
        Assert.assertTrue(
                exportParser.getLines("/setRepresentativeOf/@value").isEmpty(),
                "check that representative definition is not in the export file");
    }
}
