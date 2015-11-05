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
        return new Object[][]  {
            {"0) simple",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}" },
            // package
            {"1a) package string",
                    "",
                    "package \"abc\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"1b) package single",
                    "package \"abc\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "package abc     comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            // uuid
            {"2a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"2b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"2c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            // registered name
            {"3a) symbolic name",
                    "",
                    "symbolicname \"channel_abc\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"3b) two symbolic names",
                    "symbolicname \"channel_def\" symbolicname \"name2\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "symbolicname \"channel_def\" symbolicname \"name2\" comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            // description
            {"4a) comment",
                    "",
                    "comment \"abc def\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"4b) comment not defined",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "             active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
             // hidden flag
            {"5a) hidden",
                    "",
                    "comment \"\" !active !trusted hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"5b) not hidden (not defined)",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" !active !trusted         access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // active flag
            {"6a) active",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"6b) not active ",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"6c) active not defined ",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\"        trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // trusted flag
            {"7a) trusted",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"7b) not trusted ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"7c) trusted not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active          !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // access
            {"8a) none access",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"8b) all access ",
                    "",
                    "comment \"\" active !trusted !hidden access all admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"8c) access list ",
                    "",
                    "comment \"\" active  !trusted !hidden access {ACCESS1 ACCESS2 ACCESS3} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"8d) default access all ",
                    "comment \"\" active !trusted !hidden access all admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden            admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

             // admin
            {"9a) none admin",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"9b) all admin ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin all !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"9c) admin list ",
                    "",
                    "comment \"\" active  !trusted !hidden access {} admin {ADMIN1 ADMIN2 ADMIN3} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"9d) none admin",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active trusted !hidden access {}          !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

             // email flag
            {"10a) email",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"10b) not email ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"10c) email not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {}        !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // iconmail flag
            {"11a) iconmail",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"11b) not iconmail ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"11c) iconmail not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email          address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // address
            {"12a) empty address",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"12b) address with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"ADRESS\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"12c) address not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail              emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // emailaddress
            {"13a) empty emailaddress",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"13b) emailaddress with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"EMAILADRESS\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"13c) emailaddress not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\"                   fax \"\" fullname \"\" phone \"\" product {} type {}"},

            // fax
            {"14a) empty fax",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"14b) fax with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"FAX\" fullname \"\" phone \"\" product {} type {}"},
            {"14c) fax not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\"          fullname \"\" phone \"\" product {} type {}"},

            // fullname
            {"15a) empty fullname",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"15b) fullname with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"FULLNAME\" phone \"\" product {} type {}"},
            {"15c) fullname not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\"               phone \"\" product {} type {}"},

            // phone
            {"16a) empty phone",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}"},
            {"16b) phone with value ",
                    "",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"PHONE\" product {} type {}"},
            {"16c) phone not defined ",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {}",
                    "comment \"\" active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\"            product {} type {}"},

             // type
            {"17a) no type set",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application full}",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {}"},
            {"17b) type application set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application}"},
            {"17c) type full set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {full}"},
            {"17d) type business set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {business}"},
            {"17e) type system set",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {system}"},
            {"17f) type combination",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {application business}"},

            // vault
            {"18) vault",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} vault \"VAULTNAME\""},

            // application
            {"19) application",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} application \"APPNAME\""},

            // site
            {"20) site",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} site \"SITENAME\""},

            // group
            {"21) group",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} group \"GROUPNAME\" group \"GROUPNAME1\" "},

             // role
            {"22) role",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} role \"ROLENAME1\" role \"ROLENAME2\" "},

            // product
            {"23) product",
                    "",
                    "comment \"\" active trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {ABC DFG HEH} type {} "},

            // property
            {"24a) property special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\""},
            {"24b) property and value special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\" value \"{}\\\"\""},
            {"24c) property link special characters",
                    "",
                    "comment \"\" !active !trusted !hidden access {} admin {} !email !iconmail address \"\" emailaddress \"\" fax \"\" fullname \"\" phone \"\" product {} type {} property \"{}\\\"\" to type \"{}\\\"\""},
            {"24d) property link and value special characters",
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
