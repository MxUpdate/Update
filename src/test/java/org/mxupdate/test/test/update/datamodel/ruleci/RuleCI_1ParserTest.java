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

package org.mxupdate.test.test.update.datamodel.ruleci;

import org.mxupdate.test.test.update.AbstractParserTest;
import org.mxupdate.update.datamodel.Rule_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link Rule_mxJPO rule CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class RuleCI_1ParserTest
    extends AbstractParserTest<Rule_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
                {"0) simple",
                        "",
                        "description \"\" !hidden !enforcereserveaccess"},
                // uuid
                {"1a) uuid with minus separator",
                        "",
                        "uuid \"FDA75674-9792-11E6-AE22-56B6B6499611\" description \"\" !hidden !enforcereserveaccess"},
                {"1b) uuid w/o minus separator",
                        "",
                        "uuid \"FDA75674979211E6AE2256B6B6499611\"     description \"\" !hidden !enforcereserveaccess"},
                {"1c) uuid convert from single to string",
                        "uuid \"FDA7-5674979211-E6AE2256B6-B6499611\"  description \"\" !hidden !enforcereserveaccess",
                        "uuid   FDA7-5674979211-E6AE2256B6-B6499611    description \"\" !hidden !enforcereserveaccess"},
                // registered name
                {"2a) symbolic name",
                        "",
                        "symbolicname \"rule_abc\" description \"\" !hidden !enforcereserveaccess"},
                {"2b) two symbolic names",
                        "symbolicname \"rule_abc\" symbolicname \"rule_def\" description \"\" !hidden !enforcereserveaccess",
                        "symbolicname \"rule_def\" symbolicname \"rule_abc\" description \"\" !hidden !enforcereserveaccess"},
                // description
                {"3a) description",
                        "",
                        "description \"abc def\" !hidden !enforcereserveaccess"},
                {"3b) description not defined",
                        "description \"\" !hidden !enforcereserveaccess",
                        "                 !hidden !enforcereserveaccess"},
                {"3c) multi-line description",
                        "",
                        "description \"abc\ndef\" !hidden !enforcereserveaccess"},
                {"3d) tab's in description",
                        "",
                        "description \"abc\\tdef\" !hidden !enforcereserveaccess"},
                // hidden flag
                {"4a) hidden",
                        "",
                        "description \"\"  hidden !enforcereserveaccess"},
                {"4b) not hidden (not defined)",
                        "description \"\" !hidden !enforcereserveaccess",
                        "description \"\"         !enforcereserveaccess"},
                // enforcereserveaccess flag
                {"5a) enforcereserveaccess",
                        "",
                        "description \"\" !hidden enforcereserveaccess"},
                {"5b) not enforcereserveaccess (not defined)",
                        "description \"\" !hidden !enforcereserveaccess",
                        "description \"\" !hidden"},
                // owner access
                {"6a) owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess owner {read}"},
                {"6b) owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess owner key \"test\" {read}"},
                // login owner
                {"7a) login owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login owner {read}"},
                {"7b) login owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login owner key \"test\" {read}"},
                // revoke owner
                {"8a) revoke owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke owner {read}"},
                {"8b) revoke owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke owner key \"test\" {read}"},
                // revoke login owner
                {"9a) revoke login owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login owner {read}"},
                {"9b) revoke login owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login owner key \"test\" {read}"},
                // public
                {"10a) public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess public {read}"},
                {"10b) public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess public key \"test\" {read}"},
                // login public
                {"11a) login public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login public {read}"},
                {"11b) login public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login public key \"test\" {read}"},
                // revoke public
                {"12a) revoke public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke public {read}"},
                {"12b) revoke public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke public key \"test\" {read}"},
                // revoke login public
                {"13a) revoke login public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login public {read}"},
                {"13b) revoke login public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login public key \"test\" {read}"},
                // user access
                {"14a) user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read}"},
                {"14b) user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" key \"test\" {read}"},
                {"14c) user with filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} filter \"abc def\""},
                {"14d) user with multi-line-filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} filter \"abc\ndef\""},
                {"14e) user with filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} localfilter \"abc def\""},
                {"14f) user with multi-line-filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} localfilter \"abc\ndef\""},
                // login user access
                {"15a) login user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login user \"creator\" {read}"},
                {"15b) login user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login user \"creator\" key \"test\" {read}"},
                // revoke user access
                {"16a) revoke user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke user \"creator\" {read}"},
                {"16b) revoke user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke user \"creator\" key \"test\" {read}"},
                // revoke login user access
                {"17a) revoke login user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login user \"creator\" {read}"},
                {"17b) revoke login user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login user \"creator\" key \"test\" {read}"},
                // access sorting
                {"18) access sorting incl. non / revoke / login",
                        "description \"\"  hidden !enforcereserveaccess "
                                + "user \"creator\" key \"test\" {read} "
                                + "login user \"creator\" key \"test\" {read} "
                                + "revoke user \"creator\" key \"test\" {read} "
                                + "revoke login user \"creator\" key \"test\" {read}",
                        "description \"\"  hidden !enforcereserveaccess "
                                + "user \"creator\" key \"test\" {read}"
                                + "revoke login user \"creator\" key \"test\" {read} "
                                + "login user \"creator\" key \"test\" {read} "
                                + "revoke user \"creator\" key \"test\" {read}"},
        };
    }

    @Override
    protected Rule_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                       final String _name)
    {
        return new Rule_mxJPO(_name);
    }
}
