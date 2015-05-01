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

package org.mxupdate.test.update.datamodel.helper;

import java.io.IOException;

import org.mxupdate.update.datamodel.helper.AccessList_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests sorting of access statements in JPO {@link #AccessList_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class AccessListTest
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]
        {
            {"    user \"A\" {read}\n    login user \"A\" {read}\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/userAccessLoginRole", "",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", ""}
            },
            {"    user \"A\" {read}\n    revoke user \"A\" {read}\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/userAccessRevoke", "",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", ""}
            },
            {"    owner {read}\n    public {read}\n",
                new String[]{
                    "/publicAccess", "",
                    "/publicAccess/access/ReadAccess", "",
                    "/ownerAccess", "",
                    "/ownerAccess/access/ReadAccess", ""}
            },
            {"    user \"111\" {read}\n    user \"222\" {read}\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "222",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "111",
                    "/userAccessList/userAccess/access/ReadAccess", ""}
            },
            {"    user \"A\" key \"111\" {read}\n    user \"A\" key \"222\" {read}\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/userAccessKey", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/userAccessKey", "111"}
            },
            {"    user \"A\" {read} 111 organization\n    user \"A\" {read} 222 organization\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchOrganization", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchOrganization", "111"}
            },
            {"    user \"A\" {read} 111 project\n    user \"A\" {read} 222 project\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchProject", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchProject", "111"}
            },
            {"    user \"A\" {read} 111 owner\n    user \"A\" {read} 222 owner\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchOwner", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchOwner", "111"}
            },
            {"    user \"A\" {read} 111 reserve\n    user \"A\" {read} 222 reserve\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchReserve", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchReserve", "111"}
            },
            {"    user \"A\" {read} 111 maturity\n    user \"A\" {read} 222 maturity\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchMaturity", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchMaturity", "111"}
            },
            {"    user \"A\" {read} 111 category\n    user \"A\" {read} 222 category\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchCategory", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/matchCategory", "111"}
            },
            {"    user \"A\" {read} filter \"111\"\n    user \"A\" {read} filter \"222\"\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/expressionFilter", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/expressionFilter", "111"}
            },
            {"    user \"A\" {read} localfilter \"111\"\n    user \"A\" {read} localfilter \"222\"\n",
                new String[]{
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/localExpressionFilter", "222",
                    "/userAccessList/userAccess", "",
                    "/userAccessList/userAccess/userRef", "A",
                    "/userAccessList/userAccess/access/ReadAccess", "",
                    "/userAccessList/userAccess/localExpressionFilter", "111"}
            },
        };
    }

    /**
     * Positive test that access is correct sorted.
     *
     * @param _expected     expected result
     * @param _lines        lines to parse
     * @throws IOException if test failed
     */
    @Test(description = "positive test that access is correct sorted",
          dataProvider = "data")
    public void positiveTest(final String _expected,
                             final String[] _lines)
        throws IOException
    {
        final AccessList_mxJPO list = new AccessList_mxJPO();

        for (int idx = 0; idx < _lines.length; )  {
            list.parse(null, _lines[idx++], _lines[idx++]);
        }

        list.sort();

        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(null);
        list.write(updateBuilder);
        System.out.println("strg?\n"+updateBuilder.getStrg());


        Assert.assertEquals(updateBuilder.getStrg().toString(), _expected);
    }
}
