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
            {"1) simple",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
             // kind
             {"2a) basic kind",
                 "description \"\" !hidden !preventduplicates "
                         + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                           + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                 "kind basic description \"\" !hidden !preventduplicates "
                         + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                           + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
             {"2b) compositional kind",
                 "",
                 "description \"\" kind compositional !hidden !preventduplicates "
                         + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                           + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},

            // description
            {"3a) description",
                "",
                "description \"abc def\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3a) description",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "!hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"4a) not hidden",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"4b) hidden",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"4c) default hidden",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"5a) not preventduplicates",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"5b) preventduplicates",
                "",
                "description \"\" hidden preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"5c) default preventduplicates",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" hidden "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // rules
            {"6a) rule",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "rule \"111\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6b) rule name w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "rule \"111\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "rule 111 "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6c) two rule (and so first definition is removed, because technical not possible)",
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
            {"7a) action trigger with input",
                "",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent action \"{}\\\"\" input \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"7b) action trigger w/o input",
                "description \"\" !hidden !preventduplicates "
                        + "trigger createevent action \"{}\\\"\" input \"\" "
                          + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent action \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // check trigger
            {"8a) check trigger with input",
                "",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent check \"{}\\\"\" input \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"8b) check trigger w/o input",
                "description \"\" !hidden !preventduplicates "
                        + "trigger createevent check \"{}\\\"\" input \"\" "
                          + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent check \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // override trigger
            {"9a) override trigger with input",
                "",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent override \"{}\\\"\" input \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"9b) override trigger w/o input",
                "description \"\" !hidden !preventduplicates "
                        + "trigger createevent override \"{}\\\"\" input \"\" "
                          + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                      + "trigger createevent override \"{}\\\"\" "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // attribute
            {"10a) attribute",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute \"111\""},
            {"10b) attribute name w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute \"111\"",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "attribute 111"},
            {"10c) two attributes (to check sort)",
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
            {"11a) property special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\""},
            {"11b) property and value special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\" value \"{}\\\"\""},
            {"11c) property link special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\" to type \"{}\\\"\""},
            {"11d) property link and value special characters",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\""},
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // from meaning
            {"20a) from: meaning",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\"    cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"20b) from: meaning w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning abc cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"20c) from: default meaning",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from {              cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from cardinality
            {"21a) from: one cardinality",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality one revision none clone none  !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"21b) from: default cardinality",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\"                  revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from revision
            {"22a) from: float revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision float clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"22b) from: replicate revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision replicate clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"22c) from: default revision",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many               clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from clone
            {"23a) from: float clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone float !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"23b) from: replicate clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone replicate !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"23c) from: default clone",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none            !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from propagatemodify flag
            {"24a) from: propagatemodify",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"24b) from: default propagatemodify",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none                  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from propagateconnection flag
            {"25a) from: propagateconnection",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  propagatemodify  propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"25b) from: default propagateconnection",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify                      } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from type
            {"26a) from: all type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type all } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"26b) from: 1 type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"26c) from: 1 type w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type ABC } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"26d) from: 2 types unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" type \"DEF\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"DEF\" type \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // from relationship
            {"27a) from: all relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship all } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"27b) from: 1 relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"27c) from: 1 relationship w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship ABC } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"27d) from: 2 relationships unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" relationship \"DEF\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"DEF\" relationship \"ABC\" } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // to meaning
            {"30a) to: meaning",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\"    cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"30b) to: meaning w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"abc\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning abc cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"30c) to: default meaning",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from {              cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // to cardinality
            {"31a) to: one cardinality",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality one revision none clone none !propagatemodify !propagateconnection }"},
            {"31b) to: default cardinality",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                          + "to { meaning \"\"                  revision none clone none !propagatemodify !propagateconnection }"},
            // to revision
            {"32a) to: float revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision float clone none !propagatemodify !propagateconnection }"},
            {"32b) to: replicate revision",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision replicate clone none !propagatemodify !propagateconnection }"},
            {"32c) to: default revision",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many               clone none !propagatemodify !propagateconnection }"},
            // to clone
            {"33a) to: float clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone float !propagatemodify !propagateconnection }"},
            {"33b) to: replicate clone",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone replicate !propagatemodify !propagateconnection }"},
            {"33c) to: default clone",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none            !propagatemodify !propagateconnection }"},
            // to propagatemodify flag
            {"34a) to: propagatemodify",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection }"},
            {"34b) to: default propagatemodify",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none                  !propagateconnection }"},
            // to propagateconnection flag
            {"35a) to: propagateconnection",
                "",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify  propagateconnection }"},
            {"35b) to: default propagateconnection",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                "description \"\" hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify                      }"},
            // to type
            {"36a) from: all type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type all }"},
            {"36b) from: 1 type",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" }"},
            {"36c) from: 1 type w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type ABC }"},
            {"36d) from: 2 types unsorted",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" type \"DEF\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"DEF\" type \"ABC\" }"},
            // to relationship
            {"37a) from: all relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship all }"},
            {"37b) from: 1 relationship",
                "",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" }"},
            {"37c) from: 1 relationship w/o apostrophe",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"ABC\" }",
                "description \"\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                          + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship ABC }"},
            {"37d) from: 2 relationship unsorted",
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
