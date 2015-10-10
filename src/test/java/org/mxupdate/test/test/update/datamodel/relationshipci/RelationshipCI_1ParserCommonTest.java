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
@Test
public class RelationshipCI_1ParserCommonTest
    extends AbstractParserTest<Relationship_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"0) simple",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // uuid
            {"1a) uuid with minus separator",
                    "",
                    "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"1b) uuid w/o minus separator",
                    "",
                    "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"1c) uuid convert from single to string",
                    "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // registered name
            {"2a) symbolic name",
                    "",
                    "symbolicname \"relationship_abc\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"2b) two symbolic names",
                    "symbolicname \"relationship_abc\" symbolicname \"relationship_def\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "symbolicname \"relationship_def\" symbolicname \"relationship_abc\" description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3b) description",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "!hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"3d) tab's in description",
                    "",
                    "description \"abc\tdef\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // kind
            {"4a) basic kind",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "kind basic description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"4b) compositional kind",
                    "",
                    "description \"\" kind compositional !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // abstract flag
            {"5a) not abstract",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !abstract !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"5b) abstract",
                    "",
                    "description \"\" abstract hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // derived
            {"6a) derived",
                    "",
                    "description \"\" derived \"123\" !hidden !preventduplicates "
                            + "from { } "
                            + "  to { }"},
            {"6b) not derived (with empty string)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" derived \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6c) derived with defined from/to properties (to check that they are removed)",
                "description \"\" derived \"abc\" !hidden !preventduplicates "
                        + "from { } "
                        + "  to { }",
                "description \"\" derived \"abc\" !hidden !preventduplicates "
                        + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                        + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"6d) derived with from+to type all",
                "",
                "description \"\" derived \"123\" !hidden !preventduplicates "
                        + "from { type all } "
                        + "  to { type all }"},
            {"6e) derived with from+to type",
                "",
                "description \"\" derived \"123\" !hidden !preventduplicates "
                        + "from { type \"abc\" } "
                        + "  to { type \"def\" }"},
            {"6f) derived with from+to relationship all",
                "",
                "description \"\" derived \"123\" !hidden !preventduplicates "
                        + "from { relationship all } "
                        + "  to { relationship all }"},
            {"6g) derived with from+to relationship",
                "",
                "description \"\" derived \"123\" !hidden !preventduplicates "
                        + "from { relationship \"abc\" } "
                        + "  to { relationship \"def\" }"},
            // hidden flag
            {"7a) not hidden",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"7b) hidden",
                    "",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"7c) default hidden",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // hidden flag
            {"8a) not preventduplicates",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"8b) preventduplicates",
                    "",
                    "description \"\" hidden preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"8c) default preventduplicates",
                    "description \"\" hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" hidden "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // rules
            {"9a) rule",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "rule \"111\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"9b) rule name w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "rule \"111\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                            + "rule 111 "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"9c) two rule (and so first definition is removed, because technical not possible)",
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
            {"10a) action trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent action \"{}\\\"\" input \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"10b) action trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "trigger createevent action \"{}\\\"\" input \"\" "
                              + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                                + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent action \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // check trigger
            {"11a) check trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent check \"{}\\\"\" input \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"11b) check trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "trigger createevent check \"{}\\\"\" input \"\" "
                              + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                                + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent check \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // override trigger
            {"12a) override trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent override \"{}\\\"\" input \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"12b) override trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "trigger createevent override \"{}\\\"\" input \"\" "
                              + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                                + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
                          + "trigger createevent override \"{}\\\"\" "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            // global attribute
            {"13a) global attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "attribute \"111\""},
            {"13b) global attribute name w/o apostrophe",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "attribute \"111\"",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "attribute 111"},
            {"13c) global two attributes (to check sort)",
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
            {"14a) property special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "property \"{}\\\"\""},
            {"14b) property and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "property \"{}\\\"\" value \"{}\\\"\""},
            {"14c) property link special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "property \"{}\\\"\" to type \"{}\\\"\""},
            {"14d) property link and value special characters",
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
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning abc cardinality many revision none clone none !propagatemodify  !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }"},
            {"20c) from: default meaning",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                              + "to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection }",
                    "description \"\" !hidden !preventduplicates "
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
                            + "  to { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection relationship \"DEF\" relationship \"ABC\" }"},

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // local attribute

            // general attribute definition
            {"100a) local binary attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind binary  description \"\" !hidden             !resetonclone !resetonrevision                                    default \"\" }"},
            {"100b) local boolean attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                                    default \"\" }"},
            {"100c) local date attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind date    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"100d) local integer attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"100e) local real attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind real    description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue                        default \"\" }"},
            {"100f) local string attribute",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string  description \"\" !hidden !multivalue !resetonclone !resetonrevision             !multiline maxlength 0 default \"\" }"},

             // attribute registered name
            {"101a) attribute symbolic name",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\"                                description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"101b) attribute two symbolic names",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_abc\" symbolicname \"attribute_def\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean symbolicname \"attribute_def\" symbolicname \"attribute_abc\" description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute description
            {"102a) attribute description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"abc def\"  !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"102b) attribute description not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean                          !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"102c) multi-line attribute description",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"abc\ndef\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute hidden flag
            {"103a) attribute hidden",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" hidden  !multivalue !resetonclone !resetonrevision default \"\" }"},
            {"103b) attribute not hidden (not defined)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\"         !multivalue !resetonclone !resetonrevision default \"\" }"},

            // attribute multivalue flag
            {"104a) attribute multivalue flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden multivalue  !resetonclone !resetonrevision default \"\" }"},
            {"104b) attribute multivalue flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden             !resetonclone !resetonrevision default \"\" }"},

            // attribute resetonclone flag
            {"105a) attribute resetonclone flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue resetonclone  !resetonrevision default \"\" }"},
            {"105b) attribute resetonclone flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue               !resetonrevision default \"\" }"},

            // attribute resetonrevision flag
            {"106a) attribute resetonrevision flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone resetonrevision  default \"\" }"},
            {"106b) attribute resetonrevision !preventduplicates flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone                  default \"\" }"},

            // attribute default value
            {"107a) attribute default value",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc def\"  }"},
            {"107b) attribute default value not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\"         }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                      }"},
            {"107c) multi-line attribute default value",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"abc\ndef\" }"},

            // real attribute rangevalue flag
            {"108a) real attribute rangevalue flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision rangevalue  default \"\" }"},
            {"108b) real attribute rangevalue flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision             default \"\" }"},

            // string attribute multiline flag
            {"109a) attribute multiline flag",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 0 default \"\" }"},
            {"109b) attribute multiline flag not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0 default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision            maxlength 0 default \"\" }"},

             // string attribute maxlength
            {"110a) attribute maxlength",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision multiline  maxlength 125 default \"\" }"},
            {"110b) attribute maxlength not defined",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline maxlength 0   default \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind string description \"\" !hidden !multivalue !resetonclone !resetonrevision !multiline               default \"\" }"},

            // attribute rule
            {"111a) attribute rule",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\"            default \"\"  }"},
            {"111a) attribute rule list (if more than one none)",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision                       default \"\"  }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision rule \"A\" rule \"B\" default \"\"  }"},

            // attribute dimension
            {"112a) real attribute dimension",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind real description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue dimension \"DIM\" default \"\"  }"},

            // attribute action trigger
            {"120a) action trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"{}\\\"\" }"},
            {"120b) action trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify action \"{}\\\"\" }"},
            //  attribute check trigger
            {"121a) check trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"{}\\\"\" }"},
            {"121b) check trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify check \"{}\\\"\" }"},
            //  attribute override trigger
            {"122a) override trigger with input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"{}\\\"\" }"},
            {"122b) override trigger w/o input",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" input \"\" }",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" trigger modify override \"{}\\\"\" }"},

            // attribute ranges
            {"130a) attribute range",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" }"},
            {"130b) attribute range",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range = \"VALUE1\" range = \"VALUE2\" }"},
            {"130c) attribute range >",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range > \"VALUE1\" }"},
            {"130d) attribute range >=",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range >= \"VALUE1\" }"},
            {"130e) attribute range <",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range < \"VALUE1\" }"},
            {"130f) attribute range <=",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range <= \"VALUE1\" }"},
            {"130g) attribute range !=",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range != \"VALUE1\" }"},
            {"130h) attribute range match",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range match \"VALUE1\" }"},
            {"130i) attribute range !match",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !match \"VALUE1\" }"},
            {"130j) attribute range smatch",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range smatch \"VALUE1\" }"},
            {"130k) attribute range !smatch",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range !smatch \"VALUE1\" }"},
            {"130l) attribute range program",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" }"},
            {"130m) attribute range program input",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range program \"VALUE1\" input \"VALUE2\" }"},
            {"130n) attribute range between inclusive",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" inclusive \"VALUE2\" inclusive }"},
            {"130o) attribute range between exclusive",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind integer description \"\" !hidden !multivalue !resetonclone !resetonrevision !rangevalue default \"\" range between \"VALUE1\" exclusive \"VALUE2\" exclusive }"},

            // attribute property
            {"140a) attribute property special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" }"},
            {"140b) attribute property and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" value \"{}\\\"\" }"},
            {"140c) attribute property link special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" }"},
            {"140d) attribute property link and value special characters",
                    "",
                    "description \"\" !hidden !preventduplicates "
                            + "from { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "to   { meaning \"\" cardinality many revision none clone none !propagatemodify !propagateconnection } "
                            + "local attribute \"ATTRNAME\" { kind boolean description \"\" !hidden !multivalue !resetonclone !resetonrevision default \"\" property \"{}\\\"\" to type \"{}\\\"\" value \"{}\\\"\" }"},
        };
    }

    @Override
    protected Relationship_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                               final String _name)
    {
        return new Relationship_mxJPO(_name);
    }
}
