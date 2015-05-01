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

package org.mxupdate.test.test.update.userinterface.helper;

import org.mxupdate.update.userinterface.helper.ChildRefList_mxJPO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the JPO{@link ChildRefList_mxJPO}.
 *
 * @author The MxUpdate Team
 */
public class ChildRefListTest
{
    /**
     * Prepares the test data.
     *
     * @return test data
     */
    @SuppressWarnings("serial")
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][] {
            {
                "    command \"bcd\"\n"
              + "    command \"abc\"\n",
                new ChildRefList_mxJPO()  {{
                    this.parse("/commandRefList/commandRef".substring(15), "");
                    this.parse("/commandRef/name", "bcd");
                    this.parse("/commandRef/order", "0");
                    this.parse("/commandRef", "");
                    this.parse("/commandRef/name", "abc");
                    this.parse("/commandRef/order", "1");
                }}
            },
            {
                "    command \"bcd\"\n"
              + "    command \"abc\"\n",
                new ChildRefList_mxJPO()  {{
                    this.parse("/commandRef", "");
                    this.parse("/commandRef/name", "abc");
                    this.parse("/commandRef/order", "1");
                    this.parse("/commandRef", "");
                    this.parse("/commandRef/name", "bcd");
                    this.parse("/commandRef/order", "0");
                }}
            },
            {
                "    command \"bcd\"\n"
              + "    menu \"abc\"\n",
                new ChildRefList_mxJPO()  {{
                    this.parse("/menuRef", "");
                    this.parse("/menuRef/name", "abc");
                    this.parse("/menuRef/order", "1");
                    this.parse("/commandRef", "");
                    this.parse("/commandRef/name", "bcd");
                    this.parse("/commandRef/order", "0");
                }}
            },
            {
                "    channel \"abc\"\n"
              + "    channel \"bcd\"\n"
              + "    newrow\n"
              + "    channel \"cde\"\n",
                new ChildRefList_mxJPO()  {{
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "bcd");
                    this.parse("/channelRef/portalRow", "0");
                    this.parse("/channelRef/portalColumn", "1");
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "cde");
                    this.parse("/channelRef/portalRow", "2");
                    this.parse("/channelRef/portalColumn", "0");
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "abc");
                    this.parse("/channelRef/portalRow", "0");
                    this.parse("/channelRef/portalColumn", "0");
                }}
            },
            {
                "    channel \"abc\"\n"
              + "    channel \"bcd\"\n"
              + "    newrow\n"
              + "    channel \"cde\"\n"
              + "    channel \"def\"\n",
                new ChildRefList_mxJPO()  {{
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "bcd");
                    this.parse("/channelRef/portalRow", "0");
                    this.parse("/channelRef/portalColumn", "1");
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "cde");
                    this.parse("/channelRef/portalRow", "2");
                    this.parse("/channelRef/portalColumn", "0");
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "abc");
                    this.parse("/channelRef/portalRow", "0");
                    this.parse("/channelRef/portalColumn", "0");
                    this.parse("/channelRef", "");
                    this.parse("/channelRef/name", "def");
                    this.parse("/channelRef/portalRow", "2");
                    this.parse("/channelRef/portalColumn", "1");
                }}
            },
       };
    }

    /**
     * Positive simple test.
     *
     * @param _exp      expected update file string
     * @param _refList  list of child references
     * @throws Exception if test failed.
     */
    @Test(description = "positive simple test",
          dataProvider = "data")
    public void positiveTest(final String _exp,
                             final ChildRefList_mxJPO _refList)
        throws Exception
    {
        _refList.prepare();

        final StringBuilder strg = new StringBuilder();
        _refList.write(strg);

        Assert.assertEquals(strg.toString(), _exp);
    }
}
