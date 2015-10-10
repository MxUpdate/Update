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
import org.mxupdate.update.user.PersonCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link PersonCI_mxJPO person admin CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class PersonCI_1ParserTest
    extends AbstractParserTest<PersonCI_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"0) simple",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}" },
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"2b) two symbolic names",
                    "symbolicname \"channel_def\" symbolicname \"name2\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "symbolicname \"channel_def\" symbolicname \"name2\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            // description
            {"3a) comment",
                    "",
                    "comment \"abc def\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"3b) comment not defined",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "             active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
             // hidden flag
            {"4a) hidden",
                    "",
                    "comment \"\" !active !trusted hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"4b) not hidden (not defined)",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" !active !trusted         access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // active flag
            {"5a) active",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"5b) not active ",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"5c) active not defined ",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\"        trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // trusted flag
            {"6a) trusted",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"6b) not trusted ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"6c) trusted not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active          !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // access
            {"7a) none access",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"7b) all access ",
                    "",
                    "comment \"\" active !trusted !hidden access all admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"7c) access list ",
                    "",
                    "comment \"\" active  !trusted !hidden access {ACCESS1 ACCESS2 ACCESS3} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"7d) default access all ",
                    "comment \"\" active !trusted !hidden access all admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden            admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

             // admin
            {"8a) none admin",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"8b) all admin ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin all !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"8c) admin list ",
                    "",
                    "comment \"\" active  !trusted !hidden access {} admin {ADMIN1 ADMIN2 ADMIN3} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"8d) none admin",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active trusted !hidden access {}          !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

             // email flag
            {"9a) email",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"9b) not email ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"9c) email not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {}        !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // iconmail flag
            {"10a) iconmail",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"10b) not iconmail ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"10c) iconmail not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email          address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // address
            {"11a) empty address",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"11b) address with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"ADRESS\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"11c) address not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail              emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // emailaddress
            {"12a) empty emailaddress",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"12b) emailaddress with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"EMAILADRESS\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"12c) emailaddress not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\"                   fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // fax
            {"13a) empty fax",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"13b) fax with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"FAX\" fullname \"\" phone \"\" product {} type {}"},
            {"13c) fax not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\"          fullname \"\" phone \"\" product {} type {}"},

            // fullname
            {"14a) empty fullname",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"14b) fullname with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"FULLNAME\" phone \"\" product {} type {}"},
            {"14c) fullname not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\"               phone \"\" product {} type {}"},

            // phone
            {"15a) empty phone",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"15b) phone with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"PHONE\" product {} type {}"},
            {"15c) phone not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\"            product {} type {}"},

             // type
            {"16a) no type set",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application full}",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"16b) type application set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application}"},
            {"16c) type full set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {full}"},
            {"16d) type business set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {business}"},
            {"16e) type system set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {system}"},
            {"16f) type combination",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application business}"},

            // vault
            {"17) vault",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} vault \"VAULTNAME\""},

            // application
            {"18) application",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} application \"APPNAME\""},

            // site
            {"19) site",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} site \"SITENAME\""},

            // group
            {"20) group",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} group \"GROUPNAME\" group \"GROUPNAME1\" "},

             // role
            {"21) role",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} role \"ROLENAME1\" role \"ROLENAME2\" "},

            // product
            {"22) product",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {ABC DFG HEH} type {} "},

            // property
            {"23a) property special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\""},
            {"23b) property and value special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\" value \"{}\\\"\""},
            {"23c) property link special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\" to type \"{}\\\"\""},
            {"23d) property link and value special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
        };
    }

    @Override
    protected PersonCI_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                           final String _name)
    {
        return new PersonCI_mxJPO(_name);
    }
}
