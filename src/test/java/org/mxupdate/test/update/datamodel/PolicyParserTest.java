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

package org.mxupdate.test.update.datamodel;

import java.lang.reflect.Method;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the policy parser.
 *
 * @author The MxUpdate Team
 */
public class PolicyParserTest
    extends AbstractTest
{
    /** Name of the test dimension. */
    private static final String POLICY_NAME = AbstractTest.PREFIX + "_Test";

    /** Start of the command to update the dimension to extract the code. */
    private static final String START_INDEX = "mxUpdate policy \"${NAME}\" {";
    /** Length of the string of the command to update the dimension. */
    private static final int START_INDEX_LENGTH = PolicyParserTest.START_INDEX.length();

    /**
     * Returns data providers used for testing parses.
     *
     * @return test source code to parse
     */
    @DataProvider(name = "data")
    public Object[][] getCodes()
    {
        return new Object[][]{
                new Object[]{
                        "1) simple policy",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\""},
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
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" property \"{}\\\"\""},
                new Object[]{
                        "5b) policy with property and value special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" property \"{}\\\"\" value \"{}\\\"\""},
                new Object[]{
                        "5c) policy with property link special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" property \"{}\\\"\" to type \"{}\\\"\""},
                new Object[]{
                        "5d) policy with property link and value special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
                // policy state with special characters
                new Object[]{
                        "6a) policy with state symbolic name special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"{}\\\"\" { registeredName \"{}\\\"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "6b) policy with state action special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"{}\\\"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "6c) policy with state action input special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"A\" input \"{}\\\"\" check \"\" input \"\" }"},
                new Object[]{
                        "6d) policy with state check special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"{}\\\"\" input \"\" }"},
                new Object[]{
                        "6e) policy with state check input special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"A\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"A\" input \"{}\\\"\" }"},
                // policy state property
                new Object[]{
                        "7a) policy with state property special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" }"},
                new Object[]{
                        "7b) policy with state property and value special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
                new Object[]{
                        "7c) policy with state property link special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
                new Object[]{
                        "7d) policy with state property link and value special characters",
                        "",
                        "description \"\" hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
                // policy state with enforce flag
                new Object[]{
                        "8a) policy state not enforcereserveaccess",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "8b) policy state not enforcereserveaccess defined as not",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" notenforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "8c) policy state not enforcereserveaccess defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess false majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "8d) policy state not enforcereserveaccess defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" !enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess \"FAlse\" majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "8e) policy state enforcereserveaccess",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "8f) policy state enforcereserveaccess defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess TRUE majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "8g) policy state enforcereserveaccess defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess \"True\" majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with majorrevision flag
                new Object[]{
                        "9a) policy state not majorrevision",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "9b) policy state not majorrevision defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision false minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "9c) policy state not majorrevision defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess !majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision \"FAlse\" minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "9d) policy state majorrevision",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "9e) policy state majorrevision defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision TRUE minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "9f) policy state majorrevision defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision \"True\" minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with minorrevision flag
                new Object[]{
                        "10a) policy state not minorrevision",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "10b) policy state not minorrevision defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision false version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "10c) policy state not minorrevision defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision \"FAlse\" version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "10d) policy state minorrevision",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "10e) policy state minorrevision defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision TRUE version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "10f) policy state minorrevision defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision \"True\" version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with revision flag
                new Object[]{
                        "11a) policy state not revision",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !revision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "11b) policy state not minorrevision defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision false version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "11c) policy state not minorrevision defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision !minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision \"FAlse\" version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "11d) policy state minorrevision",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "11e) policy state minorrevision defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision TRUE version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "11f) policy state minorrevision defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision revision \"True\" version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with version flag
                new Object[]{
                        "12a) policy state not version",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "12b) policy state not version defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version false promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "12c) policy state not version defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision !version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version \"FAlse\" promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "12d) policy state version",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "12e) policy state version defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version TRUE promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "12f) policy state version defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version \"True\" promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with promote flag
                new Object[]{
                        "13a) policy state not promote",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "13b) policy state not promote defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote false checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "13c) policy state not promote defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version !promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote \"FAlse\" checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "13d) policy state promote",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "13e) policy state promote defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote TRUE checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "13f) policy state promote defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote \"True\" checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with checkouthistory flag
                new Object[]{
                        "14a) policy state not checkouthistory",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "14b) policy state not checkouthistory defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory false published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "14c) policy state not checkouthistory defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote !checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory \"FAlse\" published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "14d) policy state checkouthistory",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "14e) policy state checkouthistory defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory TRUE published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "14f) policy state checkouthistory defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory \"True\" published action \"\" input \"\" check \"\" input \"\" }"},
                // policy state with published flag
                new Object[]{
                        "15a) policy state not published",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "15b) policy state not published defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published false action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "15c) policy state not published defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory !published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published \"FAlse\" action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "15d) policy state published",
                        "",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "15e) policy state published defined as value w/o apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published TRUE action \"\" input \"\" check \"\" input \"\" }"},
                new Object[]{
                        "15f) policy state published defined as value with apostrophe",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published action \"\" input \"\" check \"\" input \"\" }",
                        "description \"\" !hidden type {} format {} defaultformat \"\" sequence \"\" store \"\" state \"A\" { registeredName \"\" enforcereserveaccess majorrevision minorrevision version promote checkouthistory published \"True\" action \"\" input \"\" check \"\" input \"\" }"},
        };
    }

    /**
     * Parsed the <code>_definition</code> code and compares the result with
     * <code>_toTest</code>.
     *
     * @param _description  description of the test
     * @param _toTest       expected result (if empty string
     *                      <code>_definition</code> is the expected result)
     * @param _definition   text of the definition to test
     * @throws Exception if <code>_definition</code> could not parsed
     */
    @Test(dataProvider = "data")
    public void positiveTestPolicy(final String _description,
                                   final String _toTest,
                                   final String _definition)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);

        final Policy_mxJPO policy = new Policy_mxJPO(paramCache.getMapping().getTypeDef("Policy"), PolicyParserTest.POLICY_NAME);
        policy.parseUpdate(_definition);

        final StringBuilder generated = new StringBuilder();
        final Method write = policy.getClass()
                .getDeclaredMethod("write", ParameterCache_mxJPO.class, Appendable.class);
        write.setAccessible(true);
        write.invoke(policy, paramCache, generated);

        final StringBuilder oldDefBuilder = new StringBuilder();
        for (final String line : _toTest.isEmpty() ? _definition.split("\n") : _toTest.split("\n"))  {
            oldDefBuilder.append(line.trim()).append(' ');
        }
        int length = 0;
        String oldDef = oldDefBuilder.toString();
        while (length != oldDef.length())  {
            length = oldDef.length();
            oldDef = oldDef.replaceAll("  ", " ");
        }

        final String temp = generated.substring(generated.indexOf(PolicyParserTest.START_INDEX) + PolicyParserTest.START_INDEX_LENGTH + 1, generated.length() - 2).toString();
        final StringBuilder newDef = new StringBuilder();
        for (final String line : temp.split("\n"))  {
            newDef.append(line.trim()).append(' ');
        }

        Assert.assertEquals(newDef.toString().trim(), oldDef.trim());
    }
}
