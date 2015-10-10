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
@Test()
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
                // registered name
                {"1a) symbolic name",
                        "",
                        "symbolicname \"rule_abc\" description \"\" !hidden !enforcereserveaccess"},
                {"1b) two symbolic names",
                        "symbolicname \"rule_abc\" symbolicname \"rule_def\" description \"\" !hidden !enforcereserveaccess",
                        "symbolicname \"rule_def\" symbolicname \"rule_abc\" description \"\" !hidden !enforcereserveaccess"},
                // description
                {"2a) description",
                        "",
                        "description \"abc def\" !hidden !enforcereserveaccess"},
                {"2b) description not defined",
                        "description \"\" !hidden !enforcereserveaccess",
                        "                 !hidden !enforcereserveaccess"},
                {"2c) multi-line description",
                        "",
                        "description \"abc\ndef\" !hidden !enforcereserveaccess"},
                {"2d) tab's in description",
                        "",
                        "description \"abc\tdef\" !hidden !enforcereserveaccess"},
                // hidden flag
                {"3a) hidden",
                        "",
                        "description \"\"  hidden !enforcereserveaccess"},
                {"3b) not hidden (not defined)",
                        "description \"\" !hidden !enforcereserveaccess",
                        "description \"\"         !enforcereserveaccess"},
                // enforcereserveaccess flag
                {"4a) enforcereserveaccess",
                        "",
                        "description \"\" !hidden enforcereserveaccess"},
                {"4b) not enforcereserveaccess (not defined)",
                        "description \"\" !hidden !enforcereserveaccess",
                        "description \"\" !hidden"},
                // owner access
                {"5a) owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess owner {read}"},
                {"5b) owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess owner key \"test\" {read}"},
                // login owner
                {"5c) login owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login owner {read}"},
                {"5d) login owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login owner key \"test\" {read}"},
                // revoke owner
                {"5c) revoke owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke owner {read}"},
                {"5d) revoke owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke owner key \"test\" {read}"},
                // revoke login owner
                {"5c) revoke login owner access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login owner {read}"},
                {"5d) revoke login owner with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login owner key \"test\" {read}"},
                // public
                {"6a) public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess public {read}"},
                {"6b) public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess public key \"test\" {read}"},
                // login public
                {"7a) login public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login public {read}"},
                {"7b) login public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login public key \"test\" {read}"},
                // revoke public
                {"8a) revoke public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke public {read}"},
                {"8b) revoke public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke public key \"test\" {read}"},
                // revoke login public
                {"9a) revoke login public access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login public {read}"},
                {"9b) revoke login public with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login public key \"test\" {read}"},
                // user access
                {"10a) user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read}"},
                {"10b) user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" key \"test\" {read}"},
                {"10c) user with filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} filter \"abc def\""},
                {"10d) user with multi-line-filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} filter \"abc\ndef\""},
                {"10e) user with filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} localfilter \"abc def\""},
                {"10f) user with multi-line-filter",
                        "",
                        "description \"\"  hidden !enforcereserveaccess user \"creator\" {read} localfilter \"abc\ndef\""},
                // login user access
                {"11a) login user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login user \"creator\" {read}"},
                {"11b) login user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess login user \"creator\" key \"test\" {read}"},
                // revoke user access
                {"12a) revoke user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke user \"creator\" {read}"},
                {"12b) revoke user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke user \"creator\" key \"test\" {read}"},
                // revoke login user access
                {"13a) revoke login user acess",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login user \"creator\" {read}"},
                {"13b) revoke login user with key access",
                        "",
                        "description \"\"  hidden !enforcereserveaccess revoke login user \"creator\" key \"test\" {read}"},
                // access sorting
                {"14) access sorting incl. non / revoke / login",
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
