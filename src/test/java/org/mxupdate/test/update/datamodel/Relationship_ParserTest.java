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

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.test.AbstractTest;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the relationship parser test.
 *
 * @author The MxUpdate Team
 */
public class Relationship_ParserTest
    extends AbstractTest
{
    /** Name of the test policy. */
    private static final String POLICY_NAME = AbstractTest.PREFIX + "_Test";

    /**
     * Returns data providers used for testing parses.
     *
     * @return test source code to parse
     */
    @DataProvider(name = "data")
    public Object[][] getCodes()
    {
        return new Object[][]
        {
            {"0) simple",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},

            // description
            {"1a) description",
                "",
                "description \"abc def\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"1a) description",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "!hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"2a) not hidden",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"2b) hidden",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"2c) default hidden",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"3a) not preventduplicates",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3b) preventduplicates",
                "",
                "description \"\" hidden preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3c) default preventduplicates",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" hidden "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // rules
            {"4a) rule",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "rule \"111\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"4b) rule name w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "rule \"111\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "rule 111 "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"4c) two rule (and so first definition is removed, because technical not possible)",
                "description \"\" !hidden !preventduplicates "
                        + "rule \"111\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "rule \"222\" "
                        + "rule \"111\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // action trigger
            {"5a) action trigger with input",
                "",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent action \"{}\\\"\" input \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"5b) action trigger w/o input",
                "description \"\" !hidden !preventduplicates "
                        + "trigger createevent action \"{}\\\"\" input \"\" "
                          + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent action \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // check trigger
            {"6a) check trigger with input",
                "",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent check \"{}\\\"\" input \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6b) check trigger w/o input",
                "description \"\" !hidden !preventduplicates "
                        + "trigger createevent check \"{}\\\"\" input \"\" "
                          + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent check \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // override trigger
            {"7a) override trigger with input",
                "",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent override \"{}\\\"\" input \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"7b) override trigger w/o input",
                "description \"\" !hidden !preventduplicates "
                        + "trigger createevent override \"{}\\\"\" input \"\" "
                          + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent override \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // attribute
            {"8a) attribute",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute \"111\""},
            {"8b) attribute name w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute \"111\"",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute 111"},
            {"8c) two attributes (to check sort)",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute \"111\" "
                        + "attribute \"222\"",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute \"222\" "
                        + "attribute \"111\""},
            // property
            {"9a) property special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\""},
            {"9b) property and value special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\" value \"{}\\\"\""},
            {"9c) property link special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\" to type \"{}\\\"\""},
            {"9d) property link and value special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // from meaning
            {"10a) from: meaning",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\"    cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"10b) from: meaning w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning abc cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"10c) from: default meaning",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from {              cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from cardinality
            {"11a) from: one cardinality",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality one revision none clone none  !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"11b) from: default cardinality",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\"                  revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from revision
            {"12a) from: float revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision float clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"12b) from: replicate revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision replicate clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"12c) from: default revision",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many               clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from clone
            {"13a) from: float clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone float !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"13b) from: replicate clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone replicate !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"13c) from: default clone",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none            !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from propagatemodify flag
            {"14a) from: propagatemodify",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"14b) from: default propagatemodify",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none                  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from propagateconnection flag
            {"15a) from: propagateconnection",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  propagatemodify  propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"15b) from: default propagateconnection",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify                      } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from type
            {"16a) from: all type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type all } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"16b) from: 1 type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"16c) from: 1 type w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type ABC } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"16d) from: 2 types unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" type \"DEF\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"DEF\" type \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from relationship
            {"17a) from: all relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship all } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"17b) from: 1 relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"17c) from: 1 relationship w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship ABC } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"17d) from: 2 relationships unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" relationship \"DEF\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"DEF\" relationship \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // to meaning
            {"20a) to: meaning",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\"    cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"20b) to: meaning w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning abc cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"20c) to: default meaning",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from {              cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // to cardinality
            {"21a) to: one cardinality",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality one revision none clone none !propagatemodify !propagateconnection }"},
            {"21b) to: default cardinality",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\"                  revision none clone none !propagatemodify !propagateconnection }"},
            // to revision
            {"22a) to: float revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision float clone none !propagatemodify !propagateconnection }"},
            {"22b) to: replicate revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision replicate clone none !propagatemodify !propagateconnection }"},
            {"22c) to: default revision",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many               clone none !propagatemodify !propagateconnection }"},
            // to clone
            {"23a) to: float clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone float !propagatemodify !propagateconnection }"},
            {"23b) to: replicate clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone replicate !propagatemodify !propagateconnection }"},
            {"23c) to: default clone",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none            !propagatemodify !propagateconnection }"},
            // to propagatemodify flag
            {"24a) to: propagatemodify",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection }"},
            {"24b) to: default propagatemodify",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none                  !propagateconnection }"},
            // to propagateconnection flag
            {"25a) to: propagateconnection",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify  propagateconnection }"},
            {"25b) to: default propagateconnection",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify                      }"},
            // to type
            {"26a) from: all type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type all }"},
            {"26b) from: 1 type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" }"},
            {"26c) from: 1 type w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type ABC }"},
            {"26d) from: 2 types unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" type \"DEF\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"DEF\" type \"ABC\" }"},
            // to relationship
            {"27a) from: all relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship all }"},
            {"27b) from: 1 relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" }"},
            {"27c) from: 1 relationship w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship ABC }"},
            {"27d) from: 2 relationship unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" relationship \"DEF\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"DEF\" relationship \"ABC\" }"},
        };
    }

    /**
     * Parsed the {@code _definition} code and compares the result with
     * {@code _toTest}.
     *
     * @param _description  description of the test
     * @param _toTest       expected result (if empty string
     *                      <code>_definition</code> is the expected result)
     * @param _definition   text of the definition to test
     * @throws Exception if <code>_definition</code> could not parsed
     */
    @Test(dataProvider = "data")
    public void positiveTest(final String _description,
                             final String _toTest,
                             final String _definition)
        throws Exception
    {
        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getContext(), false);
        final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef("Relationship");

        final Relationship_mxJPO relationship = new Relationship_mxJPO(typeDef, Relationship_ParserTest.POLICY_NAME);
        relationship.parseUpdate(_definition);

        final StringBuilder generated = new StringBuilder();
        final Method write = relationship.getClass()
                .getDeclaredMethod("write", ParameterCache_mxJPO.class, Appendable.class);
        write.setAccessible(true);
        write.invoke(relationship, paramCache, generated);

        final StringBuilder oldDefBuilder = new StringBuilder();
        for (final String line : _toTest.isEmpty() ? _definition.split("\n") : _toTest.split("\n"))  {
            oldDefBuilder.append(line.trim()).append(' ');
        }
        int length = 0;
        String oldDef = oldDefBuilder.toString();
        while (length != oldDef.length())  {
            length = oldDef.length();
            oldDef = oldDef.replaceAll("    ", " ").replaceAll("  ", " ");
        }


        final String startIndex = "mxUpdate " + typeDef.getMxAdminName() + " \"${NAME}\" {";
        final String temp = generated.substring(generated.indexOf(startIndex) + startIndex.length() + 1, generated.length() - 2).toString();
        final StringBuilder newDef = new StringBuilder();
        for (final String line : temp.split("\n"))  {
            newDef.append(line.trim()).append(' ');
        }

        Assert.assertEquals(newDef.toString().trim(), oldDef.trim());
    }
}
