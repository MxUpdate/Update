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

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.user.PersonAdmin_mxJPO;
import org.mxupdate.update.user.Role_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Role_mxJPO type CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test()
public class PersonAdminCI_1ParserTest
    extends AbstractParserTest<PersonAdmin_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"1) simple",
                "",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}" },
            // description
            {"2a) comment",
                "",
                "kind admin comment \"abc def\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"2b) comment not defined",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin active trusted !hidden access {} admin {} !email !iconmail"},
             // hidden flag
            {"3a) hidden",
                "",
                "kind admin comment \"\" !active !trusted hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"3b) not hidden (not defined)",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" !active !trusted access {} admin {} !email !iconmail"},

            // active flag
            {"4a) active",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"4b) not active ",
                "",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"4c) active not defined ",
                "kind admin comment \"\" !active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}" ,
                "kind admin comment \"\" trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // trusted flag
            {"5a) trusted",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"5b) not trusted ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"5c) trusted not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // access
            {"6a) none access",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"6b) all access ",
                "",
                "kind admin comment \"\" active !trusted !hidden access all admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"6c) access list ",
                "",
                "kind admin comment \"\" active  !trusted !hidden access {ACCESS1 ACCESS2 ACCESS3} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"6d) default access ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active  !trusted !hidden admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},


             // admin
            {"7a) none admin",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"7b) all admin ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin all !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"7c) admin list ",
                "",
                "kind admin comment \"\" active  !trusted !hidden access {} admin {ADMIN1 ADMIN2 ADMIN3} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"7d) none admin",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active trusted !hidden access {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

             // email flag
            {"8a) email",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"8b) not email ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"8c) email not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // iconmail flag
            {"9a) iconmail",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"9b) not iconmail ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"9c) iconmail not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // address
            {"10a) empty address",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"10b) address with value ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"ADRESS\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"10c) address not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // emailaddress
            {"11a) empty emailaddress",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"11b) emailaddress with value ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"EMAILADRESS\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"11c) emailaddress not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // fax
            {"12a) empty fax",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"12b) fax with value ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"FAX\" fullname \"\" phone \"\" product {}"},
            {"12c) fax not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fullname \"\" phone \"\" product {}"},

            // fullname
            {"13a) empty fullname",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"13b) fullname with value ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"FULLNAME\" phone \"\" product {}"},
            {"13c) fullname not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" phone \"\" product {}"},

            // phone
            {"14a) empty phone",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"14b) phone with value ",
                "",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"PHONE\" product {}"},
            {"14c) phone not defined ",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" product {}"},

             // type
            {"15a) no type set",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" type {} product {}"},
            {"15b) type application set",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application}"},
            {"15c) type full set",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {full}"},
            {"15d) type business set",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {business}"},
            {"15e) type system set",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {system}"},
            {"15f) type combination",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application business}"},

            // vault
            {"16a) vault",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} vault \"VAULTNAME\""},

            // application
            {"17a) application",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} application \"APPNAME\""},

            // site
            {"18a) site",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} site \"SITENAME\""},

            // group
            {"19a) group",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} group \"GROUPNAME\" group \"GROUPNAME1\" "},

             // role
            {"20a) role",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} role \"ROLENAME1\" role \"ROLENAME2\" "},

            // product
            {"21a) product",
                "",
                "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {ABC DFG HEH} "},

            // property
            {"22a) property special characters",
                "",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\""},
            {"22b) property and value special characters",
                "",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\" value \"{}\\\"\""},
            {"22c) property link special characters",
                "",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\" to type \"{}\\\"\""},
            {"22d) property link and value special characters",
                "",
                "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override()
    protected PersonAdmin_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new PersonAdmin_mxJPO(_paramCache.getMapping().getTypeDef(CI.USR_PERSONADMIN.updateType), _name);
    }
}
