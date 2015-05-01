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

package org.mxupdate.test.test.update.datamodel;

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
@Test()
public class PolicyCI_1ParserTest
    extends AbstractParserTest<Policy_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        final String simple = "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" ";
        final String state  = "registeredName \"\" !enforcereserveaccess !majorrevision !minorrevision !version !promote !checkouthistory !published ";

        return new Object[][]{
                new Object[]{
                        "1) simple policy",
                        "",
                        simple},
                // hidden flag
                new Object[]{
                        "2a) simple not hidden policy",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "2b) not hidden policy defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" hidden false type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "2c) not hidden policy defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" hidden \"FAlse\" type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "2d) hidden policy",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "2e) hidden hidden defined as value w/o apostrophe",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" hidden TRUE type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "2f) hidden policy defined as value with apostrophe",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" hidden \"True\" type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                // enforce flag
                new Object[]{
                        "3a) not enforce policy",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" !hidden type {} format {} defaultformat \"\" !enforce sequence \"\" store \"\""},
                new Object[]{
                        "3b) not enforce policy defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce false sequence \"\" store \"\""},
                new Object[]{
                        "3c) not enforce policy defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\"",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce \"FAlse\" sequence \"\" store \"\""},
                new Object[]{
                        "3d) enforce policy",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce sequence \"\" store \"\""},
                new Object[]{
                        "3e) enforce policy defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce sequence \"\" store \"\"",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce TRUE sequence \"\" store \"\""},
                new Object[]{
                        "3f) enforce policy defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce sequence \"\" store \"\"",
                        "description \"\" !hidden type {} format {} defaultformat \"\" enforce \"True\" sequence \"\" store \"\""},
                // special characters
                new Object[]{
                        "4a) policy with description special characters",
                        "",
                        "description \"{}\\\"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "4b) policy with type special characters",
                        "",
                        "description \"\" hidden type {\"{}\\\"\"} format {} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "4c) policy with format special characters",
                        "",
                        "description \"\" hidden type {} format {\"{}\\\"\"} defaultformat \"\" sequence \"\" store \"\""},
                new Object[]{
                        "4d) policy with defaultformat special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"{}\\\"\" sequence \"\" store \"\""},
                new Object[]{
                        "4e) policy with sequence special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"{}\\\"\" store \"\""},
                new Object[]{
                        "4f) policy with store special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"{}\\\"\""},
                new Object[]{
                        "4g) policy with delimeter / minor special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" delimiter . minorsequence \"{}\\\"\" majorsequence \"{}\\\"\" store \"\""},
                // policy property
                new Object[]{
                        "5a) policy with property special characters",
                        "",
                        simple + "property \"{}\\\"\""},
                new Object[]{
                        "5b) policy with property and value special characters",
                        "",
                        simple + "property \"{}\\\"\" value \"{}\\\"\""},
                new Object[]{
                        "5c) policy with property link special characters",
                        "",
                        simple + "property \"{}\\\"\" to type \"{}\\\"\""},
                new Object[]{
                        "5d) policy with property link and value special characters",
                        "",
                        simple + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
                // policy state with special characters
                new Object[]{
                        "6a) policy with state symbolic name special characters",
                        "",
                        simple + "state \"{}\\\"\" { registeredName \"{}\\\"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "6b) policy with state action",
                        "",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"AAA\" input \"\" }"},
                new Object[]{
                        "6c) policy with empty state action",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" }"},
                new Object[]{
                        "6d) policy with state action input",
                        "",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"AAA\" }"},
                new Object[]{
                        "6e) policy with state check",
                        "",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published check \"AAA\" input \"\" }"},
                new Object[]{
                        "6f) policy with empty state check",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published check \"\" input \"\" }"},
                new Object[]{
                        "6g) policy with state check input",
                        "",
                        simple + "state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published check \"\" input \"AAA\" }"},
                // policy state property
                new Object[]{
                        "7a) policy with state property special characters",
                        "",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" }"},
                new Object[]{
                        "7b) policy with state property and value special characters",
                        "",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" value \"{}\\\"\" }"},
                new Object[]{
                        "7c) policy with state property link special characters",
                        "",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" to type \"{}\\\"\" }"},
                new Object[]{
                        "7d) policy with state property link and value special characters",
                        "",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
                // policy state with enforce flag
                new Object[]{
                        "8a) policy state not enforcereserveaccess",
                        "",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "8b) policy state not enforcereserveaccess defined as not",
                        simple + "state \"A\" { registeredName \"\"   !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" notenforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "8c) policy state not enforcereserveaccess defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess false majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "8d) policy state not enforcereserveaccess defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess \"FAlse\" majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "8e) policy state enforcereserveaccess",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "8f) policy state enforcereserveaccess defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess TRUE majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "8g) policy state enforcereserveaccess defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess \"True\" majorrevision minorrevision version promote checkouthistory published }"},
                // policy state with majorrevision flag
                new Object[]{
                        "9a) policy state not majorrevision",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "9b) policy state not majorrevision defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision false minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "9c) policy state not majorrevision defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision \"FAlse\" minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "9d) policy state majorrevision",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "9e) policy state majorrevision defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision TRUE minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "9f) policy state majorrevision defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision \"True\" minorrevision version promote checkouthistory published }"},
                // policy state with minorrevision flag
                new Object[]{
                        "10a) policy state not minorrevision",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "10b) policy state not minorrevision defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision false version promote checkouthistory published }"},
                new Object[]{
                        "10c) policy state not minorrevision defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision \"FAlse\" version promote checkouthistory published }"},
                new Object[]{
                        "10d) policy state minorrevision",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "10e) policy state minorrevision defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision TRUE version promote checkouthistory published }"},
                new Object[]{
                        "10f) policy state minorrevision defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision \"True\" version promote checkouthistory published }"},
                // policy state with revision flag
                new Object[]{
                        "11a) policy state not revision",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !revision version promote checkouthistory published }"},
                new Object[]{
                        "11b) policy state not minorrevision defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision false version promote checkouthistory published }"},
                new Object[]{
                        "11c) policy state not minorrevision defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision \"FAlse\" version promote checkouthistory published }"},
                new Object[]{
                        "11d) policy state minorrevision",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision version promote checkouthistory published }"},
                new Object[]{
                        "11e) policy state minorrevision defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision TRUE version promote checkouthistory published }"},
                new Object[]{
                        "11f) policy state minorrevision defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision \"True\" version promote checkouthistory published }"},
                // policy state with version flag
                new Object[]{
                        "12a) policy state not version",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published }"},
                new Object[]{
                        "12b) policy state not version defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version false promote checkouthistory published }"},
                new Object[]{
                        "12c) policy state not version defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version \"FAlse\" promote checkouthistory published }"},
                new Object[]{
                        "12d) policy state version",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "12e) policy state version defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version TRUE promote checkouthistory published }"},
                new Object[]{
                        "12f) policy state version defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version \"True\" promote checkouthistory published }"},
                // policy state with promote flag
                new Object[]{
                        "13a) policy state not promote",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published }"},
                new Object[]{
                        "13b) policy state not promote defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote false checkouthistory published }"},
                new Object[]{
                        "13c) policy state not promote defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote \"FAlse\" checkouthistory published }"},
                new Object[]{
                        "13d) policy state promote",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "13e) policy state promote defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote TRUE checkouthistory published }"},
                new Object[]{
                        "13f) policy state promote defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote \"True\" checkouthistory published }"},
                // policy state with checkouthistory flag
                new Object[]{
                        "14a) policy state not checkouthistory",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published }"},
                new Object[]{
                        "14b) policy state not checkouthistory defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory false published }"},
                new Object[]{
                        "14c) policy state not checkouthistory defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory \"FAlse\" published }"},
                new Object[]{
                        "14d) policy state checkouthistory",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "14e) policy state checkouthistory defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory TRUE published }"},
                new Object[]{
                        "14f) policy state checkouthistory defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory \"True\" published }"},
                // policy state with published flag
                new Object[]{
                        "15a) policy state not published",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published }"},
                new Object[]{
                        "15b) policy state not published defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published false }"},
                new Object[]{
                        "15c) policy state not published defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published \"FAlse\" }"},
                new Object[]{
                        "15d) policy state published",
                        "",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }"},
                new Object[]{
                        "15e) policy state published defined as value w/o apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published TRUE }"},
                new Object[]{
                        "15f) policy state published defined as value with apostrophe",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published }",
                        simple + "state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published \"True\" }"},

                // all state owner
                new Object[]{"20a) all state owner", "", simple + "allstate {              owner             {read} }"},
                new Object[]{"20b) all state owner", "", simple + "allstate {        login owner             {read} }"},
                new Object[]{"20c) all state owner", "", simple + "allstate { revoke       owner             {read} }"},
                new Object[]{"20d) all state owner", "", simple + "allstate { revoke login owner             {read} }"},
                new Object[]{"20e) all state owner", "", simple + "allstate {              owner key \"key\" {read} }"},
                new Object[]{"20f) all state owner", "", simple + "allstate {        login owner key \"key\" {read} }"},
                new Object[]{"20g) all state owner", "", simple + "allstate { revoke       owner key \"key\" {read} }"},
                new Object[]{"20h) all state owner", "", simple + "allstate { revoke login owner key \"key\" {read} }"},
                // all state public
                new Object[]{"21a) all state public", "", simple + "allstate {              public             {read} }"},
                new Object[]{"21b) all state public", "", simple + "allstate {        login public             {read} }"},
                new Object[]{"21c) all state public", "", simple + "allstate { revoke       public             {read} }"},
                new Object[]{"21d) all state public", "", simple + "allstate { revoke login public             {read} }"},
                new Object[]{"21e) all state public", "", simple + "allstate {              public key \"key\" {read} }"},
                new Object[]{"21f) all state public", "", simple + "allstate {        login public key \"key\" {read} }"},
                new Object[]{"21g) all state public", "", simple + "allstate { revoke       public key \"key\" {read} }"},
                new Object[]{"21h) all state public", "", simple + "allstate { revoke login public key \"key\" {read} }"},
                // allstate state user
                new Object[]{"22a) all state user", "", simple + "allstate {              user \"createor\"             {read} }"},
                new Object[]{"22b) all state user", "", simple + "allstate {        login user \"createor\"             {read} }"},
                new Object[]{"22c) all state user", "", simple + "allstate { revoke       user \"createor\"             {read} }"},
                new Object[]{"22d) all state user", "", simple + "allstate { revoke login user \"createor\"             {read} }"},
                new Object[]{"22e) all state user", "", simple + "allstate {              user \"createor\" key \"key\" {read} }"},
                new Object[]{"22f) all state user", "", simple + "allstate {        login user \"createor\" key \"key\" {read} }"},
                new Object[]{"22g) all state user", "", simple + "allstate { revoke       user \"createor\" key \"key\" {read} }"},
                new Object[]{"22h) all state user", "", simple + "allstate { revoke login user \"createor\" key \"key\" {read} }"},

                // single state owner
                new Object[]{"30a) single state owner", "", simple + "state \"A\" { " + state + "              owner             {read} }"},
                new Object[]{"30b) single state owner", "", simple + "state \"A\" { " + state + "        login owner             {read} }"},
                new Object[]{"30c) single state owner", "", simple + "state \"A\" { " + state + " revoke       owner             {read} }"},
                new Object[]{"30d) single state owner", "", simple + "state \"A\" { " + state + " revoke login owner             {read} }"},
                new Object[]{"30e) single state owner", "", simple + "state \"A\" { " + state + "              owner key \"key\" {read} }"},
                new Object[]{"30f) single state owner", "", simple + "state \"A\" { " + state + "        login owner key \"key\" {read} }"},
                new Object[]{"30g) single state owner", "", simple + "state \"A\" { " + state + " revoke       owner key \"key\" {read} }"},
                new Object[]{"30h) single state owner", "", simple + "state \"A\" { " + state + " revoke login owner key \"key\" {read} }"},
                // single state public
                new Object[]{"31a) single state public", "", simple + "state \"A\" { " + state + "              public             {read} }"},
                new Object[]{"31b) single state public", "", simple + "state \"A\" { " + state + "        login public             {read} }"},
                new Object[]{"31c) single state public", "", simple + "state \"A\" { " + state + " revoke       public             {read} }"},
                new Object[]{"31d) single state public", "", simple + "state \"A\" { " + state + " revoke login public             {read} }"},
                new Object[]{"31e) single state public", "", simple + "state \"A\" { " + state + "              public key \"key\" {read} }"},
                new Object[]{"31f) single state public", "", simple + "state \"A\" { " + state + "        login public key \"key\" {read} }"},
                new Object[]{"31g) single state public", "", simple + "state \"A\" { " + state + " revoke       public key \"key\" {read} }"},
                new Object[]{"31h) single state public", "", simple + "state \"A\" { " + state + " revoke login public key \"key\" {read} }"},
                // single state user
                new Object[]{"32a) single state user", "", simple + "state \"A\" { " + state + "              user \"createor\"             {read} }"},
                new Object[]{"32b) single state user", "", simple + "state \"A\" { " + state + "        login user \"createor\"             {read} }"},
                new Object[]{"32c) single state user", "", simple + "state \"A\" { " + state + " revoke       user \"createor\"             {read} }"},
                new Object[]{"32d) single state user", "", simple + "state \"A\" { " + state + " revoke login user \"createor\"             {read} }"},
                new Object[]{"32e) single state user", "", simple + "state \"A\" { " + state + "              user \"createor\" key \"key\" {read} }"},
                new Object[]{"32f) single state user", "", simple + "state \"A\" { " + state + "        login user \"createor\" key \"key\" {read} }"},
                new Object[]{"32g) single state user", "", simple + "state \"A\" { " + state + " revoke       user \"createor\" key \"key\" {read} }"},
                new Object[]{"32h) single state user", "", simple + "state \"A\" { " + state + " revoke login user \"createor\" key \"key\" {read} }"},
        };
    }

    @Override()
    protected Policy_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                         final String _name)
    {
        return new Policy_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_POLICY.updateType), _name);
    }
}
