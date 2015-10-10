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
                {"1) uuid",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("uuid", "FDA75674979211E6AE2256B6B6499611")},
                {"2a) symbolic name",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("symbolicname", "expression_123")},
                {"2b) two symbolic name",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("symbolicname", "expression_123").setValue("symbolicname", "expression_345")},
                // comment
                {"3a) add comment",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("comment", "Descrption")},
                {"3b) update comment",
                        new PersonData(this, "Test").setValue("comment", "Descrption"),
                        new PersonData(this, "Test").setValue("comment", "Description")},
                {"3c) remove comment",
                        new PersonData(this, "Test").setValue("comment", "Descrption"),
                        new PersonData(this, "Test")},
                // hidden
                {"4a) hidden true",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setFlag("hidden", true)},
                {"4b) hidden false",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setFlag("hidden", false)},
                // active flag
                {"5a) set active",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setFlag("active", true)},
                {"5b) set inactive",
                        new PersonData(this, "Test").setFlag("active", true),
                        new PersonData(this, "Test").setFlag("active", false)},
                {"6a) set trusted",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setFlag("trusted", false)},
                {"6b) set untrusted",
                        new PersonData(this, "Test").setFlag("active", true),
                        new PersonData(this, "Test").setFlag("active", false)},
                {"7a) access all",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setSingle("access", "all")},
                {"7b) access list",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setSingle("access", "{grant promote}")},
                {"7c) access list change",
                        new PersonData(this, "Test").setSingle("access", "{grant modify promote}"),
                        new PersonData(this, "Test").setSingle("access", "{grant promote}")},
                {"8a) admin all",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "all")},
                {"8b) admin list",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
                {"8c) admin list change",
                        new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form group rule}"),
                        new PersonData(this, "Test").setSingle("type", "{application business full}").setSingle("admin", "{form rule}")},
                {"9a) email true",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setFlag("email", true)},
                {"9b) email false",
                        new PersonData(this, "Test").setFlag("email", true),
                        new PersonData(this, "Test").setFlag("email", false)},
                {"10a) iconmail true",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setFlag("iconmail", true)},
                {"10b) iconmail false",
                        new PersonData(this, "Test").setFlag("iconmail", true),
                        new PersonData(this, "Test").setFlag("iconmail", false)},
                {"11a) add address",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("address", "ADRESS")},
                {"11b) update address",
                        new PersonData(this, "Test").setValue("address", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("address", "ADRESS")},
                {"11c) remove address",
                        new PersonData(this, "Test").setValue("address", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("address", "")},
                {"12a) add emailaddress",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("emailaddress", "ADRESS")},
                {"12b) update emailaddress",
                        new PersonData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("emailaddress", "ADRESS")},
                {"12c) remove emailaddress",
                        new PersonData(this, "Test").setValue("emailaddress", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("emailaddress", "")},
                {"13a) add fax",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("fax", "ADRESS")},
                {"13b) update fax",
                        new PersonData(this, "Test").setValue("fax", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("fax", "ADRESS")},
                {"14c) remove fax",
                        new PersonData(this, "Test").setValue("fax", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("fax", "")},
                {"14a) add fullname",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("fullname", "ADRESS")},
                {"14b) update fullname",
                        new PersonData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("fullname", "ADRESS")},
                {"14c) remove fullname",
                        new PersonData(this, "Test").setValue("fullname", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("fullname", "")},
                {"15a) add phone",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("phone", "ADRESS")},
                {"15b) update phone",
                        new PersonData(this, "Test").setValue("phone", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("phone", "ADRESS")},
                {"15c) remove phone",
                        new PersonData(this, "Test").setValue("phone", "CURRENTADRESS"),
                        new PersonData(this, "Test").setValue("phone", "")},
                {"16a) add product (max. V&R2013x)",
                        new PersonData(this, "Test").defMaxSupported(Version.V6R2013x),
                        new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{ZKC}")},
                {"16b) update product (max. V&R2013x)",
                        new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC ZCS}"),
                        new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP ZCC}")},
                {"16c) remove product (max. V&R2013x)",
                        new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{XYP}"),
                        new PersonData(this, "Test").defMaxSupported(Version.V6R2013x).setSingle("product", "{}")},
                {"16d) add product (min. V&R2014x)",
                        new PersonData(this, "Test").defMinSupported(Version.V6R2014x),
                        new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}")},
                {"16e) update product (min. V&R2014x)",
                        new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV IFW}"),
                        new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{CNV CSV}")},
                {"16f) remove product (min. V&R2014x)",
                        new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{IFW}"),
                        new PersonData(this, "Test").defMinSupported(Version.V6R2014x).setSingle("product", "{}")},
                {"17a) add type",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setSingle("type", "{application}")},
                {"17b) update type",
                        new PersonData(this, "Test").setSingle("type", "{application}"),
                        new PersonData(this, "Test").setSingle("type", "{application full}")},
                {"17c) remove type",
                        new PersonData(this, "Test").setSingle("admin", "all").setSingle("type", "{system}"),
                        new PersonData(this, "Test").setSingle("type", "{application}")},
                {"18a) add vault",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").setValue("vault", "eService Administration")},
                {"18b) update vault",
                        new PersonData(this, "Test").setValue("vault", "eService Administration"),
                        new PersonData(this, "Test").setValue("vault", "eService Production")},
                {"18c) remove vault",
                        new PersonData(this, "Test").setValue("vault", "eService Administration"),
                        new PersonData(this, "Test")},
                {"19a) add application",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").defData("application", new ApplicationData(this, "test"))},
                {"19b) update application",
                        new PersonData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                        new PersonData(this, "Test").defData("application", new ApplicationData(this, "test2"))},
                {"19c) remove application",
                        new PersonData(this, "Test").defData("application", new ApplicationData(this, "test1")),
                        new PersonData(this, "Test")},
                {"20a) add site",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").defData("site", new SiteData(this,"test"))},
                {"20b) update site",
                        new PersonData(this, "Test").defData("site", new SiteData(this,"test1")),
                        new PersonData(this, "Test").defData("site", new SiteData(this,"test2"))},
                {"20c) remove site",
                        new PersonData(this, "Test").defData("site", new SiteData(this,"test")),
                        new PersonData(this, "Test")},
                // group
                {"21a) add group",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").defData("group", new GroupData(this, "test \"group\""))},
                {"21b) replace group",
                        new PersonData(this, "Test").defData("group", new GroupData(this, "group")),
                        new PersonData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2"))},
                {"21c) remove group",
                        new PersonData(this, "Test").defData("group", new GroupData(this, "group1")).defData("group", new GroupData(this, "group2")),
                        new PersonData(this, "Test")},
                // role
                {"22a) add role",
                        new PersonData(this, "Test"),
                        new PersonData(this, "Test").defData("role", new RoleData(this, "test \"role\""))},
                {"22b) replace role",
                        new PersonData(this, "Test").defData("role", new RoleData(this, "role")),
                        new PersonData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2"))},
                {"22c) remove role",
                        new PersonData(this, "Test").defData("role", new RoleData(this, "role1")).defData("role", new RoleData(this, "role2")),
                        new PersonData(this, "Test")},
                // group and roles
                {"23) person with two groups and roles",
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
    @AfterClass(groups = "close" )
    public void cleanup()
        throws MatrixException
    {
        this.cleanup(AbstractTest.CI.USR_PERSON);
        this.cleanup(AbstractTest.CI.USR_GROUP);
        this.cleanup(AbstractTest.CI.USR_ROLE);
        this.cleanup(AbstractTest.CI.SYS_APPLICATION);
        this.cleanup(AbstractTest.CI.SYS_SITE);
    }

    @Override
    protected PersonCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                           final String _name)
    {
        return new PersonCI_mxJPO(_name);
    }
}
