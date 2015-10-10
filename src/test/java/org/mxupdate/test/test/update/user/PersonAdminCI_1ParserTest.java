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
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PersonAdmin_mxJPO person admin CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class PersonAdminCI_1ParserTest
    extends AbstractParserTest<PersonAdmin_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0) simple",
                    "",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}" },
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "kind admin uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\""},
            {"1b) uuid w/o minus separator",
                    "",
                    "kind admin uuid \"FDA75674979211E6AE2256B6B6499611\"     comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\""},
            {"1c) uuid convert from single to string",
                    "kind admin uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\"",
                    "kind admin uuid   FDA7-5674979211-E6AE2256B6-B6499611    comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\""},
            // registered name
            {"2a) symbolic name",
                    "",
                    "kind admin symbolicname \"channel_abc\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"2b) two symbolic names",
                    "kind admin symbolicname \"channel_def\" symbolicname \"name2\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin symbolicname \"channel_def\" symbolicname \"name2\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            // description
            {"3a) comment",
                    "",
                    "kind admin comment \"abc def\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"3b) comment not defined",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin active trusted !hidden access {} admin {} !email !iconmail"},
             // hidden flag
            {"4a) hidden",
                    "",
                    "kind admin comment \"\" !active !trusted hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"4b) not hidden (not defined)",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" !active !trusted access {} admin {} !email !iconmail"},

            // active flag
            {"5a) active",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"5b) not active ",
                    "",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"5c) active not defined ",
                    "kind admin comment \"\" !active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}" ,
                    "kind admin comment \"\" trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // trusted flag
            {"6a) trusted",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"6b) not trusted ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"6c) trusted not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // access
            {"7a) none access",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"7b) all access ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access all admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"7c) access list ",
                    "",
                    "kind admin comment \"\" active  !trusted !hidden access {ACCESS1 ACCESS2 ACCESS3} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"7d) default access ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active  !trusted !hidden admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

             // admin
            {"8a) none admin",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"8b) all admin ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin all !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"8c) admin list ",
                    "",
                    "kind admin comment \"\" active  !trusted !hidden access {} admin {ADMIN1 ADMIN2 ADMIN3} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"8d) none admin",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active trusted !hidden access {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

             // email flag
            {"9a) email",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"9b) not email ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"9c) email not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // iconmail flag
            {"10a) iconmail",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"10b) not iconmail ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"10c) iconmail not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // address
            {"11a) empty address",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"11b) address with value ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"ADRESS\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"11c) address not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // emailaddress
            {"12a) empty emailaddress",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"12b) emailaddress with value ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"EMAILADRESS\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"12c) emailaddress not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" fax \"\" fullname \"\" phone \"\" product {}"},

            // fax
            {"13a) empty fax",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"13b) fax with value ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"FAX\" fullname \"\" phone \"\" product {}"},
            {"13c) fax not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fullname \"\" phone \"\" product {}"},

            // fullname
            {"14a) empty fullname",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"14b) fullname with value ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"FULLNAME\" phone \"\" product {}"},
            {"14c) fullname not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" phone \"\" product {}"},

            // phone
            {"15a) empty phone",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"15b) phone with value ",
                    "",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"PHONE\" product {}"},
            {"15c) phone not defined ",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" product {}"},

             // type
            {"16a) no type set",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" type {} product {}"},
            {"16b) type application set",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application}"},
            {"16c) type full set",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {full}"},
            {"16d) type business set",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {business}"},
            {"16e) type system set",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {system}"},
            {"16f) type combination",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application business}"},

            // vault
            {"17) vault",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} vault \"VAULTNAME\""},

            // application
            {"18) application",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} application \"APPNAME\""},

            // site
            {"19) site",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} site \"SITENAME\""},

            // group
            {"20) group",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} group \"GROUPNAME\" group \"GROUPNAME1\" "},

             // role
            {"21) role",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} role \"ROLENAME1\" role \"ROLENAME2\" "},

            // product
            {"22) product",
                    "",
                    "kind admin comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {ABC DFG HEH} "},

            // property
            {"23a) property special characters",
                    "",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\""},
            {"23b) property and value special characters",
                    "",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\" value \"{}\\\"\""},
            {"23c) property link special characters",
                    "",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\" to type \"{}\\\"\""},
            {"23d) property link and value special characters",
                    "",
                    "kind admin comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected PersonAdmin_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                              final String _name)
    {
        return new PersonAdmin_mxJPO(_name);
    }
}
