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

package org.mxupdate.test.test.update.userinterface;

import java.io.StringReader;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.userinterface.Menu_mxJPO;
import org.mxupdate.update.userinterface.menu.MenuDefParser_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link MenuDefParser_mxJPO menu parser}.
 *
 * @author The MxUpdate Team
 */
public class MenuCI_1ParserTest
{
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {" treeMenu"},
            {"\n    !treeMenu\n"},
            {"menu \"hello\""},
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
        final MenuDefParser_mxJPO parser = new MenuDefParser_mxJPO(new StringReader(_code));
        parser.parse(new Menu_mxJPO((TypeDef_mxJPO) null, "test"));
    }
}
