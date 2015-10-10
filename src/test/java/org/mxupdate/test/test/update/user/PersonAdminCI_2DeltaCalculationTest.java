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
@Test
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
                {"1) uuid",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
                {"2a) symbolic name",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("symbolicname", "expression_123")},
                {"2b) two symbolic name",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
                // comment
                {"3a) add comment",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("comment", "Descrption")},
                {"3b) update comment",
                        new PersonAdminData(this, "Test").setValue("comment", "Descrption"),
                        new PersonAdminData(this, "Test").setValue("comment", "Description")},
                {"3c) remove comment",
                        new PersonAdminData(this, "Test").setValue("comment", "Descrption"),
                        new PersonAdminData(this, "Test")},
                // hidden
                {"4a) hidden true",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("hidden", true)},
                {"4b) hidden false",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("hidden", false)},
                // active flag
                {"5a) set active",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("active", true)},
                {"5b) set inactive",
                        new PersonAdminData(this, "Test").setFlag("active", true),
                        new PersonAdminData(this, "Test").setFlag("active", false)},
                {"6a) set trusted",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("trusted", false)},
                {"6b) set untrusted",
                        new PersonAdminData(this, "Test").setFlag("active", true),
                        new PersonAdminData(this, "Test").setFlag("active", false)},
                {"7a) access all",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("access", "all")},
                {"7b) access list",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("access", "{grant promote}")},
                {"7c) access list change",
                        new PersonAdminData(this, "Test").setSingle("access", "{grant modify promote}"),
                        new PersonAdminData(this, "Test").setSingle("access", "{grant promote}")},
                {"8a) admin all",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "all")},
                {"8b) admin list",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
                {"8c) admin list change",
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form group rule}"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
                {"9a) email true",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("email", true)},
                {"9b) email false",
                        new PersonAdminData(this, "Test").setFlag("email", true),
                        new PersonAdminData(this, "Test").setFlag("email", false)},
                {"10a) iconmail true",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setFlag("iconmail", true)},
                {"10b) iconmail false",
                        new PersonAdminData(this, "Test").setFlag("iconmail", true),
                        new PersonAdminData(this, "Test").setFlag("iconmail", false)},
                {"11a) add address",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("address", "ADRESS")},
                {"11b) update address",
                        new PersonAdminData(this, "Test").setValue("address", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("address", "ADRESS")},
                {"11c) remove address",
                        new PersonAdminData(this, "Test").setValue("address", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("address", "")},
                {"12a) add emailaddress",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("emailaddress", "ADRESS")},
                {"12b) update emailaddress",
                        new PersonAdminData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("emailaddress", "ADRESS")},
                {"12c) remove emailaddress",
                        new PersonAdminData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("emailaddress", "")},
                {"13a) add fax",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("fax", "ADRESS")},
                {"13b) update fax",
                        new PersonAdminData(this, "Test").setValue("fax", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fax", "ADRESS")},
                {"14c) remove fax",
                        new PersonAdminData(this, "Test").setValue("fax", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fax", "")},
                {"14a) add fullname",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("fullname", "ADRESS")},
                {"14b) update fullname",
                        new PersonAdminData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fullname", "ADRESS")},
                {"14c) remove fullname",
                        new PersonAdminData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("fullname", "")},
                {"15a) add phone",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("phone", "ADRESS")},
                {"15b) update phone",
                        new PersonAdminData(this, "Test").setValue("phone", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("phone", "ADRESS")},
                {"15c) remove phone",
                        new PersonAdminData(this, "Test").setValue("phone", "CURRENTADRESS"),
                        new PersonAdminData(this, "Test").setValue("phone", "")},
                {"16a) add product (max. V&R2013x)",
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x),
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{ZKC}")},
                {"16b) update product (max. V&R2013x)",
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC ZCS}"),
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC}")},
                {"16c) remove product (max. V&R2013x)",
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP}"),
                        new PersonAdminData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{}")},
                {"16d) add product (min. V&R2014x)",
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x),
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}")},
                {"16e) update product (min. V&R2014x)",
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV IFW}"),
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV}")},
                {"16f) remove product (min. V&R2014x)",
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}"),
                        new PersonAdminData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{}")},
                {"17a) add type",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application}")},
                {"17b) update type",
                        new PersonAdminData(this, "Test").setSingle("type", "{application}"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application full}")},
                {"17c) remove type",
                        new PersonAdminData(this, "Test").setSingle("admin", "all").setSingle("type", "{system}"),
                        new PersonAdminData(this, "Test").setSingle("type", "{application}")},
                {"18a) add vault",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").setValue("vault", "eService Administration")},
                {"18b) update vault",
                        new PersonAdminData(this, "Test").setValue("vault", "eService Administration"),
                        new PersonAdminData(this, "Test").setValue("vault", "eService Production")},
                {"18c) remove vault",
                        new PersonAdminData(this, "Test").setValue("vault", "eService Administration"),
                        new PersonAdminData(this, "Test")},
                {"19a) add application",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test"))},
                {"19b) update application",
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test2"))},
                {"19c) remove application",
                        new PersonAdminData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                        new PersonAdminData(this, "Test")},
                {"20a) add site",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test"))},
                {"20b) update site",
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test1")),
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test2"))},
                {"20c) remove site",
                        new PersonAdminData(this, "Test").defData("site", new SiteData(this,"test")),
                        new PersonAdminData(this, "Test")},
                // group
                {"21a) add group",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "test \"group\""))},
                {"21b) replace group",
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "group")),
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2"))},
                {"21c) remove group",
                        new PersonAdminData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2")),
                        new PersonAdminData(this, "Test")},
                // role
                {"22a) add role",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "test \"role\""))},
                {"22b) replace role",
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "role")),
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2"))},
                {"22c) remove role",
                        new PersonAdminData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2")),
                        new PersonAdminData(this, "Test")},
                // group and roles
                {"23) person with two groups and roles",
                        new PersonAdminData(this, "Test"),
                        new PersonAdminData(this, "Test")
                                .defData("group", new GroupData(this, "test \"group\" 1"))
                                .defData("group", new GroupData(this, "test \"group\" 2"))
                                .defData("role", new RoleData(this, "test \"role\" 1"))
                                .defData("role", new RoleData(this, "test \"role\" 2"))},
        };
    }

    @Override
    @BeforeMethod
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
