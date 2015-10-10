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

package org.mxupdate.test.test.script;

import java.util.ArrayList;
import java.util.List;

import org.mxupdate.script.ScriptHandler_mxJPO;
import org.mxupdate.script.statement.AbstractStatement_mxJPO;
import org.mxupdate.script.statement.MxUpdateStatement_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link ScriptHandler_mxJPO script handler}.
 *
 * @author The MxUpdate Team
 */
public class ScriptHandlerTest
{
    /**
     * Returns the test scripts to parse.
     *
     * @return test data
     */
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {"1)",
                    "mxUpdate mxUpdateType HELLO{abc}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("mxUpdateType").setName("HELLO").setCode("abc")},
            {"2)",
                    "mxUpdate mxUpdateType HELLO {abc}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("mxUpdateType").setName("HELLO").setCode("abc")},
            {"3)",
                    "mxUpdate mxUpdateType HELLO\t{abc}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("mxUpdateType").setName("HELLO").setCode("abc")},
            {"4) aaaaaa",
                    "mxUpdate mxUpdateType HELLO {  \nasdfasd asdf { } asdf asdf asdfasd fasdfasdf asdf\n}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("mxUpdateType").setName("HELLO").setCode("  \nasdfasd asdf { } asdf asdf asdfasd fasdfasdf asdf\n")},
            {"5)",
                    "mxUpdate mxUpdateType \"HELLO Dummy\" {  \nasdfasd asdf { } asdf\n asdf asdfasd fasdfasdf asdf\n}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("mxUpdateType").setName("HELLO Dummy").setCode("  \nasdfasd asdf { } asdf\n asdf asdfasd fasdfasdf asdf\n")},
            {"6)",
                    "mxUpdate mxUpdateType \"HELLO Dummy\" {asdfasd \"}\"}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("mxUpdateType").setName("HELLO Dummy").setCode("asdfasd \"}\"")},
            {"7) business object",
                    "mxUpdate trigger \"HELLO Name\" \"Revision\" {asdfasd \"}\"}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("trigger").setName("HELLO Name").setRevision("Revision").setCode("asdfasd \"}\"")},
            {"8) with some code and new lines",
                    "mxUpdate trigger \"MyName\" {asdfasd code \"\nnew lines\n\"}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("trigger").setName("MyName").setCode("asdfasd code \"\nnew lines\n\"")},
            {"9) with some code and tabular",
                    "mxUpdate trigger \"MyName\" {asdfasd code \"abc\\tdef\"}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("trigger").setName("MyName").setCode("asdfasd code \"abc\\tdef\"")},
            {"10) with some code and unicode",
                    "mxUpdate trigger \"MyName\" {asdfasd code \"abc\\u1234def\"}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("trigger").setName("MyName").setCode("asdfasd code \"abc\\u1234def\"")},
            {"11) with some code and embeded quotations",
                    "mxUpdate trigger \"MyName\" { asdfasd code \" \\\"new quotion\\\"\"\n}\n",
                    new MxUpdateStatement_mxJPO().setMxUpdateType("trigger").setName("MyName").setCode(" asdfasd code \" \\\"new quotion\\\"\"\n")},
       };
    }

    /**
     * Tests the parsing of scripts.
     *
     * @param _description  description
     * @param _code         code to parse
     * @param _statement    expected result statement
     * @throws Exception if test failed
     */
    @Test(dataProvider = "data")
    public void positiveTest(final String _description,
                             final String _code,
                             final MxUpdateStatement_mxJPO _statement)
        throws Exception
    {
        final List<AbstractStatement_mxJPO> curStatements = new ArrayList<>();
        final ScriptHandler_mxJPO scriptHandler = new ScriptHandler_mxJPO()  {
            @Override
            public ScriptHandler_mxJPO addStatement(final AbstractStatement_mxJPO _statement)
            {
                curStatements.add(_statement);
                return this;
            }
        };
        scriptHandler.parse(_code);

        final List<AbstractStatement_mxJPO> expStatements = new ArrayList<>();
        expStatements.add(_statement);

        Assert.assertEquals(
                curStatements,
                expStatements);
    }
}
