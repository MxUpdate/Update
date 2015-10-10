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
import org.mxupdate.test.data.system.SiteData;
import org.mxupdate.test.data.user.GroupData;
import org.mxupdate.test.data.user.PersonAdminData;
import org.mxupdate.test.data.user.RoleData;
import org.mxupdate.test.test.update.AbstractDeltaCalculationTest;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.user.PersonAdmin_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import matrix.util.MatrixException;

/**
 * Tests the {@link PersonAdmin_mxJPO person admin CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PersonAdminCI_2DeltaCalculationTest
    extends AbstractDeltaCalculationTest<PersonAdmin_mxJPO, PersonAdminData>
{

    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
                {"0) simple",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test")},
                {"1a) symbolic name",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("symbolicname", "expression_123")},
                {"1b) two symbolic name",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
                // comment
                {"2a) add comment",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("comment", "Descrption")},
                {"2b) update comment",
                        new PersonAdminData(this, "Test").setValue("comment", "Descrption"),
                        new PersonAdminData(this, "Test").setValue("comment", "Description")},
                {"2c) remove comment",
                        new PersonAdminData(this, "Test").setValue("comment", "Descrption"),
                        new PersonAdminData(this, "Test")},
                // hidden
                {"3a) hidden true",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("hidden", true)},
                {"3b) hidden false",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("hidden", false)},
                // active flag
                {"4a) set active",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("active", true)},
                {"4b) set inactive",
                        new PersonAdminData(this, "Test").setFlag("active", true),
                        new PersonAdminData(this, "Test").setFlag("active", false)},
                {"5a) set trusted",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("trusted", false)},
                {"5b) set untrusted",
                        new PersonAdminData(this, "Test").setFlag("active", true),
                        new PersonAdminData(this, "Test").setFlag("active", false)},
                {"6a) access all",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("access", "all")},
                {"6b) access list",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("access", "{grant promote}")},
                {"6c) access list change",
                        new PersonAdminData(this, "Test").setSingle("access", "{grant modify promote}"),
                        new PersonAdminData(this, "Test").setSingle("access", "{grant promote}")},
                {"7a) admin all",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "all")},
                {"7b) admin list",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
                {"7c) admin list change",
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form group rule}"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
                {"8a) email true",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("email", true)},
                {"8b) email false",
                        new PersonAdminData(this, "Test").setFlag("email", true),
                        new PersonAdminData(this, "Test").setFlag("email", false)},
                {"9a) iconmail true",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("iconmail", true)},
                {"9b) iconmail false",
                        new PersonAdminData(this, "Test").setFlag("iconmail", true),
                        new PersonAdminData(this, "Test").setFlag("iconmail", false)},
                {"10a) add address",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("address", "ADRESS")},
                {"10b) update address",
                        new PersonAdminData(this, "Test").setValue("address", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("address", "ADRESS")},
                {"10c) remove address",
                        new PersonAdminData(this, "Test").setValue("address", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("address", "")},
                {"11a) add emailaddress",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("emailaddress", "ADRESS")},
                {"11b) update emailaddress",
                        new PersonAdminData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("emailaddress", "ADRESS")},
                {"11c) remove emailaddress",
                        new PersonAdminData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("emailaddress", "")},
                {"12a) add fax",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("fax", "ADRESS")},
                {"12b) update fax",
                        new PersonAdminData(this, "Test").setValue("fax", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fax", "ADRESS")},
                {"12c) remove fax",
                        new PersonAdminData(this, "Test").setValue("fax", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fax", "")},
                {"13a) add fullname",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("fullname", "ADRESS")},
                {"13b) update fullname",
                        new PersonAdminData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fullname", "ADRESS")},
                {"13c) remove fullname",
                        new PersonAdminData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fullname", "")},
                {"14a) add phone",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("phone", "ADRESS")},
                {"14b) update phone",
                        new PersonAdminData(this, "Test").setValue("phone", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("phone", "ADRESS")},
                {"14c) remove phone",
                        new PersonAdminData(this, "Test").setValue("phone", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("phone", "")},
                {"15a) add product (max. V&R2013x)",
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x),
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{ZKC}")},
                {"15b) update product (max. V&R2013x)",
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC ZCS}"),
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC}")},
                {"15c) remove product (max. V&R2013x)",
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP}"),
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{}")},
                {"15d) add product (min. V&R2014x)",
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x),
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}")},
                {"15e) update product (min. V&R2014x)",
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV IFW}"),
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV}")},
                {"15f) remove product (min. V&R2014x)",
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}"),
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{}")},
                {"16a) add type",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application}")},
                {"16b) update type",
                        new PersonAdminData(this, "Test").setSingle("type", "{application}"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application full}")},
                {"16c) remove type",
                        new PersonAdminData(this, "Test").setSingle("admin", "all").setSingle("type", "{system}"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application}")},
                {"17a) add vault",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("vault", "eService Administration")},
                {"17b) update vault",
                        new PersonAdminData(this, "Test").setValue("vault", "eService Administration"),
                        new PersonAdminData(this, "Test").setValue("vault", "eService Production")},
                {"17c) remove vault",
                        new PersonAdminData(this, "Test").setValue("vault", "eService Administration"),
                        new PersonAdminData(this, "Test")},
                {"18a) add application",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test"))},
                {"18b) update application",
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test2"))},
                {"18c) remove application",
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                        new PersonAdminData(this, "Test")},
                {"19a) add site",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test"))},
                {"19b) update site",
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test1")),
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test2"))},
                {"19c) remove site",
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test")),
                        new PersonAdminData(this, "Test")},
                // group
                {"20a) add group",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "test \"group\""))},
                {"20b) replace group",
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "group")),
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2"))},
                {"20c) remove group",
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2")),
                        new PersonAdminData(this, "Test")},
                // role
                {"21a) add role",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "test \"role\""))},
                {"21b) replace role",
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "role")),
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2"))},
                {"21c) remove role",
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2")),
                        new PersonAdminData(this, "Test")},
                // group and roles
                {"22) person with two groups and roles",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test")
                                .defData("group", new GroupData(this, "test \"group\" 1"))
                                .defData("group", new GroupData(this, "test \"group\" 2"))
                                .defData("role", new RoleData(this, "test \"role\" 1"))
                                .defData("role", new RoleData(this, "test \"role\" 2"))},
        };
    }

    @Override
    @BeforeMethod()
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSONADMIN);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.SYS_APPLICATION);
        this.cleanup(AbstractTest.CI.SYS_SITE);
    }

    @Override
    protected PersonAdmin_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new PersonAdmin_mxJPO(_name);
    }

}
