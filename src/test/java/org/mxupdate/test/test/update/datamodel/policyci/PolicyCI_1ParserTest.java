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

package org.mxupdate.test.test.update.datamodel.policyci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Policy_mxJPO policy CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class PolicyCI_1ParserTest
    extends AbstractParserTest<Policy_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        final String simple = "description \"\" hidden defaultformat \"\" sequence \"\" store \"\" ";
        final String state  = "!enforcereserveaccess !majorrevision !minorrevision !version !promote !checkouthistory !published ";

        return new Object[][] {
                {"0) simple policy",
                        "",
                        simple},
                // package
                {"1a) package string",
                        "",
                        "package \"abc\" " + simple},
                {"1b) package single",
                        "package \"abc\" " + simple,
                        "package abc     " + simple},
                // uuid
                {"2a) uuid with minus separator",
                        "",
                        "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" " + simple},
                {"2b) uuid w/o minus separator",
                        "",
                        "uuid \"FDA75674979211E6AE2256B6B6499611\"     " + simple},
                {"2c) uuid convert from single to string",
                        "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  " + simple,
                        "uuid   FDA7-5674979211-E6AE2256B6-B6499611    " + simple},
                // registered name
                {"3a) symbolic name",
                        "",
                        "symbolicname \"policy_abc\" " + simple},
                {"3b) two symbolic names",
                        "symbolicname \"policy_abc\" symbolicname \"policy_def\" " + simple,
                        "symbolicname \"policy_def\" symbolicname \"policy_abc\" " + simple},
                // description
                {"4a) description",
                        "",
                        "description \"abc def\" !hidden defaultformat \"\" sequence \"\" store \"\""},
                {"4b) description not defined",
                        "description \"\" !hidden defaultformat \"\" sequence \"\" store \"\"",
                        "                 !hidden defaultformat \"\" sequence \"\" store \"\""},
                {"4c) multi-line description",
                        "",
                        "description \"abc\ndef\" !hidden defaultformat \"\" sequence \"\" store \"\""},
                {"4d) tab's in description",
                        "",
                        "description \"abc\\tdef\" !hidden defaultformat \"\" sequence \"\" store \"\""},
                // hidden flag
                {"5a) simple not hidden policy",
                        "",
                        "description \"\" !hidden defaultformat \"\" sequence \"\" store \"\""},
                {"5b) hidden policy",
                        "",
                        "description \"\" hidden defaultformat \"\" sequence \"\" store \"\""},
                // policy type
                {"6a) policy with one type",
                        "",
                        "description \"\" !hidden type \"abc\" defaultformat \"\" sequence \"\" store \"\""},
                {"6b) policy with all types",
                        "",
                        "description \"\" !hidden type all defaultformat \"\" sequence \"\" store \"\""},
                {"6c) policy with multiple types to test sorting",
                        "description \"\" !hidden type \"abc\" type \"def\" defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" !hidden type \"def\" type \"abc\" defaultformat \"\" sequence \"\" store \"\""},
                // policy format
                {"7a) policy with one format",
                        "",
                        "description \"\" !hidden format \"abc\" defaultformat \"\" sequence \"\" store \"\""},
                {"7b) policy with all formats",
                        "",
                        "description \"\" !hidden format all defaultformat \"\" sequence \"\" store \"\""},
                {"7c) policy with multiple formats to test sorting",
                        "description \"\" !hidden format \"abc\" format \"def\" defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" !hidden format \"def\" format \"abc\" defaultformat \"\" sequence \"\" store \"\""},
                // enforce flag
                {"8a) not enforce policy",
                        "description \"\" !hidden defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" !hidden defaultformat \"\" !enforce sequence \"\" store \"\""},
                {"8b) enforce policy",
                        "",
                        "description \"\" !hidden defaultformat \"\" enforce sequence \"\" store \"\""},
                // special characters
                {"9a) policy with description special characters",
                        "",
                        "description \"{}\\\"\" hidden defaultformat \"\" sequence \"\" store \"\""},
                {"9b) policy with type special characters",
                        "",
                        "description \"\" hidden type \"{}\\\"\" defaultformat \"\" sequence \"\" store \"\""},
                {"9c) policy with format special characters",
                        "",
                        "description \"\" hidden format \"{}\\\"\" defaultformat \"\" sequence \"\" store \"\""},
                {"9d) policy with defaultformat special characters",
                        "",
                        "description \"\" hidden defaultformat \"{}\\\"\" sequence \"\" store \"\""},
                {"9e) policy with sequence special characters",
                        "",
                        "description \"\" hidden defaultformat \"\" sequence \"{}\\\"\" store \"\""},
                {"9f) policy with store special characters",
                        "",
                        "description \"\" hidden defaultformat \"\" sequence \"\" store \"{}\\\"\""},
                {"9g) policy with delimeter / minor special characters",
                        "",
                        "description \"\" hidden defaultformat \"\" delimiter \".\" minorsequence \"{}\\\"\" majorsequence \"{}\\\"\" store \"\""},
                // policy property
                {"10a) policy with property special characters",
                        "",
                        simple + "property \"{}\\\"\""},
                {"10b) policy with property and value special characters",
                        "",
                        simple + "property \"{}\\\"\" value \"{}\\\"\""},
                {"10c) policy with property link special characters",
                        "",
                        simple + "property \"{}\\\"\" to type \"{}\\\"\""},
                {"10d) policy with property link and value special characters",
                        "",
                        simple + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
                // policy state with special characters
                {"11a) policy with state symbolic name special characters",
                        "",
                        simple + "state \"{}\\\"\" { registeredname \"{}\\\"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                {"11b) policy with state action",
                        "",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"AAA\" input \"\" }"},
                {"11c) policy with empty state action",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" }"},
                {"11d) policy with state action input",
                        "",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"AAA\" }"},
                {"11e) policy with state check",
                        "",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published check \"AAA\" input \"\" }"},
                {"11f) policy with empty state check",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published check \"\" input \"\" }"},
                {"11g) policy with state check input",
                        "",
                        simple + "state \"A\" { registeredname \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published check \"\" input \"AAA\" }"},
                // policy registere state name
                {"12a) policy with state symbolic name",
                        "",
                        simple + "state \"A\" { registeredname \"state_A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published      } "},
                {"12b) policy with two state symbolic names",
                        "",
                        simple + "state \"A\" { registeredname \"state_A\" registeredname \"state_A1\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                {"12c) policy with state symbolic names defined as property",
                        simple + "state \"A\" { registeredname \"state_A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" {                            !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published } property \"state_A\" value \"A\""},
                {"12d) policy with property name equal state name (but not registered name)",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published } property \"A\" value \"A\"",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published } property \"A\" value \"A\""},
                // policy state property
                {"20a) policy with state property special characters",
                        "",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" }"},
                {"20b) policy with state property and value special characters",
                        "",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" value \"{}\\\"\" }"},
                {"20c) policy with state property link special characters",
                        "",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" to type \"{}\\\"\" }"},
                {"20d) policy with state property link and value special characters",
                        "",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
                // policy state with enforce flag
                {"21a) policy state not enforcereserveaccess",
                        "",
                        simple + "state \"A\" { !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                {"21b) policy state enforcereserveaccess",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with majorrevision flag
                {"22a) policy state not majorrevision",
                        "",
                        simple + "state \"A\" { enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published }"},
                {"22b) policy state majorrevision",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with minorrevision flag
                {"23a) policy state not minorrevision",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }"},
                {"23b) policy state minorrevision",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with revision flag
                {"24a) policy state not revision",
                        simple + "state \"A\" { enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { enforcereserveaccess majorrevision !revision      version promote checkouthistory published }"},
                {"24b) policy state minorrevision",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { enforcereserveaccess majorrevision revision      version promote checkouthistory published }"},
                // policy state with version flag
                {"25a) policy state not version",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published }"},
                {"25b) policy state version",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with promote flag
                {"26a) policy state not promote",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published }"},
                {"26b) policy state promote",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with checkouthistory flag
                {"27a) policy state not checkouthistory",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published }"},
                {"27b) policy state checkouthistory",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with published flag
                {"28a) policy state not published",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published }"},
                {"28b) policy state published",
                        "",
                        simple + "state \"A\" { enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},

                // all state owner one access
                {"30a) all state owner", "", simple + "allstate {              owner             {read} }"},
                {"30b) all state owner", "", simple + "allstate {        login owner             {read} }"},
                {"30c) all state owner", "", simple + "allstate { revoke       owner             {read} }"},
                {"30d) all state owner", "", simple + "allstate { revoke login owner             {read} }"},
                {"30e) all state owner", "", simple + "allstate {              owner key \"key\" {read} }"},
                {"30f) all state owner", "", simple + "allstate {        login owner key \"key\" {read} }"},
                {"30g) all state owner", "", simple + "allstate { revoke       owner key \"key\" {read} }"},
                {"30h) all state owner", "", simple + "allstate { revoke login owner key \"key\" {read} }"},
                // all state owner multiple access
                {"31a) all state owner", "", simple + "allstate {              owner             {read show write} }"},
                {"31b) all state owner", "", simple + "allstate {        login owner             {read show write} }"},
                {"31c) all state owner", "", simple + "allstate { revoke       owner             {read show write} }"},
                {"31d) all state owner", "", simple + "allstate { revoke login owner             {read show write} }"},
                {"31e) all state owner", "", simple + "allstate {              owner key \"key\" {read show write} }"},
                {"31f) all state owner", "", simple + "allstate {        login owner key \"key\" {read show write} }"},
                {"31g) all state owner", "", simple + "allstate { revoke       owner key \"key\" {read show write} }"},
                {"31h) all state owner", "", simple + "allstate { revoke login owner key \"key\" {read show write} }"},
                // all state public
                {"32a) all state public", "", simple + "allstate {              public             {read} }"},
                {"32b) all state public", "", simple + "allstate {        login public             {read} }"},
                {"32c) all state public", "", simple + "allstate { revoke       public             {read} }"},
                {"32d) all state public", "", simple + "allstate { revoke login public             {read} }"},
                {"32e) all state public", "", simple + "allstate {              public key \"key\" {read} }"},
                {"32f) all state public", "", simple + "allstate {        login public key \"key\" {read} }"},
                {"32g) all state public", "", simple + "allstate { revoke       public key \"key\" {read} }"},
                {"32h) all state public", "", simple + "allstate { revoke login public key \"key\" {read} }"},
                // all state public multiple access
                {"32a) all state public", "", simple + "allstate {              public             {read show write} }"},
                {"32b) all state public", "", simple + "allstate {        login public             {read show write} }"},
                {"32c) all state public", "", simple + "allstate { revoke       public             {read show write} }"},
                {"32d) all state public", "", simple + "allstate { revoke login public             {read show write} }"},
                {"32e) all state public", "", simple + "allstate {              public key \"key\" {read show write} }"},
                {"32f) all state public", "", simple + "allstate {        login public key \"key\" {read show write} }"},
                {"32g) all state public", "", simple + "allstate { revoke       public key \"key\" {read show write} }"},
                {"32h) all state public", "", simple + "allstate { revoke login public key \"key\" {read show write} }"},
                // allstate state user
                {"34a) all state user", "", simple + "allstate {              user \"createor\"             {read} }"},
                {"34b) all state user", "", simple + "allstate {        login user \"createor\"             {read} }"},
                {"34c) all state user", "", simple + "allstate { revoke       user \"createor\"             {read} }"},
                {"34d) all state user", "", simple + "allstate { revoke login user \"createor\"             {read} }"},
                {"34e) all state user", "", simple + "allstate {              user \"createor\" key \"key\" {read} }"},
                {"34f) all state user", "", simple + "allstate {        login user \"createor\" key \"key\" {read} }"},
                {"34g) all state user", "", simple + "allstate { revoke       user \"createor\" key \"key\" {read} }"},
                {"34h) all state user", "", simple + "allstate { revoke login user \"createor\" key \"key\" {read} }"},
                // allstate state user multiple access
                {"34a) all state user", "", simple + "allstate {              user \"createor\"             {read show write} }"},
                {"34b) all state user", "", simple + "allstate {        login user \"createor\"             {read show write} }"},
                {"34c) all state user", "", simple + "allstate { revoke       user \"createor\"             {read show write} }"},
                {"34d) all state user", "", simple + "allstate { revoke login user \"createor\"             {read show write} }"},
                {"34e) all state user", "", simple + "allstate {              user \"createor\" key \"key\" {read show write} }"},
                {"34f) all state user", "", simple + "allstate {        login user \"createor\" key \"key\" {read show write} }"},
                {"34g) all state user", "", simple + "allstate { revoke       user \"createor\" key \"key\" {read show write} }"},
                {"34h) all state user", "", simple + "allstate { revoke login user \"createor\" key \"key\" {read show write} }"},

                // single state owner
                {"40a) single state owner", "", simple + "state \"A\" { " + state + "              owner             {read} }"},
                {"40b) single state owner", "", simple + "state \"A\" { " + state + "        login owner             {read} }"},
                {"40c) single state owner", "", simple + "state \"A\" { " + state + " revoke       owner             {read} }"},
                {"40d) single state owner", "", simple + "state \"A\" { " + state + " revoke login owner             {read} }"},
                {"40e) single state owner", "", simple + "state \"A\" { " + state + "              owner key \"key\" {read} }"},
                {"40f) single state owner", "", simple + "state \"A\" { " + state + "        login owner key \"key\" {read} }"},
                {"40g) single state owner", "", simple + "state \"A\" { " + state + " revoke       owner key \"key\" {read} }"},
                {"40h) single state owner", "", simple + "state \"A\" { " + state + " revoke login owner key \"key\" {read} }"},
                // single state owner multiple access
                {"41a) single state owner", "", simple + "state \"A\" { " + state + "              owner             {read show write} }"},
                {"41b) single state owner", "", simple + "state \"A\" { " + state + "        login owner             {read show write} }"},
                {"41c) single state owner", "", simple + "state \"A\" { " + state + " revoke       owner             {read show write} }"},
                {"41d) single state owner", "", simple + "state \"A\" { " + state + " revoke login owner             {read show write} }"},
                {"41e) single state owner", "", simple + "state \"A\" { " + state + "              owner key \"key\" {read show write} }"},
                {"41f) single state owner", "", simple + "state \"A\" { " + state + "        login owner key \"key\" {read show write} }"},
                {"41g) single state owner", "", simple + "state \"A\" { " + state + " revoke       owner key \"key\" {read show write} }"},
                {"41h) single state owner", "", simple + "state \"A\" { " + state + " revoke login owner key \"key\" {read show write} }"},
                // single state public
                {"42a) single state public", "", simple + "state \"A\" { " + state + "              public             {read} }"},
                {"42b) single state public", "", simple + "state \"A\" { " + state + "        login public             {read} }"},
                {"42c) single state public", "", simple + "state \"A\" { " + state + " revoke       public             {read} }"},
                {"42d) single state public", "", simple + "state \"A\" { " + state + " revoke login public             {read} }"},
                {"42e) single state public", "", simple + "state \"A\" { " + state + "              public key \"key\" {read} }"},
                {"42f) single state public", "", simple + "state \"A\" { " + state + "        login public key \"key\" {read} }"},
                {"42g) single state public", "", simple + "state \"A\" { " + state + " revoke       public key \"key\" {read} }"},
                {"42h) single state public", "", simple + "state \"A\" { " + state + " revoke login public key \"key\" {read} }"},
                // single state public multiple access
                {"43a) single state public", "", simple + "state \"A\" { " + state + "              public             {read show write} }"},
                {"43b) single state public", "", simple + "state \"A\" { " + state + "        login public             {read show write} }"},
                {"43c) single state public", "", simple + "state \"A\" { " + state + " revoke       public             {read show write} }"},
                {"43d) single state public", "", simple + "state \"A\" { " + state + " revoke login public             {read show write} }"},
                {"43e) single state public", "", simple + "state \"A\" { " + state + "              public key \"key\" {read show write} }"},
                {"43f) single state public", "", simple + "state \"A\" { " + state + "        login public key \"key\" {read show write} }"},
                {"43g) single state public", "", simple + "state \"A\" { " + state + " revoke       public key \"key\" {read show write} }"},
                {"43h) single state public", "", simple + "state \"A\" { " + state + " revoke login public key \"key\" {read show write} }"},
                // single state user
                {"44a) single state user", "", simple + "state \"A\" { " + state + "              user \"createor\"             {read} }"},
                {"44b) single state user", "", simple + "state \"A\" { " + state + "        login user \"createor\"             {read} }"},
                {"44c) single state user", "", simple + "state \"A\" { " + state + " revoke       user \"createor\"             {read} }"},
                {"44d) single state user", "", simple + "state \"A\" { " + state + " revoke login user \"createor\"             {read} }"},
                {"44e) single state user", "", simple + "state \"A\" { " + state + "              user \"createor\" key \"key\" {read} }"},
                {"44f) single state user", "", simple + "state \"A\" { " + state + "        login user \"createor\" key \"key\" {read} }"},
                {"44g) single state user", "", simple + "state \"A\" { " + state + " revoke       user \"createor\" key \"key\" {read} }"},
                {"44h) single state user", "", simple + "state \"A\" { " + state + " revoke login user \"createor\" key \"key\" {read} }"},
                // single state user multiple access
                {"45a) single state user", "", simple + "state \"A\" { " + state + "              user \"createor\"             {read show write} }"},
                {"45b) single state user", "", simple + "state \"A\" { " + state + "        login user \"createor\"             {read show write} }"},
                {"45c) single state user", "", simple + "state \"A\" { " + state + " revoke       user \"createor\"             {read show write} }"},
                {"45d) single state user", "", simple + "state \"A\" { " + state + " revoke login user \"createor\"             {read show write} }"},
                {"45e) single state user", "", simple + "state \"A\" { " + state + "              user \"createor\" key \"key\" {read show write} }"},
                {"45f) single state user", "", simple + "state \"A\" { " + state + "        login user \"createor\" key \"key\" {read show write} }"},
                {"45g) single state user", "", simple + "state \"A\" { " + state + " revoke       user \"createor\" key \"key\" {read show write} }"},
                {"45h) single state user", "", simple + "state \"A\" { " + state + " revoke login user \"createor\" key \"key\" {read show write} }"},

                // state route
                {"50a) state with route",                "", simple + "state \"A\" { " + state + " route {\"abc\"} \"\"     }"},
                {"50b) state with route & message",      "", simple + "state \"A\" { " + state + " route {\"abc\"} \"abc\"  }"},
                {"50c) state with multiple route users (to test sorting)",
                        simple + "state \"A\" { " + state + " route {\"abc\" \"def\"} \"\" }",
                        simple + "state \"A\" { " + state + " route {\"def\" \"abc\"} \"\" }"},
                {"50d) state with empty route user",
                        simple + "state \"A\" { " + state + "               }",
                        simple + "state \"A\" { " + state + " route {} \"\" }"},

                // state trigger
                {"60a) state with check trigger",               "", simple + "state \"A\" { " + state + " trigger modify check    \"Prog\" input \"\" }"},
                {"60b) state with check trigger & input",       "", simple + "state \"A\" { " + state + " trigger modify check    \"Prog\" input \"abc\" }"},
                {"60c) state with check trigger & empty input",
                        simple + "state \"A\" { " + state + " trigger modify check    \"Prog\" input \"\" }",
                        simple + "state \"A\" { " + state + " trigger modify check    \"Prog\"            }"},
                {"61a) state with override trigger",         "", simple + "state \"A\" { " + state + " trigger modify override \"Prog\" input \"\" }"},
                {"61b) state with override trigger & input", "", simple + "state \"A\" { " + state + " trigger modify override \"Prog\" input \"abc\" }"},
                {"61c) state with check trigger & empty input",
                        simple + "state \"A\" { " + state + " trigger modify override    \"Prog\" input \"\" }",
                        simple + "state \"A\" { " + state + " trigger modify override    \"Prog\"            }"},

                {"62a) state with action trigger",           "", simple + "state \"A\" { " + state + " trigger modify action   \"Prog\" input \"\" }"},
                {"62b) state with action trigger & input",   "", simple + "state \"A\" { " + state + " trigger modify action   \"Prog\" input \"abc\" }"},
                {"62c) state with check trigger & no input",
                        simple + "state \"A\" { " + state + " trigger modify action    \"Prog\" input \"\" }",
                        simple + "state \"A\" { " + state + " trigger modify action    \"Prog\"             }"},

                // state signature (branch)
                {"70a) state with signature",         "", simple + "state \"A\" { " + state + " signature \"A\" { branch \"\"    approve {}        ignore {}        reject {}        filter \"\"    } }"},
                {"70b) state with signature branch",  "", simple + "state \"A\" { " + state + " signature \"A\" { branch \"abc\" approve {}        ignore {}        reject {}        filter \"\"    } }"},
                {"70c) state with signature approve", "", simple + "state \"A\" { " + state + " signature \"A\" { branch \"\"    approve {\"abc\"} ignore {}        reject {}        filter \"\"    } }"},
                {"70d) state with signature ignore",  "", simple + "state \"A\" { " + state + " signature \"A\" { branch \"\"    approve {}        ignore {\"abc\"} reject {}        filter \"\"    } }"},
                {"70e) state with signature reject",  "", simple + "state \"A\" { " + state + " signature \"A\" { branch \"\"    approve {}        ignore {}        reject {\"abc\"} filter \"\"    } }"},
                {"70f) state with signature filter",  "", simple + "state \"A\" { " + state + " signature \"A\" { branch \"\"    approve {}        ignore {}        reject {}        filter \"abc\" } }"},
                {"70g) state with empty signature value (to check default values)",
                        simple + "state \"A\" { " + state + " signature \"A\" { branch \"\" approve {} ignore {} reject {} filter \"\" } }",
                        simple + "state \"A\" { " + state + " signature \"A\" { } }"},

                // organization
                {"100a) all state user: organization any", simple + "allstate { user \"createor\" {read} }", simple + "allstate { user \"createor\" {read} any organization }"},
                {"100b) all state user: organization ancestor",   "", simple + "allstate { user \"createor\" {read} ancestor   organization }"},
                {"100c) all state user: organization descendant", "", simple + "allstate { user \"createor\" {read} descendant organization }"},
                {"109d) all state user: organization related",    "", simple + "allstate { user \"createor\" {read} related    organization }"},
                {"100e) all state user: organization single",     "", simple + "allstate { user \"createor\" {read} single     organization }"},
                {"101a) single state user: organization any", simple + "state \"A\" { " + state + " user \"createor\" {read} }", simple + "state \"A\" { " + state + " user \"createor\" {read} any organization }"},
                {"101b) single state user: organization ancestor",   "", simple + "state \"A\" { " + state + " user \"createor\" {read} ancestor   organization }"},
                {"101c) single state user: organization descendant", "", simple + "state \"A\" { " + state + " user \"createor\" {read} descendant organization }"},
                {"101d) single state user: organization related",    "", simple + "state \"A\" { " + state + " user \"createor\" {read} related    organization }"},
                {"101e) single state user: organization single",     "", simple + "state \"A\" { " + state + " user \"createor\" {read} single     organization }"},

                // project
                {"110a) all state user: project any", simple + "allstate { user \"createor\" {read} }", simple + "allstate { user \"createor\" {read} any project }"},
                {"110b) all state user: project ancestor",   "", simple + "allstate { user \"createor\" {read} ancestor   project }"},
                {"110c) all state user: project descendant", "", simple + "allstate { user \"createor\" {read} descendant project }"},
                {"119d) all state user: project related",    "", simple + "allstate { user \"createor\" {read} related    project }"},
                {"110e) all state user: project single",     "", simple + "allstate { user \"createor\" {read} single     project }"},
                {"111a) single state user: project any", simple + "state \"A\" { " + state + " user \"createor\" {read} }", simple + "state \"A\" { " + state + " user \"createor\" {read} any project }"},
                {"111b) single state user: project ancestor",   "", simple + "state \"A\" { " + state + " user \"createor\" {read} ancestor   project }"},
                {"111c) single state user: project descendant", "", simple + "state \"A\" { " + state + " user \"createor\" {read} descendant project }"},
                {"111d) single state user: project related",    "", simple + "state \"A\" { " + state + " user \"createor\" {read} related    project }"},
                {"111e) single state user: project single",     "", simple + "state \"A\" { " + state + " user \"createor\" {read} single     project }"},

                // owner
                {"120a) all state user: owner any", simple + "allstate { user \"createor\" {read} }", simple + "allstate { user \"createor\" {read} any owner }"},
                {"120b) all state user: owner context", "", simple + "allstate { user \"createor\" {read} context owner }"},
                {"121a) single state user: owner any", simple + "state \"A\" { " + state + " user \"createor\" {read} }", simple + "state \"A\" { " + state + " user \"createor\" {read} any owner }"},
                {"121b) single state user: owner context", "", simple + "state \"A\" { " + state + " user \"createor\" {read} context owner }"},

                // reserve
                {"130a) all state user: reserve any", simple + "allstate { user \"createor\" {read} }", simple + "allstate { user \"createor\" {read} any reserve }"},
                {"130b) all state user: reserve context",      "", simple + "allstate { user \"createor\" {read} context   reserve }"},
                {"130c) all state user: reserve no",           "", simple + "allstate { user \"createor\" {read} no        reserve }"},
                {"130d) all state user: reserve inclusive",    "", simple + "allstate { user \"createor\" {read} inclusive reserve }"},
                {"131a) single state user: reserve any", simple + "state \"A\" { " + state + " user \"createor\" {read} }", simple + "state \"A\" { " + state + " user \"createor\" {read} any reserve }"},
                {"131b) single state user: reserve context",   "", simple + "state \"A\" { " + state + " user \"createor\" {read} context   reserve }"},
                {"131c) single state user: reserve no",        "", simple + "state \"A\" { " + state + " user \"createor\" {read} no        reserve }"},
                {"131d) single state user: reserve inclusive", "", simple + "state \"A\" { " + state + " user \"createor\" {read} inclusive reserve }"},

                // maturity
                {"134a) all state user: maturity any", simple + "allstate { user \"createor\" {read} }", simple + "allstate { user \"createor\" {read} any maturity }"},
                {"140b) all state user: maturity no",         "", simple + "allstate { user \"createor\" {read} no         maturity }"},
                {"140c) all state user: maturity public",     "", simple + "allstate { user \"createor\" {read} public     maturity }"},
                {"140d) all state user: maturity protected",  "", simple + "allstate { user \"createor\" {read} protected  maturity }"},
                {"140e) all state user: maturity private",    "", simple + "allstate { user \"createor\" {read} private    maturity }"},
                {"140f) all state user: maturity notprivate", "", simple + "allstate { user \"createor\" {read} notprivate maturity }"},
                {"140g) all state user: maturity ppp",        "", simple + "allstate { user \"createor\" {read} ppp        maturity }"},
                {"141a) single state user: maturity any", simple + "state \"A\" { " + state + " user \"createor\" {read} }", simple + "state \"A\" { " + state + " user \"createor\" {read} any maturity }"},
                {"141b) single state user: maturity no",         "", simple + "state \"A\" { " + state + " user \"createor\" {read} no         maturity }"},
                {"141c) single state user: maturity public",     "", simple + "state \"A\" { " + state + " user \"createor\" {read} public     maturity }"},
                {"141d) single state user: maturity protected",  "", simple + "state \"A\" { " + state + " user \"createor\" {read} protected  maturity }"},
                {"141e) single state user: maturity private",    "", simple + "state \"A\" { " + state + " user \"createor\" {read} private    maturity }"},
                {"141f) single state user: maturity notprivate", "", simple + "state \"A\" { " + state + " user \"createor\" {read} notprivate maturity }"},
                {"141g) single state user: maturity ppp",        "", simple + "state \"A\" { " + state + " user \"createor\" {read} ppp        maturity }"},
                // check public access after owner access
                {"142a) all state public after owner (to ensure only public maturity is interpereted as maturity)", "", simple + "allstate { owner {read} public {read} }"},
                {"142b) single state public after owner (to ensure only public maturity is interpereted as maturity)", "", simple + "state \"A\" { " + state + " owner {read} public {read} }"},

                // category
                {"150a) all state user: category any", simple + "allstate { user \"createor\" {read} }", simple + "allstate { user \"createor\" {read} any category }"},
                {"150b) all state user: category oem",         "", simple + "allstate { user \"createor\" {read} oem         category }"},
                {"150c) all state user: category goldpartner", "", simple + "allstate { user \"createor\" {read} goldpartner category }"},
                {"150d) all state user: category partner",     "", simple + "allstate { user \"createor\" {read} partner     category }"},
                {"150e) all state user: category supplier",    "", simple + "allstate { user \"createor\" {read} supplier    category }"},
                {"150f) all state user: category customer",    "", simple + "allstate { user \"createor\" {read} customer    category }"},
                {"150g) all state user: category contractor",  "", simple + "allstate { user \"createor\" {read} contractor  category }"},
                {"151a) single state user: category any", simple + "state \"A\" { " + state + " user \"createor\" {read} }", simple + "state \"A\" { " + state + " user \"createor\" {read} any category }"},
                {"151b) single state user: category oem",         "", simple + "state \"A\" { " + state + " user \"createor\" {read} oem         category }"},
                {"151c) single state user: category goldpartner", "", simple + "state \"A\" { " + state + " user \"createor\" {read} goldpartner category }"},
                {"151d) single state user: category partner",     "", simple + "state \"A\" { " + state + " user \"createor\" {read} partner     category }"},
                {"151e) single state user: category supplier",    "", simple + "state \"A\" { " + state + " user \"createor\" {read} supplier    category }"},
                {"151f) single state user: category customer",    "", simple + "state \"A\" { " + state + " user \"createor\" {read} customer    category }"},
                {"151g) single state user: category contractor",  "", simple + "state \"A\" { " + state + " user \"createor\" {read} contractor  category }"},
        };
    }

    @Override
    protected Policy_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Policy_mxJPO(_name);
    }
}
