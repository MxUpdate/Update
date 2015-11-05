/*
 * Copyright 2008-2015 The MxUpdate Team
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
 */

package org.mxupdate.test.test.update.user;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.system.ApplicationData;
import org.mxupdate.test.data.system.PackageData;
import org.mxupdate.test.data.system.SiteData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.PersonData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.user.PersonCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link PersonCI_mxJPO person admin CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test
public class PersonCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<PersonCI_mxJPO,PersonData>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0) simple",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test")},
            // package
            {"1a) new package",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").defData("package", new PackageData(this, "TestPackage"))},
            {"1b) update package",
                    new PersonData(this, "Test").defData("package", new PackageData(this, "TestPackage1")),
                    new PersonData(this, "Test").defData("package", new PackageData(this, "TestPackage2"))},
            {"1c) remove package",
                    new PersonData(this, "Test").defData("package", new PackageData(this, "TestPackage")),
                    new PersonData(this, "Test").defKeyNotDefined("package")},
            // uuid
            {"2) uuid",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
            // symbolic names
            {"3a) symbolic name",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("symbolicname", "expression_123")},
            {"3b) two symbolic name",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
            // comment
            {"4a) add comment",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("comment", "Descrption")},
            {"4b) update comment",
                    new PersonData(this, "Test").setValue("comment", "Descrption"),
                    new PersonData(this, "Test").setValue("comment", "Description")},
            {"4c) remove comment",
                    new PersonData(this, "Test").setValue("comment", "Descrption"),
                    new PersonData(this, "Test")},
            // hidden
            {"5a) hidden true",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setFlag("hidden", true)},
            {"5b) hidden false",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setFlag("hidden", false)},
            // active flag
            {"6a) set active",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setFlag("active", true)},
            {"6b) set inactive",
                    new PersonData(this, "Test").setFlag("active", true),
                    new PersonData(this, "Test").setFlag("active", false)},
            {"7a) set trusted",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setFlag("trusted", false)},
            {"7b) set untrusted",
                    new PersonData(this, "Test").setFlag("active", true),
                    new PersonData(this, "Test").setFlag("active", false)},
            {"8a) access all",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setSingle("access", "all")},
            {"8b) access list",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setSingle("access", "{grant promote}")},
            {"8c) access list change",
                    new PersonData(this, "Test").setSingle("access", "{grant modify promote}"),
                    new PersonData(this, "Test").setSingle("access", "{grant promote}")},
            {"9a) admin all",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "all")},
            {"9b) admin list",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
            {"9c) admin list change",
                    new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form group rule}"),
                    new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
            {"10a) email true",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setFlag("email", true)},
            {"10b) email false",
                    new PersonData(this, "Test").setFlag("email", true),
                    new PersonData(this, "Test").setFlag("email", false)},
            {"11a) iconmail true",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setFlag("iconmail", true)},
            {"11b) iconmail false",
                    new PersonData(this, "Test").setFlag("iconmail", true),
                    new PersonData(this, "Test").setFlag("iconmail", false)},
            {"12a) add address",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("address", "ADRESS")},
            {"12b) update address",
                    new PersonData(this, "Test").setValue("address", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("address", "ADRESS")},
            {"12c) remove address",
                    new PersonData(this, "Test").setValue("address", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("address", "")},
            {"13a) add emailaddress",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("emailaddress", "ADRESS")},
            {"13b) update emailaddress",
                    new PersonData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("emailaddress", "ADRESS")},
            {"13c) remove emailaddress",
                    new PersonData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("emailaddress", "")},
            {"14a) add fax",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("fax", "ADRESS")},
            {"14b) update fax",
                    new PersonData(this, "Test").setValue("fax", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("fax", "ADRESS")},
            {"14c) remove fax",
                    new PersonData(this, "Test").setValue("fax", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("fax", "")},
            {"15a) add fullname",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("fullname", "ADRESS")},
            {"15b) update fullname",
                    new PersonData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("fullname", "ADRESS")},
            {"15c) remove fullname",
                    new PersonData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("fullname", "")},
            {"16a) add phone",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("phone", "ADRESS")},
            {"16b) update phone",
                    new PersonData(this, "Test").setValue("phone", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("phone", "ADRESS")},
            {"16c) remove phone",
                    new PersonData(this, "Test").setValue("phone", "CURRENTADRESS"),
                    new PersonData(this, "Test").setValue("phone", "")},
            {"17a) add product (max. V&R2013x)",
                    new PersonData(this, "Test").defMaxSupported(Version.V6R2013x),
                    new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{ZKC}")},
            {"17b) update product (max. V&R2013x)",
                    new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC ZCS}"),
                    new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC}")},
            {"17c) remove product (max. V&R2013x)",
                    new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP}"),
                    new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{}")},
            {"17d) add product (min. V&R2014x)",
                    new PersonData(this, "Test").defMinSupported(Version.V6R2014x),
                    new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}")},
            {"17e) update product (min. V&R2014x)",
                    new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV IFW}"),
                    new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV}")},
            {"17f) remove product (min. V&R2014x)",
                    new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}"),
                    new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{}")},
            {"18a) add type",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setSingle("type", "{application}")},
            {"18b) update type",
                    new PersonData(this, "Test").setSingle("type", "{application}"),
                    new PersonData(this, "Test").setSingle("type", "{application full}")},
            {"18c) remove type",
                    new PersonData(this, "Test").setSingle("admin", "all").setSingle("type", "{system}"),
                    new PersonData(this, "Test").setSingle("type", "{application}")},
            {"19a) add vault",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").setValue("vault", "eService Administration")},
            {"19b) update vault",
                    new PersonData(this, "Test").setValue("vault", "eService Administration"),
                    new PersonData(this, "Test").setValue("vault", "eService Production")},
            {"19c) remove vault",
                    new PersonData(this, "Test").setValue("vault", "eService Administration"),
                    new PersonData(this, "Test")},
            {"20a) add application",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").defData("application", new ApplicationData(this, "test"))},
            {"20b) update application",
                    new PersonData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                    new PersonData(this, "Test").defData("application", new ApplicationData(this, "test2"))},
            {"20c) remove application",
                    new PersonData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                    new PersonData(this, "Test")},
            {"21a) add site",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").defData("site", new SiteData(this,"test"))},
            {"21b) update site",
                    new PersonData(this, "Test").defData("site", new SiteData(this,"test1")),
                    new PersonData(this, "Test").defData("site", new SiteData(this,"test2"))},
            {"21c) remove site",
                    new PersonData(this, "Test").defData("site", new SiteData(this,"test")),
                    new PersonData(this, "Test")},
            // group
            {"22a) add group",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").defData("group", new GroupData(this, "test \"group\""))},
            {"22b) replace group",
                    new PersonData(this, "Test").defData("group", new GroupData(this, "group")),
                    new PersonData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2"))},
            {"22c) remove group",
                    new PersonData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2")),
                    new PersonData(this, "Test")},
            // role
            {"23a) add role",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test").defData("role", new RoleData(this, "test \"role\""))},
            {"23b) replace role",
                    new PersonData(this, "Test").defData("role", new RoleData(this, "role")),
                    new PersonData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2"))},
            {"23c) remove role",
                    new PersonData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2")),
                    new PersonData(this, "Test")},
            // group and roles
            {"24) person with two groups and roles",
                    new PersonData(this, "Test"),
                    new PersonData(this, "Test")
                            .defData("group", new GroupData(this, "test \"group\" 1"))
                            .defData("group", new GroupData(this, "test \"group\" 2"))
                            .defData("role", new RoleData(this, "test \"role\" 1"))
                            .defData("role", new RoleData(this, "test \"role\" 2"))},
        };
    }

    @Override
    @BeforeMethod
    @AfterClass
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSON);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.SYS_APPLICATION);
        this.cleanup(AbstractTest.CI.SYS_SITE);
        this.cleanup(AbstractTest.CI.SYS_PACKAGE);
    }

    @Override
    protected PersonCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                           final String _name)
    {
        return new PersonCI_mxJPO(_name);
    }
}
