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

package org.mxupdate.test.test.update.datamodel.relationshipci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Relationship_mxJPO relationship CI} delta calculation.
 *
 * @author The MxUpdate Team
 */
@Test()
public class RelationshipCI_1ParserTest
    extends AbstractParserTest<Relationship_mxJPO>
{
    @Override()
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0) simple",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // registered name
            {"1a) symbolic name",
                    "",
                    "symbolicname \"relationship_abc\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"1b) two symbolic names",
                    "symbolicname \"relationship_abc\" symbolicname \"relationship_def\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "symbolicname \"relationship_def\" symbolicname \"relationship_abc\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // description
            {"2a) description",
                    "",
                    "description \"abc def\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"2b) description",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "!hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"2c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"2d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // kind
            {"3a) basic kind",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "kind basic description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3b) compositional kind",
                    "",
                    "description \"\" kind compositional !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // abstract flag
            {"4a) not abstract",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !abstract !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"4b) abstract",
                    "",
                    "description \"\" abstract hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // derived
            {"5a) derived",
                    "",
                    "description \"\" derived \"123\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"5a) not derived (with empty string)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" derived \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"6a) not hidden",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6b) hidden",
                    "",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6c) default hidden",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"7a) not preventduplicates",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"7b) preventduplicates",
                    "",
                    "description \"\" hidden preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"7c) default preventduplicates",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" hidden "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // rules
            {"8a) rule",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "rule \"111\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"8b) rule name w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "rule \"111\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                            + "rule 111 "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"8c) two rule (and so first definition is removed, because technical not possible)",
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
            {"9a) action trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent action \"{}\\\"\" input \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"9b) action trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "trigger createevent action \"{}\\\"\" input \"\" "
                              + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                                + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent action \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // check trigger
            {"10a) check trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent check \"{}\\\"\" input \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"10b) check trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "trigger createevent check \"{}\\\"\" input \"\" "
                              + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                                + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent check \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // override trigger
            {"11a) override trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent override \"{}\\\"\" input \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"11b) override trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "trigger createevent override \"{}\\\"\" input \"\" "
                              + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                                + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent override \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // attribute
            {"12a) attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "attribute \"111\""},
            {"12b) attribute name w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "attribute \"111\"",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "attribute 111"},
            {"12c) two attributes (to check sort)",
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
            {"13a) property special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "property \"{}\\\"\""},
            {"13b) property and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "property \"{}\\\"\" value \"{}\\\"\""},
            {"13c) property link special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "property \"{}\\\"\" to type \"{}\\\"\""},
            {"13d) property link and value special characters",
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
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify  propagateconnection } "
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
                            + "  to { meaning \"\" cardinality many               clone none !propagatemodify !propagateconnection }"},
            // to clone
            {"33a) to: float clone",
                    "",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone float !propagatemodify !propagateconnection }"},
            {"33b) to: replicate clone",
                    "",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone replicate !propagatemodify !propagateconnection }"},
            {"33c) to: default clone",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none            !propagatemodify !propagateconnection }"},
            // to propagatemodify flag
            {"34a) to: propagatemodify",
                    "",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection }"},
            {"34b) to: default propagatemodify",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none                  !propagateconnection }"},
            // to propagateconnection flag
            {"35a) to: propagateconnection",
                    "",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none  propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify  propagateconnection }"},
            {"35b) to: default propagateconnection",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify  propagateconnection }",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify                      }"},
            // to type
            {"36a) from: all type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type all }"},
            {"36b) from: 1 type",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" }"},
            {"36c) from: 1 type w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type \"ABC\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection type ABC }"},
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

    @Override()
    protected Relationship_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                               final String _name)
    {
        return new Relationship_mxJPO(_paramCache.getMapping().getTypeDef(CI.DM_RELATIONSHIP.updateType), _name);
    }
}
