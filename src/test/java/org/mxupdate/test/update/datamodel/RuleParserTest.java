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

import java.io.StringReader;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.datamodel.Rule_mxJPO;
import org.mxupdate.update.datamodel.rule.RuleDefParser_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link RuleDefParser_mxJPO rule parser}.
 *
 * @author The MxUpdate Team
 */
public class RuleParserTest
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"owner {read}"},
            {"owner key \"test\" {read}"},
            {"public {read}"},
            {"public key \"test\" {read}"},
            {"user \"creator\" {read}"},
            {"user \"creator\" key \"test\" {read}"},
        };
    }

    /**
     * Simple positive test for parsing.
     *
     * @param _code     code to test
     * @throws Exception if test failed
     */
    @Test(description = "simple positive test",
          dataProvider = "data")
    public void positiveTestSimple(final String _code)
        throws Exception
    {
        final RuleDefParser_mxJPO parser = new RuleDefParser_mxJPO(new StringReader(_code));
        parser.parse(new Rule_mxJPO((TypeDef_mxJPO) null, "test"));
    }
}
