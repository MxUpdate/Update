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

package org.mxupdate.test.test.update;

import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the {@link BusObject_mxJPO business object CI} parser.
 *
 * @author The MxUpdate Team
 */
@Test
public class BusObjectCI_1ParserWODerivedTest
    extends AbstractParserTest<BusObject_mxJPO>
{
    @Override
    @DataProvider(name = "data")
    public Object[][] getData()
    {
        return new Object[][]  {
            {"1) simple",
                    "",
                    "description \"\" current \"\""},
            // type
            {"2) type (removed)",
                    "                      description \"\" current \"\"",
                    "type \"NON-EXISTING\" description \"\" current \"\""},
            // description
            {"3a) description",
                    "",
                    "description \"abc def\" current \"\""},
            {"3b) description not defined",
                    "description \"\" current \"\"",
                    "                 current \"\""},
            {"3c) multi-line description",
                    "",
                    "description \"abc\ndef\" current \"\""},

            // attribute
            {"4a) one attribute ",
                    "",
                    "description \"\" current \"\" attribute \"ATTRNAME\" \"ATTRVALUE\""},
            // attribute
            {"4b) various attributes (to test sorting of attributes)",
                    "description \"\" current \"\" attribute \"ATTRNAME\"  \"ATTRVALUE\" attribute \"ATTRNAME1\" \"ATTRVALUE\" attribute \"ATTRNAME2\" \"ATTRVALUE\"",
                    "description \"\" current \"\" attribute \"ATTRNAME2\" \"ATTRVALUE\" attribute \"ATTRNAME1\" \"ATTRVALUE\" attribute \"ATTRNAME\"  \"ATTRVALUE\""},
            {"4c) mulitine attribute value",
                    "",
                    "description \"\" current \"\" attribute \"ATTRNAME\" \"ATTR\n\nVALUE\""},

            // connection
            {"5a) one connection",
                    "",
                    "description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\""},
            {"5b) connection with attribute",
                    "",
                    "description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\" { attribute \"ATTRNAME\" \"ATTRVALUE\" }"},
            {"5c) connection with various attributes ",
                    "description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\" { attribute \"ATTRNAME1\" \"ATTRVALUE\" attribute \"ATTRNAME2\" \"ATTRVALUE\" }",
                    "description \"\" current \"\" connection \"CON1\" from \"DEMO\" \"VERSU\" \"REVI1\" { attribute \"ATTRNAME2\" \"ATTRVALUE\" attribute \"ATTRNAME1\" \"ATTRVALUE\" }"},

        };
    }

    @Override
    protected BusObject_mxJPO createNewData(final ParameterCache_mxJPO _paramCache,
                                            final String _name)
    {
        return new BusObject_mxJPO(_paramCache.getMapping().getTypeDef("Notification"), _name);
    }
}
